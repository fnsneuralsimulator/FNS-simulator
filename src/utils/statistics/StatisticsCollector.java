/**
* This file is part of FNS (Firnet NeuroScience), ver.2.0
*
* (c) 2018, Mario Salerno, Gianluca Susi, Alessandro Cristini, Emanuele Paracone,
* Fernando Maestú.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, Mario 
* Salerno, Fernando Maestú, Ernesto Pereda (2018). "FNS: an event-driven spiking 
* neural network simulator based on the LIFL neuron model". 
* Laboratory of Cognitive and Computational Neuroscience, UPM-UCM Centre for 
* Biomedical Technology, Technical University of Madrid; University of Rome "Tor 
* Vergata".   
* Paper under review.
*
* FNS is free software: you can redistribute it and/or modify it under the terms 
* of the GNU General Public License version 3 as published by  the Free Software 
* Foundation.
*
* FNS is distributed in the hope that it will be useful, but WITHOUT ANY 
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with 
* FNS. If not, see <http://www.gnu.org/licenses/>.
* -----------------------------------------------------------
* Website:   http://www.fnsneuralsimulator.org
*/

package utils.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import connectivity.conn_package.PackageReader;
import spiking.node.FiringNeuron;
import spiking.node.SpikingSynapse;
import spiking.node.Synapse;
import utils.plotter.FastScatterPlotter;
import utils.plotter.ScatterPlotter;
import utils.tools.CompressedFire;

public class StatisticsCollector extends Thread {
  
  private final String TAG = "[Statistic Collector] ";
  private volatile Long active=0l;
  private volatile Long passive=0l;
  private volatile Long passive2active=0l;
  private volatile Long active2passive=0l;
  public  volatile Long missedFires=0l;
  private volatile HashMap<Long, SpikingSynapse>burningSpikesHashMap = 
      new HashMap<Long, SpikingSynapse>();
  private volatile HashMap<Long, FiringNeuron>firingSpikesHashMap = 
      new HashMap<Long, FiringNeuron>();
  private volatile ArrayList<Double> firingNeurons= 
      new ArrayList<Double>();
  private volatile ArrayList<Double> firingTimes= 
      new ArrayList<Double>();
  private volatile ArrayList<Double> firstFiringNeurons= null;
  private volatile ArrayList<Double> firstFiringTimes= null;
  private volatile HashMap<CompressedFire, Integer> compressor= 
      new HashMap<CompressedFire, Integer>();
  private volatile Double simulatedTime=0.0;
  private volatile Double minMissedAxonalDelay = Double.MAX_VALUE;
  private volatile Double minNe_xn_ratio;
  private volatile Double maxNe_xn_ratio;
  private volatile Boolean badCurve=false;
  private volatile long firingSpikesCounter=0l;
  private volatile long burningSpikesCounter=0l;
  // the nodes of interest NOI
  private HashMap <Integer,Boolean> NOI;
  private volatile Boolean checkall=false;
  private long serialize_after = 10000l;
  private volatile int wrotes_split=0;
  private volatile String filename = "";
  private volatile Boolean matlab=false;
  private volatile Boolean reducedOutput=false;
  private volatile int count=1;
  private volatile ArrayList<CollectedFire> newFires=
      new ArrayList<CollectedFire>();
  private volatile ArrayList<CollectedBurn> newBurns=
      new ArrayList<CollectedBurn>();
  private Lock lock = new ReentrantLock();
  private Condition eventQueueCondition = lock.newCondition();
  private Boolean keepRunning=true;
  private String defFileName=null;
  
  
  public void run() {
    for(;keepRunning;) {
      if ((newBurns.size()<=0)&&(newFires.size()<=0))
        wait_event();
      while (newBurns.size()>0) 
        processBurnSpike(newBurns.remove(0));
      while (newFires.size()>0) 
        processFireSpike(newFires.remove(0));
    }
    while (newBurns.size()>0) 
      processBurnSpike(newBurns.remove(0));
    while (newFires.size()>0) 
      processFireSpike(newFires.remove(0));
  }
  
  public void kill(){
    keepRunning=false;
    lock.lock();
    eventQueueCondition.signal();
    lock.unlock();
  }
  
  private void new_event() {
    lock.lock();
    eventQueueCondition.signal();
    lock.unlock();
  }
  
  private void wait_event() {
    lock.lock();
    try {
      eventQueueCondition.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    lock.unlock();
  }
  
  public void setSerializeAfter(long sa){
    serialize_after = sa;
  }
  
  public void set_filename(String filename){
    this.filename=filename;
  }
  
  private void reset(){
    burningSpikesHashMap = new HashMap<Long, SpikingSynapse>();
    firingSpikesHashMap = new HashMap<Long, FiringNeuron>();
    if (firstFiringNeurons==null){
      firstFiringNeurons=firingNeurons;
      firstFiringTimes=firingTimes;
    }
    firingNeurons= new ArrayList<Double>();
    firingTimes= new ArrayList<Double>();
  }
  
  public void setMatlab() {
    matlab=true;
  }

  public void setReducedOutput() {
    reducedOutput=true;
  }
  
  public synchronized void collectActive(){
    ++active;
  }
  
  public synchronized void collectPassive2active(){
    ++passive2active;
  }
  
  public synchronized void collectActive2passive(){
    ++active2passive;
  }
  
  public synchronized void collectPassive(){
    ++passive;
  }
  
  
  public synchronized void collectFireSpike(
      Integer firingRegionId, 
      Long firingNeuronId, 
      Double firingTime, 
      Long maxN, 
      Double compressionFactor, 
      Boolean isExcitatory, 
      Boolean isExternal){
    processFireSpike(
        new CollectedFire(
            firingRegionId, 
            firingNeuronId, 
            firingTime, 
            maxN, 
            compressionFactor, 
            isExcitatory, 
            isExternal
            ));
    new_event();
  }

  private void processFireSpike(CollectedFire cf) {
    CompressedFire compF = new CompressedFire(
        cf.getFiringRegionId(), 
        cf.getFiringNeuronId(), 
        cf.getFiringTime(), 
        cf.getMaxN(), 
        cf.getCompressionFactor());
    Integer tmp = compressor.get(
        new CompressedFire(
            cf.getFiringRegionId(), 
            cf.getFiringNeuronId(), 
            cf.getFiringTime(), 
            cf.getMaxN(), 
            cf.getCompressionFactor()));
    if (tmp!=null)
      return;
    firingNeurons.add(new Double(compF.getCompressedNeuronId()));
    firingTimes.add(cf.getFiringTime());
    if (checkall ||( NOI.get(cf.getFiringRegionId())!=null )){
      FiringNeuron fn= new FiringNeuron(
          cf.getFiringRegionId(),
          cf.getFiringNeuronId(),
          cf.getFiringTime(),
          cf.getIsExcitatory(),
          cf.getIsExternal());
      firingSpikesHashMap.put(new Long(firingSpikesCounter), fn);
    }
    ++firingSpikesCounter;
    if ((firingSpikesCounter%serialize_after)==0){
      makeCsv(filename);
      if (firstFiringNeurons==null)
        simulatedTime=cf.getFiringTime();
    }
  }
  
  public synchronized void collectBurnSpike(
      Synapse s, 
      Double burnTime, 
      Boolean fromExternalSource, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire,
      Double fireTime) {
    processBurnSpike(
        new CollectedBurn(
            s, 
            burnTime, 
            fromExternalSource, 
            fromState, 
            stepInState, 
            postsynapticWeight, 
            presynapticWeight, 
            timeToFire, 
            fireTime));
    new_event();
  }
  
  private void processBurnSpike(CollectedBurn cb) {
    if (checkall ||( NOI.get(cb.getS().getDendriteNodeId())!=null )){
      SpikingSynapse ss = new SpikingSynapse(
          cb.getS(), 
          cb.getBurnTime(),
          cb.getFromExternalSource(), 
          cb.getFromState(), 
          cb.getStepInState(), 
          cb.getPostsynapticWeight(), 
          cb.getPresynapticWeight(), 
          cb.getTimeToFire(), 
          cb.getFireTime());
      burningSpikesHashMap.put(new Long(burningSpikesCounter), ss);
    }
    ++burningSpikesCounter;
  }
  
  public synchronized void collectMissedFire(Double missedAxonalDelay){
    if (missedAxonalDelay<minMissedAxonalDelay)
      minMissedAxonalDelay=missedAxonalDelay;
    ++missedFires;
  }
  
  public void printFirePlot(){
    double [] x = new double [firingTimes.size()];
    double [] y = new double [firingNeurons.size()];
    for (int i=0; i<firingNeurons.size();++i){
      x[i]=firingTimes.get(i);
      y[i]=firingNeurons.get(i).doubleValue();
    }
    System.out.println(
        "[Statistics Collector] X size:"
        +x.length
        +", Y size:"
        +y.length);
    System.out.println(
        "[Statistics Collector] firing times size:"
        +firingTimes.size()
        +", firing neurons size:"
        +firingNeurons.size());
    ScatterPlotter frame = 
      new ScatterPlotter("FNS", x, y,simulatedTime); 
    frame.setVisible();
  }
  
  public void printFirePlot(String outputFileName){
    double [] x = new double [firstFiringTimes.size()];
    double [] y = new double [firstFiringNeurons.size()];
    for (int i=0; i<firstFiringNeurons.size();++i){
      x[i]=firstFiringTimes.get(i);
      y[i]=firstFiringNeurons.get(i).doubleValue();
    }
    System.out.println(
        "[Statistics Collector] X size:"
        +x.length
        +", Y size:"
        +y.length);
    System.out.println(
        "[Statistics Collector] firing times size:"
        +firstFiringTimes.size()
        +", firing neurons size:"
        +firstFiringNeurons.size());
    ScatterPlotter frame = 
        new ScatterPlotter(
            "F. N. S.", 
            x, 
            y,
            simulatedTime,outputFileName); 
    frame.setVisible();
  }
  
  public void makeCsv(String filename){
    if (filename=="")
      return;
    ++wrotes_split;
    PrintWriter burnWriter;
    PrintWriter fireWriter;
    Boolean new_burn_file=false;
    Boolean new_fire_file=false;
    File towritefile;
    FileWriter fire_fw;
    DecimalFormat df = new DecimalFormat("#.################"); 
    try {
      Iterator<Long> it = burningSpikesHashMap.keySet().iterator();
      if (firstFiringNeurons==null) {
//        int count = 1;
        for(;;++count) {
          towritefile= new File(filename+String.format("%03d", count)+"_burning.csv");
            if(!towritefile.exists()){
              defFileName=filename+String.format("%03d", count);
                break;
            }
        }
      }
      towritefile= new File(defFileName+"_burning.csv");
      if (!towritefile.exists()){
        towritefile.createNewFile();
        new_burn_file=true;
      }
      FileWriter fw = new FileWriter(towritefile,true);
           BufferedWriter bw = new BufferedWriter(fw);
      burnWriter = new PrintWriter(bw);
      if (new_burn_file){
        if (reducedOutput)
          burnWriter.println(
              "Burning Time, "
              + "Burning Node, "
              + "Burning Neuron, "
              + "To Internal State");
        else
          burnWriter.println(
              "Burning Time, "
              + "Firing Node, "
              + "Firing Neuron, "
              + "Burning Node, "
              + "Burning Neuron, "
              + "External Source, "
              + "From Internal State, "
              + "To Internal State, "
              + "Step in State, "
              +" Post Synaptic Weight, "
              + "Pre Synaptic Weight, "
              + "Instant to Fire, "
              + "(Afferent) Firing Time");
            }
      while (it.hasNext()){
        Long key = it.next();
        Double fromState = burningSpikesHashMap.get(key).getFromState();
        Double stepInState=burningSpikesHashMap.get(key).getStepInState();
        String stepInStateToPrint;
        String fromStateToPrint;
        String toStateToPrint;
        if (fromState==null){
          fromStateToPrint="refr";
          toStateToPrint="refr";
        }
        else{
          fromStateToPrint=""+df.format(fromState);
          toStateToPrint=""+df.format(fromState+stepInState);
        }
        if (stepInState==null)
          stepInStateToPrint="refr";
        else
          stepInStateToPrint=""+df.format(stepInState);
        if (reducedOutput)
          burnWriter.println(
              df.format(burningSpikesHashMap.get(key).getBurnTime())+", "
              + burningSpikesHashMap.get(key).getS().getDendriteNodeId()+", "
              + burningSpikesHashMap.get(key).getS().getDendriteNeuronId()+", "
              + toStateToPrint
              );
        else
          burnWriter.println(
              df.format(burningSpikesHashMap.get(key).getBurnTime())+", "
              + burningSpikesHashMap.get(key).getS().getAxonNodeId()+", "
              + burningSpikesHashMap.get(key).getS().getAxonNeuronId()+", "
              + burningSpikesHashMap.get(key).getS().getDendriteNodeId()+", "
              + burningSpikesHashMap.get(key).getS().getDendriteNeuronId()+", "
              + burningSpikesHashMap.get(key).getS().fromExternalInput()+", "
              + fromStateToPrint +", "
              + toStateToPrint +", "
              + stepInStateToPrint+", "
              + df.format(burningSpikesHashMap.get(key).getPostSynapticWeight())+", "
              + df.format(burningSpikesHashMap.get(key).getPresynapticWeight())+","
              + df.format(burningSpikesHashMap.get(key).getInstantToFire())+","
              + df.format((burningSpikesHashMap.get(key).getFireTime()!=null)?
                  burningSpikesHashMap.get(key).getFireTime():0)
              );
//        System.out.println("[statistics]" + df.format(burningSpikesHashMap.get(key).getPostSynapticWeight()));
      }
      burnWriter.flush();
      burnWriter.close();
      System.out.println(
          "[Statistics Collector] "
          +towritefile.getAbsolutePath()
          +" update "
          +wrotes_split
          +" complete.");
      it=firingSpikesHashMap.keySet().iterator();
      towritefile= new File(defFileName+"_firing.csv");
      if (towritefile.exists())
        fire_fw = new FileWriter(towritefile,true);
      else{
        towritefile.createNewFile();
        fire_fw = new FileWriter(towritefile);
        new_fire_file=true;
      }
           BufferedWriter fire_bw = new BufferedWriter(fire_fw);
      fireWriter=new PrintWriter(fire_bw);
      if (new_fire_file)
        fireWriter.println(
            "Firing Time,"
            +" Firing Node,"
            +" Firing Neuron, "
            +" Neuron Type,"
            +" External Source");
      while (it.hasNext()){
        Long key = it.next();
        String excitStr;
        if (firingSpikesHashMap.get(key).isExcitatory())
          excitStr="excitatory";
        else
          excitStr="inhibitory";
        fireWriter.println(
            df.format(firingSpikesHashMap.get(key).getFiringTime())+", "
            +firingSpikesHashMap.get(key).getFiringRegionId()+", "
            + firingSpikesHashMap.get(key).getFiringNeuronId()+", "
            + excitStr+", "
            + firingSpikesHashMap.get(key).isExternal()
            );
      }
      fireWriter.flush();
      fireWriter.close();
      if (matlab)
        makeMatlabCsv();
      reset();
      System.out.println("[Statistics Collector] "+towritefile.getAbsolutePath()+" update "+wrotes_split+" complete.");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("[Statistics Collector] Firings:"+firingSpikesCounter);
  }
  
  private void makeMatlabCsv(){
    if (filename=="")
      return;
    PrintWriter burnWriter;
    PrintWriter fireWriter;
    try {
      Iterator<Long> it = burningSpikesHashMap.keySet().iterator();
      File towritefile;
      FileWriter fire_fw;
      towritefile= new File(defFileName+"_burning_matlab.csv");
      if (!towritefile.exists())
        towritefile.createNewFile();
      FileWriter fw = new FileWriter(towritefile,true);
           BufferedWriter bw = new BufferedWriter(fw);
      burnWriter = new PrintWriter(bw);
      while (it.hasNext()){
        Long key = it.next();
        Double fromState = burningSpikesHashMap.get(key).getFromState();
        Double stepInState=burningSpikesHashMap.get(key).getStepInState();
        String stepInStateToPrint;
        String fromStateToPrint;
        String toStateToPrint;
        String refrString="10101";
        if (fromState==null){
          fromStateToPrint=refrString;
          toStateToPrint=refrString;
        }
        else{
          fromStateToPrint=fromState.toString();
          toStateToPrint=""+(fromState+stepInState);
        }
        if (stepInState==null)
          stepInStateToPrint="10101";
        else
          stepInStateToPrint=stepInState.toString();
        //if (reducedOutput)
        //  burnWriter.println(
        //      burningSpikesHashMap.get(key).getBurnTime().toString()+", "
        //      + burningSpikesHashMap.get(key).getS().getDendriteNodeId()+", "
        //      + burningSpikesHashMap.get(key).getS().getDendriteNeuronId()+", "
        //      + toStateToPrint
        //      );
        //else
        burnWriter.println(
            burningSpikesHashMap.get(key).getBurnTime().toString()+", "
            + burningSpikesHashMap.get(key).getS().getAxonNodeId()+", "
            + burningSpikesHashMap.get(key).getS().getAxonNeuronId()+", "
            + burningSpikesHashMap.get(key).getS().getDendriteNodeId()+", "
            + burningSpikesHashMap.get(key).getS().getDendriteNeuronId()+", "
            + burningSpikesHashMap.get(key).getS().fromExternalInputInteger()+", "
            + fromStateToPrint +", "
            + toStateToPrint +", "
            + stepInStateToPrint+", "
            + burningSpikesHashMap.get(key).getPostSynapticWeight()+", "
            + burningSpikesHashMap.get(key).getPresynapticWeight()+","
            + burningSpikesHashMap.get(key).getInstantToFire()+","
            + burningSpikesHashMap.get(key).getFireTime()
            );
      }
      burnWriter.flush();
      burnWriter.close();
      System.out.println(
          "[Statistics Collector] "
          +towritefile.getAbsolutePath()
          +" update "
          +wrotes_split
          +" complete.");
      it=firingSpikesHashMap.keySet().iterator();
      towritefile= new File(defFileName+"_firing_matlab.csv");
      if (towritefile.exists())
        fire_fw = new FileWriter(towritefile,true);
      else{
        towritefile.createNewFile();
        fire_fw = new FileWriter(towritefile);
      }
           BufferedWriter fire_bw = new BufferedWriter(fire_fw);
      fireWriter=new PrintWriter(fire_bw);
      while (it.hasNext()){
        Long key = it.next();
        //if (reducedOutput)
        //  fireWriter.println(
        //      firingSpikesHashMap.get(key).getFiringTime().toString()+", "
        //      +firingSpikesHashMap.get(key).getFiringRegionId()+", "
        //      + firingSpikesHashMap.get(key).getFiringNeuronId()+", "
        //      + (firingSpikesHashMap.get(key).isExternal()?'1':'0')
        //      );
        //else
        fireWriter.println(
            firingSpikesHashMap.get(key).getFiringTime().toString()+", "
            +firingSpikesHashMap.get(key).getFiringRegionId()+", "
            + firingSpikesHashMap.get(key).getFiringNeuronId()+", "
            + (firingSpikesHashMap.get(key).isExcitatory()?'1':'0')+", "
            + (firingSpikesHashMap.get(key).isExternal()?'1':'0')
            );
      }
      fireWriter.flush();
      fireWriter.close();
      System.out.println("[Statistics Collector] "+towritefile.getAbsolutePath()+" update "+wrotes_split+" complete.");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  public void setMinMaxNe_xn_ratios(Double minNe_xn_ratio, Double maxNe_xn_ratio){
    this.minNe_xn_ratio=minNe_xn_ratio;
    this.maxNe_xn_ratio=maxNe_xn_ratio;  
  }
  
  public void PrintResults(){
    String minNe_xn_ratioStr=(minNe_xn_ratio==PackageReader.MIN_NE_EN_RATIO_DEF)?
        "no connection between nodes":(""+minNe_xn_ratio);
    String maxNe_xn_ratioStr=(maxNe_xn_ratio==PackageReader.MAX_NE_EN_RATIO_DEF)?
        "no connection between nodes":(""+maxNe_xn_ratio);
    System.out.println("active to active:"+active);
    System.out.println("active to passive:"+active2passive);
    System.out.println("passive to passive:"+passive);
    System.out.println("passive to active:"+passive2active);
//    System.out.println("min Ne en ratio:"+minNe_xn_ratioStr);
//    System.out.println("max Ne en ratio:"+maxNe_xn_ratioStr);
    System.out.println("missed Fires:"+missedFires);
    if (missedFires>0)
      System.out.println("minimum missed fire axonal delay:"+minMissedAxonalDelay);
    System.out.println("Good curve:"+(!badCurve));
  }
  
  public void setBadCurve(){
    badCurve=true;
  }
  
  //public void setNodes2checkMask(BigInteger mask){
  //  region2checkMask=mask;
  //}

  public void setNOI(HashMap <Integer, Boolean> NOI){
    this.NOI=NOI;
  }
  
  public void checkAll(){
    checkall=true;
  }
  
  
}

/**
* "FNS" (Firnet NeuroScience), ver.3.x
*				
* FNS is an event-driven Spiking Neural Network framework, oriented 
* to data-driven neural simulations.
*
* (c) 2020, Gianluca Susi, Emanuele Paracone, Mario Salerno, 
* Alessandro Cristini, Fernando Maestú.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, 
* Mario Salerno, Fernando Maestú, Ernesto Pereda (2020). 
* "FNS: an event-driven spiking neural network simulator based on the 
* LIFL neuron model". 
* Laboratory of Cognitive and Computational Neuroscience, UPM-UCM 
* Centre for Biomedical Technology, Technical University of Madrid; 
* University of Rome "Tor Vergata".   
* Paper under review.
*
* FNS is free software: you can redistribute it and/or modify it 
* under the terms of the GNU General Public License version 3 as 
* published by the Free Software Foundation.
*
* FNS is distributed in the hope that it will be useful, but WITHOUT 
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
* or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with FNS. If not, see <http://www.gnu.org/licenses/>.
* 
* -----------------------------------------------------------
*  
* Website:   http://www.fnsneuralsimulator.org
* 
* Contacts:  fnsneuralsimulator (at) gmail.com
*	    gianluca.susi82 (at) gmail.com
*	    emanuele.paracone (at) gmail.com
*
*
* -----------------------------------------------------------
* -----------------------------------------------------------
**/

package utils.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

import connectivity.conn_package.PackageReader;

public class StatisticsCollector /*extends Thread*/ {
  
  private final String TAG = "[Statistic Collector] ";
  private Long active=0l;
  private Long passive=0l;
  private Long passive2active=0l;
  private Long active2passive=0l;
  public  Long missedFires=0l;
  private ArrayList<Double> firstFiringTimes= null;
  private Double minMissedAxonalDelay = Double.MAX_VALUE;
  private Double minNe_xn_ratio;
  private Double maxNe_xn_ratio;
  private Boolean badCurve=false;
  private long firingSpikesCounter=0l;
  private long burningSpikesCounter=0l;
  private int serializeAfter = 10000;
  private int wrotes_split=0;
  private String filename = "";
  protected Boolean matlab=false;
  protected Boolean gephi=false;
  protected Boolean reducedOutput=false;
  protected Boolean superReducedOutput=false;
  private int count=1;
  private ArrayList<CollectedFire> newFires=
      new ArrayList<CollectedFire>();
  private ArrayList<CollectedBurn> newBurns=
      new ArrayList<CollectedBurn>();
  private Lock lock = new ReentrantLock();
  private Condition eventQueueCondition = lock.newCondition();
  protected Boolean keepRunning=true;
  private String defFileName=null;
  private File towritefile;
  private BurningWriter burningWriter;
  private FiringWriter firingWriter;
  
  public void start(){
    burningWriter.start();
    firingWriter.start(); 
  }

  public void kill(){
    keepRunning=false;
    burningWriter.flush();
    firingWriter.flush(); 
  }

  public void close(){
    burningWriter.close();
    firingWriter.close(); 
  }
  
  public void setSerializeAfter(int sa){
    serializeAfter = sa;
  }

  public int getSerializeAfter(){
    return serializeAfter;
  }
  
  public void init(String defFilename){
    //this.filename=filename;
    //for(;;++count) {
    //  if (superReducedOutput)
    //    towritefile= new File(
    //        filename
    //        +String.format("%03d", count)
    //        +"_burning_R.csv");
    //  else if (reducedOutput)
    //    towritefile= new File(
    //        filename
    //        +String.format("%03d", count)
    //        +"_burning_r.csv");
    //  else
    //    towritefile= new File(
    //        filename
    //        +String.format("%03d", count)
    //        +"_burning.csv");
    //  if(!towritefile.exists()){
    //    defFileName=filename+String.format("%03d", count);
    //      break;
    //  }
    //}
    burningWriter= new BurningWriter(
        this,
        defFileName,
        serializeAfter);
    firingWriter= new FiringWriter(
        this,
        defFileName,
        serializeAfter);
  }
  
  public void setMatlab() {
    matlab=true;
  }

  public void setReducedOutput() {
    reducedOutput=true;
  }

  public void setSuperReducedOutput() {
    superReducedOutput=true;
  }

  public void setGephi() {
    gephi=true;
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
      Integer firingNodeId, 
      Long firingNeuronId, 
      Double firingTime, 
      Long maxN, 
      Double compressionFactor, 
      Boolean isExcitatory, 
      Boolean isExternal){
      firingWriter.put(
          new CollectedFire(
              firingNodeId, 
              firingNeuronId, 
              firingTime, 
              maxN, 
              compressionFactor, 
              isExcitatory, 
              isExternal
              ));
  }
  public synchronized void collectBurnSpike(
      Long firingNeuronId,
      Integer firingNodeId,
      Long burningNeuronId,
      Integer burningNodeId,
      Double burnTime, 
      Boolean fromExternalSource, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire,
      Double fireTime) {
      burningWriter.put(
          new CollectedBurn(
              firingNeuronId,
              firingNodeId,
              burningNeuronId,
              burningNodeId,
              burnTime, 
              fromExternalSource, 
              fromState, 
              stepInState, 
              postsynapticWeight, 
              presynapticWeight, 
              timeToFire, 
              fireTime)
              );
  }
  
  
  public synchronized void collectMissedFire(Double missedAxonalDelay){
    if (missedAxonalDelay<minMissedAxonalDelay)
      minMissedAxonalDelay=missedAxonalDelay;
    ++missedFires;
  }
  
  
  
  public void setMinMaxNe_xn_ratios(
      Double minNe_xn_ratio, 
      Double maxNe_xn_ratio){
    this.minNe_xn_ratio=minNe_xn_ratio;
    this.maxNe_xn_ratio=maxNe_xn_ratio;  
  }
  
  public void PrintResults(){
    String minNe_xn_ratioStr =
        (minNe_xn_ratio==PackageReader.MIN_NE_EN_RATIO_DEF)?
        "no connection between nodes":(""+minNe_xn_ratio);
    String maxNe_xn_ratioStr = 
        (maxNe_xn_ratio==PackageReader.MAX_NE_EN_RATIO_DEF)?
        "no connection between nodes":(""+maxNe_xn_ratio);
    System.out.println("missed Fires:"+missedFires);
    if (missedFires>0)
      System.out.println("minimum missed fire axonal delay:"+minMissedAxonalDelay);
    System.out.println("Good curve:"+(!badCurve));
  }
  
  public void setBadCurve(){
    badCurve=true;
  }

  public Boolean getReducedOutput(){
    Thread currentThread = Thread.currentThread();
    System.out.println("Thread :" + currentThread.getId());  
    System.out.println("sc reducedOutput: " + reducedOutput);
    System.out.println();
    return reducedOutput;
  }

  public void setWriters(
      PrintWriter burningPw,
      PrintWriter burningPwGephi,
      PrintWriter burningPwMatlab,
      //File burningTowritefile,
      //File burningTowritefileGephi,
      //File burningTowritefileMatlab,
      //BufferedWriter burningBw,
      //BufferedWriter burningBwMatlab,
      //FileWriter burningFw,
      //FileWriter burningFwGephi,
      //FileWriter burningFwMatlab,
      PrintWriter firingPw,
      PrintWriter firingPwGephi,
      PrintWriter firingPwMatlab
      //File firingTowritefile,
      //File firingTowritefileGephi,
      //File firingTowritefileMatlab,
      //BufferedWriter firingBw,
      //BufferedWriter firingBwMatlab,
      //FileWriter firingFw,
      //FileWriter firingFwGephi,
      //FileWriter firingFwMatlab
  ){
    burningWriter.setWriters(
        burningPw,
        burningPwGephi,
        burningPwMatlab
        //burningTowritefile,
        //burningTowritefileGephi,
        //burningTowritefileMatlab,
        //burningBw,
        //burningBwMatlab,
        //burningFw,
        //burningFwGephi,
        //burningFwMatlab
    );
    firingWriter.setWriters(
        firingPw,
        firingPwGephi,
        firingPwMatlab
        //firingTowritefile,
        //firingTowritefileGephi,
        //firingTowritefileMatlab,
        //firingBw,
        //firingBwMatlab,
        //firingFw,
        //firingFwGephi,
        //firingFwMatlab
    );
    
  }
  
  
}

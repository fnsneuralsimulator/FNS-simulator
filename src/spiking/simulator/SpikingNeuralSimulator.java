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

package spiking.simulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import connectivity.nodes.ConnectivityPackageManager;
import utils.tools.NiceQueue;
import spiking.controllers.node.NodeThread;
import spiking.node.NodesInterconnection;
import spiking.node.NodesManager;
import spiking.node.external_inputs.ExternalInput;
import utils.configuration.NeuManCfg;
import utils.configuration.NodeCfg;
import utils.configuration.SpikingConfigManager;
import utils.configuration.SpikingSimulatorCfg;
import utils.constants.Constants;
import utils.exceptions.BadParametersException;
import utils.experiment.Experiment;
import utils.statistics.StatisticsCollector;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.cli.*; 

public class SpikingNeuralSimulator extends Thread{
  private final static String TAG = "[Spiking Neural Simulator] ";
  private final static int SERIALIZE_AFTER=1000000;
  //the simulation start time
  private long simStartTime;
  private Boolean verbose = false;
  private Boolean debug = false;
  private NodesManager nMan;
  private Double minQueuesValue = Double.MAX_VALUE;
  private NiceQueue minQueue=null;
  // the nodes of interest NOI
  private HashMap <Integer,Boolean> NOI;
  private volatile Boolean checkall=false;
  private double total_time=10;
  private double cycle_time;
  public final static long simulationNeurons=100l;
  public final static int externalNeurons=10;
  private Boolean initialized=false;
  private int debNum=27;
  private int [] debugCases = new int[debNum];
  private int completing;
  private double currentTime=0.0;
  private long splitCount = 0;
  //the number neuron Ids must be divided to allow the graphical compression
  //for plotting the neuron fires.
  private double compressionFactor;
  private Boolean keep_running=true;
  private Double avgNeuronalSignalSpeed = 5.1;
  private Boolean badCurve=false;
  //standard parameters for testing, never use for other purposes
  public static final Double excitProportion4Test = 0.8;
  public static final Integer k4Test = 20;
  public static final Double prew4Test = 0.5;
  public static final Double d4Test = 0.04;
  public static final Double ld4Test = 0.001; 
  public static final Double kr4Test = 0.3;
  public static final Double internalAmplitude4Test = 1.0;
  public static final Integer BnTest=1;
  public static final Double IBITest=0.001;
  private static final Double epsilon = 0.00000001;
  long[] times= new long[10];
  /* the coefficient for which multiply the BOP to obtain a cycle: 
  *   1. if it is less than 1, than we fall into the optimistic 
  *      simulation
  *    2. if it is greater or equal to 1, than we have a conservative 
         behavior
  *   Note: using a gamma distribution with shape parameter 
  *         (alpha_lambda) for the connectivity topology lengths, we 
  *         need to re-calculate such factor in order to controll 
  *         lossess between nodes under the bop_conservative_p
  *         probability.
  */
  private static final Double bop_to_cycle_factor = 1.0;
  private static final Double bop_conservative_p = 0.9999;
  private ArrayList<StatisticsCollector> scs = 
      new ArrayList <StatisticsCollector>();
  private Boolean matlab=false;
  private Boolean gephi=false;
  private Boolean reducedOutput=false;
  private Boolean superReducedOutput=false;
  private String defFileName=null;
  private PrintWriter burningPw;
  private PrintWriter burningPwGephi;
  private PrintWriter burningPwMatlab;
  private File burningTowritefile;
  private File burningTowritefileGephi;
  private File burningTowritefileMatlab;
  private BufferedWriter burningBw;
  private BufferedWriter burningBwMatlab;
  private FileWriter burningFw;
  private FileWriter burningFwGephi;
  private FileWriter burningFwMatlab;
  private FileWriter firingFw;
  private FileWriter firingFwGephi;
  private FileWriter firingFwMatlab;
  private PrintWriter firingPw;
  private PrintWriter firingPwGephi;
  private PrintWriter firingPwMatlab;
  private File firingTowritefile;
  private File firingTowritefileGephi;
  private File firingTowritefileMatlab;
  private BufferedWriter firingBw;
  private BufferedWriter firingBwMatlab;
  
  public SpikingNeuralSimulator (){
    nMan = new NodesManager(this, bop_conservative_p);
    simStartTime=System.currentTimeMillis();
    //sc.start();
  }
  
  private void addNodeThread(NodeThread node){
    nMan.addNodeThread(node);
  }
  

  public void init(){
    if (nMan.getnSms()<=0){
      //debprintln(" no node added to the simulator");
      return;
    }
    initialized=true;
    String minTractLengthStr=(nMan.getMinTractLength()!=NodesManager.MAX_TRACT_LENGTH)?
        (""+nMan.getMinTractLength()):" there are no connections between nodes.";
    println("min tract length:"+minTractLengthStr);
    println("avg neuronal signal speed:"+avgNeuronalSignalSpeed);
    cycle_time=(nMan.getMinTractLength()+epsilon)/
        (avgNeuronalSignalSpeed*bop_to_cycle_factor);
  }

  private void setTotalTime(double total_time){
    this.total_time=total_time;
  }

  /**
   * Starts all node threads
   */
  public void startAll(){
    completing = nMan.getNodeThreadsNum()-1;
    nMan.startAll();
  }
  
  /**
   * Awakes all node threads
   * @param newStopTime the stop time for the next "split"
   *
   */
  public void runNewSplit(double newStopTime){
    double stopTime=(newStopTime>total_time)?total_time:newStopTime;
    if (verbose||((splitCount%100)==0))
      println(
          "running new split "
          +splitCount
          +" with new stop simulated time:"
          +stopTime);
    nMan.runNewSplit(stopTime);
  }
  
  /**
   * Kills all node threads
   */
  public void killAll(){
    println("stopping node threads...");
    nMan.killAll();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    keep_running=false;
    println("node threads stopped.");
    println("killing statistics collector...");
    killscs();
    println("statistics collector stopped.");
  }

  private void killscs(){
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).kill();
    scs.get(0).close();
  }

/**
 * This function performs the spike routine for 
 * a fixed number of fires (nstop).
 * 
 * The main characters are:
 *   1. the active neurons: neurons which are over the spike 
 *      threshold and are going to "shoot"
 *      at a certain time. This fire time may be deleted by 
 *      effect of the arrival of
 *      a negative input or anticipated by the arrival 
 *      of a new excitatory signal.
 *   2. the synapsys queues: synapsys are modeled by a queue for 
 *      each couple of connected neurons. 
 *      Each queue holds the signals generated by the 
 *      source neuron but not still "delivered"
 *      at the target neuron.
 *     
 */
  
  public void run(){
    if (!initialized)
      return;
    println("starting nodes...");
    startAll();
    println("nodes started.");
    while (keep_running){
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    println(
        "end of simulator run, "+
        (System.currentTimeMillis()-simStartTime)+
        " ms elapsed.");
    println("effective simulation time: "+
        (System.currentTimeMillis()-
            simStartTime-
            times[0]-
            times[1]-
            times[2]-
            times[3]-
            times[4])+
        " ms.");
    println(
        "init phases:\n\t conn pckg read:\t\t"+
        times[0]+" ms\n\t config file read:\t\t"+
        times[1]+
        " ms\n\t node init:\t\t\t"+
        times[2]+
        " ms\n\t inter-node connections init:\t"+
        times[3]+
        " ms\n\t simulator init:\t\t"+
        times[4]+
        " ms\n\t total init time:\t\t"+
        (
            times[0]+
            times[1]+
            times[2]+
            times[3]+
            times[4])+
        " ms");
    println("avg neuronal signal speed:"+avgNeuronalSignalSpeed);
    println("cycle time:"+cycle_time);
    println(
        "total inter node axonal connections:"+
        nMan.getTotalInterNodeConnectionsNumber());
  }
  
  
  public Boolean splitComplete(Integer nodeId){
    if (completing>0)
      --completing;
    else{
      ++splitCount;
      if (currentTime>=total_time)
        killAll();
      else{
        try {
          sleep(5);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        currentTime+=cycle_time;
        runNewSplit(currentTime);
        completing=nMan.getNodeThreadsNum()-1;
      }
      return false;
    }
    return true;
  }

  private void setMinMaxNe_xn_ratiosScs(
      Double minNe_xn_ratio, 
      Double maxNe_xn_ratio){
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setMinMaxNe_xn_ratios(
          minNe_xn_ratio, 
          maxNe_xn_ratio);
  }

  private void setSerializeAfterScs(int serializeAfter){
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setSerializeAfter(serializeAfter);
  }
 
  public void startScs(){
    initBurningWriters();
    initFiringWriters();
    for (int i=0; i<scs.size(); ++i){
      scs.get(i).init(defFileName);
      scs.get(i).setWriters(
        burningPw,
        burningPwGephi,
        burningPwMatlab,
        //burningTowritefile,
        //burningTowritefileGephi,
        //burningTowritefileMatlab,
        //burningBw,
        //burningBwMatlab,
        //burningFw,
        //burningFwGephi,
        //burningFwMatlab,
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
      scs.get(i).start();
    }
  }

  private void initBurningWriters(){
    Boolean newfile=false;
    try{
      if (reducedOutput)
        burningTowritefile= new File(defFileName+"_burning_r.csv");
      else if (superReducedOutput)
        burningTowritefile= new File(defFileName+"_burning_R.csv");
      else
        burningTowritefile= new File(defFileName+"_burning.csv");
      if (burningTowritefile.exists())
        burningFw = new FileWriter(firingTowritefile,true);
      else{
        newfile=true;
        burningTowritefile.createNewFile();
        burningFw = new FileWriter(burningTowritefile);
      }
      burningBw = new BufferedWriter(burningFw);
      burningPw= new PrintWriter(burningBw);
      //----------
      // matlab
      //----------
      if (matlab){
        burningTowritefileMatlab= new File(defFileName+"_burning_matlab.csv");
        if (burningTowritefileMatlab.exists())
          burningFwMatlab = new FileWriter(burningTowritefileMatlab,true);
        else{
          burningTowritefileMatlab.createNewFile();
          burningFwMatlab = new FileWriter(burningTowritefileMatlab);
        }
        burningBwMatlab = new BufferedWriter(burningFwMatlab);
        burningPwMatlab=new PrintWriter(burningFwMatlab);
      }
      //----------
      // gephi 
      //----------
      if (gephi){
        burningTowritefileGephi = new File(defFileName+"_burning_gephi.csv");
        if (burningTowritefileGephi.exists())
          burningFwGephi = new FileWriter(burningTowritefileGephi,true);
        else{
          burningTowritefileGephi.createNewFile();
          burningFwGephi = new FileWriter(burningTowritefileGephi);
        }
        BufferedWriter burningBwGephi = new BufferedWriter(burningFwGephi);
        burningPwGephi=new PrintWriter(burningBwGephi);
        if (newfile)
          burningPwGephi.println( "Firing, Burning");
    }
    burningBw = new BufferedWriter(burningFw);
    if (newfile && !reducedOutput)
      burningPw.println(
          "Burning Time, "
          + "Firing Node, "
          + "Firing Neuron, "
          + "Burning Node, "
          + "Burning Neuron, "
          + "External Source, "
          + "From Internal State, "
          + "To Internal State, "
          + "Step in State, "
          +"Post Synaptic Weight, "
          + "Pre Synaptic Weight, "
          + "Instant to Fire, "
          + "(Afferent) Firing Time");
    } catch (IOException e){
      e.printStackTrace();
    }

  }

  private void initFiringWriters(){
    Boolean newfile=false;
    try{
      if (reducedOutput)
        firingTowritefile= new File(defFileName+"_firing_r.csv");
      else if (superReducedOutput)
        firingTowritefile= new File(defFileName+"_firing_R.csv");
      else
        firingTowritefile= new File(defFileName+"_firing.csv");
      if (firingTowritefile.exists())
        firingFw = new FileWriter(firingTowritefile,true);
      else{
        newfile=true;
        firingTowritefile.createNewFile();
        firingFw = new FileWriter(firingTowritefile);
      }
      firingBw = new BufferedWriter(firingFw);
      firingPw = new PrintWriter(firingBw);
      //----------
      // matlab
      //----------
      if (matlab){
        firingTowritefileMatlab= new File(defFileName+"_firing_matlab.csv");
        if (firingTowritefileMatlab.exists())
          firingFwMatlab = new FileWriter(firingTowritefileMatlab,true);
        else{
          firingTowritefileMatlab.createNewFile();
          firingFwMatlab = new FileWriter(firingTowritefileMatlab);
        }
        firingBwMatlab = new BufferedWriter(firingFwMatlab);
        firingPwMatlab=new PrintWriter(firingBwMatlab);
      }
      firingBw = new BufferedWriter(firingFw);
      if (newfile && !reducedOutput)
        firingPw.println(
            "Firing Time, "
            +"Firing Node, "
            +"Firing Neuron, "
            +"Neuron Type, "
            +"External Source");
    }catch(IOException e){
      e.printStackTrace();
      }

  }

  
  public void setFilename(String filename){
    //filename=filename+"exp-";
    int count=1;
    File towritefile;
    for(;;++count) {
      if (superReducedOutput)
        towritefile= new File(
            filename
            +String.format("%03d", count)
            +"_burning_R.csv");
      else if (reducedOutput)
        towritefile= new File(
            filename
            +String.format("%03d", count)
            +"_burning_r.csv");
      else
        towritefile= new File(
            filename
            +String.format("%03d", count)
            +"_burning.csv");
      if(!towritefile.exists()){
        defFileName=filename+String.format("%03d", count);
          break;
      }
    }
  }

  public void init_scs(){
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).init(defFileName);    
  }
 
  private void setMatlabScs(){
    matlab=true;
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setMatlab();    
  }
 
  private void setReducedOutputScs(){
    reducedOutput=true;
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setReducedOutput();    
  }
 
  private void setSuperReducedOutputScs(){
    superReducedOutput=true;
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setSuperReducedOutput();    
  }
 
  private void setGephiScs(){
    gephi=true;
    for (int i=0; i<scs.size(); ++i)
      scs.get(i).setGephi();    
  }
 
  private void printResultsScs(){
    for (int i=0; i<scs.size(); ++i){
      System.out.println(
          "\n==============| node "+
          i+
          "|=============");
      scs.get(i).PrintResults();    
    }
    System.out.println();
  }
 
  /**
  *   Read the config files for nodes and connectivity topology,
  *   creates a thread for each network node with the corersponding
  *   parameters and parametrizes the inter-node synapses
  *
  *   @param configPath   the path of the config package
  *   @param connPkgPath  the path of che connectivity package (the  
  *                       folder in which all the topology parameters
  *                       are stored
  *   @param do_fast      if true, fastest algorithms are used 
  *                       (with some aproximations)
  */
  public void initFromConfigFileAndConnectivityPackage(
      //String filename,
      String configPath, 
      String connPkgPath, 
      Boolean do_fast) 
      throws BadParametersException{
    long startTime = System.currentTimeMillis();
    long lastTime = startTime;
    println("reading connectivity package file:"+connPkgPath);
    ConnectivityPackageManager cpm = new ConnectivityPackageManager(); 
    cpm.readConnectivityPackage(connPkgPath);
    for (int i=0; i<cpm.getNodesNum();++i)
      scs.add(new StatisticsCollector());
    setMinMaxNe_xn_ratiosScs(
        cpm.getMinNe_xn_ratio(), 
        cpm.getMaxNe_xn_ratio());
    ArrayList<NodesInterconnection> conns = 
        cpm.getInterNodeConnections();
    times[0]=System.currentTimeMillis()-lastTime;
    lastTime+=times[0];
    System.out.println("reading config file:"+configPath);
    SpikingSimulatorCfg ssc = 
        SpikingConfigManager.readConfigFile(configPath);
    setTotalTime(new Double(ssc.getStop()));
    HashMap <Integer, NodeCfg> nodeCs =  ssc.getNodesMap();
    avgNeuronalSignalSpeed=ssc.getAvg_neuronal_signal_speed();
    Integer serializeAfter=ssc.getSerialize_after();
    if (serializeAfter==null)
      serializeAfter=SERIALIZE_AFTER;
    setSerializeAfterScs(serializeAfter);
    times[1]=System.currentTimeMillis()-lastTime;
    lastTime+=times[1];
    System.out.println("creating and adding nodes...\n");
    NodeCfg tmp;
    Boolean tmpPlasticity;
    Long tmpN;
    Integer  tmpK, tmpExternal, tmpBn;
    Double tmpExcitRatio;
    Double tmp_mu_w_exc;
    Double tmp_mu_w_inh;
    Double tmp_sigma_w_exc;
    Double tmp_sigma_w_inh;
    Double tmpW_pre_exc;
    Double tmpW_pre_inh;
    Double tmpRewiringP;
    Double tmpIBI;
    Double tmpEtap;
    Double tmpEtam;
    Double tmpTaup;
    Double tmpTaum;
    Double tmpWMax;
    Double tmpTo;
    NeuManCfg nmcfg;
    Boolean lif=new Boolean(ssc.getLif());
    Boolean exp_decay=new Boolean(ssc.getExp_decay());
    for (int i=0; i<cpm.getNodesNum();++i){
      tmp = ( nodeCs.get(i) != null )?nodeCs.get(i):null;
      tmpN=((tmp!=null)&&(tmp.getN()!=null))?
          tmp.getN():ssc.getGlob_local_n();
      tmpExcitRatio = ((tmp!=null)&&(tmp.getExcitatory_inhibitory_ratio()!=null))?
          tmp.getExcitatory_inhibitory_ratio():
          ssc.getR();
      tmp_mu_w_exc=((tmp!=null)&&(tmp.getMu_w_exc()!=null))?
          tmp.getMu_w_exc():
          ssc.getGlob_mu_w_exc();
      tmp_mu_w_inh=((tmp!=null)&&(tmp.getMu_w_inh()!=null))?
          tmp.getMu_w_inh():
          ssc.getGlob_mu_w_inh();
      tmp_sigma_w_exc=((tmp!=null)&&(tmp.getSigma_w_exc()!=null))?
          tmp.getSigma_w_exc():
          ssc.getGlob_sigma_w_exc();
      tmp_sigma_w_inh=((tmp!=null)&&(tmp.getSigma_w_inh()!=null))?
          tmp.getSigma_w_inh():
          ssc.getGlob_sigma_w_inh();
      tmpW_pre_exc=((tmp!=null)&&(tmp.getW_pre_exc()!=null))?
          tmp.getW_pre_exc():
          ssc.getGlob_w_pre_exc();
      tmpW_pre_inh=((tmp!=null)&&(tmp.getW_pre_inh()!=null))?
          tmp.getW_pre_inh():
          ssc.getGlob_w_pre_inh();
      tmpK = ((tmp!=null)&&(tmp.getK()!=null))? 
          tmp.getK(): ssc.getGlob_k();
      tmpRewiringP = ((tmp!=null)&&(tmp.get_rewiring_P()!=null))?
          tmp.get_rewiring_P() : ssc.getGlob_rewiring_P();
      tmpExternal = ((tmp!=null)&&(tmp.getExternal_inputs_number()!=null))?
          tmp.getExternal_inputs_number() : ssc.getGlob_external_inputs_number();
      tmpBn=((tmp!=null)&&(tmp.getBn()!=null))?
          tmp.getBn() : ssc.getGlob_Bn();
      if (tmpBn==0)
        tmpBn=1;
      tmpIBI=((tmp!=null)&&(tmp.getIBI()!=null))?
          tmp.getIBI() : ssc.getGlob_IBI();
      nmcfg=((tmp!=null)&&(tmp.getNeuron_manager()!=null))?
          tmp.getNeuron_manager() : ssc.getGlobal_neuron_manager();
      tmpPlasticity=((tmp!=null)&&(tmp.getPlasticity()!=null))?
          tmp.getPlasticity() : ssc.getPlasticity();
      tmpEtap=((tmp!=null)&&(tmp.getEtap()!=null))?
          tmp.getEtap() : ssc.getGlob_etap();
      tmpEtam=((tmp!=null)&&(tmp.getEtam()!=null))?
          tmp.getEtam() : ssc.getGlob_etam();
      tmpTaup=((tmp!=null)&&(tmp.getTaup()!=null))?
          tmp.getTaup() : ssc.getGlob_taup();
      tmpTaum=((tmp!=null)&&(tmp.getTaum()!=null))?
          tmp.getTaum() : ssc.getGlob_taum();  
      tmpWMax=((tmp!=null)&&(tmp.getW_max()!=null))?
          tmp.getW_max() : ssc.getGlob_w_max();  
      tmpTo=((tmp!=null)&&(tmp.getTo()!=null))?
          tmp.getTo() : ssc.getGlob_to();  
      if (tmpK>=tmpN){
        throw new BadParametersException("bad parameters exception, "
            + "n has to be greater than k (now n is "+tmpN+
            " and k is +"+tmpK+")");
      }
      //add a new node with or without external inputs
      println("adding node:"+i);
      if (tmpExternal.equals(0)) {
        addNodeThread(
            new NodeThread(
                nMan, 
                cpm.getNode(i).getId(),
                tmpN,
                tmpExcitRatio,
                tmpK,
                tmpRewiringP,
                tmpBn,
                tmpIBI,
                nmcfg.getC(),
                nmcfg.getD_exc(),
                nmcfg.getD_inh(),
                nmcfg.getT_arp(),
                tmp_mu_w_exc,
                tmp_mu_w_inh,
                tmp_sigma_w_exc,
                tmp_sigma_w_inh,
                tmpW_pre_exc,
                tmpW_pre_inh,
                Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
                tmpPlasticity,
                tmpEtap,
                tmpEtam,
                tmpTaup,
                tmpTaum,
                tmpWMax,
                tmpTo,
                avgNeuronalSignalSpeed,
                lif,
                exp_decay,
                do_fast,
                (checkall||NOI.get(cpm.getNode(i).getId())),
                scs.get(i)));
      }
      else{
        Integer tmpExternalType; 
        Double tmpExternalTimestep;
        Integer tmpExternalFireDuration;
        Integer tmpExternalFireOutdegree;
        Double tmpExternalFireAmplitude;
        Double tmpExternalInputsTimeOffset;
        tmpExternalType=
            ((tmp!=null)&&(tmp.getExternal_inputs_type()!=null))?
                tmp.getExternal_inputs_type():
                ssc.getGlob_external_inputs_type();
        tmpExternalInputsTimeOffset=
            ((tmp!=null)&&(tmp.getExternal_inputs_time_offset()!=null))?
                tmp.getExternal_inputs_time_offset():
                ssc.getGlob_external_inputs_time_offset();
        tmpExternalTimestep=
            ((tmp!=null)&&(tmp.getExternal_inputs_timestep()!=null))?
                tmp.getExternal_inputs_timestep():
                ssc.getGlob_external_inputs_timestep();
        tmpExternalFireDuration=
            ((tmp!=null)&&(tmp.getExternal_inputs_fireduration()!=null))?
                tmp.getExternal_inputs_fireduration():
                ssc.getGlob_external_inputs_fireduration();
        tmpExternalFireOutdegree=
            ((tmp!=null)&&(tmp.getExternal_inputs_outdegree()!=null))?
                tmp.getExternal_inputs_outdegree():
                ssc.getGlob_external_inputs_outdegree();
        tmpExternalFireAmplitude=
            ((tmp!=null)&&(tmp.getExternal_inputs_amplitude()!=null))?
                tmp.getExternal_inputs_amplitude():
                ssc.getGlob_external_inputs_amplitude();
        addNodeThread(
            new NodeThread(
                    nMan,
                    cpm.getNode(i).getId(),
                    tmpN,
                    tmpExternal,
                    tmpExternalType,
                    tmpExternalInputsTimeOffset,
                    tmpExternalTimestep,
                    tmpExternalFireDuration,
                    tmpExternalFireAmplitude,
                    tmpExternalFireOutdegree,
                    tmpExcitRatio,
                    tmpK,
                    tmpRewiringP,
                    tmpBn,
                    tmpIBI,
                    nmcfg.getC(),
                    nmcfg.getD_exc(), 
                    nmcfg.getD_inh(), 
                    nmcfg.getT_arp(),
                    tmp_mu_w_exc,
                    tmp_mu_w_inh,
                    tmp_sigma_w_exc,
                    tmp_sigma_w_inh,
                    tmpW_pre_exc,
                    tmpW_pre_inh,
                    Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
                    tmpPlasticity,
                    tmpEtap,
                    tmpEtam,
                    tmpTaup,
                    tmpTaum,
                    tmpWMax,
                    tmpTo,
                    avgNeuronalSignalSpeed,
                    lif,
                    exp_decay,
                    do_fast,
                    (checkall||NOI.get(cpm.getNode(i).getId())),
                    scs.get(i)));
      }
    }
    calculateCompressionFactor();
    times[2]=System.currentTimeMillis()-lastTime;
    lastTime+=times[2];
    println("adding inter-node connection ...");
    for (int i=0; i<conns.size();++i){
      println("adding connection bundle: "+conns.get(i).getSrc()+"-"+conns.get(i).getDst());
      addInterNodeThreadConnection(
          nMan.getNodeThread(conns.get(i).getSrc()), 
          nMan.getNodeThread(conns.get(i).getDst()), 
          conns.get(i).getNe_xn_ratio(),
          conns.get(i).getMu_omega(),
          conns.get(i).getSigma_w(),
          conns.get(i).getLength(),
          conns.get(i).getLengthShapeParameter(),
          conns.get(i).getType());
    }
    times[3]=System.currentTimeMillis()-lastTime;
    lastTime+=times[3];
    System.out.println("initializing simulator...\n");
    init();
    times[4]=System.currentTimeMillis()-lastTime;
    lastTime+=times[4];
  }
  
  /**
  *   Creates a new inter-node connection
  *   Each connection is to be intended as a couple of boundle of 
  *   synapsis between two nodes, one per each direction
  *
  *   @param nd1                  one node of the connection
  *   @param nd2                  the other node of the connection
  *   @param Ne_xn_ratio          the ratio of excitatory connections
  *   @param mu_omega             the mean of postsynaptic weight 
  *                               distribution
  *   @param sigma_omega          the std dev of postsynaptic weights 
  *                               distribution
  *   @param mu_lambda            the mean of synapsis length 
  *                               distribution
  *   @param alpha_lambda         the shape factor of the synapsys 
  *                               length distribution
  *   @param inter_node_conn_type the type of inter node connection
  *
  */
  public void addInterNodeThreadConnection(
      NodeThread nd1, 
      NodeThread nd2, 
      Double Ne_xn_ratio, 
      Double mu_omega, 
      Double sigma_omega, 
      Double mu_lambda, 
      Double alpha_lambda,
      Integer inter_node_conn_type){
    nMan.addInterNodeConnection(
        nd1, 
        nd2, 
        Ne_xn_ratio,
        mu_omega, 
        sigma_omega, 
        mu_lambda, 
        alpha_lambda,
        inter_node_conn_type);
  }
  
  /**
  *  Allows debug print to the stdout
  */
  public void setDebug(Boolean debug){
    this.debug=debug;
    nMan.setDebug(debug);
  }

  /**
  * Set the name for the simulation
  * The name is the same of the configuration path
  */
  public void setExperimentName(String expName) {
    Experiment.setExperimentName(expName);
    File expDir = new File (Experiment.getExperimentDir());
    if (!expDir.exists())
      expDir.mkdirs();
  }
  
  private void calculateCompressionFactor(){
    if (nMan.getTotalN()>1000000)
      compressionFactor = 
          new Double(Integer.MAX_VALUE) / nMan.getTotalN();
    else
      compressionFactor = 1.0;
    nMan.setCompressionFactor(compressionFactor);
  }
  
  public double getReducingFactor(){
    return compressionFactor;
  }
  
  
  public void setNOI(HashMap <Integer, Boolean> NOI){
    this.NOI=NOI;
  }

  public void checkAll(){
    this.checkall=true;
  }

  public void setVerbose(){
    verbose=true;
  }

  public void setBadCurve(){
    badCurve=true;
  }

  //================   printing functions ============================
  
  private void println(String s){
      System.out.println(TAG+s);
  }
  
  private void debprintln(String s){
    if (verbose&&debug)
      System.out.println(TAG+"[debug] "+s);
  }
  
  public void printDebug(){
    System.out.println("debug variables:");
    for(int i=0; i<debNum;++i)
      System.out.print("\n\t"+i+". "+debugCases[i]);
    System.out.println();
  }

  public void printNodeFields(){
    nMan.printNodeFields();
  }
  
  public void printBreakLine(){
    if (verbose)
      System.out.println("\n\n----------------------------------------"
          +splitCount
          +"-----------------------------------------------------\n");
  }
  
  //==========================   main function ========================

  public static void main(String[] args) {    
    System.out.println("\n\n\n\t\t\t\t\t"
        +"=================================");
    System.out.println("\t\t\t\t\t=\t    F N S\t\t=");
    System.out.println("\t\t\t\t\t=\tNeural Simulator\t=");
    System.out.println("\t\t\t\t\t"
        +"=================================\n\n");
    // options parsing and management
    Options options= new Options();
        options.addOption("n", "nodes-list", true, " followed by the "
            +"list of the node of interest (NOI) "
            + "for which to store the output data. "
            + "The format for such list is like this exmaple: [3,25,13,12]. "
            + "If this switch is not present, the entire set of nodes"
            + "will be considered for the generation of output data.");
        options.addOption("f", "fast", false, "enables faster "
            +"algorithms at different levels, "
            + "in return for some approximations "
            +"(i.e., plasticity exponentials, etc.)");
        options.addOption("m", "matlab", false, "provides with a set "
            +"of matlab-compliant "
            + "CSV files, in addition to the output CSVs.");
        options.addOption("r", "reduced-output", false, 
            "enables reduced CSV files, i.e., outputs that indicates "
            +"only spiking events and inner states of the neurons");
        options.addOption("R", "super-reduced-output", false, 
            "enables super-reduced CSV files, i.e., outputs that indicates "
            +"only spiking events and inner states of the neurons"
            +" with reduced precision");
        options.addOption("g", "gephi", false, 
            "produce also a csv for Gephi");
        options.addOption("v", "verbose", false, "print verbose output");
        options.addOption("h", "help", false, "shows this help");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")){
              formatter.printHelp("FNS", options);
              System.out.println("\nExamples:");
              System.out.println("[Windows] \t> .\\start.bat"
                  +" exp01 -f -n [3,25,13,12] -m -r");
              System.out.println("[Linux] \t$ ./start"
                  +" exp01 -f -n [3,25,13,12] -m -r\n");
              System.exit(0);
              return;
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("FNS", options);
            System.out.println("\nExamples:");
          System.out.println(
              "[Windows] > .\\start.bat "
              +"exp01 -f -n [3,25,13,12] -p -m -r" );
          System.out.println(
              "[Linux] > ./start exp01 -f -n [3,25,13,12] -p -m -r\n");
            System.exit(1);
            return;
        }
        // intializing simulator
    System.out.println("initializing simulator");
    SpikingNeuralSimulator sns = new SpikingNeuralSimulator();
    sns.setExperimentName(args[0]);
    String filename=null;
    HashMap<Integer, Boolean> NOI=new HashMap<Integer,Boolean>();
    Boolean do_plot=cmd.hasOption("plot");
    Boolean do_fast=cmd.hasOption("fast");
    String nodeListString=cmd.getOptionValue("nodes-list","[]")
        .replaceAll("\\s+","")
        .substring(1,cmd.getOptionValue("nodes-list","[]").length()-1);
    if(nodeListString.length()>0){
      String [] nodesStr=nodeListString.split(",");
      for (int i=0; i<nodesStr.length; ++i)
        NOI.put(Integer.parseInt(nodesStr[i]),true);
      filename = Experiment.getExperimentDir()
          +"nodes_"
          + nodeListString.replaceAll(",","-")
          +"_";
      sns.setNOI(NOI);
    }
    else {
      filename = Experiment.getExperimentDir()+"all_nodes_";
      sns.checkAll();
    }
    if (cmd.hasOption("verbose"))
      sns.setVerbose();
    try {
      sns.initFromConfigFileAndConnectivityPackage(
          (new File(args[0]+"/config.xml")).getAbsolutePath(), 
          (new File(args[0]+"/connectivity")).getAbsolutePath(),
          do_fast);
    } catch (BadParametersException e) {
      e.printStackTrace();
    }
    if (cmd.hasOption("matlab"))
      sns.setMatlabScs();
    if (cmd.hasOption("reduced-output"))
      sns.setReducedOutputScs();
    if (cmd.hasOption("super-reduced-output"))
      sns.setSuperReducedOutputScs();
    if (cmd.hasOption("gephi"))
      sns.setGephiScs();
    System.out.println("nodes to check mask:"+nodeListString);    
    sns.setFilename(filename);
    System.out.println("starting statistic collector...");
    sns.startScs();
    System.out.println("starting simulator...\n");
    sns.start();
    try {
      sns.join();
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    sns.printResultsScs();
    sns.killscs();
    System.out.println("bye!");
    System.exit(0);
  }
}



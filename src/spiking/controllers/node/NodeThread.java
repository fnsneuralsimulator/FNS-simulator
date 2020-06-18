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

package spiking.controllers.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import spiking.internode.InterNodeBurningSpike;
import spiking.internode.InterNodeSpike;
import spiking.node.Node;
import spiking.node.NodesManager;
import spiking.node.Synapse;
import spiking.node.SynapsesManager;
import spiking.node.neuron.NodeNeuronsManager;
import spiking.node.spikes.FixedBurnSpike;
import utils.constants.Constants;
import utils.tools.NiceNode;
import utils.tools.NiceQueue;
import utils.math.FastMath;
import utils.statistics.StatisticsCollector;
import utils.statistics.BurningWriter;
import utils.statistics.FiringWriter;

public class NodeThread extends Thread{
  
  private final static String TAG = "[Node Thread ";
  private final static Boolean verbose = true;

  private Node n;
  private NodeNeuronsManager nnMan;
  private NodesManager nMan;
  private SynapsesManager synMan;
  
  private Boolean plasticity;
  //queues map enqueues the spikes sended by a specific firing neuron 
  //to a specific burning neuron
  //extendible
  private HashMap<Synapse, NiceQueue>queuesMap;
  private double currentTime=0.0;
  private double stopTime=0.0;
  private double startTime=0.0;
  private Boolean debug = false;
  private Boolean activePassiveDebug = false;
  private int debug_level = 3;
  private int debNum=28;
  private ArrayList<InterNodeSpike> internodeFires;  
  private Boolean keepRunning=true;
  private Lock lock = new ReentrantLock();
  private Condition newTimeSplitCond = lock.newCondition();
  private PriorityQueue<InterNodeBurningSpike> interNodeBurningSpikes=
      new PriorityQueue<InterNodeBurningSpike>();
  private PriorityQueue<FixedBurnSpike> burningQueueSpikes=
      new PriorityQueue<FixedBurnSpike>();
  private FastMath fm = new FastMath();
  private Double debugMaxWPDiff=0.0;
  private Boolean do_fast;
  private Boolean isNOI;
  private Boolean lif = false;
  private Boolean exp_decay = false;
  long[] times= new long[10];
  private StatisticsCollector sc;
  private int toremDebug=0;
  private BurningWriter burningWriter;
  private FiringWriter firingWriter;
  //private NodeFiringsThread nft;
  //private NodeBurningsThread nbt;
  

  /**
  *   The NodeThread object
  *
  *   @param nMan             the simulation NodesManager object
  *   @param id               the node id
  *   @param n                the number of neurons of the node
  *   @param excitProportion  the ratio of excitatory neurons over the total
  *                           number of neurons 'n'
  *   @param k                the conn-degree of each neuron
  *   @param prew             the prob of small-world topology rewinig
  *   @param Bn               the number of bursts spike for each 
  *                           firing neuron
  *   @param IBI              the burst inter-spike time 
  *   @param D_exc
  *   @param D_inh
  *   @param mu_w_exc         the mean of the postsynaptic weight 
  *                           distribution for excitatory neurons
  *   @param mu_w_inh         the mean of the postsynaptic weight 
  *                           distribution for inhibitory neurons
  *   @param sigma_w_exc      the std deviation of the postsynaptic weight
  *                           distribution for excitatory neurons
  *   @param sigma_w_inh      the std deviation of the postsynaptic weight
  *                           distribution for inhibitory neurons
  *   @param w_pre_exc        the presynaptic weight for excitatory neurons
  *   @param w_pre_inh        the presynaptic weight for inhibitory neurons
  *   @param externalPresynapticDefVal
  *   @param plasticity       simulate neuron plasticity
  *   @param etap             the Etap for plasticity
  *   @param etam             the Etam for plasticity
  *   @param taup             the Taup for plasticity
  *   @param taum             the Taum for plasticity
  *   @param pwMax            the pwMax for plasticity
  *   @param to               the to for plasticity
  *   @param avgNeuronalSignalSpeed the signal speed through synapsis
  *   @param lif              do lif simulation
  *   @param exp_decay        use exponential sub-threashold decay
  *   @param do_fast          use fastest algorithms (some aproximations)
  *
  */
  public NodeThread(
      NodesManager nMan, 
      Integer id, 
      Long n, 
      Double excitProportion,
      Integer k, 
      Double prew,
      Integer Bn,
      Double IBI,      
      Double D_exc, 
      Double D_inh, 
      Double ld, 
      Double kr,
      Double mu_w_exc,
      Double mu_w_inh,
      Double sigma_w_exc,
      Double sigma_w_inh,
      Double w_pre_exc,
      Double w_pre_inh,
      Double externalPresynapticDefVal, 
      Boolean plasticity,
      Double etap,
      Double etam,
      Double taup,
      Double taum, 
      Double pwMax,
      Double to,
      Double avgNeuronalSignalSpeed,
      Boolean lif,
      Boolean exp_decay,
      Boolean do_fast,
      Boolean isNOI,
      StatisticsCollector sc){
    this.n=new Node(
        id,
        n, 
        excitProportion,
        mu_w_exc,
        mu_w_inh,
        sigma_w_exc,
        sigma_w_inh,
        w_pre_exc,
        w_pre_inh,
        k, 
        prew,
        Bn,
        IBI,
        plasticity,
        etap,
        etam,
        taup,
        taum,
        pwMax,
        to);
    init(nMan, 
        D_exc, 
        D_inh, 
        ld, 
        kr, 
        w_pre_exc, 
        w_pre_inh, 
        externalPresynapticDefVal, 
        avgNeuronalSignalSpeed,
        lif,
        exp_decay,
        do_fast,
        isNOI,
        sc);
  }
  
  /**
  *   The NodeThread object
  *
  *   @param nMan             the simulation NodesManager object
  *   @param id               the node id
  *   @param n                the number of neurons of the node
  *   @param externalInputs   the number of neurons with external inputs
  *   @param externalInputType the type of external input 
  *                            0. poisson
  *                            1. constant
  *                            2.noise
  *   @param externalInputsTimeOffset the time offset before external input  
  *                           firings to begin
  *   @param timeStep         the avg for the external input fire distribution
  *   @param fireDuration     the duration of external input activity
  *   @param externalAmplitude the amplitude of an external input signal
  *   @param externalOutdegree the external out-degree of each external input
  *   @param excitRatio  the ratio of excitatory neurons over the total
  *                           number of neurons 'n'
  *   @param k                the conn-degree of each neuron
  *   @param prew             the prob of small-world topology rewinig
  *   @param Bn               the number of bursts spike for each 
  *                           firing neuron
  *   @param IBI              the burst inter-spike time 
  *   @param D_exc
  *   @param D_inh
  *   @param mu_w_exc         the mean of the postsynaptic weight 
  *                           distribution for excitatory neurons
  *   @param mu_w_inh         the mean of the postsynaptic weight 
  *                           distribution for inhibitory neurons
  *   @param sigma_w_exc      the std deviation of the postsynaptic weight
  *                           distribution for excitatory neurons
  *   @param sigma_w_inh      the std deviation of the postsynaptic weight
  *                           distribution for inhibitory neurons
  *   @param w_pre_exc        the presynaptic weight for excitatory neurons
  *   @param w_pre_inh        the presynaptic weight for inhibitory neurons
  *   @param externalPresynapticDefVal
  *   @param plasticity       simulate neuron plasticity
  *   @param etap             the Etap for plasticity
  *   @param etam             the Etam for plasticity
  *   @param taup             the Taup for plasticity
  *   @param taum             the Taum for plasticity
  *   @param pwMax            the pwMax for plasticity
  *   @param to               the to for plasticity
  *   @param avgNeuronalSignalSpeed the signal speed through synapsis
  *   @param lif              do lif simulation
  *   @param exp_decay        use exponential sub-threashold decay
  *   @param do_fast          use fastest algorithms (some aproximations)
  *
  */
  public NodeThread(
      NodesManager nMan, 
      Integer id, 
      Long n, 
      Integer externalInputs, 
      int externalInputType,
      Double externalInputsTimeOffset,
      double timeStep, 
      //Double fireRate,  
      int fireDuration,
      Double externalAmplitude,
      int externalOutdegree,
      Double excitRatio,
      Integer k, 
      Double prew, 
      Integer Bn,
      Double IBI,
      Double c, 
      Double D_exc, 
      Double D_inh, 
      Double t_arp,
      Double mu_w_exc,
      Double mu_w_inh,
      Double sigma_w_exc,
      Double sigma_w_inh,
      Double w_pre_exc,
      Double w_pre_inh,
      Double externalPresynapticDefVal, 
      Boolean plasticity,
      Double etap,
      Double etam,
      Double taup,
      Double taum, 
      Double pwMax,
      Double to,
      Double avgNeuronalSignalSpeed,
      Boolean lif, 
      Boolean exp_decay, 
      Boolean do_fast,
      Boolean isNOI,
      StatisticsCollector sc){
    this.n=new Node(id,
        n,
        externalInputs,
        externalInputType,
        externalInputsTimeOffset,
        timeStep,
        //fireRate, 
        fireDuration,
        externalAmplitude, 
        externalOutdegree,
        excitRatio,
        mu_w_exc,
        mu_w_inh,
        sigma_w_exc,
        sigma_w_inh,
        w_pre_exc,
        w_pre_inh,
        k, 
        prew, 
        Bn,
        IBI,
        plasticity,
        etap,
        etam,
        taup,
        taum,
        pwMax,
        to);
    init(nMan, 
        c, 
        D_exc, 
        D_inh, 
        t_arp, 
        w_pre_exc, 
        w_pre_inh, 
        externalPresynapticDefVal, 
        avgNeuronalSignalSpeed,
        lif, 
        exp_decay, 
        do_fast,
        isNOI,
        sc);
    
  }

  
  /**
   *   The initialization function for NodeThread
   *   @param nMan                          the NodesManager
   *   @param c                               
   *   @param D_exc                          
   *   @param D_inh 
   *   @param t_arp 
   *   @param excitatoryPresynapticDefVal   the presynaptic weight for 
   *                                        excitatory neurons
   *   @param inhibithoryPresynapticDefVal  the presynaptic weight for 
   *                                        inhibitory neurons
   *   @param externalPresynapticDefVal     the presynaptic weight for 
   *                                        external inputs 
   *   @param avgNeuronalSignalSpeed        the avg signal through axons
   *   @param lif                           if true, a least integrate 
   *                                        and fire simulation is performed
   *   @param exp_decay                     if true, the underthreashold 
   *                                        neuronal decay would be 
   *                                        exponential
   *   @param do_fast                       if true, fastest algorithm 
   *                                        are used for expensive 
   *                                        calculations. This  introduces 
   *                                        some kind of aproximations
   */
  public void init(
      NodesManager nMan, 
      Double c, 
      Double D_exc, 
      Double D_inh, 
      Double t_arp, 
      Double excitatoryPresynapticDefVal, 
      Double inhibithoryPresynapticDefVal, 
      Double externalPresynapticDefVal, 
      Double avgNeuronalSignalSpeed,
      Boolean lif, 
      Boolean exp_decay, 
      Boolean do_fast,
      Boolean isNOI,
      StatisticsCollector sc){
    this.sc=sc;
    //nft=new NodeFiringsThread(this.sc);
    //nbt=new NodeBurningsThread(this.sc);
    //nft.start();
    //nbt.start();
    queuesMap = new HashMap<Synapse, NiceQueue>();
    internodeFires = new ArrayList<InterNodeSpike>();
    nnMan = new NodeNeuronsManager(n, 
        c, 
        D_exc, 
        D_inh, 
        t_arp, 
        excitatoryPresynapticDefVal, 
        inhibithoryPresynapticDefVal, 
        externalPresynapticDefVal);
    this.plasticity=n.getPlasticity();
    initExtInput();
    synMan = new SynapsesManager(n,avgNeuronalSignalSpeed);
    this.nMan=nMan;
    println("Bn: "+n.getBn());
    println("IBI: "+n.getIBI());
    println("c: "+c);
    println("D exc: "+D_exc);
    println("D inh: "+D_inh);
    println("t arp: "+t_arp);
    this.lif=lif;
    this.exp_decay=exp_decay;
    this.do_fast=do_fast;
    this.isNOI=isNOI;
  }
  
  /**
   *  starts the main thread routine
   */
  public void run(){
    NiceNode minFiringNeuron;
    Long firingNeuronId=null;
    Double spikeTime=null;
    Double tmpMinFiringTime;
    Double tmpMinInterNodeBurningTime;
    Double minFixedBurnTime;
    InterNodeBurningSpike tmpInterNodeBurningSpike;
    FixedBurnSpike tmpFixedBurnSpike;
    Integer lastCollectedBurstFiringNodeId=-1;
    Long lastCollectedBurstFiringNeuronId=-1l;
    Double lastCollectedBurstFiringBurnTime=-1.0;
    int fires=0;
    Boolean stopped=false;  
    //the actual thread routine
    while (keepRunning){
      for (; currentTime<stopTime; ++fires){
        if (!stopped){
          /* check which is the minimum between the
           * next firing time, the next burn due to inter-node
           * spikes and the next burn due to bursting queue
           * of a fire already happened
           */
          tmpMinFiringTime=nnMan.getMinFiringTime();
          tmpInterNodeBurningSpike=interNodeBurningSpikes.peek();
          tmpFixedBurnSpike=burningQueueSpikes.peek();
          minFixedBurnTime=(tmpFixedBurnSpike==null)?
              Double.MAX_VALUE:
                tmpFixedBurnSpike.getBurnTime();
          // case of first arrival of inter-node burn  
          if (tmpInterNodeBurningSpike!=null){
            tmpMinInterNodeBurningTime = 
                tmpInterNodeBurningSpike.getTimeToBurn();
            if ((tmpMinInterNodeBurningTime!=null)&&
                (tmpMinInterNodeBurningTime<stopTime)){
              if (tmpMinFiringTime!=null){
                // inter-node burn processing first
                if ((tmpMinInterNodeBurningTime<tmpMinFiringTime) &&
                    (tmpMinInterNodeBurningTime<minFixedBurnTime)){
                  InterNodeSpike irs=
                      interNodeBurningSpikes.poll().getInterNodeSpike();
                  if (tmpMinInterNodeBurningTime<currentTime) {
                    println("internode burning:"+
                        tmpMinInterNodeBurningTime+
                        " min FixedBurn:"+
                        minFixedBurnTime+
                        " tmpMinFiringTime:"+
                        tmpMinFiringTime);
                    if (interNodeBurningSpikes.peek()!=null)
                      println("polled:"+
                          irs.getBurnTime()+
                          " peeked:"+
                          interNodeBurningSpikes.peek().getTimeToBurn()+
                          "\n");
                  }
                  if ((interNodeBurningSpikes.peek()!=null)&&
                      (irs.getBurnTime()>interNodeBurningSpikes.peek().getTimeToBurn()))
                    println("polled:"+
                        irs.getBurnTime()+
                        " peeked:"+
                        interNodeBurningSpikes.peek().getTimeToBurn()+
                        " syn:"+
                        tmpInterNodeBurningSpike.getInterNodeSpike().getSyn());
                  currentTime=tmpMinInterNodeBurningTime;
                  burnNeuron(
                      irs.getSyn(), 
                      irs.getBurnTime(), 
                      irs.getFireTime(), 
                      false);
                  continue;
                }
              }
              // there is no next node-internal spike, check inter-node 
              // against fixed burn 
              else if (tmpMinInterNodeBurningTime<minFixedBurnTime){
                InterNodeSpike irs=
                    interNodeBurningSpikes.poll().getInterNodeSpike();
                currentTime=tmpMinInterNodeBurningTime;
                burnNeuron(
                    irs.getSyn(), 
                    irs.getBurnTime(), 
                    irs.getFireTime(), 
                    false);
                continue;
              }
            }
          }
          // case of first arrival of bursting queue spike to be burn
          if (minFixedBurnTime<stopTime) {
            if (tmpMinFiringTime!=null){
              if (minFixedBurnTime<tmpMinFiringTime) {
                FixedBurnSpike fixedBurnSpike = burningQueueSpikes.poll();
                if (tmpFixedBurnSpike.getBurnTime()!=
                    fixedBurnSpike.getBurnTime()) {
                  println("tada!:"+
                      tmpFixedBurnSpike.getBurnTime()+
                      "!="+
                      fixedBurnSpike.getBurnTime());
                  System.exit(1);
                }
                currentTime=minFixedBurnTime;
                burnNeuron(
                    fixedBurnSpike.getSyn(),
                    fixedBurnSpike.getBurnTime(),
                    minFixedBurnTime,
                    false);
                if(
                    (! lastCollectedBurstFiringNodeId.equals(n.getId()))||
                    (! lastCollectedBurstFiringNeuronId.equals(
                        fixedBurnSpike.getSyn().getAxonNeuronId()))||
                    (! lastCollectedBurstFiringBurnTime.equals(
                        fixedBurnSpike.getBurnTime()))
                ){
                  lastCollectedBurstFiringNodeId=n.getId();
                  lastCollectedBurstFiringNeuronId=
                      fixedBurnSpike.getSyn().getAxonNeuronId();
                  lastCollectedBurstFiringBurnTime=
                      fixedBurnSpike.getBurnTime();
                  if(isNOI) 
                    sc.collectFireSpike(
                        n.getId(), 
                        fixedBurnSpike.getSyn().getAxonNeuronId(),
                        fixedBurnSpike.getBurnTime(), 
                        nMan.getMaxN(), 
                        nMan.getCompressionFactor(),
                        (fixedBurnSpike.getSyn().getAxonNeuronId()<n.getExcitatory()),
                        (fixedBurnSpike.getSyn().getAxonNeuronId()>=n.getN()) );
                }
                continue;
              }
            }
            else if (minFixedBurnTime<Double.MAX_VALUE) {
              FixedBurnSpike fixedBurnSpike = burningQueueSpikes.poll();
              currentTime=minFixedBurnTime;
              burnNeuron(
                  fixedBurnSpike.getSyn(),
                  fixedBurnSpike.getBurnTime(),
                  fixedBurnSpike.getFireTime(),
                  false);
              if(
                  (! lastCollectedBurstFiringNodeId.equals(n.getId()))||
                  (! lastCollectedBurstFiringNeuronId.equals(
                      fixedBurnSpike.getSyn().getAxonNeuronId()))||
                  (! lastCollectedBurstFiringBurnTime.equals(
                      fixedBurnSpike.getBurnTime()))
              ){
                lastCollectedBurstFiringNodeId=n.getId();
                lastCollectedBurstFiringNeuronId=
                    fixedBurnSpike.getSyn().getAxonNeuronId();
                lastCollectedBurstFiringBurnTime=
                    fixedBurnSpike.getBurnTime();
                if(isNOI) 
                  sc.collectFireSpike(
                      n.getId(), 
                      fixedBurnSpike.getSyn().getAxonNeuronId(),
                      fixedBurnSpike.getBurnTime(), 
                      nMan.getMaxN(), 
                      nMan.getCompressionFactor(),
                      (fixedBurnSpike.getSyn().getAxonNeuronId()<n.getExcitatory()),
                      (fixedBurnSpike.getSyn().getAxonNeuronId()>=n.getN()) );
              }
              continue;
            }
            else {
              stopped=true;
              break;
            }
          }
          if ((tmpMinFiringTime==null)||(tmpMinFiringTime>stopTime)){
            stopped=true;
            break;
          }
          //get the next neuron ready to fire in the list of 
          //the active neurons
          //debprintln("\n\ngetting next firing neuron...");
          minFiringNeuron=nnMan.getNextFiringNeuron();
          if (minFiringNeuron==null){
            stopped=true;
            break;
          }
          //debprintln("got next firing neuron.");
          firingNeuronId=minFiringNeuron.fn;
          spikeTime=minFiringNeuron.tf;
          if (spikeTime>stopTime){
            stopped=true;
            break;
          }
        }
        else{
          break;
        }
        //case of first firing of a burst
        //time update
        currentTime = spikeTime;
        //firing spikes detecting and storing
        if (firingNeuronId<n.getN()){
          //State resetting to passive mode
          nnMan.resetState(firingNeuronId);
          nnMan.resetTimeToFire(firingNeuronId);
        }
        // last firing time for neuron
        nnMan.setLastFiringTime(firingNeuronId, currentTime);
        if (firingNeuronId>=n.getN()){
          //ext time-to-fire resetting
          nnMan.resetTimeToFire(firingNeuronId);
          //external routine
          nnMan.extInputReset(firingNeuronId, currentTime);
          if (currentTime>n.getExternalInput().getFireDuration())
            continue;
        }
        if(isNOI) 
          sc.collectFireSpike(
              n.getId(), 
              firingNeuronId, 
              spikeTime, 
              nMan.getMaxN(), 
              nMan.getCompressionFactor(),
              (firingNeuronId<n.getExcitatory()),
              (firingNeuronId>=n.getN()) );
        //search for burning neurons connected to neuron_id
        makeNeuronFire(firingNeuronId, currentTime);
      }
      completed();
      stopped=false;
    }
  }
  
  /**
   *  Adds a new internode fire spike
   *  @param syn      the inter-node synapse object through wich the 
   *                  spike signal is sent
   *  @param fireTime the fire-spike generation time
   */
  private void addInterNodeFire(Synapse syn, Double fireTime){
    Double axonalDelay=synMan.getAxDelay(syn);
    internodeFires.add(new InterNodeSpike(syn, fireTime+axonalDelay,fireTime,axonalDelay));
  }
  
  /**
   *  @return true if this node has still any inter-node spike to be 
   *          processed
   */
  public Boolean hasInterNodeSpikes(){
    if (internodeFires.size()>0)
      return true;
    return false;
  }
  
  
  /**
   * @return the list of internode spikes and clean it
   */
  public ArrayList<InterNodeSpike> pullInternodeFires() {
    ArrayList<InterNodeSpike> retval=internodeFires;
    internodeFires=new ArrayList<InterNodeSpike>();
    return retval;
  }
  
  /*
   * @return the current simulation time (which is the time of the 
   *        current fire event being processed)
   */
  public double getCurrentTime() {
    return currentTime;
  }

  /**
   * Updates the current time
   * @param currentTime the updated current time
   */
  public void setCurrentTime(double currentTime) {
    this.currentTime = currentTime;
  }
  
  /**
   * @return the current split stop time
   */
  public double getStopTime() {
    return stopTime;
  }

  /**
   * set the next split stop time
   * @param stopTime the new stop time defined for the next split
   */
  public void setStopTime(double stopTime) {
    this.stopTime = stopTime;
  }

  /**
   * @return the (unique) node id for the current node
   */
  public Integer getNodeId(){
    return n.getId();
  }
  
  /**
   * create and adds a new inter-node synapse
   * @param firingNodeId     the id of the firing node
   * @param firingNeuronId   the id of the firing neuron (within a node)
   * @param burningNodeId    the id of the burning node
   * @param burningNeuronId  the id of the burning neuron (within a node) 
   * @param presynaptic_w    the presynaptic weight of the synapse
   * @param mu               the postsynaptic weight for the synapse
   * @param lambda           the avg length of the inter-node axon
   */
  public void addInterNodeSynapse(
      Integer firingNodeId, 
      Long firingNeuronId, 
      Integer burningNodeId, 
      Long burningNeuronId,
      Double presynaptic_w,
      Double mu,
      Double lambda) {
    synMan.addInterNodeSynapse(
        firingNodeId, 
        firingNeuronId, 
        burningNodeId, 
        burningNeuronId,
        mu,
        n.getPresynapticForNeuron(firingNeuronId),
        lambda);
  }
  
  public Long getN(){
    return n.getN();
  }
  
  public Long getExcitatory(){
    return n.getExcitatory();
  }
  
  public Long getInhibithory(){
    return n.getInhibithory();
  }
  
  public Double getExcitatoryPresynapticWeight() {
    return n.getExc_ampl();
  }

  public boolean hasExternalInput() {
    return n.hasExternalInput();
  }
  
  public Integer getExternalInputs(){
    return n.getExternalInputs();
  }
  
  /**
   * Plasticity Rule.
   * Multiplicative Learning Rule using STDP (soft-bound) Spike time 
   * depending plasticity
   * 
   *  LTP: Pw = Pwold + (pwmax - Pwold)*Etap*(-delta/taup)
   *  LTD: Pw = Pwold - Pwold*Etam*(delta/taum)
   *  with delta = tpost - tpre
   *
   *  NB: in the case of LTD, tpost represents the burning neuron last burning 
   *  time, whereas tpre is the current "tempo".
   *  This rule is applied for only exc-exc intermolule connections   
   *
   * @param spikingNeuronId
   * @param presentNeuronId
   * @param currentTime
   */
  
  
  /**
   * Plasticity rule for firing events.
   * Update postsynaptic weight, increasing it according to the delta i
   * between the firing time 
   * and the last burning time of the firing neuron.
   * 
   * @param syn
   * @param fireTime
   */
  private void fire_ltp(Synapse syn, Double fireTime){
    if (!plasticity)
      return;
    if (syn.getFiring()==null)
      return;
    Long firingNeuronId = syn.getFiring();
    ArrayList <Synapse> synapses = 
        synMan.getFiringNeuronSynapses(firingNeuronId);
    ArrayList <Synapse> interNodeSynapses = 
        synMan.getFiringNeuronInterNodesSynapses(firingNeuronId);
    for(int i=0; i<synapses.size();++i){
      if (synapses.get(i).getLastBurningTime()==null)
        continue;
      Double delta;
      delta=fireTime-synapses.get(i).getLastBurningTime();
      if (delta < n.getPlasticityTo()){
        Double wp=synapses.get(i).getPostSynapticWeight();
        double wpold=wp;
        wp+=do_fast?
            (n.getPwMax()-wp)*n.getEtap()*fm.fastexp(-delta/n.getTaup()):
              (n.getPwMax()-wp)*n.getEtap()*Math.exp(-delta/n.getTaup());
        synMan.setIntraNodePostSynapticWeight(synapses.get(i),wp);
      }
    }
    for(int i=0; i<interNodeSynapses.size();++i){
      if (interNodeSynapses.get(i).getLastBurningTime()==null)
        continue;
      Double delta;
      delta=fireTime-interNodeSynapses.get(i).getLastBurningTime();
      if (delta < n.getPlasticityTo()){
        Double wp=interNodeSynapses.get(i).getPostSynapticWeight();
        double wpold=wp;
        wp+=do_fast?
            (n.getPwMax()-wp)*n.getEtap()*fm.fastexp(-delta/n.getTaup()):
              (n.getPwMax()-wp)*n.getEtap()*Math.exp(-delta/n.getTaup());
        synMan.setIntraNodePostSynapticWeight(
            interNodeSynapses.get(i),
            wp);
      }
    }
  }

  /**
   * Plasticity rule for burning events.
   * Update postsynaptic weight, decreasing it according to 
   * the delta between the burning time 
   * and the last firing time of the burning neuron.
   * @param syn
   * @param lastBurningTime
   * @param fireTime
   */
  private void burning_ltd(
      Synapse syn, 
      Double burningTime, 
      Double lastFiringTime){
    if (!plasticity)
      return; 
    if (syn.getBurning()==null)
      return;
    syn.setLastBurningTime(burningTime);
    Double delta;
    delta=burningTime-lastFiringTime;
    if (delta < n.getPlasticityTo()){
      Double wp=syn.getPostSynapticWeight();
      double wpold=wp;
      wp -= do_fast?
          wp*n.getEtam()*fm.fastexp(-delta/n.getTaum()):
           wp*n.getEtam()*Math.exp(-delta/n.getTaum());
      if (wp<0)
        wp=0.0;
      synMan.setIntraNodePostSynapticWeight(syn,wp);
    }
  }
  
  
  /**
   * Elicit a spike from the firing neuron to each connected burning.
   * 
   * @param firingNeuronId
   * @param currentTime
   */
  private void makeNeuronFire(Long firingNeuronId, Double currentTime){
    ArrayList <Synapse> synapses = 
        synMan.getFiringNeuronSynapses(firingNeuronId);
    ArrayList <Synapse> interNodeSynapses = 
        synMan.getFiringNeuronInterNodesSynapses(firingNeuronId);
    if (n.isExternalInput(firingNeuronId)){
      int eod=n.getExternalOutDegree();
      int eoj=n.getExternalOutJump();
      if (eod==1){
        burnNeuron(
            null,
            firingNeuronId, 
            n.getId(), 
            firingNeuronId%n.getN(), 
            n.getId(), 
            0.1,
            1.0,
            n.getExternalAmplitude(),
            currentTime, 
            currentTime, 
            true);
      }
      else
        for (int i=0; i<eod; ++i){
          burnNeuron(
              null,
              firingNeuronId, 
              n.getId(), 
              (firingNeuronId+(eoj*i))%n.getN(), 
              n.getId(), 
              0.1,
              1.0,
              n.getExternalAmplitude(),
              currentTime, 
              currentTime, 
              true);
        }
      return;
    }
    for (int i=0; i<synapses.size();++i){
      fire_ltp(
          synapses.get(i), 
          currentTime);
      // this is an inter-node synapse, the burning node 
      // must deal with this spike
      if (!(synapses.get(i).getBurningNodeId().equals(n.getId()))){
        continue;
      }
      burnNeuron(synapses.get(i), currentTime, currentTime, false);
      for (int j=1; j<n.getBn(); ++j)
        burningQueueSpikes.add(
            new FixedBurnSpike(synapses.get(i), 
                (currentTime+n.getIBI()*j), 
                currentTime));
    }
    for (int i=0; i<interNodeSynapses.size();++i){
      for (int j=0; j<n.getBn(); ++j)
        addInterNodeFire(interNodeSynapses.get(i), (currentTime+n.getIBI()*j));
    }
  }
  
  public void burnNeuron(
        Synapse s, 
        Double burnTime, 
        Double fireTime, 
        Boolean fromExternalInput){
    burnNeuron(
        s,
        s.getFiring(),
        s.getFiringNodeId(),
        s.getBurning(),
        s.getBurningNodeId(),
        s.getLength(),
        s.getPostSynapticWeight(),
        s.getPreSynapticWeight(),
        burnTime,
        fireTime,
        fromExternalInput);
  }


  public void burnNeuron(
      Synapse s, 
      long firingNeuronId,
      int firingNodeId,
      long burningNeuronId,
      int burningNodeId,
      double axon_length,
      double postsynapticWeight,
      double presynapticWeight,
      double burnTime, 
      double fireTime, 
      boolean fromExternalInput){
    double tmp, dsxNumerator, dsxDenominator, riseTermXFactor, oldSx;
    int arp;
    //distinguish cases of no initial network activity : already activated
    arp=(nnMan.getLastFiringTime(burningNeuronId).equals(Constants.TIME_TO_FIRE_DEF_VAL))?0:1;
    //absolutely refractory period check
    if (burnTime>=(( 
        nnMan.getLastFiringTime(burningNeuronId)+
        nnMan.getT_arp()+
        ((n.getBn()-1)*n.getIBI()))
        *arp) ){
      long startTime = System.currentTimeMillis();
      if (!fromExternalInput)
        burning_ltd(
            s, 
            burnTime, 
            nnMan.getLastFiringTime(burningNeuronId));
      tmp=nnMan.getState(burningNeuronId);
      //passive state linear decay
      if (tmp<nnMan.getSpikingThr()){
        Double decay;
        //linear decay
        if (!exp_decay){
            decay = (
                nnMan.getLinearDecayD(burningNeuronId)*
                (burnTime-
                        (nnMan.getLastBurningTime(
                                burningNeuronId))));
                nnMan.setState(
                        burningNeuronId, 
                        tmp-decay);
        }
        //exponential decay
        //Sj = Spj + A * W -Tl =  A W + Spj e^(-delta t / D)
        //Tl  =  Spj (1 - e^(-delta t / D))
        //if (exp_decay){
        else{
            decay = do_fast? (
                tmp * (
                    1 - fm.fastexp(
                        - ( burnTime -
                            nnMan.getLastBurningTime(burningNeuronId)
                        )/
                        nnMan.getLinearDecayD(burningNeuronId)))
            ):(
                tmp * (
                    1 - Math.exp(
                        -(burnTime-
                            nnMan.getLastBurningTime(burningNeuronId)
                        )/
                        nnMan.getLinearDecayD(burningNeuronId)))
            );
            nnMan.setState(
                burningNeuronId, 
                tmp-decay);
        }
        if (nnMan.getState(burningNeuronId)<0.0)
          nnMan.setState(burningNeuronId, 0.0);
      }
      times[0]+=System.currentTimeMillis()-startTime;
      startTime = System.currentTimeMillis();
      
      //BURNING NEURON
      double sx = nnMan.getState(burningNeuronId);
      oldSx=sx;
      //step in state
      double sy = postsynapticWeight*presynapticWeight;
      // UPDATING List of Active Neurons
      // case of passive neuron
      if (nnMan.getTimeToFire(burningNeuronId)
          .equals(Constants.TIME_TO_FIRE_DEF_VAL)){
        oldSx=sx;
        sx = ((sx+sy)<0)?0:sx+sy;
        nnMan.setState(burningNeuronId, sx);
        //passive to active
        if (sx>=nnMan.getSpikingThr()){
          //nnMan.setTimeToFire(s.getBurning(), burnTime+ 1.0/(sx-1));
          if (lif)
            nnMan.setTimeToFire(
                burningNeuronId, 
                burnTime+Constants.EPSILON);
          else{
            double activeTransitionDelay=(1.0/(sx-1));
            nnMan.setTimeToFire(
                burningNeuronId, 
                burnTime+ activeTransitionDelay);
          }
          //if(isNOI) 
          //  sc.collectPassive2active();
          nnMan.addActiveNeuron(
              burningNeuronId, 
              nnMan.getTimeToFire(burningNeuronId), 
              currentTime, 
              2);
        }
        //else{
        //  if(isNOI) 
        //    sc.collectPassive();
        //}
        times[1]+=System.currentTimeMillis()-startTime;
      }
      // case of active neuron
      // avoid update on lif
      else if (!lif){
      //else {
        if (nnMan.getTimeToFire(burningNeuronId)==0.0)
          nnMan.setTimeToFire(burningNeuronId, Constants.EPSILON);
        if (sx>=nnMan.getSpikingThr()){
          nnMan.removeActiveNeuron(burningNeuronId);
          if ( (burnTime < nnMan.getTimeToFire(burningNeuronId) )
              && (!nnMan.getLastBurningTime(burningNeuronId)
                  .equals(Constants.BURNING_TIME_DEF_VAL)) ){
            //Rise Term
            riseTermXFactor=
                (burnTime==nnMan.getLastBurningTime(burningNeuronId))?
                    Constants.EPSILON : (
                        burnTime-nnMan.getLastBurningTime(burningNeuronId));
            tmp=(sx-1)*riseTermXFactor;
            dsxNumerator = (sx-1)*tmp;
            dsxDenominator= 1.0-tmp;
            sx+=(dsxNumerator/dsxDenominator);
          }
          oldSx=sx;
          sx += sy;
          nnMan.setState(burningNeuronId,sx);
          //active to passive
          if (sx<nnMan.getSpikingThr()){
            nnMan.removeActiveNeuron(burningNeuronId);
            nnMan.resetTimeToFire(burningNeuronId);
            //if (isNOI)
            //  sc.collectActive2passive();
          }
          else{
            //updating firing delay
            nnMan.setTimeToFire(burningNeuronId, burnTime + 1.0/(sx-1));
            nnMan.setState(burningNeuronId, sx);
            nnMan.addActiveNeuron(
                burningNeuronId, 
                nnMan.getTimeToFire(burningNeuronId), 
                currentTime, 
                3);
             //if(isNOI) 
             // sc.collectActive();
          }
          //active to passive
          if (sx<0){
            sx=0.0;
            oldSx=sx;
            nnMan.setState(burningNeuronId,sx);
            nnMan.removeActiveNeuron(burningNeuronId);
            nnMan.resetTimeToFire(burningNeuronId);
            //if(isNOI) 
            //  sc.collectActive2passive();
          }
        }
        else{
          oldSx=sx;
          nnMan.removeActiveNeuron(burningNeuronId);
          nnMan.resetTimeToFire(burningNeuronId);
          //if(isNOI) 
          //  sc.collectActive2passive();
        }
        times[2]+=System.currentTimeMillis()-startTime;
      }
      //end of case of active neuron
      startTime = System.currentTimeMillis();
      nnMan.setLastBurningTime(burningNeuronId, burnTime);
      times[4]+=System.currentTimeMillis()-startTime;
      // collecting the spike
      if(isNOI) 
        sc.collectBurnSpike(
            firingNeuronId,
            firingNodeId,
            burningNeuronId,
            burningNodeId,
            burnTime,
            fromExternalInput, 
            oldSx, 
            sy, 
            postsynapticWeight,
            presynapticWeight,
            nnMan.getTimeToFire(burningNeuronId),
            fireTime);
      times[3]+=System.currentTimeMillis()-startTime;
    }
    else{
      // collecting the spike
      if(isNOI) 
        sc.collectBurnSpike(
            firingNeuronId,
            firingNodeId,
            burningNeuronId,
            burningNodeId,
            burnTime,
            fromExternalInput, 
            null, 
            null, 
            postsynapticWeight,
            presynapticWeight,
            nnMan.getTimeToFire(burningNeuronId),
            fireTime);
    }
  }
  
  
  public void burnInterNodeSpike(InterNodeSpike irs){
    // the comparison is done against the stoptime, since when this method is called 
    // it still holds the value for the last split run (not the current one) and
    // current time may hold a very old value.
    if (irs.getBurnTime()>=stopTime)
      interNodeBurningSpikes.add(new InterNodeBurningSpike(irs, irs.getBurnTime()));
    else{
      sc.collectMissedFire(irs.getAxonalDelay());
      println("missed fire at time:"+
            irs.getBurnTime()+
            ", axonal del:"+
            irs.getAxonalDelay()+
            ", fire time:"+
            irs.getFireTime()+
            ", current time:"+
            currentTime+
            ", syn:"+
            irs.getSyn());
    }
  }
  
  private void initExtInput(){
    println("initializing external input...");
    for (int j=0; j<n.getExternalInputs();++j){
      nnMan.extInputReset(n.getN()+j,0.0);
    }
    println("external input initialization done.");
  }
  
  
  //=======================================  thread functions  =======================================
  
  public void runNewSplit(double newStopTime){
    /* 
     * current time is updated with the last value for stoptime, ccause otherwise it can hold
     * value belonging to a past split and then could generate inconsistency against
     * spikes coming frmo external nodes
     */
    startTime=stopTime;
    if (currentTime<stopTime)
      currentTime=stopTime;
    stopTime = newStopTime;
    lock.lock();
    newTimeSplitCond.signal();
    lock.unlock();
  }

  private void completed(){
    double oldStopTime=stopTime;
    print_times();
    if (nMan.splitComplete(getNodeId())){
      lock.lock();
      try {
        newTimeSplitCond.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      lock.unlock();
    }
    else{
      while (keepRunning && (stopTime<=oldStopTime) ){
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void kill(){
    keepRunning=false;
    lock.lock();
    newTimeSplitCond.signal();
    lock.unlock();
  }
  
  
  
  
  //=======================================  printing function =======================================
  
  
  public void printQueues(){
    Iterator<Synapse> it = queuesMap.keySet().iterator();
    println("Printing queues:");
    while (it.hasNext()){
      Synapse s = it.next();
      System.out.println(s + ": "); 
      ((NiceQueue)queuesMap.get(s)).printQueue();
          
    }
    System.out.println();
  }
  
  private void println(String s){
    if (verbose){
      if (nMan != null)
        System.out.println(TAG+getNodeId()+"/"+(nMan.getNodeThreadsNum()-1)+"] "+s);
      else 
        System.out.println(TAG+getNodeId()+"/-] "+s);
    }
  }
  
  private void debprintln(String s){
    if (verbose&&debug)
      System.out.println(TAG+getNodeId()+"/"+(nMan.getNodeThreadsNum()-1)+" [debug] ] "+s);
  }
  
  private void leveldebprintln(String s, int level){
    if (this.debug_level>=level)
     debprintln(s);
  }
  
  private void debActiveprintln(String s){
    if (verbose&&debug&&activePassiveDebug)
      System.out.println(TAG+getNodeId()+"/"+(nMan.getNodeThreadsNum()-1)+"[debug] ] "+s);
  }

  public void printDebug(){
    println("max pw approximation:"+debugMaxWPDiff);
    System.out.println();
  }
  
  public void print_times() {
    return;
//    for (int i=0; i<5;++i)
//      println("times["+i+"]: "+times[i]);
  }

  
  
  
  
  


}

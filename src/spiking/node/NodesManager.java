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

package spiking.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import spiking.controllers.node.NodeThread;
import spiking.internode.InterNodeSpike;
import spiking.simulator.SpikingNeuralSimulator;
import utils.exceptions.BadCurveException;
import utils.statistics.StatisticsCollector;
import utils.tools.IntegerCouple;
import org.apache.commons.math3.distribution.GammaDistribution;

public class NodesManager implements Serializable {
  
  public static final Double MAX_TRACT_LENGTH=100000.0;
  private final static long serialVersionUID = -4781040115396333609L;
  private final static String TAG = "[Nodes Manager] ";
  private final static Boolean verbose = true;
  private final static Integer goodCurveGuessThreshold= 5;
  private final static Integer badCurveGuessThreshold= 15;
  //private StatisticsCollector sc;
  private Boolean debug = false;
  //ergion threads array list 
  private ArrayList<NodeThread> nodeThreads = new ArrayList<NodeThread>();
  //internodes connection probability
  private HashMap<IntegerCouple, NodesInterconnection> nodesConnections = 
      new HashMap<IntegerCouple, NodesInterconnection>();
  //the total number of neuron
  private Long n=0l;
  //the maximum number of neurons within a single node
  private Long maxN=0l;
  private double compressionFactor=1.0;
  //the total number of excitatory neuron
  private Long excitatory=0l;
  //the total number of inhibithory neuron
  private Long inhibithory=0l;
  //the total number of external inputs
  private Integer externalInputs=0;
  //the HasMap of small worlds with external inputs
  private ArrayList<Integer> regsWithExternalInputs = new ArrayList<Integer>();
  //the total number of inter-node connections
  private Long inter_node_conns_num=0l;
  //is set, no more node addition are allowed
  private Boolean initialized=false;
  private SpikingNeuralSimulator sim;
  private Double minTractLength=MAX_TRACT_LENGTH;
  private Random randGen = new Random(System.currentTimeMillis());
  //setting default to 1 means every x
  private Double gammaInverseCumulativeProbX = 1.0;
  private Double bop_conservative_p = null;
  
  
  public NodesManager(
      SpikingNeuralSimulator sim, 
      //StatisticsCollector sc,
      Double bop_conservative_p){
    this.sim=sim;
    //this.sc=sc;
    this.bop_conservative_p = bop_conservative_p;
  }

  //public StatisticsCollector getStatisticsCollector() {
  //  return sc;
  //}
  
  public void addNodeThread(NodeThread regT){
    if (initialized)
      return;
    nodeThreads.add(regT);
    n+=regT.getN();
    //updating the maximum number of neuron within a same node
    if (regT.getN()>maxN)
      maxN=regT.getN();
    println("adding node:"+regT.getNodeId()+", n:"+regT.getN()+"\n");
    excitatory+=regT.getExcitatory();
    inhibithory+=regT.getInhibithory();
    if (regT.hasExternalInput()){
      externalInputs+=regT.getExternalInputs();
      regsWithExternalInputs.add(regT.getNodeId());
    }
  }  
  
  public void addInterNodeConnection(
      NodeThread node1, 
      NodeThread node2, 
      Double Ne_xn_ratio, 
      Double mu_omega, 
      Double sigma_omega, 
      Double mu_lambda, 
      Double alpha_lambda,
      Integer inter_node_conn_type){
    if (mu_lambda==null)
      println("length null 1...");
    NodesInterconnection n_conn = new NodesInterconnection(
        node1, 
        node2, 
        Ne_xn_ratio,
        mu_omega,
        sigma_omega,
        mu_lambda,
        alpha_lambda,
        inter_node_conn_type);
    nodesConnections.put(
        new IntegerCouple(node1.getNodeId(), 
        node2.getNodeId()), 
        n_conn);
    _addInterNodeConnection(
        node1.getNodeId(),
        node2.getNodeId(),
        Ne_xn_ratio,
        mu_omega,
        sigma_omega, 
        mu_lambda, 
        alpha_lambda,
        inter_node_conn_type);
  }
  
  public void addInterNodeConnectionParameters(
      Integer reg1Id, 
      Integer reg2Id, 
      Double weight, 
      Double amplitude, 
      Double amplitudeStdVariation, 
      Double length, 
      Double lengthShapeParameter,
      Integer inter_node_conn_type){
    if (length==null)
      println("length null 2...");
    NodesInterconnection regi = new NodesInterconnection(reg1Id, reg2Id, weight);
    regi.setLength(length);
//    Integer src =  (reg1Id<reg2Id)? reg1Id: reg2Id;
//    Integer dst =  (reg1Id<reg2Id)? reg2Id: reg1Id;
    nodesConnections.put(new IntegerCouple(reg1Id, reg2Id), regi);
    _addInterNodeConnection(
        reg1Id, 
        reg2Id, 
        weight, 
        amplitude, 
        amplitudeStdVariation, 
        length, 
        lengthShapeParameter,
        inter_node_conn_type);
  }
  
  private void _addInterNodeConnection(
      Integer node1id, 
      Integer node2id, 
      Double weight, 
      Double amplitude, 
      Double amplitudeStdVariation, 
      Double length, 
      Double lengthShapeParameter,
      Integer inter_node_conn_type){
    if (length==null)
      println("length null...");
    if (minTractLength==null)
      println("min tract length null...");
    if (length<minTractLength)
      minTractLength=length;
    try {
      __addInterNodeConnection(
          node1id, 
          node2id, 
          weight,
          amplitude,
          amplitudeStdVariation,
          length, 
          lengthShapeParameter,
          inter_node_conn_type);
    } catch (BadCurveException e) {
      e.printStackTrace();
    }
  }
  
  
  /**
   * adds an inter node connection using the weight as the number of connections
   * between neurons of the two nodes
   * @throws BadCurveException 
   */
  private void __addInterNodeConnection(
      Integer reg1Id, 
      Integer reg2Id, 
      Double Ne_xn_ratio, 
      Double mu_omega, 
      Double sigma_omega, 
      Double mu_lambda, 
      Double alpha_lambda,
      Integer inter_node_conn_type) throws BadCurveException{
    /*
     * The schema for internode connections
     * 
     *      \     |       |       |       |
     *       \ to |  mixed |   exc  |  inh  |
     *  from  \   |       |       |       |
     * ------------------------------------
     *    mixed   |   0   |   1   |   2   |
     * ------------------------------------
     *     exc    |   3   |   4   |   5   |
     * ------------------------------------
     *     inh    |   6   |   7   |   8   |
     * ------------------------------------
     *  
     */
        long Nsrc = 0;
        // case EXC2*
        if ((inter_node_conn_type==NodesInterconnection.EXC2MIXED)||
                (inter_node_conn_type==NodesInterconnection.EXC2EXC)||
                (inter_node_conn_type==NodesInterconnection.EXC2INH)) {
            Nsrc = nodeThreads.get(reg1Id).getExcitatory();
        }
        // case INH2*
        else if ((inter_node_conn_type==NodesInterconnection.INH2MIXED)||
                (inter_node_conn_type==NodesInterconnection.INH2EXC)||
                (inter_node_conn_type==NodesInterconnection.INH2INH)) {
            Nsrc = nodeThreads.get(reg1Id).getInhibithory();
        }
        // case MIXED2*
        else
            Nsrc = nodeThreads.get(reg1Id).getN();
    long tmp = (long)(Nsrc*Ne_xn_ratio);
    Long i1, i2;
    GammaDistribution gd = (alpha_lambda!=null)?
        //new GammaDistribution(alpha_lambda, 1.0/mu_lambda)
        new GammaDistribution(alpha_lambda, mu_lambda/alpha_lambda)
        :null;
    gammaInverseCumulativeProbX = 
            ((alpha_lambda!=null)&&(bop_conservative_p != null))? 
            gd.inverseCumulativeProbability(1.0 - bop_conservative_p ):
            1.0;
    for (long i=0; i<tmp;++i){
      // case EXC2*
      if ((inter_node_conn_type==NodesInterconnection.EXC2MIXED)||
          (inter_node_conn_type==NodesInterconnection.EXC2EXC)||
          (inter_node_conn_type==NodesInterconnection.EXC2INH)) {
        i1 = (long)(Math.random() * nodeThreads.get(reg1Id).getExcitatory());
      }
      // case INH2*
      else if ((inter_node_conn_type==NodesInterconnection.INH2MIXED)||
          (inter_node_conn_type==NodesInterconnection.INH2EXC)||
          (inter_node_conn_type==NodesInterconnection.INH2INH)) {
        i1 = nodeThreads.get(reg1Id).getExcitatory()+
            ((long)(Math.random() * nodeThreads.get(reg1Id).getInhibithory()));
      }
      // case MIXED2*
      else
        i1 = (long)(Math.random() * nodeThreads.get(reg1Id).getN());
      // case *2EXC
      if ((inter_node_conn_type==NodesInterconnection.MIXED2EXC)||
          (inter_node_conn_type==NodesInterconnection.EXC2EXC)||
          (inter_node_conn_type==NodesInterconnection.INH2EXC)) {
        i2 = (long)(Math.random() * nodeThreads.get(reg2Id).getExcitatory());
      }
      // case *2INH
      else if ((inter_node_conn_type==NodesInterconnection.MIXED2INH)||
          (inter_node_conn_type==NodesInterconnection.EXC2INH)||
          (inter_node_conn_type==NodesInterconnection.INH2INH)) {
        i2 = nodeThreads.get(reg2Id).getExcitatory()+
            ((long)(Math.random() * nodeThreads.get(reg2Id).getInhibithory()));
      }
      // case *2MIXED
      else
        i2 = (long)(Math.random() * nodeThreads.get(reg2Id).getN());
      Double tmp_mu_w, tmpLength=-1.0;
      tmp_mu_w = (sigma_omega!=null)?
          Math.abs(randGen.nextGaussian()*sigma_omega+mu_omega)
          :mu_omega;
      if (mu_omega<0 && tmp_mu_w >0)
        tmp_mu_w=-tmp_mu_w;
      int goodCurveGuess=0;
      while ((tmpLength<0)&&(goodCurveGuess<badCurveGuessThreshold)){
        tmpLength = (gd!=null)?  gd.sample() : mu_lambda;
        ++goodCurveGuess;
      }
      if (goodCurveGuess>=goodCurveGuessThreshold){
        sim.setBadCurve();
        if (goodCurveGuess>=badCurveGuessThreshold)
          throw new BadCurveException("the gamma curve G("+alpha_lambda+", "+ 
              (alpha_lambda/mu_lambda)+" has a shape which is not compliant with firnet scope.");
      }
      nodeThreads.get(reg1Id).addInterNodeSynapse(
          reg1Id, 
          i1, 
          reg2Id, 
          i2,
          nodeThreads.get(reg1Id).getExcitatoryPresynapticWeight(),
          tmp_mu_w,
          tmpLength);
      nodeThreads.get(reg2Id).addInterNodeSynapse(
          reg1Id, 
          i1, 
          reg2Id, 
          i2,
          nodeThreads.get(reg1Id).getExcitatoryPresynapticWeight(),
          tmp_mu_w,
          tmpLength);
      ++ inter_node_conns_num;
    }
  }
  
  //public NodesInterconnection _getInterworldConnectionProb(Node reg1, Node reg2){
  //  Integer src=  (reg1.getId()<reg2.getId())? reg1.getId(): reg2.getId();
  //  Integer dst=  (reg1.getId()<reg2.getId())? reg2.getId(): reg1.getId();
  //  return nodesConnections.get(new IntegerCouple(src, dst));
  //}
  
  public Long getTotalN(){
    return n;
  }
  
  
  public double getCompressionFactor() {
    return compressionFactor;
  }

  public void setCompressionFactor(double compressionFactor) {
    this.compressionFactor = compressionFactor;
  }

  public int getNodeThreadsNum(){
    return nodeThreads.size();
  }

  public Boolean getInitialized() {
    return initialized;
  }
  
  public Double getMinTractLength(){
    //return minTractLength;
    return (gammaInverseCumulativeProbX == 1.0)?
        minTractLength:
        gammaInverseCumulativeProbX;
  }

  public Double getGammaInverseCumulativeProbX(){
    return gammaInverseCumulativeProbX;
  }

  public Integer getnSms() {
    return nodeThreads.size();
  }
  
  public NodeThread getNodeThread(int index){
    return nodeThreads.get(index);
  }
  
  /**
   * @return the maximum number of neuron within a single node
   */
  public long getMaxN(){
    return maxN;
  }
  
  /**
   * @ return the total number of inter-node connections
   */
  public Long getTotalInterNodeConnectionsNumber(){
    return inter_node_conns_num;
  }
    
  public void setDebug(Boolean debug){
    this.debug=debug;
  }
  
  private void println(String s){
    if (verbose)
      System.out.println(TAG+s);
  }
  
  private void debprintln(String s){
    if (verbose&&debug)
      System.out.println(TAG+"[debug] "+s);
  }
  
  public void printNodeFields(){
    println("n:\t\t"+n);
    println("excitatory:\t"+excitatory);
    println("inhibithory:\t"+inhibithory);
    println("external inputs:\t"+ externalInputs);
  }


  public void startAll() {
    for (int i=0; i<nodeThreads.size();++i)
      nodeThreads.get(i).start();
  }
  

  public void runNewSplit(double newStopTime) {
    updateInterNodeSpikes();
    for (int i=0; i<nodeThreads.size();++i)
      nodeThreads.get(i).runNewSplit(newStopTime);    
  } 
  
  public void killAll() {
    for (int i=0; i<nodeThreads.size();++i)
      nodeThreads.get(i).kill();
  }
  
  public synchronized Boolean splitComplete(Integer nodeId){
    return sim.splitComplete(nodeId);
  }
  
  private void updateInterNodeSpikes(){
    for (int i=0; i<nodeThreads.size();++i){
      ArrayList <InterNodeSpike> tmpInterNodeSpikes=nodeThreads.get(i).pullInternodeFires();
      for (int j=0; j<tmpInterNodeSpikes.size();++j)
        deliverInterNodeSpike( tmpInterNodeSpikes.get(j));
    }
  }
  
  private void deliverInterNodeSpike(InterNodeSpike irs){
    nodeThreads.get(irs.getSyn().getBurningNodeId()).burnInterNodeSpike(irs);
  }

  

  
}

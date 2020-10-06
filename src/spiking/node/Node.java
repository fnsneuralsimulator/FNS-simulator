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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import utils.experiment.Experiment;
import utils.tools.LongCouple;
import utils.tools.NiceNode;
import utils.tools.Shuffler;
import spiking.node.external_inputs.ExternalInput;
import org.mapdb.*;


public class Node {
  
  private final static String TAG = "[Node ";
  private final static Boolean verbose = true;
  private Integer id;
  private ExternalInput ext;
  //number and kind of neurons 
  private Long n=100l;
  private Long excitatory;
  private Long inhibithory;
  //Bursting Node Cardinality
  private Integer Bn;
  // InterBurst Interval
  private Double IBI;
  //the small world connection matrix
  private HashMap<LongCouple, Double> connectionMatrix = new HashMap<>();
  //proportion between excitatory and inhibithory
  private Double R=0.8;
  // postsynaptic weight
  private Double mu_w_exc;
  private Double mu_w_inh;
  // postsynaptic std dev
  private Double sigma_w_exc;
  private Double sigma_w_inh;
  //excitatory amplitude
  private Double w_pre_exc;
  //inhibitory amplitude
  private Double w_pre_inh;
  //number of external inputs
  private Integer externalInputs=0;  
  //edges per vertex mean degree
  private Integer k=20;  
  //rewiring probability
  private Double prew=0.5;
  private Boolean hasExternalInputs = false;
  private int externalInputType=-1;
  private Double externalInputsTimeOffset;
  private double timeStep = -1;
  private int fireDuration = 1;
  private Double externalAmplitude=
      ExternalInput.EXTERNAL_AMPLITUDE_DEF_VALUE;
  private int externalOutdegree = 0;
  private int externalOutJump = 1;
  private Boolean plasticity;
  private Double etap;
  private Double etam;
  private Double taup;
  private Double taum;
  private Double pwMax;
  private Double to;
  private HashMap <Long,Boolean> external_init= 
      new HashMap <Long, Boolean>();
  private Random random = new Random();

  /**
  *   The Node object
  *
  *   @param id           the node id
  *   @param n            the number of neurons of the node
  *   @param R            the ratio of excitatory neurons over the total
  *                       number of neurons 'n'
  *   @param mu_w_exc     the mean of the postsynaptic weight 
  *                       distribution for excitatory neurons
  *   @param mu_w_inh     the mean of the postsynaptic weight 
  *                       distribution for inhibitory neurons
  *   @param sigma_w_exc  the std deviation of the postsynaptic weight
  *                       distribution for excitatory neurons
  *   @param sigma_w_inh  the std deviation of the postsynaptic weight
  *                       distribution for inhibitory neurons
  *   @param w_pre_exc    the presynaptic weight for excitatory neurons
  *   @param w_pre_inh    the presynaptic weight for inhibitory neurons
  *   @param k            the conn-degree of each neuron
  *   @param prew         the prob of small-world topology rewinig
  *   @param Bn           the number of bursts spike for each 
  *                       firing neuron
  *   @param IBI          the burst inter-spike time 
  *   @param plasticity   simulate neuron plasticity
  *   @param etap         the Eta plus learning constant for plasticity
  *   @param etam         the Eta minus learning constant for plasticity
  *   @param taup         the Tau plus positive time constants for long term 
  *                       potentiation for plasticity
  *   @param taum         the Tau minus positive time constants for long term 
  *                       depression for plasticity
  *   @param pwMax        the max post-ynaptic weight (used for plasticity rules)
  *   @param to           the timeout for plasticity rules
  *
  */
  public Node(
      Integer id, 
      Long n, 
      Double R,
      Double mu_w_exc,
      Double mu_w_inh,
      Double sigma_w_exc,
      Double sigma_w_inh,
      Double w_pre_exc,
      Double w_pre_inh,
      Integer k, 
      Double prew, 
      Integer Bn, 
      Double IBI,
      Boolean plasticity,
      Double etap,
      Double etam,
      Double taup,
      Double taum,
      Double pwMax,
      Double to){
    this.id=id;
    this.n=n;
    this.R=R;
    this.mu_w_exc=mu_w_exc;
    this.mu_w_inh=mu_w_inh;
    this.sigma_w_exc=sigma_w_exc;
    this.sigma_w_inh=sigma_w_inh;
    this.w_pre_exc=w_pre_exc;
    this.w_pre_inh=w_pre_inh<0?w_pre_inh:(-w_pre_inh);
    this.k=k;
    this.prew=prew;
    this.Bn=Bn;
    this.IBI=IBI;
    this.plasticity=plasticity;
    this.etap=etap;
    this.etam=etam;
    this.taup=taup;
    this.taum=taum;
    this.pwMax=pwMax;
    this.to=to;
    initExcitInhib();
    nodeInit();
  }
  
  public Node(
      Integer id, 
      Long n, 
      Integer externalInputs, 
      int externalInputType,
      Double externalInputsTimeOffset,
      double timeStep, 
      int fireDuration, 
      Double externalAmplitude,
      Integer externalOutdegree,
      Double R, 
      Double mu_w_exc,
      Double mu_w_inh,
      Double sigma_w_exc,
      Double sigma_w_inh,
      Double w_pre_exc,
      Double w_pre_inh,
      Integer k, 
      Double prew, 
      Integer Bn, 
      Double IBI,
      Boolean plasticity,
      Double etap,
      Double etam,
      Double taup,
      Double taum,
      Double pwMax,
      Double to){
    this.id=id;
    this.n=n;
    this.externalInputType=externalInputType;
    this.externalInputsTimeOffset=externalInputsTimeOffset;
    this.R=R;
    this.mu_w_exc = mu_w_exc;
    this.mu_w_inh = mu_w_inh;
    this.sigma_w_exc=sigma_w_exc;
    this.sigma_w_inh=sigma_w_inh;
    this.w_pre_exc=w_pre_exc;
    this.w_pre_inh=w_pre_inh<0?w_pre_inh:(-w_pre_inh);
    if (externalInputs>0){
      hasExternalInputs=true;
      this.externalInputs=externalInputs;
      this.timeStep=timeStep;
      this.fireDuration=fireDuration;
      this.externalAmplitude=externalAmplitude;
      this.externalOutdegree=externalOutdegree;
      do { this.externalOutJump=random.nextInt(1987);}
        while (this.externalOutJump==0);
    }
    this.k=k;
    this.prew=prew;
    this.Bn=Bn;
    this.IBI=IBI;
    this.plasticity=plasticity;
    this.etap=etap;
    this.etam=etam;
    this.taup=taup;
    this.taum=taum;
    this.pwMax=pwMax;
    this.to=to;
    initExcitInhib();
    nodeInit();
    
  }
  
  public void nodeInit(){
    println("init...");
    // ring closure
    wireInit(); 
    externalInputInit();
    println("init done.");
  }
  
  public void initExcitInhib(){
    excitatory= (long) Math.floor(n*R);
    inhibithory=n-excitatory;
  }
  
  private void putConnection (
      Long firingNeuronId, 
      Long burningNeuronId, 
      Double presynaptic_weight){
    connectionMatrix.put(
        new LongCouple(firingNeuronId, burningNeuronId), 
        presynaptic_weight);
  }
  
  public Double getConnectionPresynapticWeight(
      Long firingNeuronId, 
      Long burningNeuronId){
    return (
        connectionMatrix.get(
            new LongCouple(firingNeuronId, burningNeuronId))!=null)?
        connectionMatrix.get(
            new LongCouple(firingNeuronId, burningNeuronId)):0;
  }
  
  public Iterator<Entry<LongCouple, Double>> getIterator(){
    return connectionMatrix.entrySet().iterator();
  }
  
  public Iterator<LongCouple> getKeyConnectionIterator(){
    return connectionMatrix.keySet().iterator();
  }
  
  public Integer getId(){
    return id;
  }
  
  private void wireInit(){
    if (n<=1)
      return;
    println("ring wiring...");
    //randomize adjacency
    DB tmpDb1 = DBMaker.memoryDirectDB().make();
    DB tmpDb2 = DBMaker.memoryDirectDB().make();
    HTreeMap<Long, Long> shuffled = tmpDb1.hashMap(
        "shuffle", 
        Serializer.LONG,Serializer.LONG).create();
    HTreeMap<Long, Long> shuffled_rand = tmpDb2.hashMap(
        "shuffle", 
        Serializer.LONG,Serializer.LONG).create();
    Shuffler.shuffleArray(shuffled,n);
    Shuffler.shuffleArray(shuffled_rand,n);
    int k2=k/2;
    Double tmpAmpl;
    long l=0;
    for (long i=0; i<n;++i){
      Long tmpSrc=shuffled.get(i);
      if (isExcitatory(shuffled.get(i)))
        tmpAmpl=w_pre_exc;
      else
        tmpAmpl=w_pre_inh;
      for (long j=1; j<=k2;++j){
        //rewiring condition
        if (Math.random()<prew){
          Long tmp;
          for (;
              ((tmp = shuffled_rand.get(l) )
                  .equals(tmpSrc)) || 
                  (tmp.equals(shuffled.get((i+j)%n)))||
                  (connectionMatrix.get(
                      new LongCouple(
                          tmpSrc,
                          tmp))!=null);
                l=(l+1)%n){}
          putConnection(shuffled.get(i), tmp, tmpAmpl);
        }
        else
          putConnection(
              shuffled.get(i), 
              shuffled.get((i+j)%n), 
              tmpAmpl);
        if (Math.random()<prew){
          Long tmp;
          for (;
              ((tmp = shuffled_rand.get(l) )
                  .equals(tmpSrc)) || 
                  (tmp.equals(shuffled.get((n+i-j)%n)))||
                  (connectionMatrix.get(
                      new LongCouple(
                          tmpSrc,
                          tmp))!=null);
                l=(l+1)%n){}
          //while ( 
          //    ((tmp = (long) Math.round(Math.random()*(n-1)))
          //    .equals(shuffled.get(i))) || 
          //    (tmp.equals(shuffled.get((n+i-j)%n)))){}
          putConnection(shuffled.get(i), tmp, tmpAmpl);
        }
        else
          putConnection(
              shuffled.get(i), 
              shuffled.get((n+i-j)%n), 
              tmpAmpl);
      }
    }
    shuffled.close();
    println("wiring done.");
  }
  
  private void externalInputInit(){
    if (externalInputs<=0){
      println("d with no external input.");
      return;
    }
    println("creating external input...");
    ext= new ExternalInput(
        this, 
        externalInputType,
        externalInputsTimeOffset,
        fireDuration,
        externalAmplitude,
        externalOutdegree,
        timeStep);
    println(
        "external input created, external spikes in queue:"
        +ext.getExternalSpikesInQueue());
  }

  public Long getN() {
    return n;
  }

  public Long getExcitatory() {
    return excitatory;
  }

  public Long getInhibithory() {
    return inhibithory;
  }

  public Double getExcitProportion() {
    return R;
  }

  public Double getMu_w_exc() {
    return mu_w_exc;
  }

  public Double getMu_w_inh() {
    return mu_w_inh;
  }

  public Double getMu_w_agnostic(Long neuronId) {
    return (isExcitatory(neuronId)?mu_w_exc:mu_w_inh);
  }

  public Double getSigma_w_exc(){
    return sigma_w_exc;
  }

  public Double getSigma_w_inh(){
    return sigma_w_inh;
  }

  public Double getSigma_w_agnostic(Long neuronId) {
    Double retval=isExcitatory(neuronId)?sigma_w_exc:sigma_w_inh;
    return (retval==null)?1:retval;
  }

  public void setMu_w_exc(Double mu_w_exc) {
    this.mu_w_exc = mu_w_exc;
  }

  public void setMu_w_inh(Double mu_w_inh) {
    this.mu_w_inh = mu_w_inh;
  }

  public Double getExc_ampl() {
    return w_pre_exc;
  }

  public Double getInh_ampl() {
    return w_pre_inh;
  }
  
  public Double getPresynapticForNeuron(Long neuronId) {
    return (isExcitatory(neuronId)?w_pre_exc:w_pre_inh);
  }

  public Integer getExternalInputs() {
    return externalInputs;
  }

  public Integer getK() {
    return k;
  }

  public Double getPrew() {
    return prew;
  }
  
  public int getExternalInputsType(){
    return externalInputType; 
  }
  
  public void setStandardExternalInput(){
    ext= new ExternalInput(
        this, 
        externalInputType,
        externalInputsTimeOffset, 
        fireDuration,
        externalAmplitude,
        externalOutdegree,
        timeStep);
  }
  
  public int getExternalOutDegree(){
    return externalOutdegree;
  }

  public int getExternalOutJump(){
    return externalOutJump;
  }

  public Boolean hasExternalInput(){
    return hasExternalInputs;
  }

  public ExternalInput getExternalInput() {
    return ext;
  }
  
  public Double getAmplitudeValue(Long extNeuronGlobalId){
    if ((extNeuronGlobalId-n)>Integer.MAX_VALUE)
      throw new IndexOutOfBoundsException(
          "[NODE ERROR] The external input id is too big");
    if (hasExternalInputs)
      return ext.getAmplitudeValue((int)(long)(extNeuronGlobalId-n));
    return null;
  }

  public Boolean isExternalInput(Long neuronId){
    return neuronId>=n;
  }
  
  public Integer getBn() {
    return (Bn!=null)? Bn : 1 ;
  }

  public Double getIBI() {
    return IBI;
  }
  
  public Boolean getPlasticity(){
    return plasticity;
  }

  public Double getEtap() {
    return etap;
  }

  public Double getEtam() {
    return etam;
  }

  public Double getTaup() {
    return taup;
  }

  public Double getTaum() {
    return taum;
  }

  public Double getPwMax() {
    return pwMax;
  }

  public Double getPlasticityTo() {
    return to;
  }
  
  public Double getExternalAmplitude() {
    return externalAmplitude;
  }
  
  public Double getExternalInputsTimeOffset(Long extNeuronId) {
    Boolean ext_init=external_init.get(extNeuronId);
    double retval=externalInputsTimeOffset;
    if (ext_init==null)
      external_init.put(extNeuronId, true);
    else
      externalInputsTimeOffset=timeStep;
    return retval;
  }
  
  public boolean isExcitatory(Long neuronId){
    if (neuronId<excitatory)
      return true;
    return false;
  }

  public void printNodesConnections(){
    println("printing connections:");
    for (long i=0; i<n; ++i){
      System.out.print(i+".\t");
      for (long j=0; j<n; ++j)
        System.out.print(getConnectionPresynapticWeight(i, j)+", ");
      System.out.println();
    }
  }

  private void println(String s){
    if (verbose)
      System.out.println(TAG+id+"] "+s);
  }
  
}

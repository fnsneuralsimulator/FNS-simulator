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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import spiking.controllers.node.NodeThread;
import utils.constants.Constants;
import utils.statistics.StatisticsCollector;
import utils.tools.IntegerCouple;
import utils.tools.LongCouple;


/**
 * This class manages all the region synapses data set.
 * This implies both the axons delays and the postsynaptic weights
 * @author paracone
 *
 */
public class SynapsesManager {
  
  private final static String TAG = "[Syapses Manager ";
  private final static Boolean verbose = true;

  
  private Node n;
  //private NeuronsManagerOld nMan;
  
  // the list of axonal delays, only for inter region connections
  private HashMap<Synapse, Double> axmap;
  //private HTreeMap<String, Double> axmap;
  // the list of all synapses with the axons belonging
  // to a neuron of the region
  private HashMap<Synapse, Synapse> synapses;
  // the lists of connections, indexed by firing neuron
  private HashMap<Long, ArrayList<Synapse>> firingNeuronSynapses;
  // the lists of connections, indexed by firing neuron
  //extendible
  private HashMap<Long, ArrayList<Synapse>> burningNeuronSynapses;
  // the lists of inter region connections, indexed by firing neuron
  //extendible
  private HashMap<Long, ArrayList<Synapse>> firingNeuronInterNodeSynapses;
  // the lists of inter region connections, indexed by firing neuron
  //extendible
  private HashMap<Long, ArrayList<Synapse>> burningNeuronInterNodeConnections;
  //the external input synapses
  //private HashMap<Long, ArrayList<Synapse>> externalInputSynapses;
  // the lists of axonal delays, only for inter region connections
  //private HTreeMap<Long, HashMap<Synapse, Double>> axmap;
  //gamma distribution
  public Double alpha =1000000.0;
  public Double madel =8.0;
  public Double beta  = madel/alpha;
  private GammaDistribution gd; 
  private Double minAxDelay = Double.MAX_VALUE;
  private Double avgNeuronalSignalSpeed;
  private Random randGen = new Random(System.currentTimeMillis());
  
  
  public SynapsesManager (Node n, Double avgNeuronalSignalSpeed){
    this.n=n;
    gd= new GammaDistribution(alpha, beta);
    axmap = new HashMap<Synapse, Double>();
    synapses = new HashMap<Synapse, Synapse>();
    firingNeuronSynapses = new HashMap<Long, ArrayList<Synapse>>();
    //if (n.getExternalOutDegree()>0)
    //  externalInputSynapses=
    //      new HashMap<Long, ArrayList<Synapse>>();
    if (n.getPlasticity())
      burningNeuronSynapses = new HashMap<Long, ArrayList<Synapse>>();
    firingNeuronInterNodeSynapses = 
        new HashMap<Long, ArrayList<Synapse>>();
    burningNeuronInterNodeConnections = 
        new HashMap<Long, ArrayList<Synapse>>();
    this.avgNeuronalSignalSpeed=avgNeuronalSignalSpeed;
    init();
  }
  
  private void init(){ 
    Iterator<LongCouple> it = n.getKeyConnectionIterator();
    Double tmp_presynaptic_w=null;
    //setting the intra-node synapses
    while (it.hasNext()){
      LongCouple tmpCouple = it.next();
      tmp_presynaptic_w=n.getConnectionPresynapticWeight(
          tmpCouple.getFiring(),
          tmpCouple.getBurning());
      if (tmp_presynaptic_w==null)
         continue;
      double postSynW=
          Math.abs(
              randGen.nextGaussian()*
              n.getSigma_w_agnostic(tmpCouple.getBurning())+
              n.getMu_w_agnostic(tmpCouple.getBurning()));
      if (n.getMu_w_agnostic(tmpCouple.getBurning()) < 0)
        postSynW=-postSynW;
      if (tmp_presynaptic_w!=null)
        setIntraNodeSynapse(
            new Synapse(
                n.getId(), 
                tmpCouple.getFiring(), 
                n.getId(), 
                tmpCouple.getBurning(),
                0.0,
                postSynW,
                tmp_presynaptic_w,
                false,
                false));
    }
  }
  
  public void setIntraNodeSynapse(Synapse syn){
    if (syn.getAxonNodeId().equals(n.getId()))
      putFiringIntraNodeSynapse(syn);
    if (syn.getBurningNodeId().equals(n.getId()))
      putBurningIntraNodeSynapse(syn);
    if ( (syn.getAxonNodeId().equals(n.getId())) &&
        (syn.getBurningNodeId().equals(n.getId())))
      synapses.put(syn, syn);
    else
      System.out.println("[SYNAPSES MANAGER SETPOSTSYNAPTICWEIGHT "
          +"WARNING] adding an internode synapse as intranode");
  }
  
  public void setIntraNodePostSynapticWeight(
      Synapse syn, 
      Double postsynaptic_w) {
    Synapse s = synapses.get(syn);
    if (s!=null)
      s.setPostsynapticWeight(postsynaptic_w);
    
  }
  
  
  public void setInterNodeSynapse(Synapse syn){
    Synapse s = synapses.get(syn);
    //idempotency rule
    if (s==null)    
      synapses.put(syn, syn);
  }
  
  
  private void putFiringIntraNodeSynapse(Synapse firingNeuronSynapse){
    if (firingNeuronSynapses.size()>=Integer.MAX_VALUE){
      throw new ArrayIndexOutOfBoundsException(
          "You are triyng to add to much internode connection"+
           " to the same neuron:"+
          firingNeuronSynapse.getFiring()+ 
          "of the node:"+
          n.getId());
    }
    ArrayList<Synapse> list = 
        firingNeuronSynapses.get(firingNeuronSynapse.getFiring());
    if (list==null){
      firingNeuronSynapses.put(
          firingNeuronSynapse.getFiring(), 
          new ArrayList<Synapse>());
      list = firingNeuronSynapses.get(
          firingNeuronSynapse.getFiring());
    }
    list.add(firingNeuronSynapse);
  }

  private void putBurningIntraNodeSynapse(Synapse burningNeuronSynapse){
    if (!n.getPlasticity())
      return;
    if (burningNeuronSynapses.size()>=Integer.MAX_VALUE){
      throw new ArrayIndexOutOfBoundsException(
          "You are triyng to add to much internode connection"+
           " to the same neuron:"+
          burningNeuronSynapse.getBurning()+ 
          "of the region:"+
          n.getId());
    }
    ArrayList<Synapse> list = 
        burningNeuronSynapses.get(burningNeuronSynapse.getBurning());
    if (list==null){
      burningNeuronSynapses.put(
          burningNeuronSynapse.getBurning(), 
          new ArrayList<Synapse>());
      list = 
          burningNeuronSynapses.get(burningNeuronSynapse.getBurning());
    }
    list.add(burningNeuronSynapse);
  }
  
  /**
   * @return the list for the specified neuron;
   * if no such list exists, it creates a new htree map 
   * and returns its pointer
   */
  public ArrayList<Synapse> getFiringNeuronSynapses(
      Long firingNeuronId){
    ArrayList<Synapse> retval = firingNeuronSynapses.get(firingNeuronId);
    if (retval==null){
      firingNeuronSynapses.put(firingNeuronId, new ArrayList<Synapse>());
      retval = firingNeuronSynapses.get(firingNeuronId);
    }
    return retval;
  }

  ///**
  // * @return the list for the specified external input;
  // * if no such list exists, it creates a new htree map 
  // * and returns its pointer
  // */
  //public ArrayList<Synapse> getExternalInputSynapses(
  //    Long externalInputId){
  //  ArrayList<Synapse> retval = 
  //      externalInputSynapses.get(externalInputId);
  //  if (retval==null){
  //    externalInputSynapses.put(
  //        firingNeuronId, 
  //        new ArrayList<Synapse>());
  //    retval = externalInputSynapses.get(ExternalInputId);
  //  }
  //  return retval;
  //}
  
  /**
   * @return the list for the specified neuron;
   * if no such list exists, it creates a new htree map 
   * and returns its pointer
   */
  public ArrayList<Synapse> getBurningNeuronSynapses(
      Long burningNeuronId){
    ArrayList<Synapse> retval = burningNeuronSynapses.get(burningNeuronId);
    if (retval==null){
      burningNeuronSynapses.put(burningNeuronId, new ArrayList<Synapse>());
      retval = burningNeuronSynapses.get(burningNeuronId);
    }
    return retval;
  }
  
  public void addInterNodeSynapse(
      Integer firingNodeId,
      Long firingNeuronId, 
      Integer burningNodeId, 
      Long burningNeuronId, 
      Double mu_w,
      Double presynaptic_w,
      Double length){
    Synapse newSyn = new Synapse(
        firingNodeId,
        firingNeuronId,
        burningNodeId,
        burningNeuronId,
        length, 
        mu_w,
        presynaptic_w,
        false,
        true);
    setInterNodeSynapse(newSyn);
    if (firingNodeId.equals(n.getId()))
      putFiringNeuronInterNodeConnection(firingNeuronId,newSyn);
    else if(burningNodeId.equals(n.getId()))
      putBurningNeuronInterNodeConnection(burningNeuronId,newSyn);
    else
      System.out.println("[SYNAPSES MANAGER WARNING]adding an internode synapse which does "
          + "not belong to the current node:\n\tsynapse:"+
          newSyn.toString()+
          "\n\tcurrent region:"+
          n.getId());
  }
  
  public int interNodeConnectionsNum(){
    return axmap.size();
  }
  
  private void putFiringNeuronInterNodeConnection(Long firingNeuronId, Synapse neuronNodeConnection){
    if (firingNeuronInterNodeSynapses.size()>=Integer.MAX_VALUE){
      throw new ArrayIndexOutOfBoundsException("You are triyng to add too much interregion connections"
          + " to the same neuron:"+firingNeuronId+ "of the region:"+n.getId());
    }
    ArrayList<Synapse> list = firingNeuronInterNodeSynapses.get(firingNeuronId);
    if (list==null){
      firingNeuronInterNodeSynapses.put(firingNeuronId, new ArrayList<Synapse>());
      list = firingNeuronInterNodeSynapses.get(firingNeuronId);
    }
    list.add(neuronNodeConnection);
    firingNeuronInterNodeSynapses.put(firingNeuronId, list);
  }

  /**
   * @return the list for the specified neuron;
   * if no such list exists, it creates a new htree map 
   * and returns its pointer
   */
  public ArrayList<Synapse> getFiringNeuronInterNodesSynapses(Long firingNeuronId){
    ArrayList<Synapse> retval = firingNeuronInterNodeSynapses.get(firingNeuronId);
    if (retval==null){
      firingNeuronInterNodeSynapses.put(firingNeuronId, new ArrayList<Synapse>());
      retval = firingNeuronInterNodeSynapses.get(firingNeuronId);
    }
    return retval;
  }
  
  private void putBurningNeuronInterNodeConnection(Long burningNeuronId, Synapse neuronNodeConnection){
    if (burningNeuronInterNodeConnections.size()>=Integer.MAX_VALUE){
      throw new ArrayIndexOutOfBoundsException("You are triyng to add to much interregion connection"
          + " to the same neuron:"+burningNeuronId+ "of the region:"+n.getId());
    }
    ArrayList<Synapse> list = burningNeuronInterNodeConnections.get(burningNeuronId);
    if (list==null){
      burningNeuronInterNodeConnections.put(burningNeuronId, new ArrayList<Synapse>());
      list = burningNeuronInterNodeConnections.get(burningNeuronId);
    }
    list.add(neuronNodeConnection);
  }

  /**
   * @return the htreemap list for the specified neuron;
   * if no such list exists, it creates a new htree map 
   * and returns its pointer
   */
  public ArrayList<Synapse> getBurningNeuronInterNodeConnections(Long burningNeuronId){
    ArrayList<Synapse> retval = burningNeuronInterNodeConnections.get(burningNeuronId);
    if (retval==null){
      burningNeuronInterNodeConnections.put(burningNeuronId, new ArrayList<Synapse>());
      retval = burningNeuronInterNodeConnections.get(burningNeuronId);
    }
    return retval;
  }
  
  public Double getMinAxDelay(){
    return minAxDelay;
  }
  
  public Double getAxDelay(Synapse syn){
    Double delta = (syn.getLength())/(avgNeuronalSignalSpeed);
    if (delta<minAxDelay)
      minAxDelay=delta;
    //println("Syn:"+syn+"\tlen:"+syn.getLength()+"\tspeed:"+avgNeuronalSignalSpeed+"\tdelta:"+delta);
    return delta;
  }
  
  public Iterator <Long> getNeuronIntermoduleConnectionIterator(){
    return firingNeuronInterNodeSynapses.keySet().iterator();
  }
  
  //=======================================  printing function =======================================

  public void printAxMap(){
    if (!verbose)
      return;
    println(" printing axmap");
    Iterator<Synapse> it = axmap.keySet().iterator();
    int i=0;
    while (it.hasNext()){
      //Entry<Synapse,Double> pair = (Entry<Synapse,Double>)it.next();
      Synapse k = it.next();
      System.out.println((i++)+". "+k.toString()+" - "+axmap.get(k));
    }
  }
  
  private void println(String s){
    if (verbose){
      if (n != null)
        System.out.println(TAG+n.getId()+"] "+s);
      else 
        System.out.println(TAG+n.getId()+"/-]"+s);
    }    
  }  
  
}

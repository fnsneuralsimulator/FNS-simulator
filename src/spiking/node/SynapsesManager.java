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


package spiking.node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import spiking.controllers.node.NodeThread;
import utils.constants.Constants;
import utils.statistics.StatisticsCollector;

//import utils.tools.NiceQueue;
//import utils.tools.NiceNode;

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
	private HashMap<Long, ArrayList<Synapse>> firingNeuronInterRegionSynapses;
	// the lists of inter region connections, indexed by firing neuron
	//extendible
	private HashMap<Long, ArrayList<Synapse>> burningNeuronInterRegionConnections;
	// the lists of axonal delays, only for inter region connections
	//private HTreeMap<Long, HashMap<Synapse, Double>> axmap;
	
	//gamma distribution
	public Double alpha =1000000.0;
	public Double madel =8.0;
	public Double beta  = madel/alpha;
	private GammaDistribution gd; 
	
	private Double minAxDelay = Double.MAX_VALUE;
	private Double avgNeuronalSignalSpeed;
	
	
	
	public SynapsesManager (Node n, Double avgNeuronalSignalSpeed){
		this.n=n;
		//this.nMan=rMan.getNeuronsManager();
		//this.rMan.setAxonsManager(this);
		gd= new GammaDistribution(alpha, beta);
		axmap = new HashMap<Synapse, Double>();
		synapses = new HashMap<Synapse, Synapse>();
		firingNeuronSynapses = new HashMap<Long, ArrayList<Synapse>>();
		if (n.getPlasticity())
			burningNeuronSynapses = new HashMap<Long, ArrayList<Synapse>>();
		firingNeuronInterRegionSynapses  = new HashMap<Long, ArrayList<Synapse>>();
		burningNeuronInterRegionConnections = new HashMap<Long, ArrayList<Synapse>>();
		this.avgNeuronalSignalSpeed=avgNeuronalSignalSpeed;
		init();
	}
	
	private void init(){ 
		Iterator<LongCouple> it = n.getKeyConnectionIterator();
		Double tmp_presynaptic_w=null;
		while (it.hasNext()){
			LongCouple tmpCouple = it.next();
			tmp_presynaptic_w=n.getConnectionPresynapticWeight(tmpCouple.getFiring(),tmpCouple.getBurning());
			//kkk il presinaptico riceve il valore del receiver e non del sender?
			if (tmp_presynaptic_w!=null)
				setIntraNodeSynapse(
						new Synapse(
								n.getId(), 
								tmpCouple.getFiring(), 
								n.getId(), 
								tmpCouple.getBurning(),
								0.0,
								n.getMu_w_agnostic(tmpCouple.getBurning()),
								tmp_presynaptic_w,
								false,
								false));
		}
	}
	
	public void setIntraNodeSynapse(Synapse syn){
		if (syn.getAxonNodeId().equals(n.getId()))
			putFiringIntraNodeSynapse(syn);
		if (syn.getDendriteNodeId().equals(n.getId()))
			putBurningIntraNodeSynapse(syn);
		if ( (syn.getAxonNodeId().equals(n.getId())) &&
				(syn.getDendriteNodeId().equals(n.getId())))
			synapses.put(syn, syn);
		else
			System.out.println("[SYNAPSES MANAGER SETPOSTSYNAPTICWEIGHT WARNING] adding an internode synapse as intranode");
	}
	
	public void setIntraNodePostSynapticWeight(Synapse syn, Double postsynaptic_w) {
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
	
//	public Double getPostSynapticWeight(Synapse syn){
//		if (synapses.get(syn)!=null)
//			return synapses.get(syn).getPostSynapticWeight();
//		else{
//			if ((syn.getAxonNodeId()==n.getId()) && 
//					(n.isExternalInput(syn.getFiring())))
//				return Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL;
//			System.out.println("[syn man] syn:"+syn+" with amplitude null...");
//			return Constants.POST_SYNAPTIC_WEIGHT_DEF_VAL;
//		}
//	}
	
	private void putFiringIntraNodeSynapse(Synapse firingNeuronSynapse){
		if (firingNeuronSynapses.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add to much internode connection"
					+ " to the same neuron:"+firingNeuronSynapse.getFiring()+ "of the region:"+n.getId());
		}
		ArrayList<Synapse> list = firingNeuronSynapses.get(firingNeuronSynapse.getFiring());
		if (list==null){
			firingNeuronSynapses.put(firingNeuronSynapse.getFiring(), new ArrayList<Synapse>());
			list = firingNeuronSynapses.get(firingNeuronSynapse.getFiring());
		}
		list.add(firingNeuronSynapse);
		
	}
	
	private void putBurningIntraNodeSynapse(Synapse burningNeuronSynapse){
		if (!n.getPlasticity())
			return;
		if (burningNeuronSynapses.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add to much internode connection"
					+ " to the same neuron:"+burningNeuronSynapse.getBurning()+ "of the region:"+n.getId());
		}
		ArrayList<Synapse> list = burningNeuronSynapses.get(burningNeuronSynapse.getBurning());
		if (list==null){
			burningNeuronSynapses.put(burningNeuronSynapse.getBurning(), new ArrayList<Synapse>());
			list = burningNeuronSynapses.get(burningNeuronSynapse.getBurning());
		}
		list.add(burningNeuronSynapse);
	}
	
	/**
	 * @return the list for the specified neuron;
	 * if no such list exists, it creates a new htree map 
	 * and returns its pointer
	 */
	public ArrayList<Synapse> getFiringNeuronConnections(Long firingNeuronId){
		ArrayList<Synapse> retval = firingNeuronSynapses.get(firingNeuronId);
		if (retval==null){
			firingNeuronSynapses.put(firingNeuronId, new ArrayList<Synapse>());
			retval = firingNeuronSynapses.get(firingNeuronId);
		}
		return retval;
	}
	
	/**
	 * @return the list for the specified neuron;
	 * if no such list exists, it creates a new htree map 
	 * and returns its pointer
	 */
	public ArrayList<Synapse> getBurningNeuronConnections(Long burningNeuronId){
		ArrayList<Synapse> retval = burningNeuronSynapses.get(burningNeuronId);
		if (retval==null){
			burningNeuronSynapses.put(burningNeuronId, new ArrayList<Synapse>());
			retval = burningNeuronSynapses.get(burningNeuronId);
		}
		return retval;
	}
	
	public void addInterNodeSynapse(
			Integer firingRegionId,
			Long firingNeuronId, 
			Integer burningRegionId, 
			Long burningNeuronId, 
			Double mu_w,
			Double presynaptic_w,
			Double length){
		Synapse newSyn = new Synapse(
				firingRegionId,
				firingNeuronId,
				burningRegionId,
				burningNeuronId,
				length, 
				mu_w,
				presynaptic_w,
				false,
				true);
		setInterNodeSynapse(newSyn);
		if (firingRegionId.equals(n.getId()))
			putFiringNeuronInterRegionConnection(firingNeuronId,newSyn);
		else if(burningRegionId.equals(n.getId()))
			putBurningNeuronInterRegionConnection(burningNeuronId,newSyn);
		else
			System.out.println("[SYNAPSES MANAGER WARNING]adding an internode synapse which does "
					+ "not belong to the current node:\n\tsynapse:"+
					newSyn.toString()+
					"\n\tcurrent region:"+
					n.getId());
	}
	
	public int interRegionConnectionsNum(){
		return axmap.size();
	}
	
	private void putFiringNeuronInterRegionConnection(Long firingNeuronId, Synapse neuronRegionConnection){
		if (firingNeuronInterRegionSynapses.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add too much interregion connections"
					+ " to the same neuron:"+firingNeuronId+ "of the region:"+n.getId());
		}
		ArrayList<Synapse> list = firingNeuronInterRegionSynapses.get(firingNeuronId);
		if (list==null){
			firingNeuronInterRegionSynapses.put(firingNeuronId, new ArrayList<Synapse>());
			list = firingNeuronInterRegionSynapses.get(firingNeuronId);
		}
		list.add(neuronRegionConnection);
		firingNeuronInterRegionSynapses.put(firingNeuronId, list);
	}

	/**
	 * @return the list for the specified neuron;
	 * if no such list exists, it creates a new htree map 
	 * and returns its pointer
	 */
	public ArrayList<Synapse> getFiringNeuronInterNodeConnections(Long firingNeuronId){
		ArrayList<Synapse> retval = firingNeuronInterRegionSynapses.get(firingNeuronId);
		if (retval==null){
			firingNeuronInterRegionSynapses.put(firingNeuronId, new ArrayList<Synapse>());
			retval = firingNeuronInterRegionSynapses.get(firingNeuronId);
		}
		return retval;
	}
	
	private void putBurningNeuronInterRegionConnection(Long burningNeuronId, Synapse neuronRegionConnection){
		if (burningNeuronInterRegionConnections.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add to much interregion connection"
					+ " to the same neuron:"+burningNeuronId+ "of the region:"+n.getId());
		}
		ArrayList<Synapse> list = burningNeuronInterRegionConnections.get(burningNeuronId);
		if (list==null){
			burningNeuronInterRegionConnections.put(burningNeuronId, new ArrayList<Synapse>());
			list = burningNeuronInterRegionConnections.get(burningNeuronId);
		}
		list.add(neuronRegionConnection);
	}

	/**
	 * @return the htreemap list for the specified neuron;
	 * if no such list exists, it creates a new htree map 
	 * and returns its pointer
	 */
	public ArrayList<Synapse> getBurningNeuronInterNodeConnections(Long burningNeuronId){
		ArrayList<Synapse> retval = burningNeuronInterRegionConnections.get(burningNeuronId);
		if (retval==null){
			burningNeuronInterRegionConnections.put(burningNeuronId, new ArrayList<Synapse>());
			retval = burningNeuronInterRegionConnections.get(burningNeuronId);
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
		return firingNeuronInterRegionSynapses.keySet().iterator();
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

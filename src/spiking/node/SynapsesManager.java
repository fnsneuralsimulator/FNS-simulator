/**
 *  Copyright 2015-2016 ETLAB http://eltlab.uniroma2.it/
 *  
 *  Mario Salerno 		- 	salerno@uniroma2.it
 *  Gianluca Susi 		- 	gianluca.susi@uniroma2.it
 *  Alessandro Cristini - 	alessandro.cristini@uniroma2.it
 *  Emanuele Paracone 	- 	emanuele.paracone@gmail.com
 *  						
 *  
 *  This file is part of Firnet.
 *
 *  Firnet is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Firnet is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Firnet.  If not, see <http://www.gnu.org/licenses/>.
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

	
	private Node r;
	//private NeuronsManagerOld nMan;
	
	// the list of axonal delays, only for inter region connections
	private HashMap<Synapse, Double> axmap;
	//private HTreeMap<String, Double> axmap;
	// the list of postsynaptic weights for all synapses with the axons belonging
	// to a neuron of the region
	//extendible
	private HashMap<Synapse, Double> postSynapticWeights;
	// the lists of connections, indexed by firing neuron
	//extendible
	private HashMap<Long, ArrayList<Synapse>> firingNeuronSynapses;
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
	
	
	
	public SynapsesManager (Node r, Double avgNeuronalSignalSpeed){
		this.r=r;
		//this.nMan=rMan.getNeuronsManager();
		//this.rMan.setAxonsManager(this);
		gd= new GammaDistribution(alpha, beta);
		axmap = new HashMap<Synapse, Double>();
		postSynapticWeights = new HashMap<Synapse, Double>();
		firingNeuronSynapses = new HashMap<Long, ArrayList<Synapse>>();
		firingNeuronInterRegionSynapses  = new HashMap<Long, ArrayList<Synapse>>();
		burningNeuronInterRegionConnections = new HashMap<Long, ArrayList<Synapse>>();
		this.avgNeuronalSignalSpeed=avgNeuronalSignalSpeed;
		init();
	}
	
	private void init(){ 
		Iterator<LongCouple> it = r.getKeyConnectionIterator();
		Double tmpAmpl=null;
		while (it.hasNext()){
			LongCouple tmpCouple = it.next();
			tmpAmpl=r.getConnection(tmpCouple.getBurning(), tmpCouple.getFiring());
			if (tmpAmpl!=null)
				setPostSynapticWeight(
						new Synapse(
								r.getId(), 
								tmpCouple.getFiring(), 
								r.getId(), 
								tmpCouple.getBurning(),
								0.0, 
								tmpAmpl), 
						tmpAmpl);
		}
		
	}
	
	
	public void setPostSynapticWeight(Synapse syn, Double postSynapticWeight){
		if (postSynapticWeight<=0.0)
			return;
		if (syn.getAxonNodeId().equals(r.getId()))
			putFiringRegionSynapse(syn);
		if ( (syn.getAxonNodeId().equals(r.getId())) &&
				(syn.getDendriteNodeId().equals(r.getId())))
			postSynapticWeights.put(syn, postSynapticWeight);
		else
			System.out.println("[SYNAPSES MANAGER SETPOSTSYNAPTICWEIGHT WARNING] adding an internode synapse as intranode");
	}
	
	public void setInterNodePostSynapticWeight(Synapse syn, Double postSynapticWeight){
		if (postSynapticWeight<=0.0)
			return;
//		if (syn.getAxonRegionId().equals(r.getId()))
//			putFiringNeuronInterRegionConnection(syn.getFiring(), syn);
		postSynapticWeights.put(syn, postSynapticWeight);
	}
	
	public Double getPostSynapticWeight(Synapse syn){
		if (postSynapticWeights.get(syn)!=null)
			return postSynapticWeights.get(syn);
		else{
			if ((syn.getAxonNodeId()==r.getId()) && 
					(r.isExternalInput(syn.getFiring())))
				return Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL;
			System.out.println("[syn man] syn:"+syn+" with amplitude null...");
			return Constants.POST_SYNAPTIC_WEIGHT_DEF_VAL;
		}
	}
	
	
	private void putFiringRegionSynapse(Synapse firingNeuronSynapse){
		if (firingNeuronSynapses.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add to much internode connection"
					+ " to the same neuron:"+firingNeuronSynapse.getFiring()+ "of the region:"+r.getId());
		}
		ArrayList<Synapse> list = firingNeuronSynapses.get(firingNeuronSynapse.getFiring());
		if (list==null){
			firingNeuronSynapses.put(firingNeuronSynapse.getFiring(), new ArrayList<Synapse>());
			list = firingNeuronSynapses.get(firingNeuronSynapse.getFiring());
		}
		
//		System.out.println("[Synapse Manager torem] - adding synapse:"+neuronIntraRegionSynapse.toString()+"firing neuron list size num:"+list.size());
		list.add(firingNeuronSynapse);
		
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
	
	
	
	public void addInterNodeSynapse(
			Integer firingRegionId,
			Long firingNeuronId, 
			Integer burningRegionId, 
			Long burningNeuronId, 
			Double amplitude, 
			Double length){
		Synapse newSyn = new Synapse(
				firingRegionId,
				firingNeuronId,
				burningRegionId,
				burningNeuronId,
				length, 
				amplitude);
		setInterNodePostSynapticWeight(newSyn, amplitude);
//		addAxDelay(tmp);
		if (firingRegionId.equals(r.getId()))
			putFiringNeuronInterRegionConnection(firingNeuronId,newSyn);
		else if(burningRegionId.equals(r.getId()))
			putBurningNeuronInterRegionConnection(burningNeuronId,newSyn);
		else
			System.out.println("[SYNAPSES MANAGER WARNING]adding an interregion synapse which does "
					+ "not belong to the current region:\n\tsynapse:"+
					newSyn.toString()+
					"\n\tcurrent region:"+
					r.getId());
	}
	
	public int interRegionConnectionsNum(){
		return axmap.size();
	}
	
	private void putFiringNeuronInterRegionConnection(Long firingNeuronId, Synapse neuronRegionConnection){
		if (firingNeuronInterRegionSynapses.size()>=Integer.MAX_VALUE){
			throw new ArrayIndexOutOfBoundsException("You are triyng to add too much interregion connections"
					+ " to the same neuron:"+firingNeuronId+ "of the region:"+r.getId());
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
					+ " to the same neuron:"+burningNeuronId+ "of the region:"+r.getId());
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
	
	
//	public void addAxDelay(Synapse syn){
//		Double tmp = gd.sample();
//		if (tmp<minAxDelay)
//			minAxDelay=tmp;
//		axmap.put(syn, tmp);		
//	}
	
	public Double getMinAxDelay(){
		return minAxDelay;
	}
	
	public Double getAxDelay(Synapse syn){
//		println("Syn:"+syn+" len:"+syn.getLenght());
//		println("avgNeuronalSignalSpeed:"+avgNeuronalSignalSpeed);
		
//		Double delta = (syn.getLength()*1000.0)/(avgNeuronalSignalSpeed);
		Double delta = (syn.getLength())/(avgNeuronalSignalSpeed);

		//		println("---------------------------------->>>delta:"+delta);
		if (delta<minAxDelay)
			minAxDelay=delta;
		return delta;//axmap.get(syn);
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
			if (r != null)
				System.out.println(TAG+r.getId()+"] "+s);
			else 
				System.out.println(TAG+r.getId()+"/???]"+s);
		}
			
	}
	
	
	
//	public static void main(String[] args) {
		
//		double minVal=1000.0;
//		double tmp;
//		GammaDistribution gd = new GammaDistribution(1000000.0, 18.0/1000000.0);
//		for (int i=0; i<Integer.MAX_VALUE;++i){
//			tmp=gd.sample();
//			if (tmp<minVal){
//				minVal=tmp;
//				System.out.println("\nnew min val:"+minVal);
//			}
//			if (i%1000000000==0)
//				System.out.print(".");
//		}
//		System.out.println("\nbye!");
		
//		File f = new File ("torem.tmp");
//		DB db = DBMaker.fileDB(f)
//				.fileMmapEnable()
//				.fileMmapEnableIfSupported()
//				.fileMmapCleanerHackEnable()
//				.make();
//		HTreeMap<Integer, Integer> h = db.hashMap("toremhash");
//		for (int i=0; i<100; ++i)
//			h.put(i, i);
//		Iterator<Integer> it = h.keySet().iterator();
//		int i=0;
//		while(it.hasNext()){
//			Integer k = it.next();
//			System.out.println((i++)+". "+k+" - "+h.get(k));
//		}
		
//		System.out.println("madel:"+s.getMadel());
		
		
//	}
	
	
}

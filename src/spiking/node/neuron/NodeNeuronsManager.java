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

package spiking.node.neuron;



import java.util.HashMap;

import spiking.node.Node;
import spiking.node.external_inputs.ExternalInput;
import utils.constants.Constants;
import utils.experiment.Experiment;
import utils.tools.NiceNode;
import utils.tools.NiceQueue;

public class NodeNeuronsManager {

	private final static String TAG = "[Neurons Manager for Node: ";
	private final static Boolean verbose = true;

	private Node n;
	private Boolean debug = false;
	//linear decay constant
	private Double D=0.001;
	//threshold
	private Double c=0.04;
	//refractory time constant
	private Double t_arp=0.3;
	//spiking threshold
	private Double sth=1+c;
	//
//	private Double [][] s;
	//private HashMap<Integer, Double[]> neurons = new HashMap<>()
	//the list of active neurons - a map ordered by time values (as key) mapping neuronsId
	private NiceQueue activeNeurons;// = new NiceQueue();	

	
	private Double excitatoryPresynapticDefVal=2.0;
	private Double inhibithoryPresynapticDefVal=-1.0;
	private Double externalSourcesPresynapticDefVal=1.0;
	
	
	//the neuron states for the node
	private HashMap<Long, Double> neuronStates;
	private HashMap<Long, Double> timesToFire;
	private HashMap<Long, Double> lastFiringTimes;
	private HashMap<Long, Double> lastBurningTimes;
	private HashMap<Long, Double> presynapticWeights;
	
	public NodeNeuronsManager(Node r, Double c, 
			Double D, Double t_arp, Double excitatoryPresynapticDefVal, 
			Double inhibithoryPresynapticDefVal, Double externalPresynapticDefVal){
		this.n=r;
		this.c=c;
		this.sth=1+c;
		this.D=D;
		this.t_arp=t_arp;
		this.excitatoryPresynapticDefVal=excitatoryPresynapticDefVal;
		this.inhibithoryPresynapticDefVal=inhibithoryPresynapticDefVal;
		this.externalSourcesPresynapticDefVal = externalPresynapticDefVal;
		activeNeurons=new NiceQueue("activeNeurons-"+r.getId());
		init();
	}
	
	private void init(){
		neuronStates = new HashMap<Long, Double>();
		timesToFire = new HashMap<Long, Double>();
		lastFiringTimes = new HashMap<Long, Double>();
		lastBurningTimes = new HashMap<Long, Double>();
		presynapticWeights = new HashMap<Long, Double>();
	}
	
	public Double getT_arp(){
		return t_arp;
	}

	public Double getSpikingThr() {
		return sth;
	}
	
	public Double getLinearDecayD(){
		return D;
	}
	
	
	public Double getExcitatoryPresynapticDefVal() {
		return excitatoryPresynapticDefVal;
	}

	public Double getInhibithorySynapticDefVal() {
		return inhibithoryPresynapticDefVal;
	}

	public Double getExternalSourcesPresynapticDefVal() {
		return externalSourcesPresynapticDefVal;
	}
	
	public void setTimeToFire(Long neuronId, Double val){
		timesToFire.put(neuronId, val);
	}
	
	public Double getTimeToFire(Long neuronId){
		Double retval = timesToFire.get(neuronId);
		if (retval==null){
			if (neuronId<n.getN())
				return Constants.TIME_TO_FIRE_DEF_VAL;
			return Constants.EXTERNAL_TIME_TO_FIRE_DEF_VAL;
		}
		return retval;
	}
	
	public void setPreSynapticWeight(Long neuronId, Double val){
		presynapticWeights.put(neuronId, val);
	}
	
	public Double getPreSynapticWeight(Long neuronId){
		Double retval = presynapticWeights.get(neuronId);
		if (retval==null)	 
			retval = (isExcitatory(neuronId))? n.getExc_ampl() : n.getInh_ampl();
		return retval;
	}
	
	public Double getState(Long neuronId){
		Double retval = neuronStates.get(neuronId);
		if (retval==null){
			Double lastBurningTime=lastBurningTimes.get(neuronId);
			if (lastBurningTime==null){
				neuronStates.put(neuronId, Math.random());
				retval=neuronStates.get(neuronId);
			}
			else
				retval=0.0;
			
		}
		return retval;
	}
	
	public void setState(Long neuronId, Double val) {
		neuronStates.put(neuronId, val);
	}
	
	
	public void resetState(Long neuronId) {
		neuronStates.put(neuronId, 0.0);		
	}
	
	public void resetTimeToFire(Long neuronId){
		timesToFire.remove(neuronId);
	}
	
	
	public void setLastFiringTime(Long neuronId, Double val){
			lastFiringTimes.put(neuronId, val);
	}
	
	public Double getLastFiringTime(Long neuronId){
		Double retval = lastFiringTimes.get(neuronId);
		if (retval==null)
			retval=Constants.TIME_TO_FIRE_DEF_VAL;
		return retval;
	}
	
	public NiceNode getNextFiringNeuron() {
		return activeNeurons.extractMin();
	}
	
	
	/** 
	 * @return the minimum time to fire for active neurons, 
	 * without polling the value from the queue 
	 * If the queue is empty, null is returned                                                                           
	 */
	public Double getMinFiringTime(){
		return activeNeurons.getMinTime();
	}
	
	public boolean isExcitatory(Long neuronId){
		return n.isExcitatory(neuronId);
	}
	
	public Double getLastBurningTime(Long neuronId) {
		Double retval = lastBurningTimes.get(neuronId);
		if (retval==null)
			retval=0.0;
		return retval;
	}
	
	public void setLastBurningTime(Long neuronId, Double lastBurningTime){
		this.lastBurningTimes.put(neuronId,lastBurningTime); 
	}
	
	/**
	 * 
	 * @param extNeuronId		the id of the external input as referred into the node reg
	 * @param globalOffset		the global offset from which the external input ids start
	 * @param reg				the node of the external input
	 * @param currentTime		the current simulation time
	 */
	public void extInputReset(Long extNeuronId, double currentTime){
		if (currentTime>n.getExternalInput().getFireDuration()){
			removeActiveNeuron(extNeuronId);
			return;
		}
		if (n.getExternalInputsType()==ExternalInput.CONSTANT){
			double fireTime;
			if (currentTime==0.0)
				fireTime=n.getExternalInputsTimeOffset();
			else
				fireTime=currentTime+ n.getExternalInput().getTimeStep();
			setPreSynapticWeight(extNeuronId, n.getAmplitudeValue(extNeuronId,(long)0));
			setTimeToFire(extNeuronId, fireTime);
			addActiveNeuron(extNeuronId, fireTime, currentTime, 0);
			if (fireTime<currentTime){
				println(".......................................constant firet:"+fireTime+" current:"+currentTime);
			}
			return;
		}
		else if ((n.getExternalInputsType()==ExternalInput.NOISE) && (Math.random()<0.3)){
			double fireTime;
			if (currentTime==0.0)
				fireTime=n.getExternalInputsTimeOffset();
			else
				fireTime=currentTime+Math.random()*n.getExternalInput().getTimeStep();
			setPreSynapticWeight(extNeuronId, n.getAmplitudeValue(extNeuronId,(long)0));
			setTimeToFire(extNeuronId, fireTime);
			addActiveNeuron(extNeuronId, fireTime, currentTime, 4);
			return;
		}
		/* Case of poissonian external inputs */
		NiceNode minExternalSpike = n.extractExternalMinSpikeTime(extNeuronId);
		if ((minExternalSpike!=null)&&(!(new Double(minExternalSpike.tf).equals(Constants.EXTERNAL_TIME_TO_FIRE_DEF_VAL)))){
			Double tmpTimeToFire = getTimeToFire(extNeuronId);
			if (tmpTimeToFire==null || tmpTimeToFire.equals(Constants.EXTERNAL_TIME_TO_FIRE_DEF_VAL)){
				/* Presynaptic value for the external neuron = 
				 * Amplitude value in the row of Amplitude Matrix */
				setPreSynapticWeight(extNeuronId, n.getAmplitudeValue(extNeuronId, minExternalSpike.fn));
				/* Assign the minimum of the row minus the current 
				 * minimum time-to-fire.*/
				setTimeToFire(extNeuronId, minExternalSpike.tf);
				/* If the time-to-fire is greater than zero, then 
				 * the firing neuron is added in the active neuron list. */
				if (!getTimeToFire(extNeuronId).equals(Constants.EXTERNAL_TIME_TO_FIRE_DEF_VAL))
					addActiveNeuron(extNeuronId, minExternalSpike.tf, currentTime, 1);
			}
		}
		else{
			removeActiveNeuron(extNeuronId); 
		}
	}
	
	
	public void addActiveNeuron(Long neuronId, Double fireTime, Double currentTime, Integer debug){
		if (fireTime<currentTime) {
			println("\n....................\ndebug"+debug+"\n....................");
			System.exit(1);
		}
		activeNeurons.insert(fireTime, neuronId);
	}
	
	public void removeActiveNeuron(Long neuronId){
		activeNeurons.delete(neuronId);
	}
	
	public int getActiveNeuronsNum(){
		return activeNeurons.size();
	}
	
	
	
	//=======================================   printing functions =======================================
	
	
	private void println(String s){
		if (verbose)
			System.out.println(TAG+n.getId()+"] "+s);
	}
	
	private void debprintln(String s){
		if (verbose&&debug)
			System.out.println(TAG+n.getId()+"][debug] "+s);
	}
	
	public void printAn(){
		println("printing active neurons:");
		NiceNode tmp [] = activeNeurons.toArray();
		for (int i=0; i<tmp.length; ++i)
//			if (tmp[i].fn>99)  
				System.out.println("active neuron "+i+":\t"+tmp[i].toString()+", state:"+getState(tmp[i].fn));	
	}

	
	

}

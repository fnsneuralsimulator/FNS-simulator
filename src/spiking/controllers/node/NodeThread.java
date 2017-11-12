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
import spiking.node.plasticity.Afferent;
import spiking.node.plasticity.AfferentManager;
import utils.constants.Constants;
import utils.statistics.StatisticsCollector;
import utils.tools.NiceNode;
import utils.tools.NiceQueue;

public class NodeThread extends Thread{
	
	private final static String TAG = "[Node Thread ";
	private final static Boolean verbose = true;

	private Node r;
	private NodeNeuronsManager nnMan;
	private NodesManager rMan;
	private SynapsesManager synMan;
	private AfferentManager affMan = null;
	
	private Boolean plasticity;
	//queues map enqueues the spikes sended by a specific firing neuron to a specific burning neuron
	//extendible
	private HashMap<Synapse, NiceQueue>queuesMap;
	private double currentTime=0.0;
	private double stopTime=0.0;
	private Boolean debug = false;
	private Boolean activePassiveDebug = false;
	private int debug_level = 3;
	private int debNum=28;
	private int [] debugCases = new int[debNum];	
	private ArrayList<InterNodeSpike> internodeSpikes;	
	private Boolean keepRunning=true;
	private Lock lock = new ReentrantLock();
	private Condition newTimeSplitCond = lock.newCondition();
	private PriorityQueue<InterNodeBurningSpike> interNodeBurningSpikes=new PriorityQueue<InterNodeBurningSpike>();
	
	public NodeThread(
			NodesManager rMan, 
			Integer id, 
			Long n, 
			Double excitProportion, 
			Integer k, 
			Double prew,
			Double internalAmplitude,
			Double d, 
			Double ld, 
			Double kr, 
			Double excitatoryPresynapticDefVal, 
			Double inhibithoryPresynapticDefVal, 
			Double externalPresynapticDefVal, 
			Boolean plasticity, 
			Double avgNeuronalSignalSpeed){
		r=new Node(id,n, excitProportion, k, prew,internalAmplitude);//,internalAmplitude);
		init(rMan, d, ld, kr, excitatoryPresynapticDefVal, 
				inhibithoryPresynapticDefVal, externalPresynapticDefVal, plasticity,avgNeuronalSignalSpeed);
	}
	
	public NodeThread(
			NodesManager rMan, 
			Integer id, 
			Long n, 
			Integer externalInputs, 
			int externalInputType, 
			int timeStep, 
			Double fireRate,  
			int fireDuration,
			Double externalAmplitude,
			Double excitRatio, 
			Integer k, 
			Double prew, 
			Double internalAmplitude,
			Double c, 
			Double D, 
			Double t_arp, 
			Double excitatoryPresynapticDefVal, 
			Double inhibithoryPresynapticDefVal, 
			Double externalPresynapticDefVal, 
			Boolean plasticity, 
			Double avgNeuronalSignalSpeed){
		r=new Node(id,
				n,
				externalInputs,
				externalInputType,
				timeStep,
				fireRate, 
				fireDuration,
				externalAmplitude, 
				excitRatio, 
				k, 
				prew, 
				internalAmplitude);
		init(rMan, 
				c, 
				D, 
				t_arp, 
				excitatoryPresynapticDefVal, 
				inhibithoryPresynapticDefVal, 
				externalPresynapticDefVal, 
				plasticity,
				avgNeuronalSignalSpeed);
		
	}
	
	public void init(NodesManager rMan, 
			Double c, 
			Double D, 
			Double t_arp, 
			Double excitatoryPresynapticDefVal, 
			Double inhibithoryPresynapticDefVal, 
			Double externalPresynapticDefVal, 
			Boolean plasticity, 
			Double avgNeuronalSignalSpeed){
		queuesMap = new HashMap<Synapse, NiceQueue>();
		internodeSpikes = new ArrayList<InterNodeSpike>();
		nnMan = new NodeNeuronsManager(r, 
				c, 
				D, 
				t_arp, 
				excitatoryPresynapticDefVal, 
				inhibithoryPresynapticDefVal, 
				externalPresynapticDefVal);
		this.plasticity=plasticity;
		if (plasticity)
			affMan = new AfferentManager();
		initExtInput();
		synMan = new SynapsesManager(r,avgNeuronalSignalSpeed);
		this.rMan=rMan;
		println("c:"+c);
		println("D:"+D);
		println("t arp:"+t_arp);
	}
	
	public void run(){
		NiceNode minFiringNeuron;
		Long firingNeuronId=null;
		Double spikeTime=null, tmpMinFiringTime, tmpMinInterNodeBurningTime;
		InterNodeBurningSpike tmpInterNodeBurningSpike;
		int fires=0;
		Boolean stopped=false;		
		//the actual thread routine
		while (keepRunning){
			for (; currentTime<stopTime; ++fires){
				if (!stopped){
					tmpMinFiringTime=nnMan.getMinFiringTime();
					tmpInterNodeBurningSpike=interNodeBurningSpikes.peek();
					if (tmpInterNodeBurningSpike!=null){
						tmpMinInterNodeBurningTime=tmpInterNodeBurningSpike.getTimeToBurn();
						if ((tmpMinInterNodeBurningTime!=null)&&(tmpMinInterNodeBurningTime<stopTime)){
							if (tmpMinFiringTime!=null){
								if (tmpMinInterNodeBurningTime<tmpMinFiringTime){
									InterNodeSpike irs=interNodeBurningSpikes.poll().getInterNodeSpike();
									burnNeuron(irs.getSyn(), irs.getBurnTime(), irs.getFireTime(), false);
									currentTime=tmpMinInterNodeBurningTime;
									continue;
								}
							}
							else{
								InterNodeSpike irs=interNodeBurningSpikes.poll().getInterNodeSpike();
								burnNeuron(irs.getSyn(), irs.getBurnTime(), irs.getFireTime(), false);
								currentTime=tmpMinInterNodeBurningTime;
								continue;
							}
						}
					}
					//get the next neuron ready to fire in the list of the active neurons
					debprintln("\n\ngetting next firing neuron...");
					minFiringNeuron=nnMan.getNextFiringNeuron();
					if (minFiringNeuron==null){
						break;
					}
					++ debugCases[27];
					debprintln("got next firing neuron.");
					firingNeuronId=minFiringNeuron.fn;
					spikeTime=minFiringNeuron.tf;
					debprintln("\n\n"+fires+". currentTime:"+currentTime+" firing:"+firingNeuronId+
							" spiketime:"+(spikeTime));
					if (spikeTime>stopTime){
						stopped=true;
						break;
					}
				}
				else{
					if (spikeTime>stopTime){
						break;
					}
					else
						stopped=false;
				}
				
				//time update
				currentTime = spikeTime;
				StatisticsCollector.collectFireSpike(
						r.getId(), 
						firingNeuronId, 
						currentTime, 
						rMan.getMaxN(), 
						rMan.getCompressionFactor(),
						(firingNeuronId<r.getExcitatory()),
						(firingNeuronId>=r.getN()) );
				//firing spikes detecting and storing
				if (firingNeuronId<r.getN()){
					++debugCases[24];
					//State resetting to passive mode
					nnMan.resetState(firingNeuronId);
					nnMan.resetTimeToFire(firingNeuronId);
				}
				// last firing time for neuron
				nnMan.setLastFiringTime(firingNeuronId, currentTime);
				if (firingNeuronId>=r.getN()){
					debprintln("external input fire resetting...");
					++debugCases[25];
					//ext time-to-fire resetting
					nnMan.resetTimeToFire(firingNeuronId);
					//external routine
					nnMan.extInputReset(firingNeuronId, currentTime);
					debprintln("external input fire reset.");
				}
				//Plasticity
				if (plasticity && (!nnMan.getPreSynapticWeight(firingNeuronId).equals(nnMan.getInhibithorySynapticDefVal()))){
					ArrayList<Synapse> neuronSynapses = synMan.getFiringNeuronInterNodeConnections(firingNeuronId);
					for (int i=0; (neuronSynapses!=null) && (i<neuronSynapses.size());++i )
						plast(firingNeuronId, neuronSynapses.get(i), currentTime);
					neuronSynapses = synMan.getBurningNeuronInterNodeConnections(firingNeuronId);
					for (int i=0; (neuronSynapses!=null) && (i<neuronSynapses.size());++i )
						plast(firingNeuronId, neuronSynapses.get(i), currentTime);
				}
				//SEARCH FOR BURNING NEURONS CONNECTED TO (neuronId)
				runBuriningNeurons(firingNeuronId, currentTime);
			}
			completed();
		}
		printDebug();
		closeDB();		
	}
	
	private void closeDB(){
		r.closeDb();
	}
	
	private void addInterNodeSpike(Synapse syn, Double fireTime){
		debprintln("sending internode spike:"+syn+"| fire time:"+fireTime+" | delay:"+synMan.getAxDelay(syn));
		Double axonalDelay=synMan.getAxDelay(syn);
		internodeSpikes.add(new InterNodeSpike(syn, fireTime+axonalDelay,fireTime,axonalDelay));
	}
	
	public Boolean hasInterNodeSpikes(){
		if (internodeSpikes.size()>0)
			return true;
		return false;
	}
	
	public InterNodeSpike popInterNodeSpike(){
		InterNodeSpike retval = internodeSpikes.get(internodeSpikes.size()-1);
		internodeSpikes.remove(internodeSpikes.size()-1);
		return retval;
	}
	
	public double getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	public double getStopTime() {
		return stopTime;
	}

	public void setStopTime(double stopTime) {
		this.stopTime = stopTime;
	}

	public Integer getNodeId(){
		return r.getId();
	}
	
	public void addIntermoduleSynapse(
			Integer firingNodeId, Long firingNeuronId, 
			Integer burningNodeId, Long burningNeuronId, 
			Double amplitude, Double lenght) {
		synMan.addInterNodeSynapse(
				firingNodeId, firingNeuronId, 
				burningNodeId, burningNeuronId, 
				amplitude,lenght);
	}
	
	public Long getN(){
		return r.getN();
	}
	
	public Long getExcitatory(){
		return r.getExcitatory();
	}
	
	public Long getInhibithory(){
		return r.getInhibithory();
	}

	public boolean hasExternalInput() {
		return r.hasExternalInput();
	}
	
	public Integer getExternalInputs(){
		return r.getExternalInputs();
	}
	
	/**
	 * Plasticity Rule:
	 * Multiplicative Learning Rule using STDP (soft-bound) Spike time depending plasticity
	 * 
	 *  LTP: Pw = Pwold + (pwmax - Pwold)*Etap*(-delta/taup)
   	 *	LTD: Pw = Pwold - Pwold*Etam*(delta/taum)
     *	with delta = tpost - tpre
  
 	 *	NB: in the case of LTD, tpost represents the burning neuron last burning 
     *	time, whereas tpre is the current "tempo".
 	 *	This rule is applied for only exc-exc intermolule connections   
 
	 * @param spikingNeuronId
	 * @param presentNeuronId
	 * @param currentTime
	 */
	
	private void plast(Long spikingNeuronId, Synapse syn, Double currentTime){
		if (syn.getBurning()==null)
			return;
		Afferent aff = affMan.popNoLtp(syn);
		Double delta;
		//LTP - Long Term Potentiation 
		if (aff!=null){
			delta = currentTime - aff.getLastFireTime();
			if (delta>0)
				aff.setOldPostsynapticWeight( aff.getOldPostSynapticWeight() + (affMan.getPwMax()-aff.getOldPostSynapticWeight()) * affMan.getEtap() * 
						Math.exp(- delta / affMan.getTaup() ) );
			synMan.setPostSynapticWeight(syn, aff.getOldPostSynapticWeight());
			aff.setLtpFlag(false);
			affMan.addAfferent(aff);
		}
	}
	
	/**
	 * Search for burning neuron connected to the firing neuron.
	 * 
	 * @param spikingNeuronId
	 * @param currentTime
	 */
	
	private void runBuriningNeurons(Long firingNeuronId, Double currentTime){
		++debugCases[7];
		ArrayList <Synapse> synapses = synMan.getFiringNeuronConnections(firingNeuronId);
		ArrayList <Synapse> interNodeSynapses = synMan.getFiringNeuronInterNodeConnections(firingNeuronId);
		debprintln("running next burning neuron...\nsynapses:"+synapses.size());
		if (r.isExternalInput(firingNeuronId)){
			for (long i=0; i<r.getN();++i)
				burnNeuron(new Synapse(r.getId(), firingNeuronId, r.getId(), i, 0.1,true), currentTime, currentTime, true);
			return;
		}
		for (int i=0; i<synapses.size();++i){
			debprintln("connection:"+synapses.get(i));
			++debugCases[8];
			//this is an inter-node synapse, the burning node must deal with this spike
			if (!(synapses.get(i).getDendriteNodeId().equals(r.getId()))){
				continue;
			}
			burnNeuron(synapses.get(i), currentTime, currentTime, false);
		}
		for (int i=0; i<interNodeSynapses.size();++i){
			addInterNodeSpike(interNodeSynapses.get(i), currentTime);
		}
	}
	
	public void burnNeuron(Synapse s, Double burnTime, Double fireTime, Boolean fromExternalSource){
		
		Double tmp, dsxNumerator, dsxDenominator, riseTermXFactor, oldSx;
		int arp;
		//distinguish cases of no initial network activity : already activated
		arp=(nnMan.getLastFiringTime(s.getBurning()).equals(Constants.TIME_TO_FIRE_DEF_VAL))?0:1;
		debActiveprintln("burning:"+s.getBurning()+" last firing:"+nnMan.getLastFiringTime(s.getBurning())+", arp:"+arp);		
		//absolutely refractory period check
		if (burnTime>=( (nnMan.getLastFiringTime(s.getBurning())+nnMan.getT_arp()) *arp) ){			
			++debugCases[9];
			tmp=nnMan.getState(s.getBurning());
			//passive state linear decay
			if (tmp<nnMan.getSpikingThr()){
				Double decay= (nnMan.getLinearDecayD()*(burnTime-(nnMan.getLastBurningTime(s.getBurning())/* *arp*/) ));
				nnMan.setState(s.getBurning(), 
						tmp-decay);
				if (nnMan.getState(s.getBurning())<0.0)
					nnMan.setState(s.getBurning(), 0.0);
			}
			//BURNING NEURON
			Double sx = nnMan.getState(s.getBurning());
			oldSx=sx;
			//step in state
			Double sy = synMan.getPostSynapticWeight(s)*nnMan.getPreSynapticWeight(s.getFiring());
			debActiveprintln("sy:"+sy+", Postsynaptic W:"+synMan.getPostSynapticWeight(s)+
					", presynaptic W:"+	nnMan.getPreSynapticWeight(s.getFiring()));
//			Double oldStateDebug = nnMan.getState(s.getBurning());
			Double oldTime2Fire = nnMan.getTimeToFire(s.getBurning());
			// UPDATING List of Active Neurons
			// case of passive neuron
			if (nnMan.getTimeToFire(s.getBurning()).equals(Constants.TIME_TO_FIRE_DEF_VAL)){
				++debugCases[10];
				oldSx=sx;
				sx = ((sx+sy)<0)?0:sx+sy;
				nnMan.setState(s.getBurning(), sx);
				//passive to active
				if (sx>=nnMan.getSpikingThr()){
					++debugCases[26];
//					debActiveprintln("passive 2 active node:"+s.getBurning()+", old state:"+oldStateDebug
//							+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr());
					nnMan.setTimeToFire(s.getBurning(), burnTime+ 1.0/(sx-1));
					StatisticsCollector.collectPassive2active();
					nnMan.addActiveNeuron(s.getBurning(), nnMan.getTimeToFire(s.getBurning()));
				}
				else{
//					debActiveprintln("passive 2 passive node:"+s.getBurning()+", old state:"+oldStateDebug
//							+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr()+", sy:"+sy);
					StatisticsCollector.collectPassive();
				}
			}
			//case of active neuron
			else{
				++debugCases[11];
				if (nnMan.getTimeToFire(s.getBurning())==0.0)
					nnMan.setTimeToFire(s.getBurning(), Constants.EPSILON);
				//sx=(1.0+(1.0/(rnMan.getTimeToFire(s.getBurning())-burnTime) ) );
				if (sx>=nnMan.getSpikingThr()){
					++debugCases[12];
					nnMan.removeActiveNeuron(s.getBurning());
					if ( (burnTime < nnMan.getTimeToFire(s.getBurning()) )
							&& (!nnMan.getLastBurningTime(s.getBurning()).equals(Constants.BURNING_TIME_DEF_VAL)) ){
						//Rise Term
						riseTermXFactor=(burnTime==nnMan.getLastBurningTime(s.getBurning()))?
								Constants.EPSILON : (burnTime-nnMan.getLastBurningTime(s.getBurning()));
						tmp=(sx-1)*riseTermXFactor;
						dsxNumerator = (sx-1)*tmp;
						dsxDenominator= 1.0-tmp;
						sx+=(dsxNumerator/dsxDenominator);
					}
					oldSx=sx;
					sx += sy;
					nnMan.setState(s.getBurning(),sx);
					//active to passive
					if (sx<nnMan.getSpikingThr()){
						nnMan.removeActiveNeuron(s.getBurning());
						nnMan.resetTimeToFire(s.getBurning());
//						debActiveprintln("active 2 passive node:"+s.getBurning()+", old state:"+oldStateDebug
//								+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr());
						StatisticsCollector.collectActive2passive();
					}
					else{
						//updating firing delay
						nnMan.setTimeToFire(s.getBurning(), burnTime + 1.0/(sx-1));
						nnMan.setState(s.getBurning(), sx);
						nnMan.addActiveNeuron(s.getBurning(), nnMan.getTimeToFire(s.getBurning()));
//						debActiveprintln("active 2 active node:"+s.getBurning()+", old state:"+oldStateDebug
//								+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr());
						StatisticsCollector.collectActive();
					}
					//active to passive
					if (sx<0){
						++debugCases[19];
						sx=0.0;
						oldSx=sx;
						nnMan.setState(s.getBurning(),sx);
						nnMan.removeActiveNeuron(s.getBurning());
						nnMan.resetTimeToFire(s.getBurning());
//						debActiveprintln("active 2 passive node:"+s.getBurning()+", old state:"+oldStateDebug
//								+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr());
						StatisticsCollector.collectActive2passive();
					}
				}
				else{
					++debugCases[20];
					oldSx=sx;
					nnMan.removeActiveNeuron(s.getBurning());
					nnMan.resetTimeToFire(s.getBurning());
//					debActiveprintln("active 2 passive node:"+s.getBurning()+", old state:"+oldStateDebug
//							+ ", new state:"+sx+", threshold:"+nnMan.getSpikingThr()+", oldTime2Fire:"+oldTime2Fire);
					StatisticsCollector.collectActive2passive();
				}
			} //end of case of active neuron
			nnMan.setLastBurningTime(s.getBurning(), burnTime);
			// collecting the spike
			StatisticsCollector.collectBurnSpike(
					s,
					burnTime,
					fromExternalSource, 
					oldSx, 
					sy, 
					synMan.getPostSynapticWeight(s),
					nnMan.getPreSynapticWeight(s.getFiring()), 
					nnMan.getTimeToFire(s.getBurning()),
					fireTime);
		}
		else{
			// collecting the spike
			StatisticsCollector.collectBurnSpike(
					s,
					burnTime,fromExternalSource, 
					null, 
					null, 
					synMan.getPostSynapticWeight(s),
					nnMan.getPreSynapticWeight(s.getFiring()), 
					nnMan.getTimeToFire(s.getBurning()),
					fireTime);
		}
	}
	
	
	public void burnInterNodeSpike(InterNodeSpike irs){
		if (irs.getBurnTime()>=currentTime){
			interNodeBurningSpikes.add(new InterNodeBurningSpike(irs, irs.getBurnTime()));
		}
		else{
			StatisticsCollector.collectMissedFire(irs.getAxonalDelay());
			println("missed fire at time:"+irs.getBurnTime()+", axonal del:"+irs.getAxonalDelay()+", current time:"+currentTime+", syn:"+irs.getSyn());
		}
	}
	
	private void initExtInput(){
		println("initializing external input...");
		for (int j=0; j<r.getExternalInputs();++j){
			nnMan.extInputReset(r.getN()+j,0.0);
		}
		println("external input initialization done.");
	}
	
	
	//=======================================  thread functions  =======================================
	
	public void runNewSplit(double newStopTime){
		stopTime = newStopTime;
		lock.lock();
		newTimeSplitCond.signal();
		lock.unlock();
	}

	private void completed(){
		double oldStopTime=stopTime;
		if (rMan.splitComplete(getNodeId())){
			lock.lock();
			try {
				newTimeSplitCond.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.unlock();
		}
		else{
			//int pollings=0;
			while (keepRunning && (stopTime<=oldStopTime) ){
				try {
			//		++pollings;
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
			if (rMan != null)
				System.out.println(TAG+getNodeId()+"/"+(rMan.getNodeThreadsNum()-1)+"] "+s);
			else 
				System.out.println(TAG+getNodeId()+"/???]"+s);
		}
	}
	
	private void debprintln(String s){
		if (verbose&&debug)
			System.out.println(TAG+getNodeId()+"/"+(rMan.getNodeThreadsNum()-1)+" [debug] ] "+s);
	}
	
	private void leveldebprintln(String s, int level){
		if (this.debug_level>=level)
			debprintln(s);
	}
	
	private void debActiveprintln(String s){
		if (verbose&&debug&&activePassiveDebug)
			System.out.println(TAG+getNodeId()+"/"+(rMan.getNodeThreadsNum()-1)+"[debug] ] "+s);
	}

	public void printDebug(){
		println("debug variables:");
		for(int i=0; i<debNum;++i)
			println("\t"+i+". "+debugCases[i]);
		System.out.println();
	}

	
	
	
	


}

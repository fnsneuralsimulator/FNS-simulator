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
//import spiking.node.plasticity.Afferent;
//import spiking.node.plasticity.AfferentManager;
import utils.constants.Constants;
import utils.tools.NiceNode;
import utils.tools.NiceQueue;
import utils.math.FastMath;
import utils.statistics.StatisticsCollector;

public class NodeThread extends Thread{
	
	private final static String TAG = "[Node Thread ";
	private final static Boolean verbose = true;
    private final static Boolean LIF = false;

	private Node n;
	private NodeNeuronsManager nnMan;
	private NodesManager nMan;
	private SynapsesManager synMan;
	
	private Boolean plasticity;
	//queues map enqueues the spikes sended by a specific firing neuron to a specific burning neuron
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
	private PriorityQueue<InterNodeBurningSpike> interNodeBurningSpikes=new PriorityQueue<InterNodeBurningSpike>();
	private PriorityQueue<FixedBurnSpike> burningQueueSpikes=new PriorityQueue<FixedBurnSpike>();
	private FastMath fm = new FastMath();
	private Double debugMaxWPDiff=0.0;
	private Boolean do_fast;
	long[] times= new long[10];
	private StatisticsCollector sc;
	private int toremDebug=0;
	
	public NodeThread(
			NodesManager nMan, 
			Integer id, 
			Long n, 
			Double excitProportion,
			Integer k, 
			Double prew,
			Integer Bn,
			Double IBI,			
			Double d, 
			Double ld, 
			Double kr,
			Double mu_w,
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
			Boolean do_fast){
		this.n=new Node(
				id,
				n, 
				excitProportion,
				mu_w,
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
				d, 
				ld, 
				kr, 
				w_pre_exc, 
				w_pre_inh, 
				externalPresynapticDefVal, 
				avgNeuronalSignalSpeed,
				do_fast);
	}
	
	public NodeThread(
			NodesManager rMan, 
			Integer id, 
			Long n, 
			Integer externalInputs, 
			int externalInputType,
			Double externalInputsTimeOffset,
			int timeStep, 
			Double fireRate,  
			int fireDuration,
			Double externalAmplitude,
			Double excitRatio,
			Integer k, 
			Double prew, 
			Integer Bn,
			Double IBI,
			Double c, 
			Double D, 
			Double t_arp,
			Double mu_w,
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
			Boolean do_fast){
		this.n=new Node(id,
				n,
				externalInputs,
				externalInputType,
				externalInputsTimeOffset,
				timeStep,
				fireRate, 
				fireDuration,
				externalAmplitude, 
				excitRatio,
				mu_w,
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
		init(rMan, 
				c, 
				D, 
				t_arp, 
				w_pre_exc, 
				w_pre_inh, 
				externalPresynapticDefVal, 
				avgNeuronalSignalSpeed,
				do_fast);
		
	}
	
	public void init(
			NodesManager nMan, 
			Double c, 
			Double D, 
			Double t_arp, 
			Double excitatoryPresynapticDefVal, 
			Double inhibithoryPresynapticDefVal, 
			Double externalPresynapticDefVal, 
			Double avgNeuronalSignalSpeed,
			Boolean do_fast){
		sc=nMan.getStatisticsCollector();
		queuesMap = new HashMap<Synapse, NiceQueue>();
		internodeFires = new ArrayList<InterNodeSpike>();
		nnMan = new NodeNeuronsManager(n, 
				c, 
				D, 
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
		println("D: "+D);
		println("t arp: "+t_arp);
		this.do_fast=do_fast;
	}
	
	
	public void run(){
		NiceNode minFiringNeuron;
		Long firingNeuronId=null;
		Double spikeTime=null;
		Double tmpMinFiringTime;
		Double tmpMinInterNodeBurningTime;
		Double minFixedBurnTime;
		InterNodeBurningSpike tmpInterNodeBurningSpike;
		FixedBurnSpike tmpFixedBurnSpike;
		int fires=0;
		Boolean stopped=false;	
		// debug variable for last event type: 
		// 0 --> init
		// 1 --> fire
		// 2 --> fixed burn
		// 3 --> inter node burn
		int debug_last_event=0;
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
						tmpMinInterNodeBurningTime=tmpInterNodeBurningSpike.getTimeToBurn();
						if ((tmpMinInterNodeBurningTime!=null)&&(tmpMinInterNodeBurningTime<stopTime)){
							if (tmpMinFiringTime!=null){
								if ((tmpMinInterNodeBurningTime<tmpMinFiringTime) &&
										(tmpMinInterNodeBurningTime<minFixedBurnTime)){
									InterNodeSpike irs=interNodeBurningSpikes.poll().getInterNodeSpike();
									if (tmpMinInterNodeBurningTime<currentTime) {
										println("internode burning:"+tmpMinInterNodeBurningTime+" min FixedBurn:"+minFixedBurnTime+" tmpMinFiringTime:"+tmpMinFiringTime);
										println("torem - current time last update:"+debug_last_event+", axonal del:"+tmpInterNodeBurningSpike.getInterNodeSpike().getAxonalDelay()+", current time:"+currentTime+", syn:"+tmpInterNodeBurningSpike.getInterNodeSpike().getSyn());
										if (interNodeBurningSpikes.peek()!=null)
											println("polled:"+irs.getBurnTime()+" peeked:"+interNodeBurningSpikes.peek().getTimeToBurn()+"\n");
//										println("torem - internode burning time:"+tmpMinInterNodeBurningTime+" current:"+currentTime);
									}
									if ((interNodeBurningSpikes.peek()!=null)&&(irs.getBurnTime()>interNodeBurningSpikes.peek().getTimeToBurn()))
										println("polled:"+irs.getBurnTime()+" peeked:"+interNodeBurningSpikes.peek().getTimeToBurn()+" syn:"+tmpInterNodeBurningSpike.getInterNodeSpike().getSyn());
									debug_last_event=35;
//									if (currentTime>tmpMinInterNodeBurningTime)
//										println("\n\n\n=========================>torem:"+debug_last_event);
									currentTime=tmpMinInterNodeBurningTime;
									burnNeuron(irs.getSyn(), irs.getBurnTime(), irs.getFireTime(), false);
									continue;
								}
							}
							else if (tmpMinInterNodeBurningTime<minFixedBurnTime){
								InterNodeSpike irs=interNodeBurningSpikes.poll().getInterNodeSpike();
								debug_last_event=3;
//								if (currentTime>tmpMinInterNodeBurningTime)
//									println("\n\n\n=========================>torem:"+debug_last_event);
								currentTime=tmpMinInterNodeBurningTime;
								burnNeuron(irs.getSyn(), irs.getBurnTime(), irs.getFireTime(), false);
								continue;
							}
						}
					}
					// case of first arrival of bursting queue spike to be burn
					if (minFixedBurnTime<stopTime) {
						if (minFixedBurnTime<currentTime) {
							println("min FixedBurn:"+
                                    minFixedBurnTime+
                                    " tmpMinFiringTime:"+
                                    tmpMinFiringTime);
							println("torem - current time last update:"+
                                    debug_last_event+
                                    ", axonal del:"+
                                    tmpInterNodeBurningSpike.getInterNodeSpike().getAxonalDelay()+
                                    ", current time:"+
                                    currentTime+
                                    ", syn:"+
                                    tmpInterNodeBurningSpike.getInterNodeSpike().getSyn());
						}					
						if (tmpMinFiringTime!=null){
							if (minFixedBurnTime<tmpMinFiringTime) {
								FixedBurnSpike fixedBurnSpike = burningQueueSpikes.poll();
								debug_last_event=2;
								if (tmpFixedBurnSpike.getBurnTime()!=fixedBurnSpike.getBurnTime()) {
									println("tada!:"+tmpFixedBurnSpike.getBurnTime()+"!="+fixedBurnSpike.getBurnTime());
									System.exit(1);
								}
//								if (currentTime>minFixedBurnTime)
//									println("\n\n\n=========================>torem:"+debug_last_event);
								currentTime=minFixedBurnTime;
								burnNeuron(fixedBurnSpike.getSyn(),fixedBurnSpike.getBurnTime(),fixedBurnSpike.getFireTime(),false);
								continue;
							}
						}
						else if (minFixedBurnTime<Double.MAX_VALUE) {
							FixedBurnSpike fixedBurnSpike = burningQueueSpikes.poll();
							debug_last_event=25;
//							if (currentTime>minFixedBurnTime)
//								println("\n\n\n=========================>torem:"+debug_last_event);
							currentTime=minFixedBurnTime;
							burnNeuron(
                                    fixedBurnSpike.getSyn(),
                                    fixedBurnSpike.getBurnTime(),
                                    fixedBurnSpike.getFireTime(),
                                    false);
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
					//get the next neuron ready to fire in the list of the active neurons
					debprintln("\n\ngetting next firing neuron...");
					minFiringNeuron=nnMan.getNextFiringNeuron();
					if (minFiringNeuron==null){
						stopped=true;
						break;
					}
					debprintln("got next firing neuron.");
					firingNeuronId=minFiringNeuron.fn;
					spikeTime=minFiringNeuron.tf;
					if (spikeTime>stopTime){
						stopped=true;
						break;
					}
				}
				else{
//					if (spikeTime>stopTime){
//						stopped=true;
//						break;
//					}
//					else {
//						stopped=false;
//					}
					break;
				}
				//case of first firing of a burst
				//time update
				int old_debug=debug_last_event;
				debug_last_event=1;
				if (currentTime>spikeTime)
					println(
                            "\n=========================>torem:"+
                            old_debug+
                            "-->"+
                            debug_last_event+
                            "  current time:"+
                            currentTime+
                            " new time:"+
                            spikeTime);
				currentTime = spikeTime;
				sc.collectFireSpike(
						n.getId(), 
						firingNeuronId, 
						currentTime, 
						nMan.getMaxN(), 
						nMan.getCompressionFactor(),
						(firingNeuronId<n.getExcitatory()),
						(firingNeuronId>=n.getN()) );
				//firing spikes detecting and storing
				if (firingNeuronId<n.getN()){
					//State resetting to passive mode
					nnMan.resetState(firingNeuronId);
					nnMan.resetTimeToFire(firingNeuronId);
					for (int i=1; i<n.getBn();++i)
						sc.collectFireSpike(
								n.getId(), 
								firingNeuronId, 
								currentTime+i*n.getIBI(), 
								nMan.getMaxN(), 
								nMan.getCompressionFactor(),
								(firingNeuronId<n.getExcitatory()),
								(firingNeuronId>=n.getN()) );
				}
				// last firing time for neuron
				nnMan.setLastFiringTime(firingNeuronId, currentTime);
				if (firingNeuronId>=n.getN()){
					debprintln("external input fire resetting...");
					//ext time-to-fire resetting
					nnMan.resetTimeToFire(firingNeuronId);
					//external routine
					nnMan.extInputReset(firingNeuronId, currentTime);
					debprintln("external input fire reset.");
				}
				//search for burning neurons connected to neuron_id
				makeNeuronFire(firingNeuronId, currentTime);
			}
			completed();
			stopped=false;
		}
		closeDB();		
	}
	
	private void closeDB(){
		n.closeDb();
	}
	
	private void addInterNodeFire(Synapse syn, Double fireTime){
		Double axonalDelay=synMan.getAxDelay(syn);
		internodeFires.add(new InterNodeSpike(syn, fireTime+axonalDelay,fireTime,axonalDelay));
	}
	
	public Boolean hasInterNodeSpikes(){
		if (internodeFires.size()>0)
			return true;
		return false;
	}
	
	
	/**
	 * 
	 * @return the list of internode spikes and clean it
	 */
	public ArrayList<InterNodeSpike> pullInternodeFires() {
		ArrayList<InterNodeSpike> retval=internodeFires;
		internodeFires=new ArrayList<InterNodeSpike>();
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
		return n.getId();
	}
	
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
	
//	private void plast(Long spikingNeuronId, Synapse syn, Double currentTime){
//		if (syn.getBurning()==null)
//			return;
//		Afferent aff = affMan.popNoLtp(syn);
//		Double delta;
//		//LTP - Long Term Potentiation 
//		if (aff!=null){
//			delta = currentTime - aff.getLastFireTime();
//			if (delta>0)
//				aff.setOldPostsynapticWeight( aff.getOldPostSynapticWeight() + 
//						(affMan.getPwMax()-aff.getOldPostSynapticWeight()) * affMan.getEtap() * 
//						Math.exp(- delta / affMan.getTaup() ) );
//			synMan.setPostSynapticWeight(syn, aff.getOldPostSynapticWeight());
//			aff.setLtpFlag(false);
//			affMan.addAfferent(aff);
//		}
//	}
	
	/**
	 * Plasticity rule for firing events.
	 * Update postsynaptic weight, increasing it according to the delta between the firing time 
	 * and the last burning time of the firing neuron.
	 * 
	 * @param syn
	 * @param lastBurningTime
	 * @param fireTime
	 */
	private void fire_ltp(Synapse syn, Double fireTime){
		if (!plasticity)
			return;
		if (syn.getFiring()==null)
			return;
		Long firingNeuronId = syn.getFiring();
		ArrayList <Synapse> synapses = synMan.getFiringNeuronConnections(firingNeuronId);
		ArrayList <Synapse> interNodeSynapses = synMan.getFiringNeuronInterNodeConnections(firingNeuronId);
		for(int i=0; i<synapses.size();++i){
			if (synapses.get(i).getLastBurningTime()==null)
				continue;
			Double delta;
			delta=fireTime-synapses.get(i).getLastBurningTime();
			if (delta < n.getTo()){
//				Double wp=synMan.getPostSynapticWeight(synapses.get(i)); 
				Double wp=synapses.get(i).getPostSynapticWeight();
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
			if (delta < n.getTo()){
//				Double wp=synMan.getPostSynapticWeight(interNodeSynapses.get(i));
				Double wp=interNodeSynapses.get(i).getPostSynapticWeight();
				wp+=do_fast?
						(n.getPwMax()-wp)*n.getEtap()*fm.fastexp(-delta/n.getTaup()):
							(n.getPwMax()-wp)*n.getEtap()*Math.exp(-delta/n.getTaup());
				synMan.setIntraNodePostSynapticWeight(interNodeSynapses.get(i),wp);
			}
		}
	}

	/**
	 * Plasticity rule for burning events.
	 * Update postsynaptic weight, decreasing it according to the delta between the burning time 
	 * and the last firing time of the burning neuron.
	 * @param syn
	 * @param lastBurningTime
	 * @param fireTime
	 */
	private void burning_ltd(Synapse syn, Double burningTime, Double lastFiringTime){
		if (!plasticity)
			return; 
		if (syn.getBurning()==null)
			return;
		syn.setLastBurningTime(burningTime);
		Double delta;
		delta=burningTime-lastFiringTime;
		if (delta < n.getTo()){
//			Double wp=synMan.getPostSynapticWeight(syn);
			Double wp=syn.getPostSynapticWeight();
			wp -= do_fast?
					n.getEtam()*fm.fastexp(-delta/n.getTaum()):
						n.getEtam()*Math.exp(-delta/n.getTaum());
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
		ArrayList <Synapse> synapses = synMan.getFiringNeuronConnections(firingNeuronId);
		ArrayList <Synapse> interNodeSynapses = synMan.getFiringNeuronInterNodeConnections(firingNeuronId);
		debprintln("running next burning neuron...\nsynapses:"+synapses.size());
		if (n.isExternalInput(firingNeuronId)){
			burnNeuron(
					new Synapse(
							n.getId(), 
							firingNeuronId, 
							n.getId(), 
							firingNeuronId%n.getN(), 
							0.1,
//							n.getMu_w(),
							1.0,
							n.getExternalAmplitude(),
							true,
							false), 
					currentTime, 
					currentTime, 
					true);
			return;
		}
		for (int i=0; i<synapses.size();++i){
			fire_ltp(
					synapses.get(i), 
					currentTime);
			//this is an inter-node synapse, the burning node must deal with this spike
			if (!(synapses.get(i).getDendriteNodeId().equals(n.getId()))){
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
		Double tmp, dsxNumerator, dsxDenominator, riseTermXFactor, oldSx;
		int arp;
		//distinguish cases of no initial network activity : already activated
		arp=(nnMan.getLastFiringTime(s.getBurning()).equals(Constants.TIME_TO_FIRE_DEF_VAL))?0:1;
		debActiveprintln("burning:"+s.getBurning()+" last firing:"+nnMan.getLastFiringTime(s.getBurning())+", arp:"+arp);		
		//absolutely refractory period check
		if (burnTime>=(( 
				nnMan.getLastFiringTime(s.getBurning())+
				nnMan.getT_arp()+
				((n.getBn()-1)*n.getIBI()))
				*arp) ){
			long startTime = System.currentTimeMillis();
			
			burning_ltd(
                    s, 
                    burnTime, 
                    nnMan.getLastFiringTime(s.getBurning()));
			tmp=nnMan.getState(s.getBurning());
			//passive state linear decay
			if (tmp<nnMan.getSpikingThr()){
				Double decay= (
                        nnMan.getLinearDecayD()*
                        (burnTime-
                                (nnMan.getLastBurningTime(
                                        s.getBurning())/* *arp*/)));
				nnMan.setState(
                        s.getBurning(), 
						tmp-decay);
				if (nnMan.getState(s.getBurning())<0.0)
					nnMan.setState(s.getBurning(), 0.0);
			}
			times[0]+=System.currentTimeMillis()-startTime;
			startTime = System.currentTimeMillis();
			
			//BURNING NEURON
			Double sx = nnMan.getState(s.getBurning());
			oldSx=sx;
			//step in state
//			Double sy = synMan.getPostSynapticWeight(s)*nnMan.getPreSynapticWeight(s.getFiring());
			Double sy = s.getPostSynapticWeight()*s.getPreSynapticWeight();
			// UPDATING List of Active Neurons
			// case of passive neuron
			if (nnMan.getTimeToFire(s.getBurning()).equals(Constants.TIME_TO_FIRE_DEF_VAL)){
				oldSx=sx;
				sx = ((sx+sy)<0)?0:sx+sy;
				nnMan.setState(s.getBurning(), sx);
				//passive to active
				if (sx>=nnMan.getSpikingThr()){
                    Double activeTransitionDelay=LIF?Constants.EPSILON:(1.0/(sx-1));
					//nnMan.setTimeToFire(s.getBurning(), burnTime+ 1.0/(sx-1));
					nnMan.setTimeToFire(s.getBurning(), burnTime+ activeTransitionDelay);
					sc.collectPassive2active();
					nnMan.addActiveNeuron(
                            s.getBurning(), 
                            nnMan.getTimeToFire(s.getBurning()), 
                            currentTime, 
                            2);
				}
				else{
					sc.collectPassive();
				}
				times[1]+=System.currentTimeMillis()-startTime;
				
			}
			//case of active neuron
			else{
				if (nnMan.getTimeToFire(s.getBurning())==0.0)
					nnMan.setTimeToFire(s.getBurning(), Constants.EPSILON);
				if (sx>=nnMan.getSpikingThr()){
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
						sc.collectActive2passive();
					}
					else{
						//updating firing delay
						nnMan.setTimeToFire(s.getBurning(), burnTime + 1.0/(sx-1));
						nnMan.setState(s.getBurning(), sx);
						nnMan.addActiveNeuron(
                                s.getBurning(), 
                                nnMan.getTimeToFire(s.getBurning()), 
                                currentTime, 
                                3);
						sc.collectActive();
					}
					//active to passive
					if (sx<0){
						sx=0.0;
						oldSx=sx;
						nnMan.setState(s.getBurning(),sx);
						nnMan.removeActiveNeuron(s.getBurning());
						nnMan.resetTimeToFire(s.getBurning());
						sc.collectActive2passive();
					}
				}
				else{
					oldSx=sx;
					nnMan.removeActiveNeuron(s.getBurning());
					nnMan.resetTimeToFire(s.getBurning());
					sc.collectActive2passive();
				}
				times[2]+=System.currentTimeMillis()-startTime;
			}
			startTime = System.currentTimeMillis();
			
			//end of case of active neuron
			nnMan.setLastBurningTime(s.getBurning(), burnTime);
			times[4]+=System.currentTimeMillis()-startTime;
			// collecting the spike
			sc.collectBurnSpike(
					s,
					burnTime,
					fromExternalInput, 
					oldSx, 
					sy, 
					s.getPostSynapticWeight(),
//					synMan.getPostSynapticWeight(s),
//					nnMan.getPreSynapticWeight(s.getFiring()), 
					s.getPreSynapticWeight(), 
					nnMan.getTimeToFire(s.getBurning()),
					fireTime);
			times[3]+=System.currentTimeMillis()-startTime;
		}
		else{
			// collecting the spike
			sc.collectBurnSpike(
					s,
					burnTime,
					fromExternalInput, 
					null, 
					null, 
					s.getPostSynapticWeight(),
					s.getPreSynapticWeight(), 
					nnMan.getTimeToFire(s.getBurning()),
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
//		for (int i=0; i<5;++i)
//			println("times["+i+"]: "+times[i]);
	}

	
	
	
	
	


}

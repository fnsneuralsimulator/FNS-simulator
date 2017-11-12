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


package spiking.simulator;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
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

public class SpikingNeuralSimulator extends Thread{
	private final static String TAG = "[Spiking Neural Simulator] ";
	private final static Boolean verbose = true;
	private Boolean debug = false;
	private Boolean activePassiveDebug = false;
	private int debug_level = 3;
	private Boolean plasticity = true;
	private NodesManager nMan;
	Double minQueuesValue = Double.MAX_VALUE;
	NiceQueue minQueue=null;
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
	//standard parameters for testing, never use for other purposes
	public static final Double excitProportion4Test = 0.8;
	public static final Integer k4Test = 20;
	public static final Double prew4Test = 0.5;
	public static final Double d4Test = 0.04;
	public static final Double ld4Test = 0.001; 
	public static final Double kr4Test = 0.3;
	public static final Double internalAmplitude4Test = 1.0;
	private static final Double epsilon = 0.00000001;
	// Ne_en_ratio normalization factor
	private Double Ne_en_ratioNormFactor=0.0;
	long[] times= new long[10];
	// the coefficient for which multiply the BOP to obtain a cycle: 
	// 		1. if it is less than 1, than we fall into the optimistic simulation
	//		2. if it is greater or equal to 1, than we have a conservative behavior
	private static final Double bop_to_cycle_factor=1.0;
	
	public SpikingNeuralSimulator (){
		nMan = new NodesManager(this);
		StatisticsCollector.setSimulatedTime(total_time);
//		if (plasticity)
//			affMan = new AfferentManager();
	}
	
	private void addNodeThread(NodeThread node){
		nMan.addNodeThread(node);
	}
	
	public void _addNodeThread(Integer id, Long n, Integer externalInputs, int externalInputType, int timeStep, double fireRate, int fireDuration, Double externalAmplitude){
		addNodeThread(
				new NodeThread(
						nMan, id, n, 
						externalInputs,
						externalInputType, timeStep, 
						fireRate,
						fireDuration,
						externalAmplitude,
						excitProportion4Test, k4Test, 
						prew4Test, internalAmplitude4Test, d4Test, ld4Test, kr4Test, 
						Constants.EXCITATORY_PRESYNAPTIC_DEF_VAL, 
						Constants.INHIBITORY_PRESYNAPTIC_DEF_VAL, 
						Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
						plasticity, avgNeuronalSignalSpeed)
				);
	}
	
	public void _addNodeThread(Integer id, Long n){
		addNodeThread(
				new NodeThread(nMan, id, n, excitProportion4Test, k4Test, prew4Test,internalAmplitude4Test, d4Test,
						ld4Test, kr4Test, Constants.EXCITATORY_PRESYNAPTIC_DEF_VAL, Constants.INHIBITORY_PRESYNAPTIC_DEF_VAL, 
						Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, plasticity, avgNeuronalSignalSpeed));
	}

	public void init(){
		if (nMan.getnSms()<=0){
			debprintln(" no node added to the simulator");
			return;
		}
		initialized=true;
		String minTractLengthStr=(nMan.getMinTractLength()!=NodesManager.MAX_TRACT_LENGTH)?
				(""+nMan.getMinTractLength()):" there are no connection betwen regions.";
		println("min tract length:"+minTractLengthStr);
		println("avg neuronal signal speed:"+avgNeuronalSignalSpeed);
		cycle_time=(nMan.getMinTractLength()+epsilon)/(avgNeuronalSignalSpeed*bop_to_cycle_factor);
		println("cycle time:"+cycle_time);
	}

	private void setTotalTime(double total_time){
		this.total_time=total_time;
		StatisticsCollector.setSimulatedTime(total_time);
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
	 */
	public void runNewSplit(double newStopTime){
		double stopTime=(newStopTime>total_time)?total_time:newStopTime;
		println("running new splits with new stop time:"+stopTime+" - cycle time:"+cycle_time);
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
	}

	/**
	 * This function performs the spike routine for a fixed number of fires (nstop).
	 * 
	 * The main characters are:
	 * 		1. the active neurons: neurons which are over the spike threshold and are going to "shoot"
	 * 								at a certain time. This fire time may be deleted by effect of the arrival of
	 * 								a negative input or anticipated by the arrival of a new excitatory signal.
	 * 		2. the synapsys queues: synapsys are modeled by a queue for each couple of connected neurons. 
	 * 								Each queue holds the signals generated by the source neuron but not still "delivered"
	 * 								t the target neuron.
	 * 								
	 */
	
	public void run(){
		println("plasticity:"+plasticity);
		long startTime = System.currentTimeMillis();
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
		println("end of simulator run, "+(System.currentTimeMillis()-startTime)+" ms elapsed.");
		println("init phases:\n\t conn pckg read:\t\t"+times[0]+"\n\t config file read:\t\t"+times[1]+
				"\n\t node init:\t\t\t"+times[2]+"\n\t inter-nodes connections init:\t"+times[3]+
				"\n\t simulator init:\t\t"+times[4]);
		println("min tract length:"+nMan.getMinTractLength());
		println("avg neuronal signal speed:"+avgNeuronalSignalSpeed);
		println("cycle time:"+cycle_time);
		println("total inter node axonal connections:"+nMan.getTotalInterNodeConnectionsNumber());
	}
	
	
	public Boolean splitComplete(Integer nodeId){
		if (completing>0)
			--completing;
		else{
			++splitCount;
			if (currentTime>=total_time)
				killAll();
			else{
				printBreakLine();
				currentTime+=cycle_time;
				runNewSplit(currentTime);
				completing=nMan.getNodeThreadsNum()-1;
			}
			return false;
		}
		return true;
	}
 
	public void initFromConfigFileAndConnectivityPackage(String configPath, String connPkgPath) throws BadParametersException{
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;
		System.out.println("reading connectivity package file:"+connPkgPath);
		ConnectivityPackageManager cpm = new ConnectivityPackageManager();
		cpm.readConnectivityPackage(connPkgPath);
		StatisticsCollector.setMinMaxNe_en_ratios(cpm.getMinNe_en_ratio(), cpm.getMaxNe_en_ratio());
		ArrayList<NodesInterconnection> conns = cpm.getInterNodeConnections();
		times[0]=System.currentTimeMillis()-lastTime;
		lastTime+=times[0];
		System.out.println("reading config file:"+configPath);
		SpikingSimulatorCfg ssc = SpikingConfigManager.readConfigFile(configPath);
		setTotalTime(new Double(ssc.getStop()));
//		NeuManCfg nmcfg = ssc.getNeuron_manager();
		ArrayList<NodeCfg> nodeCs =  ssc.getNodes();
		plasticity=ssc.getPlasticity();
		avgNeuronalSignalSpeed=ssc.getAvg_neuronal_signal_speed();
		times[1]=System.currentTimeMillis()-lastTime;
		lastTime+=times[1];
		System.out.println("creating and adding nodes...\n");
		NodeCfg tmp;
		Long tmpN;
		Integer  tmpK, tmpExternal;
		Double tmpExcitRatio, tmpRewiringP,tmpInternalMu_w;
		NeuManCfg nmcfg;
		for (int i=0; i<cpm.getNodesNum();++i){
			tmp = (nodeCs.size()>i)?nodeCs.get(i):null;
			tmpN=((tmp!=null)&&(tmp.getN()!=null))?
					tmp.getN():ssc.getGlob_local_n();
			tmpExcitRatio = ((tmp!=null)&&(tmp.getExcitatory_inhibitory_ratio()!=null))?
					tmp.getExcitatory_inhibitory_ratio():
					ssc.getR();
			tmpK = ((tmp!=null)&&(tmp.getK()!=null))? 
					tmp.getK(): ssc.getGlob_k();
			tmpRewiringP = ((tmp!=null)&&(tmp.get_rewiring_P()!=null))?
					tmp.get_rewiring_P() : ssc.getGlob_rewiring_P();
			tmpExternal = ((tmp!=null)&&(tmp.getExternal_inputs_number()!=null))?
					tmp.getExternal_inputs_number() : ssc.getGlob_external_inputs_number();
			tmpInternalMu_w=((tmp!=null)&&(tmp.getMu_w()!=null))?
					tmp.getMu_w() : ssc.getMu_w();
			nmcfg=((tmp!=null)&&(tmp.getNeuron_manager()!=null))?
					tmp.getNeuron_manager() : ssc.getGlobal_neuron_manager();
			if (tmpK>=tmpN){
				throw new BadParametersException("bad parameters exception, "
						+ "n has to be greater than k (now n is "+tmpN+
						" and k is +"+tmpK+")");
			}
			//add a new node with or without external inputs
			println("adding node:"+i);
			if (tmpExternal.equals(0))
				addNodeThread(
						new NodeThread(
								nMan, 
								cpm.getNode(i).getId(),
								tmpN,
								tmpExcitRatio,
								tmpK,
								tmpRewiringP,
								tmpInternalMu_w,
								nmcfg.getC(),
								nmcfg.getD(),
								nmcfg.getT_arp(),
								Constants.EXCITATORY_PRESYNAPTIC_DEF_VAL, 
								Constants.INHIBITORY_PRESYNAPTIC_DEF_VAL,
								Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
								plasticity,
								avgNeuronalSignalSpeed)
						);
			else{
				Integer tmpExternalType, tmpExternalTimestep, tmpExternalFireDuration;
				Double tmpExternalFirerate, tmpExternalFireAmplitude;
				tmpExternalType=((tmp!=null)&&(tmp.getExternal_inputs_type()!=null))?
						tmp.getExternal_inputs_type():ssc.getGlob_external_inputs_type();
				tmpExternalTimestep=((tmp!=null)&&(tmp.getExternal_inputs_timestep()!=null))?
						tmp.getExternal_inputs_timestep():ssc.getGlob_external_inputs_timestep();
				tmpExternalFirerate=((tmp!=null)&&(tmp.getExternal_inputs_firerate()!=null))?
						tmp.getExternal_inputs_firerate():ssc.getGlob_external_inputs_firerate();
				tmpExternalFireDuration=((tmp!=null)&&(tmp.getExternal_inputs_fireduration()!=null))?
						tmp.getExternal_inputs_fireduration():ssc.getGlob_external_inputs_fireduration();
				tmpExternalFireAmplitude=((tmp!=null)&&(tmp.getExternal_inputs_amplitude()!=null))?
						tmp.getExternal_inputs_amplitude():ssc.getGlob_external_inputs_amplitude();
				addNodeThread(
						new NodeThread(
								nMan,
								cpm.getNode(i).getId(),
								tmpN,
								tmpExternal,
								tmpExternalType,
								tmpExternalTimestep,
								tmpExternalFirerate,
								tmpExternalFireDuration,
								tmpExternalFireAmplitude,
								tmpExcitRatio,
								tmpK,
								tmpRewiringP,
								tmpInternalMu_w,
								nmcfg.getC(),
								nmcfg.getD(), 
								nmcfg.getT_arp(),
								Constants.EXCITATORY_PRESYNAPTIC_DEF_VAL, 
								Constants.INHIBITORY_PRESYNAPTIC_DEF_VAL,
								Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
								plasticity,avgNeuronalSignalSpeed)
								);
			}
		}
		calculateCompressionFactor();
		times[2]=System.currentTimeMillis()-lastTime;
		lastTime+=times[2];
		System.out.println("adding inter-node connection probability...\n");
		for (int i=0; i<conns.size();++i){
			System.out.println("[SPIKING NEURAL SIMULATOR] Ne en ratio:"+conns.get(i).getNe_en_ratio());
			addInterNodeThreadConnection(
					nMan.getNodeThread( conns.get(i).getSrc()), 
					nMan.getNodeThread(conns.get(i).getDst()), 
					conns.get(i).getNe_en_ratio(),
					conns.get(i).getMu_w(),
					conns.get(i).getSigma_w(),
					conns.get(i).getLength(),
					conns.get(i).getLengthShapeParameter());
		}
		times[3]=System.currentTimeMillis()-lastTime;
		lastTime+=times[3];
		System.out.println("initializing simulator...\n");
		init();
		times[4]=System.currentTimeMillis()-lastTime;
		lastTime+=times[4];
	}
	
	public void addInterNodeThreadConnection(
			NodeThread nd1, 
			NodeThread nd2, 
			Double Ne_en_ratio, 
			Double amplitude, 
			Double amplitudeStdDeviation, 
			Double length, 
			Double lengthShapeParameter){
		nMan.addInterNodeConnection(
				nd1, 
				nd2, 
				Ne_en_ratio,
				amplitude, 
				amplitudeStdDeviation, 
				length, 
				lengthShapeParameter);
	}
	
	public void addInterNodeConnection(
			Integer nd1Id, 
			Integer nd2Id, 
			Double Ne_en_ratio, 
			Double amplitude, 
			Double amplitudesStdDeviations, 
			Double length,
			Double lengthShapeParameter){
		nMan.addInterNodeConnectionParameters(
				nd1Id, 
				nd2Id, 
				Ne_en_ratio, 
				amplitude, 
				amplitudesStdDeviations, 
				length, 
				lengthShapeParameter);
	}
	
	public void setDebug(Boolean debug){
		this.debug=debug;
		//nMan.setDebug(debug);
		nMan.setDebug(debug);
	}

	public void setExperimentName(String expName) {
		println("setting experiment name:"+Experiment.DIR+expName);
		Experiment.setExperimentName(Experiment.DIR+expName);
		File expDir = new File (Experiment.DIR+expName);
		if (!expDir.exists())
			expDir.mkdirs();
	}
	
	private void calculateCompressionFactor(){
		if (nMan.getTotalN()>1000000)
			compressionFactor = new Double(Integer.MAX_VALUE) / nMan.getTotalN();
		else
			compressionFactor = 1.0;
		nMan.setCompressionFactor(compressionFactor);
	}
	
	public double getReducingFactor(){
		return compressionFactor;
	}
	
	public String getExperimentName(){
		return Experiment.expName; 
	}
	
	public String getNodesNumMaxMask(){
		println("nodes num:"+nMan.getNodeThreadsNum());
		return (new BigInteger("2")).pow(nMan.getNodeThreadsNum()).subtract(BigInteger.ONE).toString();
	}
	
	public void setMask(BigInteger mask){
		StatisticsCollector.setNodes2checkMask(mask);
	}

	//=======================================   printing functions =======================================
	
	private void println(String s){
		if (verbose)
			System.out.println(TAG+s);
	}
	
	private void debprintln(String s){
		if (verbose&&debug)
			System.out.println(TAG+"[debug] "+s);
	}
	
	private void leveldebprintln(String s, int level){
		if (this.debug_level>=level)
			debprintln(s);
	}
	
	private void debActiveprintln(String s){
		if (verbose&&debug&&activePassiveDebug)
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
			System.out.println("\n\n------------------------------------------"+splitCount+"-----------------------------------------------------\n");
	}
	
	//=======================================   main function =======================================

	public static void main(String[] args) {		
		System.out.println("\n\n\n\t\t\t\t\t=========================================");
		System.out.println("\t\t\t\t\t=\t\tF. N. S.\t\t=");
		System.out.println("\t\t\t\t\t=========================================\n\n\n");
		System.out.println("creating simulator instance...");
		SpikingNeuralSimulator sns = new SpikingNeuralSimulator();
		Scanner scin = new Scanner(System.in);
		BigInteger checkNodessMask = null;
		if (args.length==0){
			Double pint1 = 0.05;
			Double pint2 = 0.05;
			System.out.println("creating node...\n");
			System.out.println("adding node to simuator...\n");
			sns._addNodeThread(0,SpikingNeuralSimulator.simulationNeurons,SpikingNeuralSimulator.externalNeurons,1,1,0.1,5, ExternalInput.EXTERNAL_AMPLITUDE_DEF_VALUE);
			sns._addNodeThread(1,SpikingNeuralSimulator.simulationNeurons );
			sns._addNodeThread(2,SpikingNeuralSimulator.simulationNeurons );
			sns.calculateCompressionFactor();
			System.out.println("\n\nadding inter/node connection probability...\n");
			sns.addInterNodeConnection(0,1,pint1,1.0,1.0,40.8, 10.0);
			sns.addInterNodeConnection(1,0,pint1,1.0,1.0,40.8, 10.0);
			sns.addInterNodeConnection(1,2,pint2,1.0,1.0,40.8, 10.0);
			sns.addInterNodeConnection(2,1,pint2,1.0,1.0,40.8, 10.0);
			System.out.println("\n\ninitializing simulator...\n");
			sns.init();
			sns.setExperimentName("std_firnet");
			checkNodessMask = new BigInteger("7");
			sns.setMask(checkNodessMask);
		}
		else{			
			if (args.length>=3)
				sns.setExperimentName(args[2]);
			if (args.length>=4){
				checkNodessMask = new BigInteger(args[3]);
				sns.setMask(checkNodessMask);
			}
			try {
				sns.initFromConfigFileAndConnectivityPackage(args[0], args[1]);
			} catch (BadParametersException e) {
				e.printStackTrace();
			}
		}
		if (checkNodessMask==null){
			System.out.println("Insert the mask for the nodes to check ["+sns.getNodesNumMaxMask()+"] :");
			checkNodessMask = new BigInteger(scin.nextLine());
			sns.setMask(checkNodessMask);
		}
		System.out.println("nodes to check mask:"+checkNodessMask);		
		System.out.println("running simulator...\n");
		sns.start();
		try {
			sns.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		StatisticsCollector.PrintResults();
		try{
			String fireFileName= null;
			if (checkNodessMask.compareTo(BigInteger.ZERO)!=0){
				fireFileName = sns.getExperimentName()+"mask"+checkNodessMask;
				System.out.println("writing the file "+fireFileName+".csv ...");
				StatisticsCollector.makeCsv(fireFileName);
				System.out.println("done.");
			}
			System.out.println("Plot? [y/n]:");
			char in= (char) System.in.read();
			System.out.println(in);
			if ((in=='Y')||(in=='y')){
				StatisticsCollector.printFirePlot(fireFileName);
			}
			in= (char) System.in.read();
			System.out.println("in:"+in);
			in= (char) System.in.read();
			in= (char) System.in.read();
			in= (char) System.in.read();
			System.out.println(in);			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("bye!");
		System.exit(0);
	}


	
	
	

}



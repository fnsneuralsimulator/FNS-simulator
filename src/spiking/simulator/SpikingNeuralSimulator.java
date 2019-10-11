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


package spiking.simulator;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
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
import org.apache.commons.cli.*; 

public class SpikingNeuralSimulator extends Thread{
	private final static String TAG = "[Spiking Neural Simulator] ";
	private final static Boolean verbose = true;
	private Boolean debug = false;
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
	public static final Integer BnTest=1;
	public static final Double IBITest=0.001;
	private static final Double epsilon = 0.00000001;
	long[] times= new long[10];
	// the coefficient for which multiply the BOP to obtain a cycle: 
	// 		1. if it is less than 1, than we fall into the optimistic simulation
	//		2. if it is greater or equal to 1, than we have a conservative behavior
	private static final Double bop_to_cycle_factor=1.0;
	private StatisticsCollector sc=new StatisticsCollector();
	
	public SpikingNeuralSimulator (){
		nMan = new NodesManager(this, sc);
		sc.start();
	}
	
	private void addNodeThread(NodeThread node){
		nMan.addNodeThread(node);
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
		//println("cycle time:"+cycle_time);
	}

	private void setTotalTime(double total_time){
		this.total_time=total_time;
		//StatisticsCollector.setSimulatedTime(total_time);
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
		println("running new splits with new stop simulated time:"+stopTime);
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
		println("killing statistics collector...");
		sc.kill();
		println("statistics collector stopped.");
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
		println("init phases:\n\t conn pckg read:\t\t"+times[0]+" ms\n\t config file read:\t\t"+times[1]+
				" ms\n\t node init:\t\t\t"+times[2]+" ms\n\t inter-node connections init:\t"+times[3]+
				" ms\n\t simulator init:\t\t"+times[4]+" ms");
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
				try {
					sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentTime+=cycle_time;
				runNewSplit(currentTime);
				completing=nMan.getNodeThreadsNum()-1;
			}
			return false;
		}
		return true;
	}
 
	public void initFromConfigFileAndConnectivityPackage(String configPath, String connPkgPath, Boolean do_fast) 
			throws BadParametersException{
		long startTime = System.currentTimeMillis();
		long lastTime = startTime;
		println("reading connectivity package file:"+connPkgPath);
		ConnectivityPackageManager cpm = new ConnectivityPackageManager(); 
		cpm.readConnectivityPackage(connPkgPath);
		sc.setMinMaxNe_xn_ratios(cpm.getMinNe_xn_ratio(), cpm.getMaxNe_xn_ratio());
		ArrayList<NodesInterconnection> conns = cpm.getInterNodeConnections();
		times[0]=System.currentTimeMillis()-lastTime;
		lastTime+=times[0];
		System.out.println("reading config file:"+configPath);
		SpikingSimulatorCfg ssc = SpikingConfigManager.readConfigFile(configPath);
		setTotalTime(new Double(ssc.getStop()));
		//ArrayList<NodeCfg> nodeCs =  ssc.getNodes();
		HashMap <Integer, NodeCfg> nodeCs =  ssc.getNodesMap();
		avgNeuronalSignalSpeed=ssc.getAvg_neuronal_signal_speed();
		times[1]=System.currentTimeMillis()-lastTime;
		lastTime+=times[1];
		System.out.println("creating and adding nodes...\n");
		NodeCfg tmp;
		Boolean tmpPlasticity;
		Long tmpN;
		Integer  tmpK, tmpExternal, tmpBn;
		Double tmpExcitRatio;
		Double tmp_mu_w_exc;
		Double tmp_mu_w_inh;
		Double tmpW_pre_exc;
		Double tmpW_pre_inh;
		Double tmpRewiringP;
		Double tmpIBI;
		Double tmpEtap;
		Double tmpEtam;
		Double tmpTaup;
		Double tmpTaum;
		Double tmpPwMax;
		Double tmpTo;
		NeuManCfg nmcfg;
    Boolean lif=new Boolean(ssc.getLif());
    Boolean exp_decay=new Boolean(ssc.getExp_decay());
		for (int i=0; i<cpm.getNodesNum();++i){
			//tmp = (nodeCs.size()>i)?nodeCs.get(i):null;
      //if (nodeCs.get(i) != null){
      //  System.out.println("Node:"+ i);
      //  System.exit(0);
      //}
			tmp = ( nodeCs.get(i) != null )?nodeCs.get(i):null;
			tmpN=((tmp!=null)&&(tmp.getN()!=null))?
					tmp.getN():ssc.getGlob_local_n();
			tmpExcitRatio = ((tmp!=null)&&(tmp.getExcitatory_inhibitory_ratio()!=null))?
					tmp.getExcitatory_inhibitory_ratio():
					ssc.getR();
			tmp_mu_w_exc=((tmp!=null)&&(tmp.getMu_w_exc()!=null))?
					tmp.getMu_w_exc():
					ssc.getGlob_mu_w_exc();
			tmp_mu_w_inh=((tmp!=null)&&(tmp.getMu_w_inh()!=null))?
					tmp.getMu_w_inh():
					ssc.getGlob_mu_w_inh();
			tmpW_pre_exc=((tmp!=null)&&(tmp.getW_pre_exc()!=null))?
					tmp.getW_pre_exc():
					ssc.getGlob_w_pre_exc();
			tmpW_pre_inh=((tmp!=null)&&(tmp.getW_pre_inh()!=null))?
					tmp.getW_pre_inh():
					ssc.getGlob_w_pre_inh();
			tmpK = ((tmp!=null)&&(tmp.getK()!=null))? 
					tmp.getK(): ssc.getGlob_k();
			tmpRewiringP = ((tmp!=null)&&(tmp.get_rewiring_P()!=null))?
					tmp.get_rewiring_P() : ssc.getGlob_rewiring_P();
			tmpExternal = ((tmp!=null)&&(tmp.getExternal_inputs_number()!=null))?
					tmp.getExternal_inputs_number() : ssc.getGlob_external_inputs_number();
			tmpBn=((tmp!=null)&&(tmp.getBn()!=null))?
					tmp.getBn() : ssc.getGlob_Bn();
      if (tmpBn==0)
        tmpBn=1;
			tmpIBI=((tmp!=null)&&(tmp.getIBI()!=null))?
					tmp.getIBI() : ssc.getGlob_IBI();
			nmcfg=((tmp!=null)&&(tmp.getNeuron_manager()!=null))?
					tmp.getNeuron_manager() : ssc.getGlobal_neuron_manager();
			tmpPlasticity=((tmp!=null)&&(tmp.getPlasticity()!=null))?
					tmp.getPlasticity() : ssc.getPlasticity();
			tmpEtap=((tmp!=null)&&(tmp.getEtap()!=null))?
					tmp.getEtap() : ssc.getGlob_etap();
			tmpEtam=((tmp!=null)&&(tmp.getEtam()!=null))?
					tmp.getEtam() : ssc.getGlob_etam();
			tmpTaup=((tmp!=null)&&(tmp.getTaup()!=null))?
					tmp.getTaup() : ssc.getGlob_taup();
			tmpTaum=((tmp!=null)&&(tmp.getTaum()!=null))?
					tmp.getTaum() : ssc.getGlob_taum();	
			tmpPwMax=((tmp!=null)&&(tmp.getPw_max()!=null))?
					tmp.getPw_max() : ssc.getGlob_pw_max();	
			tmpTo=((tmp!=null)&&(tmp.getTo()!=null))?
					tmp.getTo() : ssc.getGlob_to();	
			if (tmpK>=tmpN){
				throw new BadParametersException("bad parameters exception, "
						+ "n has to be greater than k (now n is "+tmpN+
						" and k is +"+tmpK+")");
			}
			//add a new node with or without external inputs
			println("adding node:"+i);
			if (tmpExternal.equals(0)) {
				addNodeThread(
						new NodeThread(
						nMan, 
						cpm.getNode(i).getId(),
						tmpN,
						tmpExcitRatio,
						tmpK,
						tmpRewiringP,
						tmpBn,
						tmpIBI,
						nmcfg.getC(),
						nmcfg.getD(),
						nmcfg.getT_arp(),
						tmp_mu_w_exc,
						tmp_mu_w_inh,
						tmpW_pre_exc,
						tmpW_pre_inh,
						Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
						tmpPlasticity,
						tmpEtap,
						tmpEtam,
						tmpTaup,
						tmpTaum,
						tmpPwMax,
						tmpTo,
						avgNeuronalSignalSpeed,
            lif,
            exp_decay,
						do_fast));
			}
			else{
				Integer tmpExternalType; 
				Integer tmpExternalTimestep;
				Integer tmpExternalFireDuration;
				Double tmpExternalFirerate;
				Double tmpExternalFireAmplitude;
				Double tmpExternalInputsTimeOffset;
				tmpExternalType=((tmp!=null)&&(tmp.getExternal_inputs_type()!=null))?
						tmp.getExternal_inputs_type():ssc.getGlob_external_inputs_type();
				tmpExternalInputsTimeOffset=((tmp!=null)&&(tmp.getExternal_inputs_time_phase()!=null))?
						tmp.getExternal_inputs_time_phase():ssc.getGlob_external_inputs_time_offset();
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
								tmpExternalInputsTimeOffset,
								tmpExternalTimestep,
								tmpExternalFirerate,
								tmpExternalFireDuration,
								tmpExternalFireAmplitude,
								tmpExcitRatio,
								tmpK,
								tmpRewiringP,
								tmpBn,
								tmpIBI,
								nmcfg.getC(),
								nmcfg.getD(), 
								nmcfg.getT_arp(),
								tmp_mu_w_exc,
								tmp_mu_w_inh,
								tmpW_pre_exc,
								tmpW_pre_inh,
								Constants.EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL, 
								tmpPlasticity,
								tmpEtap,
								tmpEtam,
								tmpTaup,
								tmpTaum,
								tmpPwMax,
								tmpTo,
								avgNeuronalSignalSpeed,
                lif,
                exp_decay,
								do_fast));
			}
		}
		calculateCompressionFactor();
		times[2]=System.currentTimeMillis()-lastTime;
		lastTime+=times[2];
		System.out.println("adding inter-node connection probability...\n");
		for (int i=0; i<conns.size();++i){
			addInterNodeThreadConnection(
					nMan.getNodeThread(conns.get(i).getSrc()), 
					nMan.getNodeThread(conns.get(i).getDst()), 
					conns.get(i).getNe_xn_ratio(),
					conns.get(i).getMu_omega(),
					conns.get(i).getSigma_w(),
					conns.get(i).getLength(),
					conns.get(i).getLengthShapeParameter(),
					conns.get(i).getType());
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
			Double Ne_xn_ratio, 
			Double mu_omega, 
			Double sigma_omega, 
			Double mu_lambda, 
			Double alpha_lambda,
			Integer inter_node_conn_type){
		nMan.addInterNodeConnection(
				nd1, 
				nd2, 
				Ne_xn_ratio,
				mu_omega, 
				sigma_omega, 
				mu_lambda, 
				alpha_lambda,
				inter_node_conn_type);
	}
	
//	public void addInterNodeConnection(
//			Integer nd1Id,  
//			Integer nd2Id, 
//			Double Ne_xn_ratio, 
//			Double mu_omega, 
//			Double sigma_omega, 
//			Double mu_lambda,
//			Double alpha_lambda){
//		nMan.addInterNodeConnectionParameters(
//				nd1Id, 
//				nd2Id, 
//				Ne_xn_ratio, 
//				mu_omega, 
//				sigma_omega, 
//				mu_lambda, 
//				alpha_lambda);
//	}
	
	public void setDebug(Boolean debug){
		this.debug=debug;
		//nMan.setDebug(debug);
		nMan.setDebug(debug);
	}

	public void setExperimentName(String expName) {
		Experiment.setExperimentName(expName);
		File expDir = new File (Experiment.getExperimentDir());
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
	
	
	public String getNodesNumMaxMask(){
		println("nodes num:"+nMan.getNodeThreadsNum());
		return (new BigInteger("2")).pow(nMan.getNodeThreadsNum()).subtract(BigInteger.ONE).toString();
	}
	
	public void setMask(BigInteger mask){
		sc.setNodes2checkMask(mask);
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
		System.out.println("\n\n\n\t\t\t\t\t=================================");
		System.out.println("\t\t\t\t\t=\t    F N S\t\t=");
		System.out.println("\t\t\t\t\t=\tNeural Simulator\t=");
		System.out.println("\t\t\t\t\t=================================\n\n");
		// options parsing and management
		Options options= new Options();
        //Option expconfigopt = new Option("x", "exp-config", true, "the experiment configuration folder");
        //expconfigopt.setRequired(true);
        //options.addOption(expconfigopt);
        options.addOption("m", "mask", true, "followed by the mask number. The mask indicates "
        		+ "the set of NOIs (node of interests) for which to store the output data. "
        		+ "If this switch is not present, the entire set of nodes will be "
        		+ "considered for the generation of output data.");
        //options.addOption("r", "runs", true, "the number of runs");
        //options.addOption("p", "plot", false, "plot a scatter plot of the experiment");
        options.addOption("f", "fast", false, "enables faster algorithms at different levels, "
        		+ "in return for some approximations (i.e., plasticity exponentials, etc.)");
        options.addOption("M", "matlab", false, "provides with a set of matlab-compliant "
        		+ "CSV files, in addition to the output CSVs.");
        options.addOption("h", "help", false, "shows this help");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("help")){
            	formatter.printHelp("FNS", options);
            	System.out.println("\nExamples:");
            	System.out.println("[Windows] \t> .\\start.bat exp01 -f -m 7 -M");
            	System.out.println("[Linux] \t$ ./start exp01 -f -m 7 -M\n");
                System.exit(0);
                return;
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("FNS", options);
            System.out.println("\nExamples:");
        	System.out.println("[Windows] > .\\start.bat exp01 -f -m 7 -p -M" );
        	System.out.println("[Linux] > ./start exp01 -f -m 7 -p -M\n");
            System.exit(1);
            return;
        }
        // intializing simulator
		System.out.println("initializing simulator");
		SpikingNeuralSimulator sns = new SpikingNeuralSimulator();
		sns.setExperimentName(args[0]);
		String filename=null;
		BigInteger checkNodessMask = null;
		Boolean do_plot=cmd.hasOption("plot");
		Boolean do_fast=cmd.hasOption("fast");
		checkNodessMask=new BigInteger(cmd.getOptionValue("mask","0"));
		if (checkNodessMask==null || checkNodessMask.toString()=="0") {
			filename = Experiment.getExperimentDir()+"mask_all_";
			sns.sc.checkAll();
		}
		else{
			filename = Experiment.getExperimentDir()+"mask_"+checkNodessMask;
			sns.setMask(checkNodessMask);
		}
		sns.sc.set_filename(filename);
		if (cmd.hasOption("matlab"))
			sns.sc.setMatlab();
		try {
			sns.initFromConfigFileAndConnectivityPackage(
					(new File(args[0]+"/config.xml")).getAbsolutePath(), 
					(new File(args[0]+"/connectivity")).getAbsolutePath(),
					do_fast);
		} catch (BadParametersException e) {
			e.printStackTrace();
		}
		System.out.println("nodes to check mask:"+checkNodessMask);		
		System.out.println("running simulator...\n");
		sns.start();
		try {
			sns.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		sns.sc.PrintResults();
		sns.sc.makeCsv(filename);
		try{
			//filename = sns.getExperimentName()+"mask"+checkNodessMask+"_";
			sns.sc.makeCsv(filename);
			System.out.println("done.");
			if (do_plot){
				sns.sc.printFirePlot(filename);
				System.out.println("Press enter twice to quit:");
				System.in.read();
				System.in.read();
				System.in.read();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("bye!");
		System.exit(0);
	}
}



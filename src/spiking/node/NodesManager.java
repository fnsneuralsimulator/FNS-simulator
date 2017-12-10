/**
* This file is part of FNS (Firnet NeuroScience), ver.1.0.1
*
* (c) 2017, Mario Salerno, Gianluca Susi, Alessandro Cristini, Emanuele Paracone.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, Mario 
* Salerno, Fernando Maestú, Ernesto Pereda (2018). "FNS: An event-driven spiking 
* neural network framework for efficient simulations of large-scale brain 
* models". 
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;

import spiking.controllers.node.NodeThread;
import spiking.internode.InterNodeSpike;
import spiking.simulator.SpikingNeuralSimulator;
import utils.tools.NiceNode;
import utils.tools.NiceQueue;
import utils.constants.Constants;
import utils.exceptions.BadCurveException;
import utils.statistics.StatisticsCollector;
import utils.tools.IntegerCouple;

import org.apache.commons.math3.distribution.GammaDistribution;
import org.mapdb.*;

public class NodesManager implements Serializable {
	
	public static final Double MAX_TRACT_LENGTH=100000.0;
	private static final long serialVersionUID = -4781040115396333609L;
	private final static String TAG = "[Nodes Manager] ";
	private final static Boolean verbose = true;
	private final static Integer goodCurveGuessThreshold= 5;
	private final static Integer badCurveGuessThreshold= 15;
	
	
	private Boolean debug = false;


	//ergion threads array list 
	private ArrayList<NodeThread> regThrds = new ArrayList<NodeThread>();
	
	//interworld connection probability
	private HashMap<IntegerCouple, NodesInterconnection> regConnections = new HashMap<IntegerCouple, NodesInterconnection>();
	
	
	//the total number of neuron
	private Long n=0l;
	//the maximum number of neurons within a single node
	private Long maxN=0l;
	private double compressionFactor=1.0;
	
	//the total number of burning neurons
//	private Integer bn=1000;
	//the total number of excitatory neuron
	private Long excitatory=0l;
	//the total number of inhibithory neuron
	private Long inhibithory=0l;
	//the total number of external inputs
	private Integer externalInputs=0;
	//the HasMap of small worlds with external inputs
	private ArrayList<Integer> regsWithExternalInputs = new ArrayList<Integer>();
	//private HashMap<Integer, Integer> swExternalInputsNumber = new HashMap<Integer, Integer>();
	//the total number of inter-node connections
	private Long inter_node_conns_num=0l;
	
	//is set, no more node addition are allowed
	private Boolean initialized=false;

	private SpikingNeuralSimulator sim;
	
	private Double minTractLength=MAX_TRACT_LENGTH;
	
	private Random randGen = new Random(System.currentTimeMillis());
	
	
	public NodesManager(SpikingNeuralSimulator sim){
		this.sim=sim;
	}
	
	public void addNodeThread(NodeThread regT){
		if (initialized)
			return;
		//reg.setId(regs.size());
		//updateNeuronTreeIndexes(reg.getId());
		regThrds.add(regT);
		
		n+=regT.getN();
		//updating the maximum number of neuron within a same node
		if (regT.getN()>maxN)
			maxN=regT.getN();

		println("adding node:"+regT.getNodeId()+", n:"+regT.getN()+"\n");
		//reg.setExcitatoryStartingIndex(excitatory+inhibithory);
//		println("exc starting:"+reg.getExcitatoryStartingIndex());
		excitatory+=regT.getExcitatory();
//		reg.setInhibithoryStartingIndex(excitatory+inhibithory);
//		println("inhib starting:"+reg.getInhibithoryStartingIndex());
		inhibithory+=regT.getInhibithory();
		if (regT.hasExternalInput()){
//			updateExtNeuronTreeIndexes(reg.getId());
//			reg.setExternalStartingIndex(externalInputs);
			//println("External inputs:"+regT.getExternalInputs()+" starting @:"+regT.getExternalStartingIndex());
			externalInputs+=regT.getExternalInputs();
			regsWithExternalInputs.add(regT.getNodeId());
			//regExternalInputsNumber.put(reg.getId(), reg.getExternalInputs());
		}
		//++nregs;
	}
	
	
	public void addInterNodeConnection(
			NodeThread reg1, NodeThread reg2, 
			Double weight, Double amplitude, 
			Double amplitudeStdVariation, Double length, 
			Double lengthShapeParameter){
		if (length==null)
			println("length null 1...");
		NodesInterconnection regi = new NodesInterconnection(reg1, reg2, weight);
		regi.setLength(length);
//		Integer src =  (reg1.getRegionId()<reg2.getRegionId())? reg1.getRegionId(): reg2.getRegionId();
//		Integer dst =  (reg1.getRegionId()<reg2.getRegionId())? reg2.getRegionId(): reg1.getRegionId();
		regConnections.put(new IntegerCouple(reg1.getNodeId(), reg2.getNodeId()), regi);
		_addInterRegionConnection(reg1.getNodeId(),reg2.getNodeId(),weight,amplitude,amplitudeStdVariation, length, lengthShapeParameter);
	}
	
	public void addInterNodeConnectionParameters(
			Integer reg1Id, Integer reg2Id, 
			Double weight, Double amplitude, 
			Double amplitudeStdVariation, Double length, 
			Double lengthShapeParameter){
		if (length==null)
			println("length null 2...");
		NodesInterconnection regi = new NodesInterconnection(reg1Id, reg2Id, weight);
		regi.setLength(length);
//		Integer src =  (reg1Id<reg2Id)? reg1Id: reg2Id;
//		Integer dst =  (reg1Id<reg2Id)? reg2Id: reg1Id;
		regConnections.put(new IntegerCouple(reg1Id, reg2Id), regi);
		_addInterRegionConnection(reg1Id, reg2Id, weight, amplitude, amplitudeStdVariation, length, lengthShapeParameter);
	}
	
	private void _addInterRegionConnection(
			Integer reg1Id, Integer reg2Id, 
			Double weight, Double amplitude, 
			Double amplitudeStdVariation, Double length, 
			Double lengthShapeParameter){
		if (length==null)
			println("length null...");
		if (minTractLength==null)
			println("min tract length null...");
		
		if (length<minTractLength)
			minTractLength=length;
		try {
			__addInterRegionConnection(reg1Id, reg2Id, weight,amplitude,amplitudeStdVariation,length, lengthShapeParameter);
		} catch (BadCurveException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 *  adds an inter node connection using the weight as the number of connections
	 *  between neurons of the two nodes
	 * @throws BadCurveException 
	 */
	private void __addInterRegionConnection(
			Integer reg1Id, Integer reg2Id, 
			Double weight, Double amplitude, 
			Double amplitudeStdVariation, Double length, 
			Double lengthShapeParameter) throws BadCurveException{
		long tmp = (long)(regThrds.get(reg1Id).getExcitatory()*weight);
		Long i1, i2;
		GammaDistribution gd = (lengthShapeParameter!=null)?
				new GammaDistribution(lengthShapeParameter, lengthShapeParameter/length)
				:null;
		for (long i=0; i<tmp;++i){
			i1 = (long)(Math.random() * regThrds.get(reg1Id).getExcitatory());
			i2 = (long)(Math.random() * regThrds.get(reg2Id).getExcitatory());
			Double tmpAmp, tmpLength=-1.0;
			tmpAmp = (amplitudeStdVariation!=null)?
					Math.abs(randGen.nextGaussian()*amplitudeStdVariation+amplitude)
					:amplitude;
			int goodCurveGuess=0;
			while ((tmpLength<0)&&(goodCurveGuess<badCurveGuessThreshold)){
				tmpLength = (gd!=null)?	
						length* (gd.sample())
						: length;
				++goodCurveGuess;
			}
			if (goodCurveGuess>=goodCurveGuessThreshold){
				StatisticsCollector.setBadCurve();
				if (goodCurveGuess>=badCurveGuessThreshold)
					throw new BadCurveException("the gamma curve G("+lengthShapeParameter+", "+ 
							(lengthShapeParameter/length)+" has a shape which is not compliant with firnet scope.");
			}
			regThrds.get(reg1Id).addIntermoduleSynapse(
					reg1Id , i1, reg2Id, i2,
					tmpAmp, tmpLength );
			regThrds.get(reg2Id).addIntermoduleSynapse(
					reg1Id , i1, reg2Id, i2,
					tmpAmp, tmpLength );
			++ inter_node_conns_num;
		}
		println("excitatory: "+regThrds.get(reg1Id).getExcitatory()+
				 "\ninternode connections:"+inter_node_conns_num);
	}
	
	public NodesInterconnection _getInterworldConnectionProb(Node reg1, Node reg2){
		Integer src=  (reg1.getId()<reg2.getId())? reg1.getId(): reg2.getId();
		Integer dst=  (reg1.getId()<reg2.getId())? reg2.getId(): reg1.getId();
		return regConnections.get(new IntegerCouple(src, dst));
	}
	
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
		return regThrds.size();
	}

	public Boolean getInitialized() {
		return initialized;
	}
	
	public Double getMinTractLength(){
		return minTractLength;
	}

	public Integer getnSms() {
		return regThrds.size();
	}
	
	public NodeThread getNodeThread(int index){
		return regThrds.get(index);
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
		for (int i=0; i<regThrds.size();++i)
			regThrds.get(i).start();
	}
	

	public void runNewSplit(double newStopTime) {
		updateInterNodeSpikes();
		for (int i=0; i<regThrds.size();++i)
			regThrds.get(i).runNewSplit(newStopTime);		
	}
	
	public void killAll() {
		for (int i=0; i<regThrds.size();++i)
			regThrds.get(i).kill();
	}
	
	public synchronized Boolean splitComplete(Integer nodeId){
		return sim.splitComplete(nodeId);
	}
	
	private void updateInterNodeSpikes(){
		for (int i=0; i<regThrds.size();++i){
			while (regThrds.get(i).hasInterNodeSpikes())
				deliverInterNodeSpike( regThrds.get(i).popInterNodeSpike() );
		}
	}
	
	private void deliverInterNodeSpike(InterNodeSpike irs){
		regThrds.get(irs.getSyn().getDendriteNodeId()).burnInterNodeSpike(irs);
	}

	

	
}

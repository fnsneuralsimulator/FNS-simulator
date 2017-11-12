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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import utils.experiment.Experiment;
import utils.tools.LongCouple;
import utils.tools.NiceNode;
import utils.tools.Shuffler;

import org.mapdb.*;

import spiking.node.external_inputs.ExternalInput;

//import utils.constants.Constants;

public class Node {
	
	private final static String TAG = "[Node ";
	private final static Boolean verbose = true;
	private Integer id;
	private ExternalInput ext;
	//number and kind of neurons 
	private Long n=100l;
	private Long excitatory;
	private Long inhibithory;
	//the small world connection matrix
	private HashMap<LongCouple, Double> connectionMatrix = new HashMap<>();
	private File nodeDbFile;
	//proportion between excitatory and inhibithory
	private Double R=0.8;
	//number of external inputs
	private Integer externalInputs=0;	
	//edges per vertex mean degree
	private Integer k=20;	
	//rewiring probability
	private Double prew=0.5;
	private Double internalAmplitude;
	private Boolean hasExternalInputs = false;
	private int externalInputType=-1;
	private int timeStep = -1;
	private double fireRate = -1;
	private int fireDuration = 1;
	private Double externalAmplitude=ExternalInput.EXTERNAL_AMPLITUDE_DEF_VALUE;
	
	public Node(Integer id, Long n, Double R, Integer k, Double prew, Double internalAmplitude){
		this.id=id;
		this.n=n;
		this.R=R;
		this.k=k;
		this.prew=prew;
		this.internalAmplitude=internalAmplitude;
		initExcitInhib();
		nodeInit();
	}
	
	public Node(
			Integer id, 
			Long n, 
			Integer externalInputs, 
			int externalInputType, 
			int timeStep, 
			double fireRate, 
			int fireDuration, 
			Double externalAmplitude,
			Double R, 
			Integer k, 
			Double prew, 
			Double internalAmplitude){
		this.id=id;
		this.n=n;
		this.externalInputType=externalInputType;
		this.R=R;
		if (externalInputs>0){
			hasExternalInputs=true;
			this.externalInputs=externalInputs;
			this.timeStep=timeStep;
			this.fireRate=fireRate;
			this.fireDuration=fireDuration;
			this.externalAmplitude=externalAmplitude;
		}
		this.k=k;
		this.prew=prew;
		this.internalAmplitude=internalAmplitude;
		initExcitInhib();
		nodeInit();
		
	}
	
	public void nodeInit(){
		Boolean savedExperiment=false;
		nodeDbFile = new File (Experiment.expName+"node_"+id+".reg");
		if (nodeDbFile.exists())
			savedExperiment=true;
		if (savedExperiment){
			println("experiment "+Experiment.expName+" found!");
		}
		else{
			println("init...");
			//ring closure
			wireInit();
		}
		externalInputInit();
		println("init done.");
	}
	
	public void closeDb(){
		
	}
	
	public void initExcitInhib(){
		excitatory= (long) Math.floor(n*R);
		inhibithory=n-excitatory;
	}
	
	private void putConnection (Long burningNeuronId, Long firingNeuronId, Double value){
		connectionMatrix.put(new LongCouple(burningNeuronId, firingNeuronId), value);
	}
	
	public Double getConnection(Long burningNeuronId, Long firingNeuronId){
		return (connectionMatrix.get(new LongCouple(burningNeuronId, firingNeuronId))!=null)?
				connectionMatrix.get(new LongCouple(burningNeuronId, firingNeuronId)):0;
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
		Integer m;
		//randomize adjacency
		DB tmpDb = DBMaker.memoryDirectDB().make();
		HTreeMap<Long, Long> shuffled = tmpDb.hashMap("shuffle", Serializer.LONG,Serializer.LONG).create();
		Shuffler.shuffleArray(shuffled,n);
		int k2=k/2;
		for (long i=0; i<n;++i){
			for (long j=1; j<=k2;++j){
				//rewiring condition
				if (Math.random()<prew){
					Long tmp;
					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (!tmp.equals(shuffled.get((i+j)%n)))){}
					putConnection(shuffled.get(i), tmp, internalAmplitude);
				}
				else
					putConnection(shuffled.get(i), shuffled.get((i+j)%n), internalAmplitude);
				if (Math.random()<prew){
					Long tmp;
					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (!tmp.equals(shuffled.get((n+i-j)%n)))){}
					putConnection(shuffled.get(i), tmp, internalAmplitude);
				}
				else
					putConnection(shuffled.get(i), shuffled.get((n+i-j)%n), internalAmplitude);
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
		ext= new ExternalInput(this, externalInputType,fireRate,fireDuration,externalAmplitude,timeStep);
		println("external input created, external spikes in queue:"+ext.getExternalSpikesInQueue());
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
	
//	public Integer [][] getSw(){
//		return sw;
//	}
	
	public void setStandardExternalInput(){
		ext= new ExternalInput(this, externalInputType,fireRate,fireDuration,externalAmplitude,timeStep);
	}
	
//	public void setExternalInput(ExternalInput ext){
//		this.ext = ext;
//		hasExternalInputs=true;
//		externalInputs = ext.getExternalInputsNum(); 
//	}
//
//	public void resetExternalInput(){
//		hasExternalInputs=false;
//		ext=null;
//	}
	
	public Boolean hasExternalInput(){
		return hasExternalInputs;
	}

	public ExternalInput getExternalInput() {
		return ext;
	}
	
	public Double getAmplitudeValue(Long extNeuronGlobalId, Long bin){
		if ((extNeuronGlobalId-n)>Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException("[NODE ERROR] The external input id is too big");
		if (bin>Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException("[NODE ERROR] The bin is too big");
		
		if (hasExternalInputs)
			return ext.getAmplitudeValue((int)(long)(extNeuronGlobalId-n), (int)(long)(bin));
		return null;
	}
	
	public NiceNode extractExternalMinSpikeTime(Long extNeuronGlobalId){
		if (!hasExternalInputs)
			return null;
		if ((extNeuronGlobalId-n)>Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException("[NODE ERROR] The external input id is too big");
	
		return ext.extractMinSpikeTime((int) (long)(extNeuronGlobalId-n));
	}

	public Boolean isExternalInput(Long neuronId){
		return neuronId>=n;
	}
	
	public void printExternalInputSpikeTimeMatrix(){
		println("printing external input for node:"+id);
		if (ext!=null)
			ext.printSpikeTimeMatrix();
	}
	
	public void printNodesConnections(){
		println("printing connections:");
		for (long i=0; i<n; ++i){
			System.out.print(i+".\t");
			for (long j=0; j<n; ++j)
				System.out.print(getConnection(i, j)+", ");
			System.out.println();
		}
	}

	private void println(String s){
		if (verbose)
			System.out.println(TAG+id+"] "+s);
	}
	
	public static void main(String[] args) {
		Node r = new Node(0, 100l,0.8,2,0.5,1.0);
		r.printNodesConnections();
	}
	
}

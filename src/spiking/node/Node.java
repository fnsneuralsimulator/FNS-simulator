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
	//Bursting Node Cardinality
	private Integer Bn;
	// InterBurst Interval
	private Double IBI;
	//the small world connection matrix
	private HashMap<LongCouple, Double> connectionMatrix = new HashMap<>();
	private File nodeDbFile;
	//proportion between excitatory and inhibithory
	private Double R=0.8;
	// postsynaptic weight
	private Double mu_w;
	//excitatory amplitude
	private Double w_pre_exc;
	//inhibitory amplitude
	private Double w_pre_inh;
	//number of external inputs
	private Integer externalInputs=0;	
	//edges per vertex mean degree
	private Integer k=20;	
	//rewiring probability
	private Double prew=0.5;
//	private Double internalAmplitude;
	private Boolean hasExternalInputs = false;
	private int externalInputType=-1;
	private Double externalInputsTimeOffset;
	private int timeStep = -1;
	private double fireRate = -1;
	private int fireDuration = 1;
	private Double externalAmplitude=ExternalInput.EXTERNAL_AMPLITUDE_DEF_VALUE;
	private Boolean plasticity;
	private Double etap;
	private Double etam;
	private Double taup;
	private Double taum;
	private Double pwMax;
	private Double to;
	
	public Node(
			Integer id, 
			Long n, 
			Double R,
			Double mu_w,
			Double w_pre_exc,
			Double w_pre_inh,
			Integer k, 
			Double prew, 
			Integer Bn, 
			Double IBI,
			Boolean plasticity,
			Double etap,
			Double etam,
			Double taup,
			Double taum,
			Double pwMax,
			Double to){
		this.id=id;
		this.n=n;
		this.R=R;
		this.mu_w=mu_w;
		this.w_pre_exc=w_pre_exc;
		this.w_pre_inh=w_pre_inh<0?w_pre_inh:(-w_pre_inh);
		this.k=k;
		this.prew=prew;
//		this.internalAmplitude=internalAmplitude;
		this.Bn=Bn;
		this.IBI=IBI;
		this.plasticity=plasticity;
		this.etap=etap;
		this.etam=etam;
		this.taup=taup;
		this.taum=taum;
		this.pwMax=pwMax;
		this.to=to;
		initExcitInhib();
		nodeInit();
	}
	
	public Node(
			Integer id, 
			Long n, 
			Integer externalInputs, 
			int externalInputType,
			Double externalInputsTimeOffset,
			int timeStep, 
			double fireRate, 
			int fireDuration, 
			Double externalAmplitude,
			Double R, 
			Double mu_w,
			Double w_pre_exc,
			Double w_pre_inh,
			Integer k, 
			Double prew, 
			Integer Bn, 
			Double IBI,
			Boolean plasticity,
			Double etap,
			Double etam,
			Double taup,
			Double taum,
			Double pwMax,
			Double to){
		this.id=id;
		this.n=n;
		this.externalInputType=externalInputType;
		this.externalInputsTimeOffset=externalInputsTimeOffset;
		this.R=R;
		this.mu_w = mu_w;
		println("mu w:"+this.mu_w);
		this.w_pre_exc=w_pre_exc;
		this.w_pre_inh=w_pre_inh<0?w_pre_inh:(-w_pre_inh);
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
//		this.internalAmplitude=internalAmplitude;
		this.Bn=Bn;
		this.IBI=IBI;
		this.plasticity=plasticity;
		this.etap=etap;
		this.etam=etam;
		this.taup=taup;
		this.taum=taum;
		this.pwMax=pwMax;
		this.to=to;
		initExcitInhib();
		nodeInit();
		
	}
	
	public void nodeInit(){
		Boolean savedExperiment=false;
		nodeDbFile = new File (Experiment.getExperimentName()+"node_"+id+".reg");
		if (nodeDbFile.exists())
			savedExperiment=true;
		if (savedExperiment){
			println("experiment "+Experiment.getExperimentName()+" found!");
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
	
	private void putConnection (Long firingNeuronId, Long burningNeuronId, Double presynaptic_weight){
		connectionMatrix.put(new LongCouple(firingNeuronId, burningNeuronId), presynaptic_weight);
	}
	
	public Double getConnectionPresynapticWeight(Long firingNeuronId, Long burningNeuronId){
		return (connectionMatrix.get(new LongCouple(firingNeuronId, burningNeuronId))!=null)?
				connectionMatrix.get(new LongCouple(firingNeuronId, burningNeuronId)):0;
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
		//randomize adjacency
		DB tmpDb = DBMaker.memoryDirectDB().make();
		HTreeMap<Long, Long> shuffled = tmpDb.hashMap("shuffle", Serializer.LONG,Serializer.LONG).create();
		Shuffler.shuffleArray(shuffled,n);
		int k2=k/2;
		Double tmpAmpl;
		for (long i=0; i<n;++i){
			if (isExcitatory(shuffled.get(i)))
				tmpAmpl=w_pre_exc;
			else
				tmpAmpl=w_pre_inh;
			for (long j=1; j<=k2;++j){
				//rewiring condition
				if (Math.random()<prew){
					Long tmp;
//					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (!tmp.equals(shuffled.get((i+j)%n)))){}
					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (tmp.equals(shuffled.get((i+j)%n)))){}
					putConnection(shuffled.get(i), tmp, tmpAmpl);
				}
				else
					putConnection(shuffled.get(i), shuffled.get((i+j)%n), tmpAmpl);
				if (Math.random()<prew){
					Long tmp;
//					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (!tmp.equals(shuffled.get((n+i-j)%n)))){}
					while ( ((tmp = (long) Math.round(Math.random()*(n-1))).equals(shuffled.get(i))) || (tmp.equals(shuffled.get((n+i-j)%n)))){}
					putConnection(shuffled.get(i), tmp, tmpAmpl);
				}
				else
					putConnection(shuffled.get(i), shuffled.get((n+i-j)%n), tmpAmpl);
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
		ext= new ExternalInput(
				this, 
				externalInputType,
				externalInputsTimeOffset,
				fireRate,
				fireDuration,
				externalAmplitude,
				timeStep);
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

	public Double getMu_w() {
		return mu_w;
	}

	public void setMu_w(Double mu_w) {
		this.mu_w = mu_w;
	}

	public Double getExc_ampl() {
		return w_pre_exc;
	}

	public Double getInh_ampl() {
		return w_pre_inh;
	}
	
	public Double getPresynapticForNeuron(Long neuronId) {
		return (isExcitatory(neuronId)?w_pre_exc:w_pre_inh);
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
	
	public void setStandardExternalInput(){
		ext= new ExternalInput(
				this, 
				externalInputType,
				externalInputsTimeOffset, 
				fireRate,
				fireDuration,
				externalAmplitude,
				timeStep);
	}
	
	
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
	
	public Integer getBn() {
		return (Bn!=null)? Bn : 1 ;
	}

	public Double getIBI() {
		return IBI;
	}
	
	public Boolean getPlasticity(){
		return plasticity;
	}

	public Double getEtap() {
		return etap;
	}

	public Double getEtam() {
		return etam;
	}

	public Double getTaup() {
		return taup;
	}

	public Double getTaum() {
		return taum;
	}

	public Double getPwMax() {
		return pwMax;
	}

	public Double getTo() {
		return to;
	}
	
	public Double getExternalAmplitude() {
		return externalAmplitude;
	}
	
	public Double getExternalInputsTimeOffset() {
		return externalInputsTimeOffset;
	}
	
	public boolean isExcitatory(Long neuronId){
		if (neuronId<excitatory)
			return true;
		return false;
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
				System.out.print(getConnectionPresynapticWeight(i, j)+", ");
			System.out.println();
		}
	}

	private void println(String s){
		if (verbose)
			System.out.println(TAG+id+"] "+s);
	}
	
}

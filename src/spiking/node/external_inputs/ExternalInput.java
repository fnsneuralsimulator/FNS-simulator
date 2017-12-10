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


package spiking.node.external_inputs;

import java.util.PriorityQueue;

import spiking.node.Node;
import utils.tools.NiceQueue;
import utils.tools.NiceNode;

public class ExternalInput {
	
	public final static Double EXTERNAL_INPUT_TIME_ZERO = 0.1;
	public final static int POISSON = 0;
	public final static int CONSTANT = 1;
	public final static int NOISE = 2;
	private final static String TAG = "[External Input] ";
	private final static Boolean verbose = true;
	public final static Double EXTERNAL_AMPLITUDE_DEF_VALUE = 0.1;
	//the node which takes the inputs
	private Node n;
	//external input neuron number
	private Integer externalInputs;
	//Poisson distributed input variables
	private Double firingRate=0.1;												//firing rate	
	private Integer fireDuration=5; 											//duration in ms
	private int timeStep=1;														//step
	private Integer nBins=fireDuration/timeStep;								//number of bins
	private Integer [] timeVector = new Integer[fireDuration-timeStep+1];		//Time Vector: duration T and step dt
	private int type;	
	/** Time Matrix: matrix with the number of rows equal to the number of external
	 * neurons, and the number of columns equal to the number of spike times (also called
	 * the bin number).			
	 * Every row is a priority Queue of Double
	 * */
	private NiceQueue[] timeMatrix;
	/** Amplitude Matrix (am): matrix with the same size of the previous one.
	 * Here are stored the amplitudes of the external spikes.					*/
	private Double[][] amplitudeMatrix;
	private int externalSpikes = 0;
	private Double externalAmplitude = EXTERNAL_AMPLITUDE_DEF_VALUE;
	
		
	public ExternalInput(Node n, int type, double firingRate, int fireDuration, Double externalAmplitude, int timeStep){
		this.n=n;
		this.type=type;
		this.firingRate=firingRate;
		this.fireDuration=fireDuration;
		this.timeStep=timeStep;
		this.externalAmplitude=externalAmplitude;
		nBins=this.fireDuration/timeStep;
		timeVector = new Integer[this.fireDuration-timeStep+1];
		init();
	}
	
	private void init(){
		println("external input init...");
		externalInputs=n.getExternalInputs();
		timeVectorInit();
		timeMatrixInit();
		amplitudeMatrixInit();
		println("external input initialized.");
	}
	
	private void timeVectorInit(){
		for (int i=0; i<nBins; ++i){
			timeVector[i]=i*timeStep;
		}
	}
	
	private void timeMatrixInit(){
		timeMatrix = new NiceQueue[externalInputs];
	}
	
	
	/**
	 * setup the amplitude matrix and initialization of the timeMatrix
	 * 
	 * The behavior depends on the type of the external input.
	 * 
	 * In the Constant type case, we have a fixed number of stimulus (set through the fireDuration
	 * variable) which spikes at a fixed timestep.
	 * 
	 */
	private void amplitudeMatrixInit(){
		amplitudeMatrix = new Double[externalInputs][nBins];
		for (int i=0; i<externalInputs; ++i){
			if (timeMatrix[i]==null)
				timeMatrix[i]=new NiceQueue("timeMatrix-"+i+"-"+n.getId());
			if (type==POISSON){
				for (int j=0; j<nBins; ++j){
					if (Math.random()<(firingRate*timeStep)){
						timeMatrix[i].insert(EXTERNAL_INPUT_TIME_ZERO+(j*timeStep), new Long(j));
						++externalSpikes;
						amplitudeMatrix[i][j]=externalAmplitude;
					}
				}
			}
			if (type==CONSTANT){
				for (int j=0; j<fireDuration; ++j){
					timeMatrix[i].insert(EXTERNAL_INPUT_TIME_ZERO+(j*timeStep),new Long(j));
					++externalSpikes;
					amplitudeMatrix[i][j]=externalAmplitude;
				}
			}
		 }
	}
	
	
	public Double getAmplitudeValue(int extNeuron, int bin){
		if (type==NOISE)// || type==CONSTANT )
			return externalAmplitude;
		if (type==CONSTANT)
			bin=amplitudeMatrix[extNeuron].length-1;
		return amplitudeMatrix[extNeuron][bin];
	}
	
	public NiceNode extractMinSpikeTime(int extNeuron){
		if ((type==NOISE) && (Math.random()<0.3))
			return new NiceNode(new Long(extNeuron),Math.random()*timeStep);
		//System.out.println("[ext input debug]------------------->"+extNeuron+", sw:"+sw.getId());
		--externalSpikes;
		return timeMatrix[extNeuron].extractMin();
	}
	
	public void setSpikeTime(int neuron, int bin, Double time){
		timeMatrix[neuron].insert(time, new Long(bin));
	}
	
	public int getTimeStep(){
		return timeStep;
	}
	
	protected Integer getExternalInputsNum(){
		return externalInputs;
	}
	
	public int getExternalSpikesInQueue(){
		return externalSpikes;
	}
	
	public Integer getFireDuration(){
		return fireDuration;
	}
	
	public void printSpikeTimeMatrix(){
		println("printing external inputs spike time matrix:");
		for (int i=0; i<externalInputs; ++i){
			if (timeMatrix[i].size()>0)
				System.out.println("bin["+i+"]:");
			timeMatrix[i].printQueue();
			if (timeMatrix[i].size()>0)
				System.out.println();
		}
	}
	
	private void println(String s){
		if (verbose)
			System.out.println(TAG+s);
	}
	
	
	
}

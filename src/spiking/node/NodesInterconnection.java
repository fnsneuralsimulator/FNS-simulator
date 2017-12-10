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

import spiking.controllers.node.NodeThread;
import utils.constants.Constants;
import utils.tools.IntegerCouple;

public class NodesInterconnection extends IntegerCouple{
	
	//the sm interconnection probability
	private Double weight;
	private Double length= Constants.LENGTH_DEF_VAL;
	private Double amplitude=1.0;
	private Double amplitudeStdDeviation=0.0;
	private Double lengthShapeParameter=null;
	
	
	public NodesInterconnection(NodeThread r1, NodeThread r2, Double weight){
		super(r1.getNodeId() ,r2.getNodeId());
		this.weight=weight;
	}
	
	public NodesInterconnection(Integer r1, Integer r2, Double weight){
		super(r1,r2);
		this.weight=weight;
	}
	
	public Double getNe_en_ratio(){
		return weight;
	}
	
	public void setLength(Double length){
		this.length=length;
	}
	
	public Double getLength(){
		if (length==null){
			System.out.println("[NODES INTERCONNECTION WARNING] length 0 error");
			System.exit(1);
		}
		return length;
	}

	public Double getMu_w() {
		return amplitude;
	}

	public void setAmplitude(Double amplitude) {
		this.amplitude = amplitude;
	}
	
	public Double getSigma_w(){
		return amplitudeStdDeviation;
	}
	
	public void setAmplitudeStdDeviation(Double amplitudeStdDeviation) {
		this.amplitudeStdDeviation = amplitudeStdDeviation;
	}
	

	public Double getLengthShapeParameter() {
		return lengthShapeParameter;
	}

	public void setLengthShapeParameter(Double lengthShapeParameter) {
		this.lengthShapeParameter = lengthShapeParameter;
	}
	
	

}

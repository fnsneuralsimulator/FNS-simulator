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

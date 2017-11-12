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

import java.io.Serializable;

public class SpikingSynapse implements Serializable{

	
	private static final long serialVersionUID = -7182857769712861723L;
	private Synapse s;
	private Double fireTime;
	private Double burnTime;
	private Boolean fromExternalSource;
	private Double fromState;
	private Double stepInState;
	private Double postSynapticWeight;
	private Double presynapticWeight;
	private Double instantToFire;
	
	public SpikingSynapse(
			Synapse s, 
			Double burnTime, 
			Boolean externalSource, 
			Double fromState, 
			Double stepInState, 
			Double postsynapticWeight, 
			Double presynapticWeight, 
			Double timeToFire, 
			Double fireTime){
		this.s=s;
		this.burnTime=burnTime;
		this.fromExternalSource=externalSource;
		this.fromState=fromState;
		this.stepInState=stepInState;
		this.postSynapticWeight=postsynapticWeight;
		this.presynapticWeight=presynapticWeight;
		this.instantToFire=timeToFire;
		this.fireTime=fireTime;
	}

	public Synapse getS() {
		return s;
	}

	public Double getFireTime() {
		return fireTime;
	}
	
	public Boolean externalSource(){
		return fromExternalSource;
	}

	public Double getBurnTime() {
		return burnTime;
	}

	public void setBurnTime(Double burnTime) {
		this.burnTime = burnTime;
	}

	public Boolean getFromExternalSource() {
		return fromExternalSource;
	}
	
	public Double getFromState(){
		return fromState;
	}

	public Double getStepInState() {
		return stepInState;
	}

	public Double getPostSynapticWeight() {
		return postSynapticWeight;
	}

	public Double getPresynapticWeight() {
		return presynapticWeight;
	}

	public Double getInstantToFire() {
		return instantToFire;
	}
	
	
	
}

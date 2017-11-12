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

package spiking.internode;

import java.io.Serializable;

import spiking.node.Synapse;

public class InterNodeSpike implements Serializable{
	
	
	private static final long serialVersionUID = -6892509210757020267L;
	
	private Synapse syn;
	private Double burnTime;
	private Double fireTime;
	private Double axonalDelay;
	
	public InterNodeSpike (Synapse syn, Double burnTime, Double fireTime, Double axonalDelay){
		this.syn=new Synapse(
				syn.getAxonNodeId(), 
				syn.getAxonNeuronId(), 
				syn.getDendriteNodeId(), 
				syn.getDendriteNeuronId(), 
				syn.getLength(), 
				syn.getAmplitude(), 
				true);
		this.burnTime=burnTime;
		this.fireTime=fireTime;
		this.axonalDelay=axonalDelay;
	}

	public Synapse getSyn() {
		return syn;
	}

	public Double getBurnTime() {
		return burnTime;
	}
	
	public Double getFireTime(){
		return fireTime;
	}
	
	public Double getAxonalDelay(){
		return axonalDelay;
	}
	
	

}

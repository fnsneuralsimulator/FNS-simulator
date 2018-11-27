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


package spiking.node.spikes;

import java.io.Serializable;

import spiking.node.Synapse;

public class FixedBurnSpike implements Comparable<FixedBurnSpike>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 202189250921075L;
	private Synapse syn;
	private Double burnTime;
	private Double fireTime;
	
	public FixedBurnSpike(Synapse syn, Double burnTime, Double fireTime) {
		this.syn=syn;
		this.burnTime=burnTime;
	}

	public Synapse getSyn() {
		return syn;
	}

	public Double getBurnTime() {
		return burnTime;
	}
	
	public Double getFireTime() {
		return fireTime;
	}

	public int compareTo(FixedBurnSpike node){
        return Double.compare(burnTime,node.getBurnTime());
    }
	
	public String toString() {
		return "fixed burn spike: "+syn+", time to burn: "+burnTime;
	}


}



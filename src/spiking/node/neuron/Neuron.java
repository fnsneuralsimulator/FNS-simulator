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

package spiking.node.neuron;

public class Neuron {

//	private Integer index;
	private Double state;
	private Double timeToFire; 
	private Double lastFiringTime; 
	private Double lastBurningTime; 
	private Double preSynapticWeight;
	
	
	
	public Double getState() {
		return state;
	}
	public void setState(Double state) {
		this.state = state;
	}
	public Double getTimeToFire() {
		return timeToFire;
	}
	public void setTimeToFire(Double timeToFire) {
		this.timeToFire = timeToFire;
	}
	public Double getLastFiringTime() {
		return lastFiringTime;
	}
	public void setLastFiringTime(Double lastFiringTime) {
		this.lastFiringTime = lastFiringTime;
	}
	public Double getLastBurningTime() {
		return lastBurningTime;
	}
	public void setLastBurningTime(Double lastBurningTime) {
		this.lastBurningTime = lastBurningTime;
	}
	public Double getPreSynapticWeight() {
		return preSynapticWeight;
	}
	public void setPreSynapticWeight(Double preSynapticWeight) {
		this.preSynapticWeight = preSynapticWeight;
	}
	
	
	
	
	
}

/**
* "FNS" (Firnet NeuroScience), ver.3.x
*				
* FNS is an event-driven Spiking Neural Network framework, oriented 
* to data-driven neural simulations.
*
* (c) 2020, Gianluca Susi, Emanuele Paracone, Mario Salerno, 
* Alessandro Cristini, Fernando Maestú.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, 
* Mario Salerno, Fernando Maestú, Ernesto Pereda (2020). 
* "FNS: an event-driven spiking neural network simulator based on the 
* LIFL neuron model". 
* Laboratory of Cognitive and Computational Neuroscience, UPM-UCM 
* Centre for Biomedical Technology, Technical University of Madrid; 
* University of Rome "Tor Vergata".   
* Paper under review.
*
* FNS is free software: you can redistribute it and/or modify it 
* under the terms of the GNU General Public License version 3 as 
* published by the Free Software Foundation.
*
* FNS is distributed in the hope that it will be useful, but WITHOUT 
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
* or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with FNS. If not, see <http://www.gnu.org/licenses/>.
* 
* -----------------------------------------------------------
*  
* Website:   http://www.fnsneuralsimulator.org
* 
* Contacts:  fnsneuralsimulator (at) gmail.com
*	    gianluca.susi82 (at) gmail.com
*	    emanuele.paracone (at) gmail.com
*
*
* -----------------------------------------------------------
* -----------------------------------------------------------
**/

package spiking.node.neuron;

public class Neuron {

//  private Integer index;
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

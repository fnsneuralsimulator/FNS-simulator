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

package spiking.node;

import java.io.Serializable;

public class SpikingSynapse implements Serializable{

  
  private static final long serialVersionUID = -7182857769712861723L;
  //private Synapse s;
  private Long firingNeuronId;
  private Long firingNodeId;
  private Long burningNeuronId;
  private Long burningNodeId;
  private Double fireTime;
  private Double burnTime;
  private Boolean fromExternalSource;
  private Double fromState;
  private Double stepInState;
  private Double postSynapticWeight;
  private Double presynapticWeight;
  private Double instantToFire;
  
  public SpikingSynapse(
      //Synapse s, 
      Long firingNeuronId,
      Long firingNodeId,
      Long burningNeuronId,
      Long burningNodeId,
      Double burnTime, 
      Boolean externalSource, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire, 
      Double fireTime){
    //this.s=s;
    this.firingNeuronId=firingNeuronId;
    this.firingNodeId=firingNodeId;
    this.burningNeuronId=burningNeuronId;
    this.burningNodeId=burningNodeId;
    this.burnTime=burnTime;
    this.fromExternalSource=externalSource;
    this.fromState=fromState;
    this.stepInState=stepInState;
    this.postSynapticWeight=postsynapticWeight;
    this.presynapticWeight=presynapticWeight;
    this.instantToFire=timeToFire;
    this.fireTime=fireTime;
  }

  //public Synapse getS() {
  //  return s;
  //}

  public Double getFireTime() {
    return fireTime;
  }
  
  public Boolean _externalSource(){
    return fromExternalSource;
  }

  public Double getBurnTime() {
    return burnTime;
  }

  public void setBurnTime(Double burnTime) {
    this.burnTime = burnTime;
  }

  public Boolean _getFromExternalSource() {
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

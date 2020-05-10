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
//    this.syn=new Synapse(
//        syn.getAxonNodeId(), 
//        syn.getAxonNeuronId(), 
//        syn.getBurningNodeId(), 
//        syn.getBurningNeuronId(), 
//        syn.getLength(), 
//        syn.getPostSynapticWeight(),
//        syn.getPreSynapticWeight(),
//        false,
//        true);
    this.syn=syn;
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

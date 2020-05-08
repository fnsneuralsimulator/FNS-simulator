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


package spiking.node.plasticity;

import java.util.ArrayList;

import spiking.node.Synapse;
import utils.tools.IntegerCouple;

public class Afferent {
  private Synapse syn;
  //false when a LTP occurs
  private Boolean ltpFlag = true;
  //false when a LTD occurs
  private Boolean ltdFlag = true;
  //time to last firing of the firing neuron
  private Double lastFireTime;
  //store the old value of postsynaptic weight
  private Double oldPostSynapticWeight;
  private static final Double pwMax = 1.0;
  //
//  private ArrayList<Integer> 
  
  //kkk la firma precedente anteponeva i burning:
  //Long burningNeuronId, Long spikingNeuronId, Double lastFireTime, Double oldPostSynapticWeight
  public Afferent(Synapse syn, Double lastFireTime, Double oldPostSynapticWeight) {
    this.syn=syn;
    this.lastFireTime = lastFireTime;
    this.oldPostSynapticWeight = oldPostSynapticWeight;
  }
  

  public Long getBurningNeuron(){
    return syn.getBurning();
  }
  
  public Long getSpikingNeuron(){
    return syn.getFiring();
  }

  public Boolean getLtpFlag() {
    return ltpFlag;
  }

  public void setLtpFlag(Boolean ltpFlag) {
    this.ltpFlag = ltpFlag;
  }

  public Boolean getLtdFlag() {
    return ltdFlag;
  }

  public void setLtdFlag(Boolean ltdFlag) {
    this.ltdFlag = ltdFlag;
  }

  public Double getLastFireTime() {
    return lastFireTime;
  }

  public void setLastFireTime(Double lastFireTime) {
    this.lastFireTime = lastFireTime;
  }

  public Double getOldPostSynapticWeight() {
    return oldPostSynapticWeight;
  }
  
//  public IntegerCouple asIntegerCouple(){
//    return new IntegerCouple(burningNeuronId, spikingNeuronId);
//  }
  
  public Synapse getSynapse(){
    return syn;
  }

  public void setOldPostsynapticWeight(Double oldPostSynapticWeight) {
    //check Pw upper bound is not lower than 0
      this.oldPostSynapticWeight = (oldPostSynapticWeight>=0)? 
          ((oldPostSynapticWeight>pwMax)? pwMax:oldPostSynapticWeight ) 
          : 0.0;
  }
  
  
  
  
  
  

}

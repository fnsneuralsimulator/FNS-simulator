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


package utils.configuration;

public class NodeInterconnectionCfg {
  
  private Integer sourceNodeId;
  private Integer destinationNodeId;
  private Double connection_probability;
  
  public NodeInterconnectionCfg(){}
  
  public NodeInterconnectionCfg(Integer sw1, Integer sw2, Double prob){
    sourceNodeId=sw1;
    destinationNodeId=sw2;
    connection_probability=prob;
  }

  public Integer getSrcNodeId() {
    return sourceNodeId;
  }

  public void setSrcNodeId(Integer srcNodeId) {
    this.sourceNodeId = srcNodeId;
  }

  public Integer getDstNodeId() {
    return destinationNodeId;
  }

  public void setDstNodeId(Integer second_small_world_id) {
    this.destinationNodeId = second_small_world_id;
  }

  public Double getConnection_probability() {
    return connection_probability;
  }

  public void setConnection_probability(Double connection_probability) {
    this.connection_probability = connection_probability;
  }  

}

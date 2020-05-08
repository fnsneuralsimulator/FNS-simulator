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

public class RegionInterconnectionCfg {
  
  private Integer sourceRegionId;
  private Integer destinationRegionId;
  private Double connection_probability;
  
  public RegionInterconnectionCfg(){}
  
  public RegionInterconnectionCfg(Integer sw1, Integer sw2, Double prob){
    sourceRegionId=sw1;
    destinationRegionId=sw2;
    connection_probability=prob;
  }

  public Integer getSrcRegionId() {
    return sourceRegionId;
  }

  public void setSrcRegionId(Integer srcRegionId) {
    this.sourceRegionId = srcRegionId;
  }

  public Integer getDstRegionId() {
    return destinationRegionId;
  }

  public void setDstRegionId(Integer second_small_world_id) {
    this.destinationRegionId = second_small_world_id;
  }

  public Double getConnection_probability() {
    return connection_probability;
  }

  public void setConnection_probability(Double connection_probability) {
    this.connection_probability = connection_probability;
  }  

}

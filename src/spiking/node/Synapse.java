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
import java.util.Comparator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



public class Synapse implements Comparable<Synapse>, Serializable{
  
  
  
  private static final long serialVersionUID = -4428703256832002027L;
  private final static String TAG = "[Syapse] ";
  private final static Boolean verbose = true;
  private Long firingNeuronId;
  private Long burningNeuronId;
  private Integer firingNodeId;
  private Integer burningNodeId;
  private Boolean fromExternalNode=false;
  private Boolean fromExternalInput=false;
  private Double length;
  private Double mu_w;
  private Double presynaptic_w;
  private Double lastBurningTime;
  
  public Synapse(
      Integer firingNodeId, 
      Long firingNeuronId, 
      Integer burningNodeId, 
      Long burningNeuronId, 
      Double length, 
      Double mu_w,
      Double presynaptic_w,
      Boolean fromExternalInput,
      Boolean fromExternalNode) {
    this.firingNodeId = firingNodeId;
    this.firingNeuronId = firingNeuronId;
    this.burningNodeId = burningNodeId;
    this.burningNeuronId = burningNeuronId;
    this.fromExternalNode=fromExternalNode;
    this.length=length;
    this.presynaptic_w= presynaptic_w;
    this.fromExternalInput=fromExternalInput;
    this.setPostsynapticWeight(mu_w);    
  }
  
  
  public Boolean fromExternalNode(){
    return fromExternalNode;
  }
  
  
  public Long getAxonNeuronId() {
    return firingNeuronId;
  }
  
  public Long getFiring(){
    return firingNeuronId;
  }

  public void setAxonNeuronId(Long firingNeuronId) {
    this.firingNeuronId = firingNeuronId;
  }

  public Long getBurningNeuronId() {
    return burningNeuronId;
  }
  
  public Long getBurning(){
    return burningNeuronId;
  }

  public void setBurningNeuronId(Long burningNeuronId) {
    this.burningNeuronId = burningNeuronId;
  }

  public Integer getAxonNodeId() {
    return firingNodeId;
  }

  public Integer getFiringNodeId() {
    return firingNodeId;
  }

  public void setAxonNodeId(Integer firingNodeId) {
    this.firingNodeId = firingNodeId;
  }

  public Integer getBurningNodeId() {
    return burningNodeId;
  }

  public void setBurningNodeId(Integer burningNodeId) {
    this.burningNodeId = burningNodeId;
  }


  public Double getLength() {
    return length;
  }

  public void setLenght(Double length) {
    this.length = length;
  }
  
  public Double getPostSynapticWeight() {
    return (mu_w==null)?1.0:mu_w;
  }

  public void setPostsynapticWeight(Double post_synaptic_weight) {
    if ((post_synaptic_weight!=null)&&(post_synaptic_weight==1.0))
      this.mu_w=null;
    else
      this.mu_w = post_synaptic_weight;
  }
  
  public Double getPreSynapticWeight() {
    return presynaptic_w;
  }

  public Boolean fromExternal(){
    return (fromExternalNode||fromExternalInput);
  }
  
  public Double getLastBurningTime() {
    return lastBurningTime;
  }

  public void setLastBurningTime(Double lastBurningTime) {
    this.lastBurningTime = lastBurningTime;
  }

  public void resetLastBurningTime() {
    this.lastBurningTime = null;
  }

  @Override
  public String toString() {
    return "[firing:" + firingNodeId+"-"+firingNeuronId
        + ", burning:"+burningNodeId +"-"+ burningNeuronId 
        +" from external:"+ fromExternalNode+"]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false; 
    if (obj == this)
      return true; 
    if (obj.getClass() != getClass()) 
      return false;
    
    Synapse rhs = (Synapse) obj;
    return new EqualsBuilder()
        .append(firingNodeId, rhs.firingNodeId)
        .append(firingNeuronId, rhs.firingNeuronId)
        .append(burningNodeId, rhs.burningNodeId)
        .append(burningNeuronId, rhs.burningNeuronId).isEquals();
  }

  @Override
  public int hashCode() {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
       // ideally different for each class
    return new HashCodeBuilder(17, 37).append(7l*firingNeuronId+9l+firingNodeId)
        .append(burningNeuronId*burningNodeId+3l*firingNeuronId+17l).toHashCode();    
  }
  
  @Override
  public int compareTo(Synapse o) {
    if (this==o)
      return 0;

    int retval = firingNeuronId.compareTo(o.getAxonNeuronId());
    if (retval!=0)
      return retval;      
    retval = burningNeuronId.compareTo(o.getBurningNeuronId());
    if (retval!=0)
      return retval;    
    retval = firingNodeId.compareTo(o.getAxonNodeId());
    if (retval!=0)
      return retval;
    retval = burningNodeId.compareTo(o.getBurningNodeId());  
    
    return retval;
  }

  public Boolean fromExternalInput() {
    return fromExternalInput;
  }
  
  public Integer fromExternalInputInteger() {
    return fromExternalInput ? 1 : 0;
  }

  public void setFromExternalInput(Boolean fromExternalInput) {
    this.fromExternalInput = fromExternalInput;
  }

  
  private void println(String s){
    if (verbose){
      System.out.println(TAG+s);
    }    
  }  
  
  

}

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

package spiking.node;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



public class Synapse implements Comparable<Synapse>, Serializable{
	
	
	
	private static final long serialVersionUID = -4428703256832002027L;
	private final static String TAG = "[Syapse] ";
	private final static Boolean verbose = true;
	private Long axonNeuronId;
	private Long dendriteNeuronId;
	private Integer axonRegionId;
	private Integer dendriteRegionId;
	private Boolean fromExternalNode=false;
	private Boolean fromExternalInput=false;
	private Double length;
	private Double mu_w;
	private Double presynaptic_w;
	private Double lastBurningTime;
	
	
//	public Synapse(
//			Integer axonRegionId, 
//			Long axonNeuronId, 
//			Integer dendriteRegionId, 
//			Long dendriteNeuronId, 
//			Double length, 
//			Double mu_w,
//			Double presynaptic_w,
//			Boolean fromExternalInput
//			) {
//		this.axonRegionId = axonRegionId;
//		this.axonNeuronId = axonNeuronId;
//		this.dendriteRegionId = dendriteRegionId;
//		this.dendriteNeuronId = dendriteNeuronId;
//		this.length=length;
//		this.presynaptic_w=presynaptic_w;
//		this.fromExternalInput=fromExternalInput;
//		this.setPostsynapticWeight(mu_w);
//		
//	}
	
	public Synapse(
//			Boolean torem,
			Integer axonRegionId, 
			Long axonNeuronId, 
			Integer dendriteRegionId, 
			Long dendriteNeuronId, 
			Double length, 
			Double mu_w,
			Double presynaptic_w,
			Boolean fromExternalInput,
			Boolean fromExternalNode) {
		this.axonRegionId = axonRegionId;
		this.axonNeuronId = axonNeuronId;
		this.dendriteRegionId = dendriteRegionId;
		this.dendriteNeuronId = dendriteNeuronId;
		this.fromExternalNode=fromExternalNode;
		this.length=length;
		this.presynaptic_w= presynaptic_w;
		this.fromExternalInput=fromExternalInput;
		this.setPostsynapticWeight(mu_w);		
	}
	
//	public Synapse(
//			Integer axonRegionId, 
//			Long axonNeuronId, 
//			Integer dendriteRegionId, 
//			Long dendriteNeuronId, 
//			Double length, 
//			Boolean fromExternalInput ) {
//		this.axonRegionId = axonRegionId;
//		this.axonNeuronId = axonNeuronId;
//		this.dendriteRegionId = dendriteRegionId;
//		this.dendriteNeuronId = dendriteNeuronId;
//		this.setFromExternalInput(fromExternalInput);
//		this.length=length;
//		this.setPostsynapticWeight(mu_w);
//	}
	
	public Boolean fromExternalNode(){
		return fromExternalNode;
	}
	
	
	public Long getAxonNeuronId() {
		return axonNeuronId;
	}
	
	public Long getFiring(){
		return axonNeuronId;
	}

	public void setAxonNeuronId(Long axonNeuronId) {
		this.axonNeuronId = axonNeuronId;
	}

	public Long getDendriteNeuronId() {
		return dendriteNeuronId;
	}
	
//	public BurningNeuron getBurning(){
//		return new BurningNeuron(dendriteRegionId, dendriteNeuronId);
//	}
	
	public Long getBurning(){
		return dendriteNeuronId;
	}

	public void setDendriteNeuronId(Long dendriteNeuronId) {
		this.dendriteNeuronId = dendriteNeuronId;
	}

	public Integer getAxonNodeId() {
		return axonRegionId;
	}

	public void setAxonRegionId(Integer axonRegionId) {
		this.axonRegionId = axonRegionId;
	}

	public Integer getDendriteNodeId() {
		return dendriteRegionId;
	}

	public void setDendriteRegionId(Integer dendriteRegionId) {
		this.dendriteRegionId = dendriteRegionId;
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
		return "[firing:" + axonRegionId+"-"+axonNeuronId
				+ ", burning:"+dendriteRegionId +"-"+ dendriteNeuronId 
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
				.append(axonRegionId, rhs.axonRegionId)
				.append(axonNeuronId, rhs.axonNeuronId)
				.append(dendriteRegionId, rhs.dendriteRegionId)
				.append(dendriteNeuronId, rhs.dendriteNeuronId).isEquals();
	}

	@Override
	public int hashCode() {
		// you pick a hard-coded, randomly chosen, non-zero, odd number
	     // ideally different for each class
		return new HashCodeBuilder(17, 37).append(7l*axonNeuronId+9l+axonRegionId)
				.append(dendriteNeuronId*dendriteRegionId+3l*axonNeuronId+17l).toHashCode();		
	}
	
	@Override
	public int compareTo(Synapse o) {
		if (this==o)
			return 0;

		int retval = axonNeuronId.compareTo(o.getAxonNeuronId());
		if (retval!=0)
			return retval;			
		retval = dendriteNeuronId.compareTo(o.getDendriteNeuronId());
		if (retval!=0)
			return retval;		
		retval = axonRegionId.compareTo(o.getAxonNodeId());
		if (retval!=0)
			return retval;
		retval = dendriteRegionId.compareTo(o.getDendriteNodeId());	
		
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

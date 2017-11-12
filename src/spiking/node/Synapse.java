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

package spiking.node;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



public class Synapse implements Comparable<Synapse>, Serializable{
	
	
	
	private static final long serialVersionUID = -4428703256832002027L;
	private Long axonNeuronId;
	private Long dendriteNeuronId;
	private Integer axonRegionId;
	private Integer dendriteRegionId;
	private Boolean fromExternalRegion=false;
	private Boolean fromExternalInput=false;
	private Double length;
	private Double amplitude;
	
//	public Synapse(Integer axonRegionId, Long axonNeuronId, Integer dendriteRegionId, Long dendriteNeuronId) {
//		this.axonRegionId = axonRegionId;
//		this.axonNeuronId = axonNeuronId;
//		this.dendriteRegionId = dendriteRegionId;
//		this.dendriteNeuronId = dendriteNeuronId;
//		
//	}
//	
//	public Synapse(Integer axonRegionId, Long axonNeuronId, Integer dendriteRegionId, Long dendriteNeuronId, Boolean fromExternal ) {
//		this.axonRegionId = axonRegionId;
//		this.axonNeuronId = axonNeuronId;
//		this.dendriteRegionId = dendriteRegionId;
//		this.dendriteNeuronId = dendriteNeuronId;
//		this.fromExternal=fromExternal;
//	}
	
	public Synapse(Integer axonRegionId, Long axonNeuronId, Integer dendriteRegionId, Long dendriteNeuronId, Double length, Double amplitude) {
		this.axonRegionId = axonRegionId;
		this.axonNeuronId = axonNeuronId;
		this.dendriteRegionId = dendriteRegionId;
		this.dendriteNeuronId = dendriteNeuronId;
		this.length=length;
		this.setAmplitude(amplitude);
		
	}
	
	public Synapse(Integer axonRegionId, Long axonNeuronId, Integer dendriteRegionId, Long dendriteNeuronId, Double length, Double amplitude, Boolean fromExternalRegion) {
		this.axonRegionId = axonRegionId;
		this.axonNeuronId = axonNeuronId;
		this.dendriteRegionId = dendriteRegionId;
		this.dendriteNeuronId = dendriteNeuronId;
		this.fromExternalRegion=fromExternalRegion;
		this.length=length;
		this.setAmplitude(amplitude);
		
	}
	
	public Synapse(Integer axonRegionId, Long axonNeuronId, Integer dendriteRegionId, Long dendriteNeuronId, Double length, Boolean fromExternalInput ) {
		this.axonRegionId = axonRegionId;
		this.axonNeuronId = axonNeuronId;
		this.dendriteRegionId = dendriteRegionId;
		this.dendriteNeuronId = dendriteNeuronId;
		this.setFromExternalInput(fromExternalInput);
		this.length=length;
		this.setAmplitude(amplitude);
	}
	
	public Boolean fromExternalRegion(){
		return fromExternalRegion;
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
	
	public Double getAmplitude() {
		return (amplitude==null)?1.0:amplitude;
	}

	public void setAmplitude(Double amplitude) {
		if ((amplitude!=null)&&(amplitude==1.0))
			this.amplitude=null;
		else
			this.amplitude = amplitude;
	}
	
	public Boolean fromExternal(){
		return (fromExternalRegion||fromExternalInput);
	}

	@Override
	public String toString() {
		return "[firing:" + axonRegionId+"-"+axonNeuronId
				+ ", burning:"+dendriteRegionId +"-"+ dendriteNeuronId +" from external:"+ fromExternalRegion+"]";
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

	public void setFromExternalInput(Boolean fromExternalInput) {
		this.fromExternalInput = fromExternalInput;
	}

	
	

}

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


package spiking.controllers.node;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import utils.tools.IntegerCouple;

public class NodeLink implements Comparable<NodeLink>{
	
	private Integer srcNodeId;
	private Integer dstNodeId;
	private Integer srcNeuronId;
	private Integer dstNeuronId;
	
	public NodeLink (Integer srcNodeId, Integer srcNeuronId, 
			Integer dstNodeId, Integer dstNeuronId){
		this.srcNodeId = srcNodeId;
		this.srcNeuronId = srcNeuronId;
		this.dstNodeId = dstNodeId;
		this.dstNeuronId = dstNeuronId;
	}
	
	@Override
	public String toString() {
		return "Node Link [src=" + srcNodeId +"-"+srcNeuronId+", dst=" + dstNodeId+"-"+dstNeuronId + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false; 
		if (obj == this)
			return true; 
		if (obj.getClass() != getClass()) 
			return false;
		
		NodeLink rhs = (NodeLink) obj;
		return new EqualsBuilder()
//				.appendSuper(super.equals(obj))
				.append(srcNodeId, rhs.srcNodeId)
				.append(srcNeuronId, rhs.srcNeuronId)
				.append(dstNodeId, rhs.dstNodeId)
				.append(dstNeuronId, rhs.dstNeuronId).isEquals();
	}

	@Override
	public int hashCode() {
		// you pick a hard-coded, randomly chosen, non-zero, odd number
	     // ideally different for each class
	     return new HashCodeBuilder(17, 37).append(7*dstNeuronId<<srcNodeId+srcNeuronId).append(srcNeuronId<<dstNodeId+3*dstNeuronId).toHashCode();		
	}
	
	@Override
	public int compareTo(NodeLink o) {
		if (this==o)
			return 0;
		int retval = srcNodeId.compareTo(o.getSrcNodeId());
		if (retval!=0)
			return retval;
		retval = srcNeuronId.compareTo(o.getSrcNeuronId());
		if (retval!=0)
			return retval;
		retval = dstNodeId.compareTo(o.getDstNodeId());
		if (retval!=0)
			return retval;
		retval = dstNeuronId.compareTo(o.getDstNeuronId());
		if (retval!=0)
			return retval;
		return retval;
	}

	public Integer getSrcNodeId() {
		return srcNodeId;
	}

	public Integer getDstNodeId() {
		return dstNodeId;
	}

	public Integer getSrcNeuronId() {
		return srcNeuronId;
	}

	public Integer getDstNeuronId() {
		return dstNeuronId;
	}


	
	
	

}

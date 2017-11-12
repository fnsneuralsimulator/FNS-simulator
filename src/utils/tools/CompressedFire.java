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

package utils.tools;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CompressedFire implements Comparable<CompressedFire>{
	
	private Integer compressedNeuronId;
	private Double compressedFireTime;
	
	public CompressedFire(int regionId, long neuronId, double fireTime, Long maxN, Double reducingFactor ){
		this.compressedNeuronId= (int) (new Double((regionId*maxN)+neuronId)*reducingFactor);
		this.compressedFireTime=fireTime;
	}

	
	public Integer getCompressedNeuronId() {
		return compressedNeuronId;
	}

	public Double getCompressedFireTime() {
		return compressedFireTime;
	}
	
	@Override
	public String toString() {
		return "compressedFire [id:" + compressedNeuronId + ", t:" + compressedFireTime + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) 
			return false; 
		if (obj == this)
			return true; 
		if (obj.getClass() != getClass()) 
			return false;
		
		CompressedFire cf = (CompressedFire) obj;
		return new EqualsBuilder()
//				.appendSuper(super.equals(obj))
				.append(compressedNeuronId, cf.compressedNeuronId)
				.append(compressedFireTime, cf.compressedFireTime).isEquals();
	}

	@Override
	public int hashCode() {
		// you pick a hard-coded, randomly chosen, non-zero, odd number
	     // ideally different for each class
		
		return new HashCodeBuilder(17, 37).append(7l<<compressedNeuronId+9l).append(compressedFireTime+compressedNeuronId+compressedFireTime+17l).toHashCode();		
	}
	
	@Override
	public int compareTo(CompressedFire o) {
		if (this==o)
			return 0;
		int retval = compressedNeuronId.compareTo(o.getCompressedNeuronId());
		if (retval!=0)
			return retval;
		retval = compressedFireTime.compareTo(o.getCompressedFireTime());			
		return retval;
	}
	
	
	

}

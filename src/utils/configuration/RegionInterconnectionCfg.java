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

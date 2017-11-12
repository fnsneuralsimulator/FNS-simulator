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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "node")
public class NodeCfg {
	private Integer id;
	private Double rewiring_P;
	private Integer k;
	private Long n;
	private Double R;
	private Integer external_inputs_number;
	private Integer external_inputs_type;
	private Integer external_inputs_fireduration;
	private Integer external_inputs_timestep;
	private Double external_inputs_firerate;
	private Double external_inputs_amplitude;
	private Boolean small_world_topology;
	private Double mu_w;
	private NeuManCfg neuron_manager;
	
//	public NodeCfg(){}
//	public NodeCfg(
//			Integer id, 
//			Double prew, 
//			Integer k, 
//			Long n, 
//			Double R, 
//			Integer external, 
//			Integer externalInputType, 
//			Boolean smallWorld){
//		this.id=id;
//		this.rewiring_P=prew;
//		this.k=k;
//		this.n=n;
//		this.R=R;
//		this.external_inputs_number=external;
//		this.external_inputs_type=externalInputType;
//		this.small_world_topology=smallWorld;
//	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double get_rewiring_P() {
		return rewiring_P;
	}

	public void set_rewiring_P(Double prew) {
		this.rewiring_P = prew;
	}

	public Integer getK() {
		return k;
	}

	public void setK(Integer k) {
		this.k = k;
	}

	public Long getN() {
		return n;
	}

	public void setN(Long n) {
		this.n = n;
	}

	public Double getExcitatory_inhibitory_ratio() {
		return R;
	}

	public void setExcitatory_inhibitory_ratio(Double excitRatio) {
		this.R = excitRatio;
	}

	public Integer getExternal_inputs_number() {
		return external_inputs_number;
	}

	public void setExternal_inputs_number(Integer external_inputs_number) {
		this.external_inputs_number = external_inputs_number;
	}

	public Boolean getSmall_world_topology() {
		return small_world_topology;
	}

	public void setSmall_world_topology(Boolean smallWorld) {
		this.small_world_topology = smallWorld;
	}

	public Integer getExternal_inputs_type() {
		return external_inputs_type;
	}

	public void setExternal_inputs_type(Integer external_inputs_type) {
		this.external_inputs_type = external_inputs_type;
	}

	public Integer getExternal_inputs_timestep() {
		return external_inputs_timestep;
	}

	public void setExternal_inputs_timestep(Integer external_inputs_timestep) {
		this.external_inputs_timestep = external_inputs_timestep;
	}

	public Double getExternal_inputs_firerate() {
		return external_inputs_firerate;
	}

	public void setExternal_inputs_firerate(Double external_inputs_firerate) {
		this.external_inputs_firerate = external_inputs_firerate;
	}
	
	public Double getExternal_inputs_amplitude() {
		return external_inputs_amplitude;
	}

	public void setExternal_inputs_amplitude(Double external_inputs_amplitude) {
		this.external_inputs_amplitude = external_inputs_amplitude;
	}

	public Integer getExternal_inputs_fireduration() {
		return external_inputs_fireduration;
	}

	public void setExternal_inputs_fireduration(Integer external_inputs_fireduration) {
		this.external_inputs_fireduration = external_inputs_fireduration;
	}
	
	public Double getMu_w() {
		return mu_w;
	}

	public void setMu_w(Double mu_w) {
		this.mu_w = mu_w;
	}
	
	public NeuManCfg getNeuron_manager(){
		return neuron_manager;
	}
	
	public void setNeuron_manager(NeuManCfg neuron_manager){
		this.neuron_manager=neuron_manager;
	}
	
}

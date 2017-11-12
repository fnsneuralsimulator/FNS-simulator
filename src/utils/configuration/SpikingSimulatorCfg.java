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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fns_config")
public class SpikingSimulatorCfg {
	
	private int stop;
	private Boolean glob_plasticity;
	private Double avg_neuronal_signal_speed;
	private NeuManCfg global_neuron_manager;
	private ArrayList<NodeCfg> node = new ArrayList<NodeCfg>();
	private ArrayList<RegionInterconnectionCfg> connection = new ArrayList<RegionInterconnectionCfg>();
	private Double glob_rewiring_P;
	private Integer glob_k;
	private Long glob_n;
	private Double glob_R;
	private Boolean glob_small_world_topology=true;
	private Double glob_mu_w;
	private Integer glob_external_inputs_number;
	private Integer glob_external_inputs_type;
	private Integer glob_external_inputs_fireduration;
	private Integer glob_external_inputs_timestep;
	private Double glob_external_inputs_firerate; 
	private Double glob_external_inputs_amplitude; 

	public int getStop() {
		return stop;
	}

	public void setStop(int stop) {
		this.stop = stop;
	}
	
	public Boolean getPlasticity() {
		return glob_plasticity;
	}

	public void setPlasticity(Boolean plasticity) {
		this.glob_plasticity = plasticity;
	}

	public ArrayList<NodeCfg> getNodes() {
		return node;
	}

	public void setNodes(ArrayList<NodeCfg> nodes) {
		this.node = nodes;
	}
	
	public ArrayList<RegionInterconnectionCfg> getConnections() {
		return connection;
	}

	public void setConnections(ArrayList<RegionInterconnectionCfg> connections) {
		this.connection = connections;
	}
	
	
	public NeuManCfg getGlobal_neuron_manager() {
		return global_neuron_manager;
	}

	public void setGlobal_neuron_manager(NeuManCfg global_neuron_manager) {
		this.global_neuron_manager = global_neuron_manager;
	}

	public ArrayList<NodeCfg> getRegion() {
		return node;
	}

	public void setRegion(ArrayList<NodeCfg> region) {
		this.node = region;
	}

	public ArrayList<RegionInterconnectionCfg> getConnection() {
		return connection;
	}

	public void setConnection(ArrayList<RegionInterconnectionCfg> connection) {
		this.connection = connection;
	}

	public Double getGlob_rewiring_P() {
		return glob_rewiring_P;
	}

	public void setGlob_rewiring_P(Double glob_rewiring_P) {
		this.glob_rewiring_P = glob_rewiring_P;
	}

	public Integer getGlob_k() {
		return glob_k;
	}

	public void setGlob_k(Integer glob_k) {
		this.glob_k = glob_k;
	}

	public Double getMu_w() {
		return glob_mu_w;
	}

	public void setMu_w(Double mu_w) {
		this.glob_mu_w = mu_w;
	}

	public Long getGlob_local_n() {
		return glob_n;
	}

	public void setGlob_local_n(Long glob_local_n) {
		this.glob_n = glob_local_n;
	}

	public Double getR() {
		return glob_R;
	}

	public void setR(Double R) {
		this.glob_R = R;
	}

	public Boolean getGlob_small_world_topology() {
		return glob_small_world_topology;
	}

	public void setGlob_small_world_topology(Boolean glob_small_world_topology) {
		this.glob_small_world_topology = glob_small_world_topology;
	}
	

	public Integer getGlob_external_inputs_number() {
		return glob_external_inputs_number;
	}

	public void setGlob_external_inputs_number(Integer glob_external_inputs) {
		this.glob_external_inputs_number = glob_external_inputs;
	}

	public Integer getGlob_external_inputs_type() {
		return glob_external_inputs_type;
	}

	public void setGlob_external_inputs_type(Integer glob_external_inputs_type) {
		this.glob_external_inputs_type = glob_external_inputs_type;
	}

	public Integer getGlob_external_inputs_fireduration() {
		return glob_external_inputs_fireduration;
	}

	public void setGlob_external_inputs_fireduration(Integer glob_external_inputs_fireduration) {
		this.glob_external_inputs_fireduration = glob_external_inputs_fireduration;
	}

	public Integer getGlob_external_inputs_timestep() {
		return glob_external_inputs_timestep;
	}

	public void setGlob_external_inputs_timestep(Integer glob_external_inputs_timestep) {
		this.glob_external_inputs_timestep = glob_external_inputs_timestep;
	}

	public Double getGlob_external_inputs_firerate() {
		return glob_external_inputs_firerate;
	}

	public void setGlob_external_inputs_firerate(Double glob_external_inputs_firerate) {
		this.glob_external_inputs_firerate = glob_external_inputs_firerate;
	}
	
	public Double getGlob_external_inputs_amplitude() {
		return glob_external_inputs_amplitude;
	}
	
	public void setGlob_external_inputs_amplitude(Double glob_external_inputs_amplitude) {
		this.glob_external_inputs_amplitude = glob_external_inputs_amplitude;
	}

	public String toString(){
		StringBuilder sb = new StringBuilder( "stop:"+stop+", ld:"+global_neuron_manager.getD()+
				", d:"+global_neuron_manager.getC()+", kr:"+global_neuron_manager.getT_arp()+"\nregions:");
		for (int i=0; i<node.size();++i)
			sb.append("\n"+i+". "+node.get(i).getId());
		sb.append("\nconnections:");
		for (int i=0; i<connection.size();++i)
			sb.append("\n"+i+". "+connection.get(i).getSrcRegionId()+"->"+
		connection.get(i).getDstRegionId()+", prob:"+connection.get(i).getConnection_probability());
		sb.append("\n\n");
		return sb.toString();
	}

	public Double getAvg_neuronal_signal_speed() {
		return avg_neuronal_signal_speed;
	}
	
	public void setAvg_neuronal_signal_speed(Double avg_Neuronal_Signal_Speed) {
		this.avg_neuronal_signal_speed=avg_Neuronal_Signal_Speed;
	}
	
	
}

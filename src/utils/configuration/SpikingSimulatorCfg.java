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


package utils.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import utils.constants.Constants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fns_config")
public class SpikingSimulatorCfg {
	
	private int stop;
	private Double avg_neuronal_signal_speed;
	private NeuManCfg global_neuron_manager;
	private ArrayList<NodeCfg> node = new ArrayList<NodeCfg>();
	private ArrayList<RegionInterconnectionCfg> connection = new ArrayList<RegionInterconnectionCfg>();
  private Boolean LIF;
  private Boolean exp_decay;
	private Double glob_rewiring_P;
	private Integer glob_k;
	private Long glob_n;
	private Double glob_R;
	private Double glob_mu_w_exc;
	private Double glob_mu_w_inh;
	private Double glob_w_pre_exc;
	private Double glob_w_pre_inh;
	private Integer glob_Bn;
	private Double glob_IBI;
	private Integer glob_external_inputs_number;
	private Integer glob_external_inputs_type;
	private Double glob_external_inputs_time_offset;
	private Integer glob_external_inputs_fireduration;
	private Integer glob_external_inputs_timestep;
	private Double glob_external_inputs_firerate; 
	private Double glob_external_inputs_amplitude; 
	private Boolean glob_plasticity;
	private Double glob_etap = Constants.ETAP;
	private Double glob_etam = Constants.ETAM;
	private Double glob_taup = Constants.TAUP;
	private Double glob_taum = Constants.TAUM;
	private Double glob_pw_max = Constants.PWMAX;
	private Double glob_to = Constants.TO;	

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

  public Boolean getLif(){
    return lif;
  }

  public void setLif(Boolean lif) {
    this.lif=lif;
  }

  public Boolean getExp_decay() {
    return exp_decay;
  }

  public void setExp_decay() {
    this.exp_decay=exp_decay;
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

	public Integer getGlob_Bn() {
		return (glob_Bn!=null) ? glob_Bn : 1;
	}

	public void setGlob_Bn(Integer glob_Bn) {
		this.glob_Bn = glob_Bn;
	}

	public Double getGlob_IBI() {
		return (glob_IBI!=null) ? glob_IBI : 0;
	}

	public void setGlob_IBI(Double glob_IBI) {
		this.glob_IBI = glob_IBI;
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

	public Double getGlob_mu_w_exc() {
		return glob_mu_w_exc;
	}

	public Double getGlob_mu_w_inh() {
		return glob_mu_w_inh;
	}

	public void setGlob_mu_w_exc(Double glob_mu_w_exc) {
		this.glob_mu_w_exc = glob_mu_w_exc;
	}

	public void setGlob_mu_w_inh(Double glob_mu_w_inh) {
		this.glob_mu_w_inh = glob_mu_w_inh;
	}

	public Double getGlob_w_pre_exc() {
		return glob_w_pre_exc;
	}

	public void setGlob_w_pre_exc(Double glob_w_pre_exc) {
		this.glob_w_pre_exc = glob_w_pre_exc;
	}

	public Double getGlob_w_pre_inh() {
		return glob_w_pre_inh;
	}

	public void setGlob_w_pre_inh(Double glob_w_pre_inh) {
		this.glob_w_pre_inh = glob_w_pre_inh;
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

	public Double getGlob_external_inputs_time_offset() {
		return glob_external_inputs_time_offset;
	}

	public void setGlob_external_inputs_time_offset(Double glob_external_inputs_time_offset) {
		this.glob_external_inputs_time_offset = glob_external_inputs_time_offset;
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

	public Boolean getGlob_plasticity() {
		return glob_plasticity;
	}

	public void setGlob_plasticity(Boolean glob_plasticity) {
		this.glob_plasticity = glob_plasticity;
	}

	public Double getGlob_etap() {
		return glob_etap;
	}

	public void setGlob_etap(Double glob_etap) {
		this.glob_etap = glob_etap;
	}

	public Double getGlob_etam() {
		return glob_etam;
	}

	public void setGlob_etam(Double glob_etam) {
		this.glob_etam = glob_etam;
	}

	public Double getGlob_taup() {
		return glob_taup;
	}

	public void setGlob_taup(Double glob_taup) {
		this.glob_taup = glob_taup;
	}

	public Double getGlob_taum() {
		return glob_taum;
	}

	public void setGlob_taum(Double glob_taum) {
		this.glob_taum = glob_taum;
	}

	public Double getGlob_pw_max() {
		return glob_pw_max;
	}

	public void setGlob_pw_max(Double glob_pw_max) {
		this.glob_pw_max = glob_pw_max;
	}

	public Double getGlob_to() {
		return glob_to;
	}

	public void setGlob_to(Double glob_to) {
		this.glob_to = glob_to;
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

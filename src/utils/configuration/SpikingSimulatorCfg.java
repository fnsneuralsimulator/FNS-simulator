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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import utils.constants.Constants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "fns_config")
public class SpikingSimulatorCfg {
  
  private int stop=1000;
  private int serialize_after=1000;
  private Double avg_neuronal_signal_speed=5.1;
  private NeuManCfg global_neuron_manager;
  private ArrayList<NodeCfg> node = new ArrayList<NodeCfg>();
  private ArrayList<NodeInterconnectionCfg> connection = 
      new ArrayList<NodeInterconnectionCfg>();
  private Boolean lif=false;
  private Boolean exp_decay=false;
  private Double glob_rewiring_P=0.0;
  private Integer glob_k=20;
  private Long glob_n=100l;
  private Double glob_R=0.8;
  private Double glob_mu_w_exc=0.04;
  private Double glob_mu_w_inh=0.03;
  private Double glob_sigma_w_exc;
  private Double glob_sigma_w_inh;
  private Double glob_w_pre_exc=0.02;
  private Double glob_w_pre_inh=0.02;
  private Integer glob_Bn=1;
  private Double glob_IBI=1.0;
  private Integer glob_external_inputs_number=0;
  private Integer glob_external_inputs_type=1;
  private Double glob_external_inputs_time_offset=0.0;
  private Integer glob_external_inputs_fireduration=1000;
  private Double glob_external_inputs_timestep=1.0;
  private Integer glob_external_inputs_outdegree=1;
  //private Double glob_external_inputs_firerate=0.3; 
  private Double glob_external_inputs_amplitude=0.07; 
  private Boolean glob_plasticity=false;
  private Double glob_etap = Constants.ETAP;
  private Double glob_etam = Constants.ETAM;
  private Double glob_taup = Constants.TAUP;
  private Double glob_taum = Constants.TAUM;
  private Double glob_w_max = Constants.PWMAX;
  private Double glob_to = Constants.TO;  

  public int getStop() {
    return stop;
  }

  public void setStop(int stop) {
    this.stop = stop;
  }

  public int getSerialize_after() {
    return serialize_after;
  }

  public void setSerialize_after(int serialize_after) {
    this.serialize_after = serialize_after;
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

  public HashMap<Integer,NodeCfg> getNodesMap() {
    HashMap<Integer, NodeCfg> nodesMap = new HashMap<Integer, NodeCfg>();
    for (NodeCfg tmpNode : node) {
      nodesMap.put(tmpNode.getId(), tmpNode);
      //System.out.println("tmpNode:" + tmpNode.getId() );
    }
    return nodesMap;
  }

  public void setNodes(ArrayList<NodeCfg> nodes) {
    this.node = nodes;
  }
  
  public ArrayList<NodeInterconnectionCfg> getConnections() {
    return connection;
  }

  public void setConnections(ArrayList<NodeInterconnectionCfg> connections) {
    this.connection = connections;
  }
  
  
  public NeuManCfg getGlobal_neuron_manager() {
    return global_neuron_manager;
  }

  public void setGlobal_neuron_manager(NeuManCfg global_neuron_manager) {
    this.global_neuron_manager = global_neuron_manager;
  }

  public ArrayList<NodeCfg> getNode() {
    return node;
  }

  public void setNode(ArrayList<NodeCfg> region) {
    this.node = region;
  }

  public ArrayList<NodeInterconnectionCfg> getConnection() {
    return connection;
  }

  public void setConnection(ArrayList<NodeInterconnectionCfg> connection) {
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

  public Double getGlob_sigma_w_exc() {
    return glob_sigma_w_exc;
  }

  public Double getGlob_sigma_w_inh() {
    return glob_sigma_w_inh;
  }

  public void setGlob_sigma_w_exc(Double glob_sigma_w_exc) {
    this.glob_sigma_w_exc = glob_sigma_w_exc;
  }

  public void setGlob_sigma_w_inh(Double glob_sigma_w_inh) {
    this.glob_sigma_w_inh = glob_sigma_w_inh;
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

  public Integer getGlob_external_inputs_outdegree() {
    return glob_external_inputs_outdegree;
  }

  public void setGlob_external_inputs_outdegree(Integer glob_external_inputs_outdegree) {
    this.glob_external_inputs_outdegree = glob_external_inputs_outdegree;
  }

  public Double getGlob_external_inputs_timestep() {
    return glob_external_inputs_timestep;
  }

  public void setGlob_external_inputs_timestep(Double glob_external_inputs_timestep) {
    this.glob_external_inputs_timestep = glob_external_inputs_timestep;
  }

  //public Double getGlob_external_inputs_firerate() {
  //  return glob_external_inputs_firerate;
  //}

  //public void setGlob_external_inputs_firerate(Double glob_external_inputs_firerate) {
  //  this.glob_external_inputs_firerate = glob_external_inputs_firerate;
  //}
  
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

  public Double getGlob_w_max() {
    return glob_w_max;
  }

  public void setGlob_w_max(Double glob_w_max) {
    this.glob_w_max = glob_w_max;
  }

  public Double getGlob_to() {
    return glob_to;
  }

  public void setGlob_to(Double glob_to) {
    this.glob_to = glob_to;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder( 
        "stop:"+stop+
        ", ld exc:"+global_neuron_manager.getD_exc()+
        ", ld inh:"+global_neuron_manager.getD_inh()+
        ", d:"+global_neuron_manager.getC()+", kr:"+global_neuron_manager.getT_arp()+"\nregions:");
    for (int i=0; i<node.size();++i)
      sb.append("\n"+i+". "+node.get(i).getId());
    sb.append("\nconnections:");
    for (int i=0; i<connection.size();++i)
      sb.append("\n"+i+". "+connection.get(i).getSrcNodeId()+"->"+
    connection.get(i).getDstNodeId()+", prob:"+connection.get(i).getConnection_probability());
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

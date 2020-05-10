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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import utils.constants.Constants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "node")
public class NodeCfg {
  private Integer id;
  private Double rewiring_P;
  private Integer k;
  private Long n;
  private Double R;
  private Double mu_w_exc;
  private Double mu_w_inh;
  private Double sigma_w_exc;
  private Double sigma_w_inh;
  private Double w_pre_exc;
  private Double w_pre_inh;
  private Integer external_inputs_number;
  private Integer external_inputs_type;
  private Double external_inputs_time_offset;
  private Integer external_inputs_fireduration;
  private Double external_inputs_timestep;
  private Integer external_inputs_outdegree;
  //private Double external_inputs_firerate;
  private Double external_inputs_amplitude;
  private Boolean small_world_topology;
  private Integer Bn;
  private Double IBI;
  private NeuManCfg neuron_manager;
  private Boolean plasticity;
  private Double etap;
  private Double etam;
  private Double taup;
  private Double taum;
  private Double w_max;
  private Double to;
  

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

  public Double getW_pre_exc() {
    return w_pre_exc;
  }

  public void setW_pre_exc(Double exc_ampl) {
    this.w_pre_exc = exc_ampl;
  }

  public Double getW_pre_inh() {
    return w_pre_inh;
  }

  public void setW_pre_inh(Double inh_ampl) {
    this.w_pre_inh = inh_ampl;
  }

  public Double getMu_w_exc() {
    return mu_w_exc;
  }

  public Double getMu_w_inh() {
    return mu_w_inh;
  }

  public void setMu_w_exc(Double mu_w_exc) {
    this.mu_w_exc = mu_w_exc;
  }

  public void setMu_w_inh(Double mu_w_inh) {
    this.mu_w_inh = mu_w_inh;
  }

  public Double getSigma_w_exc() {
    return sigma_w_exc;
  }

  public Double getSigma_w_inh() {
    return sigma_w_inh;
  }

  public void setSigma_w_exc(Double sigma_w_exc) {
    this.sigma_w_exc = sigma_w_exc;
  }

  public void setSigma_w_inh(Double sigma_w_inh) {
    this.sigma_w_inh = sigma_w_inh;
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

  public Double getExternal_inputs_time_offset() {
    return external_inputs_time_offset;
  }

  public void setExternal_inputs_time_offset(Double external_inputs_time_offset) {
    this.external_inputs_time_offset = external_inputs_time_offset;
  }

  public Double getExternal_inputs_timestep() {
    return external_inputs_timestep;
  }

  public void setExternal_inputs_timestep(Double external_inputs_timestep) {
    this.external_inputs_timestep = external_inputs_timestep;
  }

  //public Double getExternal_inputs_firerate() {
  //  return external_inputs_firerate;
  //}

  //public void setExternal_inputs_firerate(Double external_inputs_firerate) {
  //  this.external_inputs_firerate = external_inputs_firerate;
  //}
  
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
  
  public Integer getExternal_inputs_outdegree() {
    return external_inputs_outdegree;
  }

  public void setExternal_inputs_outdegree(Integer external_inputs_outdegree) {
    this.external_inputs_outdegree = external_inputs_outdegree;
  }
  
  public Integer getBn() {
    return Bn;
  }

  public void setBn(Integer bn) {
    Bn = bn;
  }

  public Double getIBI() {
    return IBI;
  }

  public void setIBI(Double iBI) {
    IBI = iBI;
  }

  public Boolean getPlasticity() {
    return plasticity;
  }

  public void setPlasticity(Boolean plasticity) {
    this.plasticity = plasticity;
  }

  public Double getEtap() {
    return etap;
  }

  public void setEtap(Double etap) {
    this.etap = etap;
  }

  public Double getEtam() {
    return etam;
  }

  public void setEtam(Double etam) {
    this.etam = etam;
  }

  public Double getTaup() {
    return taup;
  }

  public void setTaup(Double taup) {
    this.taup = taup;
  }

  public Double getTaum() {
    return taum;
  }

  public void setTaum(Double taum) {
    this.taum = taum;
  }

  public Double getW_max() {
    return w_max;
  }

  public void setW_max(Double w_max) {
    this.w_max = w_max;
  }

  public Double getTo() {
    return to;
  }

  public void setTo(Double to) {
    this.to = to;
  }

  public NeuManCfg getNeuron_manager(){
    return neuron_manager;
  }
  
  public void setNeuron_manager(NeuManCfg neuron_manager){
    this.neuron_manager=neuron_manager;
  }
  
}

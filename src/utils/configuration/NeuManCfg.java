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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "neurons_manager")
public class NeuManCfg {
  private Double D_exc;
  private Double D_inh;
  private Double c;
  private Double t_arp;
  
  public NeuManCfg(){  
  }
  
  public NeuManCfg(Double D_exc, Double D_inh, Double c, Double t_arp){
    this.D_exc=D_exc;
    this.D_inh=D_inh;
    this.c=c;
    this.t_arp=t_arp;
  }
  
  public Double getD_exc() {
    return D_exc;
  }

  public Double getD_inh() {
    return D_inh;
  }
  public void setD_exc(Double d_exc) {
    this.D_exc = d_exc;
  }

  public void setD_inh(Double d_inh) {
    this.D_inh = d_inh;
  }

  public Double getC() {
    return c;
  }
  public void setC(Double c) {
    this.c = c;
  }
  public Double getT_arp() {
    return t_arp;
  }
  public void setT_arp(Double t_arp) {
    this.t_arp = t_arp;
  }
  

  
  
  
  
}

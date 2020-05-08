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


package spiking.node.plasticity;

import java.util.HashMap;
import spiking.node.Synapse;
import spiking.node.neuron.NodeNeuronsManager;
import utils.constants.Constants;

public class AfferentManager {
  
  HashMap<Synapse, Afferent> affMap = new HashMap<>();
  private Double etap = 0.01;
  private Double etam = 0.05;
  private Double taup = 15.0;
  private Double taum = 30.0;
  private Double pwMax = 100.0;
  // if the time delta is bigger than to, no effect is calculated - cutoff filter
  private Double to=3.0;
  
  
  public void addAfferent(Afferent aff){
    affMap.put(aff.getSynapse(), aff);
  }
  
  public Afferent findNoLtp(Synapse syn) {
    Afferent retval = affMap.get(syn);
    retval=retval.getLtpFlag()?retval:null;
    return retval;
  }
  
  public Afferent popNoLtp(Synapse syn) {
    Afferent retval = affMap.get(syn);
    if (retval==null)
      return retval;
    retval=retval.getLtpFlag()?retval:null;
    if (retval==null)
      return retval;
    retval=retval.getLastFireTime().equals(Constants.TIME_TO_FIRE_DEF_VAL)?null:retval;    
    affMap.remove(retval);
    return retval;
  }
  
  public Afferent popNoLtd(Synapse syn) {
    Afferent retval = affMap.get(syn);
    if (retval==null)
      return retval;
    retval=retval.getLtdFlag()?retval:null;
    retval=retval.getLastFireTime().equals(Constants.TIME_TO_FIRE_DEF_VAL)?null:retval;
    affMap.remove(retval);
    return retval;
  }

  public Double getEtap() {
    return etap;
  }

  public Double getEtam() {
    return etam;
  }

  public Double getTaup() {
    return taup;
  }

  public Double getTaum() {
    return taum;
  }

  public Double getPwMax() {
    return pwMax;
  }
  
  public Double getTo(){
    return to;
  }
  
  
  
  

}

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

package spiking.node.plasticity;

import java.util.HashMap;

import spiking.node.Synapse;
import spiking.node.neuron.NodeNeuronsManager;
import utils.constants.Constants;

public class AfferentManager {
	
	HashMap<Synapse, Afferent> affMap = new HashMap<>();
	private Double etap = 0.01;
	private Double etam = 0.05;
	private Double taup = 10.0;
	private Double taum = 20.0;
	private Double pwMax = 0.0;
	

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
	
	
	
	

}

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

package utils.constants;


public class Constants {
	
	public static final Double EXCITATORY_PRESYNAPTIC_DEF_VAL=1.0;
	public static final Double INHIBITORY_PRESYNAPTIC_DEF_VAL=-1.0;
	public static final Double EXTERNAL_SOURCES_PRESYNAPTIC_DEF_VAL=1.0;
	public static final Double TIME_TO_FIRE_DEF_VAL=-1.0;//Double.MAX_VALUE;
	public static final Double BURNING_TIME_DEF_VAL=-1.0;
	public static final Double EXTERNAL_TIME_TO_FIRE_DEF_VAL=Double.MAX_VALUE;
	public static final Double POST_SYNAPTIC_WEIGHT_DEF_VAL = 0.0;
	public static final Double LENGTH_DEF_VAL = null;
	public static final Double EPSILON=0.000001;
	public static final Boolean PLASTICITY = false;
	public static final Double ETAP = 0.01;
	public static final Double ETAM = 0.05;
	public static final Double TAUP = 15.0;
	public static final Double TAUM = 30.0;
	public static final Double PWMAX = 100.0;
	public static final Double TO = 3.0;
}

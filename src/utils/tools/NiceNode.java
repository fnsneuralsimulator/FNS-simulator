/**
* This file is part of FNS (Firnet NeuroScience), ver.1.0.1
*
* (c) 2017, Mario Salerno, Gianluca Susi, Alessandro Cristini, Emanuele Paracone.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, Mario 
* Salerno, Fernando Maestú, Ernesto Pereda (2018). "FNS: An event-driven spiking 
* neural network framework for efficient simulations of large-scale brain 
* models". 
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

package utils.tools;

import java.io.Serializable;

public class NiceNode implements Comparable<NiceNode>, Serializable{
   
	
	private static final long serialVersionUID = 4284840399532187183L;
	public Long fn;
    public Double tf;
    
    public NiceNode(Long fn, Double tf){
        this.fn=fn;
        this.tf=tf;
    }
   
    public String toString(){
    	return "fn:"+fn+",\ttf:"+tf;
    }
    
    public int compareTo(NiceNode node){
        return Double.compare(tf,node.tf);
    }
}

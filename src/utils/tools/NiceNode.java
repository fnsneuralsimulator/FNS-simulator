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
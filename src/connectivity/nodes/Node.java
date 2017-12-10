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


package connectivity.nodes;

public class Node {
	private static Integer globalId=0;
	private Integer id=globalId;
	private String label;
	private Double x,y,z;
	private ConnectivityPackageManager rm;
	
	public Node(ConnectivityPackageManager rm, String label, Double x, Double y, Double z){
		this.rm = rm;
		this.label = label;
		this.x=x;
		this.y=y;
		this.z=z;
		++globalId;
	}
	
	public Node(){
		++globalId;
	}

	public String getLabel() {
		return label;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Double getZ() {
		return z;
	}
	
	public Integer getId(){
		return id;
	}
	
	public String toString(){
		return "node:"+id+", name:"+label+", x:"+x+", y:"+y+", z:"+z;
	}

	public static void main(String[] args) {
		ConnectivityPackageManager rm = new ConnectivityPackageManager();
		Node r1 = new Node(rm, "nd1", 1.432, 12.342, 12.423);
		Node r2 = new Node(rm, "nd2", 1.4232, 12.3654, 134.423);
		System.out.println(r1.toString());
		System.out.println(r2.toString());
		
	}
		
	
}

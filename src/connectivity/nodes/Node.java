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

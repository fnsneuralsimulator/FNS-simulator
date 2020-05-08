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

import java.util.ArrayList;
import java.util.HashMap;

//import org.jgrapht.graph.DefaultDirectedWeightedGraph;
//import org.jgrapht.graph.DefaultWeightedEdge;

import connectivity.conn_package.PackageReader;
import spiking.node.NodesInterconnection;
import utils.tools.IntegerCouple;

//import grph.Grph;
//import grph.in_memory.InMemoryGrph;

public class ConnectivityPackageManager {
  //the nodes list sorted by Id
  private ArrayList<Node> nodes = new ArrayList<>();
  private PackageReader pr = new PackageReader(this);
  private ArrayList <NodesInterconnection> ris = new ArrayList<>();
  private HashMap<IntegerCouple, Integer> indexes = new HashMap<>();
  
  private void _addNode(Node r){
    if (nodes==null)
      return;
    nodes.add(r);
  }
  
  public void addNode(){
    _addNode(new Node());
  }
  
  public void addNode(Node r){
    _addNode(r);
  }
  
  public void addNode(String label, Double x, Double y, Double z ){
    _addNode(new Node(this,label,x,y,z));
  }
  
  public Node getNode(Integer id){
    return nodes.get(id);
  }
  
  public void addEdge(Integer src, Integer dst, Double weight){
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);
      if (index==null){
        indexes.put(tmp, ris.size());
        ris.add(new NodesInterconnection(src, dst, weight));
      }
      
    }
  }
  
  public void addAmplitude(Integer src, Integer dst, Double amplitude){
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);      
      if (index!=null)
        ris.get(index).setAmplitude(amplitude);
    }
  }
  
  public void addAmplitudeStdDeviation(Integer src, Integer dst, Double amplitude){
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);      
      if (index!=null)
        ris.get(index).setAmplitudeStdDeviation(amplitude);
    }
  }
  
  public void addLength(Integer src, Integer dst, double lenght) {
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);      
      if (index!=null)
        ris.get(index).setLength(lenght);
    }
  }

  
  public void addLengthShapeParameter(Integer src, Integer dst, double lenghtShapeParameter) {
    
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);      
      if (index!=null)
        ris.get(index).setLengthShapeParameter(lenghtShapeParameter);
    }
  }
  
  public void addType(Integer src, Integer dst, Integer type) {
    if ((nodes.get(src)!=null)&&(nodes.get(dst)!=null)){
      IntegerCouple tmp = new IntegerCouple(src, dst);
      Integer index = indexes.get(tmp);      
      if (index!=null)
        ris.get(index).setType(type);
    }
  }

  
  public Integer getNodesNum() {
    return nodes.size();
  }

  public Integer getEdgesNum() {
    return ris.size();
  }
  
  

  public ArrayList<NodesInterconnection> getInterNodeConnections() {
    return ris;
  }

  public void readConnectivityPackage(String path){
    pr.readConnectivityPackage(path);
  }
  
  public Double getMinNe_xn_ratio(){
    return pr.getMinNe_xn_ratio();
  }
  
  public Double getMaxNe_xn_ratio(){
    return pr.getMaxNe_xn_ratio();
  }
  
  
  
  
  public static void main(String[] args) {
    ConnectivityPackageManager rm = new ConnectivityPackageManager();
    //PackageReader pr = new PackageReader(rm);
    rm.readConnectivityPackage("/home/knizontes/Documenti/uni/tesi/neural_network/connectivity_998");
    System.out.println("Nodes:"+rm.getNodesNum());
    System.out.println("Edges:"+rm.getEdgesNum());
  }

  

}

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

package utils.tools;

import java.util.PriorityQueue;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class NiceQueue implements Serializable{
  
    
  private static final long serialVersionUID = 3248671994878955651L;
  private PriorityQueue<NiceNode> queue;
    private HashMap<Long, NiceNode> nodesHash;
    
    
    
    public NiceQueue(String tmpHashName){
      queue=new PriorityQueue<NiceNode>();
      nodesHash = new HashMap<Long, NiceNode>();
    }
    
//    public NiceQueue(DB regionDb, String hashName){
//      this.regionDb=regionDb;
//        queue=new PriorityQueue<NiceNode>();
//        nodesHash = new HashMap<Long, NiceNode>();
//    }
    
//  public NiceNode[] toArray(){
//    NiceNode[] aux=new NiceNode[queue.size()];
//    return queue.toArray(aux);
//  }


    public void insert(Double tf, Long fn){
        NiceNode node=new NiceNode(fn,tf);
        queue.add(node);
        nodesHash.put(fn, node);
    }
     

  public NiceNode min(){
    return queue.peek();
  }

  public void _update(double x){
    Iterator<NiceNode> it=queue.iterator();
//    ArrayList<NiceNode> toremList = new ArrayList<NiceNode>();
//    NiceNode tmp;
//    
//    while(it.hasNext()){
//      if ((tmp=it.next()).tf<x)
//        toremList.add(tmp);
//    }
//    for (int i=0; i<toremList.size();++i)
//      queue.remove(toremList.get(i));
    while(it.hasNext())
      it.next().tf-=x;
  }

  /** 
   * @return the minimum time to fire for active neurons, 
   * without polling the value from the queue 
   * If the queue is empty, null is returned                                                                           
   */
  public Double getMinTime(){
    NiceNode min=min();
    if (min==null)
      return null;
    return min.tf;
  }
  
    public NiceNode extractMin(){
    NiceNode min=queue.poll();
    if(min!=null){
      nodesHash.remove(min.fn);
//      this.update(min.tf);
    }
    return min;
  }
    
    public void delete(Long fn){
//      System.out.println("nice queue deleting fn:"+fn+"...");
        NiceNode q = nodesHash.get(fn);
        if(q!=null){
            queue.remove(q);
//            System.out.println("deleted.");
        }
//        else
//          System.out.println("not found.");
        
    }
    
    public int size(){
      return queue.size();
    }
    
    public void printQueue(){
      Iterator<NiceNode> it=queue.iterator();
        while(it.hasNext()){
            System.out.println(it.next().toString());
        }
    }
    
    public static void main(String[] args) {
      
    
    NiceQueue nq = new NiceQueue("tmp"); 
    for (long i=0; i<10;++i)
      nq.insert(new Double((23+i+(i*i%7))%27), i);
    
    
    nq.printQueue();
    NiceNode nn = nq.min();
    System.out.println("\n\n\nmin node:\t"+nn.toString());
//    nq.update(7);
    nq.printQueue();
    
  }

    public NiceNode[] toArray(){
    NiceNode[] aux=new NiceNode[queue.size()];
    return queue.toArray(aux);
  }
    
}

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class IntegerCouple implements Comparable<IntegerCouple>{
  
  private Integer a;
  private Integer b;
  
  public IntegerCouple(Integer src, Integer dst){
    this.a=src;
    this.b=dst;
  }
  
  public Integer getA(){
    return a;
  }
  
  public Integer getB(){
    return b;
  }
  
  public Integer getSrc(){
    return a;
  }
  
  public Integer getDst(){
    return b;
  }

  public Integer getBurning(){
    return a;
  }
  
  public Integer getFiring(){
    return b;
  }
  
  @Override
  public String toString() {
    return "IntegerCouple [a=" + a + ", b=" + b + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false; 
    if (obj == this)
      return true; 
    if (obj.getClass() != getClass()) 
      return false;
    
    IntegerCouple rhs = (IntegerCouple) obj;
    return new EqualsBuilder()
//        .appendSuper(super.equals(obj))
        .append(a, rhs.a)
        .append(b, rhs.b).isEquals();
  }

  @Override
  public int hashCode() {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
       // ideally different for each class
       return new HashCodeBuilder(17, 37).append(7*a).append(b*b+3*b).toHashCode();    
  }
  
  @Override
  public int compareTo(IntegerCouple o) {
    if (this==o)
      return 0;
    int retval = a.compareTo(o.getA());
    if (retval!=0)
      return retval;
    retval = b.compareTo(o.getB());      
    return retval;
  }
  
  public void torem(Integer a){
    System.out.println("torem");
    a=new Integer(3);
  }
  
  public static void main(String[] args) {
    HashMap<Integer, Integer> h = new HashMap<>();
    HashMap<IntegerCouple, Integer> kh= new HashMap<>();
    TreeMap<Integer, Integer> t = new TreeMap<>();
    TreeMap<IntegerCouple, Integer> kt = new TreeMap<>();
    
    h.put(new Integer(3), new Integer(3));
    h.put(new Integer(3), new Integer(3));
    h.put(new Integer(3), new Integer(3));
    
    kh.put(new IntegerCouple(3,9), new Integer(3));
    kh.put(new IntegerCouple(3,9), new Integer(3));
    kh.put(new IntegerCouple(3,9), new Integer(3));
    
    t.put(new Integer(3), new Integer(3));
    t.put(new Integer(3), new Integer(3));
    t.put(new Integer(3), new Integer(3));
        
    kt.put(new IntegerCouple(3,9), new Integer(3));
    kt.put(new IntegerCouple(3,9), new Integer(3));
    kt.put(new IntegerCouple(3,9), new Integer(3));
    
    Iterator<Entry<Integer, Integer>> it =h.entrySet().iterator();    
    System.out.println("Integer hash:");
    while (it.hasNext()){
      Map.Entry pair = (Map.Entry)it.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          it.remove();
    }
    System.out.println();

    Iterator<Entry<IntegerCouple, Integer>> itkh =kh.entrySet().iterator();    
    System.out.println("IntegerCouple hash:");
    while (itkh.hasNext()){
      Map.Entry pair = (Map.Entry)itkh.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          itkh.remove();
    }
    System.out.println();

    Iterator<Entry<Integer, Integer>> itt =t.entrySet().iterator();    
    System.out.println("Integer tree:");
    while (itt.hasNext()){
      Map.Entry pair = (Map.Entry)itt.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          itt.remove();
    }
    System.out.println();

    
    Iterator<Entry<IntegerCouple, Integer>> itkt =kt.entrySet().iterator();    
    System.out.println("IntegerCouple tree:");
    while (itkt.hasNext()){
      Map.Entry pair = (Map.Entry)itkt.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          itkt.remove();
    }
    System.out.println();
    
    Double a = 2.0;
//    if (a.equals(.excitatoryPresynapticDefVal))
//      System.out.println("k!");
//    else
//      System.out.println("!k");
    Integer b=9;
    new IntegerCouple(3,4).torem(b);
    System.out.println(b);
    
  }

    
  
  

}

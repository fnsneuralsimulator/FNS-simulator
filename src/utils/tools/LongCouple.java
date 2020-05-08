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

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.lang.Comparable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;


public class LongCouple implements Comparable<LongCouple>, Serializable{
  
  
  private static final long serialVersionUID = 2986440218028674578L;
  private Long a;
  private Long b;
  
  public LongCouple(Long src, Long dst){
    this.a=src;
    this.b=dst;
  }
  
  public Long getA(){
    return a;
  }
  
  public Long getB(){
    return b;
  }
  
  public Long getSrc(){
    return a;
  }
  
  public Long getDst(){
    return b;
  }

  public Long getFiring(){
    return a;
  }
  
  public Long getBurning(){
    return b;
  }
  
  @Override
  public String toString() {
    return "[a=" + a + ", b=" + b + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false; 
    if (obj == this)
      return true; 
    if (obj.getClass() != getClass()) 
      return false;
    
    LongCouple rhs = (LongCouple) obj;
    return new EqualsBuilder()
//        .appendSuper(super.equals(obj))
        .append(a, rhs.a)
        .append(b, rhs.b).isEquals();
  }

  @Override
  public int hashCode() {
    // you pick a hard-coded, randomly chosen, non-zero, odd number
       // ideally different for each class
    if (a==null){
      System.out.println("a null");
      System.out.println("this:"+toString());
    }
    if (b==null){
      System.out.println("b null");
      System.out.println("this:"+toString());
    }
    return new HashCodeBuilder(17, 37).append(7l*a+9l).append(b*b+3l*b+17l).toHashCode();    
  }
  
  @Override
  public int compareTo(LongCouple o) {
    if (this==o)
      return 0;
    int retval = a.compareTo(o.getA());
    if (retval!=0)
      return retval;
    retval = b.compareTo(o.getB());      
    return retval;
  }
  
  

  
  public void torem(Long a){
    System.out.println("torem");
    a=new Long(3);
  }
  
  public static void main(String[] args) {
    HashMap<Long, Long> h = new HashMap<>();
    HashMap<LongCouple, Long> kh= new HashMap<>();
    TreeMap<Long, Long> t = new TreeMap<>();
    TreeMap<LongCouple, Long> kt = new TreeMap<>();
    
    h.put(new Long(3), new Long(3));
    h.put(new Long(3), new Long(3));
    h.put(new Long(3), new Long(3));
    
    kh.put(new LongCouple(3l,9l), new Long(3));
    kh.put(new LongCouple(3l,9l), new Long(3));
    kh.put(new LongCouple(3l,9l), new Long(3));
    
    t.put(new Long(3), new Long(3));
    t.put(new Long(3), new Long(3));
    t.put(new Long(3), new Long(3));
        
    kt.put(new LongCouple(3l,9l), new Long(3));
    kt.put(new LongCouple(3l,9l), new Long(3));
    kt.put(new LongCouple(3l,9l), new Long(3));
    
    Iterator<Entry<Long, Long>> it =h.entrySet().iterator();    
    System.out.println("Long hash:");
    while (it.hasNext()){
      Map.Entry pair = (Map.Entry)it.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          it.remove();
    }
    System.out.println();

    Iterator<Entry<LongCouple, Long>> itkh =kh.entrySet().iterator();    
    System.out.println("LongCouple hash:");
    while (itkh.hasNext()){
      Map.Entry pair = (Map.Entry)itkh.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          itkh.remove();
    }
    System.out.println();

    Iterator<Entry<Long, Long>> itt =t.entrySet().iterator();    
    System.out.println("Long tree:");
    while (itt.hasNext()){
      Map.Entry pair = (Map.Entry)itt.next();
          System.out.println(pair.getKey() + " --> " + pair.getValue());
          itt.remove();
    }
    System.out.println();

    
    Iterator<Entry<LongCouple, Long>> itkt =kt.entrySet().iterator();    
    System.out.println("LongCouple tree:");
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
    Long b=9l;
    new LongCouple(3l,4l).torem(b);
    System.out.println(b);
    
  }

  

    
  
  

}

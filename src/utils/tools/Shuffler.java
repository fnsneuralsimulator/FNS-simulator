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

import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import org.mapdb.HTreeMap;

public class Shuffler {

  public static int [] shuffleArray(int n){
    int [] retval = new int[n];
    for (int i=0;i<n;++i)
      retval[i]=i;
    Random rnd = ThreadLocalRandom.current();
    for (int i = n - 1; i > 0; i--)
    {
      int index = rnd.nextInt(i + 1);
      // Simple swap
      int a = retval[index];
      retval[index] = retval[i];
      retval[i] = a;
    }
    return retval;
  }
  
  public static void shuffleArray(HTreeMap<Long, Long> shuffled, long n){
    
    for (long i=0;i<n;++i)
      shuffled.put(i, i);
    Random rnd = ThreadLocalRandom.current();
    for (long i = n - 1; i > 0; i--)
    {
      Long tmp = rnd.nextLong();
      Long index = (tmp<0)?((-tmp)%(i)):(tmp%(i));
      // Simple swap
      Long a = shuffled.get(index);
      shuffled.put(index, shuffled.get(i));
      shuffled.put(i,a);
    }
  }
  
  
  public static void main(String[] args) {
    int [] a = Shuffler.shuffleArray(30);
    for (int i=0; i<a.length;++i)
      System.out.println(i+". "+a[i]);
  }
  
}

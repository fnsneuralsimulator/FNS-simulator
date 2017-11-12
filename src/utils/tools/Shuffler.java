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

//import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

import org.mapdb.HTreeMap;

public class Shuffler {

	public static int [] shuffleArray(int n){
		int [] retval = new int[n];
		for (int i=0;i<n;++i)
			retval[i]=i;

		// If running on Java 6 or older, use `new Random()` on RHS here
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

		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = ThreadLocalRandom.current();
		for (long i = n - 1; i > 0; i--)
		{
			Long tmp = rnd.nextLong();
			Long index = (tmp<0)?((-tmp)%(i)):(tmp%(i));
			// Simple swap
			//kkk controlla di non andare a sovrascrivere lo stesso valore
			Long a = shuffled.get(index);
			//System.out.println("tmp:"+tmp+", index:"+index+", a:"+a);
			
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
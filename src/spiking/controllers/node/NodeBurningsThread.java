
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

package spiking.controllers.node;


import utils.statistics.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class NodeBurningsThread extends Thread {


  private final String TAG = "[Node Burnings Thread] ";
  private Boolean keepRunning=true;
  private BlockingQueue <CollectedBurn> burningSpikesQueue;
  private StatisticsCollector sc;
  
  public NodeBurningsThread(StatisticsCollector sc){
    this.sc=sc;
    burningSpikesQueue=
        new ArrayBlockingQueue<CollectedBurn>(sc.getSerializeAfter());

  }

  public void run(){
    CollectedBurn cb;
    try{
      while(true){
        cb=burningSpikesQueue.take();
        sc.collectBurnSpike(cb);
      }
    }  
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

 
  public synchronized void collectBurnSpike(
      Long firingNeuronId,
      Integer firingNodeId,
      Long burningNeuronId,
      Integer burningNodeId,
      Double burnTime, 
      Boolean fromExternalSource, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire,
      Double fireTime) {
    try{
      burningSpikesQueue.put(
          new CollectedBurn(
              firingNeuronId,
              firingNodeId,
              burningNeuronId,
              burningNodeId,
              burnTime, 
              fromExternalSource, 
              fromState, 
              stepInState, 
              postsynapticWeight, 
              presynapticWeight, 
              timeToFire, 
              fireTime)
              );
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }




}

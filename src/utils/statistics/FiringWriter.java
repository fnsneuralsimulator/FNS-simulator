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

package utils.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.concurrent.BlockingQueue;


public class FiringWriter extends Thread {

  
  private final String TAG = "[Firing Writer] ";
  private Boolean keepRunning=true;
  private String filename;
  private Boolean gephi = false;
  private Boolean matlab = false;
  private Boolean reducedOutput = false;
  private Boolean superReducedOutput = false;
  private PrintWriter pw;
  private PrintWriter pwGephi;
  private PrintWriter pwMatlab;
  private File towritefile;
  private File towritefileGephi;
  private File towritefileMatlab;
  private BufferedWriter bw;
  private BufferedWriter bwMatlab;
  private FileWriter fw;
  private FileWriter fwGephi;
  private FileWriter fwMatlab;
  private DecimalFormat df = new DecimalFormat("#.################");
  private int count=1;
  private BlockingQueue <CollectedFire> firingSpikesQueue;
  private String defFileName=null;

  public FiringWriter(
      String filename, 
      BlockingQueue <CollectedFire> firingSpikesQueue){
    this.filename=filename;
    this.firingSpikesQueue=firingSpikesQueue;
  }

  public void init(){
    try{
      for(;;++count) {
        if (superReducedOutput)
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_firing_R.csv");
        else if (reducedOutput)
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_firing_r.csv");
        else
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_firing.csv");
          if(!towritefile.exists()){
            defFileName=filename+String.format("%03d", count);
              break;
          }
      }
      if (reducedOutput)
        towritefile= new File(defFileName+"_firing_r.csv");
      else if (superReducedOutput)
        towritefile= new File(defFileName+"_firing_R.csv");
      else
        towritefile= new File(defFileName+"_firing.csv");
      if (towritefile.exists())
        fw = new FileWriter(towritefile,true);
      else{
        towritefile.createNewFile();
        fw = new FileWriter(towritefile);
      }
      bw = new BufferedWriter(fw);
      pw = new PrintWriter(bw);
      //----------
      // matlab
      //----------
      if (matlab){
        towritefileMatlab= new File(defFileName+"_firing_matlab.csv");
        if (towritefileMatlab.exists())
          fwMatlab = new FileWriter(towritefileMatlab,true);
        else{
          towritefileMatlab.createNewFile();
          fwMatlab = new FileWriter(towritefileMatlab);
        }
        bwMatlab = new BufferedWriter(fwMatlab);
        pwMatlab=new PrintWriter(bwMatlab);
      }


      bw = new BufferedWriter(fw);
      if (!reducedOutput)
        pw.println(
            "Firing Time,"
            +" Firing Node,"
            +" Firing Neuron, "
            +" Neuron Type,"
            +" External Source");
    }catch(IOException e){
      e.printStackTrace();
      }
  }

  public void run() {
    init();
    CollectedFire cf;
    try{
      while (true){
        cf=firingSpikesQueue.take();
        // ---------
        // std csv
        // ---------
        String excitStr;
        String isExternalStr;
        if (cf.isExcitatory())
          excitStr="excitatory";
        else
          excitStr="inhibitory";
        if (cf.isExternal())
          isExternalStr=(reducedOutput||superReducedOutput)?"1":"true";
        else
          isExternalStr=(reducedOutput||superReducedOutput)?"0":"false";
        if (reducedOutput||superReducedOutput)
          pw.println(
              df.format(cf.getFiringTime())+", "
              +cf.getFiringNodeId()+", "
              + cf.getFiringNeuronId()+", "
              + isExternalStr
              );
        else{
          pw.println(
              df.format(cf.getFiringTime())+", "
              +cf.getFiringNodeId()+", "
              + cf.getFiringNeuronId()+", "
              + excitStr+", "
              + cf.isExternal()
              );
        }
        // ---------
        // matlab csv
        // ---------
        if (matlab)
          pwMatlab.println(
              cf.getFiringTime().toString()+", "
              +cf.getFiringNodeId()+", "
              + cf.getFiringNeuronId()+", "
              + (cf.isExcitatory()?'1':'0')+", "
              + (cf.isExternal()?'1':'0')
              );
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
  }

  public void close(){
    try{
      while (!firingSpikesQueue.isEmpty())
        firingSpikesQueue.wait();
      pw.flush();
      pw.close();
      pwMatlab.flush();
      pwMatlab.close();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void setReducedOutput(){
    reducedOutput=true;
  }

  public void setSuperReducedOutput(){
    reducedOutput=true;
    superReducedOutput=true;
    df = new DecimalFormat("#.################");
  }
}

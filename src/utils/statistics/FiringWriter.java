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
import java.util.concurrent.ArrayBlockingQueue;



public class FiringWriter extends Thread {

  
  private final String TAG = "[Firing Writer] ";
  private String defFileName;
  private PrintWriter pw;
  private PrintWriter pwGephi;
  private PrintWriter pwMatlab;
  //private File towritefile;
  //private File towritefileGephi;
  //private File towritefileMatlab;
  //private BufferedWriter bw;
  //private BufferedWriter bwMatlab;
  //private FileWriter fw;
  //private FileWriter fwGephi;
  //private FileWriter fwMatlab;
  private DecimalFormat df = new DecimalFormat("#.################");
  private int count=1;
  private BlockingQueue <CollectedFire> firingSpikesQueue;
  private StatisticsCollector sc;
  private int sa; 

  public FiringWriter(
      StatisticsCollector sc,
      String defFileName, 
      int sa){
    this.sc=sc;
    this.defFileName=defFileName;
    this.sa=sa;
  }
  protected void setWriters(
      PrintWriter pw,
      PrintWriter pwGephi,
      PrintWriter pwMatlab
      //File towritefile,
      //File towritefileGephi,
      //File towritefileMatlab,
      //BufferedWriter bw,
      //BufferedWriter bwMatlab,
      //FileWriter fw,
      //FileWriter fwGephi,
      //FileWriter fwMatlab
  ){
    this.pw=pw;
    this.pwGephi=pwGephi;
    this.pwMatlab=pwMatlab;
    //this.towritefile=towritefile;
    //this.towritefileGephi=towritefileGephi;
    //this.towritefileMatlab=towritefileMatlab;
    //this.bw=bw;
    //this.bwMatlab=bwMatlab;
    //this.fw=fw;
    //this.fwGephi=fwGephi;
    //this.fwMatlab=fwMatlab;
  }

  public void init(){
    //Boolean newfile=false;
    firingSpikesQueue= new ArrayBlockingQueue<CollectedFire>(sa);
    //try{
    //  if (sc.reducedOutput)
    //    towritefile= new File(defFileName+"_firing_r.csv");
    //  else if (sc.superReducedOutput)
    //    towritefile= new File(defFileName+"_firing_R.csv");
    //  else
    //    towritefile= new File(defFileName+"_firing.csv");
    //  if (towritefile.exists())
    //    fw = new FileWriter(towritefile,true);
    //  else{
    //    newfile=true;
    //    towritefile.createNewFile();
    //    fw = new FileWriter(towritefile);
    //  }
    //  bw = new BufferedWriter(fw);
    //  pw = new PrintWriter(bw);
    //  //----------
    //  // matlab
    //  //----------
    //  if (sc.matlab){
    //    towritefileMatlab= new File(defFileName+"_firing_sc.matlab.csv");
    //    if (towritefileMatlab.exists())
    //      fwMatlab = new FileWriter(towritefileMatlab,true);
    //    else{
    //      towritefileMatlab.createNewFile();
    //      fwMatlab = new FileWriter(towritefileMatlab);
    //    }
    //    bwMatlab = new BufferedWriter(fwMatlab);
    //    pwMatlab=new PrintWriter(bwMatlab);
    //  }
    //  bw = new BufferedWriter(fw);
    //  if (newfile && !sc.reducedOutput)
    //    pw.println(
    //        "Firing Time, "
    //        +"Firing Node, "
    //        +"Firing Neuron, "
    //        +"Neuron Type, "
    //        +"External Source");
    //}catch(IOException e){
    //  e.printStackTrace();
    //  }
  }

  public void run() {
    init();
    CollectedFire cf;
    try{
      while (sc.keepRunning){
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
          isExternalStr=(sc.reducedOutput||sc.superReducedOutput)?"1":"true";
        else
          isExternalStr=(sc.reducedOutput||sc.superReducedOutput)?"0":"false";
        if (sc.reducedOutput||sc.superReducedOutput)
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
        if (sc.matlab)
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

  protected void put(CollectedFire cf){
    try{
      firingSpikesQueue.put(cf);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void close(){
      pw.flush();
      pw.close();
      if (pwMatlab!=null){
        pwMatlab.flush();
        pwMatlab.close();
      }
  }

  protected void flush(){
      pw.flush();
      if (pwMatlab!=null){
        pwMatlab.flush();
      }
  }

 
}

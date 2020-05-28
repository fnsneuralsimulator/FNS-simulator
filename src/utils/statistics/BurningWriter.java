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


public class BurningWriter extends Thread {

  
  private final String TAG = "[Burning Writer] ";
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
  private BlockingQueue <CollectedBurn> burningSpikesQueue;
  private String defFileName=null;

  public BurningWriter(
      String filename, 
      BlockingQueue <CollectedBurn> burningSpikesQueue){
    this.filename=filename;
    this.burningSpikesQueue=burningSpikesQueue;
  }

  public void init(){
    try{
      for(;;++count) {
        if (superReducedOutput)
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_burning_R.csv");
        else if (reducedOutput)
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_burning_r.csv");
        else
          towritefile= new File(
              filename
              +String.format("%03d", count)
              +"_burning.csv");
          if(!towritefile.exists()){
            defFileName=filename+String.format("%03d", count);
              break;
          }
      }
      if (reducedOutput)
        towritefile= new File(defFileName+"_burning_r.csv");
      else if (superReducedOutput)
        towritefile= new File(defFileName+"_burning_R.csv");
      else
        towritefile= new File(defFileName+"_burning.csv");
      if (towritefile.exists())
        fw = new FileWriter(towritefile,true);
      else{
        towritefile.createNewFile();
        fw = new FileWriter(towritefile);
      }
      bw = new BufferedWriter(fw);
      pw= new PrintWriter(bw);
      //----------
      // matlab
      //----------
      if (matlab){
        towritefileMatlab= new File(defFileName+"_burning_matlab.csv");
        if (towritefileMatlab.exists())
          fwMatlab = new FileWriter(towritefileMatlab,true);
        else{
          towritefileMatlab.createNewFile();
          fwMatlab = new FileWriter(towritefileMatlab);
        }
        BufferedWriter bwMatlab = new BufferedWriter(fwMatlab);
        pwMatlab=new PrintWriter(bwMatlab);
      }
      //----------
      // matlab
      //----------
      if (gephi){
        towritefileGephi = new File(defFileName+"_burning_gephi.csv");
        if (towritefileGephi.exists())
          fwGephi = new FileWriter(towritefileGephi,true);
        else{
          towritefileGephi.createNewFile();
          fwGephi = new FileWriter(towritefileGephi);
        }
        BufferedWriter bwGephi = new BufferedWriter(fwGephi);
        pwGephi=new PrintWriter(bwGephi);
        pwGephi.println( "Firing, Burning");
    }



    bw = new BufferedWriter(fw);
    if (!reducedOutput)
      pw.println(
          "Burning Time, "
          + "Firing Node, "
          + "Firing Neuron, "
          + "Burning Node, "
          + "Burning Neuron, "
          + "External Source, "
          + "From Internal State, "
          + "To Internal State, "
          + "Step in State, "
          +" Post Synaptic Weight, "
          + "Pre Synaptic Weight, "
          + "Instant to Fire, "
          + "(Afferent) Firing Time");
    } catch (IOException e){
      e.printStackTrace();
    }

  }

  public void run() {
    init();
    CollectedBurn cb;
    try{
      while (true){
        cb=(CollectedBurn)burningSpikesQueue.take();
        // ---------
        // std csv
        // ---------
        Double fromState = cb.getFromState();
        Double stepInState=cb.getStepInState();
        String stepInStateToPrint;
        String fromStateToPrint;
        String toStateToPrint;
        String stepInStateToPrintMatlab;
        String fromStateToPrintMatlab;
        String toStateToPrintMatlab;
        if (fromState==null){
          fromStateToPrint=(reducedOutput||superReducedOutput)?"0":"refr";
          toStateToPrint=(reducedOutput||superReducedOutput)?"0":"refr";
        }
        else{
          fromStateToPrint=""+df.format(fromState);
          toStateToPrint=""+df.format(fromState+stepInState);
        }
        if (stepInState==null)
          stepInStateToPrint=(reducedOutput||superReducedOutput)?"0":"refr";
        else
          stepInStateToPrint=""+df.format(stepInState);
        if (reducedOutput||superReducedOutput)
          pw.println(
              df.format(cb.getBurnTime())+", "
              + cb.getBurningNodeId()+", "
              + cb.getBurningNeuronId()+", "
              + toStateToPrint
              );
        else
          pw.println(
              df.format(cb.getBurnTime())+", "
              + cb.getFiringNodeId()+", "
              + cb.getFiringNeuronId()+", "
              + cb.getBurningNodeId()+", "
              + cb.getBurningNeuronId()+", "
              + cb.fromExternalInput()+", "
              + fromStateToPrint +", "
              + toStateToPrint +", "
              + stepInStateToPrint+", "
              + df.format(cb.getPostSynapticWeight())+", "
              + df.format(cb.getPreSynapticWeight())+","
              + df.format(cb.getTimeToFire())+","
              + df.format((cb.getFireTime()!=null)?
                  cb.getFireTime():0)
              );
        // ------------
        // matlab csv
        // ------------
        if (matlab){
          String refrStringMatlab="0";
          if (fromState==null){
            fromStateToPrintMatlab=refrStringMatlab;
            toStateToPrintMatlab=refrStringMatlab;
          }
          else{
            fromStateToPrintMatlab=fromState.toString();
            toStateToPrintMatlab=""+(fromState+stepInState);
          }
          if (stepInState==null)
            stepInStateToPrintMatlab="0";
          else
            stepInStateToPrintMatlab=stepInState.toString();
          pwMatlab.println(
              cb.getBurnTime().toString()+", "
              + cb.getFiringNodeId()+", "
              + cb.getFiringNeuronId()+", "
              + cb.getBurningNodeId()+", "
              + cb.getBurningNeuronId()+", "
              + cb.fromExternalInputInteger()+", "
              + fromStateToPrintMatlab +", "
              + toStateToPrintMatlab +", "
              + stepInStateToPrintMatlab+", "
              + cb.getPostSynapticWeight()+", "
              + cb.getPreSynapticWeight()+","
              + cb.getTimeToFire()+","
              + cb.getFireTime()
              );
        }
        // ------------
        // gephi csv
        // ------------
        if(gephi&&(!cb.fromExternalInput())){
          pwGephi.println(
              + cb.getFiringNodeId()+"-"
              + cb.getFiringNeuronId()+", "
              + cb.getBurningNodeId()+"-"
              + cb.getBurningNeuronId());
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
  }
  
  public void close(){
    try{
      while (!burningSpikesQueue.isEmpty())
        burningSpikesQueue.wait();
      pw.flush();
      pw.close();
      if (pwMatlab!=null){
        pwMatlab.flush();
        pwMatlab.close();
      }
      if (pwGephi!=null){
        pwGephi.flush();
        pwGephi.close();
      }
    } catch (InterruptedException e){
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

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


package utils.configuration;


import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class SpikingConfigManager {

  
  public static SpikingSimulatorCfg readConfigFile(String path){
    SpikingSimulatorCfg ssc=null;
    try {
      File file = new File(path);
      JAXBContext jaxbContext = JAXBContext.newInstance(SpikingSimulatorCfg.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      ssc= (SpikingSimulatorCfg) jaxbUnmarshaller.unmarshal(file);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return ssc;    
  }
  
  //public static void main(String[] args) {
  //  
  //  String in;
  //  ArrayList<NodeCfg> regList = new ArrayList<NodeCfg>();
  //  ArrayList<NodeInterconnectionCfg> connList = new ArrayList<NodeInterconnectionCfg>();
  //  SpikingSimulatorCfg ssc = new SpikingSimulatorCfg();
  //  Scanner reader = new Scanner(System.in);  // Reading from System.in
  //  System.out.print("Configuration manager for firnet\n\n1. simulation parameters");
  //  System.out.print("\n\t o number of simulation fires [n]:");
  //  ssc.setStop(reader.nextInt());
  //  reader.nextLine();
  //  System.out.print("\n\to plasticity on? [y,n]:");
  //  in = reader.nextLine();
  //  ssc.setPlasticity(( (in.charAt(0)=='y')||(in.charAt(0)=='Y')));
  //  
  //  
  //  
  //  System.out.print("\n2. neurons parameters:\n\to ld: [0.n]");
  //  Double ld = reader.nextDouble();
  //  reader.nextLine();
  //  System.out.print("\to d: [0.n]");
  //  Double d = reader.nextDouble();
  //  reader.nextLine();
  //  System.out.print("\to kr: [0.n]");
  //  Double kr = reader.nextDouble();
  //  reader.nextLine();
  //  System.out.print("\to default number of neuron per node: [n]");
  //  ssc.setGlob_local_n(reader.nextLong());
  //  reader.nextLine();
  //  System.out.print("\to default neural connectivity degree: [n]");
  //  ssc.setGlob_k(reader.nextInt());
  //  reader.nextLine();
  //  System.out.print("\to default rewiring probability: [0.n]");
  //  ssc.setGlob_rewiring_P(reader.nextDouble());
  //  reader.nextLine();
  //  System.out.print("\to default ratio of excitatory respect the total number of neuron N for the node: [0.n]");
  //  ssc.setR(reader.nextDouble());
  //  reader.nextLine();
  //  
  //  
  //  System.out.println("\n3. adding nodes:");
  //  for (int i=0;;++i){
  //    System.out.print("\tadd a new node? [y,n]:");
  //    in = reader.nextLine();
  //    
  //    if ( !(in.charAt(0)=='y')&&!(in.charAt(0)=='Y'))
  //      break;
  //    NodeCfg rcfg = new NodeCfg();//(id, prew, k, n, excitRatio, external, smallWorld);
  //    rcfg.setId(i);
  //    System.out.print("\to number of neurons N for the node "+i+" [n]:");
  //    rcfg.setN(reader.nextLong());
  //    reader.nextLine();
  //    
  //    System.out.print("\to number of external input neurons (if any) for the node"+i+" [n]:");
  //    rcfg.setExternal_inputs_number(reader.nextInt());
  //    reader.nextLine();
  //    
  //    System.out.print("\to ratio of excitatory respect the total number of neuron N for the node"+i+"( 0 <= [0,n] < 1):");
  //    rcfg.setExcitatory_inhibitory_ratio(reader.nextDouble());
  //    reader.nextLine();
  //    
  //    System.out.print("\to rewiring probability for the node"+i+" [0,n]:");
  //    rcfg.set_rewiring_P(reader.nextDouble());
  //    reader.nextLine();
  //    
  //    System.out.print("\to k (neurons connection degree) for the node"+i+" [n]:");
  //    rcfg.setK(reader.nextInt());
  //    reader.nextLine();      
  //    
  //    System.out.print("\to the topology of the node is a small world? (if not, the topology is random) [y,n]:");
  //    in = reader.nextLine();
  //    
  //    if ( (in.charAt(0)=='y')||(in.charAt(0)=='Y'))
  //      rcfg.setSmall_world_topology(true);
  //    regList.add(rcfg);
  //    
  //    System.out.println("\n\n");
  //  }
  //  System.out.println("\n4. adding connections:");
  //  for (int i=0;;++i){
  //    System.out.print("\tadd a new inter node connection? [y,n]:");
  //    in = reader.nextLine();
  //    if ( !(in.charAt(0)=='y')&&!(in.charAt(0)=='Y'))
  //      break;
  //    NodeInterconnectionCfg conn = new NodeInterconnectionCfg();
  //    System.out.print("\to from node:");
  //    conn.setSrcNodeId(reader.nextInt());
  //    reader.nextLine();
  //    System.out.print("\to to node:");
  //    conn.setDstNodeId(reader.nextInt());
  //    reader.nextLine();
  //    System.out.print("\tconnection "+i+" probability:");
  //    conn.setConnection_probability(reader.nextDouble());
  //    reader.nextLine();
  //    connList.add(conn);
  //  }
  //  ssc.setGlobal_neuron_manager(new NeuManCfg(ld, d, kr));
  //  ssc.setNodes(regList);
  //  ssc.setConnections(connList);
  //  System.out.print("save to file:");
  //  in = reader.nextLine();
  //  reader.close();
  //  try{
  //    File file = new File(in+".xml");
  //    JAXBContext jaxbContext = JAXBContext.newInstance(SpikingSimulatorCfg.class);
  //    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
  //    // output pretty printed
  //    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
  //    jaxbMarshaller.marshal(ssc, file);
  //    jaxbMarshaller.marshal(ssc, System.out);
  //  } catch (JAXBException e) {
  //    e.printStackTrace();
  //  }  
  //}
  
  
}

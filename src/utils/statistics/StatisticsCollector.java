/**
* This file is part of FNS (Firnet NeuroScience), ver.1.0.1
*
* (c) 2017, Mario Salerno, Gianluca Susi, Alessandro Cristini, Emanuele Paracone.
*
* CITATION:
* When using FNS for scientific publications, cite us as follows:
*
* Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, Mario 
* Salerno, Fernando Maestú, Ernesto Pereda (2018). "FNS: An event-driven spiking 
* neural network framework for efficient simulations of large-scale brain 
* models". 
* Laboratory of Cognitive and Computational Neuroscience, UPM-UCM Centre for 
* Biomedical Technology, Technical University of Madrid; University of Rome "Tor 
* Vergata".   
* Paper under review.
*
* FNS is free software: you can redistribute it and/or modify it under the terms 
* of the GNU General Public License version 3 as published by  the Free Software 
* Foundation.
*
* FNS is distributed in the hope that it will be useful, but WITHOUT ANY 
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
* A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along with 
* FNS. If not, see <http://www.gnu.org/licenses/>.
* -----------------------------------------------------------
* Website:   http://www.fnsneuralsimulator.org
*/

package utils.statistics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

//import javax.swing.JFrame;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartFrame;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.mapdb.DB;
import org.mapdb.DBMaker;
//import org.math.plot.Plot2DPanel;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import connectivity.conn_package.PackageReader;
import spiking.node.FiringNeuron;
import spiking.node.SpikingSynapse;
import spiking.node.Synapse;
import spiking.node.external_inputs.ExternalInput;
import utils.plotter.FastScatterPlotter;
import utils.plotter.ScatterPlotter;
import utils.tools.CompressedFire;
import utils.tools.LongCouple;

public class StatisticsCollector {
	
	private final static String TAG = "[Statistic Collector] ";
	private static Long active=0l;
	private static Long passive=0l;
	private static Long passive2active=0l;
	private static Long active2passive=0l;
	public static Long missedFires=0l;
	private static HashMap<Long, SpikingSynapse>burningSpikesHashMap = new HashMap<Long, SpikingSynapse>();
	private static HashMap<Long, FiringNeuron>firingSpikesHashMap = new HashMap<Long, FiringNeuron>();
	private static ArrayList<Double> firingNeurons= new ArrayList<Double>();
	private static ArrayList<Double> firingTimes= new ArrayList<Double>();
	private static HashMap<CompressedFire, Integer> compressor = new HashMap<CompressedFire, Integer>();
	private static Double simulatedTime=0.0;
	private static Double minMissedAxonalDelay = Double.MAX_VALUE;
	private static Double minNe_en_ratio;
	private static Double maxNe_en_ratio;
	private static Boolean badCurve=false;
	private static long firingSpikesCounter=0l;
	private static long burningSpikesCounter=0l;
	private static BigInteger region2checkMask = BigInteger.ZERO;
	
	public static synchronized void collectActive(){
		++active;
	}
	
	public static synchronized void collectPassive2active(){
		++passive2active;
	}
	
	public static synchronized void collectActive2passive(){
		++active2passive;
	}
	
	public static synchronized void collectPassive(){
		++passive;
	}
	
	public static void setSimulatedTime(Double sTime){
		simulatedTime=sTime;
	}
	
	public static synchronized void collectFireSpike(Integer firingRegionId, Long firingNeuronId, Double firingTime, Long maxN, Double compressionFactor, Boolean isExcitatory, Boolean isExternal){
		CompressedFire cf = new CompressedFire(firingRegionId, firingNeuronId, firingTime, maxN, compressionFactor);
		Integer tmp = compressor.get(new CompressedFire(firingRegionId, firingNeuronId, firingTime, maxN, compressionFactor));
		if (tmp!=null)
			return;
		firingNeurons.add(new Double(cf.getCompressedNeuronId()));
		firingTimes.add(firingTime);
		if ( region2checkMask.testBit(((int)firingRegionId))){
			FiringNeuron fn= new FiringNeuron(firingRegionId,firingNeuronId,firingTime,isExcitatory,isExternal);
			firingSpikesHashMap.put(new Long(firingSpikesCounter), fn);
		}
		++firingSpikesCounter;
	}
	
	public static synchronized void collectMissedFire(Double missedAxonalDelay){
		if (missedAxonalDelay<minMissedAxonalDelay)
			minMissedAxonalDelay=missedAxonalDelay;
		++missedFires;
	}
	
	public static void printFirePlot(){
		double [] x = new double [firingTimes.size()];
		double [] y = new double [firingNeurons.size()];
		for (int i=0; i<firingNeurons.size();++i){
			x[i]=firingTimes.get(i);
			y[i]=firingNeurons.get(i).doubleValue();
		}
		System.out.println("[Statistics Collector] X size:"+x.length+", Y size:"+y.length);
		System.out.println("[Statistics Collector] firing times size:"+firingTimes.size()+", firing neurons size:"+firingNeurons.size());
		
		ScatterPlotter frame = new ScatterPlotter("Firnet", x, y,simulatedTime); 
		frame.setVisible();
	}
	
	public static void printFirePlot(String outputFileName){
		double [] x = new double [firingTimes.size()];
		double [] y = new double [firingNeurons.size()];
		for (int i=0; i<firingNeurons.size();++i){
			x[i]=firingTimes.get(i);
			y[i]=firingNeurons.get(i).doubleValue();
		}
		System.out.println("[Statistics Collector] X size:"+x.length+", Y size:"+y.length);
		System.out.println("[Statistics Collector] firing times size:"+firingTimes.size()+", firing neurons size:"+firingNeurons.size());
		
		ScatterPlotter frame = new ScatterPlotter("F. N. S.", x, y,simulatedTime,outputFileName); 
		frame.setVisible();
	}
	
	public static void _printFirePlot(){
		float [] x = new float [firingTimes.size()];
		float [] y = new float [firingNeurons.size()];
		for (int i=0; i<firingNeurons.size();++i){
			x[i]=(float)(double)firingTimes.get(i);
			y[i]=(float)(double)firingNeurons.get(i).doubleValue();
		}
		FastScatterPlotter frame = new FastScatterPlotter("Firnet", x, y); 
		frame.setVisible(true);
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
        
	}
	
	public static void makeCsv(String filename){
		PrintWriter burnWriter;
		PrintWriter fireWriter;
		DecimalFormat df = new DecimalFormat("#.################"); 
		try {
			Iterator<Long> it = burningSpikesHashMap.keySet().iterator();
			burnWriter = new PrintWriter(filename+"_burning.csv", "UTF-8");
			burnWriter.println("Burning Time; Firing Node; Firing Neuron; Burning Node; Burning Neuron;"+
						" External Source; From Internal State; To Internal State; Step in State;"+
						" Post Synaptic Weight; Pre Synaptic Weight; Instant to Fire; (Afferent) Firing Time");
			while (it.hasNext()){
				Long key = it.next();
				Double fromState = burningSpikesHashMap.get(key).getFromState();
				Double stepInState=burningSpikesHashMap.get(key).getStepInState();
				String stepInStateToPrint;
				String fromStateToPrint;
				String toStateToPrint;
				if (fromState==null){
					fromStateToPrint="refr";
					toStateToPrint="refr";
				}
				else{
					fromStateToPrint=""+df.format(fromState);
					toStateToPrint=""+df.format(fromState+stepInState);
				}
				if (stepInState==null)
					stepInStateToPrint="refr";
				else
					stepInStateToPrint=""+df.format(stepInState);
				burnWriter.println(
						df.format(burningSpikesHashMap.get(key).getBurnTime())+"; "
						+ burningSpikesHashMap.get(key).getS().getAxonNodeId()+"; "
						+ burningSpikesHashMap.get(key).getS().getAxonNeuronId()+"; "
						+ burningSpikesHashMap.get(key).getS().getDendriteNodeId()+"; "
						+ burningSpikesHashMap.get(key).getS().getDendriteNeuronId()+"; "
						+ burningSpikesHashMap.get(key).getS().fromExternalInput()+"; "
						+ fromStateToPrint +"; "
						+ toStateToPrint +"; "
						+ stepInStateToPrint+"; "
						+ df.format(burningSpikesHashMap.get(key).getPostSynapticWeight())+"; "
						+ df.format(burningSpikesHashMap.get(key).getPresynapticWeight())+";"
						+ df.format(burningSpikesHashMap.get(key).getInstantToFire())+";"
						+ df.format(burningSpikesHashMap.get(key).getFireTime())
						);
			}
			burnWriter.close();
			it=firingSpikesHashMap.keySet().iterator();
			fireWriter=new PrintWriter(filename+"_firing.csv", "UTF-8");
			fireWriter.println("Firing Time; Firing Node; Firing Neuron;  Neuron Type; External Source");
			while (it.hasNext()){
				Long key = it.next();
				String excitStr;
				if (firingSpikesHashMap.get(key).isExcitatory())
					excitStr="excitatory";
				else
					excitStr="inhibitory";
				fireWriter.println(
						df.format(firingSpikesHashMap.get(key).getFiringTime())+"; "
						+firingSpikesHashMap.get(key).getFiringRegionId()+"; "
						+ firingSpikesHashMap.get(key).getFiringNeuronId()+"; "
						+ excitStr+"; "
						+ firingSpikesHashMap.get(key).isExternal()+"; "
						);
			}
			fireWriter.close();
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void setMinMaxNe_en_ratios(Double minNe_en_ratio, Double maxNe_en_ratio){
		StatisticsCollector.minNe_en_ratio=minNe_en_ratio;
		StatisticsCollector.maxNe_en_ratio=maxNe_en_ratio;	
	}
	
	public static void PrintResults(){
		String minNe_en_ratioStr=(minNe_en_ratio==PackageReader.MIN_NE_EN_RATIO_DEF)?
				"no connection between nodes":(""+minNe_en_ratio);
		String maxNe_en_ratioStr=(maxNe_en_ratio==PackageReader.MAX_NE_EN_RATIO_DEF)?
				"no connection between nodes":(""+maxNe_en_ratio);
		System.out.println("active:"+active);
		System.out.println("passive:"+passive);
		System.out.println("active to passive:"+active2passive);
		System.out.println("passive to active:"+passive2active);
		System.out.println("min Ne en ratio:"+minNe_en_ratioStr);
		System.out.println("max Ne en ratio:"+maxNe_en_ratioStr);
		System.out.println("missed Fires:"+missedFires);
		if (missedFires>0)
			System.out.println("minimum missed fire axonal delay:"+minMissedAxonalDelay);
		System.out.println("Good curve:"+(!badCurve));
	}
	
	public static void setBadCurve(){
		badCurve=true;
	}

	public synchronized static void collectBurnSpike(
			Synapse s, 
			Double burnTime, 
			Boolean fromExternalSource, 
			Double fromState, 
			Double stepInState, 
			Double postsynapticWeight, 
			Double presynapticWeight, 
			Double timeToFire,
			Double fireTime) {
		if ( region2checkMask.testBit(((int)s.getDendriteNodeId()))){
			SpikingSynapse ss = new SpikingSynapse(s, burnTime,fromExternalSource, fromState, stepInState, postsynapticWeight, presynapticWeight, timeToFire, fireTime);
			burningSpikesHashMap.put(new Long(burningSpikesCounter), ss);
		}
		++burningSpikesCounter;
	}
	
	public static void setNodes2checkMask(BigInteger mask){
		region2checkMask=mask;
	}
	
	
}

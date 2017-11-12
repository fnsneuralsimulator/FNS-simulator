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



package connectivity.conn_package;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import connectivity.nodes.ConnectivityPackageManager;

/**
 * 
 * @author knizontes@gmail.com
 *
 */
public class PackageReader {
	
	public static final Double MAX_NE_EN_RATIO_DEF=0.0;
	public static final Double MIN_NE_EN_RATIO_DEF=Double.MAX_VALUE;
	public static final Double MAX_AMPLITUDE_DEF=0.0;
	public static final Double MIN_AMPLITUDE_DEF=Double.MAX_VALUE;
	private String packagePath;
	private Integer vertexNum;
	private Long edgesNum;
	private ConnectivityPackageManager rm;
	private final static Double zeroDouble = new Double (0);
	private final static String TAG = "[Package Reader]";
	private static Boolean verbose = true;
	private Double maxNe_en_ratio=MAX_NE_EN_RATIO_DEF;
	private Double maxAmplitude=MAX_AMPLITUDE_DEF;
	private Double minNe_en_ratio=MIN_NE_EN_RATIO_DEF;
	private Double minAmplitude=MIN_AMPLITUDE_DEF;
	private static final String AMPLITUDES_FILE_NAME="mu_omega.txt";
	private static final String AMPLITUDES_STD_DEVIATION_FILE_NAME="sigma_omega.txt";
	private static final String TRACT_LENGTHS_FILE_NAME="mu_lambda.txt";
	private static final String TRACT_LENGTHS_SHAPE_PARAMETERS_FILE_NAME="alpha_lambda.txt";
	private static final String CENTRES_FILE_NAME="centres.txt";
	private static final String WEIGHTS_FILE_NAME="Ne_en_ratio.txt";
	
	public PackageReader (ConnectivityPackageManager rm){
		this.rm=rm;
	}
	
	public void readConnectivityPackage(String path){
		packagePath=path;
		if (path.charAt(path.length()-1)!='/')
			packagePath+='/';
		println("reading configs file...");
		readCentersFile();
		println("done.");
		println("reading the Ne-en-ratio file...");
		readWeightsFile();
		println("reading the mu omega file...");
		readAmplitudesFile();
		println("reading the sigma omega file...");
		readAmplitudesStdDeviationFile();
		println("reading the mu lambda file...");
		readLengthsFile();
		println("done.");
	}
	
	private void readCentersFile(){
		int i=0;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+CENTRES_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [4];
				int k=0;
				for (int j=0; k<4 ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				rm.addNode(goodTkns[0], new Double(goodTkns[1]), 
						new Double(goodTkns[2]), new Double(goodTkns[3]));
				++i;
			}
			vertexNum=i;
		} catch (FileNotFoundException e) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(packagePath+WEIGHTS_FILE_NAME));
				int lines=0;
				while (reader.readLine() != null){
					rm.addNode();
					++lines;
				}
				reader.close();
				vertexNum=lines;
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			}catch (IOException e1) {
				e1.printStackTrace();
			} 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readWeightsFile(){
		int i=0;
		Double d;
		edgesNum=0l;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+WEIGHTS_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [vertexNum];
				int k=0;
				for (int j=0; k<vertexNum ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				for (int j=0; j<vertexNum; ++j){
					if (i==j)
						continue;
					d=new Double(goodTkns[j]);
					if (d>maxNe_en_ratio)
						maxNe_en_ratio=d;
					
					if (!d.equals(zeroDouble)){
						if (d<minNe_en_ratio)
							minNe_en_ratio=d;
						rm.addEdge(i, j, (d));
						++edgesNum;
					}
				}
				++i;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readAmplitudesFile(){
		int i=0;
		Double d;
//		edgesNum=0l;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+AMPLITUDES_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [vertexNum];
				int k=0;
				for (int j=0; k<vertexNum ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				for (int j=0; j<vertexNum; ++j){
					if (i==j)
						continue;
					d=new Double(goodTkns[j]);
					if (d>maxAmplitude)
						maxAmplitude=d;
						if (d<minAmplitude)
							minAmplitude=d;
						rm.addAmplitude(i, j, (d));
				}
				++i;
			}
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readAmplitudesStdDeviationFile(){
		int i=0;
		Double d;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+AMPLITUDES_STD_DEVIATION_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [vertexNum];
				int k=0;
				for (int j=0; k<vertexNum ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				for (int j=0; j<vertexNum; ++j){
					if (i==j)
						continue;
					d=new Double(goodTkns[j]);
					
					if (!d.equals(zeroDouble)){
						if (d<minNe_en_ratio)
							minNe_en_ratio=d;
						rm.addAmplitudeStdDeviation(i, j, (d));
					}
				}
				++i;
			}
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readLengthsFile(){
		int i=0;
		Double d;
		edgesNum=0l;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+TRACT_LENGTHS_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [vertexNum];
				int k=0;
				for (int j=0; k<vertexNum ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				for (int j=0; j<vertexNum; ++j){
					if (i==j)
						continue;
					d=new Double(goodTkns[j]);
					if (!d.equals(zeroDouble)){
						rm.addLength(i, j, d);
						++edgesNum;					
					}
				}
				++i;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readLengthsShapeParametersFile(){
		int i=0;
		Double d;
		try (BufferedReader br = new BufferedReader(new FileReader(packagePath+TRACT_LENGTHS_SHAPE_PARAMETERS_FILE_NAME))){
			for (String line; (line=br.readLine())!=null;){
				String [] tokens = line.split(" ");
				String [] goodTkns = new String [vertexNum];
				int k=0;
				for (int j=0; k<vertexNum ; ++j){
					if (!tokens[j].equals(""))
						goodTkns[k++]=tokens[j];						
				}
				for (int j=0; j<vertexNum; ++j){
					if (i==j)
						continue;
					d=new Double(goodTkns[j]);
					if (!d.equals(zeroDouble)){
						rm.addLengthShapeParameter(i, j, d);
					}
				}
				++i;
			}
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Integer getVertexNum(){
		return vertexNum;
	}
	
	public Long getedgesNum(){
		return edgesNum;
	}
	
	public Double getMinNe_en_ratio(){
		return minNe_en_ratio;
	}
	
	public Double getMaxNe_en_ratio(){
		return maxNe_en_ratio;
	}
	
	private void println(String s){
		if (verbose)
			System.out.println(TAG+s);
	}
	

}

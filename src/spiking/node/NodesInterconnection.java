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

package spiking.node;

import spiking.controllers.node.NodeThread;
import utils.constants.Constants;
import utils.tools.IntegerCouple;

public class NodesInterconnection extends IntegerCouple{
  
  /*
   * The schema for internode connections
   * 
   *   \        |       |       |       |
   *    \  from |  mixed |   exc  |  inh  |
   *  to \      |       |       |       |
   * ------------------------------------
   *    mixed   |   0   |   3   |   6   |
   * ------------------------------------
   *     exc    |   1   |   4   |   7   |
   * ------------------------------------
   *     inh    |   2   |   5   |   8   |
   * ------------------------------------
   *  
   */
  public static final Integer MIXED2MIXED=0;
  public static final Integer MIXED2EXC=1;
  public static final Integer MIXED2INH=2;
  public static final Integer EXC2MIXED=3;
  public static final Integer EXC2EXC=4;
  public static final Integer EXC2INH=5;
  public static final Integer INH2MIXED=6;
  public static final Integer INH2EXC=7;
  public static final Integer INH2INH=8;
  //the sm interconnection probability
  private Double Ne_xn_ratio;
  private Double mu_lambda= Constants.LENGTH_DEF_VAL;
  private Double mu_omega=1.0;
  private Double sigma_omega=0.0;
  private Double alpha_lambda=null;
  private Integer type=0;
  
  
  public NodesInterconnection(
      NodeThread r1, 
      NodeThread r2, 
      Double Ne_xn_ratio, 
      Double mu_omega, 
      Double sigma_omega, 
      Double mu_lambda, 
      Double alpha_lambda,
      int type){
    super(r1.getNodeId() ,r2.getNodeId());
    this.Ne_xn_ratio=Ne_xn_ratio;
    this.mu_omega=mu_omega;
    this.sigma_omega=sigma_omega;
    this.mu_lambda=mu_lambda;
    this.sigma_omega=sigma_omega;
    this.type=(type<=8)?type:MIXED2MIXED;
      
  }
  
  public NodesInterconnection(Integer r1, Integer r2, Double Ne_xn_ratio){
    super(r1,r2);
    this.Ne_xn_ratio=Ne_xn_ratio;
  }
  
  public Double getNe_xn_ratio(){
    return Ne_xn_ratio;
  }
  
  public void setLength(Double length){
    this.mu_lambda=length;
  }
  
  public Double getLength(){
    if (mu_lambda==null){
      System.out.println("[NODES INTERCONNECTION WARNING] length 0 error");
      try {
          throw new Exception();
       }
       catch (Exception e) {
          e.printStackTrace();
       }
      System.exit(1);
    }
    return mu_lambda;
  }

  public Double getMu_omega() {
    return mu_omega;
  }

  public void setAmplitude(Double amplitude) {
    this.mu_omega = amplitude;
  }
  
  public Double getSigma_w(){
    return sigma_omega;
  }
  
  public void setAmplitudeStdDeviation(Double amplitudeStdDeviation) {
    this.sigma_omega = amplitudeStdDeviation;
  }
  

  public Double getLengthShapeParameter() {
    return alpha_lambda;
  }

  public void setLengthShapeParameter(Double lengthShapeParameter) {
    this.alpha_lambda = lengthShapeParameter;
  }
  
  public void setType(int type) {
    this.type=(type<=8)?type:MIXED2MIXED;
  }
  
  public Integer getType() {
    return type;
  }

}

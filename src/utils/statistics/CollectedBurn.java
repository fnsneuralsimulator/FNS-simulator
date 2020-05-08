package utils.statistics;

import spiking.node.Synapse;

public class CollectedBurn {

  //private Synapse s; 
  private Long firingNeuronId;
  private int firingNodeId;
  private Long burningNeuronId;
  private int burningNodeId;
  private Double burnTime; 
  private Boolean fromExternalInput; 
  private Double fromState; 
  private Double stepInState; 
  private Double postsynapticWeight; 
  private Double presynapticWeight; 
  private Double timeToFire;
  private Double fireTime;
  
  public CollectedBurn(
      //Synapse s, 
      Long firingNeuronId,
      int firingNodeId,
      Long burningNeuronId,
      int burningNodeId,
      Double burnTime, 
      Boolean fromExternalInput, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire,
      Double fireTime) {
    //this.s=s;
    this.firingNeuronId=firingNeuronId;
    this.firingNodeId=firingNodeId;
    this.burningNeuronId=burningNeuronId;
    this.burningNodeId=burningNodeId;
    this.burnTime=burnTime;
    this.fromExternalInput=fromExternalInput;
    this.fromState=fromState;
    this.stepInState=stepInState;
    this.postsynapticWeight=postsynapticWeight;
    this.presynapticWeight=presynapticWeight;
    this.timeToFire=timeToFire;
    this.fireTime=fireTime;
  }

  //public Synapse getS() {
  //  return s;
  //}

  public Long getFiringNeuronId(){
    return firingNeuronId;
  }

  public Integer getFiringNodeId(){
    return firingNodeId;
  }

  public Long getBurningNeuronId(){
    return burningNeuronId;
  }

  public Integer getBurningNodeId(){
    return burningNodeId;
  }

  public Double getBurnTime() {
    return burnTime;
  }

  public Boolean getFromExternalSource() {
    return fromExternalInput;
  }

  public Boolean fromExternalInput() {
    return fromExternalInput;
  }

  public Integer fromExternalInputInteger() {
    return fromExternalInput ? 1 : 0;
  }


  public Double getFromState() {
    return fromState;
  }

  public Double getStepInState() {
    return stepInState;
  }

  public Double getPostSynapticWeight() {
    return postsynapticWeight;
  }

  public Double getPreSynapticWeight() {
    return presynapticWeight;
  }

  public Double getTimeToFire() {
    return timeToFire;
  }

  public Double getFireTime() {
    return fireTime;
  }
  
  
}

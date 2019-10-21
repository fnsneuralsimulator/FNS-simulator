package utils.statistics;

import spiking.node.Synapse;

public class CollectedBurn {

  private Synapse s; 
  private Double burnTime; 
  private Boolean fromExternalSource; 
  private Double fromState; 
  private Double stepInState; 
  private Double postsynapticWeight; 
  private Double presynapticWeight; 
  private Double timeToFire;
  private Double fireTime;
  
  public CollectedBurn(
      Synapse s, 
      Double burnTime, 
      Boolean fromExternalSource, 
      Double fromState, 
      Double stepInState, 
      Double postsynapticWeight, 
      Double presynapticWeight, 
      Double timeToFire,
      Double fireTime) {
    this.s=s;
    this.burnTime=burnTime;
    this.fromExternalSource=fromExternalSource;
    this.fromState=fromState;
    this.stepInState=stepInState;
    this.postsynapticWeight=postsynapticWeight;
    this.presynapticWeight=presynapticWeight;
    this.timeToFire=timeToFire;
    this.fireTime=fireTime;
  }

  public Synapse getS() {
    return s;
  }

  public Double getBurnTime() {
    return burnTime;
  }

  public Boolean getFromExternalSource() {
    return fromExternalSource;
  }

  public Double getFromState() {
    return fromState;
  }

  public Double getStepInState() {
    return stepInState;
  }

  public Double getPostsynapticWeight() {
    return postsynapticWeight;
  }

  public Double getPresynapticWeight() {
    return presynapticWeight;
  }

  public Double getTimeToFire() {
    return timeToFire;
  }

  public Double getFireTime() {
    return fireTime;
  }
  
  
}

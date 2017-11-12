package spiking.node;

public class FiringNeuron {
	
	private Integer firingRegionId;
	private Long firingNeuronId;
	private Double firingTime;
	private Boolean isExcitatory;
	private Boolean isExternal;
	
	public FiringNeuron(Integer firingRegionId, Long firingNeuronId, Double firingTime, Boolean isExcitatory, Boolean isExternal){
		this.firingRegionId=firingRegionId;
		this.firingNeuronId=firingNeuronId;
		this.firingTime=firingTime;
		this.isExternal=isExternal;
		if (isExternal)
			this.isExcitatory=true;
		else
			this.isExcitatory=isExcitatory;
	}

	public Integer getFiringRegionId() {
		return firingRegionId;
	}

	public Long getFiringNeuronId() {
		return firingNeuronId;
	}

	public Double getFiringTime() {
		return firingTime;
	}

	public Boolean isExcitatory() {
		return isExcitatory;
	}

	public Boolean isExternal() {
		return isExternal;
	}
	
	

}

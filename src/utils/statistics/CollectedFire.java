package utils.statistics;

import spiking.node.Synapse;

public class CollectedFire {

	private Integer firingRegionId; 
	private Long firingNeuronId; 
	private Double firingTime; 
	private Long maxN; 
	private Double compressionFactor; 
	private Boolean isExcitatory; 
	private Boolean isExternal;
	
	public CollectedFire(
			Integer firingRegionId, 
			Long firingNeuronId, 
			Double firingTime, 
			Long maxN, 
			Double compressionFactor, 
			Boolean isExcitatory, 
			Boolean isExternal) {
		this.firingRegionId=firingRegionId;
		this.firingNeuronId=firingNeuronId;
		this.firingTime=firingTime;
		this.maxN= maxN;
		this.compressionFactor=compressionFactor;
		this.isExcitatory=isExcitatory;
		this.isExternal=isExternal;
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

	public Long getMaxN() {
		return maxN;
	}

	public Double getCompressionFactor() {
		return compressionFactor;
	}

	public Boolean getIsExcitatory() {
		return isExcitatory;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}
	
	
	
	
	
}

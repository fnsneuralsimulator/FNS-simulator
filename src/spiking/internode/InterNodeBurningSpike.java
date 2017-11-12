package spiking.internode;

import java.io.Serializable;

public class InterNodeBurningSpike implements Comparable<InterNodeBurningSpike>, Serializable{

	
	private static final long serialVersionUID = 4390183727488402447L;
	private Double timeToBurn;
	private InterNodeSpike interNodeSpike;
	
	public InterNodeBurningSpike(InterNodeSpike internodeSpike, Double timeToBurn) {
		this.interNodeSpike=internodeSpike;
		this.timeToBurn=timeToBurn;
	}
	
	public Double getTimeToBurn(){
		return timeToBurn;
	}
	
	public InterNodeSpike getInterNodeSpike(){
		return interNodeSpike;
	}
	
	public String toString(){
    	return "interNodeSpike:"+interNodeSpike+",\ttimeToBurn:"+timeToBurn;
    }
    
    public int compareTo(InterNodeBurningSpike node){
        return Double.compare(timeToBurn,node.getTimeToBurn());
    }

}

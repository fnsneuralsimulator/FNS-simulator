/**
 *  Copyright 2015-2016 Emanuele Paracone - knizontes
 *  knizontes_æŧ_gmail.com
 *  emanuele.paracone_æŧ_gmail.com
 *  emanuele.paracone_æŧ_hpe.com
 *  https://github.com/knizontes
 *  http://knizontes.ns0.it
 *  
 *  This file is part of k-fren.
 *
 *  k-fren is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  k-fren is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with k-fren.  If not, see <http://www.gnu.org/licenses/>.
 */


package connectivity.nodes;

public class Node {
	private static Integer globalId=0;
	private Integer id=globalId;
	private String label;
	private Double x,y,z;
	private ConnectivityPackageManager rm;
	
	public Node(ConnectivityPackageManager rm, String label, Double x, Double y, Double z){
		this.rm = rm;
		this.label = label;
		this.x=x;
		this.y=y;
		this.z=z;
		++globalId;
	}
	
	public Node(){
		++globalId;
	}

	public String getLabel() {
		return label;
	}

	public Double getX() {
		return x;
	}

	public Double getY() {
		return y;
	}

	public Double getZ() {
		return z;
	}
	
	public Integer getId(){
		return id;
	}
	
	public String toString(){
		return "node:"+id+", name:"+label+", x:"+x+", y:"+y+", z:"+z;
	}

	public static void main(String[] args) {
		ConnectivityPackageManager rm = new ConnectivityPackageManager();
		Node r1 = new Node(rm, "nd1", 1.432, 12.342, 12.423);
		Node r2 = new Node(rm, "nd2", 1.4232, 12.3654, 134.423);
		System.out.println(r1.toString());
		System.out.println(r2.toString());
		
	}
		
	
}

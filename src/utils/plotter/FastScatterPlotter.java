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


package utils.plotter;

import java.awt.RenderingHints;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.ApplicationFrame;

public class FastScatterPlotter extends ApplicationFrame{


	private float [][]data;

	public FastScatterPlotter(String title, float [] x, float[] y) {
		super(title);
		data = new float [2][x.length];
		for (int i=0; i<x.length;++i){
			data[0][i]=(float)x[i];
			data[1][i]=(float)y[i];
		}
		final NumberAxis domainAxis = new NumberAxis("X");
		domainAxis.setAutoRangeIncludesZero(false);
		final NumberAxis rangeAxis = new NumberAxis("Y");
		rangeAxis.setAutoRangeIncludesZero(false);
		final FastScatterPlot plot = new FastScatterPlot();//(this.data, domainAxis, rangeAxis);
		plot.setData(data);
		plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(rangeAxis);
		final JFreeChart chart = new JFreeChart("Fast Scatter Plot", plot);
		//      chart.setLegend(null);

		// force aliasing of the rendered content..
		chart.getRenderingHints().put
		(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		final ChartPanel panel = new ChartPanel(chart, true);
		panel.setPreferredSize(new java.awt.Dimension(500, 270));
		//      panel.setHorizontalZoom(true);
		//    panel.setVerticalZoom(true);
		panel.setMinimumDrawHeight(10);
		panel.setMaximumDrawHeight(2000);
		panel.setMinimumDrawWidth(20);
		panel.setMaximumDrawWidth(2000);

		setContentPane(panel);	
	}
	
	

}

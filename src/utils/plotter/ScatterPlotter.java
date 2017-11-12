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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

public class ScatterPlotter {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5840221804907296764L;
	private JFrame frame;

//	public ScatterPlotter(String s, JFreeChart jfreechart) {
//		super(s);
//		JPanel jpanel = createDemoPanel(jfreechart);
//		jpanel.setPreferredSize(new Dimension(640, 480));
//		add(jpanel);
//	}
//
//	public static JPanel createDemoPanel(JFreeChart jfreechart) {
//		double size = 2.0;
//	    double delta = size / 2.0;
//		Shape shp = new Ellipse2D.Double(-delta, -delta, size, size);
//		XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
//		xyPlot.setDomainCrosshairVisible(true);
//		xyPlot.setRangeCrosshairVisible(true);
//		XYItemRenderer renderer = xyPlot.getRenderer();
//		renderer.setSeriesShape(0, shp);
//		renderer.setSeriesPaint(0, Color.blue);
//		return new ChartPanel(jfreechart);
//	}
	
	public ScatterPlotter(String s, double [] x, double[] y, Double simulatedTime){
		int lastMillis = 15;
		if (simulatedTime!=null)
			lastMillis = simulatedTime.intValue()+1;
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot(s, x, y);
		plot.setFixedBounds(0, 0, (int)(lastMillis*1.3));
		// add a title
        BaseLabel title = new BaseLabel("Fires", Color.RED, 0.5, 1.1);
        title.setFont(new Font("Courier", Font.BOLD, 20));
        plot.addPlotable(title);
		// put the PlotPanel in a JFrame like a JPanel
        frame = new JFrame(s);
        frame.setSize(1500, 700);
        frame.setContentPane(plot);
        
	}
	
	public ScatterPlotter(String s, double [] x, double[] y, Double simulatedTime, String filename){
		int lastMillis = 15;
		if (simulatedTime!=null)
			lastMillis = simulatedTime.intValue()+1;
		int width = 1500;
		int height = 700;
		Plot2DPanel plot = new Plot2DPanel();
		plot.addScatterPlot(s, x, y);
		plot.setFixedBounds(0, 0, (int)(lastMillis*1.1));
		// add a title
        BaseLabel title = new BaseLabel("Fires", Color.RED, 0.5, 1.1);
        title.setFont(new Font("Courier", Font.BOLD, 20));
        plot.addPlotable(title);
		// put the PlotPanel in a JFrame like a JPanel
        frame = new JFrame(s);
        frame.setSize(width, height);
        frame.setContentPane(plot);
//        Component component =  frame.getContentPane();
        BufferedImage image = new BufferedImage(
        	      width,
        	      height,
        	      BufferedImage.TYPE_INT_RGB
        	      );
        plot.paint( image.getGraphics() ); 
//        try {
//            // write the image as a PNG
//            ImageIO.write(
//              image,
//              "png",
//              new File(filename+".png"));
//          } catch(Exception e) {
//            e.printStackTrace();
//          }
       
        
	}
	
	public void setVisible(){
		frame.setVisible(true);
	}

	
	


}

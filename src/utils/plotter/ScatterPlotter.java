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


package utils.plotter;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;
public class ScatterPlotter {


  /**
   * 
   */
  private static final long serialVersionUID = 5840221804907296764L;
  private JFrame frame;
  
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
        
  }
  
  public void setVisible(){
    frame.setVisible(true);
  }

  
  


}

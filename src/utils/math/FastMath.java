/* fast floating point exp function
 * must initialize table with buildexptable before using
 
Based on 
 A Fast, Compact Approximation of the Exponential Function
 Nicol N. Schraudolph 1999

Adapted to single precision to improve speed and added adjustment table to improve accuracy.
Alrecenk 2014

* i = ay + b
* a = 2^(mantissa bits) / ln(2)   ~ 12102203
* b = (exponent bias) * 2^ ( mantissa bits) ~ 1065353216
*/

package utils.math;
 
public class FastMath{

  public FastMath(){
    buildexptable(-10.0,10.0,0.0001);
  }
  
  private static float expadjust[] ;
  
  public float fastexp(float x){
    final int temp = (int)(12102203 * x + 1065353216) ;
    return Float.intBitsToFloat(temp)*expadjust[(temp>>15)&0xff] ;
  }
  
  public float fastexp(Double x){
    final int temp = (int)(12102203 * x + 1065353216) ;
    return Float.intBitsToFloat(temp)*expadjust[(temp>>15)&0xff] ;
  }
  //build correction table to improve result in region of interest
  //if region of interest is large enough then improves result everywhere
  private void buildexptable(double min, double max, double step){
    expadjust = new float[256];
    int amount[] = new int[256] ;
    //calculate what adjustments should have been for values in region
    for(double x=min; x < max;x+=step){
      double exp = Math.exp(x);
      int temp = (int)(12102203 * x + 1065353216) ;
      int index = (temp>>15)&0xff ;
        double fexp = Float.intBitsToFloat(temp);
      expadjust[index]+= exp/fexp ;
      amount[index]++;
    }
    //average them out to get adjustment table
    for(int k=0;k<amount.length;k++){
      expadjust[k]/=amount[k];
    }
  }
  
  public static void main(String[] args) {
    FastMath fm = new FastMath();
    Double d=0.01;
    float f=10;
    float retval=0;
    retval=fm.fastexp(d);
    System.out.println("retval: "+retval);
  }
}
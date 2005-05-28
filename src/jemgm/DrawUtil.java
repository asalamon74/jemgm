package jemgm;

import java.awt.Graphics;
import java.awt.Color;

/**
 * DrawUtil.java
 *
 * Found the methods somewhere int the Internet.
 *
 * Created: Tue Jul 16 19:34:30 2002
 *
 * @author Salamon Andras
 * @version
 */

public class DrawUtil  {

    static int al = 12;         // Arrow length
    static int aw = 8;         // Arrow width
    static int haw = aw/2;     // Half arrow width
    static int xValues[] = new int[3];
    static int yValues[] = new int[3];
    
    public DrawUtil() {
            // TODO: implement
    }

    public static void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
        // The thick line is in fact a filled polygon
        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;
        // line length
        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double)(thickness) / (2 * lineLength);

        // The x and y increments from an endpoint needed to create a rectangle...
        double ddx = -scale * (double)dY;
        double ddy = scale * (double)dX;
        ddx += (ddx > 0) ? 0.5 : -0.5; 
        ddy += (ddy > 0) ? 0.5 : -0.5;   
        int dx = (int)ddx;
        int dy = (int)ddy;
        // Now we can compute the corner points...
        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }

    public static void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        // Draw line
        drawThickLine(g,x1,y1,x2,y2,2,g.getColor());
        // Calculate x-y values for arrow head
        calcValues(x1,y1,x2,y2);
        g.fillPolygon(xValues,yValues,3);
    }

    /* CALC VALUES: Calculate x-y values. */
        
    public static void calcValues(int x1, int y1, int x2, int y2) {
        // North or south       
        if (x1 == x2) { 
            // North
            if (y2 < y1) arrowCoords(x2,y2,x2-haw,y2+al,x2+haw,y2+al);
            // South
            else arrowCoords(x2,y2,x2-haw,y2-al,x2+haw,y2-al);
            return;
            }   
        // East or West 
        if (y1 == y2) {
            // East
            if (x2 > x1) arrowCoords(x2,y2,x2-al,y2-haw,x2-al,y2+haw);
            // West
            else arrowCoords(x2,y2,x2+al,y2-haw,x2+al,y2+haw);
            return;
            }
        // Calculate quadrant
        
        calcValuesQuad(x1,y1,x2,y2);
        }

    /* CALCULATE VALUES QUADRANTS: Calculate x-y values where direction is
not
    parallel to eith x or y axis. */
    
   public static void calcValuesQuad(int x1, int y1, int x2, int y2) { 
        double arrowAng = toDegrees (Math.atan((double) haw/(double)
al));
        double dist = Math.sqrt(al*al + aw);
        double lineAng = toDegrees(Math.atan(((double) Math.abs(x1-x2))/
                            ((double) Math.abs(y1-y2))));
                                
        // Adjust line angle for quadrant
        if (x1 > x2) {
            // South East
            if (y1 > y2) lineAng = 180.0-lineAng;
            }
        else {
            // South West
            if (y1 > y2) lineAng = 180.0+lineAng;
            // North West
            else lineAng = 360.0-lineAng;
            }
        
        // Calculate coords
        
        xValues[0] = x2;
        yValues[0] = y2;        
        calcCoords(1,x2,y2,dist,lineAng-arrowAng);
        calcCoords(2,x2,y2,dist,lineAng+arrowAng);
        }
    
    /* CALCULATE COORDINATES: Determine new x-y coords given a start x-y
and
    a distance and direction */
    
    public static void calcCoords(int index, int x, int y, double dist, 
                double dirn) {
        //        System.out.println("dirn = " + dirn);
        while(dirn < 0.0)   dirn = 360.0+dirn;
        while(dirn > 360.0) dirn = dirn-360.0;
        //        System.out.println("dirn = " + dirn);
                
        // North-East
        if (dirn <= 90.0) {
            xValues[index] = x + (int)(Math.sin(toRadians(dirn))*dist);
            yValues[index] = y - (int)(Math.cos(toRadians(dirn))*dist);
            return;
            }
        // South-East
        if (dirn <= 180.0) {
            xValues[index] = x + (int)(Math.cos(toRadians(dirn-90))*dist);
            yValues[index] = y + (int)(Math.sin(toRadians(dirn-90))*dist);
            return;
            }
        // South-West
        if (dirn <= 90.0) {
            xValues[index] = x - (int)(Math.sin(toRadians(dirn-180))*dist);
            yValues[index] = y + (int)(Math.cos(toRadians(dirn-180))*dist);
            }
        // Nort-West    
        else {
            xValues[index] = x - (int)(Math.cos(toRadians(dirn-270))*dist);
            yValues[index] = y - (int)(Math.sin(toRadians(dirn-270))*dist);
            }
        }      

    
    // ARROW COORDS: Load x-y value arrays */
    
    public static void arrowCoords(int x1, int y1, int x2, int y2, int x3, int
y3) {
        xValues[0] = x1;
        yValues[0] = y1;
        xValues[1] = x2;
        yValues[1] = y2;
        xValues[2] = x3;
        yValues[2] = y3;
    } 

    // no toDegrees int JDK 1.1
    public static double toDegrees(double angrad) {
        return angrad * 180.0 / Math.PI;
    }

    // no toRadians int JDK 1.1
    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * Math.PI;
    }

} // DrawUtil

package jemgm;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

/**
 * HexMap.java
 *
 *
 * Created: Mon Feb 18 17:27:04 2002
 *
 * @author Salamon Andras
 * @version
 */

public class HexMap extends JPanel implements MouseListener, MouseMotionListener {
    
    Image capitalImage =  Toolkit.getDefaultToolkit().getImage( "images/capital.png" );
    
    public HexMap(Manager aodm) {
        addMouseListener(this);
        addMouseMotionListener(this);
        setAodm(aodm);
        //	cc.setGame(aodm.getGame());
    }
    
    Manager aodm;
    
    /**
     * Get the value of aodm.
     * @return Value of aodm.
     */
    public Manager getAodm() {
        return aodm;
    }
    
    /**
     * Set the value of aodm.
     * @param v  Value to assign to aodm.
     */
    public void setAodm(Manager  v) {
        this.aodm = v;
    }
    
    
    AreaDataBase adb;
    
    /**
     * Get the value of adb.
     * @return Value of adb.
     */
    public AreaDataBase getAdb() {
        return adb;
    }
    
    /**
     * Set the value of adb.
     * @param v  Value to assign to adb.
     */
    public void setAdb(AreaDataBase  v) {
        this.adb = v;
        Player p = aodm.game.getPlayer();
        System.out.println("supply:"+adb.getSupplyPointNum(p));
        System.out.println("army:"+adb.getArmyStrength(p));
    }
    
    
    CommandCollection cc;// = new CommandCollection();
    
    /**
     * Get the value of cc.
     * @return Value of cc.
     */
    public CommandCollection getCc() {
        return cc;
    }
    
    /**
     * Set the value of cc.
     * @param v  Value to assign to cc.
     */
    public void setCc(CommandCollection  v) {
        this.cc = v;
    }
    
    
    public void paint(Graphics g) {
        if( imageBuffer == null //||
                //            imageBuffer.getWidth(null)  != getSize().width ||
                //            imageBuffer.getHeight(null) != getSize().height ) {
                && adb.getXSize() != 0
                ) {
            int xsize = (int)(2*adb.getXSize()*xdiff);
            int ysize = (int)(2*adb.getYSize()*ydiff);
            imageBuffer = createImage(xsize, ysize);
            graphicsBuffer = imageBuffer.getGraphics();
            graphicsBuffer.setClip(0, 0, xsize, ysize);
            offScreen = true;
        }
        if( offScreen ) {
            if( needRepaint ) {
                realPaint(graphicsBuffer);
                needRepaint = false;
            }
            g.drawImage(imageBuffer, -offX, -offY, this);
        } else {
            realPaint(g);
        }
    }
    
    /**
     * This is the real paint method.
     */
    public void realPaint(Graphics g) {
        System.out.println("realPaint");
        g.setColor(getBackground());
        g.fillRect(0, 0, (int)(2*adb.getXSize()*xdiff), (int)(2*adb.getYSize()*ydiff));
        g.setColor(borderColor);
        for( int i=1; i<=2*adb.getXSize(); ++i ) {
            for( int j=1; j<=2*adb.getYSize(); ++j ) {
                int reali = ((i-1) % adb.getXSize()) + 1;
                int realj = ((j-1) % adb.getYSize()) + 1;
                int xpoints[] = new int[6];
                for( int xi = 0; xi<6; ++xi ) {
                    xpoints[xi] = (int)(topx + xhex[xi]+i*xdiff);
                }
                int ypoints[] = new int[6];
                double jj = j - (reali % 2)*0.5;
                for( int yi = 0; yi<6; ++yi ) {
                    ypoints[yi] = (int)(topy + yhex[yi]+jj*ydiff);
                }
                AreaInformation ai = adb.getAreaInformation(adb.getId(reali,realj));
                if( ai != null ) {
                    g.setColor(getColor(ai));
                    g.fillPolygon(xpoints, ypoints, 6);
                    g.setColor(borderColor);
                    g.drawPolygon(xpoints, ypoints, 6);
                }
                if( supplyDraw && ai != null && ai.getX(1) == reali && ai.getY(1) == realj &&
                        ai.getSupplyPointNum() != 0 ) {
                    if( drawUnitHere(ai, reali, realj) ||
                            drawNewUnitHere(ai, reali, realj)) {
                        if( ai.isCapital() ) {
                            drawCapital(g, i, j, getColor(ai.getOwner()), false);
                        } else {
                            drawSmallSupplyPoints(g, i, j, ai.getSupplyPointNum());
                        }
                    } else {
                        if( ai.isCapital() ) {
                            drawCapital(g, i, j, getColor(ai.getOwner()), true);
                        } else {
                            drawSupplyPoints(g, i, j, ai.getSupplyPointNum());
                        }
                    }
                }
                if( numberDraw && ai != null && ai.getX(1) == reali && ai.getY(1) == realj ) {
                    drawNumber(g, i, j, ""+ai.getId());
                }
                if( drawUnitHere(ai, reali, realj) ) {
                    drawUnit(g, i, j, ai.getUnitType(), getColor(ai.getUnitOwner()), borderColor);
                }
                
                if( adb.getId(reali+1, realj+1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali+1, realj+1, getColor(ai));
                }
                if( adb.getId(reali+1, realj) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali+1, realj, getColor(ai));
                }
                if( adb.getId(reali+1, realj-1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali+1, realj-1, getColor(ai));
                }
                if( adb.getId(reali, realj+1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali, realj+1, getColor(ai));
                }
                if( adb.getId(reali, realj-1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali, realj-1, getColor(ai));
                }
                
                if( adb.getId(reali-1, realj+1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali-1, realj+1, getColor(ai));
                }
                if( adb.getId(reali-1, realj) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali-1, realj, getColor(ai));
                }
                if( adb.getId(reali-1, realj-1) == ai.getId() ) {
                    drawLineBetween(g, reali, realj, reali-1, realj-1, getColor(ai));
                }
            }
        }
        
//         // delete the line between hexes
//         if( adb.getXSize() != 0) {
//             for( int ainum = 1; ainum<adb.getXSize()*adb.getYSize(); ++ainum ) {
//                 AreaInformation ai = adb.getAreaInformation(ainum);
//                 if( ai != null && ai.getX(2) != 0 ) {
//                     drawLineBetween(g, ai.getX(1), ai.getY(1), ai.getX(2), ai.getY(2), getColor(ai));
//                     if( ai.getX(3) != 0 ) {
//                         drawLineBetween(g, ai.getX(1), ai.getY(1), ai.getX(3), ai.getY(3), getColor(ai));
//                         drawLineBetween(g, ai.getX(2), ai.getY(2), ai.getX(3), ai.getY(3), getColor(ai));
//                     }
//                 }
//             }
//         }
        
        // commands
        for( int i=0; cc != null && i<cc.getCommandNum(); ++i ) {
            Command c = cc.getCommand(i);
            if( c.getType().equals( CommandType.MO ) ||
                    c.getType().equals( CommandType.SA ) ||
                    c.getType().equals( CommandType.SD ) ||
                    c.getType().equals( CommandType.CO ) ) {
                // draw command lines
                Color arrowColor = c.getType().color;
                // System.out.println("MOSASD "+c.getParam(0)+" "+c.getParam(1));
                for( int pi=0; pi < c.getParamNum()-1; ++pi ) {
                    if( !c.getType().equals( CommandType.CO ) && pi > 0) {
                        // only convoy command has more than 2 area parameters
                        break;
                    }
                    if( c.getIntParam(pi+1) == 0 ) {
                        break;
                    }
                    AreaInformation ai1 = adb.getAreaInformation(c.getIntParam(pi));
                    AreaInformation ai2 = adb.getAreaInformation(c.getIntParam(pi+1));
                    // System.out.println("X1:"+ai1.getX(1)+" Y1:"+ai1.getY(1));
                    // System.out.println("X2:"+ai2.getX(1)+" Y2:"+ai2.getY(1));
                    int ai1x = ai1.getX(1);
                    int ai1y = ai1.getY(1);
                    int ai2x = ai2.getX(1);
                    int ai2y = ai2.getY(1);
                    // change the values if the other end is too far away.
                    if( Math.abs(ai2x - ai1x ) > adb.getXSize()/2 ) {
                        if( ai1x < ai2x ) {
                            ai1x = ai1x + adb.getXSize();
                        } else {
                            ai2x = ai2x + adb.getXSize();
                        }
                    }
                    if( Math.abs(ai2y - ai1y ) > adb.getYSize()/2 ) {
                        if( ai1y < ai2y ) {
                            ai1y += adb.getYSize();
                        } else {
                            ai2y += adb.getYSize();
                        }
                    }
                    double jj1 = ai1y - (ai1x % 2)*0.5;
                    int x1 = (int)(topx+ai1x*xdiff);
                    int y1 = (int)(topy+jj1*ydiff);
                    double jj2 = ai2y - (ai2x % 2)*0.5;
                    int x2 = (int)(topx+ai2x*xdiff);
                    int y2 = (int)(topy+jj2*ydiff);
                    // System.out.println("x1:"+x1+" y1:"+y1+" x2:"+x2+" y2:"+y2);
                    g.setColor(arrowColor);
                    drawArrowLine(g,x1,y1,x2,y2);
                    drawArrowLine(g,(int)(x1+adb.getXSize()*xdiff),y1,(int)(x2+adb.getXSize()*xdiff),y2);
                    drawArrowLine(g,x1,(int)(y1+adb.getYSize()*ydiff),x2,(int)(y2+adb.getYSize()*ydiff));
                    drawArrowLine(g,(int)(x1+adb.getXSize()*xdiff),(int)(y1+adb.getYSize()*ydiff),(int)(x2+adb.getXSize()*xdiff),(int)(y2+adb.getYSize()*ydiff));
                }
            } else if( c.getType() == CommandType.AA ||
                    c.getType() == CommandType.AC ||
                    c.getType() == CommandType.AF ||
                    c.getType() == CommandType.AS ) {
                int id = c.getIntParam(0);
                AreaInformation cai = adb.getAreaInformation(id);
                int unit=0;
                if(  c.getType().equals(CommandType.AA ) ) {
                    unit = Unit.ARMY.getId();
                } else if(  c.getType().equals(CommandType.AC ) ) {
                    unit = Unit.CORPS.getId();
                } else if(  c.getType().equals(CommandType.AF ) ) {
                    unit = Unit.FLEET.getId();
                } else if(  c.getType().equals(CommandType.AS ) ) {
                    unit = Unit.SQUANDRON.getId();
                }
                int x1 = cai.getX(1);
                int y1 = cai.getY(1);
                if( !drawNewUnitHere(cai, x1, y1) ) {
                    x1 = cai.getX(2);
                    y1 = cai.getY(2);
                }
                drawUnit(g, x1, y1, unit, getColor(cai.getOwner()), c.getType().color);
                drawUnit(g, x1+adb.getXSize(), y1, unit, getColor(cai.getOwner()), c.getType().color);
                drawUnit(g, x1, y1+adb.getYSize(), unit, getColor(cai.getOwner()), c.getType().color);
                drawUnit(g, x1+adb.getXSize(), y1+adb.getYSize(), unit, getColor(cai.getOwner()), c.getType().color);
                
            } else if( c.getType().equals(CommandType.UU) ||
                    c.getType().equals(CommandType.DU) ||
                    c.getType().equals(CommandType.RU) ) {
                int id = c.getIntParam(0);
                AreaInformation cai = adb.getAreaInformation(id);
                int unit=0;
                if( c.getType().equals(CommandType.UU) ) {
                    if( cai.getUnitType() == Unit.CORPS.getId() ) {
                        unit = Unit.ARMY.getId();
                    } else if( cai.getUnitType() == Unit.SQUANDRON.getId() ) {
                        unit = Unit.FLEET.getId();
                    }
                } else if( c.getType().equals(CommandType.DU) ||
                        c.getType().equals(CommandType.RU) ) {
                    unit = cai.getUnitType();
                }
                Color bColor = borderColor;
                if( c.getType().equals(CommandType.RU) ) {
                    bColor = c.getType().color;
                }
                int x1 = cai.getX(1);
                int y1 = cai.getY(1);
                if( !drawUnitHere(cai, x1, y1) ) {
                    x1 = cai.getX(2);
                    y1 = cai.getY(2);
                }
                drawUnit(g, x1, y1, unit, getColor(cai.getOwner()), bColor, c.getType().color);
                drawUnit(g, x1+adb.getXSize(), y1, unit, getColor(cai.getOwner()), bColor, c.getType().color);
                drawUnit(g, x1, y1+adb.getYSize(), unit, getColor(cai.getOwner()), bColor, c.getType().color);
                drawUnit(g, x1+adb.getXSize(), y1+adb.getYSize(), unit, getColor(cai.getOwner()), bColor, c.getType().color);
            } else if( c.getType().equals(CommandType.SP) ) {
                System.out.println("paint spies "+c.getParamNum());
                for( int si=0; si<c.getParamNum(); ++si ) {
                    AreaInformation sai = adb.getAreaInformation(c.getIntParam(si));
                    if( sai != null ) {
                        drawSpy( g, sai );
                    } else if( c.getIntParam(si) != 0 ) {
                        int x = shift( (c.getIntParam(si) - 10000) / 100, adb.getXSize(), -aodm.getGame().getShiftX());
                        int y = shift( c.getIntParam(si) % 100, adb.getYSize(), -aodm.getGame().getShiftY());
                        drawSpyOne( g, x, y);
                    }
                }
            }
        }
    }
    
    public int shift(int raw, int size, int shift) {
        int ret = raw + shift;
        if( ret < 1 ) {
            ret += size;
        }
        if( ret > size ) {
            ret -= size;
        }
        return ret;
    }
    
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2) {
        //        g.drawLine(x1,y1,x2,y2);
        //        g.fillOval((int)(x2-size/8),(int)(y2-size/8), (int)(size/4), (int)(size/4));
        DrawUtil.drawArrow(g, x1, y1, x2, y2);
    }
    
    private boolean drawUnitHere(AreaInformation ai, int reali, int realj) {
        return ai != null && ai.getUnitType() != 0 &&
                ((ai.getX(1) == reali && ai.getY(1) == realj && ai.getX(2)==0) ||
                (ai.getX(2) == reali && ai.getY(2) == realj));
    }
    
    private boolean drawNewUnitHere(AreaInformation ai, int reali, int realj) {
        if( cc == null ) {
            // no command collection
            return false;
        }
        Command c = cc.getCommand(ai);
        return c != null &&
                ( c.getType() == CommandType.AA ||
                c.getType() == CommandType.AC ||
                c.getType() == CommandType.AF ||
                c.getType() == CommandType.AS ) &&
                ai != null &&
                ((ai.getX(1) == reali && ai.getY(1) == realj && ai.getX(2)==0) ||
                (ai.getX(2) == reali && ai.getY(2) == realj));
    }
    
    private void drawLineBetween(Graphics g, int x1, int y1, int x2, int y2, Color c) {
        drawLineBetween(g, x1, y1, x2, y2, c, 1);
    }
    
    private void drawLineBetween(Graphics g, int x1, int y1, int x2, int y2, Color c, int thickness) {
        int line = getAdjLine(x1, y1, x2, y2);
        double jj = y1 - (x1 % 2)*0.5;
        
        if( line != 0 ) {
            g.setColor(c);
            for( int i=0;i<2; ++i ) {
                for( int j=0; j<2; ++j ) {
                    double newtopx = topx + i*adb.getXSize()*xdiff;
                    double newtopy = topy + j*adb.getYSize()*ydiff;
                    int lx1 = (int)(newtopx+xhex[line-1]+x1*xdiff);
                    int ly1 = (int)(newtopy+yhex[line-1]+jj*ydiff);
                    int lx2 = (int)(newtopx+xhex[line % 6]+x1*xdiff);
                    int ly2 = (int)(newtopy+yhex[line % 6]+jj*ydiff);
                    
                    if( thickness == 1 ) {
                        g.drawLine(lx1,ly1,lx2, ly2);
                    } else {
                        DrawUtil.drawThickLine(g, lx1, ly1, lx2, ly2,
                                thickness, g.getColor());
                    }
                }
            }
        }
        
    }
    
    private void drawUnit(Graphics g, int i, int j, int unitId, Color c, Color bc) {
        drawUnit(g, i, j, unitId, c, bc, bc);
    }
    
    
    private void drawUnit(Graphics g, int i, int j, int unitId, Color c, Color bc, Color bc2) {
        double jj = j - (i % 2)*0.5;
        int x1 = (int)(topx-size*cos30+i*xdiff);
        int y1 = (int)(topy+jj*ydiff);
        
        Unit unit = Unit.getUnit(unitId);
        unit.draw(g, x1, y1, size, (int)(3.0/4*size), c, bc, bc2);
    }
    
    private void drawSpy(Graphics g, Area a) {
        drawSpyOne(g, a.getX(1), a.getY(1) );
        if( a.getX(2) != 0 ) {
            drawSpyOne(g, a.getX(2), a.getY(2) );
        }
        if( a.getX(3) != 0 ) {
            drawSpyOne(g, a.getX(3), a.getY(3) );
        }
    }
    
    private void drawSpyOne(Graphics g, int i, int j) {
        drawSpyOneReal(g,i,j);
        drawSpyOneReal(g,i+adb.getXSize(),j);
        drawSpyOneReal(g,i,j+adb.getYSize());
        drawSpyOneReal(g,i+adb.getXSize(),j+adb.getYSize());
    }
    
    
    private void drawSpyOneReal(Graphics g, int i, int j) {
        double jj = j - (i % 2)*0.5;
        int x1 = (int)(topx-2*size*cos30+i*xdiff);
        int y1 = (int)(topy+jj*ydiff-0.5*ydiff);
        
        g.drawImage(spyImage, x1, y1, null);
    }
    
    private void drawNumber(Graphics g, int i, int j, String text) {
        double jj = j - (i % 2)*0.5;
        int x = (int)(topx-size*cos30+i*xdiff);
        int y = (int)(topy+jj*ydiff-size*sin30/2);
        g.setFont(new Font("Dialog", Font.PLAIN, (int)(size/2)));
        g.drawString(text, x, y);
    }
    
    /**
     * Draws small size of supply points.
     */
    private void drawSmallSupplyPoints(Graphics g, int i, int j, int num) {
        double jj = j - (i % 2)*0.5;
        int x = (int)(topx-0.75*size*cos30+i*xdiff);
        int y = (int)(topy+jj*ydiff-3.0/8*size);
        i = (i-1) % adb.getXSize() + 1;
        j = (j-1) % adb.getYSize() + 1;
        for( int s=0; s<num; ++s ) {
            g.drawRect(x, y, (int)(smallSupplySize), (int)(smallSupplySize));
            g.drawLine((int)(x + smallSupplySize/2), (int)(y+smallSupplySize/4), (int)(x+smallSupplySize/2), (int)(y+3*smallSupplySize/4));
            g.drawLine((int)(x + smallSupplySize/4), (int)(y+smallSupplySize/2), (int)(x+3*smallSupplySize/4), (int)(y+smallSupplySize/2));
            x += 1.2 * smallSupplySize;
        }
    }
    
    /**
     * Draws normal size of supply points.
     */
    private void drawSupplyPoints(Graphics g, int i, int j, int num) {
        double jj = j - (i % 2)*0.5;
        int x = (int)(topx-size*cos30+i*xdiff);
        int y = (int)(topy+jj*ydiff);
        i = (i-1) % adb.getXSize() + 1;
        j = (j-1) % adb.getYSize() + 1;
        for( int s=0; s<num; ++s ) {
            g.drawRect(x, y, (int)(supplySize), (int)(supplySize));
            DrawUtil.drawThickLine(g,(int)(x + supplySize/2), (int)(y+supplySize/4), (int)(x+supplySize/2), (int)(y+3*supplySize/4), 1, g.getColor());
            DrawUtil.drawThickLine(g,(int)(x + supplySize/4), (int)(y+supplySize/2), (int)(x+3*supplySize/4), (int)(y+supplySize/2), 1, g.getColor());
            x += 1.2 * supplySize;
        }
    }
    
    /**
     * Draws the capital image.
     */
    private void drawCapital(Graphics g, int i, int j, Color c, boolean normalSize) {
        Color oldColor = g.getColor();
        double jj = j - (i % 2)*0.5;
        int x1;
        int y1;
        double ratio = 1;
        if( normalSize ) {
            x1 = (int)(topx-size*cos30+i*xdiff);
            y1 = (int)(topy+jj*ydiff);
        } else {
            x1 = (int)(topx-0.75*size*cos30+i*xdiff);
            y1 = (int)(topy+jj*ydiff-3.0/8*size);
            ratio = 2.0/3;
        }
        
        g.setColor(c);
        g.fillRect( x1, y1, (int)(size*ratio), (int)(3.0/4*size*ratio) );
        g.drawImage(capitalImage, x1, y1, (int)(size*ratio), (int)(3.0/4*size*ratio), null);
        g.setColor(oldColor);
    }
    
    
//     private void drawArea(Graphics g, AreaInformation a, Color c, int thickness) {
//         int x,y;
//         for( int i=1; i<= a.getSize(); ++i ) {
//             x = a.getX(i);
//             y = a.getY(i);
//             drawLineBetween(g, x, y, x+1, y+1, c, thickness);
//             drawLineBetween(g, x, y, x+1, y, c, thickness);
//             drawLineBetween(g, x, y, x+1, y-1, c, thickness);
//             drawLineBetween(g, x, y, x, y+1, c, thickness);
//             drawLineBetween(g, x, y, x, y, c, thickness);
//             drawLineBetween(g, x, y, x, y-1, c, thickness);
//             drawLineBetween(g, x, y, x-1, y+1, c, thickness);
//             drawLineBetween(g, x, y, x-1, y, c, thickness);
//             drawLineBetween(g, x, y, x-1, y-1, c, thickness);
//         }
//         if( a.getSize() > 1 ) {
//             drawLineBetween(g, a.getX(1), a.getY(1), a.getX(2), a.getY(2), getColor(a), 2);
//         };
//         if( a.getSize() > 2 ) {
//             drawLineBetween(g, a.getX(2), a.getY(2), a.getX(3), a.getY(3), getColor(a), 2);
//             drawLineBetween(g, a.getX(1), a.getY(1), a.getX(3), a.getY(3), getColor(a), 2);
//         }
//     }
    
    private Color getColor(AreaInformation ai) {
        if( ai == null ) {
            return unknownColor;
        } else if( ai.getAreaType() == Area.AREA_TYPE_LAND
                || ai.getAreaType() == Area.AREA_TYPE_COSTAL) {
            if( ai.getOwner() == -1 ) {
                return unknownColor;
            }
            return getColor(ai.getOwner());
        } else if( ai.getAreaType() == Area.AREA_TYPE_SEA ) {
            return seaColor;
        }
        return unknownColor;
    }
    
    private Color getColor(int playerNum) {
        //return playerColors[playerNum % playerColors.length];
        return Colors.getInstance(aodm.getGame()).getPlayerColor(playerNum);
    }
    
    private int getAdjLine(int x1, int y1, int x2, int y2) {
        int ydiff = y2-y1;
        if( ydiff == adb.getYSize()-1 ) {
            ydiff = -1;
        } else if( ydiff == -(adb.getYSize()-1) ) {
            ydiff = 1;
        }
        
        int xdiff = (x2-x1);
        if( xdiff == adb.getXSize()-1 ) {
            xdiff = -1;
        } else if( xdiff == -(adb.getXSize() -1) ) {
            xdiff = 1;
        }
        
        if( Math.abs(ydiff) > 1 || Math.abs(xdiff) > 1) {
            return 0;
        }
        
        if( ydiff == 0 ) { // same row
            if( xdiff == 1 ) {
                return (x1 % 2) + 1;
            } else if( xdiff == -1 ) {
                return  6 - ((x1 % 2) + 1);
            } else {
                return 0;
            }
        } else if( ydiff == 1 ) { // next row
            if( xdiff == 0 ) {
                return 3;
            } else if( xdiff == 1 ) { // next column
                return (x1 % 2 == 0 ? 2 : 0);
            } else { // prev column
                return (x1 % 2 == 0 ? 4 : 0);
            }
        } else if( ydiff == -1 ) { // prev row
            if( xdiff == 0 ) { // same column
                return 6;
            } else if( xdiff == 1 ) { // next column
                return (x1 % 2 != 0 ? 1 : 0);
            } else { // prev column
                return (x1 % 2 != 0 ? 5 : 0);
            }
        }
        return 0;
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
    public void mouseClicked(java.awt.event.MouseEvent evt) {
    }
    
    public void mousePressed(java.awt.event.MouseEvent evt) {
        if( (evt.getModifiers() & evt.BUTTON2_MASK) != 0 ) {
            dragStartX = evt.getX();
            dragStartY = evt.getY();
        } else if( (evt.getModifiers() & evt.BUTTON1_MASK) != 0 ) {
            Dimension d = getAreaPlace(evt.getX(), evt.getY());
            AreaInformation ai = adb.getAreaInformation(adb.getId(d.width,d.height));
            if( ai != null ) {
                aodm.setNextCommandParameter(ai.getId());
            } else {
                // no area id, we give the exact location
                // shifted
                int w = shift(d.width,  adb.getXSize(), aodm.getGame().getShiftX());
                int h = shift(d.height, adb.getYSize(), aodm.getGame().getShiftY());
                aodm.setNextCommandParameter(10000+w*100+h);
            }
        }
    }
    
    public void mouseReleased(java.awt.event.MouseEvent evt) {
    }
    
    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }
    
    public void mouseExited(java.awt.event.MouseEvent evt) {
    }
    
    public void mouseDragged(java.awt.event.MouseEvent evt) {
        if( (evt.getModifiers() & evt.BUTTON2_MASK) != 0 ) {
            offX -= evt.getX() - dragStartX;
            offY -= evt.getY() - dragStartY;
            dragStartX = evt.getX();
            dragStartY = evt.getY();
            if( offX > 2*size+adb.getXSize()*xdiff ) {
                offX -= adb.getXSize()*xdiff;
            } else if( offX < 2*size ) {
                offX += adb.getXSize()*xdiff;
            }
            if( offY > 2*size+adb.getYSize()*ydiff ) {
                offY -= adb.getYSize()*ydiff;
            } else if( offY < 2*size ) {
                offY += adb.getYSize()*ydiff;
            }
            repaint();
        }
    }
    
    public void mouseMoved(java.awt.event.MouseEvent evt) {
        updateStatusLabel(getAreaPlace(evt.getX(), evt.getY()));
    }
    
    public Dimension getAreaPlace(int x, int y) {
        int xx = x+offX;
        int yy = y+offY;
        int minxx=0;
        int minyy=0;
        
        if( adb.getXSize()*xdiff > 0 ) {
            xx %= (int)(adb.getXSize()*xdiff);
            yy %= (int)(adb.getYSize()*ydiff);
            int xxp = (int)(xx /xdiff);
            int yyp = (int)(yy /ydiff);
            
            // the area is somowhere close to xi, yi
            int mindiff=Integer.MAX_VALUE;
            for( int xi=xxp-2; xi<xxp+3; ++xi ) {
                for( int yi=yyp-2; yi<yyp+3; ++yi ) {
                    int xim = xi;
                    if( xim < 1 ) {
                        xim += adb.getXSize();
                    } else if( xim > adb.getXSize() ) {
                        xim -= adb.getXSize();
                    }
                    int yim = yi;
                    if( yim < 1 ) {
                        yim += adb.getYSize();
                    } else if( yim > adb.getYSize() ) {
                        yim -= adb.getYSize();
                    }
                    int xpos = (int)(topx + xi * xdiff);
                    int ypos = (int)(topy + (yi - 0.5*(xi % 2))* ydiff);
                    int diff = (xpos-xx)*(xpos-xx)+(ypos-yy)*(ypos-yy);
                    if( diff < mindiff ) {
                        mindiff = diff;
                        minxx = xim;
                        minyy = yim;
                    }
                }
            }
        }
        return new Dimension(minxx,minyy);
    }
    
    public void updateStatusLabel(Dimension d) {
        if( adb.getXSize() != 0 ) {
            AreaInformation ai = adb.getAreaInformation(adb.getId(d.width,d.height));
            if( ai != null ) {
                String statusStr = "Area ["+d.width+","+d.height+"] = "+ai.getId()+" Player: ";
                if( ai.getOwner() == -1 ) {
                    statusStr += "Unknown";
                } else {
                    statusStr += aodm.getGame().getPlayer(ai.getOwner()).getName();
                }
                if( ai.getUnitType() != 0 ) {
                    statusStr += " Unit: "+aodm.getGame().getPlayer(ai.getUnitOwner()).getName()+" "+Unit.getUnit(ai.getUnitType()).getName();
                }
                if( ai.getSupplyPointNum() > 0 ) {
                    statusStr += " Supply Points: "+ai.getSupplyPointNum();
                }
                if( cc != null ) {
                    Command c = cc.getCommand(ai);
                    if( c != null ) {
                        System.out.println("c:"+c);
                        statusStr += " ["+c.toHumanReadableString()+"]";
                    }
                }
                getAodm().setStatusLabel(statusStr);
                return;
            }
            getAodm().setStatusLabel("Area ["+d.width+","+d.height+"]");
        }
    }
    
    /**
     * Changes the offset, so the area will be somewhere int the middle
     */
    public void center(int x, int y) {
        int wxsize = (int)getSize().width;
        int wysize = (int)getSize().height;
        
        offX = (int)((x-1)*xdiff - wxsize/2);
        offY = (int)((y-1)*ydiff - wysize/2);
        
        if( offX < 2*size ) {
            offX += adb.getXSize()*xdiff;
        }
        
        if( offY < 2*size ) {
            offY += adb.getYSize()*ydiff;
        }
        
        repaint();
    }
    
    public static final Color borderColor  = Color.black;
    public static final Color seaColor     = Color.cyan;
    public static final Color unknownColor = Color.lightGray;
    
//     public static final Color playerColors[] = {
//         Color.white, Color.red, Color.pink, Color.orange, Color.yellow,Color.magenta,
//     new Color(150,150, 255), new Color(255, 100, 100), new Color(100,255,100), Color.green, new Color(240,240,100), new Color(70,70, 170), new Color(170,70,70) };
    
    
    public static final double sin30 = Math.sin(30.0/180*2*Math.PI);
    public static final double cos30 = 0.5;
    public static final int size = 32;
    public static final double supplySize = size/2;
    public static final double smallSupplySize = size/3;
    public static final double xdiff = (1+cos30)*size;
    public static final double ydiff = 2*sin30*size;
    
    public static final double xhex[] = {
        size*cos30,size,size*cos30,-size*cos30,-size,-size*cos30};
        
        public static final double yhex[] = {
            -size*sin30,0,+size*sin30,size*sin30,0,-size*sin30};
            
            
            public static final int topx=-size;
            public static final int topy=-size;
            
            public static final boolean numberDraw = true;
            public static final boolean supplyDraw = true;
            
            private boolean  offScreen = false;
            private Image    imageBuffer;
            private Graphics graphicsBuffer;
            public  boolean  needRepaint = true;
            
            private int dragStartX;
            private int dragStartY;
            private int offX = size;
            private int offY = size;
            
            protected Image spyImage = Toolkit.getDefaultToolkit().getImage(
                    this.getClass().getResource( "/images/spy.png" ));
            
} // HexMap

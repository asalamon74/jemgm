package jemgm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import jemgm.AbstractBattleFieldPanel.LayerType;

/**
 *
 * @author salamon
 */
public class HexBattleFieldPanel extends AbstractBattleFieldPanel {
    
    /** Creates a new instance of HexBattleFieldPanel */
    public HexBattleFieldPanel(Manager aodm) {        
        super(aodm);
        // init layers
        FrontLineLayerPanel frontLineLayerPanel = new FrontLineLayerPanel();
        frontLineLayerPanel.setOpaque(false);
        add(frontLineLayerPanel, new Integer(4));
        layers.put( LayerType.LAYER_FRONTLINE, frontLineLayerPanel);
                
        SupplyLayerPanel supplyLayerPanel = new SupplyLayerPanel();
        supplyLayerPanel.setOpaque(false);
        add(supplyLayerPanel, new Integer(3));
        layers.put( LayerType.LAYER_SUPPLY, supplyLayerPanel);                
        
        NewAreaLayerPanel newAreaLayerPanel = new NewAreaLayerPanel();
        newAreaLayerPanel.setOpaque(false);
        add(newAreaLayerPanel, new Integer(2));
        layers.put( LayerType.LAYER_NEWAREA, newAreaLayerPanel);                
        
        CommandLayerPanel commandLayerPanel = new CommandLayerPanel();                
        commandLayerPanel.setOpaque(false);
        add(commandLayerPanel, new Integer(1));
        layers.put( LayerType.LAYER_COMMAND, commandLayerPanel);       

        AreaLayerPanel areaLayerPanel = new AreaLayerPanel();
        areaLayerPanel.setOpaque(false);
        add(areaLayerPanel, new Integer(0));
        layers.put( LayerType.LAYER_AREA, areaLayerPanel);              
        
    }

    public void init() {
        System.out.println("init");
        int xPixels = (int)(2*getAdb().getXSize()*xdiff);
        int yPixels = (int)(getAdb().getYSize()*ydiff);
        yLoop = 1;
        if( getAodm().getGame().getGameType().wordWrapY() ) {
            yPixels *= 2;
            yLoop = 2;
        }
        //System.out.println("xp:"+xPixels+" yp:"+yPixels);
        init( xPixels, yPixels);
        //setPreferredSize(new Dimension(xPixels, yPixels));
        //setSize( xPixels, yPixels);        
    }

    
     public Dimension getAreaPlace(int x, int y) {
         int xx = x+xOffset;
         int yy = y+yOffset;
         
         int minxx=0;
         int minyy=0;
         
         if( getAdb().getXSize()*xdiff > 0 ) {
             xx %= (int)(getAdb().getXSize()*xdiff);
             yy %= (int)(getAdb().getYSize()*ydiff);
             int xxp = (int)(xx /xdiff);
             int yyp = (int)(yy /ydiff);
             
             // the area is somowhere close to xi, yi
             int mindiff=Integer.MAX_VALUE;
             for( int xi=xxp-2; xi<xxp+3; ++xi ) {
                 for( int yi=yyp-2; yi<yyp+3; ++yi ) {
                     int xim = xi;
                     if( xim < 1 ) {
                         xim += getAdb().getXSize();
                     } else if( xim > getAdb().getXSize() ) {
                         xim -= getAdb().getXSize();
                     }
                     int yim = yi;
                     if( yim < 1 ) {
                         yim += getAdb().getYSize();
                     } else if( yim > getAdb().getYSize() ) {
                         yim -= getAdb().getYSize();
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
       

    // precalculated constants
    public static final double sin30 = Math.sin(30.0/180*2*Math.PI);
    public static final double cos30 = 0.5;    
    private static final int size = 32;    
    
    private static final double supplySize = size/2;
    private static final double smallSupplySize = size/3;
    private static final double xdiff = (1+cos30)*size;
    private static final double ydiff = 2*sin30*size;
    
    private int yLoop;
    
    private static final double xhex[] = {size*cos30,size,size*cos30,-size*cos30,-size,-size*cos30};
    
    private static final double yhex[] = {-size*sin30,0,+size*sin30,size*sin30,0,-size*sin30};
    
    private static final int topx=-size;
    private static final int topy=-size;
    private static final boolean numberDraw = true;
    private static final boolean supplyDraw = true;
    
    public static final Color borderColor       = Color.black;
    public static final Color seaColor          = Color.cyan;
    public static final Color unknownColor      = Color.lightGray;
    public static final Color frontLineColor    = Color.red;
    public static final Color friendlyLineColor = Color.green;
    private final BasicStroke thickLineStroke = new BasicStroke(1);
    
    // common methods
    private boolean drawUnitHere(AreaInformation ai, int reali, int realj) {
        return ai != null && ai.getUnitType() != 0 &&
                ((ai.getX(1) == reali && ai.getY(1) == realj && ai.getX(2)==0) ||
                (ai.getX(2) == reali && ai.getY(2) == realj));
    }
    
    private boolean drawNewUnitHere(AreaInformation ai, int reali, int realj) {
        if( getCc() == null ) {
            // no command collection
            return false;
        }
        Command c = getCc().getCommand(ai);
        return c != null &&
                ( c.getType() == CommandType.AA ||
                c.getType() == CommandType.AC ||
                c.getType() == CommandType.AF ||
                c.getType() == CommandType.AS ) &&
                ai != null &&
                ((ai.getX(1) == reali && ai.getY(1) == realj && ai.getX(2)==0) ||
                (ai.getX(2) == reali && ai.getY(2) == realj));
    }

    /**
     * Draws a unit.
     */
    private void drawUnit(Graphics g, int i, int j, int unitId, Color c, Color bc) {
        drawUnit(g, i, j, unitId, c, bc, bc);
    }
    
    /**
     * Draws a unit.
     */
    private void drawUnit(Graphics g, int i, int j, int unitId, Color c, Color bc, Color bc2) {
        double jj = j - (i % 2)*0.5;
        
        int x1 = (int)(topx-size*cos30+i*xdiff);
        int y1 = (int)(topy+jj*ydiff);
        
        Unit unit = Unit.getUnit(unitId);
        unit.draw(g, x1, y1, size, (int)(3.0/4*size), c, bc, bc2);
        
        
    }
    
    private int getAdjLine(int x1, int y1, int x2, int y2) {
        int ydiff = y2-y1;
        if( ydiff == getAdb().getYSize()-1 ) {
            ydiff = -1;
        } else if( ydiff == -(getAdb().getYSize()-1) ) {
            ydiff = 1;
        }
        
        int xdiff = (x2-x1);
        if( xdiff == getAdb().getXSize()-1 ) {
            xdiff = -1;
        } else if( xdiff == -(getAdb().getXSize() -1) ) {
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
         
    private void drawLineBetween(Graphics2D g, int x1, int y1, int x2, int y2, Color c) {
        drawLineBetween(g, x1, y1, x2, y2, c, thickLineStroke);
    }
        
    private void drawLineBetween(Graphics2D g, int x1, int y1, int x2, int y2, Color c, Stroke stroke) {
        int line = getAdjLine(x1, y1, x2, y2);
        double jj = y1 - (x1 % 2)*0.5;
        
        double newtopx, newtopy;
        int lx1,lx2,ly1,ly2;
        if( line != 0 ) {
            Stroke oldStroke = g.getStroke();
            g.setColor(c);
            g.setStroke(stroke);
            for( int i=0;i<2; ++i ) {
                newtopx = topx + i*getAdb().getXSize()*xdiff;
                lx1 = (int)(newtopx+xhex[line-1]+x1*xdiff);
                lx2 = (int)(newtopx+xhex[line % 6]+x1*xdiff);
                for( int j=0; j<yLoop; ++j ) {
                    newtopy = topy + j*getAdb().getYSize()*ydiff;
                    ly1 = (int)(newtopy+yhex[line-1]+jj*ydiff);
                    ly2 = (int)(newtopy+yhex[line % 6]+jj*ydiff);
                    g.drawLine(lx1,ly1,lx2, ly2);
                }
            }
            g.setStroke(oldStroke);
        }
    }
    
    /**
     * Calculates the color for an area.
     */
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
        return Colors.getInstance(getAodm().getGame()).getPlayerColor(playerNum);
    }     
    
    /**
     * This layer contains the area in the battlefield, which is not changing,
     * when the user adds a new command.
     */
    public class AreaLayerPanel extends BufferedPanel {
        
                        
        /**
         * This is the real paint method.
         */
        public void realPaint(Graphics2D g) {
            
            System.out.println("area paint");
            if( xPixels == 0 ) {
                // not yet initialized
                return;
            }
            PlayersRelation plr = getAodm().getTurn(getAodm().getActTurnNumber()).getPr();
            
//            g.setColor(getBackground());
//            g.fillRect(0, 0, (int)(2*getAdb().getXSize()*xdiff), (int)(2*getAdb().getYSize()*ydiff));
//            g.setColor(borderColor);
            
            int ySize = getAdb().getYSize();
            if( getAodm().getGame() != null && getAodm().getGame().getGameType().wordWrapY() ) {
                ySize *= 2;
            }
            int reali, realj;
            int xpoints[] = new int[6];
            int ypoints[] = new int[6];
            int nri,nrj;
            
            // first loop the draw the hex poligons, and the colored hexes
            for( int i=1; i<=2*getAdb().getXSize(); ++i ) {
                reali = ((i-1) % getAdb().getXSize()) + 1;
                for( int xi = 0; xi<6; ++xi ) {
                    xpoints[xi] = (int)(topx + xhex[xi]+i*xdiff);
                }
                for( int j=1; j<=ySize; ++j ) {                    
                    realj = ((j-1) % getAdb().getYSize()) + 1;                    
                                        
                    double jj = j - (reali % 2)*0.5;
                    for( int yi = 0; yi<6; ++yi ) {
                        ypoints[yi] = (int)(topy + yhex[yi]+jj*ydiff);
                    }
                    AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(reali,realj));
                    if( ai != null ) {
                        g.setColor(getColor(ai));
                        g.fillPolygon(xpoints, ypoints, 6);
                        g.setColor(borderColor);
                        g.drawPolygon(xpoints, ypoints, 6);
                    }                    
                }
            }
            // main loop to draw the board
            for( int i=1; i<=2*getAdb().getXSize(); ++i ) {
                reali = ((i-1) % getAdb().getXSize()) + 1;
                for( int j=1; j<=ySize; ++j ) {                    
                    realj = ((j-1) % getAdb().getYSize()) + 1;                    
                                        
                    double jj = j - (reali % 2)*0.5;
                    AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(reali,realj));
                    if( ai != null ) {

                        /*g.setColor(borderColor);
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
                        }*/
                        if( drawUnitHere(ai, reali, realj) ) {
                            drawUnit(g, i, j, ai.getUnitType(), getColor(ai.getUnitOwner()), borderColor);
                        }
                        // delete the lines between the hexes, if they are in the same area.
                        for( int ni=-1; ni<=1; ++ni) {
                            for( int nj=-1; nj<=1; ++nj ) {
                                if( ni <= 0 && nj <= 0 ) {
                                    continue;
                                }
                                nri = ((reali+ni-1) % getAdb().getXSize())+1;
                                nrj = ((realj+nj-1) % getAdb().getYSize())+1;
                                if( getAdb().getId(nri, nrj) == ai.getId() ) {
                                    drawLineBetween(g, reali, realj, nri, nrj, getColor(ai));
                                }
                                AreaInformation nai = getAdb().getAreaInformation(getAdb().getId(nri, nrj));
                            }
                        }
                    }
                }
            }            
        }        
    }

    /**
     * Layer for suuply points, capital drawing.
     *
     * Not part of LAYER_AREA, because position/size of supply points depends
     * on commands.
     */
    public class SupplyLayerPanel extends BufferedPanel {

        private Image capitalImage =  Toolkit.getDefaultToolkit().getImage( "images/capital.png" );         
        
        /**
         * Draws the id number of an area.
         */
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
            i = (i-1) % getAdb().getXSize() + 1;
            j = (j-1) % getAdb().getYSize() + 1;
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
            i = (i-1) % getAdb().getXSize() + 1;
            j = (j-1) % getAdb().getYSize() + 1;
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
        
        public void realPaint(Graphics2D g) {
            System.out.println("supply draw");
            int ySize = getAdb().getYSize();
            if( getAodm().getGame() != null && getAodm().getGame().getGameType().wordWrapY() ) {
                ySize *= 2;
            }            
            int reali, realj;
            // main loop to draw the board
            for( int i=1; i<=2*getAdb().getXSize(); ++i ) {
                reali = ((i-1) % getAdb().getXSize()) + 1;
                for( int j=1; j<=ySize; ++j ) {                    
                    realj = ((j-1) % getAdb().getYSize()) + 1;                    
                                        
                    double jj = j - (reali % 2)*0.5;
                    AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(reali,realj));
                    if( ai != null ) {

                        g.setColor(borderColor);
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
                    }
                }
            }
        }
    }
    
    
    /**
     * Layer for friend and enemy fronline drawing.
     */
    public class FrontLineLayerPanel extends BufferedPanel {
        
        private float[] dash = {5};
        private BasicStroke frontLineStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, dash, 0);        
        
        public void realPaint(Graphics2D g) {
            System.out.println("frontLine paint");
            int reali, realj;
            int nri,nrj;
            
            System.out.println("atn:"+getAodm().getActTurnNumber());
            PlayersRelation plr = getAodm().getTurn(getAodm().getActTurnNumber()).getPr();
            
            int ySize = getAdb().getYSize();
            if( getAodm().getGame() != null && getAodm().getGame().getGameType().wordWrapY() ) {
                ySize *= 2;
            }            
            for( int i=1; i<=2*getAdb().getXSize(); ++i ) {
                reali = ((i-1) % getAdb().getXSize()) + 1;
                for( int j=1; j<=ySize; ++j ) {
                    realj = ((j-1) % getAdb().getYSize()) + 1;
                    
                    double jj = j - (reali % 2)*0.5;
                    AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(reali,realj));                    
                    for( int ni=-1; ni<=1; ++ni) {
                        for( int nj=-1; nj<=1; ++nj ) {
                            if( ni <= 0 && nj <= 0 ) {
                                continue;
                            }
                            nri = ((reali+ni-1) % getAdb().getXSize())+1;
                            nrj = ((realj+nj-1) % getAdb().getYSize())+1;
                            AreaInformation nai = getAdb().getAreaInformation(getAdb().getId(nri, nrj));
                            if(  nai != null && ai.getOwner() >= 1 && nai.getOwner() >= 1 &&
                                        plr.getSimpleRelation(ai.getOwner(), nai.getOwner()) == PlayersRelation.RelationType.WAR) {
                                    drawLineBetween(g, reali, realj, nri, nrj, frontLineColor, frontLineStroke);
                            }
                            if( nai != null && ai.getOwner() >= 1 && nai.getOwner() >= 1 &&
                                    plr.getSimpleRelation(ai.getOwner(), nai.getOwner()) == PlayersRelation.RelationType.ALLY) {
                                drawLineBetween(g, reali, realj, nri, nrj, friendlyLineColor, frontLineStroke);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * New area layer drawing.
     */
    public class NewAreaLayerPanel extends BufferedPanel {
        
        protected Image newAreaImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource( "/images/newarea.png" ));        
        
        public void realPaint(Graphics2D g) {
            int ySize = getAdb().getYSize();
            if( getAodm().getGame() != null && getAodm().getGame().getGameType().wordWrapY() ) {
                ySize *= 2;
            }
            // main loop to draw the board
            for( int i=1; i<=2*getAdb().getXSize(); ++i ) {
                for( int j=1; j<=ySize; ++j ) {
                    int reali = ((i-1) % getAdb().getXSize()) + 1;
                    int realj = ((j-1) % getAdb().getYSize()) + 1;
                    double jj = j - (reali % 2)*0.5;
                    AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(reali,realj));
                    if( ai != null ) {
                        if( ai.getAreaType() != Area.AREA_TYPE_SEA &&
                                ai.getOwner() != ai.getPrevOwner()  &&
                                ai.getOwner() != 0 ) {
//                        System.out.println("new area: "+ai.getId());
                            int x1 = (int)(topx-2*size*cos30+i*xdiff);
                            int y1 = (int)(topy+jj*ydiff-0.5*ydiff);
                            
                            g.drawImage(newAreaImage, x1, y1, null);
                        }
                    }                    
                }
            }
        }
    }
    
    /**
     * Command layer drawing
     */
    public class CommandLayerPanel extends BufferedPanel {
        
        private Image spyImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource( "/images/spy.png" ));        
        
        /**
         * Draws an arrow. Used for drawing MO,SA,SD,... commands.
         */
        private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2) {
            DrawUtil.drawArrow(g, x1, y1, x2, y2);
        }
                
        /**
         * Draw the spied hexes of an area.
         */
        private void drawSpy(Graphics g, Area a) {
            //System.out.println("drawSpy:"+a.getId()+" size: "+a.getSize());
            
            for( int i=1; i<=a.getSize(); ++i) {
                drawSpyOne(g, a.getX(i), a.getY(i) );
            }
        }
        
        /**
         * Draws one spied hex. Draws more than one times because of the looping.
         */
        private void drawSpyOne(Graphics g, int i, int j) {
            //System.out.printf("drawSpyOne: %d, %d\n", i, j);
            drawSpyOneReal(g,i,j);
            drawSpyOneReal(g,i+getAdb().getXSize(),j);
            drawSpyOneReal(g,i,j+getAdb().getYSize());
            drawSpyOneReal(g,i+getAdb().getXSize(),j+getAdb().getYSize());
        }
        
        /**
         * Draws one spied hex.
         */
        private void drawSpyOneReal(Graphics g, int i, int j) {
            double jj = j - (i % 2)*0.5;
            int x1 = (int)(topx-2*size*cos30+i*xdiff);
            int y1 = (int)(topy+jj*ydiff-0.5*ydiff);
            
            g.drawImage(spyImage, x1, y1, null);
        }
        
        public void realPaint(Graphics2D g) {
            System.out.println("command paint");
            //g.setColor(Color.BLUE);
            //g.drawRect(0,0, 100, 100);
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
                        AreaInformation ai1 = getAdb().getAreaInformation(c.getIntParam(pi));
                        AreaInformation ai2 = getAdb().getAreaInformation(c.getIntParam(pi+1));
                        // System.out.println("X1:"+ai1.getX(1)+" Y1:"+ai1.getY(1));
                        // System.out.println("X2:"+ai2.getX(1)+" Y2:"+ai2.getY(1));
                        int ai1x = ai1.getX(1);
                        int ai1y = ai1.getY(1);
                        int ai2x = ai2.getX(1);
                        int ai2y = ai2.getY(1);
                        // change the values if the other end is too far away.
                        if( Math.abs(ai2x - ai1x ) > getAdb().getXSize()/2 ) {
                            if( ai1x < ai2x ) {
                                ai1x = ai1x + getAdb().getXSize();
                            } else {
                                ai2x = ai2x + getAdb().getXSize();
                            }
                        }
                        if( Math.abs(ai2y - ai1y ) > getAdb().getYSize()/2 ) {
                            if( ai1y < ai2y ) {
                                ai1y += getAdb().getYSize();
                            } else {
                                ai2y += getAdb().getYSize();
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
                        drawArrowLine(g,(int)(x1+getAdb().getXSize()*xdiff),y1,(int)(x2+getAdb().getXSize()*xdiff),y2);
                        drawArrowLine(g,x1,(int)(y1+getAdb().getYSize()*ydiff),x2,(int)(y2+getAdb().getYSize()*ydiff));
                        drawArrowLine(g,(int)(x1+getAdb().getXSize()*xdiff),(int)(y1+getAdb().getYSize()*ydiff),(int)(x2+getAdb().getXSize()*xdiff),(int)(y2+getAdb().getYSize()*ydiff));
                    }
                } else if( c.getType() == CommandType.AA ||
                        c.getType() == CommandType.AC ||
                        c.getType() == CommandType.AF ||
                        c.getType() == CommandType.AS ) {
                    int id = c.getIntParam(0);
                    AreaInformation cai = getAdb().getAreaInformation(id);
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
                    drawUnit(g, x1+getAdb().getXSize(), y1, unit, getColor(cai.getOwner()), c.getType().color);
                    drawUnit(g, x1, y1+getAdb().getYSize(), unit, getColor(cai.getOwner()), c.getType().color);
                    drawUnit(g, x1+getAdb().getXSize(), y1+getAdb().getYSize(), unit, getColor(cai.getOwner()), c.getType().color);
                    
                } else if( c.getType().equals(CommandType.UU) ||
                        c.getType().equals(CommandType.DU) ||
                        c.getType().equals(CommandType.RU) ) {
                    int id = c.getIntParam(0);
                    AreaInformation cai = getAdb().getAreaInformation(id);
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
                    drawUnit(g, x1+getAdb().getXSize(), y1, unit, getColor(cai.getOwner()), bColor, c.getType().color);
                    drawUnit(g, x1, y1+getAdb().getYSize(), unit, getColor(cai.getOwner()), bColor, c.getType().color);
                    drawUnit(g, x1+getAdb().getXSize(), y1+getAdb().getYSize(), unit, getColor(cai.getOwner()), bColor, c.getType().color);
                } else if( c.getType().equals(CommandType.SP) ) {
                    //System.out.println("paint spies "+c.getParamNum());
                    for( int si=0; si<c.getParamNum(); ++si ) {
                        AreaInformation sai = getAdb().getAreaInformation(c.getIntParam(si));
                        if( sai != null ) {
                            drawSpy( g, sai );
                        } else if( c.getIntParam(si) != 0 ) {
                            int x = shift( (c.getIntParam(si) - 10000) / 100, getAdb().getXSize(), -getAodm().getGame().getShiftX());
                            int y = shift( c.getIntParam(si) % 100, getAdb().getYSize(), -getAodm().getGame().getShiftY());
                            drawSpyOne( g, x, y);
                        }
                    }
                }
            }
        }
    }
}

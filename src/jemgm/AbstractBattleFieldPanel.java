package jemgm;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * Abstract class for battle field panel.
 *
 * Supports:<br>
 * <ul>
 * <li>Scroll with mouse drag</li>
 * <li>Predefined layers</li>
 * <li>Offscreen buffer</li>
 * </ul>
 * @author salamon
 */
public abstract class AbstractBattleFieldPanel extends JLayeredPane implements MouseListener, MouseMotionListener{

    public enum LayerType {LAYER_AREA, LAYER_COMMAND, LAYER_FRONTLINE, LAYER_NEWAREA, LAYER_SUPPLY};
    
    protected HashMap<LayerType, BufferedPanel> layers;
    private boolean needRepaint;
    private boolean initted = false;
    private int xDragStart, yDragStart;
    private int xPixels, yPixels;
    protected int xOffset, yOffset;
    private Image imageBuffer;
    private Graphics2D graphicsBuffer;
    
    
    /** Creates a new instance of AbstractBattleFieldPanel */
    public AbstractBattleFieldPanel(Manager aodm) {
        this.aodm = aodm;
        layers = new HashMap<LayerType, BufferedPanel>();
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void paint(Graphics g) {
        System.out.println("abstract paint");
        if( !initted && getAdb().getYSize() != 0 ) {
            System.out.println("ready to init");
            init();
        }
        if( initted ) {
            if( needRepaint ) {
                System.out.println("super paint");                        
                super.paint(graphicsBuffer);
                needRepaint = false;
            }
            System.out.println("drawImage x,y");
            g.drawImage(imageBuffer, -xOffset, -yOffset, this);            
        }
    }
    
    public void setNeedRepaint( boolean needRepaint) {
        if( needRepaint ) {
            this.needRepaint = true;
        }
        for (LayerType layerType : LayerType.values()) {
            setNeedRepaint( layerType, needRepaint);
        }
    }
    
    public void setNeedRepaint(LayerType layerType, boolean needRepaint) {
        if( needRepaint ) {
            this.needRepaint = true;
        }
        //needRepaints.put(layerType, Boolean.valueOf(needRepaint));
        if( layers.get(layerType) != null ) {
            layers.get(layerType).setNeedRepaint( needRepaint );
        }
    }
    
    /*public boolean isNeedRepaint(LayerType layerType) {
        return needRepaints.get(layerType).booleanValue();
    }*/
    
    public void setVisible(LayerType layerType, boolean visible) {
        //visibles.put(layerType, Boolean.valueOf(visible));
        if( layers.get(layerType) != null ) {
            layers.get(layerType).setVisible( visible );
        }
    }
    
    /*public boolean isVisible(LayerType layerType) {
        return visibles.get(layerType).booleanValue();
    }*/
    
    public void init(int xPixels, int yPixels) {
        this.xPixels = xPixels;
        this.yPixels = yPixels;
        for (BufferedPanel layer : layers.values()) {
            layer.init(xPixels, yPixels);
        }
        // TYPE_INT_ARGB supports transparency
        imageBuffer = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
        graphicsBuffer = (Graphics2D)imageBuffer.getGraphics();
        graphicsBuffer.setClip(0, 0, xPixels, yPixels);
        setPreferredSize(new Dimension(xPixels, yPixels));
        initted = true;        
        needRepaint = true;
    }
    
    public abstract void init();
    
    private Manager aodm;
    
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
    
    private AreaDataBase adb;
    
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
    public void setAdb(AreaDataBase  adb) {
        this.adb = adb;
        if( adb != null ) {
            Player p = adb.game.getPlayer();
        }
    }
    
        CommandCollection cc;
    
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
        
    
    public abstract Dimension getAreaPlace(int x, int y);
         
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        // do nothing
    }
    
    public void mousePressed(java.awt.event.MouseEvent evt) {
        if( (evt.getModifiers() & evt.BUTTON2_MASK) != 0 ) {
            xDragStart = evt.getX();
            yDragStart = evt.getY();
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
        // do nothing
    }
    
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        // do nothing        
    }
    
    public void mouseExited(java.awt.event.MouseEvent evt) {
        // do nothing
    }    
    
    public void mouseDragged(MouseEvent evt) {        
         if( (evt.getModifiers() & evt.BUTTON2_MASK) != 0 ) {
            xOffset -= evt.getX() - xDragStart;
            yOffset -= evt.getY() - yDragStart;
            xDragStart = evt.getX();
            yDragStart = evt.getY();
            if( xOffset > xPixels/2 ) {
                xOffset -= xPixels/2;
            } else if( xOffset < 0 /*2*size*/ ) {
                xOffset += xPixels/2;
            }
            if( aodm.getGame().getGameType().wordWrapY() ) {
                if( yOffset > yPixels ) {
                    yOffset -= yPixels/2;
                } else if( yOffset < 0 /*size*/ ) {
                    yOffset += yPixels/2;
                }
            } else {
                if( yOffset < 0 ) {
                    yOffset = 0;
                }
                if( yOffset > yPixels - getHeight() ) {
                    yOffset = yPixels - getHeight();
                }
            }
            //System.out.println("xOffset:"+xOffset+"  yOffset:"+yOffset);
            repaint();
        }
    }

    /**
     * Updates the status label, based on the position of the mouse
     */
    public void mouseMoved(MouseEvent evt) {
        updateStatusLabel(getAreaPlace(evt.getX(), evt.getY()));
    }


    /** 
     * Updates the status label.
     * Shows information about the current area.
     */
    public void updateStatusLabel(Dimension d) {
        if (getAdb().getXSize() != 0) {
            AreaInformation ai = getAdb().getAreaInformation(getAdb().getId(d.width,d.height));
            if (ai != null) {
                String statusStr = "Area ["+d.width+","+d.height+"] = "+ai.getId()+" Player: ";
                if( ai.getOwner() == -1 ) {
                    statusStr += "Unknown";
                } else {
                    statusStr += getAodm().getGame().getPlayer(ai.getOwner()).getName();
                }
                if (ai.getUnitType() != 0) {
                    statusStr += " Unit: "+getAodm().getGame().getPlayer(ai.getUnitOwner()).getName()+" " + Unit.getUnit(ai.getUnitType()).getName();
                }
                if( ai.getSupplyPointNum() > 0 ) {
                    statusStr += " Supply Points: "+ai.getSupplyPointNum();
                }
                if (cc != null) {
                    Command c = cc.getCommand(ai);
                    if( c != null ) {
                        //System.out.println("c:"+c);
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
     * Common abstract superclass for the layer-drawing classes.
     */
    public abstract class BufferedPanel extends JPanel {
        
        private Image imageBuffer;
        protected int xPixels, yPixels;
        private boolean offScreen;
        private Graphics2D graphicsBuffer;
        private boolean needRepaint=true;
        //private boolean visible=true;
        
        public void init(int xPixels, int yPixels) {
            this.xPixels = xPixels;
            this.yPixels = yPixels;
            setPreferredSize( new Dimension(xPixels, yPixels));
            setSize( xPixels, yPixels);
        }
        
        public void paint(Graphics g) {   
            System.out.println("Buffered paint ");
            /*if( !visible ) {
                System.out.println("Not visible layer");
                return;
            }*/
            if( imageBuffer == null && xPixels != 0 ) { 
                // TYPE_INT_ARGB supports transparency
                imageBuffer = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
                graphicsBuffer = (Graphics2D)imageBuffer.getGraphics();
                graphicsBuffer.setClip(0, 0, xPixels, yPixels);
                offScreen = true;                                    
            }
            if( offScreen ) {
                if( isNeedRepaint() ) {
                     // Clear image with transparent alpha by drawing a rectangle
                    graphicsBuffer.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                    Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, xPixels, yPixels); 
                    graphicsBuffer.fill(rect);
                    graphicsBuffer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    realPaint(graphicsBuffer);
                    setNeedRepaint(false);                    
                }
                System.out.println("No need the realpaint");
                g.drawImage(imageBuffer, 0, 0, this);
            } else {
                realPaint((Graphics2D)g);
            }
        }

        public boolean isNeedRepaint() {
            return needRepaint;
        }

        public void setNeedRepaint(boolean needRepaint) {
            this.needRepaint = needRepaint;
        }
        
        /**
         * The real paint method of the class. Subclasses has to implement
         * this method.
         */
        public abstract void realPaint(Graphics2D g);
    }           
}

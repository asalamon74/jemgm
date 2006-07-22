package jemgm;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author salamon
 */
public abstract class AbstractBattleFieldPanel extends JLayeredPane{

    public enum LayerType {LAYER_AREA, LAYER_COMMAND, LAYER_FRONTLINE, LAYER_NEWAREA};
    
    protected HashMap<LayerType, BufferedPanel> layers;
    //private HashMap<LayerType, Boolean> needRepaints;
    private HashMap<LayerType, Boolean> visibles;
    private boolean initted = false;
    
    /** Creates a new instance of AbstractBattleFieldPanel */
    public AbstractBattleFieldPanel(Manager aodm) {
        this.aodm = aodm;
        layers = new HashMap<LayerType, BufferedPanel>();
        //needRepaints = new HashMap<LayerType, Boolean>();
        visibles = new HashMap<LayerType, Boolean>();
    }
    
    public void paint(Graphics g) {
        System.out.println("abstract paint");
        if( !initted && getAdb().getYSize() != 0 ) {
            System.out.println("ready to init");
            init();
        }
        if( initted ) {
            System.out.println("super paint");
            super.paint(g);
        }
    }
    
    public void setNeedRepaint( boolean needRepaint) {
        for (LayerType layerType : LayerType.values()) {
            setNeedRepaint( layerType, needRepaint);
        }
    }
    
    public void setNeedRepaint(LayerType layerType, boolean needRepaint) {
        //needRepaints.put(layerType, Boolean.valueOf(needRepaint));
        if( layers.get(layerType) != null ) {
            layers.get(layerType).setNeedRepaint( needRepaint );
        }
    }
    
    /*public boolean isNeedRepaint(LayerType layerType) {
        return needRepaints.get(layerType).booleanValue();
    }*/
    
    public void setVisible(LayerType layerType, boolean visible) {
        visibles.put(layerType, Boolean.valueOf(visible));
    }
    
    public boolean isVisible(LayerType layerType) {
        return visibles.get(layerType).booleanValue();
    }
    
    public void init(int xPixels, int yPixels) {
        for (BufferedPanel layer : layers.values()) {
            layer.init(xPixels, yPixels);
        }
        setPreferredSize(new Dimension(xPixels, yPixels));
        initted = true;
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
            
    /**
     * Common abstract superclass for the layer-drawing classes.
     */
    public abstract class BufferedPanel extends JPanel {
        
        private Image imageBuffer;
        protected int xPixels, yPixels;
        private boolean offScreen;
        private Graphics2D graphicsBuffer;
        private boolean needRepaint=true;
        private boolean visible=true;
        
        public void init(int xPixels, int yPixels) {
            this.xPixels = xPixels;
            this.yPixels = yPixels;
            setPreferredSize( new Dimension(xPixels, yPixels));
            setSize( xPixels, yPixels);
        }
        
        public void paint(Graphics g) {   
            System.out.println("Buffered paint "+xPixels);
            if( !visible ) {
                return;
            }
            if( imageBuffer == null && xPixels != 0 ) { 
                // TYPE_INT_ARGB supports transparency
                imageBuffer = new BufferedImage(xPixels, yPixels, BufferedImage.TYPE_INT_ARGB);
                graphicsBuffer = (Graphics2D)imageBuffer.getGraphics();
                graphicsBuffer.setClip(0, 0, xPixels, yPixels);
                offScreen = true;                                    
            }
            if( offScreen ) {
                if( isNeedRepaint() ) {
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
        
        public abstract void realPaint(Graphics2D g);
    }
    
}

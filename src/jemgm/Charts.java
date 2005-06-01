package jemgm;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jfree.data.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.*;
import org.jfree.chart.axis.*;

/**
 *
 * @author  salamon
 */
public class Charts extends JPanel implements ActionListener {
    
    private Charts(Manager aodm) {
        this.aodm = aodm;
        initComponents();
        fillComponents();
    }
    
    public static Charts getInstance(Manager aodm) {
        if( __instance == null ) {
            __instance = new Charts(aodm);
        }
        return __instance;
    }
    
    public void initComponents() {
    }
    
    public void fillComponents() {
        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        if( chartPanel == null ) {
            chartPanel = new ChartPanel(chart);
            //chartPanel.setPreferredSize(new Dimension(700, 470));
            setLayout(new BorderLayout());
            add(BorderLayout.CENTER, chartPanel);
            //setSize(new Dimension(800,500));
        } else {
            chartPanel.setChart(chart);
        }
    }
    
    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
        
        // row keys...
        String supplySeries = "Supply points";
        String armySeries = "Army strength";
        String landSeries = "Land areas";
        String seaSeries = "Sea areas";
        String victorySeries = "Victory Points";
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        if( aodm.getGame() == null ) {
            return null;
        }
        int lastTurn = aodm.getGame().getMapcoll().getLatestTurn();
        String []turns = new String[lastTurn];
        for( int i=0; i<lastTurn; ++i ) {
            turns[i] = "Turn "+(i+1);
            Turn tt = aodm.getTurn(i+1);
            int supply = tt.getAreadb().getSupplyPointNum(aodm.getGame().getPlayer());
            int army = tt.getAreadb().getArmyStrength(aodm.getGame().getPlayer());
            int land = tt.getAreadb().getLandAreasNum(aodm.getGame().getPlayer());
            int sea = tt.getAreadb().getSeaAreasNum(aodm.getGame().getPlayer());
            int vp = supply+land+sea;
            dataset.addValue(supply, supplySeries, turns[i]);
            dataset.addValue(army, armySeries, turns[i]);
            dataset.addValue(land, landSeries, turns[i]);
            dataset.addValue(sea, seaSeries, turns[i]);
            dataset.addValue(vp, victorySeries, turns[i]);
        }
        
        return dataset;
    }
    
    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(CategoryDataset dataset) {
        
        // create the chart...
        JFreeChart chart = ChartFactory.createLineChart(
                "AOD Charts",              // chart title
                "Turn",                    // domain axis label
                "",                        // range axis label
                dataset,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
                );
        
        // customizations
        chart.setBackgroundPaint(new Color(0x6C, 0x6C, 0xFF));
        
        StandardLegend legend = (StandardLegend) chart.getLegend();
        legend.setDisplaySeriesShapes(true);
        legend.setShapeScaleX(1.5);
        legend.setShapeScaleY(1.5);
        legend.setDisplaySeriesLines(true);
        
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        
        renderer.setDrawShapes(true);
        renderer.setShapesFilled(true);
        renderer.setItemLabelsVisible(true);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setUpperMargin(0.20);
        //rangeAxis.setLabelAngle(Math.PI / 2.0);
        
        return chart;
    }
    
    public  void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
    }
    
    private Manager aodm;
    private static Charts __instance;
    private ChartPanel chartPanel;
    
}

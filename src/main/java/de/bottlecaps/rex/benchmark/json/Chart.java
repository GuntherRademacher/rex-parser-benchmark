 package de.bottlecaps.rex.benchmark.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class Chart {
  private static enum Property {
    chartTitle,
    xAxisLabel,
    yAxisLabel,
    xAxisFormat,
    yAxisFormat,
  }

  private final JFreeChart jFreeChart;

  public static class Builder {
    private final DefaultXYDataset dataset1 = new DefaultXYDataset();
    private final DefaultXYDataset dataset2 = new DefaultXYDataset();
    String chartTitle;
    NumberFormat xAxisFormat;
    NumberFormat yAxisFormat1;
    NumberFormat yAxisFormat2;
    String xAxisLabel;
    String yAxisLabel1;
    String yAxisLabel2;

    public Chart build1() throws IOException {
      Map<Property, Object> props = new HashMap<>();
      if (yAxisLabel1 != null && xAxisLabel != null)
        props.put(Property.chartTitle, yAxisLabel1 + " by " + xAxisLabel);
      if (xAxisFormat  != null)
        props.put(Property.xAxisFormat, xAxisFormat);
      if (yAxisFormat1  != null)
        props.put(Property.yAxisFormat, yAxisFormat1);
      if (xAxisLabel  != null)
        props.put(Property.xAxisLabel, xAxisLabel);
      if (yAxisLabel1  != null)
        props.put(Property.yAxisLabel, yAxisLabel1);
      return new Chart(dataset1, props);
    }

    public Chart build2() throws IOException {
      Map<Property, Object> props = new HashMap<>();
      if (yAxisLabel2 != null && xAxisLabel != null)
        props.put(Property.chartTitle, yAxisLabel2 + " by " + xAxisLabel);
      if (xAxisFormat  != null)
        props.put(Property.xAxisFormat, xAxisFormat);
      if (yAxisFormat2  != null)
        props.put(Property.yAxisFormat, yAxisFormat2);
      if (xAxisLabel  != null)
        props.put(Property.xAxisLabel, xAxisLabel);
      if (yAxisLabel2  != null)
        props.put(Property.yAxisLabel, yAxisLabel2);
      return new Chart(dataset2, props);
    }

    public Chart build() throws IOException {
      return new Chart(chartTitle, xAxisLabel, xAxisFormat,
          dataset1, yAxisLabel1, yAxisFormat1,
          dataset2, yAxisLabel2, yAxisFormat2);
    }

    public Builder chartTitle(String chartTitle) {
      this.chartTitle = chartTitle;
      return this;
    }

    public Builder xAxisFormat(NumberFormat xAxisFormat) {
      this.xAxisFormat = xAxisFormat;
      return this;
    }

    public Builder yAxisFormat1(NumberFormat yAxisFormat) {
      this.yAxisFormat1 = yAxisFormat;
      return this;
    }

    public Builder yAxisFormat2(NumberFormat yAxisFormat) {
      this.yAxisFormat2 = yAxisFormat;
      return this;
    }

    public Builder xAxisLabel(String xAxisLabel) {
      this.xAxisLabel = xAxisLabel;
      return this;
    }

    public Builder yAxisLabel1(String yAxisLabel) {
      yAxisLabel1 = yAxisLabel;
      return this;
    }

    public Builder yAxisLabel2(String yAxisLabel) {
      yAxisLabel2 = yAxisLabel;
      return this;
    }

    public Builder addSeries1(String name, double[][] series) {
      dataset1.addSeries(name, series);
      return this;
    }

    public Builder addSeries2(String name, double[][] series) {
      dataset2.addSeries(name, series);
      return this;
    }
  }

  private Chart(String chartTitle, String xAxisLabel, NumberFormat xAxisFormat,
      XYDataset dataset1, String yAxisLabel1, NumberFormat yAxisFormat1,
      XYDataset dataset2, String yAxisLabel2, NumberFormat yAxisFormat2) throws IOException {

    NumberAxis domainAxis = new NumberAxis(xAxisLabel);
    domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    if (xAxisFormat != null)
      domainAxis.setNumberFormatOverride(xAxisFormat);

    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainAxis);
    plot.setOrientation(PlotOrientation.VERTICAL);

    final NumberAxis rangeAxis1 = new NumberAxis(yAxisLabel1);
    rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis1.setNumberFormatOverride(yAxisFormat1);
    final XYPlot subPlot1 = new XYPlot(dataset1, null, rangeAxis1, new XYLineAndShapeRenderer(true, true));

    final NumberAxis rangeAxis2 = new NumberAxis(yAxisLabel2);
    rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis2.setNumberFormatOverride(yAxisFormat2);
    final XYPlot subPlot2 = new XYPlot(dataset2, null, rangeAxis2, new XYLineAndShapeRenderer(true, true));

    TreeSet<Double> xValues = new TreeSet<>();
    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    for (int series = 0; series < dataset1.getSeriesCount(); ++series)
      for (int item = 0; item < dataset1.getItemCount(series); ++item) {
        double x = dataset1.getXValue(series, item);
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        xValues.add(x);
      }
    for (int series = 0; series < dataset2.getSeriesCount(); ++series)
      for (int item = 0; item < dataset1.getItemCount(series); ++item) {
        double x = dataset1.getXValue(series, item);
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        xValues.add(x);
      }
    System.out.println("minX: " + minX);
    System.out.println("maxX: " + maxX);
    domainAxis.setRange(minX, maxX + 2);

    plot.add(subPlot1, 1);
    plot.add(subPlot2, 1);

    domainAxis.setRange(new Range(12., 17.), false, false);

    jFreeChart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    LegendTitle legend = jFreeChart.getLegend();
    legend.setPosition(RectangleEdge.TOP);
  }

  private Chart(XYDataset dataset, Map<Property, Object> props) throws IOException {
    this.jFreeChart = ChartFactory.createXYLineChart((String) props.get(Property.chartTitle),
        (String) props.get(Property.xAxisLabel),
        (String) props.get(Property.yAxisLabel),
        dataset,
        PlotOrientation.VERTICAL,
        true,
        true,
        false
    );
    LegendTitle legend = this.jFreeChart.getLegend();
    legend.setPosition(RectangleEdge.RIGHT);
    XYPlot plot = (XYPlot) jFreeChart.getPlot();
    plot.setRenderer(new XYLineAndShapeRenderer(true, true));

    NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
    NumberFormat xAxisFormat = (NumberFormat) props.get(Property.xAxisFormat);
    if (xAxisFormat != null)
      xAxis.setNumberFormatOverride(xAxisFormat);

    NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
    yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    NumberFormat yAxisformat = (NumberFormat) props.get(Property.yAxisFormat);
    if (yAxisformat != null)
      yAxis.setNumberFormatOverride(yAxisformat);

  }

  public void writeToFile(String path) throws IOException {
    Dimension preferredSize = new ChartPanel(jFreeChart, false).getPreferredSize();
    int width = (int) preferredSize.getWidth();
    int height = (int) preferredSize.getHeight();
    ChartUtils.writeChartAsPNG(new FileOutputStream(path), jFreeChart, width, height);
  }

  public void displayChart(String title) {
    if (!GraphicsEnvironment.isHeadless())
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          JFrame frame = new JFrame(title);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          ChartPanel chartPanel = new ChartPanel(jFreeChart, false);
          frame.add(chartPanel, BorderLayout.CENTER);
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
          frame.setExtendedState(JFrame.ICONIFIED);
          frame.setExtendedState(JFrame.NORMAL);
        }
      });
  }

  public static String formatSize(long size, int fractionDigits) {
    String format = "%." + fractionDigits + "f ";
    String[] formats = {
        "%.0f B",
        format + "kB",
        format + "MB",
        format + "GB",
        format + "TB",
        format + "PB",
        format + "EB"};
    int unit = size <= 0 ? 0 : (int) (Math.log10(size) / 3);
    return String.format(Locale.US, formats[unit], size / Math.pow(1000, unit));
  }

  public static class LogarithmicSizeFormat extends NumberFormat {
    private static final long serialVersionUID = -3657230198170622639L;
    private int base;

    public LogarithmicSizeFormat(int base) {
      this.base = base;
    }

    @Override
    public StringBuffer format(double log, StringBuffer sb, FieldPosition pos) {
      return sb.append(formatSize((long) Math.pow(base, log), 0));
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
      throw new UnsupportedOperationException();
    }
  }

  public static class LinearSizeFormat extends NumberFormat {
    private static final long serialVersionUID = -3657230198170622639L;

    public LinearSizeFormat() {
    }

    @Override
    public StringBuffer format(double value, StringBuffer sb, FieldPosition pos) {
      return sb.append(formatSize((long) value, 1));
    }

    @Override
    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Number parse(String source, ParsePosition parsePosition) {
      throw new UnsupportedOperationException();
    }
  }

  public static void main(String[] args) throws IOException {
    Benchmark.csvToPng();
  }
}
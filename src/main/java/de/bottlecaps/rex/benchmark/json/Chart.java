 package de.bottlecaps.rex.benchmark.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class Chart {
  private static enum Property {
    chartTitle,
    xAxisLabel,
    yAxisLabel,
  }

  private final JFreeChart jFreeChart;

  public static class Builder {
    private final DefaultXYDataset dataset1 = new DefaultXYDataset();
    private final DefaultXYDataset dataset2 = new DefaultXYDataset();
    String chartTitle;
    String xAxisLabel;
    String yAxisLabel1;
    String yAxisLabel2;

    public Chart build1() throws IOException {
      Map<Property, Object> props = new HashMap<>();
      if (yAxisLabel1 != null && xAxisLabel != null)
        props.put(Property.chartTitle, yAxisLabel1 + " by " + xAxisLabel);
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
      if (xAxisLabel  != null)
        props.put(Property.xAxisLabel, xAxisLabel);
      if (yAxisLabel2  != null)
        props.put(Property.yAxisLabel, yAxisLabel2);
      return new Chart(dataset2, props);
    }

    public Chart build() throws IOException {
      return new Chart(chartTitle, xAxisLabel,
          dataset1, yAxisLabel1,
          dataset2, yAxisLabel2);
    }

    public Builder chartTitle(String chartTitle) {
      this.chartTitle = chartTitle;
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

  private Chart(String chartTitle, String xAxisLabel,
      XYDataset dataset1, String yAxisLabel1,
      XYDataset dataset2, String yAxisLabel2) throws IOException {

    final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(logAxis(xAxisLabel));
    plot.setOrientation(PlotOrientation.VERTICAL);
    plot.add(new XYPlot(dataset1, null, logAxis(yAxisLabel1), new XYLineAndShapeRenderer(true, true)), 1);
    plot.add(new XYPlot(dataset2, null, logAxis(yAxisLabel2), new XYLineAndShapeRenderer(true, true)), 1);
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
    plot.setDomainAxis(logAxis((String) props.get(Property.xAxisLabel)));
    plot.setRangeAxis(logAxis((String) props.get(Property.yAxisLabel)));
  }


  private static LogAxis logAxis(String label) {
    LogAxis axis = new LogAxis(label);
    axis.setBase(2);
    axis.setNumberFormatOverride(new SizeFormat());
    return axis;
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

  private static String[] units = { "B", "KB", "MB", "GB", "TB", "PB", "EB" };

  public static String formatSize(double size, int fractionDigits) {
    if (size < 0 || fractionDigits < 0 || fractionDigits > 3)
      throw new IllegalArgumentException();
    size *= 1.0000000000001;
    int unit = size == 0 ? 0 : (int) (Math.log(size) / Math.log(2) / 10);
    NumberFormat format = NumberFormat.getInstance(Locale.US);
    format.setRoundingMode(RoundingMode.DOWN);
    format.setGroupingUsed(false);
    if (fractionDigits > 0) {
      format.setMinimumFractionDigits(fractionDigits);
      format.setMaximumFractionDigits(fractionDigits);
    }
    String formatted = format.format(size / Math.pow(2, 10 * unit));
    if (fractionDigits == 0) {
      formatted = formatted.substring(0, Math.min(4, formatted.length()));
      if (formatted.endsWith("."))
        formatted = formatted.substring(0, formatted.length() - 1);
    }
    return formatted + " " + units[unit];
  }

  public static class SizeFormat extends NumberFormat {
    private static final long serialVersionUID = -3657230198170622639L;

    @Override
    public StringBuffer format(double value, StringBuffer sb, FieldPosition pos) {
      return sb.append(formatSize(value, 0));
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
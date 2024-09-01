package com.larcode.ij_mea_viewer.plot;

import com.larcode.ij_mea_viewer.readBio.Mea;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@RequiredArgsConstructor
public class MeaPlot {

  public final Mea mea;
  public final float plotStart;
  public final float plotEnd;
  public final int voltMax;
  public final int voltMin;

  public void showSingle(int ch) {
    int startFrame = (int) (this.plotStart * mea.samplingRate);
    int endFrame = (int) (this.plotEnd * mea.samplingRate);

    XYSeries series = new XYSeries("Voltage");
    for (int i = startFrame; i < endFrame; i++) {
      series.add(mea.array[0][i], mea.array[ch][i]);
    }

    XYSeriesCollection dataset = new XYSeriesCollection(series);
    JFreeChart chart = ChartFactory.createXYLineChart(
        null,
        "Time (s)",
        "Voltage (μV)",
        dataset,
        PlotOrientation.VERTICAL,
        false, false, false
    );

    XYPlot plot = chart.getXYPlot();
    plot.getDomainAxis().setRange(this.plotStart, this.plotEnd);
    plot.getRangeAxis().setRange(this.voltMin, this.voltMax);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(800, 600));

    showChartInWindow(chartPanel, String.format("ch %d", ch));
  }

  public void showAll() {
    int startFrame = (int) (this.plotStart * mea.samplingRate);
    int endFrame = (int) (this.plotEnd * mea.samplingRate);

    JPanel panel = new JPanel(new GridLayout(8, 8));
    for (int i = 1; i <= 64; i++) {
      XYSeries series = new XYSeries(String.format("ch %d", i));

      for (int j = startFrame; j < endFrame; j++) {
        series.add(mea.array[0][j], mea.array[i][j]);
      }

      XYSeriesCollection dataset = new XYSeriesCollection(series);
      JFreeChart chart = ChartFactory.createXYLineChart(
          null,
          "Time (s)",
          "Voltage (μV)",
          dataset,
          PlotOrientation.VERTICAL,
          false, false, false
      );

      XYPlot plot = chart.getXYPlot();
      plot.getRangeAxis().setRange(voltMin, voltMax);

      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setPreferredSize(new Dimension(100, 100));
      panel.add(chartPanel);
    }

    JFrame frame = new JFrame("64電極表示");
    frame.setSize(800, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(panel);
    frame.pack();
    frame.setVisible(true);
  }

  public void a() {
    Thread th = new Thread(() -> {
      for (int i = 0; i < 5; i++) {
        System.out.println(i);
      }
    });
    th.start();
  }

  private void showChartInWindow(ChartPanel chartPanel, String title) {
    JFrame frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(chartPanel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

}

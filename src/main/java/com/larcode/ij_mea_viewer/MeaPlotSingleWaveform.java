package com.larcode.ij_mea_viewer;

import com.larcode.ij_mea_viewer.plot.Plotter;
import ij.plugin.PlugIn;

public class MeaPlotSingleWaveform implements PlugIn {

  @Override
  public void run(String s) {
    String hedPath = "/Users/ken/Documents/dev/MEA_modules/test/public/230615_day2_test_5s_.hed";
    Plotter.plot(hedPath, meaPlot -> meaPlot.showSingle(1));
  }
}

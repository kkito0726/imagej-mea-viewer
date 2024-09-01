package com.larcode.ij_mea_viewer;

import com.larcode.ij_mea_viewer.plot.MeaPlot;
import com.larcode.ij_mea_viewer.plot.Plotter;
import ij.plugin.PlugIn;

public class MeaPlot64Waveform implements PlugIn {

  @Override
  public void run(String s) {
    String hed_path = "/Users/ken/Documents/dev/MEA_modules/test/public/230615_day2_test_5s_.hed";
    Plotter.plot(hed_path, MeaPlot::showAll);
  }
}

package com.larcode.ij_mea_viewer;

public class IjMeaViewerApplication {

  public static void main(String[] args) {
    new MeaPlotSingleWaveform().run("");
    new MeaPlot64Waveform().run("");
  }
}

package com.larcode.ij_mea_viewer;

import com.larcode.ij_mea_viewer.plot.MeaPlot;
import com.larcode.ij_mea_viewer.plot.Plotter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class MeaPlot64WaveformTest {

  @Test
  public void 全64電極表示ができる() {
    String hedPath = "/Users/ken/Documents/dev/MEA_modules/test/public/230615_day2_test_5s_.hed";
    Plotter.plot(hedPath, MeaPlot::showAll);
  }
}
package com.larcode.ij_mea_viewer.plot;

import com.larcode.ij_mea_viewer.readBio.Mea;
import java.io.IOException;
import java.util.function.Consumer;

public class Plotter {
  public static void plot(String hedPath, Consumer<MeaPlot> consumer) {
    try {
      Mea mea = new Mea(hedPath, 0, 6);
      MeaPlot plot = new MeaPlot(mea, 0, 5, 200, -200);
      consumer.accept(plot);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}

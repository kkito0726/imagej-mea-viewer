package com.larcode.ij_mea_viewer.readBio;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class MeaTest {

  @Test
  public void hedファイルからMEAデータを読み込むことができる() throws IOException {
    String hed_path = "/Users/ken/Documents/dev/MEA_modules/test/public/230615_day2_test_5s_.hed";

    Mea mea = new Mea(hed_path, 1, 3);

    assertEquals(mea.samplingRate, 10000);
    assertEquals(mea.gain, 2000);

    assertEquals(mea.array.length, 65);
    for (double[] data : mea.array) {
      assertEquals(data.length, mea.time * mea.samplingRate);
    }
  }
}
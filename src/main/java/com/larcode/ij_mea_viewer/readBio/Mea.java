package com.larcode.ij_mea_viewer.readBio;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Mea {
  public static final int ELECTRODES = 64;
  public final String hedPath;
  public final int start;
  public final int end;
  public final int time;
  public final int samplingRate;
  public final int gain;
  public final double[][] array;

  public Mea(String hedPath, int start, int end) throws IOException {
    this.hedPath = hedPath;
    this.start = start;
    this.end = end;
    this.time = this.end - this.start;

    int[] hedData = decodeHed(hedPath);
    this.samplingRate = hedData[0];
    this.gain = hedData[1];

    // bioファイルのパスを生成
    String bioPath = Paths.get(hedPath).toString().replace(".hed", "0001.bio");
    this.array = readBio(bioPath, this.start, this.end, this.samplingRate, this.gain);
  }

  private int[] decodeHed(String hedPath) throws IOException {
    // hedファイルを読み込む
    short[] hedData = readHedFile(hedPath);

    // rate（サンプリングレート）、gain（ゲイン）の解読辞書
    Map<Integer, Integer> rates = new HashMap<>();
    rates.put(0, 100000);
    rates.put(1, 50000);
    rates.put(2, 25000);
    rates.put(3, 20000);
    rates.put(4, 10000);
    rates.put(5, 5000);

    Map<Integer, Integer> gains = new HashMap<>();
    gains.put(16436, 20);
    gains.put(16473, 100);
    gains.put(16527, 1000);
    gains.put(16543, 2000);
    gains.put(16563, 5000);
    gains.put(16579, 10000);
    gains.put(16595, 20000);
    gains.put(16616, 50000);

    // サンプリングレートとゲインを返す
    // hedDataの要素16がrate、要素3がgainのキーとなる
    return new int[]{rates.get((int) hedData[16]), gains.get((int) hedData[3])};
  }

  private static short[] readHedFile(String filePath) throws IOException {
    try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
      byte[] fileData = dis.readAllBytes();
      short[] hedData = new short[fileData.length / 2];
      ByteBuffer buffer = ByteBuffer.wrap(fileData).order(ByteOrder.LITTLE_ENDIAN);
      for (int i = 0; i < hedData.length; i++) {
        hedData[i] = buffer.getShort();
      }
      return hedData;
    }
  }

  private double[][] readBio(
      String bioPath, int start, int end, int samplingRate, int gain) throws IOException {

    int electrodeNumber = 64;
    int dataUnitLength = electrodeNumber + 4;

    int bytesize = Short.BYTES;
    int offset = start * samplingRate * bytesize * dataUnitLength;
    int count = (end - start) * samplingRate * dataUnitLength;

    short[] dataShort = readBioFile(bioPath, offset, count);

    double scalingFactor = (100 / (Math.pow(2, 16) - 2)) * 4;
    double[] data = new double[dataShort.length];
    for (int i = 0; i < dataShort.length; i++) {
      data[i] = dataShort[i] * scalingFactor;
    }

    double[][] reshapedData = reshapeData(data, electrodeNumber, dataUnitLength);
    reshapedData = removeFirstColumns(reshapedData);

    // ゲインの調整
    if (gain != 50000) {
      double amp = 50000.0 / gain;
      for (int i = 0; i < reshapedData.length; i++) {
        for (int j = 0; j < reshapedData[i].length; j++) {
          reshapedData[i][j] *= amp;
        }
      }
    }

    double[] time = new double[reshapedData[0].length];
    for (int i = 0; i < time.length; i++) {
      time[i] = start + (i / (double) samplingRate);
    }

    double[][] resultData = new double[reshapedData.length + 1][];
    resultData[0] = time;
    System.arraycopy(reshapedData, 0, resultData, 1, reshapedData.length);

    return resultData;
  }
  private short[] readBioFile(String filePath, int offset, int count) throws IOException {
    try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
      dis.skipBytes(offset);
      byte[] buffer = new byte[count * Short.BYTES];
      dis.readFully(buffer);
      short[] shortData = new short[count];
      ByteBuffer byteBuffer = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN);
      for (int i = 0; i < count; i++) {
        shortData[i] = byteBuffer.getShort();
      }
      return shortData;
    }
  }

  private double[][] reshapeData(double[] data, int electrodeNumber, int dataUnitLength) {
    int numCols = data.length / dataUnitLength;
    double[][] reshapedData = new double[electrodeNumber][numCols];
    for (int i = 0; i < numCols; i++) {
      for (int j = 0; j < electrodeNumber; j++) {
        reshapedData[j][i] = data[i * dataUnitLength + j + 4]; // 最初の4列をスキップ
      }
    }
    return reshapedData;
  }
  private double[][] removeFirstColumns(double[][] data) {
    int frames = this.time * this.samplingRate;

    int numRows = data.length;
    int numCols = data[0].length - 4;
    double[][] newData = new double[ELECTRODES][frames];
    for (int i = 0; i < numRows; i++) {
      System.arraycopy(data[i], 4, newData[i], 0, numCols);
    }
    return newData;
  }

}

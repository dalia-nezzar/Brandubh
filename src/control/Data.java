package control;

import java.io.*;

// A simple data class that we want to serialize
public class Data<T> implements Serializable {
  private String key;
  private int WCountR;
  private int WCountB;

  public Data(int WCountR, int WCountB) {
    this.WCountR = WCountR;
    this.WCountB = WCountB;
  }

  public int getWCountR() {
    return WCountR;
  }

  public void setWCountR(int WCountR) {
    this.WCountR = WCountR;
  }

  public int getWCountB() {
    return WCountB;
  }

  public void setWCountB(int WCountB) {
    this.WCountB = WCountB;
  }

  public String getKey() {
    // Return the key for this data object
    return key;
  }

  @Override
  public String toString() {
    return "Data{" +
        "Red Win Count='" + WCountR + '\'' +
        ", Blue Win Count='" + WCountB + '\'' +
        '}';
  }
}
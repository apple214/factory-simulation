package org.anan.project.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

  static Random random = new Random();

  public static int exponential(double mean) {
    double u = -mean * Math.log(random.nextDouble());
    return (int) u;
  }

  public static int normal(double mean,double sigma) {
    double u = mean + sigma * random.nextGaussian();
    return Math.abs( (int)u);
  }



  public static String doubleFormat(double input, String pattern) {
    return new java.text.DecimalFormat(pattern).format(input);
  }

  public static String doubleFormat(double input) {
    return doubleFormat(input, "0.00");
  }

  public static List<String> doubleListFormat(List<Double> input, String pattern) {
    List<String> numOutListStrEven = new ArrayList<>();
    for (Double num : input) {
      numOutListStrEven.add(doubleFormat(num,pattern));
    }
    return numOutListStrEven;
  }

  public static List<String> doubleListFormat(List<Double> input) {
    List<String> numOutListStrEven = new ArrayList<>();
    for (Double num : input) {
      numOutListStrEven.add(doubleFormat(num));
    }
    return numOutListStrEven;
  }

  public static List<Integer> generateExponentialData(int num,double sigma){
    List<Integer> arr = new ArrayList();

    for (int i = 0; i < num; i++) {
      arr.add(Utils.exponential(sigma));
    }
    return arr;
  }
  public static void main(String[] args) {

    int num = 500;
    List<Integer> arr = generateExponentialData(500,20.63);

    for (int i = 1; i < num; i++) {
      System.out.println(arr.get(i));
    }

  }
}

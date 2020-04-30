package org.anan.project;

import static org.anan.project.utils.Utils.doubleFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CICalculator {

  public static void main(String[] args) {
    Double[] array1 = new Double[]{0.520, 0.724, 0.451, 0.255, 0.565, 0.705, 0.579, 0.617, 0.697, 0.318};
    Double[] array2 = new Double[]{0.010, 0.034, 0.038, 0.026, 0.072, 0.049, 0.009, 0.007, 0.025, 0.041};
    List<Double> list1 = Arrays.asList(array1);
    List<Double> list2 = Arrays.asList(array2);
    System.out.println("array1 even:" + calEven(list1));
    System.out.println("array2 even:" + calEven(list2));

    System.out.println("array1 s2:" + doubleFormat(calS2(list1), "0.000000"));
    System.out.println("array2 s2:" + doubleFormat(calS2(list2), "0.000000"));

    List<Double> deltaList = new ArrayList<>();
    for (int i = 0; i < list1.size(); i++) {
      deltaList.add(list1.get(i) - list2.get(i));
    }

    System.out.println("deltaList even:" + calEven(deltaList));

    double delta = calCI(deltaList);
    System.out.println("delta:" + delta);
    System.exit(1);
  }


  public static double calCI(List<Double> array) {

    Double s2 = calS2(array);

    //a = 95%,R=10,t=2.262
    return 2.262 * Math.sqrt(s2) / Math.sqrt(array.size());
  }

  public static void calV(Double[] array) {

    Arrays.sort(array);
    List<Double> numOutList = Arrays.asList(array);
    double p = 0.8;
    int replication = 100;
    double z = 1.96;
    double pl = p - deltaP(replication, p, z);
    double pu = p + deltaP(replication, p, z);

    int indexL = (int) (replication * pl);
    int indexR = (int) (replication * pu);

    Double valueL = numOutList.get(indexL);
    Double valueR = numOutList.get(indexR);
    System.out.println(
        "pl:" + doubleFormat(pl) + ",pu:" + doubleFormat(pu));
    System.out.println("indexL:" + indexL + ",indexU:" + indexR);
    System.out.println("valueL:" + valueL + ",valueU:" + valueR);

  }

  static public double deltaP(int replication, double p, double z) {
    return z * Math.sqrt((p * (1 - p)) / (replication - 1));

  }


  static public Double calEven(List<Double> countList) {
    int size = countList.size();
    Double sum = 0.0;
    for (Double value : countList) {
      sum += value;
    }
    return sum / size;
  }

  static public Double calS2(List<Double> countList) {
    int size = countList.size();
    Double sum = 0.0;
    for (Double value : countList) {
      sum += value;
    }
    Double even = sum / size;

    Double s2 = 0.0;
    for (Double value : countList) {
      s2 += Math.pow((value - even), 2);
    }

    s2 = s2 / (size - 1);

    return s2;
  }


}

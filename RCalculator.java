package org.anan.project;

import static org.anan.project.utils.Utils.doubleFormat;

import java.util.Arrays;
import java.util.List;

public class RCalculator {

  public static void main(String[] args) {
    // component one period，(10, 15)
    Double[] array = new Double[]{0.084, 0.103, 0.094, 0.095, 0.094, 0.090, 0.100, 0.109, 0.103,
        0.102, 0.103, 0.103, 0.101, 0.103, 0.103, 0.101, 0.103, 0.103, 0.104, 0.100, 0.095, 0.102,
        0.103, 0.103, 0.104, 0.101, 0.101, 0.101, 0.103, 0.101, 0.103, 0.103, 0.101, 0.102, 0.103,
        0.102, 0.103, 0.101, 0.103, 0.102, 0.103, 0.100, 0.101, 0.102, 0.102, 0.102, 0.102, 0.101,
        0.102, 0.103, 0.102, 0.102, 0.102, 0.104, 0.103, 0.102, 0.101, 0.102, 0.103, 0.103, 0.103,
        0.103, 0.102, 0.103, 0.103, 0.102, 0.102, 0.103, 0.103, 0.103, 0.104, 0.103, 0.101, 0.102,
        0.101, 0.102, 0.101, 0.101, 0.103, 0.102, 0.102, 0.102, 0.102, 0.103, 0.103, 0.102, 0.103,
        0.103, 0.103, 0.103, 0.104, 0.103, 0.102, 0.102, 0.103, 0.101, 0.103, 0.101, 0.101, 0.102};

    //inspect block time in one period，(10, 15)
//    Double[] array = new Double[]{0.622, 0.651, 0.534, 0.594, 0.862, 0.701, 0.436, 0.554, 0.585,
//        0.624, 0.692, 0.666, 0.559, 0.684, 0.553, 0.706, 0.655, 0.468, 0.591, 0.528, 0.475, 0.708,
//        0.470, 0.478, 0.645, 0.629, 0.657, 0.562, 0.460, 0.512, 0.578, 0.487, 0.565, 0.453, 0.754,
//        0.695, 0.449, 0.376, 0.539, 0.629, 0.592, 0.642, 0.696, 0.435, 0.629, 0.526, 0.595, 0.550,
//        0.615, 0.562, 0.542, 0.639, 0.684, 0.480, 0.637, 0.499, 0.613, 0.503, 0.536, 0.622, 0.579,
//        0.439, 0.560, 0.552, 0.643, 0.527, 0.637, 0.689, 0.528, 0.580, 0.612, 0.610, 0.694, 0.655,
//        0.710, 0.632, 0.524, 0.583, 0.590, 0.433, 0.601, 0.648, 0.559, 0.643, 0.652, 0.492, 0.608,
//        0.550, 0.631, 0.683, 0.582, 0.623, 0.648, 0.485, 0.708, 0.464, 0.565, 0.522, 0.677, 0.464};
    calR(array);
    calV(array);
    System.exit(1);
  }

  public static void calR(Double[] array) {
    List<Double> numOutList = Arrays.asList(array).subList(10, 20);

    //check for each replication
    System.out.println("numOutList:" + numOutList);

    // component 0.001,block 0.05
    double e = 0.001;
    System.out.println("e:" + e);

    Double s2 = calS2(numOutList);

    double R1 = calNew1(s2, 1.96, e);
    double t = 2.306;
    double newR = calNewR(s2, t, e);
    System.out.println("t:" + t);
    System.out.println("R1:" + doubleFormat(R1));
    System.out.println("newR:" + doubleFormat(newR));
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

    System.out.println("S2:" + doubleFormat(s2, "0.000000"));
    return s2;
  }

  static public double calNew1(Double s2, double t, double e) {
    double newR = Math.pow(t * Math.sqrt(s2) / e, 2.0);
    return newR;
  }

  static public double calNewR(Double s2, double t, double e) {
    double newR = Math.pow(t * Math.sqrt(s2) / e, 2.0);
    return newR;
  }

}

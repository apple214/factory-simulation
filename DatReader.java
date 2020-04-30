package org.anan.project;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import org.anan.project.utils.Utils;

public class DatReader {

  public static List<Object> getDatFile(String fileName) throws IOException {
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    List<Object> list = new ArrayList<Object>();
    Object obj = null;
    try {
      fis = new FileInputStream("./" + fileName);
      BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
      ois = new ObjectInputStream(fis);
      while (true) {
        try {
          obj = ois.readObject();
          list.add(obj);
        } catch (EOFException e) {
          break;
        }
      }
    } catch (FileNotFoundException e) {
      //System.out.println("Can't find " + fileName);
      System.exit(-1);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (ois != null) {
        ois.close();
      }
      if (fis != null) {
        fis.close();
      }
    }
    return list;
  }


  private static List<Object> parseTxt(File file, String pattern) {
    String fileName = file.getName(); // testFile.txt

    List<Object> listRows = null;
    BufferedReader bf = null;
    String temp = null;
    try {
      FileInputStream fis = new FileInputStream(new File(file, ""));

      bf = new BufferedReader(new InputStreamReader(fis));
      listRows = new ArrayList<Object>();
      while ((temp = bf.readLine()) != null) {
        listRows.add(temp);
      }
    } catch (Exception e) {
      e.printStackTrace();
      //System.out.println("fail to read document");
    } finally {
      if (bf != null) {
        try {
          bf.close();
        } catch (IOException e2) {
        }
      }
    }
    return listRows;
  }

  public static List<Integer> getTimeArr(String fileS) {
    File file = new File(fileS);
    List<Object> arr = DatReader.parseTxt(file, "\\.");

    List<Integer> timeArr = new ArrayList<>();
    for (Object o : arr) {
      if (o.toString() != null && !o.toString().equals("")) {
//        timeArr.add((int) Float.parseFloat(o.toString()) * 60);
        timeArr.add((int) Float.parseFloat(o.toString()));
      }
    }
    return timeArr;
  }

  public static void generateDataToFile(String file, List<Integer> list) {
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(file);
      for (Integer i : list) {
        fileWriter.write(i + "\r\n");
      }
      fileWriter.flush();
      fileWriter.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {

    System.out.println("user.dir:" + System.getProperty("user.dir"));// look current path

    //read data
//    String fileS = "files/5001/Project/servinsp1.dat";
//    System.out.println(getTimeArr(fileS));

    //write data
    int num = 5000;
    String file1 = "files/5001/Project/generatedData/ws1.dat";
    String file2 = "files/5001/Project/generatedData/ws2.dat";
    String file3 = "files/5001/Project/generatedData/ws3.dat";
    String file4 = "files/5001/Project/generatedData/servinsp1.dat";
    String file5 = "files/5001/Project/generatedData/servinsp22.dat";
    String file6 = "files/5001/Project/generatedData/servinsp23.dat";

    List<Integer> arr1 = Utils.generateExponentialData(num, 4.60);
    List<Integer> arr2 = Utils.generateExponentialData(num, 11.09);
    List<Integer> arr3 = Utils.generateExponentialData(num, 8.79);
    List<Integer> arr4 = Utils.generateExponentialData(num, 10.35);
    List<Integer> arr5 = Utils.generateExponentialData(num, 15.53);
    List<Integer> arr6 = Utils.generateExponentialData(num, 20.63);
    generateDataToFile(file1,arr1);
    generateDataToFile(file2,arr2);
    generateDataToFile(file3,arr3);
    generateDataToFile(file4,arr4);
    generateDataToFile(file5,arr5);
    generateDataToFile(file6,arr6);
//    System.out.println(getDatFile(fileS));
  }
}

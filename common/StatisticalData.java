package org.anan.project.common;


import static org.anan.project.utils.Utils.doubleFormat;

import java.util.ArrayList;
import java.util.List;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;

public class StatisticalData {

  //The number of components currently in the system. The inspect input is increased and decreased when ws is completed.
  long currentComponentNum = 0;
  //Record the time of the last change, used to calculate (time, quantity) points
  public long lastProcessTime = 0;
  //Used to record (time, quantity) points
  long numTimeAccumulation = 0;
  //Used to record the total time spent generating each component
  long sumProcessTime = 0;

  public long numOutOfC = 0;
  public long numInOfC = 0;

  //output info
  double L;
  double lambda;
  double W;


  //Count the amount of completion in each batch time, used to calculate the initialization time T0
  public long sumNumInCurrentBatch = 0;
  public long timeIntervalInBatch = 100;
  public long timeInCurrentBatch = 0;
  List<Long> sumNumListInBatches = new ArrayList<>();
  List<Double> sumNumEvenListInBatches = new ArrayList<>();

  boolean supportLittleLawLog = false;

  //for batch test
  public void updateSumNumList(long currentTime) {
    timeInCurrentBatch = currentTime;
    sumNumListInBatches.add(sumNumInCurrentBatch);
    sumNumEvenListInBatches.add(sumNumInCurrentBatch * 1.0 / timeIntervalInBatch);
    sumNumInCurrentBatch = 0;
  }

  public void updateSumProcessTimeForParentEvent(Event event2, long currentTime) {

    for (Event event : ((Event2) event2).getParentEvent()) {
      sumProcessTime +=
          currentTime - event.getStartTime();
      if (supportLittleLawLog) {
        System.out.println("sumProcessTime:" + sumProcessTime +
            ",delta:" + (currentTime - event.getStartTime()));
      }
    }

  }

  public void updateSumProcessTime(Event event2, long currentTime) {

    if (event2 instanceof Event2) {
      sumProcessTime +=
          ((Event2) event2).getParentEvent().size() * (currentTime - event2.getStartTime());
    } else {
      sumProcessTime += currentTime - event2.getStartTime();
    }

    if (supportLittleLawLog) {
      System.out.println("sumProcessTime:" + sumProcessTime +
          ",delta:" + (currentTime - event2.getStartTime()));
    }

  }

  public void calculateNumAccumulation(long deltaComponentNum, long currentTime) {
    numTimeAccumulation += currentComponentNum * (currentTime - lastProcessTime);

    if (supportLittleLawLog) {

      System.out.println("numTimeAccumulation:" + numTimeAccumulation +
          ",beforeComponentNum:" + currentComponentNum +
          ",currentComponentNum:" + (currentComponentNum + deltaComponentNum) +
          ",deltaTime:" + (currentTime - lastProcessTime));
    }
    lastProcessTime = currentTime;
    currentComponentNum += deltaComponentNum;
  }


  public void reportGeneration() {
//    System.out.println("little'law");

    System.out.println(
        "L:" + doubleFormat(L) + ",numInOfC:" + numInOfC + ",numOutOfC:" + numOutOfC + ",lambda:"
            + doubleFormat(lambda)
            + ",W:" + doubleFormat(W)
            + ",lambda*W:" + doubleFormat(lambda * W));


  }


  public void calculateLittleLaw(long allExecutionTime) {

    L = numTimeAccumulation * 1.0 / allExecutionTime;
    lambda = numOutOfC * 1.0 / allExecutionTime;
    W = sumProcessTime * 1.0 / numOutOfC;
  }
}

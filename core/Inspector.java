package org.anan.project.core;

import org.anan.project.common.InspectorPolicy;
import org.anan.project.utils.EventList.Event;
import org.anan.project.Project;

public abstract class Inspector extends SystemComponentImp {

  boolean isBlocking = false;
  int blockType;
  Event blockingEvent;

  public int blockCount1 = 0;
  public int blockCount2 = 0;
  public int blockCount3 = 0;

  public long blockTimeForStation1 = 0;
  public long blockTimeForStation2 = 0;
  public long blockTimeForStation3 = 0;

  long lastBlockTime = 0;
  public long blockTime = 0;// = blockTimeFor1+blockTimeFor2+blockTimeFor3


  public InspectorPolicy inspectorPolicy;

  public Inspector(String name, Project project) {
    super(name, project);
  }

  //after blocking, trigger by ws
  public void restart() {
    if (supportInterLog) {
      System.out.println(name + " restart");
    }
    isBlocking = false;
    processDeparture(blockingEvent);
    if (!isBlocking) {
      resetAndCountBlockInfo();
    }
  }

  private void resetAndCountBlockInfo() {
    blockingEvent = null;
    long blockTimeDelta = project.currentTime - lastBlockTime;
    blockTime += blockTimeDelta;
    if (blockType == 1) {
      blockTimeForStation1 += blockTimeDelta;
    } else if (blockType == 2) {
      blockTimeForStation2 += blockTimeDelta;
    } else {
      blockTimeForStation3 += blockTimeDelta;
    }
  }

  @Override
  public void processArrival(Event evt) {
    if (isBlocking) {
      restart();
    }
  }


  public void scheduleNewJob() {
    //update L
    project.statisticalData.calculateNumAccumulation(componentNum, project.currentTime);
    project.statisticalData.numInOfC++;

    statisticalData.calculateNumAccumulation(componentNum, project.currentTime);
    statisticalData.numInOfC++;
  }

  @Override
  public void scheduleNewJob(Event evt) {
    throw new RuntimeException("not support");
  }


  @Override
  public void scheduleNewJob(Event event1, Event event2) {

    throw new RuntimeException("not support");
  }

}

package org.anan.project.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;
import org.anan.project.Project;
import org.anan.project.utils.Utils;

public abstract class WorkStation extends SystemComponentImp {

  public boolean useDataFile = false;
  public int dataCount = 0;
  List<Integer> dataList;
  double sigma;

  public int blockCount1 = 0;
  public int blockCount2 = 0;
  public int blockCount3 = 0;

  public long blockTimeForComponent1 = 0;
  public long blockTimeForComponent2 = 0;
  public long blockTimeForComponent3 = 0;
  //  long blockTimeForComponentAll = 0;
  long blockType;
  boolean isBlocking = false;
  long lastBlockTime = 0;

  Queue<Event> queueList1 = new LinkedList<>();
  Queue<Event> queueList2 = new LinkedList<>();
  Queue<Event> queueList3 = new LinkedList<>();


  final static int QueueSize = 2;

  public WorkStation(String name, Project project, double sigma) {
    super(name, project);
    this.sigma = sigma;

  }

  public void loadData(List<Integer> dataList) {
    this.dataList = dataList;
  }


  @Override
  public void processDeparture(Event event2) {
    super.processDeparture(event2);

    //do statistical
    project.statisticalData.calculateNumAccumulation(-componentNum, project.currentTime);
    project.statisticalData.updateSumProcessTimeForParentEvent(event2, project.currentTime);
    project.statisticalData.sumNumInCurrentBatch += componentNum;

    statisticalData.numOutOfC += componentNum;

    for (Event event : ((Event2) event2).getParentEvent()) {
      project.workingTaskMap.remove(event.getId());
      workingTaskMap.remove(event.getId());
    }
  }


  public Integer getNextCount() {
    if (useDataFile) {
      dataCount++;
      if (dataCount >= dataList.size()) {
        dataCount = 0;
      }
      return dataList.get(dataCount);
    } else {
      return Utils.exponential(sigma);
    }

  }

  public void scheduleNewJob() {
    throw new RuntimeException("not support");
  }


}

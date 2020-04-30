package org.anan.project.core;

import java.util.List;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.EventType;
import org.anan.project.Project;
import org.anan.project.utils.Utils;


public class Inspector_1 extends Inspector {

  double sigma_insp1;
  public boolean useDataFile = false;
  public int dataCount = 0;
  List<Integer> dataList;

  public Inspector_1(Project project, double sigma_insp1) {
    super("org.anan.project.system.Inspector_1", project);
    componentNum = 1;
    this.sigma_insp1 = sigma_insp1;
  }

  public void loadData(List<Integer> dataList) {
    this.dataList = dataList;
  }

  public Integer getNextCount() {
    if (useDataFile) {
      dataCount++;
      if (dataCount >= dataList.size()) {
        dataCount = 0;
      }
      return dataList.get(dataCount);
    } else {
      return Utils.exponential(sigma_insp1);
    }
  }

  public WorkStation findNextStation() {
    //find the station with the smallest queue size

    switch (inspectorPolicy) {
      case BASIC:
        //workStation1 has the highest priority
        return workStation1WithHighPriority();

      case SECOND:
        //workStation1 has the lowest priority
        return workStation1WithLowPriority();

      default:
        throw new RuntimeException("not support policy");
    }

  }

  public WorkStation workStation1WithHighPriority() {
    WorkStation targetStation;
    if (project.workStation_1.queueList1.size() <= project.workStation_2.queueList1.size()) {
      targetStation = project.workStation_1;
    } else {
      targetStation = project.workStation_2;
    }

    if (targetStation.queueList1.size() > project.workStation_3.queueList1.size()) {
      targetStation = project.workStation_3;
    }
    return targetStation;
  }

  public WorkStation workStation1WithLowPriority() {
    WorkStation targetStation;
    if (project.workStation_2.queueList1.size() <= project.workStation_3.queueList1.size()) {
      targetStation = project.workStation_2;
    } else {
      targetStation = project.workStation_3;
    }

    if (targetStation.queueList1.size() > project.workStation_1.queueList1.size()) {
      targetStation = project.workStation_1;
    }
    return targetStation;
  }

  @Override
  public void scheduleNewJob() {

    super.scheduleNewJob();

    long count = getNextCount();
    Event event = new Event(EventType.Component1_done, count, project.currentTime);
    project.futureEventList.enqueue(event);
    if (supportInterLog) {
      System.out.println(name + " begins to repair component_1 , count " + count);
    }

    project.workingTaskMap.put(event.getId(), event);
    workingTaskMap.put(event.getId(), event);
  }

  private void setBlockInfo(Event event, WorkStation targetStation) {
    blockCount1++;
    isBlocking = true;
    lastBlockTime = project.currentTime;
    blockingEvent = event;

    int workStationId;
    if (targetStation.name.equals("WorkStation_1")) {
      workStationId = 1;
    } else if (targetStation.name.equals("WorkStation_2")) {
      workStationId = 2;
    } else {
      workStationId = 3;
    }
    blockType = workStationId;
  }

  @Override
  public void processDeparture(Event event) {

    //decide where to send
    WorkStation targetStation = findNextStation();

    //check if queue is blocking
    if (targetStation.queueList1.size() == WorkStation.QueueSize) {
      if (supportInterLog) {
        System.out.println(name + ",there is a block,waiting");
      }
      setBlockInfo(event, targetStation);
      //如果ws端阻塞了这么办？
      //在ws完成任务时，增加对inspect的判断。
    } else {
      super.processDeparture(event);
      workingTaskMap.remove(event.getId());

      //处理，把component发给哪个ws，并且启动新的任务

      if (targetStation.name == "WorkStation_1") {
        project.futureEventList
            .enqueue(event.toInterEvent(EventType.Component1_to_ws1));
      }
      if (targetStation.name == "WorkStation_2") {
        project.futureEventList
            .enqueue(event.toInterEvent(EventType.Component1_to_ws2));
      }
      if (targetStation.name == "WorkStation_3") {
        project.futureEventList
            .enqueue(event.toInterEvent(EventType.Component1_to_ws3));
      }

      scheduleNewJob();
    }


  }


}

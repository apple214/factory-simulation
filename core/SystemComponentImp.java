package org.anan.project.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;
import org.anan.project.Project;
import org.anan.project.common.StatisticalData;


public abstract class SystemComponentImp implements SystemComponent {

  public int productCompleted;
  int numberInService;
  String name;
  Project project;

  int componentNum = 0;
  public StatisticalData statisticalData;
  public boolean supportInterLog = false;

  Map<Long, Event> workingTaskMap = new HashMap<>();

  public SystemComponentImp(String name, Project project ) {
    this.name = name;
    this.project = project;
    this.statisticalData = new StatisticalData();
  }

  public void processArrival(Event evt) {
    if (supportInterLog) {
      System.out.println(name + " receives " + evt.getType());
    }
  }

  public void processDeparture(Event evt) {
    if (supportInterLog) {
      System.out.println(name + " receives " + evt.getType());
    }

    productCompleted++;
    numberInService = 0;

    statisticalData.calculateNumAccumulation(-componentNum, project.currentTime);
    statisticalData.updateSumProcessTime(evt, project.currentTime);
  }

  public void handleUnfinishedTasks(long currentTime) {

    Collection<Event> componentUnFinished = workingTaskMap.values();
//    System.out.println(name + "componentUnFinished.size:" + componentUnFinished.size());
    for (Event event : componentUnFinished) {
      if (event instanceof Event2) {
        statisticalData
            .calculateNumAccumulation(-((Event2) event).getParentEvent().size(), currentTime);
        statisticalData.updateSumProcessTimeForParentEvent(event, currentTime);
      } else {
        statisticalData.calculateNumAccumulation(-1, currentTime);
        statisticalData.updateSumProcessTime(event, currentTime);
      }
    }
  }

}

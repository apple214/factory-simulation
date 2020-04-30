package org.anan.project.core;

import java.util.List;
import java.util.Random;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.EventType;
import org.anan.project.Project;
import org.anan.project.utils.Utils;

public class Inspector_2 extends Inspector {

  double sigma_insp22;
  double sigma_insp23;
  public boolean useDataFile = false;
  public int dataCount = 0;
  List<Integer> dataList22;
  List<Integer> dataList23;

  public Inspector_2(Project project, double sigma_insp22, double sigma_insp23) {
    super("Inspector_2", project);
    componentNum = 1;
    this.sigma_insp22 = sigma_insp22;
    this.sigma_insp23 = sigma_insp23;
  }

  public void loadData(List<Integer> dataList22, List<Integer> dataList23) {
    this.dataList22 = dataList22;
    this.dataList23 = dataList23;
  }

  public Integer getNextCount(int type) {
    if (type == 2) {
      if (useDataFile) {
        dataCount++;
        if (dataCount >= dataList22.size()) {
          dataCount = 0;
        }
        return dataList22.get(dataCount);
      } else {
        return Utils.exponential(sigma_insp22);
      }
    } else {
      if (useDataFile) {
        dataCount++;
        if (dataCount >= dataList23.size()) {
          dataCount = 0;
        }
        return dataList23.get(dataCount);
      } else {
        return Utils.exponential(sigma_insp23);
      }
    }
  }

  public int getNextComponentType() {
    switch (inspectorPolicy) {
      case BASIC:
        return getNextComponentTypeByRandom();

      case SECOND:
        return getNextComponentTypeByQueueSize();

      default:
        throw new RuntimeException("not support policy");
    }
  }

  public int getNextComponentTypeByRandom() {
    int max = 3;
    int min = 2;
    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  public int getNextComponentTypeByQueueSize() {
    return project.workStation_2.queueList1.size() <=
        project.workStation_3.queueList1.size() ? 2 : 3;
  }

  @Override
  public void scheduleNewJob() {

    super.scheduleNewJob();

    int nextComponentType = getNextComponentType();
    long count = getNextCount(nextComponentType);
    Event event;
    if (nextComponentType == 2) {
      event = new Event(EventType.Component2_done, count, project.currentTime);
      if (supportInterLog) {
        System.out.println(name + " begins to repair component_2 , count " + count);
      }
    } else {
      event = new Event(EventType.Component3_done, count, project.currentTime);
      if (supportInterLog) {
        System.out.println(name + " begins to repair component_3 , count " + count);
      }
    }
    project.futureEventList.enqueue(event);
    project.workingTaskMap.put(event.getId(), event);

    workingTaskMap.put(event.getId(), event);
  }

  @Override
  public void processDeparture(Event event) {

    WorkStation_2 workStation2 = project.workStation_2;
    WorkStation_3 workStation3 = project.workStation_3;

    switch (event.getType()) {
      case Component2_done:
        if (workStation2.queueList2.size() == WorkStation.QueueSize) {
          if (supportInterLog) {
            System.out.println(name + ",there is a block,waiting");
          }
          blockCount2++;
          isBlocking = true;
          blockType = 2;
          lastBlockTime = project.currentTime;
          blockingEvent = event;
        } else {
          super.processDeparture(event);
          workingTaskMap.remove(event.getId());
          //send to ws
          project.futureEventList
              .enqueue(event.toInterEvent(EventType.Component2_to_ws2));
          scheduleNewJob();
        }
        break;
      case Component3_done:
        if (workStation3.queueList3.size() == WorkStation.QueueSize) {
          if (supportInterLog) {
            System.out.println(name + ",there is a block,waiting");
          }
          blockCount3++;
          blockType = 3;
          isBlocking = true;
          lastBlockTime = project.currentTime;
          blockingEvent = event;
        } else {
          super.processDeparture(event);
          workingTaskMap.remove(event.getId());
          //send to ws
          project.futureEventList
              .enqueue(event.toInterEvent(EventType.Component3_to_ws3));
          scheduleNewJob();
        }
        break;
      default:
        throw new RuntimeException("wrong event type");
    }

  }

}

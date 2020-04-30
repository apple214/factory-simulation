package org.anan.project.core;

import java.util.ArrayList;
import java.util.List;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;
import org.anan.project.utils.EventList.EventType;
import org.anan.project.Project;

public class WorkStation_3 extends WorkStation {


  public WorkStation_3(Project project, double sigma) {
    super("WorkStation_3", project, sigma);
    componentNum = 2;
  }

  @Override
  public void processArrival(Event evt) {
    super.processArrival(evt);

    switch (evt.getType()) {
      case Component1_to_ws3:
        queueList1.add(evt);
        break;
      case Component3_to_ws3:
        queueList3.add(evt);
        break;
      default:
        throw new RuntimeException("wrong event type");
    }
    if (numberInService == 0) {

      // If it is completed, determine whether the component is ready
      if (queueList1.size() == 0) {
        if (supportInterLog) {
          System.out.println(name + " receive component_3, but has not component_1,waiting");
        }

        isBlocking = true;
        blockCount1++;
        blockType = 1;
        lastBlockTime = project.currentTime;
      }

      if (queueList3.size() == 0) {
        if (supportInterLog) {
          System.out.println(name + " receive component_1, but has not component_3,waiting");
        }

        isBlocking = true;
        blockCount3++;
        blockType = 3;
        lastBlockTime = project.currentTime;
      }

      if (queueList1.size() > 0 && queueList3.size() > 0) {
        Event event1 = queueList1.poll();
        Event event3 = queueList3.poll();
        scheduleNewJob(event1, event3);

        if (isBlocking) {
          if (blockType == 1) {
            blockTimeForComponent1 += project.currentTime - lastBlockTime;
          } else {
            blockTimeForComponent3 += project.currentTime - lastBlockTime;
          }
        }

        isBlocking = false;
      }
    } else {
      //wait

    }
  }


  @Override
  public void scheduleNewJob(Event event) {
    throw new RuntimeException("not support");
  }

  @Override
  public void scheduleNewJob(Event event1, Event event2) {

    statisticalData.calculateNumAccumulation(componentNum, project.currentTime);
    statisticalData.numInOfC += componentNum;

    numberInService = 1;
    int count = getNextCount();

    List<Event> parentEvent = new ArrayList<>();
    parentEvent.add(event1);
    parentEvent.add(event2);
    project.futureEventList
        .enqueue(new Event2(EventType.Product3_done, count, parentEvent, project.currentTime));
    if (supportInterLog) {
      System.out.println(name + " begin new Product3,count " + count);
    }
  }

  @Override
  public void processDeparture(Event event2) {
    //process event
    super.processDeparture(event2);

    if (queueList1.size() > 0 && queueList3.size() > 0) {
      Event event1 = queueList1.poll();
      Event event3 = queueList3.poll();
      scheduleNewJob(event1, event3);
    }

  }
}

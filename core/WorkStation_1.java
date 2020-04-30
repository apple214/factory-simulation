package org.anan.project.core;

import java.util.ArrayList;
import java.util.List;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;
import org.anan.project.utils.EventList.EventType;
import org.anan.project.Project;

public class WorkStation_1 extends WorkStation {


  public WorkStation_1(Project project, double sigma) {
    super("WorkStation_1", project, sigma);
    componentNum = 1;
  }

  @Override
  public void processArrival(Event evt) {
    super.processArrival(evt);

    queueList1.add(evt);
    if (numberInService == 0) {
      queueList1.poll();
      scheduleNewJob(evt);
    }
    //else wait
  }


  @Override
  public void scheduleNewJob(Event event) {

    statisticalData.calculateNumAccumulation(componentNum, project.currentTime);
    statisticalData.numInOfC += componentNum;

    numberInService = 1;
    int count = getNextCount();
    List<Event> parentEvent = new ArrayList<>();
    parentEvent.add(event);
    project.futureEventList
        .enqueue(new Event2(EventType.Product1_done, count, parentEvent, project.currentTime));
    if (supportInterLog) {
      System.out.println(name + " begin new Product1,count " + count);
    }
  }

  @Override
  public void scheduleNewJob(Event event1, Event event2) {

    throw new RuntimeException("not support");
  }

  @Override
  public void processDeparture(Event event2) {

    //do process
    super.processDeparture(event2);

    if (queueList1.size() > 0) {
      Event event1 = queueList1.poll();
      scheduleNewJob(event1);
    }
  }

}

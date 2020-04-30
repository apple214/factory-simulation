package org.anan.project.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventList {

  BlockingQueue<Event> queue = new LinkedBlockingQueue<>();
  //Used to record which tasks are waiting
  List<Event> waitingList = new ArrayList<>();//用于记录哪些task在等待
  ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

  public Event getMin() throws Exception {
    Event event = queue.take();
    waitingList.remove(event);
    return event;
  }

  public List<Event> getWaitingList() {
    return waitingList;
  }

  public void enqueue(Event ev) {
    waitingList.add(ev);
    Runnable runnable = () -> {
      queue.add(ev);
    };
    service.schedule(runnable, (long) ev.getProcessTime(), TimeUnit.MILLISECONDS);
  }


  public void dequeue() {
    //do nothing
  }


  public enum EventType {
    Component1_done,
    Component2_done,
    Component3_done,
    Component1_to_ws1,
    Component1_to_ws2,
    Component1_to_ws3,
    Component2_to_ws2,
    Component3_to_ws3,
    Product1_done,
    Product2_done,
    Product3_done;
  }

  //用于product的事件,封装Event
  //Event for product, encapsulates Event
  public static class Event2 extends Event {

    private List<Event> parentEvent;

    public Event2(EventType type, double processTime, List<Event> parentEvent, double startTime) {
      super(type, processTime, startTime);
      this.parentEvent = parentEvent;
    }

    public List<Event> getParentEvent() {
      return parentEvent;
    }
  }


  public static class Event {

    private long id;

    private EventType type;

    private double processTime;

    private double startTime;


    public Event(EventType type, double processTime, double startTime) {
      this.id = new Random().nextLong();
      this.type = type;
      this.processTime = processTime;
      this.startTime = startTime;
    }

    public Event toInterEvent(EventType type) {
      this.type = type;
      this.processTime = 0;
      return this;
    }


    public Long getId() {
      return id;
    }

    public EventType getType() {
      return type;
    }


    public double getProcessTime() {
      return processTime;
    }


    public double getStartTime() {
      return startTime;
    }

  }
}

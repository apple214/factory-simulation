package org.anan.project.core;

import org.anan.project.utils.EventList.Event;

public interface SystemComponent {

  void processArrival(Event evt);

  void scheduleNewJob();

  void scheduleNewJob(Event evt);

  void scheduleNewJob(Event evt1,Event evt2);

  void processDeparture(Event e);
}

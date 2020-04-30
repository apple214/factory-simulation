package org.anan.project;

import static org.anan.project.utils.Utils.doubleFormat;
import static org.anan.project.utils.Utils.doubleListFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.anan.project.common.InspectorPolicy;
import org.anan.project.common.StatisticalData;
import org.anan.project.utils.EventList;
import org.anan.project.utils.EventList.Event;
import org.anan.project.utils.EventList.Event2;
import org.anan.project.core.Inspector_1;
import org.anan.project.core.Inspector_2;
import org.anan.project.core.WorkStation_1;
import org.anan.project.core.WorkStation_2;
import org.anan.project.core.WorkStation_3;

public class Project {

  public WorkStation_1 workStation_1;
  public WorkStation_2 workStation_2;
  public WorkStation_3 workStation_3;

  public Inspector_1 inspector_1;
  public Inspector_2 inspector_2;

  public EventList futureEventList;
  public Map<Long, Event> workingTaskMap = new HashMap<>();

  public long allExecutionTime = 0;
  public long currentTime = 0;

  public StatisticalData statisticalData;


  public Project() {
  }


  public void init(long timeMax, boolean supportSubSystemInterLog,
      InspectorPolicy inspectorPolicy1, InspectorPolicy inspectorPolicy2) {

    //init inner object
    futureEventList = new EventList();

    workStation_1 = new WorkStation_1(this, 4.60);
    workStation_2 = new WorkStation_2(this, 11.09);
    workStation_3 = new WorkStation_3(this, 8.79);
    inspector_1 = new Inspector_1(this, 10.35);
    inspector_2 = new Inspector_2(this, 15.53, 20.63);

    statisticalData = new StatisticalData();

    //Set execution time
    this.allExecutionTime = timeMax;//processing time，
    this.inspector_1.inspectorPolicy = inspectorPolicy1;
    this.inspector_2.inspectorPolicy = inspectorPolicy2;

    //Initialization start time
//    currentTime = System.nanoTime() / 1000;//initial start time，
    currentTime = System.currentTimeMillis();//initial start time，
    statisticalData.lastProcessTime = currentTime;
    inspector_1.statisticalData.lastProcessTime = currentTime;
    inspector_2.statisticalData.lastProcessTime = currentTime;
    workStation_1.statisticalData.lastProcessTime = currentTime;
    workStation_2.statisticalData.lastProcessTime = currentTime;
    workStation_3.statisticalData.lastProcessTime = currentTime;

    statisticalData.timeIntervalInBatch = 100;
//    statisticalData.supportLittleLawLog = true;
//    inspector_1.statisticalData.supportLog = true;
//    inspector_2.statisticalData.supportLog = true;
//    workStation_1.statisticalData.supportLog = true;
//    workStation_2.statisticalData.supportLog = true;
//    workStation_3.statisticalData.supportLog = true;

    if (supportSubSystemInterLog) {
      inspector_1.supportInterLog = true;
      inspector_2.supportInterLog = true;
      workStation_1.supportInterLog = true;
      workStation_2.supportInterLog = true;
      workStation_3.supportInterLog = true;
    }

    //start inspector
    inspector_1.scheduleNewJob();
    inspector_2.scheduleNewJob();
  }


  public void loadData(boolean useDataFile) {
    workStation_1.loadData(DatReader.getTimeArr("files/5001/Project/generatedData/ws1.dat"));
    workStation_1.useDataFile = useDataFile;
    workStation_2.loadData(DatReader.getTimeArr("files/5001/Project/generatedData/ws2.dat"));
    workStation_2.useDataFile = useDataFile;
    workStation_3.loadData(DatReader.getTimeArr("files/5001/Project/generatedData/ws3.dat"));
    workStation_3.useDataFile = useDataFile;
    inspector_1.loadData(DatReader.getTimeArr("files/5001/Project/generatedData/servinsp1.dat"));
    inspector_1.useDataFile = useDataFile;
    inspector_2.loadData(DatReader.getTimeArr("files/5001/Project/generatedData/servinsp22.dat"),
        DatReader.getTimeArr("files/5001/Project/generatedData/servinsp23.dat"));
    inspector_2.useDataFile = useDataFile;
  }

  public void setDataStart(int start) {
    workStation_1.dataCount = start;
    workStation_2.dataCount = start;
    workStation_3.dataCount = start;
    inspector_1.dataCount = start;
    inspector_2.dataCount = start;
  }

  public void doWork() throws Exception {

    long startTime = currentTime;

    //used to count the amount of completion in each batch time
    statisticalData.timeInCurrentBatch = startTime;

    while (currentTime < startTime + allExecutionTime) {

      Event event = futureEventList.getMin();
      switch (event.getType()) {
        case Component1_done:
          inspector_1.processDeparture(event);
          break;
        case Component2_done:
        case Component3_done:
          inspector_2.processDeparture(event);
          break;
        case Component1_to_ws1:
          workStation_1.processArrival(event);
          break;
        case Component1_to_ws2:
        case Component2_to_ws2:
          workStation_2.processArrival(event);
          break;
        case Component1_to_ws3:
        case Component3_to_ws3:
          workStation_3.processArrival(event);
          break;
        case Product1_done:
          workStation_1.processDeparture(event);
          inspector_1.processArrival(event);
          break;
        case Product2_done:
          workStation_2.processDeparture(event);
          inspector_1.processArrival(event);
          inspector_2.processArrival(event);
          break;
        case Product3_done:
          workStation_3.processDeparture(event);
          inspector_1.processArrival(event);
          inspector_2.processArrival(event);
          break;
        default:
          throw new RuntimeException("get wrong event type");

      }
      currentTime = System.currentTimeMillis();

      if (currentTime - statisticalData.timeInCurrentBatch > statisticalData.timeIntervalInBatch) {
        statisticalData.updateSumNumList(currentTime);
      }
    }

//    System.out.println("\n");
//    System.out.println("system is stopping");

    handleUnfinishedTasks();

  }

  //deal with un-finished
//  Handling unfinished tasks
  public void handleUnfinishedTasks() {
    Collection<Event> componentUnFinished = workingTaskMap.values();
//    System.out.println("system componentUnFinished.size:" + componentUnFinished.size());
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

    inspector_1.handleUnfinishedTasks(currentTime);
    inspector_2.handleUnfinishedTasks(currentTime);
    workStation_1.handleUnfinishedTasks(currentTime);
    workStation_2.handleUnfinishedTasks(currentTime);
    workStation_3.handleUnfinishedTasks(currentTime);
  }

  public void reportGeneration() {

    System.out.println("done!");
    System.out.println("product1:" + workStation_1.productCompleted +
        ",product2:" + workStation_2.productCompleted +
        ",product3:" + workStation_3.productCompleted);

    System.out.println("component num out:" + statisticalData.numOutOfC);
    System.out.println(
        "component num out per period:" + statisticalData.numOutOfC * 1.0 / allExecutionTime);

    printBlockInfo();
//    printBlockCount();

//    System.out.println("sumNumEvenListInBatches:" + (statisticalData.sumNumEvenListInBatches));

//    generateAndPrintLittleLawData();

  }

  private void printBlockInfo() {

    System.out.println("");
    System.out.println("blockTime");

    //inspector_1
    System.out.println("");
    System.out.println("inspector_1.blockTime all:" + inspector_1.blockTime);
    System.out.println(
        "inspector_1.blockTime per period:" + inspector_1.blockTime * 1.0 / allExecutionTime);
    System.out.println("inspector_1.blockTimeForStation1:" + inspector_1.blockTimeForStation1);
    System.out.println("inspector_1.blockTimeForStation2:" + inspector_1.blockTimeForStation2);
    System.out.println("inspector_1.blockTimeForStation3:" + inspector_1.blockTimeForStation3);

    //inspector_2
    System.out.println("");
    System.out.println("inspector_2.blockTime all:" + inspector_2.blockTime);
    System.out.println("inspector_2.blockTimeForStation2:" + inspector_2.blockTimeForStation2);
    System.out.println("inspector_2.blockTimeForStation3:" + inspector_2.blockTimeForStation3);
    System.out.println(
        "inspector_2.blockTime per period:" + inspector_2.blockTime * 1.0 / allExecutionTime);

    //workStation_1
    System.out.println("");
    System.out
        .println("workStation_1.blockTimeForComponent1:" + workStation_1.blockTimeForComponent1);
    System.out.println("workStation_1.blockTimeForComponent1 per period:"
        + workStation_1.blockTimeForComponent1 * 1.0 / allExecutionTime);

    //workStation_2
    System.out.println("");
    System.out
        .println("workStation_2.blockTimeForComponent1:" + workStation_2.blockTimeForComponent1);
    System.out
        .println("workStation_2.blockTimeForComponent2:" + workStation_2.blockTimeForComponent2);
    System.out.println("workStation_2.blockTimeForComponent1 per period:"
        + workStation_2.blockTimeForComponent1 * 1.0 / allExecutionTime);
    System.out.println("workStation_2.blockTimeForComponent2 per period:"
        + workStation_2.blockTimeForComponent2 * 1.0 / allExecutionTime);

    //workStation_3
    System.out.println("");
    System.out
        .println("workStation_3.blockTimeForComponent1:" + workStation_3.blockTimeForComponent1);
    System.out
        .println("workStation_3.blockTimeForComponent3:" + workStation_3.blockTimeForComponent3);
    System.out.println("workStation_3.blockTimeForComponent1 per period:"
        + workStation_3.blockTimeForComponent1 * 1.0 / allExecutionTime);
    System.out.println("workStation_3.blockTimeForComponent3 per period:"
        + workStation_3.blockTimeForComponent3 * 1.0 / allExecutionTime);

  }

  //not a formal evaluation parameter
  private void printBlockCount() {
    System.out.println("");
    System.out.println("block count");
//    inspector's blocking info,
    System.out.println("inspector_1 for workStation_1:" + inspector_1.blockCount1);
    System.out.println("inspector_1 for workStation_2:" + inspector_1.blockCount2);
    System.out.println("inspector_1 for workStation_3:" + inspector_1.blockCount3);
    System.out.println("inspector_2 for workStation_2:" + inspector_2.blockCount2);
    System.out.println("inspector_2 for workStation_3:" + inspector_2.blockCount3);

    System.out.println("");
//    workStation 's blocking info
    System.out.println("workStation_1 for component1:" + workStation_1.blockCount1);
    System.out.println("workStation_2 for component1:" + workStation_2.blockCount1);
    System.out.println("workStation_2 for component2:" + workStation_2.blockCount2);
    System.out.println("workStation_3 for component1:" + workStation_3.blockCount1);
    System.out.println("workStation_3 for component3:" + workStation_3.blockCount3);
  }

  private void generateAndPrintLittleLawData() {

    System.out.println("\n");
    System.out.println("little'law:");
    System.out.println("all system:");
    statisticalData.reportGeneration();

    System.out.println("inspector_1:");
    inspector_1.statisticalData.reportGeneration();
    System.out.println("inspector_2:");
    inspector_2.statisticalData.reportGeneration();
    System.out.println("workStation_1:");
    workStation_1.statisticalData.reportGeneration();
    System.out.println("workStation_2:");
    workStation_2.statisticalData.reportGeneration();
    System.out.println("workStation_3:");
    workStation_3.statisticalData.reportGeneration();
  }


  public void calculateStatisticalData() {
    //calculate L
    statisticalData.numOutOfC = workStation_1.productCompleted + workStation_2.productCompleted
        + workStation_3.productCompleted + workStation_2.productCompleted
        + workStation_3.productCompleted;

    statisticalData.calculateLittleLaw(allExecutionTime);

    inspector_1.statisticalData.numOutOfC = inspector_1.productCompleted;
    inspector_1.statisticalData.calculateLittleLaw(allExecutionTime);

    inspector_2.statisticalData.numOutOfC = inspector_2.productCompleted;
    inspector_2.statisticalData.calculateLittleLaw(allExecutionTime);

    workStation_1.statisticalData.calculateLittleLaw(allExecutionTime);
    workStation_2.statisticalData.calculateLittleLaw(allExecutionTime);
    workStation_3.statisticalData.calculateLittleLaw(allExecutionTime);
  }

  private static void performOneTask(long timeMax) throws Exception {
    Project project = new Project();
    project.init(timeMax, false, InspectorPolicy.SECOND, InspectorPolicy.BASIC);
//    project.loadData(true);
    project.doWork();

    project.calculateStatisticalData();
    project.reportGeneration();

  }

  private static void performMultipleTask(int replication, long timeMax) throws Exception {

    List<Long> numOutList = new ArrayList<>();//record the results
    List<Double> numOutListEven = new ArrayList<>();

    List<Long> blockTimeListInspect2 = new ArrayList<>();
    List<Double> blockTimeListEvenInspect2 = new ArrayList<>();

    int count = 0;
    for (int i = 0; i < replication; i++) {
      Project project = new Project();

      project.init(timeMax, false, InspectorPolicy.BASIC, InspectorPolicy.BASIC);
//      project.loadData(true);//use DataFile
      project.setDataStart(count);
      count += 500;

      project.doWork();

      project.calculateStatisticalData();
//      project.reportGeneration();

      //to record result
      numOutList.add(project.statisticalData.numOutOfC);
      numOutListEven.add(project.statisticalData.numOutOfC * 1.0 / timeMax);
      blockTimeListInspect2.add(project.inspector_2.blockTime);
      blockTimeListEvenInspect2.add((project.inspector_2.blockTime) * 1.0 / timeMax);

      if (i % 1 == 0) {
        System.out.println("i:" + i);
      }
    }

    //to print final result
    System.out.println("numOutList:" + numOutList);
    System.out.println("numOutListEven:" + doubleListFormat(numOutListEven));
    System.out.println("blockTimeListInspect2:" + blockTimeListInspect2);
    System.out.println(
        "blockTimeListEvenInspect2:" + doubleListFormat(blockTimeListEvenInspect2, "0.000"));

  }

  public static void main(String[] args) throws Exception {

    long timeMax = 3 * 1000;
    int replication = 100;
    performOneTask(timeMax);
//    performMultipleTask(replication, timeMax);

    System.exit(1);
  }


}

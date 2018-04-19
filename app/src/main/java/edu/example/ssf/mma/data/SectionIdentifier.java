package edu.example.ssf.mma.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SectionIdentifier {
    private static Section currentSection;
    private static ArrayList<TickData> curveTicks;
    private static boolean curveStarted = false;
    private static final float CURVETHRESHOLD = 1.8f;
    private static final int MINCURVEPOINTS = 4;
    private static final int NUMBEROFPOINTSCONSIDERED = 4;
    private static boolean firstSection= false;


    public static ArrayList<Section> identifySections(ArrayList<TickData> data){
        curveStarted = false;
        firstSection = true;
        ArrayList<Section> sections = new ArrayList<>();
        currentSection = new Section();
        currentSection.setStart(data.get(0));
        currentSection.setMedian(new TickData());
        currentSection.setType(Section.SectionType.STRAIGHT);
        for (TickData tickData : data) {
            if(CheckNextPointsGREATERThanThreshold(data,tickData,CURVETHRESHOLD,NUMBEROFPOINTSCONSIDERED) && !curveStarted && currentSection.getType() == Section.SectionType.STRAIGHT){
                curveStarted = true;
                curveTicks = new ArrayList<>();
                currentSection.setEnd(data.get(data.indexOf(tickData)));
                currentSection.setTimeTaken();
                sections.add(currentSection);
                currentSection = new Section();
                currentSection.setStart(data.get(data.indexOf(tickData)));
            } else if(CheckNextPointsSMALLERThanThreshold(data,tickData,CURVETHRESHOLD,NUMBEROFPOINTSCONSIDERED) && curveStarted && currentSection.getType() == Section.SectionType.UNDEFINED){
                curveStarted = false;
                currentSection.setEnd(data.get(data.indexOf(tickData)));
                curveTicks.add(data.get(data.indexOf(tickData)));
                currentSection.setTimeTaken();
                TickData median = calculateMedian(curveTicks);
                currentSection.setMedian(median);
                if(curveTicks.size()<MINCURVEPOINTS){
                    currentSection.setType(Section.SectionType.INVALID);
                } else{
                    currentSection.calculateCurveType();
                }
                sections.add(currentSection);
                currentSection = new Section();
                currentSection.setStart(data.get(data.indexOf(tickData)));
                currentSection.setType(Section.SectionType.STRAIGHT);
            }
            if(curveStarted && tickData.getAccX() >= CURVETHRESHOLD){
                curveTicks.add(tickData);
            }
        }
        return sections;
    }

    private static TickData calculateMedian(ArrayList<TickData> curveData){
        TickData accumulatedTickData = new TickData();
        TickData result = new TickData();

        for (TickData data : curveData) {
            accumulatedTickData.setAccX(accumulatedTickData.getAccX()+data.getAccX());
            accumulatedTickData.setAccY(accumulatedTickData.getAccY()+data.getAccY());
        }

        result.setTimeStamp((curveData.get(0).getTimeStamp()+curveData.get(curveData.size()-1).getTimeStamp())/2);
        result.setAccX(accumulatedTickData.getAccX()/curveData.size());
        result.setAccY(accumulatedTickData.getAccY()/curveData.size());

        return result;
    }

    private static boolean CheckNextPointsGREATERThanThreshold(ArrayList<TickData> dataSet, TickData origin, float threshold, int checkRange){
        int originIndex = dataSet.indexOf(origin);
        for (int i = 0; i < checkRange; i++) {
            if(dataSet.get(originIndex+i).getAccX()<threshold);
             return false;
        }
        return true;
    }
    private static boolean CheckNextPointsSMALLERThanThreshold(ArrayList<TickData> dataSet, TickData origin, float threshold, int checkRange){
        int originIndex = dataSet.indexOf(origin);
        for (int i = 0; i < checkRange; i++) {
            if(dataSet.get(originIndex+i).getAccX()>threshold);
             return false;
        }
        return true;
    }
}

package com.example.myapplication;

import java.util.List;

public class TrainModel {
    private String trainName;
    private String platform;
    private String arrivalTime;
    private String delay;
    private String source;
    private String destination;
    private List<String> stops;
    private List<String> coachSequence;

    public TrainModel(String trainName, String platform, String arrivalTime, String delay, String source, String destination, List<String> stops, List<String> coachSequence) {
        this.trainName = trainName;
        this.platform = platform;
        this.arrivalTime = arrivalTime;
        this.delay = delay;
        this.source = source;
        this.destination = destination;
        this.stops = stops;
        this.coachSequence = coachSequence;
    }

    public String getTrainName() { return trainName; }
    public String getPlatform() { return platform; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDelay() { return delay; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public List<String> getStops() { return stops; }
    public List<String> getCoachSequence() { return coachSequence; }
}

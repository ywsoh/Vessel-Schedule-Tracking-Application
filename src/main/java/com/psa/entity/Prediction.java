package com.psa.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.json.JSONObject;

@Entity
@Table(name = "berthing_prediction")
public class Prediction {

    public enum SpeedChange {
        INCREASED, DECREASED, UNCHANGED
    }

    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Prediction() {
    }

    public Prediction(boolean isPatching, String predBerthing, double distance, double maxSpeed, double avgSpeed) {
        this.isPatching = isPatching;
        this.predBerthing = LocalDateTime.parse(predBerthing, DATEFORMAT);
        this.distance = distance;
        this.maxSpeed = maxSpeed;
        SpeedRecord sr = new SpeedRecord(avgSpeed);
        sr.setPrediction(this);
        speedRecords.add(sr);
    }

    @Id
    private int id;

    @Column(name = "patching_activated")
    private boolean isPatching;

    @Column(name = "predicted_berthing_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime predBerthing;

    @OneToMany(mappedBy = "prediction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SpeedRecord> speedRecords = new ArrayList<SpeedRecord>();

    private double distance;
    private double maxSpeed;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Vessel vessel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPatching() {
        return isPatching;
    }

    public void setPatching(boolean isPatching) {
        this.isPatching = isPatching;
    }

    public LocalDateTime getPredBerthing() {
        return predBerthing;
    }

    public void setPredBerthing(LocalDateTime predBerthing) {
        this.predBerthing = predBerthing;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public List<SpeedRecord> getSpeedRecords() {
        return speedRecords;
    }

    public void setSpeedRecords(List<SpeedRecord> speedRecords) {
        this.speedRecords = speedRecords;
    }

    /**
     * Sort SpeedRecords List in Desc order by TimeStamp
     */
    public void sortByTimestamp() {
        Collections.sort(speedRecords, new Comparator<SpeedRecord>() {
            @Override
            public int compare(SpeedRecord sr1, SpeedRecord sr2) {
                return sr1.getTimestamp().compareTo(sr2.getTimestamp()) * -1;
            }
        });
    }

    /**
     * Get latest average speed
     * 
     * @return speed record
     */
    public double getLatestSpeedRecord() {
        return speedRecords.get(0).getAverageSpeed();
    }

    /**
     * Determine if there has been an INCREASED, DECREASED or UNCHANGED in speed
     * 
     * @return enum of INCREASED, DECREASED or UNCHANGED
     */
    public SpeedChange isSpeedIncrease() {
        int recordSize = speedRecords.size();
        if (recordSize == 1) {
            return SpeedChange.UNCHANGED;
        } else {
            SpeedRecord latest = speedRecords.get(0);
            SpeedRecord previous = speedRecords.get(1);

            if (latest.equals(previous)) {
                return SpeedChange.UNCHANGED;
            } else if (latest.isFaster(previous)) {
                return SpeedChange.INCREASED;
            } else {
                return SpeedChange.DECREASED;
            }
        }
    }

    /**
     * Determine if the predicted time is after the expected time
     * 
     * @param time Time vessel is expeected to berth
     * @return Boolean of whether the vessel is late
     */
    public boolean isLate(LocalDateTime time) {
        return time.isBefore(predBerthing);
    }

    public static Prediction getPredictionFromJsonObj(JSONObject jsonObject) {
        boolean isPatching = jsonObject.getInt("IS_PATCHING_ACTIVATED") == 1 ? true : false;
        String predBerthing;
        if (isPatching) {
            predBerthing = jsonObject.getString("PATCHING_PREDICTED_BTR");
        } else {
            predBerthing = jsonObject.getString("PREDICTED_BTR");
        }
        return new Prediction(isPatching, predBerthing, jsonObject.getDouble("DISTANCE_TO_GO"),
                jsonObject.getDouble("MAX_SPEED"), jsonObject.getDouble("AVG_SPEED"));
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }
}

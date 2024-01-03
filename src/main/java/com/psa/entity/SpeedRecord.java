package com.psa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "speed_record")
public class SpeedRecord implements Serializable {

    private static final long serialVersionUID = 3556921139513066019L;

    public SpeedRecord() {
    }

    public SpeedRecord(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    @Id
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="prediction_id", referencedColumnName = "id")
    private Prediction prediction;

    @Column(name = "average_speed", nullable = false)
    private double averageSpeed;

    @Column(name = "timestamp", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }

    /**
     * Determine if the current speed is faster than the previous speed
     * @param speedRecord SpeedRecord Object of the previous SpeedRecord
     * @return Boolean value if current speed is faster than the previous speed
     */
    public boolean isFaster(SpeedRecord speedRecord) {
        return this.averageSpeed > speedRecord.averageSpeed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpeedRecord)) {
            return false;
        }
        SpeedRecord other = (SpeedRecord) obj;
        return this.averageSpeed == other.averageSpeed;
    }
}

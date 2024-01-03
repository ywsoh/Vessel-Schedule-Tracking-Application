package com.psa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "alert")
public class Alert implements Serializable{

    private static final long serialVersionUID = 6920248057508164881L;
    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany
    List<User> subscribed;

    @OneToOne
    @JoinColumn(name = "vessel_id", referencedColumnName = "id")
    private Vessel vessel;

    @Column(name = "predicted_time")
    private LocalDateTime predictedTime;

    @Column(name = "timestamp", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "type", nullable = false)
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public LocalDateTime getPredictedTime() {
        return predictedTime;
    }

    public void setPredictedTime(LocalDateTime predictedTime) {
        this.predictedTime = predictedTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        
        if (type.equals("BD")) {
        return "\n\t\tBerthing - The ship is predicted to berth at " +  predictedTime.format(TIMEFORMAT) + " on " + predictedTime.format(DATEFORMAT);
        } else if (type.equals("UBD")) {
            return "\n\t\tUnberthing - The ship is predicted to unberth at " +  predictedTime.format(TIMEFORMAT) + " on " + predictedTime.format(DATEFORMAT);
        } else if (type.equals("status")) {
            return "\n\t\tStatus Changed - The ship is " + vessel.getStatus() + " the berth at " +  predictedTime.format(TIMEFORMAT) + " on " + predictedTime.format(DATEFORMAT);
        } else if (type.equals("PB")) {
            return "\n\t\tPredicted Berthing Time Changed -  The ship is predicted to berth at " +  predictedTime.format(TIMEFORMAT) + " on " + predictedTime.format(DATEFORMAT);
        } else if (type.equals("Late")) {
            return "\n\t\t<span style='color:#FF0000'>Late</span> -  The ship is predicted to berth at " +  predictedTime.format(TIMEFORMAT) + " on " + predictedTime.format(DATEFORMAT);
        }
        return null;
    }

}

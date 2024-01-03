package com.psa.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.json.JSONArray;
import org.json.JSONObject;

@Entity
@Table(name = "vessel_record")
public class Vessel {

    public Vessel() {
    }

    public Vessel(String fullName, String abbrName, String fullInVoyNo, String inVoyNo, String fullOutVoyNo,
            String outVoyNo, String berthingDate, String unBerthingDate, String berthNo, String status,
            String shiftSeqNo) {
        this.fullName = fullName;
        this.abbrName = abbrName;
        this.fullInVoyNo = fullInVoyNo;
        this.inVoyNo = inVoyNo;
        this.fullOutVoyNo = fullOutVoyNo;
        this.outVoyNo = outVoyNo;
        this.berthingDate = LocalDateTime.parse(berthingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.unBerthingDate = LocalDateTime.parse(unBerthingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.berthNo = berthNo;
        this.status = status;
        this.shiftSeqNo = Integer.parseInt(shiftSeqNo);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "abbreviated_name", nullable = false)
    private String abbrName;

    @Column(name = "full_in_voyage_no")
    private String fullInVoyNo;

    @Column(name = "in_voyage_no", nullable = false)
    private String inVoyNo;

    @Column(name = "full_out_voyage_no")
    private String fullOutVoyNo;

    @Column(name = "out_voyage_no", nullable = false)
    private String outVoyNo;

    @Column(name = "berthing_date", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime berthingDate;

    @Column(name = "unberthing_date", nullable = false)
    private LocalDateTime unBerthingDate;

    @Column(name = "berth_no")
    private String berthNo;

    @OneToOne(mappedBy = "vessel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
    @PrimaryKeyJoinColumn
    private Prediction prediction;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "shift_sequence_no", nullable = false)
    private int shiftSeqNo;

    @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // @JoinTable(name = "subscription", 
    //         inverseJoinColumns = @JoinColumn(name = "user_id"), 
    //         joinColumns = @JoinColumn(name = "vessel_id", referencedColumnName = "id"))
    Set<User> subscribers = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAbbrName() {
        return abbrName;
    }

    public void setAbbrName(String abbrName) {
        this.abbrName = abbrName;
    }

    public String getFullInVoyNo() {
        return fullInVoyNo;
    }

    public void setFullInVoyNo(String fullInVoyNo) {
        this.fullInVoyNo = fullInVoyNo;
    }

    public String getInVoyNo() {
        return inVoyNo;
    }

    public void setInVoyNo(String inVoyNo) {
        this.inVoyNo = inVoyNo;
    }

    public LocalDateTime getBerthingDate() {
        return berthingDate;
    }

    public void setBerthingDate(LocalDateTime berthingDate) {
        this.berthingDate = berthingDate;
    }

    public LocalDateTime getUnBerthingDate() {
        return unBerthingDate;
    }

    public void setUnBerthingDate(LocalDateTime unBerthingDate) {
        this.unBerthingDate = unBerthingDate;
    }

    public String getBerthNo() {
        return berthNo;
    }

    public void setBerthNo(String berthNo) {
        this.berthNo = berthNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }

    public String getFullOutVoyNo() {
        return fullOutVoyNo;
    }

    public void setFullOutVoyNo(String fullOutVoyNo) {
        this.fullOutVoyNo = fullOutVoyNo;
    }

    public String getOutVoyNo() {
        return outVoyNo;
    }

    public void setOutVoyNo(String outVoyNo) {
        this.outVoyNo = outVoyNo;
    }

    public int getShiftSeqNo() {
        return shiftSeqNo;
    }

    public void setShiftSeqNo(int shiftSeqNo) {
        this.shiftSeqNo = shiftSeqNo;
    }

    public String getVslVoy() {
        return (fullName + inVoyNo).replace(" ", "").replace("/", "");
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vessel other = (Vessel) obj;
        if (id != other.id)
            return false;
        return true;
    }

    /**
     * Returns a List of Vessels from a JSONArray input
     * 
     * @param jsonArray A JSONArray containing result from the
     *                  retrieveByBerthingDate API
     * @return List of Vessels
     */
    public static List<Vessel> getVesselListFromJsonArr(JSONArray jsonArray) {
        List<Vessel> vesselList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject vessel = jsonArray.getJSONObject(i);

            String berthN = vessel.isNull("berthN") ? null : vessel.getString("berthN");
            String fullInVoyN = vessel.isNull("fullInVoyN") ? null : vessel.getString("fullInVoyN");
            String fullOutVoyN = vessel.isNull("fullOutVoyN") ? null : vessel.getString("fullOutVoyN");

            vesselList.add(new Vessel(vessel.getString("fullVslM"), vessel.getString("abbrVslM"), fullInVoyN,
                    vessel.getString("inVoyN"), fullOutVoyN, vessel.getString("outVoyN"), vessel.getString("bthgDt"),
                    vessel.getString("unbthgDt"), berthN, vessel.getString("status"), vessel.getString("shiftSeqN")));
        }
        return vesselList;
    }
}

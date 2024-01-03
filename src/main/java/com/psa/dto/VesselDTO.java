package com.psa.dto;

import java.time.format.DateTimeFormatter;

import com.psa.entity.Prediction;
import com.psa.entity.Vessel;
import com.psa.entity.Prediction.SpeedChange;

public class VesselDTO {
	private int id;
	private String vesselName;
	private String inVoyNo;
	private String outVoyNo;
	private String berthingTime;
	private String depatureTime;
	private String berthNo;
	private String status;
	private int shiftSeqNo;

	private String avgSpeed;
	private String maxSpeed;
	private String distance;
	private String predictedTime;
	private boolean isLate;
	private SpeedChange isSpeedIncrease;

	private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	/**
	 * Encapsulates Vessel with multiple inheritance to a single Object
	 * 
	 * @param vessel a Vessel object
	 * @return a VesselDTO
	 */
	public static VesselDTO convertToVesselDTO(Vessel vessel) {
		VesselDTO vesselDTO = new VesselDTO();
		vesselDTO.id = vessel.getId();
		vesselDTO.vesselName = vessel.getFullName();
		vesselDTO.inVoyNo = vessel.getInVoyNo();
		vesselDTO.outVoyNo = vessel.getOutVoyNo();
		vesselDTO.berthingTime = vessel.getBerthingDate().format(DATEFORMAT);
		vesselDTO.depatureTime = vessel.getUnBerthingDate().format(DATEFORMAT);
		vesselDTO.berthNo = vessel.getBerthNo();
		vesselDTO.status = vessel.getStatus();
		vesselDTO.shiftSeqNo = vessel.getShiftSeqNo();

		Prediction prediction = vessel.getPrediction();
		if (prediction != null) {
			prediction.sortByTimestamp();
			vesselDTO.maxSpeed = prediction.getMaxSpeed() + "";
			vesselDTO.distance = prediction.getDistance() + "";
			vesselDTO.avgSpeed = prediction.getLatestSpeedRecord() + "";
			vesselDTO.isSpeedIncrease = prediction.isSpeedIncrease();
			vesselDTO.predictedTime = prediction.getPredBerthing().format(DATEFORMAT);
			vesselDTO.isLate = prediction.isLate(vessel.getBerthingDate());
		} else {
			vesselDTO.isSpeedIncrease = SpeedChange.UNCHANGED;
		}
		return vesselDTO;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getInVoyNo() {
		return inVoyNo;
	}

	public void setInVoyNo(String inVoyNo) {
		this.inVoyNo = inVoyNo;
	}

	public String getOutVoyNo() {
		return outVoyNo;
	}

	public void setOutVoyNo(String outVoyNo) {
		this.outVoyNo = outVoyNo;
	}

	public String getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(String avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public SpeedChange isSpeedIncrease() {
		return isSpeedIncrease;
	}

	public void setSpeedIncrease(SpeedChange isSpeedIncrease) {
		this.isSpeedIncrease = isSpeedIncrease;
	}

	public String getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getBerthingTime() {
		return berthingTime;
	}

	public void setBerthingTime(String berthingTime) {
		this.berthingTime = berthingTime;
	}

	public String getDepatureTime() {
		return depatureTime;
	}

	public void setDepatureTime(String depatureTime) {
		this.depatureTime = depatureTime;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SpeedChange getIsSpeedIncrease() {
		return isSpeedIncrease;
	}

	public void setIsSpeedIncrease(SpeedChange isSpeedIncrease) {
		this.isSpeedIncrease = isSpeedIncrease;
	}

	public int getShiftSeqNo() {
		return shiftSeqNo;
	}

	public void setShiftSeqNo(int shiftSeqNo) {
		this.shiftSeqNo = shiftSeqNo;
	}

	public String getPredictedTime() {
		return predictedTime;
	}

	public void setPredictedTime(String predictedTime) {
		this.predictedTime = predictedTime;
	}

	public boolean isLate() {
		return isLate;
	}

	public void setLate(boolean isLate) {
		this.isLate = isLate;
	}
}

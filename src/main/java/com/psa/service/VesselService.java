package com.psa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.psa.dto.VesselDTO;
import com.psa.entity.Alert;
import com.psa.entity.Prediction;
import com.psa.entity.SpeedRecord;
import com.psa.entity.Vessel;
import com.psa.repository.VesselRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VesselService {

    @Autowired
    private VesselRecordRepository repo;

    @Autowired
    private AlertService alertService;

    public List<VesselDTO> listAll() {
        return ((List<Vessel>) repo.findAll()).stream().map(this::convertToVesselDTO).collect(Collectors.toList());
    }

    public void save(Vessel vessel) {
        repo.save(vessel);
    }

    private VesselDTO convertToVesselDTO(Vessel vessel) {
        return VesselDTO.convertToVesselDTO(vessel);
    }

    public VesselDTO get(int id) {
        return VesselDTO.convertToVesselDTO(repo.findById(id).get());
    }

    public Vessel getOne(int id) {
        return repo.getOne(id);
    }

    public Vessel getVesselByFullNameAndInVoyNoAndShiftSeqNo(String fullName, String inVoyNo, int shiftSeqNo) {
        return repo.getVesselByFullNameAndInVoyNoAndShiftSeqNo(fullName, inVoyNo, shiftSeqNo);
    }

    public List<Vessel> findAllByBerthingDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return repo.findAllByBerthingDateBetweenOrderByBerthingDateAsc(dateFrom, dateTo);
    }

    public List<VesselDTO> retrieveSchedules(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return repo.findAllByUnBerthingDateBetweenOrBerthingDateBetweenOrderByBerthingDateAsc(dateFrom, dateTo, dateFrom, dateTo).stream().map(this::convertToVesselDTO).collect(Collectors.toList());
    }

    public Map<Vessel, List<Alert>> updateVesselRecords(List<Vessel> vesselList) {
        Map<Vessel, List<Alert>> vesselAlerts = new HashMap<>();
        for (Vessel newVessel : vesselList) {
            List<Alert> alertList = new ArrayList<>();
            String fullName = newVessel.getFullName();
            String inVoyNo = newVessel.getInVoyNo();
            int shiftSeqNo = newVessel.getShiftSeqNo();

            Vessel oldVessel = getVesselByFullNameAndInVoyNoAndShiftSeqNo(fullName, inVoyNo, shiftSeqNo);

            if (oldVessel == null) {
                save(newVessel);
                
            } else {
                boolean valuesChanged = false;

                if (!(oldVessel.getBerthingDate().isEqual(newVessel.getBerthingDate()))) {
                    valuesChanged = true;
                    Alert alert = new Alert();
                    alert.setPredictedTime(oldVessel.getBerthingDate());
                    oldVessel.setBerthingDate(newVessel.getBerthingDate());
                    alert.setVessel(oldVessel);
                    alert.setType("BD");
                    alertList.add(alert);
                }
                if (!(oldVessel.getUnBerthingDate().isEqual(newVessel.getUnBerthingDate()))) {
                    valuesChanged = true;
                    Alert alert = new Alert();
                    alert.setPredictedTime(oldVessel.getUnBerthingDate());
                    oldVessel.setUnBerthingDate(newVessel.getUnBerthingDate());
                    alert.setVessel(oldVessel);
                    alert.setType("UBD");
                    alertList.add(alert);
                }
                if (!(oldVessel.getStatus().equals(newVessel.getStatus()))) {
                    valuesChanged = true;
                    Alert alert = new Alert();
                    alert.setPredictedTime(oldVessel.getBerthingDate());
                    oldVessel.setStatus(newVessel.getStatus());
                    alert.setVessel(oldVessel);
                    alert.setType("Status");
                    alertList.add(alert);
                }
                if (valuesChanged) {
                    alertService.saveAll(alertList);
                    save(oldVessel);
                    vesselAlerts.put(oldVessel, alertList);
                }
            }
        }
        return vesselAlerts;
    }

    public List<Alert> updatePredictions(Vessel vessel, Prediction newPrediction) {
        Prediction oldPrediction = vessel.getPrediction();
        List<Alert> alertList = new ArrayList<>();

        if (oldPrediction == null) {
            newPrediction.setVessel(vessel);
            vessel.setPrediction(newPrediction);
        } else {
            
            // Is Patching
            if (newPrediction.isPatching() != oldPrediction.isPatching()) {
                Alert alert = new Alert();
                alert.setPredictedTime(vessel.getBerthingDate());
                oldPrediction.setPatching(newPrediction.isPatching());
                alert.setVessel(vessel);
                alert.setType("Patching");
                alertList.add(alert);
            }

            // Predicted Timing Change
            if (!(newPrediction.getPredBerthing().isEqual(oldPrediction.getPredBerthing()))) {
                Alert alert = new Alert();
                alert.setPredictedTime(vessel.getBerthingDate());
                oldPrediction.setPredBerthing(newPrediction.getPredBerthing());
                alert.setVessel(vessel);
                alert.setType("PB");
                alertList.add(alert);
            }

            // Late
            if (newPrediction.getPredBerthing().isAfter(vessel.getBerthingDate())) {
                Alert alert = new Alert();
                alert.setPredictedTime(vessel.getBerthingDate());
                oldPrediction.setPredBerthing(newPrediction.getPredBerthing());
                alert.setVessel(vessel);
                alert.setType("Late");
                alertList.add(alert);
            }
            
            SpeedRecord sr = newPrediction.getSpeedRecords().get(0);
            sr.setPrediction(oldPrediction);
            oldPrediction.getSpeedRecords().add(sr);

            vessel.setPrediction(oldPrediction);
            alertService.saveAll(alertList);
        }
        save(vessel);
        
        if (alertList.isEmpty()) {
            return null;
        }
        return alertList;
    }
}

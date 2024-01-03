package com.psa.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.psa.entity.Vessel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VesselRecordRepository extends JpaRepository<Vessel, Integer> {
    public Vessel getVesselByFullNameAndInVoyNoAndShiftSeqNo(String fullName, String inVoyNo, int shiftSeqNo);

    public List<Vessel> findAllByBerthingDateBetweenOrderByBerthingDateAsc(LocalDateTime startDate, LocalDateTime endDate);

    public List<Vessel> findAllByUnBerthingDateBetweenOrBerthingDateBetweenOrderByBerthingDateAsc(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime startDate1, LocalDateTime endDate1);
}

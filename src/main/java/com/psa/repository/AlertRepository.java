package com.psa.repository;

import java.util.ArrayList;

import com.psa.entity.Alert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    
    @Query(value = "SELECT * FROM alert "
                    + "INNER JOIN user_alert alerts ON alerts.alert_id = alert.id "
                    + "INNER JOIN user ON alerts.user_id = user.user_id "
                    + "WHERE user.username = :username ", nativeQuery = true)
    public ArrayList<Alert> getAlertsByUsername(String username);

    
    @Query(value = "SELECT Count(alert.id) FROM user_alert alerts "
            + "INNER JOIN alert ON alerts.alert_id = alert.id "
            + "INNER JOIN user ON alerts.user_id = user.user_id "
            + "WHERE user.username = :username ", nativeQuery = true)
    public int getNoOfAlertsByUsername(@Param("username") String username);

}

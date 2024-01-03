package com.psa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.psa.entity.Alert;
import com.psa.entity.Mail;
import com.psa.entity.User;
import com.psa.entity.Vessel;
import com.psa.repository.AlertRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private MailService mailService;

    public List<Alert> listAll() {
        return alertRepository.findAll();
    }

    public void save(Alert alert){
        alertRepository.save(alert);
    }

    public List<Alert> saveAll(List<Alert> alerts) {
        return alertRepository.saveAll(alerts);
    }

    public List<Alert> getAlert(String username) {
        List<Alert> alertList = alertRepository.getAlertsByUsername(username);
        if(alertList.size()==0) {
            return null;
        }
        return alertList;
    }

    public Alert get(int id){
        return alertRepository.findById(id).get();
    }

    public int getNoOfAlertsByUsername(String username){
        return alertRepository.getNoOfAlertsByUsername(username);
    }
    
    public void sendEmail(Map<Vessel, List<Alert>> alertMap, boolean isVesselQuery) {
        Map<User, String> userAlertMap = new HashMap<>();
        for (Map.Entry<Vessel, List<Alert>> entry: alertMap.entrySet()) {
            List<Alert> vesselAlerts = entry.getValue();
            Vessel vessel = entry.getKey();
            String content = "<li>" + vessel.getFullName() + " - " + vessel.getInVoyNo() + "</li>";
            content += Mail.formatAlerts(vesselAlerts);

            Set<User> subscribers = vessel.getSubscribers();
            for (User user: subscribers) {
                if (userAlertMap.containsKey(user)) {
                    String mail = userAlertMap.get(user);
                    mail += content;
                    userAlertMap.put(user, mail);
                } else {
                    userAlertMap.put(user, "<ol>" + content);
                }
            }
        }
       

        Mail mail = new Mail();
        if (isVesselQuery) {
            mail.setMailSubject("Vessel Alert(s) Updates");
        } else {
            mail.setMailSubject("Prediction Updates");
        }
        mail.setMailFrom("PSA");
        
       
        for (Map.Entry<User, String> entry: userAlertMap.entrySet()){
            mail.setMailContent("Dear " + entry.getKey().getUsername() + ",\n" + entry.getValue()+ "</ol>");
            mail.setMailTo("webtestercs102@gmail.com");
            mailService.sendEmail(mail);
        }

    }

    //for testing purposes only

    // public void alertEmail(List<Alert> alertList) {
    //     Map<Vessel, List<Alert>> alertMap = new HashMap<>();
    //     for (Alert alert : alertList) {
    //         Vessel vessel = alert.getVessel();
    //         if (alertMap.containsKey(vessel)) {
    //             List<Alert> vesselAlerts = alertMap.get(vessel);
    //             vesselAlerts.add(alert);
    //             alertMap.put(vessel, vesselAlerts);
    //         } else {
    //             List<Alert> vesselAlerts = new ArrayList<>();
    //             vesselAlerts.add(alert);
    //             alertMap.put(vessel, vesselAlerts);
    //         }
    //     }

    //     Map<User, String> userAlertMap = new HashMap<>();
    //     for (Map.Entry<Vessel, List<Alert>> entry: alertMap.entrySet()) {
    //         List<Alert> vesselAlerts = entry.getValue();
    //         Vessel vessel = entry.getKey();
    //         String content = "<li>" + vessel.getFullName() + " - " + vessel.getInVoyNo() + "</li>";
    //         content += Mail.formatAlerts(vesselAlerts);

    //         List<User> subscribers = vessel.getSubscribers();
    //         for (User user: subscribers) {
    //             if (userAlertMap.containsKey(user)) {
    //                 String mail = userAlertMap.get(user);
    //                 mail += content;
    //                 userAlertMap.put(user, mail);
    //             } else {
    //                 userAlertMap.put(user, "<ol>" + content);
    //             }
    //         }
    //     }
       

    //     Mail mail = new Mail();
    //     // if (isVesselQuery) {
    //     //     mail.setMailSubject("Vessel Alert(s) Updates");
    //     // }
    //     mail.setMailSubject("Prediction Updates");
    //     mail.setMailFrom("PSA");
        
       
    //     for (Map.Entry<User, String> entry: userAlertMap.entrySet()){
    //         mail.setMailContent("Dear " + entry.getKey().getUsername() + ",\n" + entry.getValue()+ "</ol>");
    //         mail.setMailTo("webtestercs102@gmail.com");
    //         mailService.sendEmail(mail);
    //     }

    // }
}
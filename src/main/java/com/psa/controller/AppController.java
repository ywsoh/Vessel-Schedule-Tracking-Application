package com.psa.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.psa.config.ApiConfig;
import com.psa.dto.VesselDTO;
import com.psa.entity.User;
import com.psa.service.AlertService;
import com.psa.service.UserService;
import com.psa.service.VesselService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

    @Autowired
    private UserService userDetails;

    @Autowired
    private VesselService vesselService;

    @Autowired
    private AlertService alertService;
    
    @Autowired
    private ApiConfig apiConfig;

    @GetMapping(value = "/")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/home";
    }

    @GetMapping(value = "/admin/user-list")
    public String viewUsers(Model model, Principal principal) {
        List<User> userList = userDetails.listAll();
        model.addAttribute("userList", userList);
        model.addAttribute("noOfAlerts", alertService.getNoOfAlertsByUsername(principal.getName()));
        return "admin/user-list";
    }

    @GetMapping(value = "/view-schedules")
    public String viewSchedule(Model model, Principal principal) {
        LocalDateTime dateFrom = LocalDate.now().plusDays(0).atStartOfDay();
        LocalDateTime dateTo = LocalDate.now().plusDays(6).atTime(23, 59, 59);
        List<VesselDTO> vesselList;
        vesselList = vesselService.retrieveSchedules(dateFrom, dateTo);

        if (dateFrom.getMonth().equals(dateTo.getMonth())) {
            model.addAttribute("monthView", 1);
        } else {
            model.addAttribute("monthView", 2);
        }
        // List <Alert> alertList = alertService.getAlert("user");
        // for (Alert alert : alertList){
        //     System.out.println(alert);
        // }
        // alertService.alertEmail(alertList);
        model.addAttribute("subscribed", userDetails.getSubscribedVesselId(principal.getName()));
        model.addAttribute("vesselList", vesselList);
        model.addAttribute("noOfAlerts", alertService.getNoOfAlertsByUsername(principal.getName()));



        return "common/vessel-list";
    }

    @PostMapping(value = "/subscribe")
    public String userSubscription(@RequestParam(value = "subscribe", required = true) List<String> vesselIDs,
            Model model, Principal principal) {
        userDetails.updateSubscriptions(principal.getName(), vesselIDs);
        return "redirect:/view-schedules";
    }

    @GetMapping(value = "/home")
    public String viewHomePage(Model model, Principal principal) {
        model.addAttribute("subscribedVessels", userDetails.getUserByUsername(principal.getName()).getSubscriptions());
        model.addAttribute("noOfAlerts", alertService.getNoOfAlertsByUsername(principal.getName()));
        return "common/home";
    }

    @GetMapping(value = "/create-user")
    public String createUser(Model model) {
        return "admin/create-user";
    }

    @GetMapping(value = "/admin/admin-settings")
    public String adminSettings(Model model) {
        model.addAttribute("vesselTimeInterval", apiConfig.getVesselBerthingQueryInterval());
        model.addAttribute("predictionTimeInterval", apiConfig.getPredictionQueryInterval());
        model.addAttribute("apiKey", apiConfig.getApiKey());
        model.addAttribute("vesselServerName", apiConfig.getVesselBerthingUrl());
        model.addAttribute("predictionServerName", apiConfig.getPredictionUrl());
        return "admin/admin-settings";
    }

    @GetMapping(value = "/ship-view")
    public String shipView(@RequestParam int shipId, Model model) {
        VesselDTO vessel = vesselService.get(shipId);
        model.addAttribute("vessel", vessel);
        return "common/ship-view";
    }

    @GetMapping(value = "/alerts-settings")
    public String alertSettings() {
        return "common/alerts-settings";
    }

    @GetMapping(value = "/password-change")
    public String passwordChange() {
        return "common/password-change";
    }

    @PostMapping(value = "/change-password")
    public String changePassword(@RequestParam String newPassword, Principal principal) {
        System.out.println(newPassword);
        // userDetails.changePasswordByUsername(principal.getName(), newPassword);
        return "redirect:/home";
    }

    @GetMapping(value = "/notifications")
    public String notificationHistory() {
        return "common/notifications";
    }
}

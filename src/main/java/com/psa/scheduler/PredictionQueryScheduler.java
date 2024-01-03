package com.psa.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.psa.config.ApiConfig;
import com.psa.entity.Alert;
import com.psa.entity.Prediction;
import com.psa.entity.Vessel;
import com.psa.service.AlertService;
import com.psa.service.ApiService;
import com.psa.service.VesselService;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Component
public class PredictionQueryScheduler implements SchedulingConfigurer, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionQueryScheduler.class);

    private static final Integer CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS = 10;

    private final String jobName = "PREDICTION_QUERY_SCHEDULER";

    @Autowired
    private TaskScheduler apiCallTaskScheduler;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private ApiService apiService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private VesselService vesselRecordService;

    private Date nextExecutionTime = null;

    private ScheduledTaskRegistrar scheduledTaskRegistrar;
    private ScheduledFuture<?> scheduledFuture;

    private Integer scheduleInSeconds = 0;

    @Override
    public void run() {
        LocalDateTime dateFrom = LocalDate.now().atStartOfDay();
        LocalDateTime dateTo = LocalDate.now().plusDays(3).atTime(23, 59, 59);

        LOGGER.info("Retrieving vesselList for next 3 days");
        List<Vessel> vesselList = vesselRecordService.findAllByBerthingDateBetween(dateFrom, dateTo);

        LOGGER.info("Iterating vesselList");

        Map<Vessel, List<Alert>> vesselAlertsMap = new HashMap<>();
        for (Vessel vessel : vesselList) {
            LOGGER.info("Querying prediction for " + vessel.getVslVoy());
            JSONObject predictionJson = apiService.retrievePredictionRecord(vessel.getVslVoy());

            if (predictionJson == null) {
                // LOGGER.info("Null Prediction: " + vessel.getVslVoy());
                continue;
            }

            LOGGER.info("Processing Prediction");
            Prediction prediction = Prediction.getPredictionFromJsonObj(predictionJson);

            LOGGER.info("Updating Prediction");
            List<Alert> vesselAlerts = vesselRecordService.updatePredictions(vessel, prediction);
            
            if(vesselAlerts != null) {
                vesselAlertsMap.put(vessel, vesselAlerts);
            }

            LOGGER.info("Updated Prediction");
        }
        alertService.sendEmail(vesselAlertsMap, false);
        LOGGER.info("Prediction Update Complete");
    }

    @Override
    public synchronized void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        if (this.scheduledTaskRegistrar == null) {
            this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        }

        this.scheduledTaskRegistrar.setScheduler(apiCallTaskScheduler);

        scheduledFuture = this.scheduledTaskRegistrar.getScheduler().schedule(this, triggerContext -> {
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            if (lastActualExecutionTime == null) {
                lastActualExecutionTime = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(lastActualExecutionTime);

            if (apiConfig.isRefreshPredictionTableOnStartup()) {
                cal.add(Calendar.SECOND, 8);
                apiConfig.setRefreshPredictionTableOnStartup(false);
            } else {
                if (scheduleInSeconds == 0) {
                    scheduleInSeconds = apiConfig.getPredictionQueryInterval();
                }
                cal.add(Calendar.SECOND, scheduleInSeconds);
            }

            this.nextExecutionTime = cal.getTime();
            return nextExecutionTime;
        });

    }

    /**
     * Update the interval of the API Call
     * 
     * @param apiCallInSeconds Number of seconds between each API Calls
     */
    public synchronized void updateSchedule(Integer apiCallInSeconds) {
        this.scheduleInSeconds = apiCallInSeconds;

        long delayInSeconds = this.scheduledFuture.getDelay(TimeUnit.SECONDS);

        if (delayInSeconds < 0) {
            LOGGER.info("API call currrently in progress. New schedule will take effect after the current run");
        } else if (delayInSeconds < CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS) {
            LOGGER.info(
                    "Next sync is less than {} seconds away. after the next run, schedule will automatically be adjusted.",
                    CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS);
        } else {
            LOGGER.info(
                    "Next sync is more than {} seconds away. scheduledFuture.delay() is {}. Hence cancelling the schedule and rescheduling.",
                    CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS, delayInSeconds);

            boolean cancel = this.scheduledFuture.cancel(false); // do not interrupt the current run if it kicked in.
            LOGGER.info("future.cancel() returned {}. isCancelled() : {} isDone : {}", cancel,
                    scheduledFuture.isCancelled(), scheduledFuture.isDone());
            LOGGER.info("Reconfiguring sync for {} with new schedule {}", jobName, apiCallInSeconds);

            configureTasks(this.scheduledTaskRegistrar);
        }
    }

    public void trigger() {
        run();
    }

}

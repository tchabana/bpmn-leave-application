package com.lln.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component("processLeaveDelegate")
public class ProcessLeaveDelegate implements JavaDelegate {
    
    private final Logger LOGGER = Logger.getLogger(ProcessLeaveDelegate.class.getName());
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requester = (String) execution.getVariable("requester");
        String leaveType = (String) execution.getVariable("LeaveType");
        Date startDate = (Date) execution.getVariable("StartDate");
        Date endDate = (Date) execution.getVariable("EndDate");
        String reason = (String) execution.getVariable("RequestReason");
        
        LOGGER.info("========== Processing Leave Request ==========");
        LOGGER.info("Requester: " + requester);
        LOGGER.info("Leave Type: " + leaveType);
        LOGGER.info("Start Date: " + startDate);
        LOGGER.info("End Date: " + endDate);
        LOGGER.info("Reason: " + reason);
        LOGGER.info("==============================================");
        
        // Simuler le traitement
        Thread.sleep(2000);
        
        // Vous pouvez ajouter ici la logique métier :
        // - Enregistrer dans une base de données
        // - Envoyer des notifications par email
        // - Mettre à jour un système RH externe
        
        execution.setVariable("processedDate", new java.util.Date().toString());
        execution.setVariable("status", "PROCESSED");
        
        LOGGER.info("Leave request processed successfully!");
    }
}
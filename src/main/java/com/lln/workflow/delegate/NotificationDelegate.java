package com.lln.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component("notificationDelegate")
public class NotificationDelegate implements JavaDelegate {
    
    private final Logger LOGGER = Logger.getLogger(NotificationDelegate.class.getName());
    
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.warning("========== ⏰ REMINDER ==========");
        LOGGER.warning("Task 'Approve' is still pending!");
        LOGGER.warning("Please complete this task ASAP.");
        LOGGER.warning("================================");
    }
}
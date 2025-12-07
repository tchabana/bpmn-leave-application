package com.lln.workflow.service;

import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class NotificationService {
    
    private final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    
    public void sendReminder(String assignee, String taskName) {
        LOGGER.warning("========== REMINDER ==========");
        LOGGER.warning("Task '" + taskName + "' is still pending for: " + assignee);
        LOGGER.warning("Please complete this task as soon as possible.");
        LOGGER.warning("==============================");
        
        // Ici vous pouvez ajouter :
        // - Envoi d'email
        // - Notification Slack/Teams
        // - SMS
    }
}
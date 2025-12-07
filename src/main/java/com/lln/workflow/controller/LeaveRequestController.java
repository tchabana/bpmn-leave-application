package com.lln.workflow.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startLeaveRequest(@RequestBody Map<String, String> variables) {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("requester", variables.get("requester"));
        
        ProcessInstance processInstance = runtimeService
            .startProcessInstanceByKey("Process_1", processVariables);
        
        Map<String, String> response = new HashMap<>();
        response.put("processInstanceId", processInstance.getId());
        response.put("message", "Leave request initiated successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks/{assignee}")
    public ResponseEntity<List<Task>> getTasksForUser(@PathVariable String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
            .taskAssignee(assignee)
            .list();
        
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, String>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        
        taskService.complete(taskId, variables);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task completed successfully");
        
        return ResponseEntity.ok(response);
    }
}
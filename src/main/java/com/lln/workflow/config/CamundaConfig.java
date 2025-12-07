package com.lln.workflow.config;

import org.camunda.bpm.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import jakarta.annotation.PostConstruct;
import java.util.logging.Logger;

@Configuration
public class CamundaConfig {
    
    private final Logger LOGGER = Logger.getLogger(CamundaConfig.class.getName());
    
    @Autowired
    private RepositoryService repositoryService;
    
    @PostConstruct
    public void deployProcesses() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:**/*.bpmn");
            
            for (Resource resource : resources) {
                LOGGER.info("Deploying BPMN: " + resource.getFilename());
                
                repositoryService.createDeployment()
                    .addInputStream(resource.getFilename(), resource.getInputStream())
                    .name("Process Deployment")
                    .deploy();
                
                LOGGER.info("✅ Successfully deployed: " + resource.getFilename());
            }
        } catch (Exception e) {
            LOGGER.severe("❌ Error deploying processes: " + e.getMessage());
        }
    }
}

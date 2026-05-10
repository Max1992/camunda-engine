package ru.backup.engine;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TestUtils {
  public static void deploy(String fileName) throws FileNotFoundException {
    repositoryService().createDeployment()
        .addInputStream(fileName, new FileInputStream("bpmn/" + fileName))
        .deploy();
  }
}

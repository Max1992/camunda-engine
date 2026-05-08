package ru.backup.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;

public class DebugUtils {
  private static ObjectMapper mapper = new ObjectMapper();

  public static void print(Logger log, DelegateExecution execution) {
    String vars;
    try {
      vars = mapper.writeValueAsString(execution.getVariables());
    } catch (JsonProcessingException e) {
      vars = "error: " + e.getMessage();
    }
    log.info("'{}:{}' vars '{}'",
        execution.getCurrentActivityId(),
        execution.getCurrentActivityName(),
        vars
    );
  }
}

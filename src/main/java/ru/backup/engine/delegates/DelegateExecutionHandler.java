package ru.backup.engine.delegates;

import java.util.Map;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.Variables.SerializationDataFormats;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.model.bpmn.instance.FlowElement;

public class DelegateExecutionHandler {
  private final DelegateExecution execution;

  public DelegateExecutionHandler(DelegateExecution execution) {
    this.execution = execution;
  }

  public void setVariable(String name, Object value) {
    execution.setVariable(name, value);
  }

  public void setVariableLocal(String name, Object value) {
    execution.setVariableLocal(name, value);
  }

  public void setTransientVariable(String name, Object value) {
    ObjectValue transientVariable = Variables.objectValue(value)
        //.serializationDataFormat(SerializationDataFormats.JSON) // необязательно
        .setTransient(true)
        .create();
    setVariable(name, transientVariable);
  }

  public  <T> T getVariable(String variable) {
    T value = (T) execution.getVariable(variable);
    if (value == null) {
      throw new BpmnError("error500", "В списке параметров не найден ключ " + variable);
    }
    return value;
  }

  public Map<String, Object> getVariables() {
    return execution.getVariables();
  }

  public String getCurrentActivityId() {
    return execution.getCurrentActivityId();
  }

  public String getCurrentActivityName() {
    return execution.getCurrentActivityName();
  }

  public FlowElement getBpmnModelElementInstance() {
    return execution.getBpmnModelElementInstance();
  }

  public String getBusinessKey() {
    return execution.getBusinessKey();
  }

  public ProcessEngineServices getProcessEngineServices() {
    return execution.getProcessEngineServices();
  }

  public String getProcessBusinessKey() {
    return execution.getProcessBusinessKey();
  }
}

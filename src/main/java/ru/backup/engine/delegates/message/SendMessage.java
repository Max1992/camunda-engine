package ru.backup.engine.delegates.message;

import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("sendMessage")
public class SendMessage extends JavaDelegateHandler {

  private static final Logger log = LoggerFactory.getLogger(SendMessage.class);

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    ThrowEvent endEvent = (ThrowEvent) delegateExecution.getBpmnModelElementInstance();

    MessageEventDefinition messageDefinition = endEvent.getChildElementsByType(MessageEventDefinition.class)
        .stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Message definition not found in Modeler!"));

    String messageName = messageDefinition.getMessage().getName();
    String businessKey = delegateExecution.getBusinessKey();
    String correlation = delegateExecution.getVariableLocal("correlation");

    var runtimeService = delegateExecution.getProcessEngineServices().getRuntimeService();

    long count = runtimeService.createEventSubscriptionQuery()
        .eventType("message")
        .eventName(messageName)
        .count();
    log.info("Активных подписок на {}: {}", messageName, count);

    runtimeService
        .createMessageCorrelation(messageName)
        .processInstanceBusinessKey(businessKey)
        .processInstanceVariableEquals("correlation", correlation)
        .setVariables(delegateExecution.getVariables())
        .correlate();
  }
}

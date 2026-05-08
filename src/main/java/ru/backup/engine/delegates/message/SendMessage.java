package ru.backup.engine.delegates.message;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.DebugUtils;

@Component("sendMessage")
public class SendMessage implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(SendMessage.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    DebugUtils.print(log, delegateExecution);

    EndEvent endEvent = (EndEvent) delegateExecution.getBpmnModelElementInstance();

    MessageEventDefinition messageDefinition = endEvent.getChildElementsByType(MessageEventDefinition.class)
        .stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Message definition not found in Modeler!"));

    String messageName = messageDefinition.getMessage().getName();
    String businessKey = delegateExecution.getBusinessKey();

    long receiversCount = delegateExecution.getProcessEngineServices().getRuntimeService()
        .createExecutionQuery()
        .messageEventSubscriptionName(messageName)
        .processInstanceBusinessKey(businessKey)
        .count();

    if (receiversCount > 0) {
      delegateExecution.getProcessEngineServices().getRuntimeService()
          .createMessageCorrelation(messageName)
          .processInstanceBusinessKey(businessKey)
          .correlate();
    } else {
      log.info("Получатель для сообщения {} еще не готов.", messageName);
    }
  }
}

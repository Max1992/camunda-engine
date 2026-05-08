package ru.backup.engine.delegates.message;

import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.MessageService;

@Component("sendStartMessage")
public class SendStartMessage extends JavaDelegateHandler {

  private static final Logger log = LoggerFactory.getLogger(SendStartMessage.class);
  private final MessageService messageService;

  public SendStartMessage(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    ThrowEvent endEvent = (ThrowEvent) delegateExecution.getBpmnModelElementInstance();
    MessageEventDefinition messageDefinition = endEvent.getChildElementsByType(MessageEventDefinition.class)
        .stream()
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Message definition not found in Modeler!"));

    String messageName = messageDefinition.getMessage().getName();

    messageService.sendMessage(messageName, delegateExecution, delegateExecution.getVariables());
  }
}

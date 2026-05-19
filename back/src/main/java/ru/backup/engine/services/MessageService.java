package ru.backup.engine.services;

import java.util.Map;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.backup.engine.delegates.DelegateExecutionHandler;

@Service
public class MessageService {

  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  /**
   * Отправить сообщение создающее новый процесс.
   * */
  public void sendMessage(
      String messageName,
      DelegateExecutionHandler execution,
      Map<String, Object> variables) {

    RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
    ProcessDefinition targetProcess = repositoryService.createProcessDefinitionQuery()
        .messageEventSubscriptionName(messageName)
        .latestVersion() // берем последнюю версию
        .active()        // только если процесс не приостановлен
        .singleResult();

    if (targetProcess != null) {
      String businessKey = execution.getProcessBusinessKey();
      RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
      runtimeService.startProcessInstanceByMessage(messageName, businessKey, variables);
    } else {
      String msg = "Не найден процесс, начинающийся с сообщения: " + messageName;
      log.warn(msg);
      throw new BpmnError("error500", msg);
    }
  }
}

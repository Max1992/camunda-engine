package ru.backup.engine.delegates.location;

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.DebugUtils;

@Component("detainPlayersInOtherWorld")
public class DetainPlayersInOtherWorld implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(DetainPlayersInOtherWorld.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    DebugUtils.print(log, delegateExecution);

    String player = (String) delegateExecution.getVariable("player");

    Map<String, Object> variables = new HashMap<>();
    variables.put("player", player);
    sendMessage("msg201", delegateExecution, variables);
  }

  private void sendMessage(
      String messageName,
      DelegateExecution execution,
      Map<String, Object> variables) {

    RepositoryService repositoryService = execution.getProcessEngineServices().getRepositoryService();
    RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
    String businessKey = execution.getProcessBusinessKey();

    ProcessDefinition targetProcess = repositoryService.createProcessDefinitionQuery()
        .messageEventSubscriptionName(messageName)
        .latestVersion() // берем последнюю версию
        .active()        // только если процесс не приостановлен
        .singleResult();

    if (targetProcess != null) {
      runtimeService.startProcessInstanceByMessage(messageName, businessKey, variables);
    } else {
      String msg = "Не найден процесс, начинающийся с сообщения: " + messageName;
      log.warn(msg);
      throw new BpmnError("error500", msg);
    }
  }
}

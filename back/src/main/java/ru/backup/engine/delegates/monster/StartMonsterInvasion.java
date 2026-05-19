package ru.backup.engine.delegates.monster;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.MessageService;

@Component("startMonsterInvasion")
public class StartMonsterInvasion extends JavaDelegateHandler {

  private final MessageService messageService;

  public StartMonsterInvasion(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String monsterId = execution.getVariableLocal("monsterId");
    String locationId = execution.getVariableLocal("locationId");
    String messageName = execution.getVariableLocal("messageName");

    Map<String, Object> variables = new HashMap<>();
    variables.put("monsterId", monsterId);
    variables.put("locationId", locationId);
    messageService.sendMessage(messageName, execution, variables);
  }
}

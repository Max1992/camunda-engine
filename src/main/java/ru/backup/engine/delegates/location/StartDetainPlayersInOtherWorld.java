package ru.backup.engine.delegates.location;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.MessageService;

@Component("startDetainPlayersInOtherWorld")
public class StartDetainPlayersInOtherWorld extends JavaDelegateHandler {

  private final MessageService messageService;

  public StartDetainPlayersInOtherWorld(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {

    String player = delegateExecution.getVariableLocal("playerId");
    String messageName = delegateExecution.getVariableLocal("messageName");

    Map<String, Object> variables = new HashMap<>();
    variables.put("playerId", player);
    messageService.sendMessage(messageName, delegateExecution, variables);
  }
}

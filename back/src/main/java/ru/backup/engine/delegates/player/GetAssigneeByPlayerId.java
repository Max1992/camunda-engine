package ru.backup.engine.delegates.player;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("getAssigneeByPlayerId")
public class GetAssigneeByPlayerId extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String playerId = execution.getVariableLocal("playerId");
    execution.setVariableLocal("assignee", "admin");
  }
}

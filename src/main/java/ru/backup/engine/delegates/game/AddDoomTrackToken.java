package ru.backup.engine.delegates.game;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("addDoomTrackToken")
public class AddDoomTrackToken extends JavaDelegateHandler {

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    delegateExecution.setVariable("currentTokenCount", getCurrentDoomTokenCount());
  }

  private int getCurrentDoomTokenCount() {
    return 2;
  }
}

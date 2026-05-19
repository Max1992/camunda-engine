package ru.backup.engine.dmn;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("addDoomTrackToken")
public class AddDoomTrackToken extends JavaDelegateHandler {

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    delegateExecution.setVariableLocal("totalDoomToken", getTotalDoomToken());
  }

  private int getTotalDoomToken() {
    return 2;
  }
}

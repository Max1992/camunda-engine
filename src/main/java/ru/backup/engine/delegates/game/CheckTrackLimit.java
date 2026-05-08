package ru.backup.engine.delegates.game;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("checkTrackLimit")
public class CheckTrackLimit extends JavaDelegateHandler {

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    delegateExecution.setVariable("tokenLimitReached", isLimitReached());
  }

  private boolean isLimitReached() {
    return false;
  }
}

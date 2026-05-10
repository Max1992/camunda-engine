package ru.backup.engine.dmn;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("checkTrackLimit")
public class CheckTrackLimit extends JavaDelegateHandler {

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    delegateExecution.setVariableLocal("doomTokenLimitReached", isLimitReached());
  }

  private boolean isLimitReached() {
    return false;
  }
}

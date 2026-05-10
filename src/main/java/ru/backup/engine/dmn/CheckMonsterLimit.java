package ru.backup.engine.dmn;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("checkMonsterLimit")
public class CheckMonsterLimit extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    execution.setVariableLocal("monsterLimitReached", isLimitReached());
  }

  private boolean isLimitReached() {
    return false;
  }
}

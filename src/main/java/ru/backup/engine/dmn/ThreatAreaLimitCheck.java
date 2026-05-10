package ru.backup.engine.dmn;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

/**
 * проверка ломита жетонов монстров на окраине
 * */
@Component("threatAreaLimitCheck")
public class ThreatAreaLimitCheck extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    execution.setVariableLocal("threatAreaLimitReached", isLimitReached());
  }

  private boolean isLimitReached() {
    return false;
  }
}

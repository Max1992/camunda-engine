package ru.backup.engine.delegates.location;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("setGateComplited")
public class SetGateComplited extends JavaDelegateHandler {

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
  }
}

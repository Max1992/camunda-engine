package ru.backup.engine.delegates;

import org.springframework.stereotype.Component;

/**
 * Ничего не делает.
 * */
@Component("nop")
public class NopDelegate extends JavaDelegateHandler {
  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
  }
}

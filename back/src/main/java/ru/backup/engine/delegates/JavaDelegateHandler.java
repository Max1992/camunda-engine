package ru.backup.engine.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.backup.engine.DebugUtils;

public abstract class JavaDelegateHandler implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(JavaDelegateHandler.class);
  private DelegateExecution context;

  protected abstract void onExecute(DelegateExecutionHandler execution) throws Exception;

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    try {
      context = delegateExecution;
      DelegateExecutionHandler handler = new DelegateExecutionHandler(context);
      DebugUtils.print(log, handler);
      onExecute(handler);
    } catch (Exception e) {
      log.error("Ошибка {}", e.getMessage());
    } finally {
      context = null;
    }
  }
}

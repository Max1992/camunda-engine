package ru.backup.engine.delegates.location;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.DebugUtils;

@Component("setGateToLocation")
public class SetGateToLocation implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(SetGateToLocation.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    DebugUtils.print(log, delegateExecution);
  }
}

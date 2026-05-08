package ru.backup.engine.delegates.otherworld;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.DebugUtils;

@Component("delayPlayersInOtherWorld")
public class DelayPlayersInOtherWorld implements JavaDelegate {
  private static final Logger log = LoggerFactory.getLogger(DelayPlayersInOtherWorld.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    DebugUtils.print(log, delegateExecution);
  }
}

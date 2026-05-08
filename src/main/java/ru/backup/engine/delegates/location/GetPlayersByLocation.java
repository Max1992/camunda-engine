package ru.backup.engine.delegates.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.DebugUtils;

@Component("getPlayersByLocation")
public class GetPlayersByLocation implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(GetPlayersByLocation.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    DebugUtils.print(log, delegateExecution);
    delegateExecution.setVariable("playerCollection", getPlayers());
  }

  private List<String> getPlayers() {
    return new ArrayList<>(Arrays.asList("player1", "player2"));
  }
}

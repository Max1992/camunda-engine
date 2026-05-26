package ru.backup.engine.delegates.player;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("getLuckConfiguration")
public class GetLuckConfiguration extends JavaDelegateHandler {

  private static final Logger log = LoggerFactory.getLogger(GetLuckConfiguration.class);

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String playerId = execution.getVariableLocal("playerId");

    log.info("Получение модификации удачи для пользователя '{}'", playerId);
    execution.setVariableLocal("skills", new ArrayList<>(List.of("SKILL_00005")));
    execution.setVariableLocal("ally", new ArrayList<>(List.of("ALLY_00006")));
    execution.setVariableLocal("base", 2);
    execution.setVariableLocal("availableClue", 2);
    execution.setVariableLocal("wasteOfClueCount", 0);
  }
}

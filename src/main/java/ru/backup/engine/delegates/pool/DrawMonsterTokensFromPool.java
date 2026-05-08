package ru.backup.engine.delegates.pool;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.LocationService;

@Component("drawMonsterTokensFromPool")
public class DrawMonsterTokensFromPool extends JavaDelegateHandler {

  private final LocationService locationService;

  public DrawMonsterTokensFromPool(LocationService locationService) {
    this.locationService = locationService;
  }

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    String location = delegateExecution.getVariable("locationId");

    List<String> players = locationService.getPlayersByLocation(location);

    List<String> monsterCollection = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      monsterCollection.add("monster_" + i);
    }

    delegateExecution.setVariable("monsterCollection", new ArrayList<>(monsterCollection));
  }
}

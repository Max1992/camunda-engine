package ru.backup.engine.delegates.location;

import static ru.backup.engine.delegates.Consts.GET_PLAYERS_BY_LOCATION_TASK;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.LocationService;

@Component(GET_PLAYERS_BY_LOCATION_TASK)
public class GetPlayersByLocation extends JavaDelegateHandler {

  private final LocationService locationService;

  public GetPlayersByLocation(LocationService locationService) {
    this.locationService = locationService;
  }

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    String location = delegateExecution.getVariableLocal("locationId");

    List<String> collection = locationService.getPlayersByLocation(location);
    delegateExecution.setVariableLocal("playerCollection", new ArrayList<>(collection));
  }
}

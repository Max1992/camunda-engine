package ru.backup.engine.delegates.location;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.LocationService;

@Component("getPlayersByLocation")
public class GetPlayersByLocation extends JavaDelegateHandler {

  private final LocationService locationService;

  public GetPlayersByLocation(LocationService locationService) {
    this.locationService = locationService;
  }

  @Override
  public void onExecute(DelegateExecutionHandler delegateExecution) throws Exception {
    String location = delegateExecution.getVariable("locationId");

    List<String> collection = locationService.getPlayersByLocation(location);
    delegateExecution.setVariable("playerCollection", new ArrayList<>(collection));
  }
}

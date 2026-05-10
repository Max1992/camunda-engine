package ru.backup.engine.delegates.location;

import static ru.backup.engine.delegates.Consts.GET_LOCATION_STATUS_TASK;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.enums.LocationStatus;

@Component(GET_LOCATION_STATUS_TASK)
public class GetLocationStatus extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    execution.setVariableLocal("locationStatus", LocationStatus.EMPTY);
  }
}

package ru.backup.engine.delegates.location;

import static ru.backup.engine.delegates.Consts.GET_NEXT_LOCATION_BY_ARROW_TASK;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component(GET_NEXT_LOCATION_BY_ARROW_TASK)
public class GetNextLocationByArrow extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String locationId = execution.getVariableLocal("locationId");
    execution.setVariableLocal("newLocationId", locationId + "-next");
  }
}

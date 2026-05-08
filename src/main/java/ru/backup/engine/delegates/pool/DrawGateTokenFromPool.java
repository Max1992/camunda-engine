package ru.backup.engine.delegates.pool;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.GateService;

@Component("drawGateTokenFromPool")
public class DrawGateTokenFromPool extends JavaDelegateHandler {

  private final GateService gateService;

  public DrawGateTokenFromPool(GateService gateService) {
    this.gateService = gateService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String gateId = gateService.drawGateTokenFromPool();
    execution.setVariable("gateId", gateId);
  }
}

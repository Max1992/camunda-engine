package ru.backup.engine.delegates;

import org.springframework.stereotype.Component;
import ru.backup.engine.services.SkillTestContextService;

@Component("clearSkillTestContext")
public class ClearSkillTestContext extends JavaDelegateHandler {

  private final SkillTestContextService skillTestContextService;

  public ClearSkillTestContext(SkillTestContextService skillTestContextService) {
    this.skillTestContextService = skillTestContextService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String playerId = execution.getVariableLocal("playerId");
    skillTestContextService.cleanup(playerId);
  }
}

package ru.backup.engine.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.enums.SkillType;
import ru.backup.engine.models.SkillTestContext;
import ru.backup.engine.services.SkillTestContextService;

@Component("intiSkillTestContext")
public class IntiSkillTestContext extends JavaDelegateHandler {

  private final SkillTestContextService skillTestContextService;

  public IntiSkillTestContext(SkillTestContextService skillTestContextService) {
    this.skillTestContextService = skillTestContextService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

    SkillTestContext context = skillTestContextService.init(
        SkillType.valueOf(execution.getVariableLocal("skillType")),
        execution.getVariableLocal("difficulty"),
        execution.getVariableLocal("playerId"),
        execution.getVariableLocal("mythId"),
        execution.getVariableLocal("mythBonus"),
        execution.getVariableLocal("requiredSuccesses")
    );

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> testContext = mapper.convertValue(context, Map.class);

    execution.setVariableLocal("context", testContext);
  }
}

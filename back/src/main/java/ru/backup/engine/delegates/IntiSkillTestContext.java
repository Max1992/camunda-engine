package ru.backup.engine.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.enums.SkillType;
import ru.backup.engine.models.SkillTestContext;

@Component("intiSkillTestContext")
public class IntiSkillTestContext extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

    SkillTestContext context = initContext(
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

  private SkillTestContext initContext(
      SkillType skillType,
      int difficulty,
      String playerId,
      String mythId,
      int mythBonus,
      int requiredSuccesses)
  {
    SkillTestContext context = new SkillTestContext();
    context.wasteOfClueCount = 0;
    context.skillType = skillType;
    context.difficulty = difficulty;
    context.playerId = playerId;
    context.mythId = mythId;
    context.mythBonus = mythBonus;
    context.requiredSuccesses = requiredSuccesses;
    return context;
  }
}

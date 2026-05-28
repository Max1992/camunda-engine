package ru.backup.engine.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.models.PlayerGameSheet;
import ru.backup.engine.models.SkillTestContext;
import ru.backup.engine.services.PlayerGameSheetProfileService;
import ru.backup.engine.services.SkillTestContextService;

@Component("skillTestSpendClue")
public class SkillTestSpendClue extends JavaDelegateHandler {

  private final SkillTestContextService skillTestContextService;
  private final PlayerGameSheetProfileService playerGameSheetProfileService;

  public SkillTestSpendClue(SkillTestContextService skillTestContextService,
      PlayerGameSheetProfileService playerGameSheetProfileService) {
    this.skillTestContextService = skillTestContextService;
    this.playerGameSheetProfileService = playerGameSheetProfileService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    SkillTestContext context = mapper.convertValue(execution.getVariableLocal("context"), SkillTestContext.class);
    PlayerGameSheet playerProfile = mapper.convertValue(execution.getVariableLocal("playerProfile"), PlayerGameSheet.class);
    int wasteOfClueCount = execution.getVariableLocal("wasteOfClueCount");

    context.wasteOfClueCount += wasteOfClueCount;
    playerProfile.clue -= context.wasteOfClueCount;
    skillTestContextService.update(context.playerId, context);
    playerGameSheetProfileService.update(context.playerId, playerProfile);

    execution.setVariableLocal("context", mapper.convertValue(context, Map.class));
    execution.setVariableLocal("playerProfile", mapper.convertValue(playerProfile, Map.class));
  }
}

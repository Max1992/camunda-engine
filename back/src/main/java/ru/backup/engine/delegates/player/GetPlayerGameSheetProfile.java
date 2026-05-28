package ru.backup.engine.delegates.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.enums.PlayerStatus;
import ru.backup.engine.models.PlayerGameSheet;
import ru.backup.engine.services.PlayerGameSheetProfileService;

@Component("getAssigneeByPlayerId")
public class GetPlayerGameSheetProfile extends JavaDelegateHandler {

  private final PlayerGameSheetProfileService playerGameSheetProfileService;

  public GetPlayerGameSheetProfile(PlayerGameSheetProfileService playerGameSheetProfileService) {
    this.playerGameSheetProfileService = playerGameSheetProfileService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String playerId = execution.getVariableLocal("playerId");

    PlayerGameSheet sheet = playerGameSheetProfileService.getPlayerGameSheet(playerId);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> profile = mapper.convertValue(sheet, Map.class);

    execution.setVariableLocal("playerProfile", profile);
  }
}

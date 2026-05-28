package ru.backup.engine.delegates.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.enums.PlayerStatus;
import ru.backup.engine.models.PlayerGameSheet;

@Component("getAssigneeByPlayerId")
public class GetPlayerGameSheetProfile extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String playerId = execution.getVariableLocal("playerId");

    PlayerGameSheet sheet = getPlayerGameSheet(playerId);

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> profile = mapper.convertValue(sheet, Map.class);

    execution.setVariableLocal("playerProfile", profile);
  }

  private PlayerGameSheet getPlayerGameSheet(String playerId) {
    PlayerGameSheet sheet = new PlayerGameSheet();

    sheet.playerStatus = PlayerStatus.NORMAL;
    sheet.clue = 3;
    sheet.knowledge = 2;
    sheet.luck = 4;
    sheet.power = 3;
    sheet.stealth = 2;
    sheet.speed = 4;
    sheet.will = 6;
    sheet.assignee = "admin";
    sheet.simpleItem = List.of("ITEM_00018");
    sheet.skill = List.of("SKILL_00005");
    sheet.ally = List.of("ALLY_00006");

    return sheet;
  }
}

package ru.backup.engine.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.backup.engine.enums.PlayerStatus;
import ru.backup.engine.models.PlayerGameSheet;

@Service
public class PlayerGameSheetProfileService {

  private final Map<String, PlayerGameSheet> profiles = new HashMap<>();

  public PlayerGameSheet getPlayerGameSheet(String playerId) {
    return profiles.computeIfAbsent(playerId, (id) -> initPlayerGameSheet(id));
  }

  public void update(String playerId, PlayerGameSheet playerProfile) {
    PlayerGameSheet profile = profiles.get(playerId);
    profile.clue = playerProfile.clue;
  }

  private PlayerGameSheet initPlayerGameSheet(String playerId) {
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

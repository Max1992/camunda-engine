package ru.backup.engine.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

@Service
public class MonsterService {
  public Map<String, Object> getMonsterInfo(String monsterId) {
    Map<String, Object> info = new HashMap<>();
    info.put("monsterType", MonsterType.NORMAL);
    info.put("monsterSymbol", Symbols.SQUARE);
    info.put("monsterLocationId", "monster-current-location-id");
    return info;
  }
}

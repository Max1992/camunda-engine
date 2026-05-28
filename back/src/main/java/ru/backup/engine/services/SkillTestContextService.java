package ru.backup.engine.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import ru.backup.engine.enums.SkillType;
import ru.backup.engine.models.SkillTestContext;

@Service
public class SkillTestContextService {

  private final Map<String, SkillTestContext> map = new HashMap<>();

  public SkillTestContext init(SkillType skillType, int difficulty, String playerId,
      String mythId, int mythBonus, int requiredSuccesses) {
    return map.computeIfAbsent(playerId, id -> initContext(skillType, difficulty, id, mythId, mythBonus, requiredSuccesses));
  }

  public void update(String playerId, SkillTestContext context) {
    SkillTestContext ctx = map.get(playerId);
    ctx.availableDices = context.availableDices;
    ctx.base = context.base;
    ctx.wasteOfClueCount = context.wasteOfClueCount;
  }

  public void cleanup(String playerId) {
    map.remove(playerId);
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

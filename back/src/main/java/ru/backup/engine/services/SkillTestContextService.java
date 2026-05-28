package ru.backup.engine.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.backup.engine.enums.SkillType;
import ru.backup.engine.models.SkillTestContext;

@Service
public class SkillTestContextService {

  private final Map<String, SkillTestContext> map = new HashMap<>();

  public SkillTestContext init(SkillType skillType, int difficulty, String playerId,
      String mythId, int mythBonus, int requiredSuccesses) {

    String contextId = UUID.randomUUID().toString();
    return map.computeIfAbsent(contextId, id -> initContext(id, skillType, difficulty, playerId, mythId, mythBonus, requiredSuccesses));
  }

  public void update(String id, SkillTestContext context) {
    map.put(id, context);
  }

  public void cleanup(String playerId) {
    map.remove(playerId);
  }

  private SkillTestContext initContext(
      String id,
      SkillType skillType,
      int difficulty,
      String playerId,
      String mythId,
      int mythBonus,
      int requiredSuccesses)
  {
    SkillTestContext context = new SkillTestContext();
    context.id = id;
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

package ru.backup.engine.models;

import ru.backup.engine.enums.SkillType;

public class SkillTestContext {

  public String id;

  public SkillTestContext() {}

  public int wasteOfClueCount;
  public SkillType skillType;
  public int difficulty;
  public String playerId;
  public String mythId;
  public int mythBonus;
  public int requiredSuccesses;
  public int availableDices = 0;
  public int base = 0;
}

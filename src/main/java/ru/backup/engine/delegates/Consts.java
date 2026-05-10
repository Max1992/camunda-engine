package ru.backup.engine.delegates;

public class Consts {

  // Технические ID элементов из Camunda Modeler
  public static final String GET_ACTIVE_MONSTERS_TASK = "getActiveMonsters";//Получить активных монстров в игре
  public static final String GET_MONSTER_INFO_TASK = "getMonsterInfo";//Получить информацию по монстру
  public static final String GET_MONSTER_STEP_COUNT = "getMonsterStepCount";//Утсновить оставшееся количество шагов
  public static final String GET_PLAYERS_BY_LOCATION_TASK = "getPlayersByLocation";// получить игроков в локации
  public static final String GET_NEXT_LOCATION_BY_ARROW_TASK = "getNextLocationByArrow";
  public static final String MOVE_MONSTER_TO_LOCATION_TASK = "moveMonsterToLocation";
  public static final String DECREASE_MONSTER_STEP_COUNT_TASK = "decreaseMonsterStepCount";
  public static final String GET_LOCATION_KEY_TASK = "getLocationKey";
  public static final String GET_NEAREST_LOCATION_WITH_PLAYER_TASK = "getNearestLocationWithPlayer";
  public static final String GET_ANY_LOCATION_WITH_PLAYER_TASK = "getAnyLocationWithPlayer";
  public static final String GET_MONSTER_SPECIAL_ACTION_ID_TASK = "getMonsterSpecialActionId";
  public static final String INVOKE_MONSTER_SPECIAL_ACTION_TASK = "invokeMonsterSpecialAction";
  public static final String GET_LOCATION_STATUS_TASK = "getLocationStatus";
  public static final String PUT_CLUE_TO_LOCATION_TASK = "addClueToLocation";
  public static final String PUT_CLUE_TO_PLAYER_TASK = "grantClueToInvestigator";
  public static final String DETERMINE_INVESTIGATOR_FOR_CLUE_TASK = "determineInvestigatorForClue";
}

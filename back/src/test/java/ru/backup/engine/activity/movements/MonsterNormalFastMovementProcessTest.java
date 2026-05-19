package ru.backup.engine.activity.movements;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.backup.engine.TestUtils;
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.delegates.GetMonsterStepCount;
import ru.backup.engine.enums.ArrowColor;
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static ru.backup.engine.delegates.Consts.*;


public class MonsterNormalFastMovementProcessTest {

  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .setJobExecutorActivate(false)
          .buildProcessEngine())
      .build();

  @BeforeAll
  public static void beforeAll() throws FileNotFoundException {
    TestUtils.deploy("Decision_MonsterMovement.dmn");
    TestUtils.deploy("Фаза мифа.bpmn");
  }

  @BeforeEach
  public void setUp() throws Exception {

    JavaDelegate getActiveMonstersMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getMonsterStepCountMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getNextLocationByArrowMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate moveMonsterToLocationMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate decreaseMonsterStepCountMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(GET_ACTIVE_MONSTERS_TASK, getActiveMonstersMock);
    Mocks.register(GET_MONSTER_STEP_COUNT, getMonsterStepCountMock);
    Mocks.register(GET_NEXT_LOCATION_BY_ARROW_TASK, getNextLocationByArrowMock);
    Mocks.register(MOVE_MONSTER_TO_LOCATION_TASK, moveMonsterToLocationMock);
    Mocks.register(DECREASE_MONSTER_STEP_COUNT_TASK, decreaseMonsterStepCountMock);

    // список монстров
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      List<String> monsterCollection = List.of("monster_0001");
      execution.setVariableLocal("activeMonsters", new ArrayList<>(monsterCollection));
      return null;
    }).when(getActiveMonstersMock).execute(Mockito.any());

    GetMonsterStepCount getMonsterStepCountReal = new GetMonsterStepCount();
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      getMonsterStepCountReal.execute(execution);
      return null;
    }).when(getMonsterStepCountMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("newLocationId", "next-location");
      return null;
    }).when(getNextLocationByArrowMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> null).when(moveMonsterToLocationMock).execute(Mockito.any());

    // один шаг монстра
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      int stepsLeft = (int) execution.getVariableLocal("stepsLeft");
      execution.setVariableLocal("stepsLeft", stepsLeft - 1);
      return null;
    }).when(decreaseMonsterStepCountMock).execute(Mockito.any());
  }

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  /**
   * данные для проверки хода монстров.
   * */
  private static Stream<Arguments> provideMonsterTestData() {
    return Stream.of(
        // 1. Обычный монстр, количество ходов 1
        arguments(MonsterType.NORMAL, 1),
        // 2. Летающий монстр, количество ходов 2
        arguments(MonsterType.FAST, 2)
    );
  }

  /**
   * Проверка движения монстров.
   * */
  @ParameterizedTest(name = "{index} ==> Тип: {0}, Количество ходов: {1}")
  @MethodSource("provideMonsterTestData")
  @Timeout(value = 15, unit = TimeUnit.SECONDS)
  public void testMonster_StepsMovement(
      MonsterType monsterType,
      int monsterStepsCount
  ) throws Exception {

    JavaDelegate getMonsterInfoMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getPlayersByLocationMock = mock(JavaDelegate.class);

    Mocks.register(GET_MONSTER_INFO_TASK, getMonsterInfoMock);
    Mocks.register(GET_PLAYERS_BY_LOCATION_TASK, getPlayersByLocationMock);

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      Map<String, Object> info = new HashMap<>();
      info.put("monsterType", monsterType);
      info.put("monsterSymbol", Symbols.SQUARE);
      info.put("monsterLocationId", "monster-current-location-id");
      execution.setVariable("monsterInfo", info);
      return null;
    }).when(getMonsterInfoMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("playerCollection", new ArrayList<>());
      return null;
    }).when(getPlayersByLocationMock).execute(Mockito.any());

    // 1. Запускаем процесс, передав также контекст текущей карты Мифа
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.MONSTER_MOVEMENT_PROCESS_KEY,
        withVariables(
            "mythosSymbol", new ArrayList<>(List.of(Symbols.SQUARE.name())),
            "arrowColor", ArrowColor.WHITE.name()
        )
    );

    // должен запросить информацию по монстру
    Mockito.verify(getMonsterInfoMock, Mockito.times(1)).execute(Mockito.any());

    // монстр не быстрый запускается 1 раз
    JavaDelegate getNextLocationByArrowMock = (JavaDelegate) Mocks.get(GET_NEXT_LOCATION_BY_ARROW_TASK);
    Mockito.verify(getNextLocationByArrowMock, Mockito.times(monsterStepsCount)).execute(Mockito.any());

    // Проверяем, что процесс полностью завершился
    assertThat(pi).isEnded();
  }

  /**
   * Монстр не может идти потому что в локации есть игрок.
   * */
  @Test
  @Timeout(value = 15, unit = TimeUnit.SECONDS)
  public void testMonster_CantMoveBecauseThereArePlayer() throws Exception {

    JavaDelegate getMonsterInfoMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getPlayersByLocationMock = mock(JavaDelegate.class);

    Mocks.register(GET_MONSTER_INFO_TASK, getMonsterInfoMock);
    Mocks.register(GET_PLAYERS_BY_LOCATION_TASK, getPlayersByLocationMock);

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      Map<String, Object> info = new HashMap<>();
      info.put("monsterType", MonsterType.NORMAL);
      info.put("monsterSymbol", Symbols.SQUARE);
      info.put("monsterLocationId", "monster-current-location-id");
      execution.setVariable("monsterInfo", info);
      return null;
    }).when(getMonsterInfoMock).execute(Mockito.any());

    // есть игрок в локации с монстром
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      List<String> collection = List.of("player_001");
      execution.setVariableLocal("playerCollection", new ArrayList<>(collection));
      return null;
    }).when(getPlayersByLocationMock).execute(Mockito.any());

    // 1. Запускаем процесс, передав также контекст текущей карты Мифа
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.MONSTER_MOVEMENT_PROCESS_KEY,
        withVariables(
            "mythosSymbol", new ArrayList<>(List.of(Symbols.SQUARE.name())),
            "arrowColor", ArrowColor.WHITE.name()
        )
    );

    Mockito.verify(getMonsterInfoMock, Mockito.times(1)).execute(Mockito.any());
    Mockito.verify(getPlayersByLocationMock, Mockito.times(1)).execute(Mockito.any());
    assertThat(pi).hasPassed("Gateway_1g172fg");
    assertThat(pi).hasNotPassed("Gateway_1mw0tfy");

    // Проверяем, что процесс полностью завершился
    assertThat(pi).isEnded();
  }
}

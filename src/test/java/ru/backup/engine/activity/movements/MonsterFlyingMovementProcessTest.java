package ru.backup.engine.activity.movements;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static ru.backup.engine.delegates.Consts.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.backup.engine.TestUtils;
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.enums.ArrowColor;
import ru.backup.engine.enums.LocationKey;
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

public class MonsterFlyingMovementProcessTest {
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

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  @BeforeEach
  public void setUp() throws Exception {

    JavaDelegate getActiveMonstersMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getMonsterInfoMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate moveMonsterToLocationMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate getAnyLocationWithPlayerMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(GET_ACTIVE_MONSTERS_TASK, getActiveMonstersMock);
    Mocks.register(GET_MONSTER_INFO_TASK, getMonsterInfoMock);
    Mocks.register(MOVE_MONSTER_TO_LOCATION_TASK, moveMonsterToLocationMock);
    Mocks.register(GET_ANY_LOCATION_WITH_PLAYER_TASK, getAnyLocationWithPlayerMock);

    // список монстров
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      List<String> monsterCollection = List.of("monster_0001");
      execution.setVariableLocal("activeMonsters", new ArrayList<>(monsterCollection));
      return null;
    }).when(getActiveMonstersMock).execute(Mockito.any());

    // получить информацию по монстру
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      Map<String, Object> info = new HashMap<>();
      info.put("monsterType", MonsterType.FLYING);
      info.put("monsterSymbol", Symbols.SQUARE);
      info.put("monsterLocationId", "monster-current-location-id");
      execution.setVariable("monsterInfo", info);
      return null;
    }).when(getMonsterInfoMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> null).when(moveMonsterToLocationMock).execute(Mockito.any());
    Mockito.doAnswer(invocation -> null).when(getAnyLocationWithPlayerMock).execute(Mockito.any());
  }

  @ParameterizedTest(name = "{index} ==> Локация: {0}, Ожидаемый таск поиска: {1}, Монстр двигается: {3}")
  @MethodSource("provideLocationTestData")
  public void testMonster_MovementToPlayer_BasedOnLocation(
      LocationKey locationKey,
      String expectedSearchTask,
      String unexpectedSearchTask,
      boolean shouldMove) throws Exception {

    // 1. Создаем базовые общие моки
    JavaDelegate getPlayersByLocationMock = mock(JavaDelegate.class);
    JavaDelegate getLocationKeyMock = mock(JavaDelegate.class);
    JavaDelegate targetSearchMock = mock(JavaDelegate.class);
    JavaDelegate dummyMock = mock(JavaDelegate.class);

    // Регистрация обязательных общих шагов
    Mocks.register(GET_PLAYERS_BY_LOCATION_TASK, getPlayersByLocationMock);
    Mocks.register(GET_LOCATION_KEY_TASK, getLocationKeyMock);

    // Динамическая регистрация в зависимости от текущего параметра теста
    Mocks.register(expectedSearchTask, targetSearchMock);
    Mocks.register(unexpectedSearchTask, dummyMock);

    // 2. Описываем поведение моков
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("playerCollection", new ArrayList<>());
      return null;
    }).when(getPlayersByLocationMock).execute(Mockito.any());

    // Подставляем тип локации (неважно просто строка)
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("locationKey", locationKey);
      return null;
    }).when(getLocationKeyMock).execute(Mockito.any());

    // Симулируем успешный поиск локации игрока (работает для обоих тасков)
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("playerLocationId", shouldMove ? "new-loc-id" : "");
      return null;
    }).when(targetSearchMock).execute(Mockito.any());

    // 3. Запускаем экземпляр процесса фазы Мифов
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.MONSTER_MOVEMENT_PROCESS_KEY,
        withVariables(
            "mythosSymbol", new ArrayList<>(List.of(Symbols.SQUARE.name())),
            "arrowColor", ArrowColor.WHITE.name()
        )
    );

    // 4. Проверка выполнения шагов процесса
    Mockito.verify((JavaDelegate) Mocks.get(GET_MONSTER_INFO_TASK), Mockito.times(1)).execute(Mockito.any());
    Mockito.verify((JavaDelegate) Mocks.get(GET_LOCATION_KEY_TASK), Mockito.times(1)).execute(Mockito.any());
    Mockito.verify((JavaDelegate) Mocks.get(MOVE_MONSTER_TO_LOCATION_TASK),
        shouldMove ? Mockito.times(1) : Mockito.never()
    ).execute(Mockito.any());

    // Динамическая проверка: нужный таск вызвался 1 раз, ненужный — 0 раз
    Mockito.verify(targetSearchMock, Mockito.times(1)).execute(Mockito.any());
    Mockito.verify(dummyMock, Mockito.never()).execute(Mockito.any());

    // Проверяем сквозное прохождение ключевых шлюзов
    assertThat(pi).hasPassed(
        "Gateway_05iy8np", // Летающий монстр
        "Gateway_1nw4j6p", // Текущая локация (Небо / Улица)
        "Gateway_0df03ef", // Локация найдена?
        "Gateway_1u4xy8e"  // Объединяющий шлюз
    );

    assertThat(pi).isEnded();
  }

  private static Stream<Arguments> provideLocationTestData() {
    return Stream.of(
        // Для STREET: ищем ближайшего, обычный поиск не должен вызываться
        arguments(LocationKey.STREET, GET_NEAREST_LOCATION_WITH_PLAYER_TASK, GET_ANY_LOCATION_WITH_PLAYER_TASK, true),
        // Для SKY: летим к любому игроку, поиск ближайшего не должен вызываться
        arguments(LocationKey.SKY, GET_ANY_LOCATION_WITH_PLAYER_TASK, GET_NEAREST_LOCATION_WITH_PLAYER_TASK, true),
        // Для STREET: ищем ближайшего, обычный поиск не должен вызываться. монстр стоит локация не найдена
        arguments(LocationKey.STREET, GET_NEAREST_LOCATION_WITH_PLAYER_TASK, GET_ANY_LOCATION_WITH_PLAYER_TASK, false),
        // Для SKY: летим к любому игроку, поиск ближайшего не должен вызываться. монстр стоит локация не найдена
        arguments(LocationKey.SKY, GET_ANY_LOCATION_WITH_PLAYER_TASK, GET_NEAREST_LOCATION_WITH_PLAYER_TASK, false)
    );
  }
}

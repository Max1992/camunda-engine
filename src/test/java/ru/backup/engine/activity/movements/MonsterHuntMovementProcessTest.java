package ru.backup.engine.activity.movements;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;
import static org.mockito.Mockito.mock;
import static ru.backup.engine.delegates.Consts.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
import org.mockito.Mockito;
import ru.backup.engine.TestUtils;
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.enums.ArrowColor;
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

public class MonsterHuntMovementProcessTest {
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
    JavaDelegate getMonsterSpecialActionIdMock = Mockito.mock(JavaDelegate.class);
    JavaDelegate invokeMonsterSpecialActionMock = Mockito.mock(JavaDelegate.class);
    //JavaDelegate getPlayersByLocationMock = mock(JavaDelegate.class);

    Mocks.register(GET_ACTIVE_MONSTERS_TASK, getActiveMonstersMock);
    Mocks.register(GET_MONSTER_INFO_TASK, getMonsterInfoMock);
    Mocks.register(GET_MONSTER_SPECIAL_ACTION_ID_TASK, getMonsterSpecialActionIdMock);
    Mocks.register(INVOKE_MONSTER_SPECIAL_ACTION_TASK, invokeMonsterSpecialActionMock);
    //Mocks.register(GET_PLAYERS_BY_LOCATION_TASK, getPlayersByLocationMock);

    // список монстров
    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      List<String> monsterCollection = List.of("monster_0001");
      execution.setVariableLocal("activeMonsters", new ArrayList<>(monsterCollection));
      return null;
    }).when(getActiveMonstersMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      Map<String, Object> info = new HashMap<>();
      info.put("monsterType", MonsterType.HUNTING);
      info.put("monsterSymbol", Symbols.SQUARE);
      info.put("monsterLocationId", "monster-current-location-id");
      execution.setVariable("monsterInfo", info);
      return null;
    }).when(getMonsterInfoMock).execute(Mockito.any());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("monsterSpecialActionMessage", "special_msg_001");
      return null;
    }).when(getMonsterSpecialActionIdMock).execute(Mockito.any());

    //Mockito.doAnswer(invocation -> null).when(getPlayersByLocationMock).execute(Mockito.any());
  }

  /**
   * Процесс должен вызвать особое действие монстра и дождаться его завершения
   * */
  @Test
  @Timeout(value = 15, unit = TimeUnit.SECONDS)
  public void testMonster_ShouldSendAnfWaitSpecialAction() throws Exception {

    // 3. Запускаем экземпляр процесса фазы Мифов
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.MONSTER_MOVEMENT_PROCESS_KEY,
        withVariables(
            "mythosSymbol", new ArrayList<>(List.of(Symbols.SQUARE.name())),
            "correlation", "correlationId-001",
            "arrowColor", ArrowColor.WHITE.name()
        )
    );

    // 2. Проверяем, что первые два шага успешно выполнились
    Mockito.verify((JavaDelegate) Mocks.get(GET_MONSTER_SPECIAL_ACTION_ID_TASK), Mockito.times(1)).execute(Mockito.any());
    Mockito.verify((JavaDelegate) Mocks.get(INVOKE_MONSTER_SPECIAL_ACTION_TASK), Mockito.times(1)).execute(Mockito.any());

    assertThat(pi).hasPassed(
        "Activity_17qtood",
        "Activity_07rapzq" // отправить сообщение
    );

    // Процесс должен остановиться и ждать сообщения!
    // Он не должен быть завершен (isEnded() вернет false)
    assertThat(pi).isNotEnded();
    assertThat(pi).isWaitingAt("Event_0gt99xh");

    // 4. Имитируем, что внешняя система или подпроцесс завершили особое действие
    // Коррелируем сообщение, чтобы сдвинуть токен дальше
    runtimeService().createMessageCorrelation("MonsterSpecialActionCompletedMessage")
        .processInstanceId(pi.getId())
        .correlate();

    // 5. Проверяем, что после получения сообщения процесс успешно дошел до финала
    assertThat(pi).hasPassed("Event_0gt99xh");
    assertThat(pi).isEnded();
  }
}

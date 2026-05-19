package ru.backup.engine.activity.myth;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;
import static ru.backup.engine.activity.CallConsts.*;
import static ru.backup.engine.activity.ProcessConsts.MYTH_PROCESS_KEY;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import ru.backup.engine.TestUtils;
import ru.backup.engine.enums.ArrowColor;

/**
 * главный процесс мифа
 * */
public class MythProcessTest {
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
    TestUtils.deploy("Фаза мифа.bpmn");
  }

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  @BeforeEach
  public void setUp() throws Exception {
    CamundaMockito.registerCallActivityMock(PLACE_GATES_AND_MONSTERS_TASK);
    CamundaMockito.registerCallActivityMock(CLUE_DISTRIBUTION_TASK);
    CamundaMockito.registerCallActivityMock(MONSTER_MOVEMENT_WHITE_TASK);
    CamundaMockito.registerCallActivityMock(MONSTER_MOVEMENT_BLACK_TASK);
  }

  /**
   * обычный процесс через все шаги
   *
   * выключено. включить когда просеццы будут в своих файлах (отдельно)
   * в противном случае все процессы загружаются единовременно, и моки не работают
   * */
  //@Test
  public void testMyth_ShoutGoesHappyPath() throws Exception {

    JavaDelegate invokeMythSpecialActionMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(INVOKE_MYTH_SPECIAL_ACTION_TASK, invokeMythSpecialActionMock);

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("mythSpecialActionMessage", "special_msg_001");
      return null;
    }).when(invokeMythSpecialActionMock).execute(Mockito.any());

    ProcessInstance pi = runtimeService().createMessageCorrelation("msgMyth00001")
        .processInstanceBusinessKey("ARKHAM-SESSION-123")
        .setVariables(withVariables(
            "specialAction", "spec-002",
            "locationId", "loc-002",
            "correlation", "correlationId-001",
            "arrowColor", ArrowColor.WHITE.name()
        ))
        .correlateWithResult()
        .getProcessInstance();

    assertThat(pi).hasPassed(
        PLACE_GATES_AND_MONSTERS_TASK,
        CLUE_DISTRIBUTION_TASK,
        MONSTER_MOVEMENT_WHITE_TASK,
        MONSTER_MOVEMENT_BLACK_TASK,
        INVOKE_MYTH_SPECIAL_ACTION_TASK
    );

    assertThat(pi).isNotEnded();
    assertThat(pi).isWaitingAt("Event_16rb7md");

    // 4. Имитируем, что внешняя система или подпроцесс завершили особое действие
    // Коррелируем сообщение, чтобы сдвинуть токен дальше
    runtimeService().createMessageCorrelation("MythSpecialActionCompletedMessage")
        .processInstanceId(pi.getId())
        .correlate();

    // 5. Проверяем, что после получения сообщения процесс успешно дошел до финала
    assertThat(pi).hasPassed("Event_16rb7md");
    assertThat(pi).isEnded();
  }
}

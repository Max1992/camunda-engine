package ru.backup.engine.activity.checking.configuration;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;
import static ru.backup.engine.activity.CallConsts.*;
import static ru.backup.engine.delegates.Consts.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
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
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.delegates.form.embedded.ConvertInputMapListener;
import ru.backup.engine.delegates.form.embedded.ConvertOutputMapListener;
import ru.backup.engine.enums.SkillType;

/**
 * Простая проверка конфигурации удачи.
 * Только чтобы убедиться что процесс идет по основным веткам.
 * */
public class LuckConfigurationTest {
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
    TestUtils.deploy("Настройка параметров броска игроком.bpmn");
    TestUtils.deploy("Decision_0ejmcoa.dmn");
    TestUtils.deploy("totalDiceCount.dmn");
  }

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  @BeforeEach
  public void setUp() throws Exception {
    JavaDelegate getLuckConfigurationMock = Mockito.mock(JavaDelegate.class);

    Mocks.register(SERVICE_ROUTE_TASK, getLuckConfigurationMock);
    Mocks.register("convertInputMap", new ConvertInputMapListener());
    Mocks.register("convertOutputMap", new ConvertOutputMapListener());

    Mockito.doAnswer(invocation -> {
      var execution = (org.camunda.bpm.engine.delegate.DelegateExecution) invocation.getArgument(0);
      execution.setVariableLocal("skills", new ArrayList<>(List.of("SKILL_00005")));
      execution.setVariableLocal("ally", new ArrayList<>(List.of("ALLY_00006")));
      execution.setVariableLocal("base", 2);
      execution.setVariableLocal("availableClue", 2);
      execution.setVariableLocal("wasteOfClueCount", 0);
      execution.setVariableLocal("decisionRuleId", "Decision_0ejmcoa"); // задается в моделере
      return null;
    }).when(getLuckConfigurationMock).execute(Mockito.any());
  }

  @Test
  public void testLuckConfigration_ShouldGoThrough() {
    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.LUCK_CONFIGURATION_PROCESS_KEY,
        withVariables(
            "playerId", "player_0001",
            "difficulty", 1,
            "skillType", SkillType.LUCK.name(),
            "mythBonus", 0,
            "assignee", "admin",
            "targetService", "dummy"
        )
    );

    assertThat(pi).hasPassed(
        "Activity_1djzwns" // Получить базовые характеристики проверки
    );

    String userTaskId = "Activity_0849wro";

    assertThat(pi).isWaitingAt(userTaskId);

    Task task = taskService().createTaskQuery()
        .processInstanceId(pi.getId())
        .taskDefinitionKey(userTaskId)
        .singleResult();

    Map<String, Object> formVariables = Map.of(
        "approved", true
    );
    taskService().complete(task.getId(), formVariables);

    assertThat(pi).hasPassed("Gateway_1ygrtu1");

    assertThat(pi).isEnded();
  }
}

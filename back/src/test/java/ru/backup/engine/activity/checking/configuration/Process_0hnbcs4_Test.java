package ru.backup.engine.activity.checking.configuration;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.historyService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import ru.backup.engine.TestUtils;
import ru.backup.engine.activity.ProcessConsts;
import ru.backup.engine.delegates.form.embedded.ConvertInputMapListener;
import ru.backup.engine.delegates.form.embedded.ConvertOutputMapListener;
import ru.backup.engine.enums.SkillType;

/**
 * Вычислить доступное количество кубиков для броска
 * ProcessId: Process_0hnbcs4
 * */
public class Process_0hnbcs4_Test {
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
    Mocks.register("convertInputMap", new ConvertInputMapListener());
    Mocks.register("convertOutputMap", new ConvertOutputMapListener());
  }

  @Test
  public void testLuckConfigration_ShouldGoThrough() {

    Map<String, Object> context = new HashMap<>();
    context.put("skillType", SkillType.LUCK.name());
    context.put("mythBonus", 0);
    context.put("difficulty", 0);
    context.put("wasteOfClueCount", 0);

    Map<String, Object> playerProfile = new HashMap<>();
    playerProfile.put("luck", 2);
    playerProfile.put("skill", Collections.EMPTY_LIST);
    playerProfile.put("ally", Collections.EMPTY_LIST);
    playerProfile.put("simpleItem", Collections.EMPTY_LIST);

    ProcessInstance pi = runtimeService().startProcessInstanceByKey(ProcessConsts.LUCK_CONFIGURATION_PROCESS_KEY,
        withVariables(
            "context", context,
            "playerProfile", playerProfile
        )
    );

    assertThat(pi).hasPassed(
        "Gateway_0ytaopr", // Получить базовые характеристики проверки
        "Activity_14z645t" // DMN Вычислить достпуное значение кубиков
    );

    assertThat(pi).isEnded();

    Map<String, Object> outputVariables = historyService()
        .createHistoricVariableInstanceQuery()
        .processInstanceId(pi.getId())
        .unlimitedList()
        .stream()
        .collect(java.util.stream.Collectors.toMap(
            var -> var.getName(),
            var -> var.getValue()
        ));

    int finalLuck = (int) outputVariables.get("availableDices");
    Assertions.assertThat(finalLuck).isEqualTo(2);
  }
}

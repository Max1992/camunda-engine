package ru.backup.engine.dmn.skills;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.decisionService;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.backup.engine.TestUtils;

/**
 * тестирования вычисления кубиков при проверку удачи
 * */
public class LuckDiceTest {
  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .buildProcessEngine())
      .build();

  private static final String DMN_KEY = "Decision_0ejmcoa"; // ID таблицы из свойств DMN
  private static final String DMN_FILE_NAME = "Вычислить кубики удачи.dmn";

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  /**
   * Тестовые данные
   * skills      Навыки
   * ally        Союзники
   * clue        Количество потрачиваемых улик
   * expected    Ожидаемый рузультат
   * */
  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        arguments(List.of("SKILL_00005"), List.of("ALLY_00001"), 0, 3),
        arguments(List.of("SKILL_00005"), List.of("ALLY_00006"), 0, 2),
        arguments(List.of("SKILL_00005"), List.of("ALLY_00001", "ALLY_00006"), 0, 4),
        arguments(List.of(""), List.of(""), 0, 0),
        // с уликами
        arguments(List.of(""), List.of(""), 1, 1),
        arguments(List.of("SKILL_00005"), List.of(""), 0, 1),
        arguments(List.of("SKILL_00005"), List.of(""), 1, 3),
        arguments(List.of("SKILL_00005"), List.of(""), 2, 5)
    );
  }

  @ParameterizedTest(name = "{index} ==> Навык: {0}, Союзник: {1}, Улики {2} -> {3}")
  @MethodSource("provideTestData")
  public void testLuckDiceRules(
      List<String> skills,
      List<String> ally,
      int wasteOfClueCount,
      int expected
  ) throws FileNotFoundException {

    // Деплоим DMN (достаточно сделать один раз перед запуском параметров)
    TestUtils.deploy(DMN_FILE_NAME);

    // Формируем переменные контекста игры
    Map<String, Object> variables = Variables.createVariables()
        .putValue("skills", skills)
        .putValue("ally", ally)
        .putValue("wasteOfClueCount", wasteOfClueCount);

    // Вычисляем таблицу решений
    var result = decisionService().evaluateDecisionByKey(DMN_KEY)
        .variables(variables)
        .evaluate();

    // Проверяем итоговые очки
    int score = result.getSingleResult().getSingleEntry();
    assertThat(score).isEqualTo(expected);
  }
}

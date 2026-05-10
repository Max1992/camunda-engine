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
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

public class HidingTest {
  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .buildProcessEngine())
      .build();

  private static final String DMN_KEY = "Decision_HidingScore"; // ID таблицы из свойств DMN

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  private static Stream<Arguments> provideMonsterTestData() {
    return Stream.of(
        arguments(List.of("a"), 1),
        arguments(List.of("a", "b"), 2),
        arguments(List.of("a", "b", "c"), 3),
        arguments(List.of("a", "b", "c", "d"), 3),
        arguments(List.of("d", "e"), 0)
    );
  }

  @ParameterizedTest(name = "{index} ==> Тип: {0}, Ожидаем: {1}")
  @MethodSource("provideMonsterTestData")
  public void testMonsterMovementRules(
      List<Symbols> tags,
      int expectedScore) throws FileNotFoundException {

    // Деплоим DMN (достаточно сделать один раз перед запуском параметров)
    TestUtils.deploy("Скрытность.dmn");

    // Формируем переменные контекста игры
    Map<String, Object> variables = Variables.createVariables()
        .putValue("tags", tags);

    // Вычисляем таблицу решений
    var result = decisionService().evaluateDecisionByKey(DMN_KEY)
        .variables(variables)
        .evaluate();

    // Проверяем итоговые очки
    int score = result.getSingleResult().getSingleEntry();
    assertThat(score).isEqualTo(expectedScore);
  }
}

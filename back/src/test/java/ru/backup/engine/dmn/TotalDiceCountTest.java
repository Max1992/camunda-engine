package ru.backup.engine.dmn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.decisionService;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.FileNotFoundException;
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
 * вычисление итогового количества кубиков
 * */
public class TotalDiceCountTest {
  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .buildProcessEngine())
      .build();

  private static final String DMN_KEY = "totalDiceCount"; // ID таблицы из свойств DMN
  private static final String DMN_FILE_NAME = "Вычислить итоговое количество кубиков.dmn";

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  /**
   * параметры:
   *  base         - базовое значение
   *  dices        - количество кубиков с учетом бонусов (как временных так и постоянных)
   *  difficulty   - сложность проверки
   *    ожидаемое значение
   * */
  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        arguments(2, 1, -1, 2),
        arguments(1, 2, 0, 3),
        arguments(0, 0, -1, 0),
        arguments(2, 2, -4, 0)
    );
  }

  @ParameterizedTest(name = "{index} ==> База+Кубики-Сложность: {0}+{1}-{2} -> Ожидаем: {3}")
  @MethodSource("provideTestData")
  public void testTotalDiceCount(
      int base,
      int dices,
      int difficulty,
      int expected
  ) throws FileNotFoundException {
    // Деплоим DMN (достаточно сделать один раз перед запуском параметров)
    TestUtils.deploy(DMN_FILE_NAME);

    // Формируем переменные контекста игры
    Map<String, Object> variables = Variables.createVariables()
        .putValue("base", base)
        .putValue("dices", dices)
        .putValue("difficulty", difficulty);

    // Вычисляем таблицу решений
    var evaluated = decisionService().evaluateDecisionByKey(DMN_KEY)
        .variables(variables)
        .evaluate();

    // Проверяем итоговые очки
    int actual = evaluated.getSingleResult().getSingleEntry();
    assertThat(actual).isEqualTo(expected);
  }
}

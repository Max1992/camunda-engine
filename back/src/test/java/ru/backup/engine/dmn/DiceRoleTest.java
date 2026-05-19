package ru.backup.engine.dmn;

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
import ru.backup.engine.enums.DiceEncounterResult;
import ru.backup.engine.enums.PlayerStatus;

/**
 * тестирование результатов броска кубиков
 * например:
 *    [1,4,6] игрок обычный, ожидается 1 успех итог: успех
 *    [5,6,6] игрок обычный, ожидается 2 успеха итог: успех
 * */
public class DiceRoleTest {
  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .buildProcessEngine())
      .build();

  private static final String DMN_KEY = "Decision_1ryv9go"; // ID таблицы из свойств DMN
  private static final String DMN_FILE_NAME = "Проверка броска кубиков.dmn";

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        // игрок обычный
        arguments(PlayerStatus.NORMAL, 1, List.of(6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.NORMAL, 1, List.of(5), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.NORMAL, 1, List.of(4), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.NORMAL, 2, List.of(5, 6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.NORMAL, 2, List.of(4, 5), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.NORMAL, 2, List.of(1, 5), DiceEncounterResult.FAIL),
        // игрок благословлен
        arguments(PlayerStatus.BLESSED, 1, List.of(6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.BLESSED, 1, List.of(5), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.BLESSED, 1, List.of(4), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.BLESSED, 2, List.of(4, 5, 6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.BLESSED, 2, List.of(3, 4), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.BLESSED, 2, List.of(1, 5), DiceEncounterResult.FAIL),
        // игрок проклят
        arguments(PlayerStatus.CURSED, 1, List.of(6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.CURSED, 1, List.of(5), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.CURSED, 1, List.of(4), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.CURSED, 2, List.of(6, 6), DiceEncounterResult.SUCCESS),
        arguments(PlayerStatus.CURSED, 2, List.of(5, 6), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.CURSED, 2, List.of(4, 5), DiceEncounterResult.FAIL),
        arguments(PlayerStatus.CURSED, 2, List.of(1, 5), DiceEncounterResult.FAIL)
    );
  }

  @ParameterizedTest(name = "{index} ==> Статус: {0}, Количество успехов: {1} из {2} -> Ожидаем: {3}")
  @MethodSource("provideTestData")
  public void testDiceRoll(
      PlayerStatus playerStatus,
      int requiredSuccesses,
      List<Integer> diceRolls,
      DiceEncounterResult expectedEncounterResult) throws FileNotFoundException {

    // Деплоим DMN (достаточно сделать один раз перед запуском параметров)
    TestUtils.deploy(DMN_FILE_NAME);

    // Формируем переменные контекста игры
    Map<String, Object> variables = Variables.createVariables()
        .putValue("playerStatus", playerStatus)
        .putValue("requiredSuccesses", requiredSuccesses)
        .putValue("diceRolls", diceRolls);

    // Вычисляем таблицу решений
    var evaluated = decisionService().evaluateDecisionByKey(DMN_KEY)
        .variables(variables)
        .evaluate();

    // Проверяем итоговые очки
    String actual = evaluated.getSingleResult().getSingleEntry();
    assertThat(actual).isEqualTo(expectedEncounterResult.name());
  }
}

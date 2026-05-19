package ru.backup.engine.dmn.movement;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Stream;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.AfterEach;

import java.util.Map;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.backup.engine.TestUtils;
import ru.backup.engine.enums.MonsterType;
import ru.backup.engine.enums.Symbols;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MonsterMovementDmnTest {

  @RegisterExtension
  public static ProcessEngineExtension extension = ProcessEngineExtension.builder()
      .useProcessEngine(new StandaloneInMemProcessEngineConfiguration()
          .setJdbcUrl("jdbc:h2:mem:camunda-test;DB_CLOSE_DELAY=-1")
          .setDatabaseSchemaUpdate("true")
          .buildProcessEngine())
      .build();

  private static final String DMN_KEY = "Decision_MonsterMovement"; // ID таблицы из свойств DMN
  private static final String DMN_FILE_NAME = "Decision_MonsterMovement.dmn";

  @AfterEach
  void tearDown() {
    Mocks.reset();
  }

  private static Stream<Arguments> provideMonsterTestData() {
    return Stream.of(
        // 1. Обычный монстр, символы не совпали -> Спит
        arguments(MonsterType.NORMAL, List.of(Symbols.SQUARE), Symbols.CIRCLE, "STAY"),
        // 2. Стационарный монстр, символы совпали -> Все равно стоит
        arguments(MonsterType.STATIONARY, List.of(Symbols.SQUARE), Symbols.SQUARE, "STAY"),
        // 3. Обычный монстр, символы совпали -> Идет по стрелке
        arguments(MonsterType.NORMAL, List.of(Symbols.SQUARE), Symbols.SQUARE, "MOVE_BY_ARROW"),
        // 4. Летающий монстр, символы совпали -> Взлетает
        arguments(MonsterType.FLYING, List.of(Symbols.SQUARE), Symbols.SQUARE, "FLY_TO_SKY"),
        // 5. Монстр с особым ходом
        arguments(MonsterType.HUNTING, List.of(Symbols.SQUARE), Symbols.SQUARE, "HUNT_INVESTIGATOR")
    );
  }

  @ParameterizedTest(name = "{index} ==> Тип: {0}, Символы: {1}/{2} -> Ожидаем: {3}")
  @MethodSource("provideMonsterTestData")
  public void testMonsterMovementRules(
      MonsterType monsterType,
      List<Symbols> symbols,
      Symbols monsterSymbol,
      String expectedAction) throws FileNotFoundException {

    // Деплоим DMN (достаточно сделать один раз перед запуском параметров)
    TestUtils.deploy(DMN_FILE_NAME);

    // Формируем переменные контекста игры
    Map<String, Object> variables = Variables.createVariables()
        .putValue("monsterType", monsterType)
        .putValue("mythosSymbol", symbols)
        .putValue("monsterSymbol", monsterSymbol);

    // Вычисляем таблицу решений
    var result = decisionService().evaluateDecisionByKey(DMN_KEY)
        .variables(variables)
        .evaluate();

    // Проверяем итоговую команду движения
    String action = result.getSingleResult().getSingleEntry();
    assertThat(action).isEqualTo(expectedAction);
  }
}

package ru.backup.engine.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DiceRollerTest {
  /**
   * данные для теста
   * */
  private static Stream<Arguments> provideTestData() {
    return Stream.of(
        arguments(1),
        arguments(2),
        arguments(3),
        arguments(7),
        arguments(0),
        arguments(-1)
    );
  }

  @ParameterizedTest(name = "{index} ==> Кубики для броска: {0}, Количество успехов: {0}")
  @MethodSource("provideTestData")
  public void testDiceRoll(
    int availableDices
  ) {
    List<Integer> dices = DiceRoller.rollDices(availableDices);
    assertThat(dices.size()).isEqualTo(Math.max(availableDices, 0));
  }
}
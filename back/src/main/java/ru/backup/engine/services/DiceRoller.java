package ru.backup.engine.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DiceRoller {
  public static List<Integer> rollDices(int numberOfDices) {
    if (numberOfDices <= 0) {
      return new ArrayList<>();
    }

    List<Integer> diceResults = new ArrayList<>(numberOfDices);
    for (int i = 0; i < numberOfDices; i++) {
      int roll = ThreadLocalRandom.current().nextInt(1, 7);
      diceResults.add(roll);
    }
    return diceResults;
  }
}

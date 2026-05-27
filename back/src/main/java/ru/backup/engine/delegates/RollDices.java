package ru.backup.engine.delegates;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.backup.engine.services.DiceRoller;

@Component("rollDices")
public class RollDices extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    int availableDices = execution.getVariableLocal("availableDices");
    List<Integer> dices = DiceRoller.rollDices(availableDices);
    execution.setVariableLocal("diceRolls", new ArrayList<>(dices));
  }
}

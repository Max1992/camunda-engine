package ru.backup.engine.delegates;

import static ru.backup.engine.delegates.Consts.GET_MONSTER_STEP_COUNT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.enums.MonsterType;

@Component(GET_MONSTER_STEP_COUNT)
public class GetMonsterStepCount extends JavaDelegateHandler {

  private static final Logger log = LoggerFactory.getLogger(GetMonsterStepCount.class);

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    // символ монстра (такое имя потому что другое занято)
    try {
      MonsterType monsterType = execution.getVariableLocal("type");
      int stepsLeft = monsterType.equals(MonsterType.FAST) ? 2 : 1;
      execution.setVariableLocal("stepsLeft", stepsLeft);
    } catch (Exception e) {
      log.error("Ошибка преобразования символа монстра {}", e.getMessage());
    }
  }
}

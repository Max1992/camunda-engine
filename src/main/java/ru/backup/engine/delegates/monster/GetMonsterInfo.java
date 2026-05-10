package ru.backup.engine.delegates.monster;

import static ru.backup.engine.delegates.Consts.GET_MONSTER_INFO_TASK;

import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;
import ru.backup.engine.services.MonsterService;

@Component(GET_MONSTER_INFO_TASK)
public class GetMonsterInfo extends JavaDelegateHandler {

  private final MonsterService monsterService;

  public GetMonsterInfo(MonsterService monsterService) {
    this.monsterService = monsterService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {
    String monsterId = execution.getVariableLocal("monsterId");
    Map<String, Object> info = monsterService.getMonsterInfo(monsterId);
    execution.setVariable("monsterInfo", info);
  }
}

package ru.backup.engine.dmn;

import static ru.backup.engine.delegates.Consts.GET_ACTIVE_MONSTERS_TASK;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component(GET_ACTIVE_MONSTERS_TASK)
public class GetActiveMonsters extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

    List<String> monsterCollection = new ArrayList<>();
    for (int i = 0; i < 1; i++) {
      monsterCollection.add("active_monster_" + i);
    }

    execution.setVariableLocal("activeMonsters", new ArrayList<>(monsterCollection));
  }
}

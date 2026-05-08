package ru.backup.engine.delegates.monster;

import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import ru.backup.engine.delegates.JavaDelegateHandler;

@Component("startMonsterInvasion")
public class StartMonsterInvasion extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

  }
}

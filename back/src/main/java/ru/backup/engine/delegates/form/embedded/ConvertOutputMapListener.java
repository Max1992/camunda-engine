package ru.backup.engine.delegates.form.embedded;

import java.util.ArrayList;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;

@Component("convertOutputMap")
public class ConvertOutputMapListener implements TaskListener {

  private static final Logger log = LoggerFactory.getLogger(ConvertOutputMapListener.class);

  protected void onExecute(DelegateExecutionHandler execution) {
    Map<String, Object> variables = execution.getVariables();

    variables.forEach((key, value) -> {
      if (FormVariableUtils.isVariableAsList(key)) {
        String originalKey = FormVariableUtils.getOriginalListVariableName(key);
        String[] split = ((String) value).split(",");
        var list = new ArrayList<>();
        for (String s : split) {
          list.add(s.trim());
        }
        execution.setVariable(originalKey, list);
        log.info("Установить параметр: {}->{}", originalKey, list);
      }
    });
  }

  @Override
  public void notify(DelegateTask task) {
    DelegateExecutionHandler execution = new DelegateExecutionHandler(task.getExecution());
    onExecute(execution);
  }
}

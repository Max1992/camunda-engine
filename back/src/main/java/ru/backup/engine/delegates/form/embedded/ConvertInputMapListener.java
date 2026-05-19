package ru.backup.engine.delegates.form.embedded;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import ru.backup.engine.delegates.DelegateExecutionHandler;
import org.camunda.bpm.engine.delegate.TaskListener;

@Component("convertInputMap")
public class ConvertInputMapListener implements TaskListener {

  protected void onExecute(DelegateExecutionHandler execution) {
    Map<String, Object> variables = execution.getVariables();

    variables.forEach((key, value) -> {
      if (value instanceof List<?>) {
        List<String> list = new ArrayList<>((List) value);
        // важно удалить переменную
        execution.removeVariable(key);
        // установить новую переменную для списка
        execution.setVariable(FormVariableUtils.setVariableNameAsList(key), String.join(", ", list));
      }
    });
  }

  @Override
  public void notify(DelegateTask task) {
    DelegateExecutionHandler execution = new DelegateExecutionHandler(task.getExecution());
    onExecute(execution);
  }
}

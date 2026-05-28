package ru.backup.engine.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.models.SkillTestContext;

@Component("updateSkillTestContext")
public class UpdateSkillTestContext extends JavaDelegateHandler {

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    SkillTestContext context = mapper.convertValue(
        execution.getVariableLocal("context"),
        SkillTestContext.class
    );

    context.availableDices = execution.getVariableLocal("availableDices");
    context.base = execution.getVariableLocal("base");

    Map<String, Object> testContext = mapper.convertValue(context, Map.class);
    execution.setVariableLocal("context", testContext);
  }
}

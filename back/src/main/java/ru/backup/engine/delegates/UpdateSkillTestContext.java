package ru.backup.engine.delegates;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Component;
import ru.backup.engine.models.SkillTestContext;
import ru.backup.engine.services.SkillTestContextService;

@Component("updateSkillTestContext")
public class UpdateSkillTestContext extends JavaDelegateHandler {

  private final SkillTestContextService skillTestContextService;

  public UpdateSkillTestContext(SkillTestContextService skillTestContextService) {
    this.skillTestContextService = skillTestContextService;
  }

  @Override
  protected void onExecute(DelegateExecutionHandler execution) throws Exception {

    ObjectMapper mapper = new ObjectMapper();
    SkillTestContext context = mapper.convertValue(execution.getVariableLocal("context"), SkillTestContext.class);

    context.availableDices = execution.getVariableLocal("availableDices");
    context.base = execution.getVariableLocal("base");

    execution.setVariableLocal("context", mapper.convertValue(context, Map.class));
    skillTestContextService.update(context.id, context);
  }
}

package ru.backup.engine.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component("serviceRouter")
public class ServiceRouter implements JavaDelegate  {

  private final ApplicationContext context;

  public ServiceRouter(ApplicationContext context) {
    this.context = context;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String targetServiceParamName = "targetService";
    String targetServiceBeanName = (String) execution.getVariableLocal(targetServiceParamName);
    execution.removeVariable(targetServiceParamName);
    JavaDelegate targetDelegate = context.getBean(targetServiceBeanName, JavaDelegate.class);
    targetDelegate.execute(execution);
  }
}

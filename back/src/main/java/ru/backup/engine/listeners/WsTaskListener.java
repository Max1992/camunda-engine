package ru.backup.engine.listeners;

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component("wsTaskListener")
public class WsTaskListener implements TaskListener {

  private final SimpMessagingTemplate messagingTemplate;

  public WsTaskListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Override
  public void notify(DelegateTask delegateTask) {

    if (!TaskListener.EVENTNAME_CREATE.equals(delegateTask.getEventName())) {
      return;
    }

    String assignee = delegateTask.getAssignee();

    if (assignee == null) {
      return;
    }

    Map<String, Object> payload = new HashMap<>();
    payload.put("taskId", delegateTask.getId());
    payload.put("taskName", delegateTask.getName());
    // для отладки отправить всё
    payload.putAll(delegateTask.getVariables());

    // Отправляем строго пользователю "john_doe" в его личную очередь "/queue/personal-tasks"
    messagingTemplate.convertAndSendToUser(
        assignee,        // Имя пользователя (Principal)
        "/queue/tasks",  // Внутренний адрес очереди
        payload          // Данные
    );
  }
}

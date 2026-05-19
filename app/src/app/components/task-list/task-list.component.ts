import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { Subscription } from 'rxjs';
import { CamundaWebsocketService } from '../../services/camunda-websocket.service';
import { TaskRestService, CamundaTask } from '../../services/task-rest.service';

@Component({
  selector: 'app-task-list',
  standalone: true,
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit, OnDestroy {
  // Сигнал для реактивного хранения массива задач
  public tasks = signal<CamundaTask[]>([]);

  private wsService = inject(CamundaWebsocketService);
  private restService = inject(TaskRestService);
  private taskSubscription!: Subscription;

  // Имитируем сохраненный токен авторизации (в реальном приложении берется из AuthService)
  private token: string = 'mock-jwt-token-john-doe';

  ngOnInit(): void {
    // 1. Загрузка старых задач по REST
    this.restService.getMyTasks(this.token).subscribe({
      next: (initialTasks) => this.tasks.set(initialTasks),
      error: (err) => console.warn('Бэкенд недоступен (REST), ждем сокеты...', err)
    });

    // 2. Подключение к WebSockets
    this.wsService.connect(this.token);

    // 3. Подписка на пуши новых задач
    this.taskSubscription = this.wsService.task$.subscribe((newCamundaTask: CamundaTask) => {
      // Добавляем новую таску наверх списка
      this.tasks.update(currentTasks => [newCamundaTask, ...currentTasks]);
    });
  }

  openForm(taskId: string): void {
    alert(`Открываем экранную форму для Camunda таски: ${taskId}`);
  }

  ngOnDestroy(): void {
    if (this.taskSubscription) this.taskSubscription.unsubscribe();
    this.wsService.disconnect();
  }
}

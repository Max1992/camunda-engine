import { Injectable, OnDestroy } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { CamundaTask } from './task-rest.service';

@Injectable({
  providedIn: 'root'
})
export class CamundaWebsocketService implements OnDestroy {
  private client!: Client;
  private taskSubject = new Subject<CamundaTask>();

  // Открытый поток задач для компонентов
  public task$: Observable<CamundaTask> = this.taskSubject.asObservable();

  connect(jwtToken: string): void {
    if (this.client && this.client.active) return;

    this.client = new Client({
      brokerURL: 'ws://localhost:5001/ws-camunda',
      connectHeaders: {
        Authorization: `Bearer ${jwtToken}`,
        userName: `john_doe`
      },
      reconnectDelay: 5000,
      debug: (str: string) => {
        console.log('--- [STOMP Client Log] ---', str);
      }
    });

    this.client.onConnect = () => {
      console.log('Успешное WebSocket подключение к Camunda брокеру.');

      // Подписка на персональную очередь Spring Security /user/queue/...
      this.client.subscribe('/user/queue/personal-tasks', (message: Message) => {
        if (message.body) {
          const newTask: CamundaTask = JSON.parse(message.body);
          this.taskSubject.next(newTask); // Передаем задачу в поток RxJS
        }
      });
    };

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) this.client.deactivate();
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}

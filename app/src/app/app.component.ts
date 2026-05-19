import { Component } from '@angular/core';
import { TaskListComponent } from './components/task-list/task-list.component';
import { RouterOutlet } from "@angular/router";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [TaskListComponent, RouterOutlet], // ВАЖНО: импортируем наш компонент сюда
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'camunda-frontend';
}

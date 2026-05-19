import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CamundaTask {
  taskId: string;
  taskName: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskRestService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:5001/engine-rest';

  getMyTasks(token: string): Observable<CamundaTask[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const username = 'john_doe';
    return this.http.get<CamundaTask[]>(`${this.apiUrl}/task?assignee=${username}`, { headers });
  }
}

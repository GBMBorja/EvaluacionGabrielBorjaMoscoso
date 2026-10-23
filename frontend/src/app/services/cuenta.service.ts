import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cuenta, CuentaRequest, CuentaResumen } from '../models/cuenta.model';

@Injectable({
  providedIn: 'root'
})
export class CuentaService {
  private readonly apiUrl = 'http://localhost:8080/api/cuentas';

  constructor(private http: HttpClient) {}

  getCuentas(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.apiUrl);
  }

  crearCuenta(cuenta: CuentaRequest): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.apiUrl, cuenta);
  }

  getResumen(id: number): Observable<CuentaResumen> {
    return this.http.get<CuentaResumen>(`${this.apiUrl}/${id}/resumen`);
  }
}

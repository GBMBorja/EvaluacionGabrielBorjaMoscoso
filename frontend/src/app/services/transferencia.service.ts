import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transferencia, TransferenciaFiltros, TransferenciaRequest } from '../models/transferencia.model';

@Injectable({
  providedIn: 'root'
})
export class TransferenciaService {
  private readonly apiUrl = 'http://localhost:8080/api/transferencias';

  constructor(private http: HttpClient) {}

  ejecutarTransferencia(request: TransferenciaRequest): Observable<Transferencia> {
    return this.http.post<Transferencia>(this.apiUrl, request);
  }

  getTransferencias(filtros?: TransferenciaFiltros): Observable<Transferencia[]> {
    let params = new HttpParams();

    if (filtros) {
      if (filtros.cuentaOrigen) {
        params = params.set('cuentaOrigen', filtros.cuentaOrigen);
      }
      if (filtros.cuentaDestino) {
        params = params.set('cuentaDestino', filtros.cuentaDestino);
      }
      if (filtros.fechaInicio) {
        params = params.set('fechaInicio', filtros.fechaInicio);
      }
      if (filtros.fechaFin) {
        params = params.set('fechaFin', filtros.fechaFin);
      }
      if (filtros.estado) {
        params = params.set('estado', filtros.estado);
      }
    }

    return this.http.get<Transferencia[]>(this.apiUrl, { params });
  }
}

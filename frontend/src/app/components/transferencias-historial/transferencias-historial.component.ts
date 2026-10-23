import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Transferencia, TransferenciaFiltros } from '../../models/transferencia.model';
import { TransferenciaService } from '../../services/transferencia.service';

@Component({
  selector: 'app-transferencias-historial',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <div class="card-header">
        <div class="header-left">
          <h2>Historial de Movimientos y Transferencias (CA-05)</h2>
          <span class="count-badge">{{ transferencias.length }} registro(s)</span>
        </div>
        <div class="header-actions">
          <button type="button" class="btn btn-black btn-sm" (click)="cargarTransferencias()">
            Actualizar Historial ⟳
          </button>
        </div>
      </div>

      <!-- Filtros Opcionales -->
      <div class="filters-container">
        <div class="filter-group">
          <label>Cuenta Origen</label>
          <input
            type="text"
            class="form-control"
            [(ngModel)]="filtros.cuentaOrigen"
            placeholder="10 dígitos..."
          />
        </div>
        <div class="filter-group">
          <label>Cuenta Destino</label>
          <input
            type="text"
            class="form-control"
            [(ngModel)]="filtros.cuentaDestino"
            placeholder="10 dígitos..."
          />
        </div>
        <div class="filter-group">
          <label>Estado</label>
          <select class="form-control" [(ngModel)]="filtros.estado">
            <option [ngValue]="undefined">Todos los estados</option>
            <option value="COMPLETADA">COMPLETADA</option>
            <option value="REQUIERE_REVISION">REQUIERE_REVISION</option>
            <option value="RECHAZADA">RECHAZADA</option>
          </select>
        </div>
        <div class="filter-actions">
          <button type="button" class="btn btn-black" (click)="cargarTransferencias()">
            Filtrar
          </button>
          <button type="button" class="btn btn-red" (click)="limpiarFiltros()">
            Limpiar
          </button>
        </div>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Fecha y Hora</th>
              <th>Cuenta Origen</th>
              <th>Cuenta Destino</th>
              <th>Monto</th>
              <th>Estado</th>
              <th>Sospechosa</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let t of transferencias">
              <td><strong>#{{ t.id }}</strong></td>
              <td>{{ t.fecha | date:'yyyy-MM-dd HH:mm:ss' }}</td>
              <td>
                <div>{{ t.numeroCuentaOrigen }}</div>
                <small style="color: #666;">{{ t.titularOrigen }}</small>
              </td>
              <td>
                <div>{{ t.numeroCuentaDestino }}</div>
                <small style="color: #666;">{{ t.titularDestino }}</small>
              </td>
              <td>
                <strong>{{ t.monto | currency:'USD':'symbol':'1.2-2' }}</strong>
              </td>
              <td>
                <span
                  class="badge"
                  [class.badge-active]="t.estado === 'COMPLETADA'"
                  [class.badge-review]="t.estado === 'REQUIERE_REVISION'"
                  [class.badge-inactive]="t.estado === 'RECHAZADA'"
                >
                  {{ t.estado }}
                </span>
              </td>
              <td>
                <span *ngIf="t.sospechosa" class="badge badge-review" [title]="t.motivoSospecha || 'Operación sospechosa'">
                  ⚠️ Sí ({{ t.motivoSospecha }})
                </span>
                <span *ngIf="!t.sospechosa" style="color: #666; font-size: 12px;">
                  No
                </span>
              </td>
            </tr>
            <tr *ngIf="transferencias.length === 0">
              <td colspan="7" style="text-align: center; padding: 24px; color: #666666;">
                No se registraron movimientos con los filtros seleccionados.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .card {
      background: #ffffff;
      border: 1px solid #000000;
      border-radius: 6px;
      margin-bottom: 24px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      overflow: hidden;
    }

    .card-header {
      background-color: #ffffff;
      border-bottom: 2px solid #000000;
      padding: 14px 18px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
    }

    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .header-left h2 {
      font-size: 16px;
      font-weight: bold;
      color: #000000;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin: 0;
    }

    .count-badge {
      background-color: #000000;
      color: #ffffff;
      font-size: 11px;
      padding: 2px 8px;
      border-radius: 12px;
      font-weight: bold;
    }

    .filters-container {
      background-color: #f9fafb;
      border-bottom: 1px solid #e5e7eb;
      padding: 14px 18px;
      display: flex;
      gap: 12px;
      align-items: flex-end;
      flex-wrap: wrap;
    }

    .filter-group {
      flex: 1;
      min-width: 180px;
    }

    .filter-group label {
      display: block;
      font-size: 12px;
      font-weight: bold;
      margin-bottom: 4px;
    }

    .filter-actions {
      display: flex;
      gap: 8px;
    }

    @media (max-width: 768px) {
      .filters-container {
        flex-direction: column;
        align-items: stretch;
      }
      .filter-actions {
        margin-top: 8px;
      }
      .filter-actions .btn {
        flex: 1;
      }
    }
  `]
})
export class TransferenciasHistorialComponent implements OnInit {
  @Input() transferencias: Transferencia[] = [];

  filtros: TransferenciaFiltros = {};

  constructor(private transferenciaService: TransferenciaService) {}

  ngOnInit(): void {
    if (!this.transferencias || this.transferencias.length === 0) {
      this.cargarTransferencias();
    }
  }

  cargarTransferencias(): void {
    this.transferenciaService.getTransferencias(this.filtros).subscribe({
      next: (data) => {
        this.transferencias = data;
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros = {};
    this.cargarTransferencias();
  }
}

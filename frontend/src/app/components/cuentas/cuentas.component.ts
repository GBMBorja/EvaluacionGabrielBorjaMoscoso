import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Cuenta, CuentaResumen } from '../../models/cuenta.model';
import { CuentaService } from '../../services/cuenta.service';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-cuentas',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './cuentas.component.html',
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

    .header-actions {
      display: flex;
      gap: 8px;
      align-items: center;
      flex-wrap: wrap;
    }

    .search-bar-container {
      background-color: #f9fafb;
      border-bottom: 1px solid #e5e7eb;
      padding: 10px 18px;
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .search-input-wrapper {
      position: relative;
      flex: 1;
      max-width: 450px;
    }

    .search-input {
      width: 100%;
      padding: 8px 12px 8px 34px;
      font-size: 13px;
      border: 1px solid #000000;
      border-radius: 4px;
      outline: none;
    }

    .search-input:focus {
      box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.15);
    }

    .search-icon {
      position: absolute;
      left: 10px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 14px;
      color: #6b7280;
    }

    .resumen-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }

    .resumen-card {
      border: 1px solid #000000;
      padding: 12px;
      border-radius: 4px;
      background: #f9fafb;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .resumen-label {
      font-size: 12px;
      color: #4b5563;
      text-transform: uppercase;
      font-weight: bold;
    }

    .resumen-value {
      font-size: 18px;
      font-weight: bold;
      color: #000000;
    }

    .alert-card {
      background-color: #fef2f2;
      border-color: #b91c1c;
    }

    @media (max-width: 640px) {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
      }
      .header-actions {
        width: 100%;
        justify-content: space-between;
      }
      .search-bar-container {
        flex-direction: column;
        align-items: stretch;
      }
      .search-input-wrapper {
        max-width: 100%;
      }
      .resumen-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class CuentasComponent implements OnChanges {
  @Input() cuentas: Cuenta[] = [];
  @Output() nuevaCuentaClicked = new EventEmitter<void>();

  cuentasFiltradas: Cuenta[] = [];
  terminoBusqueda = '';
  ordenAscendente = false;

  modalResumenAbierto = false;
  resumenSeleccionado: CuentaResumen | null = null;

  constructor(private cuentaService: CuentaService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cuentas']) {
      this.aplicarFiltrosYOrdenamiento();
    }
  }

  onBusquedaChange(): void {
    this.aplicarFiltrosYOrdenamiento();
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.aplicarFiltrosYOrdenamiento();
  }

  toggleOrdenSaldo(): void {
    this.ordenAscendente = !this.ordenAscendente;
    this.aplicarFiltrosYOrdenamiento();
  }

  aplicarFiltrosYOrdenamiento(): void {
    let lista = [...(this.cuentas || [])];

    // Filtrar por término de búsqueda (número de cuenta o titular)
    if (this.terminoBusqueda && this.terminoBusqueda.trim() !== '') {
      const termino = this.terminoBusqueda.trim().toLowerCase();
      lista = lista.filter(c =>
        (c.numeroCuenta && c.numeroCuenta.toLowerCase().includes(termino)) ||
        (c.titular && c.titular.toLowerCase().includes(termino))
      );
    }

    // Ordenar por saldo
    lista.sort((a, b) => {
      const saldoA = a.saldo || 0;
      const saldoB = b.saldo || 0;
      return this.ordenAscendente ? saldoA - saldoB : saldoB - saldoA;
    });

    this.cuentasFiltradas = lista;
  }

  abrirModalNuevaCuenta(): void {
    this.nuevaCuentaClicked.emit();
  }

  verResumen(cuenta: Cuenta): void {
    if (!cuenta.id) return;
    this.cuentaService.getResumen(cuenta.id).subscribe({
      next: (resumen) => {
        this.resumenSeleccionado = resumen;
        this.modalResumenAbierto = true;
      },
      error: (err) => {
        alert(err.error?.mensaje || 'Error al obtener resumen de la cuenta.');
      }
    });
  }

  cerrarModalResumen(): void {
    this.modalResumenAbierto = false;
    this.resumenSeleccionado = null;
  }
}

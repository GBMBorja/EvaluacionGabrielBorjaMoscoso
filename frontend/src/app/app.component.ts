import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CuentaService } from './services/cuenta.service';
import { TransferenciaService } from './services/transferencia.service';
import { Cuenta } from './models/cuenta.model';
import { Transferencia } from './models/transferencia.model';
import { CuentasComponent } from './components/cuentas/cuentas.component';
import { TransferenciaFormComponent } from './components/transferencia-form/transferencia-form.component';
import { TransferenciasHistorialComponent } from './components/transferencias-historial/transferencias-historial.component';
import { CuentaFormComponent } from './components/cuenta-form/cuenta-form.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    CuentasComponent,
    TransferenciaFormComponent,
    TransferenciasHistorialComponent,
    CuentaFormComponent
  ],
  templateUrl: './app.component.html',
  styles: [`
    .app-wrapper {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .navbar {
      background-color: #000000;
      color: #ffffff;
      padding: 16px 24px;
      border-bottom: 3px solid #333333;
    }

    .navbar-content {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 14px;
    }

    .brand-icon {
      font-size: 28px;
    }

    .brand-title {
      font-size: 18px;
      font-weight: bold;
      letter-spacing: 0.5px;
      margin: 0;
    }

    .brand-subtitle {
      font-size: 12px;
      color: #9ca3af;
      margin-top: 2px;
    }

    .navbar-status {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      background: #1f2937;
      padding: 6px 14px;
      border-radius: 20px;
      border: 1px solid #374151;
    }

    .status-indicator {
      color: #22c55e;
      font-size: 14px;
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0% { opacity: 1; }
      50% { opacity: 0.4; }
      100% { opacity: 1; }
    }

    .main-container {
      max-width: 1200px;
      margin: 24px auto;
      padding: 0 16px;
      flex: 1;
      width: 100%;
    }

    .kpi-banner {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      margin-bottom: 24px;
    }

    .kpi-card {
      background: #ffffff;
      border: 1px solid #000000;
      border-radius: 6px;
      padding: 16px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .kpi-label {
      font-size: 11px;
      text-transform: uppercase;
      color: #6b7280;
      font-weight: bold;
      letter-spacing: 0.5px;
    }

    .kpi-value {
      font-size: 22px;
      font-weight: bold;
      color: #000000;
    }

    .footer {
      background-color: #ffffff;
      border-top: 1px solid #000000;
      padding: 16px;
      text-align: center;
      font-size: 12px;
      color: #4b5563;
      margin-top: auto;
    }

    @media (max-width: 900px) {
      .kpi-banner {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    @media (max-width: 500px) {
      .kpi-banner {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class AppComponent implements OnInit {
  cuentas: Cuenta[] = [];
  transferencias: Transferencia[] = [];
  modalCrearCuentaAbierto = false;

  constructor(
    private cuentaService: CuentaService,
    private transferenciaService: TransferenciaService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.cargarCuentas();
    this.cargarTransferencias();
  }

  cargarCuentas(): void {
    this.cuentaService.getCuentas().subscribe({
      next: (data) => {
        this.cuentas = data || [];
      },
      error: (err) => console.error('Error al cargar cuentas:', err)
    });
  }

  cargarTransferencias(): void {
    this.transferenciaService.getTransferencias().subscribe({
      next: (data) => {
        this.transferencias = data || [];
      },
      error: (err) => console.error('Error al cargar transferencias:', err)
    });
  }

  get totalSaldo(): number {
    return this.cuentas.reduce((acc, c) => acc + (c.saldo || 0), 0);
  }

  get cuentasActivasCount(): number {
    return this.cuentas.filter(c => c.estado === 'ACTIVA').length;
  }

  abrirModalCrearCuenta(): void {
    this.modalCrearCuentaAbierto = true;
  }

  cerrarModalCrearCuenta(): void {
    this.modalCrearCuentaAbierto = false;
  }

  onCuentaCreada(cuenta: Cuenta): void {
    this.cerrarModalCrearCuenta();
    this.cargarCuentas();
  }

  onTransferenciaExitosa(transaccion: Transferencia): void {
    // Al ejecutar una transferencia, se actualizan los saldos de las cuentas y el historial
    this.cargarDatos();
  }
}

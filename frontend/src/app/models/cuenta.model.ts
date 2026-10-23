export type TipoCuenta = 'AHORRO' | 'CORRIENTE';
export type EstadoCuenta = 'ACTIVA' | 'INACTIVA';

export interface Cuenta {
  id?: number;
  numeroCuenta: string;
  titular: string;
  tipoCuenta: TipoCuenta;
  saldo: number;
  estado: EstadoCuenta;
  fechaCreacion?: string;
}

export interface CuentaRequest {
  numeroCuenta: string;
  titular: string;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  estado: EstadoCuenta;
}

export interface CuentaResumen {
  cuentaId: number;
  numeroCuenta: string;
  titular: string;
  saldoActual: number;
  totalTransferidoHoy: number;
  cantidadTransferenciasHoy: number;
  cantidadSospechosas: number;
}

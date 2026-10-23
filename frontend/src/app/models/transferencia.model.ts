export type EstadoTransferencia = 'COMPLETADA' | 'RECHAZADA' | 'REQUIERE_REVISION';

export interface Transferencia {
  id?: number;
  numeroCuentaOrigen: string;
  titularOrigen?: string;
  numeroCuentaDestino: string;
  titularDestino?: string;
  monto: number;
  fecha: string;
  estado: EstadoTransferencia;
  sospechosa: boolean;
  motivoSospecha?: string;
  motivoRechazo?: string;
}

export interface TransferenciaRequest {
  numeroCuentaOrigen: string;
  numeroCuentaDestino: string;
  monto: number;
}

export interface TransferenciaFiltros {
  cuentaOrigen?: string;
  cuentaDestino?: string;
  fechaInicio?: string;
  fechaFin?: string;
  estado?: EstadoTransferencia;
}

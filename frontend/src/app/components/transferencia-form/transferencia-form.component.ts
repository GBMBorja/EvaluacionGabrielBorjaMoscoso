import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { TransferenciaService } from '../../services/transferencia.service';
import { Cuenta } from '../../models/cuenta.model';
import { Transferencia } from '../../models/transferencia.model';

export const cuentasDistintasValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const origen = control.get('numeroCuentaOrigen')?.value;
  const destino = control.get('numeroCuentaDestino')?.value;
  if (origen && destino && origen === destino) {
    return { cuentasIguales: true };
  }
  return null;
};

@Component({
  selector: 'app-transferencia-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transferencia-form.component.html',
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
      background-color: #000000;
      color: #ffffff;
      padding: 12px 18px;
    }

    .card-header h2 {
      font-size: 15px;
      font-weight: bold;
      margin: 0;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .card-body {
      padding: 18px;
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
    }

    .saldo-preview {
      background-color: #f9fafb;
      border: 1px dashed #000000;
      padding: 10px 14px;
      border-radius: 4px;
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
      gap: 8px;
      font-size: 13px;
    }

    .actions {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      margin-top: 8px;
    }

    @media (max-width: 768px) {
      .form-grid {
        grid-template-columns: 1fr;
      }
      .actions {
        flex-direction: column-reverse;
      }
      .actions .btn {
        width: 100%;
      }
    }
  `]
})
export class TransferenciaFormComponent implements OnChanges {
  @Input() cuentas: Cuenta[] = [];
  @Output() transferenciaExitosa = new EventEmitter<Transferencia>();

  form: FormGroup;
  loading = false;
  mensajeExito = '';
  mensajeError = '';

  cuentasActivas: Cuenta[] = [];

  constructor(private fb: FormBuilder, private transferenciaService: TransferenciaService) {
    this.form = this.fb.group({
      numeroCuentaOrigen: ['', Validators.required],
      numeroCuentaDestino: ['', Validators.required],
      monto: ['', [Validators.required, Validators.min(0.01)]]
    }, { validators: cuentasDistintasValidator });
  }

  get f() {
    return this.form.controls;
  }

  get cuentaOrigenSeleccionada(): Cuenta | undefined {
    const num = this.form.value.numeroCuentaOrigen;
    return this.cuentas.find(c => c.numeroCuenta === num);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['cuentas']) {
      this.cuentasActivas = (this.cuentas || []).filter(c => c.estado === 'ACTIVA');
    }
  }

  onSubmit(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.transferenciaService.ejecutarTransferencia(this.form.value).subscribe({
      next: (resp) => {
        this.loading = false;
        let estadoNota = resp.estado;
        if (resp.sospechosa) {
          estadoNota += ` (⚠️ Notificada como Sospechosa: ${resp.motivoSospecha})`;
        }
        this.mensajeExito = `Transferencia por $${resp.monto.toFixed(2)} procesada exitosamente. Estado: ${estadoNota}.`;
        this.form.reset({
          numeroCuentaOrigen: '',
          numeroCuentaDestino: '',
          monto: ''
        });
        this.transferenciaExitosa.emit(resp);
      },
      error: (err) => {
        this.loading = false;
        this.mensajeError = err.error?.mensaje || 'Error al procesar la transferencia.';
      }
    });
  }

  limpiarFormulario(): void {
    this.mensajeExito = '';
    this.mensajeError = '';
    this.form.reset({
      numeroCuentaOrigen: '',
      numeroCuentaDestino: '',
      monto: ''
    });
  }
}

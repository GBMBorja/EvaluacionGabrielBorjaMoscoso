import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CuentaService } from '../../services/cuenta.service';
import { ModalComponent } from '../modal/modal.component';
import { Cuenta } from '../../models/cuenta.model';

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalComponent],
  template: `
    <app-modal [isOpen]="isOpen" [title]="'Registrar Nueva Cuenta Bancaria'" (close)="onCancel()">
      <form [formGroup]="form" (ngSubmit)="onSubmit()">
        <div class="alert alert-danger" *ngIf="errorMessage">
          {{ errorMessage }}
        </div>

        <div class="form-group">
          <label class="form-label">Número de Cuenta (10 Dígitos) *</label>
          <input
            type="text"
            class="form-control"
            [class.is-invalid]="f['numeroCuenta'].invalid && (f['numeroCuenta'].dirty || f['numeroCuenta'].touched)"
            formControlName="numeroCuenta"
            placeholder="Ej: 1020304050"
            maxlength="10"
          />
          <div class="invalid-feedback" *ngIf="f['numeroCuenta'].errors?.['required']">
            El número de cuenta es obligatorio.
          </div>
          <div class="invalid-feedback" *ngIf="f['numeroCuenta'].errors?.['pattern']">
            Debe contener exactamente 10 dígitos numéricos.
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Nombre del Titular *</label>
          <input
            type="text"
            class="form-control"
            [class.is-invalid]="f['titular'].invalid && (f['titular'].dirty || f['titular'].touched)"
            formControlName="titular"
            placeholder="Ej: Carlos Pérez Gómez"
          />
          <div class="invalid-feedback" *ngIf="f['titular'].errors?.['required']">
            El nombre del titular es obligatorio.
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Tipo de Cuenta *</label>
          <select class="form-control" formControlName="tipoCuenta">
            <option value="AHORRO">Ahorros</option>
            <option value="CORRIENTE">Corriente</option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Saldo Inicial ($) *</label>
          <input
            type="number"
            step="0.01"
            class="form-control"
            [class.is-invalid]="f['saldoInicial'].invalid && (f['saldoInicial'].dirty || f['saldoInicial'].touched)"
            formControlName="saldoInicial"
            placeholder="0.00"
          />
          <div class="invalid-feedback" *ngIf="f['saldoInicial'].errors?.['required']">
            El saldo inicial es obligatorio.
          </div>
          <div class="invalid-feedback" *ngIf="f['saldoInicial'].errors?.['min']">
            El saldo no puede ser negativo (≥ 0.00).
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Estado Inicial *</label>
          <select class="form-control" formControlName="estado">
            <option value="ACTIVA">ACTIVA</option>
            <option value="INACTIVA">INACTIVA</option>
          </select>
        </div>

        <div class="modal-footer" style="padding: 12px 0 0 0; margin-top: 18px;">
          <button type="button" class="btn btn-red" (click)="onCancel()">
            Cancelar
          </button>
          <button type="submit" class="btn btn-green" [disabled]="form.invalid || loading">
            {{ loading ? 'Guardando...' : 'Guardar Cuenta' }}
          </button>
        </div>
      </form>
    </app-modal>
  `
})
export class CuentaFormComponent {
  @Input() isOpen = false;
  @Output() cuentaCreada = new EventEmitter<Cuenta>();
  @Output() cancelar = new EventEmitter<void>();

  form: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private cuentaService: CuentaService) {
    this.form = this.fb.group({
      numeroCuenta: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      titular: ['', [Validators.required, Validators.minLength(3)]],
      tipoCuenta: ['AHORRO', Validators.required],
      saldoInicial: [0.00, [Validators.required, Validators.min(0)]],
      estado: ['ACTIVA', Validators.required]
    });
  }

  get f() {
    return this.form.controls;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.cuentaService.crearCuenta(this.form.value).subscribe({
      next: (cuenta) => {
        this.loading = false;
        this.form.reset({
          numeroCuenta: '',
          titular: '',
          tipoCuenta: 'AHORRO',
          saldoInicial: 0.00,
          estado: 'ACTIVA'
        });
        this.cuentaCreada.emit(cuenta);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.mensaje || 'Error al registrar la cuenta bancaria.';
      }
    });
  }

  onCancel(): void {
    this.errorMessage = '';
    this.cancelar.emit();
  }
}

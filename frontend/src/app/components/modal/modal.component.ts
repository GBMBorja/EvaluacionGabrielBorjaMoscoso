import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="modal-backdrop" *ngIf="isOpen" (click)="onBackdropClick($event)">
      <div class="modal-card" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>{{ title }}</h3>
          <button type="button" class="btn-close" (click)="onClose()">×</button>
        </div>
        <div class="modal-body">
          <ng-content></ng-content>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-backdrop {
      position: fixed;
      top: 0;
      left: 0;
      width: 100vw;
      height: 100vh;
      background: rgba(0, 0, 0, 0.75);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 9999;
      padding: 16px;
    }

    .modal-card {
      background: #ffffff;
      border: 2px solid #000000;
      border-radius: 6px;
      width: 100%;
      max-width: 520px;
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.35);
      overflow: hidden;
      animation: modalScale 0.2s ease-out;
    }

    @keyframes modalScale {
      from { transform: scale(0.95); opacity: 0; }
      to { transform: scale(1); opacity: 1; }
    }

    .modal-header {
      background-color: #000000;
      color: #ffffff;
      padding: 12px 18px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .modal-header h3 {
      font-size: 15px;
      font-weight: bold;
      margin: 0;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .btn-close {
      background: transparent;
      border: none;
      color: #ffffff;
      font-size: 24px;
      line-height: 1;
      cursor: pointer;
    }

    .modal-body {
      padding: 20px;
      max-height: 80vh;
      overflow-y: auto;
    }
  `]
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Output() close = new EventEmitter<void>();

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    this.onClose();
  }
}

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IExpediente } from '../expediente.model';
import { ExpedienteService } from '../service/expediente.service';

@Component({
  standalone: true,
  templateUrl: './expediente-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ExpedienteDeleteDialogComponent {
  expediente?: IExpediente;

  protected expedienteService = inject(ExpedienteService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.expedienteService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICelda } from '../celda.model';
import { CeldaService } from '../service/celda.service';

@Component({
  standalone: true,
  templateUrl: './celda-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CeldaDeleteDialogComponent {
  celda?: ICelda;

  protected celdaService = inject(CeldaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.celdaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

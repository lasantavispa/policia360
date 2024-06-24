import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICalendario } from '../calendario.model';
import { CalendarioService } from '../service/calendario.service';

@Component({
  standalone: true,
  templateUrl: './calendario-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CalendarioDeleteDialogComponent {
  calendario?: ICalendario;

  protected calendarioService = inject(CalendarioService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.calendarioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

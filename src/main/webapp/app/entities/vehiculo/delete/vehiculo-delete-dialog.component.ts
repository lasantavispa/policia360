import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IVehiculo } from '../vehiculo.model';
import { VehiculoService } from '../service/vehiculo.service';

@Component({
  standalone: true,
  templateUrl: './vehiculo-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class VehiculoDeleteDialogComponent {
  vehiculo?: IVehiculo;

  protected vehiculoService = inject(VehiculoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.vehiculoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

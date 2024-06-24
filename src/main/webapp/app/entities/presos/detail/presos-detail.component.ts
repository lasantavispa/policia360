import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IPresos } from '../presos.model';

@Component({
  standalone: true,
  selector: 'jhi-presos-detail',
  templateUrl: './presos-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PresosDetailComponent {
  presos = input<IPresos | null>(null);

  previousState(): void {
    window.history.back();
  }
}

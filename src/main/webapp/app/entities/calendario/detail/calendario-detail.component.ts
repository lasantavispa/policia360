import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ICalendario } from '../calendario.model';

@Component({
  standalone: true,
  selector: 'jhi-calendario-detail',
  templateUrl: './calendario-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class CalendarioDetailComponent {
  calendario = input<ICalendario | null>(null);

  previousState(): void {
    window.history.back();
  }
}

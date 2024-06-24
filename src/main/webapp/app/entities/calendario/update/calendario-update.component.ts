import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';
import { TrabajadoresService } from 'app/entities/trabajadores/service/trabajadores.service';
import { ICalendario } from '../calendario.model';
import { CalendarioService } from '../service/calendario.service';
import { CalendarioFormService, CalendarioFormGroup } from './calendario-form.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  standalone: true,
  selector: 'jhi-calendario-update',
  templateUrl: './calendario-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CalendarioUpdateComponent implements OnInit {
  isSaving = false;
  calendario: ICalendario | null = null;

  trabajadoresSharedCollection: ITrabajadores[] = [];

  protected calendarioService = inject(CalendarioService);
  protected calendarioFormService = inject(CalendarioFormService);
  protected trabajadoresService = inject(TrabajadoresService);
  protected activatedRoute = inject(ActivatedRoute);
  private activemodal = inject(NgbActiveModal);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CalendarioFormGroup = this.calendarioFormService.createCalendarioFormGroup();

  compareTrabajadores = (o1: ITrabajadores | null, o2: ITrabajadores | null): boolean =>
    this.trabajadoresService.compareTrabajadores(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ calendario }) => {
      this.calendario = calendario;
      if (calendario) {
        this.updateForm(calendario);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    // window.history.back();
    this.closeSurvey();
  }
  closeSurvey(result?: string): void {
    this.activemodal.close(result);
  }

  save(): void {
    this.isSaving = true;
    const calendario = this.calendarioFormService.getCalendario(this.editForm);
    if (calendario.id !== null) {
      this.subscribeToSaveResponse(this.calendarioService.update(calendario));
    } else {
      this.subscribeToSaveResponse(this.calendarioService.create(calendario));
    }
    // this.closeSurvey('save');
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICalendario>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(calendario: ICalendario): void {
    this.calendario = calendario;
    this.calendarioFormService.resetForm(this.editForm, calendario);

    this.trabajadoresSharedCollection = this.trabajadoresService.addTrabajadoresToCollectionIfMissing<ITrabajadores>(
      this.trabajadoresSharedCollection,
      calendario.trabajadores,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trabajadoresService
      .query()
      .pipe(map((res: HttpResponse<ITrabajadores[]>) => res.body ?? []))
      .pipe(
        map((trabajadores: ITrabajadores[]) =>
          this.trabajadoresService.addTrabajadoresToCollectionIfMissing<ITrabajadores>(trabajadores, this.calendario?.trabajadores),
        ),
      )
      .subscribe((trabajadores: ITrabajadores[]) => (this.trabajadoresSharedCollection = trabajadores));
  }
}

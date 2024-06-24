import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Resolucion } from 'app/entities/enumerations/resolucion.model';
import { IExpediente } from '../expediente.model';
import { ExpedienteService } from '../service/expediente.service';
import { ExpedienteFormService, ExpedienteFormGroup } from './expediente-form.service';

@Component({
  standalone: true,
  selector: 'jhi-expediente-update',
  templateUrl: './expediente-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ExpedienteUpdateComponent implements OnInit {
  isSaving = false;
  expediente: IExpediente | null = null;
  resolucionValues = Object.keys(Resolucion);

  protected expedienteService = inject(ExpedienteService);
  protected expedienteFormService = inject(ExpedienteFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ExpedienteFormGroup = this.expedienteFormService.createExpedienteFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expediente }) => {
      this.expediente = expediente;
      if (expediente) {
        this.updateForm(expediente);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const expediente = this.expedienteFormService.getExpediente(this.editForm);
    if (expediente.id !== null) {
      this.subscribeToSaveResponse(this.expedienteService.update(expediente));
    } else {
      this.subscribeToSaveResponse(this.expedienteService.create(expediente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExpediente>>): void {
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

  protected updateForm(expediente: IExpediente): void {
    this.expediente = expediente;
    this.expedienteFormService.resetForm(this.editForm, expediente);
  }
}

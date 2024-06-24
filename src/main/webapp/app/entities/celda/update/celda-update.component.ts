import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { NumeroCamas } from 'app/entities/enumerations/numero-camas.model';
import { ICelda } from '../celda.model';
import { CeldaService } from '../service/celda.service';
import { CeldaFormService, CeldaFormGroup } from './celda-form.service';

@Component({
  standalone: true,
  selector: 'jhi-celda-update',
  templateUrl: './celda-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CeldaUpdateComponent implements OnInit {
  isSaving = false;
  celda: ICelda | null = null;
  numeroCamasValues = Object.keys(NumeroCamas);

  protected celdaService = inject(CeldaService);
  protected celdaFormService = inject(CeldaFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CeldaFormGroup = this.celdaFormService.createCeldaFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ celda }) => {
      this.celda = celda;
      if (celda) {
        this.updateForm(celda);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const celda = this.celdaFormService.getCelda(this.editForm);
    if (celda.id !== null) {
      this.subscribeToSaveResponse(this.celdaService.update(celda));
    } else {
      this.subscribeToSaveResponse(this.celdaService.create(celda));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICelda>>): void {
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

  protected updateForm(celda: ICelda): void {
    this.celda = celda;
    this.celdaFormService.resetForm(this.editForm, celda);
  }
}

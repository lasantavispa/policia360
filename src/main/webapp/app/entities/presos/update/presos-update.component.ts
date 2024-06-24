import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { IExpediente } from 'app/entities/expediente/expediente.model';
import { ExpedienteService } from 'app/entities/expediente/service/expediente.service';
import { ICelda } from 'app/entities/celda/celda.model';
import { CeldaService } from 'app/entities/celda/service/celda.service';
import { PresosService } from '../service/presos.service';
import { IPresos } from '../presos.model';
import { PresosFormService, PresosFormGroup } from './presos-form.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  standalone: true,
  selector: 'jhi-presos-update',
  styleUrl: './presos-update.component.scss',
  templateUrl: './presos-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PresosUpdateComponent implements OnInit {
  isSaving = false;
  presos: IPresos | null = null;

  expedientesCollection: IExpediente[] = [];
  celdasSharedCollection: ICelda[] = [];

  protected presosService = inject(PresosService);
  protected presosFormService = inject(PresosFormService);
  protected expedienteService = inject(ExpedienteService);
  protected celdaService = inject(CeldaService);
  protected activatedRoute = inject(ActivatedRoute);
  private activemodal = inject(NgbActiveModal);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PresosFormGroup = this.presosFormService.createPresosFormGroup();

  compareExpediente = (o1: IExpediente | null, o2: IExpediente | null): boolean => this.expedienteService.compareExpediente(o1, o2);

  compareCelda = (o1: ICelda | null, o2: ICelda | null): boolean => this.celdaService.compareCelda(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ presos }) => {
      this.presos = presos;
      if (presos) {
        this.updateForm(presos);
      }

      this.loadRelationshipsOptions();
    });
    this.editForm.get('tipoDocumento')?.valueChanges.subscribe(() => this.onTipoDocumentoChange());
  }

  previousState(): void {
    // window.history.back();s
    this.closeSurvey();
  }

  save(): void {
    this.isSaving = true;
    const presos = this.presosFormService.getPresos(this.editForm);
    if (presos.id !== null) {
      this.subscribeToSaveResponse(this.presosService.update(presos));
    } else {
      this.subscribeToSaveResponse(this.presosService.create(presos));
    }
    this.closeSurvey('save');
  }
  closeSurvey(result?: string): void {
    this.activemodal.close(result);
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPresos>>): void {
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

  protected updateForm(presos: IPresos): void {
    this.presos = presos;
    this.presosFormService.resetForm(this.editForm, presos);

    this.expedientesCollection = this.expedienteService.addExpedienteToCollectionIfMissing<IExpediente>(
      this.expedientesCollection,
      presos.expediente,
    );
    this.celdasSharedCollection = this.celdaService.addCeldaToCollectionIfMissing<ICelda>(this.celdasSharedCollection, presos.celda);
  }

  protected loadRelationshipsOptions(): void {
    this.expedienteService
      .query({ filter: 'presos-is-null' })
      .pipe(map((res: HttpResponse<IExpediente[]>) => res.body ?? []))
      .pipe(
        map((expedientes: IExpediente[]) =>
          this.expedienteService.addExpedienteToCollectionIfMissing<IExpediente>(expedientes, this.presos?.expediente),
        ),
      )
      .subscribe((expedientes: IExpediente[]) => (this.expedientesCollection = expedientes));

    this.celdaService
      .query()
      .pipe(map((res: HttpResponse<ICelda[]>) => res.body ?? []))
      .pipe(map((celdas: ICelda[]) => this.celdaService.addCeldaToCollectionIfMissing<ICelda>(celdas, this.presos?.celda)))
      .subscribe((celdas: ICelda[]) => (this.celdasSharedCollection = celdas));
  }
  onTipoDocumentoChange(): void {
    const tipoDocumento = this.editForm.get('tipoDocumento')?.value;
    const dniControl = this.editForm.get('dni');

    if (tipoDocumento === 'DNI') {
      dniControl?.setValidators([Validators.required, Validators.pattern(/^\d{8}[A-Z]$/)]);
    } else if (tipoDocumento === 'NIE') {
      dniControl?.setValidators([Validators.required, Validators.pattern(/^[XYZ]\d{7}[A-Z]$/)]);
    } else if (tipoDocumento === 'TIE') {
      dniControl?.setValidators([Validators.required, Validators.pattern(/^\d{8}[A-Z]$/)]);
    }

    dniControl?.updateValueAndValidity();
  }
}

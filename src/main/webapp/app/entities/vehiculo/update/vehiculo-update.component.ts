import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AbstractControl, FormBuilder, FormControl, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';

import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';
import { TrabajadoresService } from 'app/entities/trabajadores/service/trabajadores.service';
import { TipoVehiculo } from 'app/entities/enumerations/tipo-vehiculo.model';
import { VehiculoService } from '../service/vehiculo.service';
import { IVehiculo } from '../vehiculo.model';
import { VehiculoFormService, VehiculoFormGroup } from './vehiculo-form.service';

@Component({
  standalone: true,
  selector: 'jhi-vehiculo-update',
  templateUrl: './vehiculo-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VehiculoUpdateComponent implements OnInit {
  isSaving = false;
  vehiculo: IVehiculo | null = null;
  tipoVehiculoValues = Object.keys(TipoVehiculo);

  trabajadoresSharedCollection: ITrabajadores[] = [];

  protected vehiculoService = inject(VehiculoService);
  protected vehiculoFormService = inject(VehiculoFormService);
  protected trabajadoresService = inject(TrabajadoresService);
  protected activatedRoute = inject(ActivatedRoute);
  protected fb = inject(FormBuilder);

  // eslint-disable-next-line @typescript-eslint/member-ordering

  // editForm: VehiculoFormGroup = this.vehiculoFormService.createVehiculoFormGroup();

  editForm: VehiculoFormGroup = this.fb.group({
    id: new FormControl<number | null>(null),
    identificador: new FormControl<string | null>(null, [Validators.required, this.matriculaValidator]),
    tipovehiculo: new FormControl<string | null>(null, [Validators.required]),
    disponibilidad: new FormControl<boolean>(false),
    trabajadores: new FormControl<ITrabajadores[]>([]),
  }) as VehiculoFormGroup;

  compareTrabajadores = (o1: ITrabajadores | null, o2: ITrabajadores | null): boolean =>
    this.trabajadoresService.compareTrabajadores(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vehiculo }) => {
      this.vehiculo = vehiculo;
      if (vehiculo) {
        this.updateForm(vehiculo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const vehiculo = this.vehiculoFormService.getVehiculo(this.editForm);
    if (vehiculo.id !== null) {
      this.subscribeToSaveResponse(this.vehiculoService.update(vehiculo));
    } else {
      this.subscribeToSaveResponse(this.vehiculoService.create(vehiculo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVehiculo>>): void {
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

  protected updateForm(vehiculo: IVehiculo): void {
    this.vehiculo = vehiculo;
    this.vehiculoFormService.resetForm(this.editForm, vehiculo);

    this.trabajadoresSharedCollection = this.trabajadoresService.addTrabajadoresToCollectionIfMissing<ITrabajadores>(
      this.trabajadoresSharedCollection,
      ...(vehiculo.trabajadores ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.trabajadoresService
      .query()
      .pipe(map((res: HttpResponse<ITrabajadores[]>) => res.body ?? []))
      .pipe(
        map((trabajadores: ITrabajadores[]) =>
          this.trabajadoresService.addTrabajadoresToCollectionIfMissing<ITrabajadores>(
            trabajadores,
            ...(this.vehiculo?.trabajadores ?? []),
          ),
        ),
      )
      .subscribe((trabajadores: ITrabajadores[]) => (this.trabajadoresSharedCollection = trabajadores));
  }

  matriculaValidator(control: AbstractControl): ValidationErrors | null {
    const matricula = control.value;
    const matriculaPattern = /^\d{4}[A-Z]{3}$/; // Ejemplo de patrón: 3 letras seguidas de 3 números
    if (!matriculaPattern.test(matricula)) {
      return { invalidMatricula: true };
    }
    return null;
  }
}

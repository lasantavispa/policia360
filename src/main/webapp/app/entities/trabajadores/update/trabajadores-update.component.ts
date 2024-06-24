import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { VehiculoService } from 'app/entities/vehiculo/service/vehiculo.service';
import { Puesto } from 'app/entities/enumerations/puesto.model';
import { Turno } from 'app/entities/enumerations/turno.model';
import { Estado } from 'app/entities/enumerations/estado.model';
import { TrabajadoresService } from '../service/trabajadores.service';
import { ITrabajadores } from '../trabajadores.model';
import { TrabajadoresFormService, TrabajadoresFormGroup } from './trabajadores-form.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';

@Component({
  standalone: true,
  selector: 'jhi-trabajadores-update',
  templateUrl: './trabajadores-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TrabajadoresUpdateComponent implements OnInit {
  isSaving = false;
  trabajadores: ITrabajadores | null = null;
  puestoValues = Object.keys(Puesto);
  turnoValues = Object.keys(Turno);
  estadoValues = Object.keys(Estado);

  vehiculosSharedCollection: IVehiculo[] = [];
  usersSharedCollection: IUser[] = [];

  protected trabajadoresService = inject(TrabajadoresService);
  protected trabajadoresFormService = inject(TrabajadoresFormService);
  protected vehiculoService = inject(VehiculoService);
  protected userService = inject(UserService);

  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TrabajadoresFormGroup = this.trabajadoresFormService.createTrabajadoresFormGroup();

  compareVehiculo = (o1: IVehiculo | null, o2: IVehiculo | null): boolean => this.vehiculoService.compareVehiculo(o1, o2);
  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trabajadores }) => {
      this.trabajadores = trabajadores;
      if (trabajadores) {
        this.updateForm(trabajadores);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    if (this.editForm.valid) {
      this.isSaving = true;
      const trabajadores = this.trabajadoresFormService.getTrabajadores(this.editForm);
      if (trabajadores.id !== null) {
        this.subscribeToSaveResponse(this.trabajadoresService.update(trabajadores));
      } else {
        this.subscribeToSaveResponse(this.trabajadoresService.create(trabajadores));
      }
    } else {
      console.error('El formulario no es válido');
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITrabajadores>>): void {
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

  protected updateForm(trabajadores: ITrabajadores): void {
    this.trabajadores = trabajadores;
    this.trabajadoresFormService.resetForm(this.editForm, trabajadores);

    this.vehiculosSharedCollection = this.vehiculoService.addVehiculoToCollectionIfMissing<IVehiculo>(
      this.vehiculosSharedCollection,
      ...(trabajadores.vehiculos ?? []),
    );

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, trabajadores.user);
  }

  protected loadRelationshipsOptions(): void {
    this.vehiculoService
      .query()
      .pipe(map((res: HttpResponse<IVehiculo[]>) => res.body ?? []))
      .pipe(
        map((vehiculos: IVehiculo[]) =>
          this.vehiculoService.addVehiculoToCollectionIfMissing<IVehiculo>(vehiculos, ...(this.trabajadores?.vehiculos ?? [])),
        ),
      )
      .subscribe((vehiculos: IVehiculo[]) => (this.vehiculosSharedCollection = vehiculos));
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.trabajadores?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}

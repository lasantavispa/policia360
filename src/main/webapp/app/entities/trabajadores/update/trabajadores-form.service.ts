import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITrabajadores, NewTrabajadores } from '../trabajadores.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITrabajadores for edit and NewTrabajadoresFormGroupInput for create.
 */
type TrabajadoresFormGroupInput = ITrabajadores | PartialWithRequiredKeyOf<NewTrabajadores>;

type TrabajadoresFormDefaults = Pick<NewTrabajadores, 'id' | 'vehiculos'>;

type TrabajadoresFormGroupContent = {
  id: FormControl<ITrabajadores['id'] | NewTrabajadores['id']>;
  dni: FormControl<ITrabajadores['dni']>;
  puesto: FormControl<ITrabajadores['puesto']>;
  turno: FormControl<ITrabajadores['turno']>;
  antiguedad: FormControl<ITrabajadores['antiguedad']>;
  estado: FormControl<ITrabajadores['estado']>;
  vehiculos: FormControl<ITrabajadores['vehiculos']>;
};

export type TrabajadoresFormGroup = FormGroup<TrabajadoresFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TrabajadoresFormService {
  createTrabajadoresFormGroup(trabajadores: TrabajadoresFormGroupInput = { id: null }): TrabajadoresFormGroup {
    const trabajadoresRawValue = {
      ...this.getFormDefaults(),
      ...trabajadores,
    };
    return new FormGroup<TrabajadoresFormGroupContent>({
      id: new FormControl(
        { value: trabajadoresRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dni: new FormControl(trabajadoresRawValue.dni, [Validators.required, Validators.pattern(/^[0-9]{8}[A-Z]$/)]),
      puesto: new FormControl(trabajadoresRawValue.puesto),
      turno: new FormControl(trabajadoresRawValue.turno),
      antiguedad: new FormControl(trabajadoresRawValue.antiguedad),
      estado: new FormControl(trabajadoresRawValue.estado),
      vehiculos: new FormControl(trabajadoresRawValue.vehiculos ?? []),
    });
  }

  getTrabajadores(form: TrabajadoresFormGroup): ITrabajadores | NewTrabajadores {
    return form.getRawValue() as ITrabajadores | NewTrabajadores;
  }

  resetForm(form: TrabajadoresFormGroup, trabajadores: TrabajadoresFormGroupInput): void {
    const trabajadoresRawValue = { ...this.getFormDefaults(), ...trabajadores };
    form.reset(
      {
        ...trabajadoresRawValue,
        id: { value: trabajadoresRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TrabajadoresFormDefaults {
    return {
      id: null,
      vehiculos: [],
    };
  }
}

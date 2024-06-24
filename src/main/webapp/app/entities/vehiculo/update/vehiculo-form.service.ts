import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IVehiculo, NewVehiculo } from '../vehiculo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVehiculo for edit and NewVehiculoFormGroupInput for create.
 */
type VehiculoFormGroupInput = IVehiculo | PartialWithRequiredKeyOf<NewVehiculo>;

type VehiculoFormDefaults = Pick<NewVehiculo, 'id' | 'disponibilidad' | 'trabajadores'>;

type VehiculoFormGroupContent = {
  id: FormControl<IVehiculo['id'] | NewVehiculo['id']>;
  identificador: FormControl<IVehiculo['identificador']>;
  tipovehiculo: FormControl<IVehiculo['tipovehiculo']>;
  disponibilidad: FormControl<IVehiculo['disponibilidad']>;
  trabajadores: FormControl<IVehiculo['trabajadores']>;
};

export type VehiculoFormGroup = FormGroup<VehiculoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VehiculoFormService {
  createVehiculoFormGroup(vehiculo: VehiculoFormGroupInput = { id: null }): VehiculoFormGroup {
    const vehiculoRawValue = {
      ...this.getFormDefaults(),
      ...vehiculo,
    };
    return new FormGroup<VehiculoFormGroupContent>({
      id: new FormControl(
        { value: vehiculoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      identificador: new FormControl(vehiculoRawValue.identificador),
      tipovehiculo: new FormControl(vehiculoRawValue.tipovehiculo),
      disponibilidad: new FormControl(vehiculoRawValue.disponibilidad),
      trabajadores: new FormControl(vehiculoRawValue.trabajadores ?? []),
    });
  }

  getVehiculo(form: VehiculoFormGroup): IVehiculo | NewVehiculo {
    return form.getRawValue() as IVehiculo | NewVehiculo;
  }

  resetForm(form: VehiculoFormGroup, vehiculo: VehiculoFormGroupInput): void {
    const vehiculoRawValue = { ...this.getFormDefaults(), ...vehiculo };
    form.reset(
      {
        ...vehiculoRawValue,
        id: { value: vehiculoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): VehiculoFormDefaults {
    return {
      id: null,
      disponibilidad: false,
      trabajadores: [],
    };
  }
}

import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPresos, NewPresos } from '../presos.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPresos for edit and NewPresosFormGroupInput for create.
 */
type PresosFormGroupInput = IPresos | PartialWithRequiredKeyOf<NewPresos>;

type PresosFormDefaults = Pick<NewPresos, 'id'>;

type PresosFormGroupContent = {
  id: FormControl<IPresos['id'] | NewPresos['id']>;
  nombre: FormControl<IPresos['nombre']>;
  apellido: FormControl<IPresos['apellido']>;
  edad: FormControl<IPresos['edad']>;
  tipoDocumento: FormControl<IPresos['tipoDocumento']>;
  dni: FormControl<IPresos['dni']>;
  enfermedades: FormControl<IPresos['enfermedades']>;
  expediente: FormControl<IPresos['expediente']>;
  celda: FormControl<IPresos['celda']>;
};

export type PresosFormGroup = FormGroup<PresosFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PresosFormService {
  createPresosFormGroup(presos: PresosFormGroupInput = { id: null }): PresosFormGroup {
    const presosRawValue = {
      ...this.getFormDefaults(),
      ...presos,
    };
    return new FormGroup<PresosFormGroupContent>({
      id: new FormControl(
        { value: presosRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(presosRawValue.nombre),
      apellido: new FormControl(presosRawValue.apellido),
      edad: new FormControl(presosRawValue.edad),
      tipoDocumento: new FormControl('DNI', [Validators.required]),
      dni: new FormControl(presosRawValue.dni),
      enfermedades: new FormControl(presosRawValue.enfermedades),
      expediente: new FormControl(presosRawValue.expediente),
      celda: new FormControl(presosRawValue.celda),
    });
  }

  getPresos(form: PresosFormGroup): IPresos | NewPresos {
    return form.getRawValue() as IPresos | NewPresos;
  }

  resetForm(form: PresosFormGroup, presos: PresosFormGroupInput): void {
    const presosRawValue = { ...this.getFormDefaults(), ...presos };
    form.reset(
      {
        ...presosRawValue,
        id: { value: presosRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PresosFormDefaults {
    return {
      id: null,
    };
  }
}

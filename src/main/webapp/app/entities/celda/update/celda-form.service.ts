import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICelda, NewCelda } from '../celda.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICelda for edit and NewCeldaFormGroupInput for create.
 */
type CeldaFormGroupInput = ICelda | PartialWithRequiredKeyOf<NewCelda>;

type CeldaFormDefaults = Pick<NewCelda, 'id' | 'disponibilidad'>;

type CeldaFormGroupContent = {
  id: FormControl<ICelda['id'] | NewCelda['id']>;
  numeroCamas: FormControl<ICelda['numeroCamas']>;
  disponibilidad: FormControl<ICelda['disponibilidad']>;
};

export type CeldaFormGroup = FormGroup<CeldaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CeldaFormService {
  createCeldaFormGroup(celda: CeldaFormGroupInput = { id: null }): CeldaFormGroup {
    const celdaRawValue = {
      ...this.getFormDefaults(),
      ...celda,
    };
    return new FormGroup<CeldaFormGroupContent>({
      id: new FormControl(
        { value: celdaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numeroCamas: new FormControl(celdaRawValue.numeroCamas),
      disponibilidad: new FormControl(celdaRawValue.disponibilidad),
    });
  }

  getCelda(form: CeldaFormGroup): ICelda | NewCelda {
    return form.getRawValue() as ICelda | NewCelda;
  }

  resetForm(form: CeldaFormGroup, celda: CeldaFormGroupInput): void {
    const celdaRawValue = { ...this.getFormDefaults(), ...celda };
    form.reset(
      {
        ...celdaRawValue,
        id: { value: celdaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CeldaFormDefaults {
    return {
      id: null,
      disponibilidad: false,
    };
  }
}

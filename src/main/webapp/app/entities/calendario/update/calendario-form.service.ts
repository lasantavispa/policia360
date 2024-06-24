import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICalendario, NewCalendario } from '../calendario.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICalendario for edit and NewCalendarioFormGroupInput for create.
 */
type CalendarioFormGroupInput = ICalendario | PartialWithRequiredKeyOf<NewCalendario>;

type CalendarioFormDefaults = Pick<NewCalendario, 'id'>;

type CalendarioFormGroupContent = {
  id: FormControl<ICalendario['id'] | NewCalendario['id']>;
  fechaInicio: FormControl<ICalendario['fechaInicio']>;
  fechaFin: FormControl<ICalendario['fechaFin']>;
  concepto: FormControl<ICalendario['concepto']>;
  trabajadores: FormControl<ICalendario['trabajadores']>;
};

export type CalendarioFormGroup = FormGroup<CalendarioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CalendarioFormService {
  createCalendarioFormGroup(calendario: CalendarioFormGroupInput = { id: null }): CalendarioFormGroup {
    const calendarioRawValue = {
      ...this.getFormDefaults(),
      ...calendario,
    };
    return new FormGroup<CalendarioFormGroupContent>({
      id: new FormControl(
        { value: calendarioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fechaInicio: new FormControl(calendarioRawValue.fechaInicio),
      fechaFin: new FormControl(calendarioRawValue.fechaFin),
      concepto: new FormControl(calendarioRawValue.concepto),
      trabajadores: new FormControl(calendarioRawValue.trabajadores),
    });
  }

  getCalendario(form: CalendarioFormGroup): ICalendario | NewCalendario {
    return form.getRawValue() as ICalendario | NewCalendario;
  }

  resetForm(form: CalendarioFormGroup, calendario: CalendarioFormGroupInput): void {
    const calendarioRawValue = { ...this.getFormDefaults(), ...calendario };
    form.reset(
      {
        ...calendarioRawValue,
        id: { value: calendarioRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CalendarioFormDefaults {
    return {
      id: null,
    };
  }
}

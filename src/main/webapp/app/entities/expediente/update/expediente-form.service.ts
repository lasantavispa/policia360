import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IExpediente, NewExpediente } from '../expediente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExpediente for edit and NewExpedienteFormGroupInput for create.
 */
type ExpedienteFormGroupInput = IExpediente | PartialWithRequiredKeyOf<NewExpediente>;

type ExpedienteFormDefaults = Pick<NewExpediente, 'id'>;

type ExpedienteFormGroupContent = {
  id: FormControl<IExpediente['id'] | NewExpediente['id']>;
  delito: FormControl<IExpediente['delito']>;
  fecha: FormControl<IExpediente['fecha']>;
  resolucion: FormControl<IExpediente['resolucion']>;
};

export type ExpedienteFormGroup = FormGroup<ExpedienteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExpedienteFormService {
  createExpedienteFormGroup(expediente: ExpedienteFormGroupInput = { id: null }): ExpedienteFormGroup {
    const expedienteRawValue = {
      ...this.getFormDefaults(),
      ...expediente,
    };
    return new FormGroup<ExpedienteFormGroupContent>({
      id: new FormControl(
        { value: expedienteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      delito: new FormControl(expedienteRawValue.delito),
      fecha: new FormControl(expedienteRawValue.fecha),
      resolucion: new FormControl(expedienteRawValue.resolucion),
    });
  }

  getExpediente(form: ExpedienteFormGroup): IExpediente | NewExpediente {
    return form.getRawValue() as IExpediente | NewExpediente;
  }

  resetForm(form: ExpedienteFormGroup, expediente: ExpedienteFormGroupInput): void {
    const expedienteRawValue = { ...this.getFormDefaults(), ...expediente };
    form.reset(
      {
        ...expedienteRawValue,
        id: { value: expedienteRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ExpedienteFormDefaults {
    return {
      id: null,
    };
  }
}

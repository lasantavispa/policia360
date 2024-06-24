import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../expediente.test-samples';

import { ExpedienteFormService } from './expediente-form.service';

describe('Expediente Form Service', () => {
  let service: ExpedienteFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExpedienteFormService);
  });

  describe('Service methods', () => {
    describe('createExpedienteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExpedienteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            delito: expect.any(Object),
            fecha: expect.any(Object),
            resolucion: expect.any(Object),
          }),
        );
      });

      it('passing IExpediente should create a new form with FormGroup', () => {
        const formGroup = service.createExpedienteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            delito: expect.any(Object),
            fecha: expect.any(Object),
            resolucion: expect.any(Object),
          }),
        );
      });
    });

    describe('getExpediente', () => {
      it('should return NewExpediente for default Expediente initial value', () => {
        const formGroup = service.createExpedienteFormGroup(sampleWithNewData);

        const expediente = service.getExpediente(formGroup) as any;

        expect(expediente).toMatchObject(sampleWithNewData);
      });

      it('should return NewExpediente for empty Expediente initial value', () => {
        const formGroup = service.createExpedienteFormGroup();

        const expediente = service.getExpediente(formGroup) as any;

        expect(expediente).toMatchObject({});
      });

      it('should return IExpediente', () => {
        const formGroup = service.createExpedienteFormGroup(sampleWithRequiredData);

        const expediente = service.getExpediente(formGroup) as any;

        expect(expediente).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExpediente should not enable id FormControl', () => {
        const formGroup = service.createExpedienteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExpediente should disable id FormControl', () => {
        const formGroup = service.createExpedienteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

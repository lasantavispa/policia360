import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../celda.test-samples';

import { CeldaFormService } from './celda-form.service';

describe('Celda Form Service', () => {
  let service: CeldaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CeldaFormService);
  });

  describe('Service methods', () => {
    describe('createCeldaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCeldaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroCamas: expect.any(Object),
            disponibilidad: expect.any(Object),
          }),
        );
      });

      it('passing ICelda should create a new form with FormGroup', () => {
        const formGroup = service.createCeldaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numeroCamas: expect.any(Object),
            disponibilidad: expect.any(Object),
          }),
        );
      });
    });

    describe('getCelda', () => {
      it('should return NewCelda for default Celda initial value', () => {
        const formGroup = service.createCeldaFormGroup(sampleWithNewData);

        const celda = service.getCelda(formGroup) as any;

        expect(celda).toMatchObject(sampleWithNewData);
      });

      it('should return NewCelda for empty Celda initial value', () => {
        const formGroup = service.createCeldaFormGroup();

        const celda = service.getCelda(formGroup) as any;

        expect(celda).toMatchObject({});
      });

      it('should return ICelda', () => {
        const formGroup = service.createCeldaFormGroup(sampleWithRequiredData);

        const celda = service.getCelda(formGroup) as any;

        expect(celda).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICelda should not enable id FormControl', () => {
        const formGroup = service.createCeldaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCelda should disable id FormControl', () => {
        const formGroup = service.createCeldaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

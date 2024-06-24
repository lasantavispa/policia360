import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../presos.test-samples';

import { PresosFormService } from './presos-form.service';

describe('Presos Form Service', () => {
  let service: PresosFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PresosFormService);
  });

  describe('Service methods', () => {
    describe('createPresosFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPresosFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            apellido: expect.any(Object),
            edad: expect.any(Object),
            dni: expect.any(Object),
            enfermedades: expect.any(Object),
            expediente: expect.any(Object),
            celda: expect.any(Object),
          }),
        );
      });

      it('passing IPresos should create a new form with FormGroup', () => {
        const formGroup = service.createPresosFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nombre: expect.any(Object),
            apellido: expect.any(Object),
            edad: expect.any(Object),
            dni: expect.any(Object),
            enfermedades: expect.any(Object),
            expediente: expect.any(Object),
            celda: expect.any(Object),
          }),
        );
      });
    });

    describe('getPresos', () => {
      it('should return NewPresos for default Presos initial value', () => {
        const formGroup = service.createPresosFormGroup(sampleWithNewData);

        const presos = service.getPresos(formGroup) as any;

        expect(presos).toMatchObject(sampleWithNewData);
      });

      it('should return NewPresos for empty Presos initial value', () => {
        const formGroup = service.createPresosFormGroup();

        const presos = service.getPresos(formGroup) as any;

        expect(presos).toMatchObject({});
      });

      it('should return IPresos', () => {
        const formGroup = service.createPresosFormGroup(sampleWithRequiredData);

        const presos = service.getPresos(formGroup) as any;

        expect(presos).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPresos should not enable id FormControl', () => {
        const formGroup = service.createPresosFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPresos should disable id FormControl', () => {
        const formGroup = service.createPresosFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

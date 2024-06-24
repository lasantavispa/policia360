import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../trabajadores.test-samples';

import { TrabajadoresFormService } from './trabajadores-form.service';

describe('Trabajadores Form Service', () => {
  let service: TrabajadoresFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrabajadoresFormService);
  });

  describe('Service methods', () => {
    describe('createTrabajadoresFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTrabajadoresFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dni: expect.any(Object),
            puesto: expect.any(Object),
            turno: expect.any(Object),
            antiguedad: expect.any(Object),
            estado: expect.any(Object),
            vehiculos: expect.any(Object),
          }),
        );
      });

      it('passing ITrabajadores should create a new form with FormGroup', () => {
        const formGroup = service.createTrabajadoresFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dni: expect.any(Object),
            puesto: expect.any(Object),
            turno: expect.any(Object),
            antiguedad: expect.any(Object),
            estado: expect.any(Object),
            vehiculos: expect.any(Object),
          }),
        );
      });
    });

    describe('getTrabajadores', () => {
      it('should return NewTrabajadores for default Trabajadores initial value', () => {
        const formGroup = service.createTrabajadoresFormGroup(sampleWithNewData);

        const trabajadores = service.getTrabajadores(formGroup) as any;

        expect(trabajadores).toMatchObject(sampleWithNewData);
      });

      it('should return NewTrabajadores for empty Trabajadores initial value', () => {
        const formGroup = service.createTrabajadoresFormGroup();

        const trabajadores = service.getTrabajadores(formGroup) as any;

        expect(trabajadores).toMatchObject({});
      });

      it('should return ITrabajadores', () => {
        const formGroup = service.createTrabajadoresFormGroup(sampleWithRequiredData);

        const trabajadores = service.getTrabajadores(formGroup) as any;

        expect(trabajadores).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITrabajadores should not enable id FormControl', () => {
        const formGroup = service.createTrabajadoresFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTrabajadores should disable id FormControl', () => {
        const formGroup = service.createTrabajadoresFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

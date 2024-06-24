import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../calendario.test-samples';

import { CalendarioFormService } from './calendario-form.service';

describe('Calendario Form Service', () => {
  let service: CalendarioFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CalendarioFormService);
  });

  describe('Service methods', () => {
    describe('createCalendarioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCalendarioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            concepto: expect.any(Object),
            trabajadores: expect.any(Object),
          }),
        );
      });

      it('passing ICalendario should create a new form with FormGroup', () => {
        const formGroup = service.createCalendarioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fechaInicio: expect.any(Object),
            fechaFin: expect.any(Object),
            concepto: expect.any(Object),
            trabajadores: expect.any(Object),
          }),
        );
      });
    });

    describe('getCalendario', () => {
      it('should return NewCalendario for default Calendario initial value', () => {
        const formGroup = service.createCalendarioFormGroup(sampleWithNewData);

        const calendario = service.getCalendario(formGroup) as any;

        expect(calendario).toMatchObject(sampleWithNewData);
      });

      it('should return NewCalendario for empty Calendario initial value', () => {
        const formGroup = service.createCalendarioFormGroup();

        const calendario = service.getCalendario(formGroup) as any;

        expect(calendario).toMatchObject({});
      });

      it('should return ICalendario', () => {
        const formGroup = service.createCalendarioFormGroup(sampleWithRequiredData);

        const calendario = service.getCalendario(formGroup) as any;

        expect(calendario).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICalendario should not enable id FormControl', () => {
        const formGroup = service.createCalendarioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCalendario should disable id FormControl', () => {
        const formGroup = service.createCalendarioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

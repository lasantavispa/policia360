import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ExpedienteService } from '../service/expediente.service';
import { IExpediente } from '../expediente.model';
import { ExpedienteFormService } from './expediente-form.service';

import { ExpedienteUpdateComponent } from './expediente-update.component';

describe('Expediente Management Update Component', () => {
  let comp: ExpedienteUpdateComponent;
  let fixture: ComponentFixture<ExpedienteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let expedienteFormService: ExpedienteFormService;
  let expedienteService: ExpedienteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ExpedienteUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExpedienteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpedienteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    expedienteFormService = TestBed.inject(ExpedienteFormService);
    expedienteService = TestBed.inject(ExpedienteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const expediente: IExpediente = { id: 456 };

      activatedRoute.data = of({ expediente });
      comp.ngOnInit();

      expect(comp.expediente).toEqual(expediente);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpediente>>();
      const expediente = { id: 123 };
      jest.spyOn(expedienteFormService, 'getExpediente').mockReturnValue(expediente);
      jest.spyOn(expedienteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expediente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expediente }));
      saveSubject.complete();

      // THEN
      expect(expedienteFormService.getExpediente).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(expedienteService.update).toHaveBeenCalledWith(expect.objectContaining(expediente));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpediente>>();
      const expediente = { id: 123 };
      jest.spyOn(expedienteFormService, 'getExpediente').mockReturnValue({ id: null });
      jest.spyOn(expedienteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expediente: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expediente }));
      saveSubject.complete();

      // THEN
      expect(expedienteFormService.getExpediente).toHaveBeenCalled();
      expect(expedienteService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpediente>>();
      const expediente = { id: 123 };
      jest.spyOn(expedienteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expediente });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(expedienteService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

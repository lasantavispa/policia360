import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IExpediente } from 'app/entities/expediente/expediente.model';
import { ExpedienteService } from 'app/entities/expediente/service/expediente.service';
import { ICelda } from 'app/entities/celda/celda.model';
import { CeldaService } from 'app/entities/celda/service/celda.service';
import { IPresos } from '../presos.model';
import { PresosService } from '../service/presos.service';
import { PresosFormService } from './presos-form.service';

import { PresosUpdateComponent } from './presos-update.component';

describe('Presos Management Update Component', () => {
  let comp: PresosUpdateComponent;
  let fixture: ComponentFixture<PresosUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let presosFormService: PresosFormService;
  let presosService: PresosService;
  let expedienteService: ExpedienteService;
  let celdaService: CeldaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PresosUpdateComponent],
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
      .overrideTemplate(PresosUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PresosUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    presosFormService = TestBed.inject(PresosFormService);
    presosService = TestBed.inject(PresosService);
    expedienteService = TestBed.inject(ExpedienteService);
    celdaService = TestBed.inject(CeldaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call expediente query and add missing value', () => {
      const presos: IPresos = { id: 456 };
      const expediente: IExpediente = { id: 25040 };
      presos.expediente = expediente;

      const expedienteCollection: IExpediente[] = [{ id: 32416 }];
      jest.spyOn(expedienteService, 'query').mockReturnValue(of(new HttpResponse({ body: expedienteCollection })));
      const expectedCollection: IExpediente[] = [expediente, ...expedienteCollection];
      jest.spyOn(expedienteService, 'addExpedienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ presos });
      comp.ngOnInit();

      expect(expedienteService.query).toHaveBeenCalled();
      expect(expedienteService.addExpedienteToCollectionIfMissing).toHaveBeenCalledWith(expedienteCollection, expediente);
      expect(comp.expedientesCollection).toEqual(expectedCollection);
    });

    it('Should call Celda query and add missing value', () => {
      const presos: IPresos = { id: 456 };
      const celda: ICelda = { id: 2907 };
      presos.celda = celda;

      const celdaCollection: ICelda[] = [{ id: 20878 }];
      jest.spyOn(celdaService, 'query').mockReturnValue(of(new HttpResponse({ body: celdaCollection })));
      const additionalCeldas = [celda];
      const expectedCollection: ICelda[] = [...additionalCeldas, ...celdaCollection];
      jest.spyOn(celdaService, 'addCeldaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ presos });
      comp.ngOnInit();

      expect(celdaService.query).toHaveBeenCalled();
      expect(celdaService.addCeldaToCollectionIfMissing).toHaveBeenCalledWith(
        celdaCollection,
        ...additionalCeldas.map(expect.objectContaining),
      );
      expect(comp.celdasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const presos: IPresos = { id: 456 };
      const expediente: IExpediente = { id: 5231 };
      presos.expediente = expediente;
      const celda: ICelda = { id: 3621 };
      presos.celda = celda;

      activatedRoute.data = of({ presos });
      comp.ngOnInit();

      expect(comp.expedientesCollection).toContain(expediente);
      expect(comp.celdasSharedCollection).toContain(celda);
      expect(comp.presos).toEqual(presos);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresos>>();
      const presos = { id: 123 };
      jest.spyOn(presosFormService, 'getPresos').mockReturnValue(presos);
      jest.spyOn(presosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: presos }));
      saveSubject.complete();

      // THEN
      expect(presosFormService.getPresos).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(presosService.update).toHaveBeenCalledWith(expect.objectContaining(presos));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresos>>();
      const presos = { id: 123 };
      jest.spyOn(presosFormService, 'getPresos').mockReturnValue({ id: null });
      jest.spyOn(presosService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presos: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: presos }));
      saveSubject.complete();

      // THEN
      expect(presosFormService.getPresos).toHaveBeenCalled();
      expect(presosService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPresos>>();
      const presos = { id: 123 };
      jest.spyOn(presosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ presos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(presosService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareExpediente', () => {
      it('Should forward to expedienteService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(expedienteService, 'compareExpediente');
        comp.compareExpediente(entity, entity2);
        expect(expedienteService.compareExpediente).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCelda', () => {
      it('Should forward to celdaService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(celdaService, 'compareCelda');
        comp.compareCelda(entity, entity2);
        expect(celdaService.compareCelda).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

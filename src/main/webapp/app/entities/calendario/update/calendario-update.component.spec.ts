import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';
import { TrabajadoresService } from 'app/entities/trabajadores/service/trabajadores.service';
import { CalendarioService } from '../service/calendario.service';
import { ICalendario } from '../calendario.model';
import { CalendarioFormService } from './calendario-form.service';

import { CalendarioUpdateComponent } from './calendario-update.component';

describe('Calendario Management Update Component', () => {
  let comp: CalendarioUpdateComponent;
  let fixture: ComponentFixture<CalendarioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let calendarioFormService: CalendarioFormService;
  let calendarioService: CalendarioService;
  let trabajadoresService: TrabajadoresService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CalendarioUpdateComponent],
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
      .overrideTemplate(CalendarioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CalendarioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    calendarioFormService = TestBed.inject(CalendarioFormService);
    calendarioService = TestBed.inject(CalendarioService);
    trabajadoresService = TestBed.inject(TrabajadoresService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Trabajadores query and add missing value', () => {
      const calendario: ICalendario = { id: 456 };
      const trabajadores: ITrabajadores = { id: 28435 };
      calendario.trabajadores = trabajadores;

      const trabajadoresCollection: ITrabajadores[] = [{ id: 949 }];
      jest.spyOn(trabajadoresService, 'query').mockReturnValue(of(new HttpResponse({ body: trabajadoresCollection })));
      const additionalTrabajadores = [trabajadores];
      const expectedCollection: ITrabajadores[] = [...additionalTrabajadores, ...trabajadoresCollection];
      jest.spyOn(trabajadoresService, 'addTrabajadoresToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ calendario });
      comp.ngOnInit();

      expect(trabajadoresService.query).toHaveBeenCalled();
      expect(trabajadoresService.addTrabajadoresToCollectionIfMissing).toHaveBeenCalledWith(
        trabajadoresCollection,
        ...additionalTrabajadores.map(expect.objectContaining),
      );
      expect(comp.trabajadoresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const calendario: ICalendario = { id: 456 };
      const trabajadores: ITrabajadores = { id: 14037 };
      calendario.trabajadores = trabajadores;

      activatedRoute.data = of({ calendario });
      comp.ngOnInit();

      expect(comp.trabajadoresSharedCollection).toContain(trabajadores);
      expect(comp.calendario).toEqual(calendario);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendario>>();
      const calendario = { id: 123 };
      jest.spyOn(calendarioFormService, 'getCalendario').mockReturnValue(calendario);
      jest.spyOn(calendarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: calendario }));
      saveSubject.complete();

      // THEN
      expect(calendarioFormService.getCalendario).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(calendarioService.update).toHaveBeenCalledWith(expect.objectContaining(calendario));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendario>>();
      const calendario = { id: 123 };
      jest.spyOn(calendarioFormService, 'getCalendario').mockReturnValue({ id: null });
      jest.spyOn(calendarioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendario: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: calendario }));
      saveSubject.complete();

      // THEN
      expect(calendarioFormService.getCalendario).toHaveBeenCalled();
      expect(calendarioService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICalendario>>();
      const calendario = { id: 123 };
      jest.spyOn(calendarioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ calendario });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(calendarioService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTrabajadores', () => {
      it('Should forward to trabajadoresService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trabajadoresService, 'compareTrabajadores');
        comp.compareTrabajadores(entity, entity2);
        expect(trabajadoresService.compareTrabajadores).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

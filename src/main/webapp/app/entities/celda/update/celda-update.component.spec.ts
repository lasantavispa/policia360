import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { CeldaService } from '../service/celda.service';
import { ICelda } from '../celda.model';
import { CeldaFormService } from './celda-form.service';

import { CeldaUpdateComponent } from './celda-update.component';

describe('Celda Management Update Component', () => {
  let comp: CeldaUpdateComponent;
  let fixture: ComponentFixture<CeldaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let celdaFormService: CeldaFormService;
  let celdaService: CeldaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CeldaUpdateComponent],
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
      .overrideTemplate(CeldaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CeldaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    celdaFormService = TestBed.inject(CeldaFormService);
    celdaService = TestBed.inject(CeldaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const celda: ICelda = { id: 456 };

      activatedRoute.data = of({ celda });
      comp.ngOnInit();

      expect(comp.celda).toEqual(celda);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICelda>>();
      const celda = { id: 123 };
      jest.spyOn(celdaFormService, 'getCelda').mockReturnValue(celda);
      jest.spyOn(celdaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ celda });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: celda }));
      saveSubject.complete();

      // THEN
      expect(celdaFormService.getCelda).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(celdaService.update).toHaveBeenCalledWith(expect.objectContaining(celda));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICelda>>();
      const celda = { id: 123 };
      jest.spyOn(celdaFormService, 'getCelda').mockReturnValue({ id: null });
      jest.spyOn(celdaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ celda: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: celda }));
      saveSubject.complete();

      // THEN
      expect(celdaFormService.getCelda).toHaveBeenCalled();
      expect(celdaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICelda>>();
      const celda = { id: 123 };
      jest.spyOn(celdaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ celda });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(celdaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

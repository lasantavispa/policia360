import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';
import { TrabajadoresService } from 'app/entities/trabajadores/service/trabajadores.service';
import { VehiculoService } from '../service/vehiculo.service';
import { IVehiculo } from '../vehiculo.model';
import { VehiculoFormService } from './vehiculo-form.service';

import { VehiculoUpdateComponent } from './vehiculo-update.component';

describe('Vehiculo Management Update Component', () => {
  let comp: VehiculoUpdateComponent;
  let fixture: ComponentFixture<VehiculoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let vehiculoFormService: VehiculoFormService;
  let vehiculoService: VehiculoService;
  let trabajadoresService: TrabajadoresService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, VehiculoUpdateComponent],
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
      .overrideTemplate(VehiculoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VehiculoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    vehiculoFormService = TestBed.inject(VehiculoFormService);
    vehiculoService = TestBed.inject(VehiculoService);
    trabajadoresService = TestBed.inject(TrabajadoresService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Trabajadores query and add missing value', () => {
      const vehiculo: IVehiculo = { id: 456 };
      const trabajadores: ITrabajadores[] = [{ id: 32316 }];
      vehiculo.trabajadores = trabajadores;

      const trabajadoresCollection: ITrabajadores[] = [{ id: 6680 }];
      jest.spyOn(trabajadoresService, 'query').mockReturnValue(of(new HttpResponse({ body: trabajadoresCollection })));
      const additionalTrabajadores = [...trabajadores];
      const expectedCollection: ITrabajadores[] = [...additionalTrabajadores, ...trabajadoresCollection];
      jest.spyOn(trabajadoresService, 'addTrabajadoresToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      expect(trabajadoresService.query).toHaveBeenCalled();
      expect(trabajadoresService.addTrabajadoresToCollectionIfMissing).toHaveBeenCalledWith(
        trabajadoresCollection,
        ...additionalTrabajadores.map(expect.objectContaining),
      );
      expect(comp.trabajadoresSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const vehiculo: IVehiculo = { id: 456 };
      const trabajadores: ITrabajadores = { id: 21912 };
      vehiculo.trabajadores = [trabajadores];

      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      expect(comp.trabajadoresSharedCollection).toContain(trabajadores);
      expect(comp.vehiculo).toEqual(vehiculo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehiculo>>();
      const vehiculo = { id: 123 };
      jest.spyOn(vehiculoFormService, 'getVehiculo').mockReturnValue(vehiculo);
      jest.spyOn(vehiculoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vehiculo }));
      saveSubject.complete();

      // THEN
      expect(vehiculoFormService.getVehiculo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(vehiculoService.update).toHaveBeenCalledWith(expect.objectContaining(vehiculo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehiculo>>();
      const vehiculo = { id: 123 };
      jest.spyOn(vehiculoFormService, 'getVehiculo').mockReturnValue({ id: null });
      jest.spyOn(vehiculoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: vehiculo }));
      saveSubject.complete();

      // THEN
      expect(vehiculoFormService.getVehiculo).toHaveBeenCalled();
      expect(vehiculoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVehiculo>>();
      const vehiculo = { id: 123 };
      jest.spyOn(vehiculoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(vehiculoService.update).toHaveBeenCalled();
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

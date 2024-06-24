import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { VehiculoService } from 'app/entities/vehiculo/service/vehiculo.service';
import { TrabajadoresService } from '../service/trabajadores.service';
import { ITrabajadores } from '../trabajadores.model';
import { TrabajadoresFormService } from './trabajadores-form.service';

import { TrabajadoresUpdateComponent } from './trabajadores-update.component';

describe('Trabajadores Management Update Component', () => {
  let comp: TrabajadoresUpdateComponent;
  let fixture: ComponentFixture<TrabajadoresUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trabajadoresFormService: TrabajadoresFormService;
  let trabajadoresService: TrabajadoresService;
  let vehiculoService: VehiculoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TrabajadoresUpdateComponent],
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
      .overrideTemplate(TrabajadoresUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrabajadoresUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trabajadoresFormService = TestBed.inject(TrabajadoresFormService);
    trabajadoresService = TestBed.inject(TrabajadoresService);
    vehiculoService = TestBed.inject(VehiculoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Vehiculo query and add missing value', () => {
      const trabajadores: ITrabajadores = { id: 456 };
      const vehiculos: IVehiculo[] = [{ id: 8084 }];
      trabajadores.vehiculos = vehiculos;

      const vehiculoCollection: IVehiculo[] = [{ id: 1458 }];
      jest.spyOn(vehiculoService, 'query').mockReturnValue(of(new HttpResponse({ body: vehiculoCollection })));
      const additionalVehiculos = [...vehiculos];
      const expectedCollection: IVehiculo[] = [...additionalVehiculos, ...vehiculoCollection];
      jest.spyOn(vehiculoService, 'addVehiculoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trabajadores });
      comp.ngOnInit();

      expect(vehiculoService.query).toHaveBeenCalled();
      expect(vehiculoService.addVehiculoToCollectionIfMissing).toHaveBeenCalledWith(
        vehiculoCollection,
        ...additionalVehiculos.map(expect.objectContaining),
      );
      expect(comp.vehiculosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trabajadores: ITrabajadores = { id: 456 };
      const vehiculo: IVehiculo = { id: 24492 };
      trabajadores.vehiculos = [vehiculo];

      activatedRoute.data = of({ trabajadores });
      comp.ngOnInit();

      expect(comp.vehiculosSharedCollection).toContain(vehiculo);
      expect(comp.trabajadores).toEqual(trabajadores);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrabajadores>>();
      const trabajadores = { id: 123 };
      jest.spyOn(trabajadoresFormService, 'getTrabajadores').mockReturnValue(trabajadores);
      jest.spyOn(trabajadoresService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trabajadores });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trabajadores }));
      saveSubject.complete();

      // THEN
      expect(trabajadoresFormService.getTrabajadores).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trabajadoresService.update).toHaveBeenCalledWith(expect.objectContaining(trabajadores));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrabajadores>>();
      const trabajadores = { id: 123 };
      jest.spyOn(trabajadoresFormService, 'getTrabajadores').mockReturnValue({ id: null });
      jest.spyOn(trabajadoresService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trabajadores: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trabajadores }));
      saveSubject.complete();

      // THEN
      expect(trabajadoresFormService.getTrabajadores).toHaveBeenCalled();
      expect(trabajadoresService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrabajadores>>();
      const trabajadores = { id: 123 };
      jest.spyOn(trabajadoresService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trabajadores });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trabajadoresService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVehiculo', () => {
      it('Should forward to vehiculoService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(vehiculoService, 'compareVehiculo');
        comp.compareVehiculo(entity, entity2);
        expect(vehiculoService.compareVehiculo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

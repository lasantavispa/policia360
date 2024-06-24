import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ITrabajadores } from '../trabajadores.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../trabajadores.test-samples';

import { TrabajadoresService, RestTrabajadores } from './trabajadores.service';

const requireRestSample: RestTrabajadores = {
  ...sampleWithRequiredData,
  antiguedad: sampleWithRequiredData.antiguedad?.format(DATE_FORMAT),
};

describe('Trabajadores Service', () => {
  let service: TrabajadoresService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrabajadores | ITrabajadores[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrabajadoresService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Trabajadores', () => {
      const trabajadores = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trabajadores).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Trabajadores', () => {
      const trabajadores = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trabajadores).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Trabajadores', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Trabajadores', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Trabajadores', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrabajadoresToCollectionIfMissing', () => {
      it('should add a Trabajadores to an empty array', () => {
        const trabajadores: ITrabajadores = sampleWithRequiredData;
        expectedResult = service.addTrabajadoresToCollectionIfMissing([], trabajadores);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trabajadores);
      });

      it('should not add a Trabajadores to an array that contains it', () => {
        const trabajadores: ITrabajadores = sampleWithRequiredData;
        const trabajadoresCollection: ITrabajadores[] = [
          {
            ...trabajadores,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrabajadoresToCollectionIfMissing(trabajadoresCollection, trabajadores);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Trabajadores to an array that doesn't contain it", () => {
        const trabajadores: ITrabajadores = sampleWithRequiredData;
        const trabajadoresCollection: ITrabajadores[] = [sampleWithPartialData];
        expectedResult = service.addTrabajadoresToCollectionIfMissing(trabajadoresCollection, trabajadores);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trabajadores);
      });

      it('should add only unique Trabajadores to an array', () => {
        const trabajadoresArray: ITrabajadores[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const trabajadoresCollection: ITrabajadores[] = [sampleWithRequiredData];
        expectedResult = service.addTrabajadoresToCollectionIfMissing(trabajadoresCollection, ...trabajadoresArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trabajadores: ITrabajadores = sampleWithRequiredData;
        const trabajadores2: ITrabajadores = sampleWithPartialData;
        expectedResult = service.addTrabajadoresToCollectionIfMissing([], trabajadores, trabajadores2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trabajadores);
        expect(expectedResult).toContain(trabajadores2);
      });

      it('should accept null and undefined values', () => {
        const trabajadores: ITrabajadores = sampleWithRequiredData;
        expectedResult = service.addTrabajadoresToCollectionIfMissing([], null, trabajadores, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trabajadores);
      });

      it('should return initial array if no Trabajadores is added', () => {
        const trabajadoresCollection: ITrabajadores[] = [sampleWithRequiredData];
        expectedResult = service.addTrabajadoresToCollectionIfMissing(trabajadoresCollection, undefined, null);
        expect(expectedResult).toEqual(trabajadoresCollection);
      });
    });

    describe('compareTrabajadores', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrabajadores(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrabajadores(entity1, entity2);
        const compareResult2 = service.compareTrabajadores(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrabajadores(entity1, entity2);
        const compareResult2 = service.compareTrabajadores(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrabajadores(entity1, entity2);
        const compareResult2 = service.compareTrabajadores(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

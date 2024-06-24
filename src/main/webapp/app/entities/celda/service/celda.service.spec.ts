import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICelda } from '../celda.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../celda.test-samples';

import { CeldaService } from './celda.service';

const requireRestSample: ICelda = {
  ...sampleWithRequiredData,
};

describe('Celda Service', () => {
  let service: CeldaService;
  let httpMock: HttpTestingController;
  let expectedResult: ICelda | ICelda[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CeldaService);
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

    it('should create a Celda', () => {
      const celda = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(celda).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Celda', () => {
      const celda = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(celda).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Celda', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Celda', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Celda', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCeldaToCollectionIfMissing', () => {
      it('should add a Celda to an empty array', () => {
        const celda: ICelda = sampleWithRequiredData;
        expectedResult = service.addCeldaToCollectionIfMissing([], celda);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(celda);
      });

      it('should not add a Celda to an array that contains it', () => {
        const celda: ICelda = sampleWithRequiredData;
        const celdaCollection: ICelda[] = [
          {
            ...celda,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCeldaToCollectionIfMissing(celdaCollection, celda);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Celda to an array that doesn't contain it", () => {
        const celda: ICelda = sampleWithRequiredData;
        const celdaCollection: ICelda[] = [sampleWithPartialData];
        expectedResult = service.addCeldaToCollectionIfMissing(celdaCollection, celda);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(celda);
      });

      it('should add only unique Celda to an array', () => {
        const celdaArray: ICelda[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const celdaCollection: ICelda[] = [sampleWithRequiredData];
        expectedResult = service.addCeldaToCollectionIfMissing(celdaCollection, ...celdaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const celda: ICelda = sampleWithRequiredData;
        const celda2: ICelda = sampleWithPartialData;
        expectedResult = service.addCeldaToCollectionIfMissing([], celda, celda2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(celda);
        expect(expectedResult).toContain(celda2);
      });

      it('should accept null and undefined values', () => {
        const celda: ICelda = sampleWithRequiredData;
        expectedResult = service.addCeldaToCollectionIfMissing([], null, celda, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(celda);
      });

      it('should return initial array if no Celda is added', () => {
        const celdaCollection: ICelda[] = [sampleWithRequiredData];
        expectedResult = service.addCeldaToCollectionIfMissing(celdaCollection, undefined, null);
        expect(expectedResult).toEqual(celdaCollection);
      });
    });

    describe('compareCelda', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCelda(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCelda(entity1, entity2);
        const compareResult2 = service.compareCelda(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCelda(entity1, entity2);
        const compareResult2 = service.compareCelda(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCelda(entity1, entity2);
        const compareResult2 = service.compareCelda(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

import { ICelda, NewCelda } from './celda.model';

export const sampleWithRequiredData: ICelda = {
  id: 5222,
};

export const sampleWithPartialData: ICelda = {
  id: 30703,
};

export const sampleWithFullData: ICelda = {
  id: 17028,
  numeroCamas: 'DOS',
  disponibilidad: true,
};

export const sampleWithNewData: NewCelda = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

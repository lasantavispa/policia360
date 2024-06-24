import dayjs from 'dayjs/esm';

import { IPresos, NewPresos } from './presos.model';

export const sampleWithRequiredData: IPresos = {
  id: 16937,
};

export const sampleWithPartialData: IPresos = {
  id: 22401,
  nombre: 'hackwork stucco',
  apellido: 'vegetation constitute spearhead',
  edad: dayjs('2024-05-09'),
  dni: 'fooey wonder announce',
};

export const sampleWithFullData: IPresos = {
  id: 15333,
  nombre: 'architecture',
  apellido: 'innocently observation',
  edad: dayjs('2024-05-09'),
  dni: 'glossy healthily',
  enfermedades: 'across perfectly late',
};

export const sampleWithNewData: NewPresos = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

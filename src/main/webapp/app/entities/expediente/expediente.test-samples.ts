import dayjs from 'dayjs/esm';

import { IExpediente, NewExpediente } from './expediente.model';

export const sampleWithRequiredData: IExpediente = {
  id: 4893,
};

export const sampleWithPartialData: IExpediente = {
  id: 9569,
  fecha: dayjs('2024-05-08'),
};

export const sampleWithFullData: IExpediente = {
  id: 27782,
  delito: 'pastor',
  fecha: dayjs('2024-05-09'),
  resolucion: 'Inocente',
};

export const sampleWithNewData: NewExpediente = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

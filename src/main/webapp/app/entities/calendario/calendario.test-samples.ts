import dayjs from 'dayjs/esm';

import { ICalendario, NewCalendario } from './calendario.model';

export const sampleWithRequiredData: ICalendario = {
  id: 11096,
};

export const sampleWithPartialData: ICalendario = {
  id: 15194,
  fechaInicio: dayjs('2024-05-09'),
  fechaFin: dayjs('2024-05-09'),
};

export const sampleWithFullData: ICalendario = {
  id: 32581,
  fechaInicio: dayjs('2024-05-08'),
  fechaFin: dayjs('2024-05-09'),
  concepto: 'for',
};

export const sampleWithNewData: NewCalendario = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

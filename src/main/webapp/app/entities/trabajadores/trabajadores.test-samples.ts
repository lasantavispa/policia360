import dayjs from 'dayjs/esm';

import { ITrabajadores, NewTrabajadores } from './trabajadores.model';

export const sampleWithRequiredData: ITrabajadores = {
  id: 19655,
};

export const sampleWithPartialData: ITrabajadores = {
  id: 22965,
  turno: 'NOCHE',
  estado: 'NOACTIVO',
};

export const sampleWithFullData: ITrabajadores = {
  id: 17416,
  dni: 'among practice',
  puesto: 'COMISARIO',
  turno: 'MANANA',
  antiguedad: dayjs('2024-05-09'),
  estado: 'NOACTIVO',
};

export const sampleWithNewData: NewTrabajadores = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

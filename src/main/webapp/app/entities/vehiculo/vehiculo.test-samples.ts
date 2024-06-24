import { IVehiculo, NewVehiculo } from './vehiculo.model';

export const sampleWithRequiredData: IVehiculo = {
  id: 21294,
};

export const sampleWithPartialData: IVehiculo = {
  id: 4165,
  identificador: 'after queasy',
  tipovehiculo: 'COCHEPATRULLA',
};

export const sampleWithFullData: IVehiculo = {
  id: 1506,
  identificador: 'what describe',
  tipovehiculo: 'FURGONCELULAR',
  disponibilidad: true,
};

export const sampleWithNewData: NewVehiculo = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

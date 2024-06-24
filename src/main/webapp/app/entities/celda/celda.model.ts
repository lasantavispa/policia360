import { NumeroCamas } from 'app/entities/enumerations/numero-camas.model';

export interface ICelda {
  id: number;
  numeroCamas?: keyof typeof NumeroCamas | null;
  disponibilidad?: boolean | null;
  presos?: number[] | null;
}

export type NewCelda = Omit<ICelda, 'id'> & { id: null };

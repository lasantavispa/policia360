import dayjs from 'dayjs/esm';
import { Resolucion } from 'app/entities/enumerations/resolucion.model';

export interface IExpediente {
  id: number;
  delito?: string | null;
  fecha?: dayjs.Dayjs | null;
  resolucion?: keyof typeof Resolucion | null;
  presos?: number[] | null;
}

export type NewExpediente = Omit<IExpediente, 'id'> & { id: null };

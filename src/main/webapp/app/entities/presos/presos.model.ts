import dayjs from 'dayjs/esm';
import { IExpediente } from 'app/entities/expediente/expediente.model';
import { ICelda } from 'app/entities/celda/celda.model';

export interface IPresos {
  id: number;
  nombre?: string | null;
  apellido?: string | null;
  edad?: dayjs.Dayjs | null;
  age?: number | null;
  tipoDocumento?: string | null;
  dni?: string | null;
  enfermedades?: string | null;
  expediente?: IExpediente | null;
  celda?: ICelda | null;
}

export type NewPresos = Omit<IPresos, 'id'> & { id: null };

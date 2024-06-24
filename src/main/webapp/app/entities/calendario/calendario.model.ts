import dayjs from 'dayjs/esm';
import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';

export interface ICalendario {
  id: number;
  fechaInicio?: dayjs.Dayjs | null;
  fechaFin?: dayjs.Dayjs | null;
  concepto?: string | null;
  trabajadores?: ITrabajadores | null;
}

export type NewCalendario = Omit<ICalendario, 'id'> & { id: null };

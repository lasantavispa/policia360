import dayjs from 'dayjs/esm';
import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { Puesto } from 'app/entities/enumerations/puesto.model';
import { Turno } from 'app/entities/enumerations/turno.model';
import { Estado } from 'app/entities/enumerations/estado.model';
import { IUser } from '../user/user.model';

export interface ITrabajadores {
  id: number;
  dni?: string | null;
  puesto?: keyof typeof Puesto | null;
  turno?: keyof typeof Turno | null;
  antiguedad?: dayjs.Dayjs | null;
  estado?: keyof typeof Estado | null;
  vehiculos?: IVehiculo[] | null;
  user?: IUser | null;
}

export type NewTrabajadores = Omit<ITrabajadores, 'id'> & { id: null };

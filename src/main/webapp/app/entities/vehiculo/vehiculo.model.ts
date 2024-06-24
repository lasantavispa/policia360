import { ITrabajadores } from 'app/entities/trabajadores/trabajadores.model';
import { TipoVehiculo } from 'app/entities/enumerations/tipo-vehiculo.model';

export interface IVehiculo {
  id: number;
  identificador?: string | null;
  tipovehiculo?: keyof typeof TipoVehiculo | null;
  disponibilidad?: boolean | null;
  trabajadores?: ITrabajadores[] | null;
}

export type NewVehiculo = Omit<IVehiculo, 'id'> & { id: null };

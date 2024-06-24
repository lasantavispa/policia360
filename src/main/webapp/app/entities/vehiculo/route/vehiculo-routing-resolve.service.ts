import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IVehiculo } from '../vehiculo.model';
import { VehiculoService } from '../service/vehiculo.service';

const vehiculoResolve = (route: ActivatedRouteSnapshot): Observable<null | IVehiculo> => {
  const id = route.params['id'];
  if (id) {
    return inject(VehiculoService)
      .find(id)
      .pipe(
        mergeMap((vehiculo: HttpResponse<IVehiculo>) => {
          if (vehiculo.body) {
            return of(vehiculo.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default vehiculoResolve;

import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITrabajadores } from '../trabajadores.model';
import { TrabajadoresService } from '../service/trabajadores.service';

const trabajadoresResolve = (route: ActivatedRouteSnapshot): Observable<null | ITrabajadores> => {
  const id = route.params['id'];
  if (id) {
    return inject(TrabajadoresService)
      .find(id)
      .pipe(
        mergeMap((trabajadores: HttpResponse<ITrabajadores>) => {
          if (trabajadores.body) {
            return of(trabajadores.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default trabajadoresResolve;

import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICalendario } from '../calendario.model';
import { CalendarioService } from '../service/calendario.service';

const calendarioResolve = (route: ActivatedRouteSnapshot): Observable<null | ICalendario> => {
  const id = route.params['id'];
  if (id) {
    return inject(CalendarioService)
      .find(id)
      .pipe(
        mergeMap((calendario: HttpResponse<ICalendario>) => {
          if (calendario.body) {
            return of(calendario.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default calendarioResolve;

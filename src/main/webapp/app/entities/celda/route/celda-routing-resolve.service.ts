import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICelda } from '../celda.model';
import { CeldaService } from '../service/celda.service';

const celdaResolve = (route: ActivatedRouteSnapshot): Observable<null | ICelda> => {
  const id = route.params['id'];
  if (id) {
    return inject(CeldaService)
      .find(id)
      .pipe(
        mergeMap((celda: HttpResponse<ICelda>) => {
          if (celda.body) {
            return of(celda.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default celdaResolve;

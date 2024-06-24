import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPresos } from '../presos.model';
import { PresosService } from '../service/presos.service';

const presosResolve = (route: ActivatedRouteSnapshot): Observable<null | IPresos> => {
  const id = route.params['id'];
  if (id) {
    return inject(PresosService)
      .find(id)
      .pipe(
        mergeMap((presos: HttpResponse<IPresos>) => {
          if (presos.body) {
            return of(presos.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default presosResolve;

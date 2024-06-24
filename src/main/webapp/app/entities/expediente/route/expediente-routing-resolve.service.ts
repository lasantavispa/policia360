import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExpediente } from '../expediente.model';
import { ExpedienteService } from '../service/expediente.service';

const expedienteResolve = (route: ActivatedRouteSnapshot): Observable<null | IExpediente> => {
  const id = route.params['id'];
  if (id) {
    return inject(ExpedienteService)
      .find(id)
      .pipe(
        mergeMap((expediente: HttpResponse<IExpediente>) => {
          if (expediente.body) {
            return of(expediente.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default expedienteResolve;

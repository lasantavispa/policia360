import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITrabajadores, NewTrabajadores } from '../trabajadores.model';

export type PartialUpdateTrabajadores = Partial<ITrabajadores> & Pick<ITrabajadores, 'id'>;

type RestOf<T extends ITrabajadores | NewTrabajadores> = Omit<T, 'antiguedad'> & {
  antiguedad?: string | null;
};

export type RestTrabajadores = RestOf<ITrabajadores>;

export type NewRestTrabajadores = RestOf<NewTrabajadores>;

export type PartialUpdateRestTrabajadores = RestOf<PartialUpdateTrabajadores>;

export type EntityResponseType = HttpResponse<ITrabajadores>;
export type EntityArrayResponseType = HttpResponse<ITrabajadores[]>;

@Injectable({ providedIn: 'root' })
export class TrabajadoresService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/trabajadores');

  create(trabajadores: NewTrabajadores): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trabajadores);
    return this.http
      .post<RestTrabajadores>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(trabajadores: ITrabajadores): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trabajadores);
    return this.http
      .put<RestTrabajadores>(`${this.resourceUrl}/${this.getTrabajadoresIdentifier(trabajadores)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(trabajadores: PartialUpdateTrabajadores): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(trabajadores);
    return this.http
      .patch<RestTrabajadores>(`${this.resourceUrl}/${this.getTrabajadoresIdentifier(trabajadores)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTrabajadores>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTrabajadores[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTrabajadoresIdentifier(trabajadores: Pick<ITrabajadores, 'id'>): number {
    return trabajadores.id;
  }

  compareTrabajadores(o1: Pick<ITrabajadores, 'id'> | null, o2: Pick<ITrabajadores, 'id'> | null): boolean {
    return o1 && o2 ? this.getTrabajadoresIdentifier(o1) === this.getTrabajadoresIdentifier(o2) : o1 === o2;
  }

  addTrabajadoresToCollectionIfMissing<Type extends Pick<ITrabajadores, 'id'>>(
    trabajadoresCollection: Type[],
    ...trabajadoresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const trabajadores: Type[] = trabajadoresToCheck.filter(isPresent);
    if (trabajadores.length > 0) {
      const trabajadoresCollectionIdentifiers = trabajadoresCollection.map(trabajadoresItem =>
        this.getTrabajadoresIdentifier(trabajadoresItem),
      );
      const trabajadoresToAdd = trabajadores.filter(trabajadoresItem => {
        const trabajadoresIdentifier = this.getTrabajadoresIdentifier(trabajadoresItem);
        if (trabajadoresCollectionIdentifiers.includes(trabajadoresIdentifier)) {
          return false;
        }
        trabajadoresCollectionIdentifiers.push(trabajadoresIdentifier);
        return true;
      });
      return [...trabajadoresToAdd, ...trabajadoresCollection];
    }
    return trabajadoresCollection;
  }

  protected convertDateFromClient<T extends ITrabajadores | NewTrabajadores | PartialUpdateTrabajadores>(trabajadores: T): RestOf<T> {
    return {
      ...trabajadores,
      antiguedad: trabajadores.antiguedad?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restTrabajadores: RestTrabajadores): ITrabajadores {
    return {
      ...restTrabajadores,
      antiguedad: restTrabajadores.antiguedad ? dayjs(restTrabajadores.antiguedad) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTrabajadores>): HttpResponse<ITrabajadores> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTrabajadores[]>): HttpResponse<ITrabajadores[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

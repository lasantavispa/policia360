import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExpediente, NewExpediente } from '../expediente.model';

export type PartialUpdateExpediente = Partial<IExpediente> & Pick<IExpediente, 'id'>;

type RestOf<T extends IExpediente | NewExpediente> = Omit<T, 'fecha'> & {
  fecha?: string | null;
};

export type RestExpediente = RestOf<IExpediente>;

export type NewRestExpediente = RestOf<NewExpediente>;

export type PartialUpdateRestExpediente = RestOf<PartialUpdateExpediente>;

export type EntityResponseType = HttpResponse<IExpediente>;
export type EntityArrayResponseType = HttpResponse<IExpediente[]>;

@Injectable({ providedIn: 'root' })
export class ExpedienteService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/expedientes');

  create(expediente: NewExpediente): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(expediente);
    return this.http
      .post<RestExpediente>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(expediente: IExpediente): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(expediente);
    return this.http
      .put<RestExpediente>(`${this.resourceUrl}/${this.getExpedienteIdentifier(expediente)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(expediente: PartialUpdateExpediente): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(expediente);
    return this.http
      .patch<RestExpediente>(`${this.resourceUrl}/${this.getExpedienteIdentifier(expediente)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestExpediente>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExpediente[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getPrisonersPerExp(): Observable<HttpResponse<Object[]>> {
    return this.http.get<Object[]>(`${this.resourceUrl}/prisionersExp`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getExpedienteIdentifier(expediente: Pick<IExpediente, 'id'>): number {
    return expediente.id;
  }

  compareExpediente(o1: Pick<IExpediente, 'id'> | null, o2: Pick<IExpediente, 'id'> | null): boolean {
    return o1 && o2 ? this.getExpedienteIdentifier(o1) === this.getExpedienteIdentifier(o2) : o1 === o2;
  }

  addExpedienteToCollectionIfMissing<Type extends Pick<IExpediente, 'id'>>(
    expedienteCollection: Type[],
    ...expedientesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const expedientes: Type[] = expedientesToCheck.filter(isPresent);
    if (expedientes.length > 0) {
      const expedienteCollectionIdentifiers = expedienteCollection.map(expedienteItem => this.getExpedienteIdentifier(expedienteItem));
      const expedientesToAdd = expedientes.filter(expedienteItem => {
        const expedienteIdentifier = this.getExpedienteIdentifier(expedienteItem);
        if (expedienteCollectionIdentifiers.includes(expedienteIdentifier)) {
          return false;
        }
        expedienteCollectionIdentifiers.push(expedienteIdentifier);
        return true;
      });
      return [...expedientesToAdd, ...expedienteCollection];
    }
    return expedienteCollection;
  }

  protected convertDateFromClient<T extends IExpediente | NewExpediente | PartialUpdateExpediente>(expediente: T): RestOf<T> {
    return {
      ...expediente,
      fecha: expediente.fecha?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restExpediente: RestExpediente): IExpediente {
    return {
      ...restExpediente,
      fecha: restExpediente.fecha ? dayjs(restExpediente.fecha) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestExpediente>): HttpResponse<IExpediente> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestExpediente[]>): HttpResponse<IExpediente[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICalendario, NewCalendario } from '../calendario.model';

export type PartialUpdateCalendario = Partial<ICalendario> & Pick<ICalendario, 'id'>;

type RestOf<T extends ICalendario | NewCalendario> = Omit<T, 'fechaInicio' | 'fechaFin'> & {
  fechaInicio?: string | null;
  fechaFin?: string | null;
};

export type RestCalendario = RestOf<ICalendario>;

export type NewRestCalendario = RestOf<NewCalendario>;

export type PartialUpdateRestCalendario = RestOf<PartialUpdateCalendario>;

export type EntityResponseType = HttpResponse<ICalendario>;
export type EntityArrayResponseType = HttpResponse<ICalendario[]>;

@Injectable({ providedIn: 'root' })
export class CalendarioService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/calendarios');

  create(calendario: NewCalendario): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendario);
    return this.http
      .post<RestCalendario>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(calendario: ICalendario): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendario);
    return this.http
      .put<RestCalendario>(`${this.resourceUrl}/${this.getCalendarioIdentifier(calendario)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(calendario: PartialUpdateCalendario): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(calendario);
    return this.http
      .patch<RestCalendario>(`${this.resourceUrl}/${this.getCalendarioIdentifier(calendario)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCalendario>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCalendario[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCalendarioIdentifier(calendario: Pick<ICalendario, 'id'>): number {
    return calendario.id;
  }

  compareCalendario(o1: Pick<ICalendario, 'id'> | null, o2: Pick<ICalendario, 'id'> | null): boolean {
    return o1 && o2 ? this.getCalendarioIdentifier(o1) === this.getCalendarioIdentifier(o2) : o1 === o2;
  }

  addCalendarioToCollectionIfMissing<Type extends Pick<ICalendario, 'id'>>(
    calendarioCollection: Type[],
    ...calendariosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const calendarios: Type[] = calendariosToCheck.filter(isPresent);
    if (calendarios.length > 0) {
      const calendarioCollectionIdentifiers = calendarioCollection.map(calendarioItem => this.getCalendarioIdentifier(calendarioItem));
      const calendariosToAdd = calendarios.filter(calendarioItem => {
        const calendarioIdentifier = this.getCalendarioIdentifier(calendarioItem);
        if (calendarioCollectionIdentifiers.includes(calendarioIdentifier)) {
          return false;
        }
        calendarioCollectionIdentifiers.push(calendarioIdentifier);
        return true;
      });
      return [...calendariosToAdd, ...calendarioCollection];
    }
    return calendarioCollection;
  }

  protected convertDateFromClient<T extends ICalendario | NewCalendario | PartialUpdateCalendario>(calendario: T): RestOf<T> {
    return {
      ...calendario,
      fechaInicio: calendario.fechaInicio?.format(DATE_FORMAT) ?? null,
      fechaFin: calendario.fechaFin?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restCalendario: RestCalendario): ICalendario {
    return {
      ...restCalendario,
      fechaInicio: restCalendario.fechaInicio ? dayjs(restCalendario.fechaInicio) : undefined,
      fechaFin: restCalendario.fechaFin ? dayjs(restCalendario.fechaFin) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCalendario>): HttpResponse<ICalendario> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCalendario[]>): HttpResponse<ICalendario[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

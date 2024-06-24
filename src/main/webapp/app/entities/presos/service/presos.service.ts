import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPresos, NewPresos } from '../presos.model';

export type PartialUpdatePresos = Partial<IPresos> & Pick<IPresos, 'id'>;

type RestOf<T extends IPresos | NewPresos> = Omit<T, 'edad'> & {
  edad?: string | null;
};

export type RestPresos = RestOf<IPresos>;

export type NewRestPresos = RestOf<NewPresos>;

export type PartialUpdateRestPresos = RestOf<PartialUpdatePresos>;

export type EntityResponseType = HttpResponse<IPresos>;
export type EntityArrayResponseType = HttpResponse<IPresos[]>;

@Injectable({ providedIn: 'root' })
export class PresosService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/presos');

  create(presos: NewPresos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presos);
    return this.http
      .post<RestPresos>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(presos: IPresos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presos);
    return this.http
      .put<RestPresos>(`${this.resourceUrl}/${this.getPresosIdentifier(presos)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(presos: PartialUpdatePresos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(presos);
    return this.http
      .patch<RestPresos>(`${this.resourceUrl}/${this.getPresosIdentifier(presos)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPresos>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPresos[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPresosIdentifier(presos: Pick<IPresos, 'id'>): number {
    return presos.id;
  }

  comparePresos(o1: Pick<IPresos, 'id'> | null, o2: Pick<IPresos, 'id'> | null): boolean {
    return o1 && o2 ? this.getPresosIdentifier(o1) === this.getPresosIdentifier(o2) : o1 === o2;
  }

  addPresosToCollectionIfMissing<Type extends Pick<IPresos, 'id'>>(
    presosCollection: Type[],
    ...presosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const presos: Type[] = presosToCheck.filter(isPresent);
    if (presos.length > 0) {
      const presosCollectionIdentifiers = presosCollection.map(presosItem => this.getPresosIdentifier(presosItem));
      const presosToAdd = presos.filter(presosItem => {
        const presosIdentifier = this.getPresosIdentifier(presosItem);
        if (presosCollectionIdentifiers.includes(presosIdentifier)) {
          return false;
        }
        presosCollectionIdentifiers.push(presosIdentifier);
        return true;
      });
      return [...presosToAdd, ...presosCollection];
    }
    return presosCollection;
  }

  protected convertDateFromClient<T extends IPresos | NewPresos | PartialUpdatePresos>(presos: T): RestOf<T> {
    return {
      ...presos,
      edad: presos.edad?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPresos: RestPresos): IPresos {
    return {
      ...restPresos,
      edad: restPresos.edad ? dayjs(restPresos.edad) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPresos>): HttpResponse<IPresos> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPresos[]>): HttpResponse<IPresos[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

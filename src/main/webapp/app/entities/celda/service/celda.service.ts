import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { map, Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICelda, NewCelda } from '../celda.model';

export type PartialUpdateCelda = Partial<ICelda> & Pick<ICelda, 'id'>;

export type EntityResponseType = HttpResponse<ICelda>;
export type EntityArrayResponseType = HttpResponse<ICelda[]>;

@Injectable({ providedIn: 'root' })
export class CeldaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/celdas');

  create(celda: NewCelda): Observable<EntityResponseType> {
    return this.http.post<ICelda>(this.resourceUrl, celda, { observe: 'response' });
  }

  update(celda: ICelda): Observable<EntityResponseType> {
    return this.http.put<ICelda>(`${this.resourceUrl}/${this.getCeldaIdentifier(celda)}`, celda, { observe: 'response' });
  }

  partialUpdate(celda: PartialUpdateCelda): Observable<EntityResponseType> {
    return this.http.patch<ICelda>(`${this.resourceUrl}/${this.getCeldaIdentifier(celda)}`, celda, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICelda>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICelda[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getPrisoners(): Observable<HttpResponse<Object[]>> {
    return this.http.get<Object[]>(`${this.resourceUrl}/prisioners`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCeldaIdentifier(celda: Pick<ICelda, 'id'>): number {
    return celda.id;
  }

  compareCelda(o1: Pick<ICelda, 'id'> | null, o2: Pick<ICelda, 'id'> | null): boolean {
    return o1 && o2 ? this.getCeldaIdentifier(o1) === this.getCeldaIdentifier(o2) : o1 === o2;
  }

  addCeldaToCollectionIfMissing<Type extends Pick<ICelda, 'id'>>(
    celdaCollection: Type[],
    ...celdasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const celdas: Type[] = celdasToCheck.filter(isPresent);
    if (celdas.length > 0) {
      const celdaCollectionIdentifiers = celdaCollection.map(celdaItem => this.getCeldaIdentifier(celdaItem));
      const celdasToAdd = celdas.filter(celdaItem => {
        const celdaIdentifier = this.getCeldaIdentifier(celdaItem);
        if (celdaCollectionIdentifiers.includes(celdaIdentifier)) {
          return false;
        }
        celdaCollectionIdentifiers.push(celdaIdentifier);
        return true;
      });
      return [...celdasToAdd, ...celdaCollection];
    }
    return celdaCollection;
  }
}

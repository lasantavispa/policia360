import { Component, NgZone, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { ICelda } from '../celda.model';
import { EntityArrayResponseType, CeldaService } from '../service/celda.service';
import { CeldaDeleteDialogComponent } from '../delete/celda-delete-dialog.component';
import { HttpResponse } from '@angular/common/http';
import { NumeroCamas } from 'app/entities/enumerations/numero-camas.model';

@Component({
  standalone: true,
  selector: 'jhi-celda',
  styleUrl: './celda.component.scss',
  templateUrl: './celda.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class CeldaComponent implements OnInit {
  subscription: Subscription | null = null;
  celdas?: ICelda[];
  isLoading = false;

  sortState = sortStateSignal({});

  public router = inject(Router);
  protected celdaService = inject(CeldaService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: ICelda): number => this.celdaService.getCeldaIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.celdas || this.celdas.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(celda: ICelda): void {
    const modalRef = this.modalService.open(CeldaDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.celda = celda;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
        this.getPrisonersC();
      },
    });
  }
  getPrisonersC(): void {
    this.celdaService.getPrisoners().subscribe({
      next: (res: HttpResponse<Object[]>) => {
        const prisoners = res.body ?? [];
        this.assignPrisonersToCeldas(prisoners);
        this.updateAvailability();
      },
      error: error => {
        console.error('Error al obtener prisioneros:', error);
      },
    });
  }

  protected assignPrisonersToCeldas(prisoners: Object[]): void {
    const prisonerMap = new Map<number, number[]>();
    prisoners.forEach((item: any) => {
      const [celdaId, presoId] = item as [number, number];
      if (!prisonerMap.has(celdaId)) {
        prisonerMap.set(celdaId, []);
      }
      prisonerMap.get(celdaId)?.push(presoId);
    });

    if (this.celdas) {
      this.celdas.forEach(celda => {
        celda.presos = prisonerMap.get(celda.id) ?? [];
      });
    }
  }

  protected updateAvailability(): void {
    const numeroCamasMap: { [key in NumeroCamas]: number } = {
      [NumeroCamas.UNO]: 1,
      [NumeroCamas.DOS]: 2,
      [NumeroCamas.TRES]: 3,
      [NumeroCamas.CUATRO]: 4,
    };

    if (this.celdas) {
      this.celdas.forEach(celda => {
        if (celda.numeroCamas && celda.presos) {
          const numeroCamas = numeroCamasMap[celda.numeroCamas];
          celda.disponibilidad = celda.presos.length < numeroCamas;
        } else {
          celda.disponibilidad = true; // Asume disponible si no hay datos suficientes
        }
      });
    }
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.celdas = this.refineData(dataFromBody);
  }

  protected refineData(data: ICelda[]): ICelda[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ICelda[] | null): ICelda[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.celdaService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}

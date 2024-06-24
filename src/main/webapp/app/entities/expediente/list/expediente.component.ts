import { Component, NgZone, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { IExpediente } from '../expediente.model';
import { EntityArrayResponseType, ExpedienteService } from '../service/expediente.service';
import { ExpedienteDeleteDialogComponent } from '../delete/expediente-delete-dialog.component';
import { HttpResponse } from '@angular/common/http';
import { FilterResolutionComponent } from 'app/layouts/filter-resolution/filter-resolution.component';

@Component({
  standalone: true,
  selector: 'jhi-expediente',
  styleUrl: './expediente.component.scss',
  templateUrl: './expediente.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterResolutionComponent,
  ],
})
export class ExpedienteComponent implements OnInit {
  subscription: Subscription | null = null;
  expedientes?: IExpediente[];
  expedientesFiltered?: IExpediente[];
  isLoading = false;

  sortState = sortStateSignal({});

  public router = inject(Router);
  protected expedienteService = inject(ExpedienteService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: IExpediente): number => this.expedienteService.getExpedienteIdentifier(item);
  selectedResolution!: string;

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.expedientes || this.expedientes.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  filterResolutions(selectedResolution: string): void {
    if (selectedResolution !== '' && selectedResolution !== null && this.expedientes !== undefined) {
      this.expedientesFiltered = this.expedientes.filter(expediente => expediente.resolucion === selectedResolution);
    } else if (selectedResolution == null) {
      console.log(selectedResolution);
      this.expedientesFiltered = this.expedientes;
    }
  }

  delete(expediente: IExpediente): void {
    const modalRef = this.modalService.open(ExpedienteDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.expediente = expediente;
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
        this.getPrisonersPerExp();
      },
    });
  }

  getPrisonersPerExp(): void {
    this.expedienteService.getPrisonersPerExp().subscribe({
      next: (res: HttpResponse<Object[]>) => {
        const prisoners = res.body ?? [];
        this.assignPrisonersToExpedients(prisoners);
      },
      error: error => {
        console.error('Error al obtener prisioneros:', error);
      },
    });
  }

  protected assignPrisonersToExpedients(prisoners: Object[]): void {
    const prisonerMap = new Map<number, number[]>();
    prisoners.forEach((item: any) => {
      const [expedienteId, presoId] = item as [number, number];
      if (!prisonerMap.has(expedienteId)) {
        prisonerMap.set(expedienteId, []);
      }
      prisonerMap.get(expedienteId)?.push(presoId);
    });
    console.log(prisonerMap);

    // Actualizar expedientes con los presos correspondientes
    if (this.expedientes) {
      this.expedientes.forEach(expediente => {
        expediente.presos = prisonerMap.get(expediente.id) ?? [];
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
    this.expedientes = this.refineData(dataFromBody);
  }

  protected refineData(data: IExpediente[]): IExpediente[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IExpediente[] | null): IExpediente[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.expedienteService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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

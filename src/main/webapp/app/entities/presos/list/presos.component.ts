import { Component, NgZone, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { IPresos } from '../presos.model';
import { EntityArrayResponseType, PresosService } from '../service/presos.service';
import { PresosDeleteDialogComponent } from '../delete/presos-delete-dialog.component';
import dayjs from 'dayjs';
import { PresosUpdateComponent } from '../update/presos-update.component';
import { FilterResolutionComponent } from 'app/layouts/filter-resolution/filter-resolution.component';

@Component({
  standalone: true,
  selector: 'jhi-presos',
  styleUrl: './presos.component.scss',
  templateUrl: './presos.component.html',
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
export class PresosComponent implements OnInit {
  subscription: Subscription | null = null;
  presos?: IPresos[];
  presosFiltered?: IPresos[];
  isLoading = false;

  sortState = sortStateSignal({});

  public router = inject(Router);
  protected presosService = inject(PresosService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  private modalservice = inject(NgbModal);

  trackId = (_index: number, item: IPresos): number => this.presosService.getPresosIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.presos || this.presos.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  filterResolutions(selectedResolution: string): void {
    if (selectedResolution !== '' && selectedResolution !== null && this.presos !== undefined) {
      this.presosFiltered = this.presos.filter(preso => preso.expediente?.resolucion === selectedResolution);
      console.log(selectedResolution);
      //  console.log(preso.expediente);
    } else if (selectedResolution == null) {
      console.log(selectedResolution);
      this.presosFiltered = this.presos;
    }
  }

  openAddPrisoner() {
    const modalRef = this.modalservice.open(PresosUpdateComponent);
    modalRef.componentInstance.name = 'Crear Preso';
  }

  delete(presos: IPresos): void {
    const modalRef = this.modalService.open(PresosDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.presos = presos;
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
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  calcularEdad(fechaNacimiento: dayjs.Dayjs): number {
    const hoy = dayjs();
    let edad = hoy.year() - fechaNacimiento.year();
    if (hoy.month() < fechaNacimiento.month() || (hoy.month() === fechaNacimiento.month() && hoy.date() < fechaNacimiento.date())) {
      edad--;
    }
    return edad;
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.presos = this.refineData(dataFromBody);
    this.presos.forEach(preso => {
      if (preso.edad) {
        preso.age = this.calcularEdad(preso.edad);
      }
    });
  }

  protected refineData(data: IPresos[]): IPresos[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IPresos[] | null): IPresos[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.presosService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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

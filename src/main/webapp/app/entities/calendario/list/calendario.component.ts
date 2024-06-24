import { Component, NgZone, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { ICalendario } from '../calendario.model';
import { EntityArrayResponseType, CalendarioService } from '../service/calendario.service';
import { CalendarioDeleteDialogComponent } from '../delete/calendario-delete-dialog.component';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, DateSelectArg, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import esLocale from '@fullcalendar/core/locales/es';
import dayjs from 'dayjs';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';
import { CalendarioUpdateComponent } from '../update/calendario-update.component';
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  standalone: true,
  styleUrl: './calendario.component.scss',
  selector: 'jhi-calendario',
  templateUrl: './calendario.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FullCalendarModule,
    HasAnyAuthorityDirective,
  ],
})
export class CalendarioComponent implements OnInit {
  calendarOptions: CalendarOptions = {
    themeSystem: 'bootstrap5',
    initialView: 'dayGridMonth',
    plugins: [dayGridPlugin, interactionPlugin],
    locale: esLocale,
    aspectRatio: 1.087,
    selectable: true,
    editable: true,
    // eventClick: this.handleDateSelect,
    select: this.handleDateSelect,
    dateClick: this.onDateClick.bind(this),
    // selectAllow: this.onDateClick.bind(this),
    // selectionInfo: this.onDateClick.bind(this),
    // selectLongPressDelay: this.handleDateSelect,
    // eventClick: function(info) {
    // selectHelper: true,
    // selectHelper: true,
    // selectLongPressDelay: this.onDateClick.bind(this),
    // select: this.onDateClick.bind(this),
    // selectAllow: this.onDateClick.bind(this),
    // dateClick: this.onDateClick.bind(this),
    // eventClick: function(info) {
    //   info.jsEvent.preventDefault(); // don't let the browser navigate

    //   if (info.event.url) {
    //     window.open('/calendario', info.event.id, 'view');
    //   }
    // },

    // select: this.openAddVancances,
    // selectHelper: true,
    // dateClick: (arg: any) => this.handleDateClick(arg),
    displayEventTime: false,
    eventColor: '#3B417B',
    eventBorderColor: '#3B417B',
  };

  eventsPromise!: Promise<EventInput[]>;

  handleDateClick(arg: { dateStr: string }) {
    alert('date click! ' + arg.dateStr);
  }

  // convertirDatos() {
  //datos que coges de la bd, tiene que ser un array, te lo devuelve de tipo ICalendar
  //convertir el array al formato que pide el calendario (titulo, inicio, finalización)
  // }

  subscription: Subscription | null = null;
  calendarios?: ICalendario[];
  isLoading = false;

  sortState = sortStateSignal({});

  public router = inject(Router);
  protected calendarioService = inject(CalendarioService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);
  private modalservice = inject(NgbModal);

  trackId = (_index: number, item: ICalendario): number => this.calendarioService.getCalendarioIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (!this.calendarios || this.calendarios.length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(calendario: ICalendario): void {
    const modalRef = this.modalService.open(CalendarioDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.calendario = calendario;
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
        this.iniciarCalendarios();
        console.log(this.calendarios);
      },
    });
  }

  handleDateSelect(arg: DateSelectArg): void {
    const dateStr = arg.startStr; // O la propiedad relevante que necesites
    console.log(dateStr);
    this.onDateClick;
    // Tu lógica con dateStr
  }

  onDateClick(fecha: { dateStr: string }): void {
    const modalRef = this.modalService.open(CalendarioUpdateComponent);
    const fechaString = fecha.dateStr;
    modalRef.componentInstance.name = 'Crear nuevas vacaciones';
  }

  openAddVancances() {
    const modalRef = this.modalservice.open(CalendarioUpdateComponent);
    modalRef.componentInstance.name = 'Crear nuevas vacaciones';
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.calendarios = this.refineData(dataFromBody);
    console.log(dataFromBody);
    console.log(this.calendarios);
  }

  protected refineData(data: ICalendario[]): ICalendario[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ICalendario[] | null): ICalendario[] {
    return data ?? [];
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.calendarioService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
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

  iniciarCalendarios(): void {
    console.log(this.calendarios);
    console.log('cositas');

    if (this.calendarios) {
      this.calendarOptions = {
        initialView: 'dayGridMonth',
        plugins: [dayGridPlugin],
        locale: esLocale,
        aspectRatio: 2.087,
        events: this.convertirDatos(this.calendarios),
      };
    }
  }

  // convertirDatos(calendarios: ICalendario[]): { title: string; start: string; end?: string }[] {
  //   return calendarios.map(calendario => {
  //     const title = `Id: ${calendario.trabajadores?.id} - ${calendario.concepto}`;
  //     const start = calendario.fechaInicio?.toISOString() ?? '';
  //     const end = calendario.fechaFin?.toISOString() ?? undefined;
  //     console.log(title);
  //     return { title, start, end };
  //   });
  // }

  convertirDatos(calendarios: ICalendario[]): { title: string; start: string; end?: string }[] {
    return calendarios.map(calendario => {
      const title = `Id: ${calendario.trabajadores?.id} - ${calendario.concepto}`;
      const start = calendario.fechaInicio?.toISOString() ?? '';

      let end: string | undefined = undefined;
      if (calendario.fechaFin) {
        const fechaFin = dayjs(calendario.fechaFin).add(1, 'day').toDate(); // Añadir un día usando dayjs
        end = fechaFin.toISOString();
      }

      console.log(title);
      return { title, start, end };
    });
  }
}

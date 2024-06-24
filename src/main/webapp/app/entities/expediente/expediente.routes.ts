import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { ExpedienteComponent } from './list/expediente.component';
import { ExpedienteDetailComponent } from './detail/expediente-detail.component';
import { ExpedienteUpdateComponent } from './update/expediente-update.component';
import ExpedienteResolve from './route/expediente-routing-resolve.service';

const expedienteRoute: Routes = [
  {
    path: '',
    component: ExpedienteComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ExpedienteDetailComponent,
    resolve: {
      expediente: ExpedienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ExpedienteUpdateComponent,
    resolve: {
      expediente: ExpedienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ExpedienteUpdateComponent,
    resolve: {
      expediente: ExpedienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default expedienteRoute;

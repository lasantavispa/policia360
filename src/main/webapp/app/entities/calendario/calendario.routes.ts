import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CalendarioComponent } from './list/calendario.component';
import { CalendarioDetailComponent } from './detail/calendario-detail.component';
import { CalendarioUpdateComponent } from './update/calendario-update.component';
import CalendarioResolve from './route/calendario-routing-resolve.service';

const calendarioRoute: Routes = [
  {
    path: '',
    component: CalendarioComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CalendarioDetailComponent,
    resolve: {
      calendario: CalendarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CalendarioUpdateComponent,
    resolve: {
      calendario: CalendarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CalendarioUpdateComponent,
    resolve: {
      calendario: CalendarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default calendarioRoute;

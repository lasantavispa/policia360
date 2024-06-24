import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CeldaComponent } from './list/celda.component';
import { CeldaDetailComponent } from './detail/celda-detail.component';
import { CeldaUpdateComponent } from './update/celda-update.component';
import CeldaResolve from './route/celda-routing-resolve.service';

const celdaRoute: Routes = [
  {
    path: '',
    component: CeldaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CeldaDetailComponent,
    resolve: {
      celda: CeldaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CeldaUpdateComponent,
    resolve: {
      celda: CeldaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CeldaUpdateComponent,
    resolve: {
      celda: CeldaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default celdaRoute;

import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PresosComponent } from './list/presos.component';
import { PresosDetailComponent } from './detail/presos-detail.component';
import { PresosUpdateComponent } from './update/presos-update.component';
import PresosResolve from './route/presos-routing-resolve.service';

const presosRoute: Routes = [
  {
    path: '',
    component: PresosComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PresosDetailComponent,
    resolve: {
      presos: PresosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PresosUpdateComponent,
    resolve: {
      presos: PresosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PresosUpdateComponent,
    resolve: {
      presos: PresosResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default presosRoute;

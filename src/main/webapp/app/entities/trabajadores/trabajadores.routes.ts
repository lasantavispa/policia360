import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TrabajadoresComponent } from './list/trabajadores.component';
import { TrabajadoresDetailComponent } from './detail/trabajadores-detail.component';
import { TrabajadoresUpdateComponent } from './update/trabajadores-update.component';
import TrabajadoresResolve from './route/trabajadores-routing-resolve.service';

const trabajadoresRoute: Routes = [
  {
    path: '',
    component: TrabajadoresComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TrabajadoresDetailComponent,
    resolve: {
      trabajadores: TrabajadoresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TrabajadoresUpdateComponent,
    resolve: {
      trabajadores: TrabajadoresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TrabajadoresUpdateComponent,
    resolve: {
      trabajadores: TrabajadoresResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default trabajadoresRoute;

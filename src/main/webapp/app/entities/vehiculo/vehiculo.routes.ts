import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { VehiculoComponent } from './list/vehiculo.component';
import { VehiculoDetailComponent } from './detail/vehiculo-detail.component';
import { VehiculoUpdateComponent } from './update/vehiculo-update.component';
import VehiculoResolve from './route/vehiculo-routing-resolve.service';

const vehiculoRoute: Routes = [
  {
    path: '',
    component: VehiculoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: VehiculoDetailComponent,
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: VehiculoUpdateComponent,
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: VehiculoUpdateComponent,
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default vehiculoRoute;

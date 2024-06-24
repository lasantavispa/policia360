import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'policia360App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'calendario',
    data: { pageTitle: 'policia360App.calendario.home.title' },
    loadChildren: () => import('./calendario/calendario.routes'),
  },
  {
    path: 'celda',
    data: { pageTitle: 'policia360App.celda.home.title' },
    loadChildren: () => import('./celda/celda.routes'),
  },
  {
    path: 'expediente',
    data: { pageTitle: 'policia360App.expediente.home.title' },
    loadChildren: () => import('./expediente/expediente.routes'),
  },
  {
    path: 'presos',
    data: { pageTitle: 'policia360App.presos.home.title' },
    loadChildren: () => import('./presos/presos.routes'),
  },
  {
    path: 'trabajadores',
    data: { pageTitle: 'policia360App.trabajadores.home.title' },
    loadChildren: () => import('./trabajadores/trabajadores.routes'),
  },
  {
    path: 'vehiculo',
    data: { pageTitle: 'policia360App.vehiculo.home.title' },
    loadChildren: () => import('./vehiculo/vehiculo.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;

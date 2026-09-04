import { Routes } from '@angular/router';
import { blockAdministrateur, requireEtudiant, requireManager } from './core/auth/role.guard';

/**
 * Top-level routes. `student` and `admin` are the two independent shells,
 * each guarded at its natural role floor (any authenticated user for the
 * student shell, MANAGER-and-above for the admin shell), lazy-loaded so
 * neither shell pulls in the other's bundle. `student` also runs
 * `blockAdministrateur` alongside `requireEtudiant`: ADMINISTRATEUR holds
 * every role beneath it in the hierarchy so requireEtudiant alone would let
 * them in — they're bounced to /admin instead, since they have their own
 * dashboard and shouldn't be operating the self-service student views.
 */
export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'unauthorized',
    loadComponent: () =>
      import('./pages/unauthorized/unauthorized.component').then((m) => m.UnauthorizedComponent),
  },
  {
    path: 'forbidden',
    loadComponent: () =>
      import('./pages/forbidden/forbidden.component').then((m) => m.ForbiddenComponent),
  },
  {
    path: 'student',
    canActivate: [requireEtudiant, blockAdministrateur],
    loadChildren: () => import('./student/student.routes').then((m) => m.studentRoutes),
  },
  {
    path: 'admin',
    canActivate: [requireManager],
    loadChildren: () => import('./admin/admin.routes').then((m) => m.ADMIN_ROUTES),
  },
  { path: '**', redirectTo: '' },
];

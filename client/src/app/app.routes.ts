import { Routes } from '@angular/router';
import { requireEtudiant, requireManager } from './core/auth/role.guard';

/**
 * Top-level routes. `student` and `admin` are the two independent shells,
 * each guarded at its natural role floor (any authenticated user for the
 * student shell, MANAGER-and-above for the admin shell), lazy-loaded so
 * neither shell pulls in the other's bundle. `student` now points at the
 * real Track A shell (see ./student/student.routes.ts); `admin` is still
 * the Track B placeholder.
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
    canActivate: [requireEtudiant],
    loadChildren: () => import('./student/student.routes').then((m) => m.studentRoutes),
  },
  {
    path: 'admin',
    canActivate: [requireManager],
    // TODO(admin-shell): loadChildren pointing at the admin shell's own routes file.
    children: [],
  },
  { path: '**', redirectTo: '' },
];

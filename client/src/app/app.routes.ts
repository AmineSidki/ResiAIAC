import { Routes } from '@angular/router';
import { requireEtudiant, requireManager } from './core/auth/role.guard';

/**
 * Scaffold-level routes only. The `student` and `admin` route groups are
 * placeholders for the two shells to be built independently on top of this
 * scaffold — each guarded at its natural role floor (any authenticated user
 * for the student shell, MANAGER-and-above for the admin shell), lazy-loaded
 * so neither shell pulls in the other's bundle.
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
    // TODO(student-shell): loadChildren pointing at the student shell's own routes file.
    children: [],
  },
  {
    path: 'admin',
    canActivate: [requireManager],
    // TODO(admin-shell): loadChildren pointing at the admin shell's own routes file.
    children: [],
  },
  { path: '**', redirectTo: '' },
];

import { Routes } from '@angular/router';

/**
 * Everything reachable by a bare ETUDIANT. Loaded under the app-level
 * `/student` route (already guarded by `requireEtudiant`), lazy so the
 * admin shell never pulls this bundle in and vice versa.
 */
export const studentRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./layout/student-shell.component').then((m) => m.StudentShellComponent),
    children: [
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
      {
        path: 'profile',
        loadComponent: () => import('./pages/profile/profile.component').then((m) => m.ProfileComponent),
      },
      {
        path: 'documents',
        loadComponent: () =>
          import('./pages/documents/documents.component').then((m) => m.DocumentsComponent),
      },
      {
        path: 'reservation',
        loadComponent: () =>
          import('./pages/reservation/reservation.component').then((m) => m.ReservationComponent),
      },
      {
        path: 'reclamations',
        loadComponent: () =>
          import('./pages/reclamations/reclamations.component').then((m) => m.ReclamationsComponent),
      },
    ],
  },
];

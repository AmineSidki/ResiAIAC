import { Routes } from '@angular/router';
import { requireResponsable } from '../core/auth/role.guard';

/**
 * Mounted under /admin in app.routes.ts, which already applies
 * requireManager as the outer floor. Individual routes here tighten that
 * floor further where the backend does (Utilisateurs list, Documents list —
 * both RESPONSABLE-only server-side even though the shell itself is
 * MANAGER+). Everything else inherits the MANAGER floor from the parent.
 */
export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout/admin-shell.component').then((m) => m.AdminShellComponent),
    children: [
      { path: '', redirectTo: 'chambres', pathMatch: 'full' },

      {
        path: 'utilisateurs',
        canActivate: [requireResponsable],
        loadComponent: () => import('./pages/users/user-list.page').then((m) => m.UserListPageComponent),
      },
      {
        // Separately guarded at MANAGER (inherited) rather than RESPONSABLE:
        // GET /{id} is MANAGER-accessible even though the list isn't.
        path: 'utilisateurs/:id',
        loadComponent: () => import('./pages/users/user-detail.page').then((m) => m.UserDetailPageComponent),
      },

      {
        path: 'chambres',
        loadComponent: () => import('./pages/rooms/chambre-list.page').then((m) => m.ChambreListPageComponent),
      },
      {
        path: 'chambres/:id',
        loadComponent: () => import('./pages/rooms/chambre-detail.page').then((m) => m.ChambreDetailPageComponent),
      },

      {
        path: 'reclamations',
        loadComponent: () =>
          import('./pages/reclamations/reclamation-list.page').then((m) => m.ReclamationListPageComponent),
      },
      {
        path: 'reclamations/:id',
        loadComponent: () =>
          import('./pages/reclamations/reclamation-detail.page').then((m) => m.ReclamationDetailPageComponent),
      },

      {
        path: 'reservations',
        loadComponent: () =>
          import('./pages/reservations/reservation-list.page').then((m) => m.ReservationListPageComponent),
      },
      {
        path: 'reservations/:id',
        loadComponent: () =>
          import('./pages/reservations/reservation-detail.page').then((m) => m.ReservationDetailPageComponent),
      },

      {
        path: 'documents',
        canActivate: [requireResponsable],
        loadComponent: () => import('./pages/documents/document-list.page').then((m) => m.DocumentListPageComponent),
      },
      {
        // getById/getUrlById/update are all MANAGER-gated — only the list is stricter.
        path: 'documents/:id',
        loadComponent: () =>
          import('./pages/documents/document-detail.page').then((m) => m.DocumentDetailPageComponent),
      },

      // Batiment/Etage/Filiere/Service/Equipement GET (list + by-id) are all
      // open to any authenticated user server-side — only writes are
      // role-gated (RESPONSABLE for the first four, MANAGER for Equipement).
      // None of these routes narrow the route-level guard beyond the
      // inherited MANAGER floor; each page component itself hides
      // create/edit/delete via canWrite() so a plain MANAGER still sees the
      // reference data, just without write actions on the four that need
      // RESPONSABLE.
      {
        path: 'reference/batiments',
        loadComponent: () => import('./pages/reference-data/batiment.page').then((m) => m.BatimentPageComponent),
      },
      {
        path: 'reference/etages',
        loadComponent: () => import('./pages/reference-data/etage.page').then((m) => m.EtagePageComponent),
      },
      {
        path: 'reference/filieres',
        loadComponent: () => import('./pages/reference-data/filiere.page').then((m) => m.FilierePageComponent),
      },
      {
        path: 'reference/services',
        loadComponent: () =>
          import('./pages/reference-data/service-entity.page').then((m) => m.ServiceEntityPageComponent),
      },
      {
        path: 'reference/equipements',
        loadComponent: () => import('./pages/reference-data/equipement.page').then((m) => m.EquipementPageComponent),
      },

      // TEMPORARY diagnostic routes — bypass BaseCrudService and
      // EntityCrudTableComponent entirely to isolate which layer is
      // responsible for the etage/chambre/batiment bug. Remove these three
      // once the theory has been tested.
      {
        path: 'diagnostic/etage',
        loadComponent: () =>
          import('./pages/reference-data/etage-manual.page').then((m) => m.EtageManualPageComponent),
      },
      {
        path: 'diagnostic/chambre',
        loadComponent: () =>
          import('./pages/reference-data/chambre-manual.page').then((m) => m.ChambreManualPageComponent),
      },
      {
        path: 'diagnostic/batiment',
        loadComponent: () =>
          import('./pages/reference-data/batiment-manual.page').then((m) => m.BatimentManualPageComponent),
      },
    ],
  },
];

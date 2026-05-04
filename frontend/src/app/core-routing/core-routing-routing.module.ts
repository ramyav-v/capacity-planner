import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';
import { roleGuard } from '../auth/guards/role.guard';

export const CORE_ROUTES: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [

      {
        path: 'dashboard',
        loadComponent: () =>
          import('../feature/dashboard/dashboard.component')
            .then(c => c.DashboardComponent),
        children: [
          {
            path: 'overview',
            loadComponent: () =>
              import('../feature/dashboard/components/overview/overview.component')
                .then(c => c.OverviewComponent)
          },
          {
            path: 'allocation',
            loadComponent: () =>
              import('../feature/dashboard/components/allocation/allocation.component')
                .then(c => c.AllocationComponent)
          },
          {
            path: 'hiring-gap',
            loadComponent: () =>
              import('../feature/dashboard/components/hiring-gap/hiring-gap.component')
                .then(c => c.HiringGapComponent)
          },
          {
            path: '',
            redirectTo: 'overview',
            pathMatch: 'full'
          }
        ]
      },
      {
        path: 'data-management',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'SUPER_ADMIN'] },
        loadComponent: () =>
          import('../feature/data-management/data-management.component')
            .then(c => c.DataManagementComponent),
        children: [
          {
            path: 'organization',
            loadComponent: () =>
              import('../feature/data-management/organization/organization.component')
                .then(c => c.OrganizationComponent),
            children: [
              {
                path: 'employees',
                loadComponent: () =>
                  import('../feature/data-management/organization/employees/employees.component')
                    .then(c => c.EmployeesComponent)
              },
              {
                path: 'projects',
                loadComponent: () =>
                  import('../feature/data-management/organization/projects/projects.component')
                    .then(c => c.ProjectsComponent)
              },
              {
                path: '',
                redirectTo: 'employees',
                pathMatch: 'full'
              }
            ]
          },
          {
            path: 'capacity-inputs',
            loadComponent: () =>
              import('../feature/data-management/capacity-inputs/capacity-inputs.component')
                .then(c => c.CapacityInputsComponent)
          },
          {
            path: '',
            redirectTo: 'organization',
            pathMatch: 'full'
          }
        ]
      },
      {
        path: 'project-financials',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'SUPER_ADMIN'] },
        loadComponent: () =>
          import('../feature/project-financials/project-financials.component')
            .then(c => c.ProjectFinancialsComponent),
        children: [
          {
            path: 'overview',
            loadComponent: () =>
              import('../feature/project-financials/components/financials-list/financials-list.component')
                .then(c => c.FinancialsListComponent)
          },
          {
            path: 'role-rates',
            canActivate: [roleGuard],
            data: { roles: ['ADMIN', 'SUPER_ADMIN'] },
            loadComponent: () =>
              import('../feature/project-financials/components/role-rates/role-rates.component')
                .then(c => c.RoleRatesComponent)
          },
          {
            path: '',
            redirectTo: 'overview',
            pathMatch: 'full'
          }
        ]
      },
      {
        path: 'user-management',
        canActivate: [roleGuard],
        data: { roles: ['SUPER_ADMIN'] },
        loadComponent: () =>
          import('../feature/user-management/users/users.component')
            .then(c => c.UsersComponent)
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(CORE_ROUTES)],
  exports: [RouterModule]
})
export class CoreRoutingRoutingModule { }

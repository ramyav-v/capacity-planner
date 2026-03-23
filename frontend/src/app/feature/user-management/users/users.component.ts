import { Component, NgZone, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';
import {
  ColDef,
  GridReadyEvent,
  GridApi,
  ModuleRegistry,
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule
} from 'ag-grid-community';
import { AppUser, AppUserDto, UserRole } from '../user.model';
import { UserService } from '../services/user.service';
import { AddEditUserComponent } from './components/add-edit-user/add-edit-user.component';

ModuleRegistry.registerModules([
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule
]);

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, AgGridModule, AddEditUserComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
  @ViewChild(AddEditUserComponent) addEditDialog!: AddEditUserComponent;

  private gridApi!: GridApi<AppUser>;
  searchText = '';

  // Filters
  allData: AppUser[] = [];
  filterRole: UserRole | '' = '';
  roles: UserRole[] = ['SUPER_ADMIN', 'ADMIN', 'VIEWER'];

  // Dialog state
  isDialogOpen = false;
  editingUser: AppUser | null = null;

  // Delete confirmation state
  isDeleteConfirmOpen = false;
  userToDelete: AppUser | null = null;

  columnDefs: ColDef<AppUser>[] = [
    {
      headerName: 'USERNAME',
      field: 'username',
      flex: 1.5,
      filter: 'agTextColumnFilter',
      sortable: true
    },
    {
      headerName: 'FULL NAME',
      field: 'fullName',
      flex: 1.5,
      sortable: true
    },
    {
      headerName: 'EMAIL',
      field: 'email',
      flex: 2,
      sortable: true
    },
    {
      headerName: 'ROLE',
      field: 'userRole',
      flex: 1.2,
      sortable: true,
      cellRenderer: (params: any) => {
        const role = params.value as string;
        let bgColor: string;
        let textColor: string;

        switch (role) {
          case 'SUPER_ADMIN':
            bgColor = '#fef3c7'; textColor = '#92400e'; break;
          case 'ADMIN':
            bgColor = '#dbeafe'; textColor = '#1e40af'; break;
          case 'VIEWER':
            bgColor = '#f3f4f6'; textColor = '#374151'; break;
          default:
            bgColor = '#f3f4f6'; textColor = '#374151';
        }

        return `<span style="display:inline-block;padding:4px 10px;border-radius:12px;font-size:12px;font-weight:600;background:${bgColor};color:${textColor}">${role}</span>`;
      }
    },
    {
      headerName: 'CREATED',
      field: 'createdAt',
      flex: 1.2,
      sortable: true,
      cellRenderer: (params: any) => {
        if (!params.value) return '';
        const date = new Date(params.value);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
      }
    },
    {
      headerName: '',
      field: 'id',
      width: 100,
      maxWidth: 100,
      sortable: false,
      filter: false,
      cellRenderer: (params: any) => {
        const container = document.createElement('div');
        container.classList.add('action-cell');

        const editBtn = document.createElement('button');
        editBtn.classList.add('action-btn', 'edit-btn');
        editBtn.title = 'Edit';
        editBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 000-1.41l-2.34-2.34a1 1 0 00-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>`;
        editBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.ngZone.run(() => this.onEditUser(params.data));
        });

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('action-btn', 'delete-btn');
        deleteBtn.title = 'Delete';
        deleteBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>`;
        deleteBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.ngZone.run(() => this.onDeleteUser(params.data));
        });

        container.appendChild(editBtn);
        container.appendChild(deleteBtn);
        return container;
      }
    }
  ];

  defaultColDef: ColDef = {
    resizable: true
  };

  rowData: AppUser[] = [];

  constructor(private userService: UserService, private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  onGridReady(params: GridReadyEvent<AppUser>): void {
    this.gridApi = params.api;
    this.gridApi.sizeColumnsToFit();
  }

  onSearchChange(): void {
    this.gridApi?.setGridOption('quickFilterText', this.searchText);
  }

  onDownloadCsv(): void {
    this.gridApi?.exportDataAsCsv({
      fileName: 'users.csv'
    });
  }

  onAddUser(): void {
    this.editingUser = null;
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm(null));
  }

  onEditUser(user: AppUser): void {
    this.editingUser = { ...user };
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm({ ...user }));
  }

  onCreateUser(dto: AppUserDto): void {
    this.userService.create(dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingUser = null;
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to create user', err);
      }
    });
  }

  onUpdateUser(event: { id: number; dto: AppUserDto }): void {
    this.userService.update(event.id, event.dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingUser = null;
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to update user', err);
      }
    });
  }

  onCloseDialog(): void {
    this.isDialogOpen = false;
    this.editingUser = null;
  }

  onDeleteUser(user: AppUser): void {
    this.userToDelete = user;
    this.isDeleteConfirmOpen = true;
  }

  confirmDelete(): void {
    if (this.userToDelete?.id != null) {
      this.userService.delete(this.userToDelete.id).subscribe({
        next: () => {
          this.loadUsers();
        },
        error: (err) => {
          console.error('Failed to delete user', err);
        }
      });
    }
    this.isDeleteConfirmOpen = false;
    this.userToDelete = null;
  }

  cancelDelete(): void {
    this.isDeleteConfirmOpen = false;
    this.userToDelete = null;
  }

  onFilterChange(): void {
    let filtered = this.allData;
    if (this.filterRole) {
      filtered = filtered.filter(u => u.userRole === this.filterRole);
    }
    this.rowData = filtered;
  }

  private loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (users) => {
        this.allData = users;
        this.onFilterChange();
      },
      error: (err) => {
        console.error('Failed to load users', err);
      }
    });
  }
}

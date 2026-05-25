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
import { Employee, EmployeeDto, EmployeeCompany, EmployeeRegion, EmployeeRole } from './employee.model';
import { EmployeeService } from './services/employee.service';
import { AddEditEmployeeComponent } from './add-edit-employee/add-edit-employee.component';

ModuleRegistry.registerModules([
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule
]);

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule, AgGridModule, AddEditEmployeeComponent],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.scss'
})
export class EmployeesComponent implements OnInit {
  @ViewChild(AddEditEmployeeComponent) addEditDialog!: AddEditEmployeeComponent;

  private gridApi!: GridApi<Employee>;
  searchText = '';

  // Filters
  allData: Employee[] = [];
  filterCompany: EmployeeCompany | '' = '';
  filterRegion: EmployeeRegion | '' = '';
  filterRole = '';
  filterStatus = '';
  companies: EmployeeCompany[] = ['Zinier', 'Sankey', 'TopGrep'];
  regions: EmployeeRegion[] = ['US', 'EMEA'];
  roles: EmployeeRole[] = ['PM', 'TL', 'Dev', 'QA', 'Support'];

  // Dialog state
  isDialogOpen = false;
  editingEmployee: Employee | null = null;

  // Delete confirmation state
  isDeleteConfirmOpen = false;
  employeeToDelete: Employee | null = null;

  columnDefs: ColDef<Employee>[] = [
    {
      headerName: 'NAME',
      field: 'name',
      flex: 2,
      filter: 'agTextColumnFilter',
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
      field: 'role',
      flex: 1,
      sortable: true
    },
    {
      headerName: 'COMPANY',
      field: 'company',
      flex: 1,
      sortable: true
    },
    {
      headerName: 'REGION',
      field: 'region',
      flex: 0.8,
      sortable: true
    },
    {
      headerName: 'START DATE',
      field: 'startDate',
      flex: 1.2,
      sortable: true,
      cellRenderer: (params: any) => {
        if (!params.value) return '';
        const date = new Date(params.value);
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
      }
    },
    {
      headerName: 'STATUS',
      field: 'isActive',
      flex: 1,
      sortable: true,
      cellRenderer: (params: any) => {
        const badge = document.createElement('span');
        badge.classList.add('status-badge', params.value ? 'status-active' : 'status-inactive');
        badge.textContent = params.value ? 'Active' : 'Inactive';
        return badge;
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
          this.ngZone.run(() => this.onEditEmployee(params.data));
        });

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('action-btn', 'delete-btn');
        deleteBtn.title = 'Delete';
        deleteBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>`;
        deleteBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.ngZone.run(() => this.onDeleteEmployee(params.data));
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

  rowData: Employee[] = [];

  constructor(private employeeService: EmployeeService, private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  get totalEmployees(): number {
    return this.rowData.length;
  }

  get displayedCount(): number {
    if (!this.gridApi) return this.totalEmployees;
    return this.gridApi.getDisplayedRowCount();
  }

  onGridReady(params: GridReadyEvent<Employee>): void {
    this.gridApi = params.api;
    this.gridApi.sizeColumnsToFit();
  }

  onSearchChange(): void {
    this.gridApi?.setGridOption('quickFilterText', this.searchText);
  }

  onDownloadCsv(): void {
    this.gridApi?.exportDataAsCsv({
      fileName: 'employees.csv'
    });
  }

  onAddEmployee(): void {
    this.editingEmployee = null;
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm(null));
  }

  onEditEmployee(employee: Employee): void {
    this.editingEmployee = { ...employee };
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm({ ...employee }));
  }

  onCreateEmployee(dto: EmployeeDto): void {
    this.employeeService.create(dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingEmployee = null;
        this.loadEmployees();
      },
      error: (err) => {
        console.error('Failed to create employee', err);
      }
    });
  }

  onUpdateEmployee(event: { id: number; dto: EmployeeDto }): void {
    this.employeeService.update(event.id, event.dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingEmployee = null;
        this.loadEmployees();
      },
      error: (err) => {
        console.error('Failed to update employee', err);
      }
    });
  }

  onCloseDialog(): void {
    this.isDialogOpen = false;
    this.editingEmployee = null;
  }

  onDeleteEmployee(employee: Employee): void {
    this.employeeToDelete = employee;
    this.isDeleteConfirmOpen = true;
  }

  confirmDelete(): void {
    if (this.employeeToDelete?.id != null) {
      this.employeeService.delete(this.employeeToDelete.id).subscribe({
        next: () => {
          this.loadEmployees();
        },
        error: (err) => {
          console.error('Failed to delete employee', err);
        }
      });
    }
    this.isDeleteConfirmOpen = false;
    this.employeeToDelete = null;
  }

  cancelDelete(): void {
    this.isDeleteConfirmOpen = false;
    this.employeeToDelete = null;
  }

  onFilterChange(): void {
    let filtered = this.allData;
    if (this.filterCompany) {
      filtered = filtered.filter(e => e.company === this.filterCompany);
    }
    if (this.filterRegion) {
      filtered = filtered.filter(e => e.region === this.filterRegion);
    }
    if (this.filterRole) {
      filtered = filtered.filter(e => e.role === this.filterRole);
    }
    if (this.filterStatus !== '') {
      const active = this.filterStatus === 'active';
      filtered = filtered.filter(e => e.isActive === active);
    }
    this.rowData = filtered;
  }

  private loadEmployees(): void {
    this.employeeService.getAll().subscribe({
      next: (employees) => {
        this.allData = employees;
        this.onFilterChange();
      },
      error: (err) => {
        console.error('Failed to load employees', err);
      }
    });
  }
}

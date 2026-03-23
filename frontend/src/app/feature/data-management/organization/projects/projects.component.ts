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
import { Project, ProjectDto, ProjectStatus, ProjectRegion } from './project.model';
import { ProjectService } from './services/project.service';
import { AddProjectComponent } from './components/add-project/add-project.component';

ModuleRegistry.registerModules([
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule
]);

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, FormsModule, AgGridModule, AddProjectComponent],
  templateUrl: './projects.component.html',
  styleUrl: './projects.component.scss'
})
export class ProjectsComponent implements OnInit {
  @ViewChild(AddProjectComponent) addEditDialog!: AddProjectComponent;

  private gridApi!: GridApi<Project>;
  searchText = '';

  // Filters
  allData: Project[] = [];
  filterStatus: ProjectStatus | '' = '';
  filterRegion: ProjectRegion | '' = '';
  statuses: ProjectStatus[] = ['ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED', 'TO_BE_STARTED'];
  regions: ProjectRegion[] = ['US', 'EMEA', 'AU'];

  // Dialog state
  isDialogOpen = false;
  editingProject: Project | null = null;

  // Delete confirmation state
  isDeleteConfirmOpen = false;
  projectToDelete: Project | null = null;

  columnDefs: ColDef<Project>[] = [
    {
      headerName: 'PROJECT NAME',
      field: 'name',
      flex: 2,
      filter: 'agTextColumnFilter',
      sortable: true
    },
    {
      headerName: 'CODE',
      field: 'code',
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
      headerName: 'STATUS',
      field: 'status',
      flex: 1,
      sortable: true,
      cellRenderer: (params: any) => {
        const status = params.value as string;
        let bgColor: string;
        let textColor: string;
        switch (status) {
          case 'ACTIVE':
            bgColor = '#e8f5e9'; textColor = '#2e7d32'; break;
          case 'ON_HOLD':
            bgColor = '#fff3e0'; textColor = '#e65100'; break;
          case 'COMPLETED':
            bgColor = '#e3f2fd'; textColor = '#1565c0'; break;
          case 'CANCELLED':
            bgColor = '#ffebee'; textColor = '#c62828'; break;
          case 'TO_BE_STARTED':
            bgColor = '#f3e5f5'; textColor = '#7b1fa2'; break;
          default:
            bgColor = '#f5f5f5'; textColor = '#616161';
        }
        return `<span style="
          display:inline-block;
          padding:4px 12px;
          border-radius:4px;
          font-size:12px;
          font-weight:600;
          text-transform:uppercase;
          letter-spacing:0.5px;
          background:${bgColor};
          color:${textColor};
        ">${status}</span>`;
      }
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
      headerName: 'END DATE',
      field: 'endDate',
      flex: 1.2,
      sortable: true,
      cellRenderer: (params: any) => {
        if (!params.value) return '—';
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
          this.ngZone.run(() => this.onEditProject(params.data));
        });

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('action-btn', 'delete-btn');
        deleteBtn.title = 'Delete';
        deleteBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>`;
        deleteBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          this.ngZone.run(() => this.onDeleteProject(params.data));
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

  rowData: Project[] = [];

  constructor(private projectService: ProjectService, private ngZone: NgZone) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  get totalProjects(): number {
    return this.rowData.length;
  }

  get displayedCount(): number {
    if (!this.gridApi) return this.totalProjects;
    return this.gridApi.getDisplayedRowCount();
  }

  onGridReady(params: GridReadyEvent<Project>): void {
    this.gridApi = params.api;
    this.gridApi.sizeColumnsToFit();
  }

  onSearchChange(): void {
    this.gridApi?.setGridOption('quickFilterText', this.searchText);
  }

  onDownloadCsv(): void {
    this.gridApi?.exportDataAsCsv({
      fileName: 'projects.csv'
    });
  }

  onAddProject(): void {
    this.editingProject = null;
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm(null));
  }

  onEditProject(project: Project): void {
    this.editingProject = { ...project };
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm({ ...project }));
  }

  onCreateProject(dto: ProjectDto): void {
    this.projectService.create(dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingProject = null;
        this.loadProjects();
      },
      error: (err) => {
        console.error('Failed to create project', err);
      }
    });
  }

  onUpdateProject(event: { id: number; dto: ProjectDto }): void {
    this.projectService.update(event.id, event.dto).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.editingProject = null;
        this.loadProjects();
      },
      error: (err) => {
        console.error('Failed to update project', err);
      }
    });
  }

  onCloseDialog(): void {
    this.isDialogOpen = false;
    this.editingProject = null;
  }

  onDeleteProject(project: Project): void {
    this.projectToDelete = project;
    this.isDeleteConfirmOpen = true;
  }

  confirmDelete(): void {
    if (this.projectToDelete?.id != null) {
      this.projectService.delete(this.projectToDelete.id).subscribe({
        next: () => {
          this.loadProjects();
        },
        error: (err) => {
          console.error('Failed to delete project', err);
        }
      });
    }
    this.isDeleteConfirmOpen = false;
    this.projectToDelete = null;
  }

  cancelDelete(): void {
    this.isDeleteConfirmOpen = false;
    this.projectToDelete = null;
  }

  onFilterChange(): void {
    let filtered = this.allData;
    if (this.filterStatus) {
      filtered = filtered.filter(p => p.status === this.filterStatus);
    }
    if (this.filterRegion) {
      filtered = filtered.filter(p => p.region === this.filterRegion);
    }
    this.rowData = filtered;
  }

  private loadProjects(): void {
    this.projectService.getAll(undefined, undefined, undefined).subscribe({
      next: (projects) => {
        this.allData = projects;
        this.onFilterChange();
      },
      error: (err) => {
        console.error('Failed to load projects', err);
      }
    });
  }
}

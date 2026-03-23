import { Component, NgZone, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';
import {
  ColDef,
  ColGroupDef,
  GridReadyEvent,
  GridApi,
  ModuleRegistry,
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule,
} from 'ag-grid-community';
import { forkJoin } from 'rxjs';
import { AllocationGroup, QuarterAllocationRequest } from './allocation.model';
import { AllocationService } from './services/allocation.service';
import { ProjectService } from '../organization/projects/services/project.service';
import { Project } from '../organization/projects/project.model';
import { AddAllocationComponent } from './components/add-allocation/add-allocation.component';

ModuleRegistry.registerModules([
  ClientSideRowModelModule,
  TextFilterModule,
  ValidationModule,
  CsvExportModule,
  QuickFilterModule,
]);

interface WeekColumn {
  date: string;
  label: string;
  month: string;
}

interface MonthGroup {
  label: string;
  weeks: WeekColumn[];
}

@Component({
  selector: 'app-capacity-inputs',
  standalone: true,
  imports: [CommonModule, FormsModule, AgGridModule, AddAllocationComponent],
  templateUrl: './capacity-inputs.component.html',
  styleUrl: './capacity-inputs.component.scss',
})
export class CapacityInputsComponent implements OnInit {
  @ViewChild(AddAllocationComponent) addEditDialog!: AddAllocationComponent;

  private gridApi!: GridApi;
  searchText = '';
  quarterLabel = '';
  quarterOffset = 0;

  columnDefs: (ColDef | ColGroupDef)[] = [];
  defaultColDef: ColDef = { resizable: true, sortable: true };
  rowData: any[] = [];

  private projectMap = new Map<number, Project>();
  private monthGroups: MonthGroup[] = [];
  private allWeekDates: string[] = [];

  // Dialog state
  isDialogOpen = false;

  // Delete confirmation state
  isDeleteConfirmOpen = false;
  rowToDelete: any = null;

  // Grid context to pass component reference to cell renderers
  gridContext = { componentParent: this };

  constructor(
    private allocationService: AllocationService,
    private projectService: ProjectService,
    private ngZone: NgZone,
  ) {}

  ngOnInit(): void {
    this.generateQuarterWeeks();
    this.buildColumnDefs();
    this.loadData();
  }

  onGridReady(params: GridReadyEvent): void {
    this.gridApi = params.api;
    this.gridApi.sizeColumnsToFit();
  }

  onSearchChange(): void {
    this.gridApi?.setGridOption('quickFilterText', this.searchText);
  }

  onExport(): void {
    const allColumns = this.gridApi?.getColumns() || [];
    const exportKeys = allColumns
      .map(c => c.getColId())
      .filter(id => id !== '_actions');
    this.gridApi?.exportDataAsCsv({
      fileName: 'allocations.csv',
      columnKeys: exportKeys,
      skipColumnGroupHeaders: true,
    });
  }

  onPrevQuarter(): void {
    this.quarterOffset--;
    this.refreshQuarter();
  }

  onNextQuarter(): void {
    this.quarterOffset++;
    this.refreshQuarter();
  }

  private refreshQuarter(): void {
    this.generateQuarterWeeks();
    this.buildColumnDefs();
    this.loadData();
  }

  onAddAllocation(): void {
    this.isDialogOpen = true;
    setTimeout(() => this.addEditDialog?.resetForm());
  }

  onEditRow(row: any): void {
    this.isDialogOpen = true;
    setTimeout(() => {
      const weekData: { [date: string]: number } = {};
      for (const date of this.allWeekDates) {
        if (row[date] != null) {
          weekData[date] = row[date];
        }
      }
      this.addEditDialog?.populateForEdit(
        row.employeeId,
        row.projectId,
        weekData,
      );
    });
  }

  onSaveQuarter(request: QuarterAllocationRequest): void {
    this.allocationService.createQuarter(request).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.loadData();
      },
      error: (err) => console.error('Failed to save allocation', err),
    });
  }

  onUpdateQuarter(request: QuarterAllocationRequest): void {
    this.allocationService.updateQuarter(request).subscribe({
      next: () => {
        this.isDialogOpen = false;
        this.loadData();
      },
      error: (err) => console.error('Failed to update allocation', err),
    });
  }

  onCloseDialog(): void {
    this.isDialogOpen = false;
  }

  onDeleteRow(row: any): void {
    this.rowToDelete = row;
    this.isDeleteConfirmOpen = true;
  }

  confirmDelete(): void {
    if (this.rowToDelete) {
      this.allocationService
        .delete(this.rowToDelete.employeeId, this.rowToDelete.projectId)
        .subscribe({
          next: () => this.loadData(),
          error: (err) => console.error('Failed to delete allocation', err),
        });
    }
    this.isDeleteConfirmOpen = false;
    this.rowToDelete = null;
  }

  cancelDelete(): void {
    this.isDeleteConfirmOpen = false;
    this.rowToDelete = null;
  }

  private generateQuarterWeeks(): void {
    const now = new Date();
    const baseMonth = now.getMonth() + this.quarterOffset * 3;
    const targetDate = new Date(now.getFullYear(), baseMonth, 1);
    const currentMonth = targetDate.getMonth();
    const currentYear = targetDate.getFullYear();
    const quarterStartMonth = Math.floor(currentMonth / 3) * 3;
    const quarterNumber = Math.floor(quarterStartMonth / 3) + 1;

    const monthNames = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ];
    this.quarterLabel = `Q${quarterNumber} ${monthNames[quarterStartMonth]} - ${monthNames[quarterStartMonth + 2]} ${currentYear}`;

    const yearShort = String(currentYear).slice(-2);
    const groups: MonthGroup[] = [];
    const weekDates: string[] = [];

    for (let m = 0; m < 3; m++) {
      const monthIndex = quarterStartMonth + m;
      const monthLabel = `${monthNames[monthIndex]}'${yearShort}`;
      const weeks: WeekColumn[] = [];

      let date = new Date(currentYear, monthIndex, 1);

      // Move forward until it's Monday
      while (date.getDay() !== 1) {
        date.setDate(date.getDate() + 1);
      }

      while (date.getMonth() === monthIndex) {
        const iso = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        const dayLabel = `${date.getDate()}-${monthNames[monthIndex]}`;
        weeks.push({ date: iso, label: dayLabel, month: monthLabel });
        weekDates.push(iso);
        date.setDate(date.getDate() + 7);
      }

      groups.push({ label: monthLabel, weeks });
    }

    this.monthGroups = groups;
    this.allWeekDates = weekDates;
  }

  private buildColumnDefs(): void {
    const fixedCols: ColDef[] = [
      {
        headerName: 'NAME',
        field: 'employeeName',
        width: 150,
        pinned: 'left',
        filter: 'agTextColumnFilter',
      },
      {
        headerName: 'REGION',
        field: 'region',
        width: 90,
        pinned: 'left',
        filter: 'agTextColumnFilter',
      },
      {
        headerName: 'PROJECT',
        field: 'projectName',
        width: 140,
        pinned: 'left',
        filter: 'agTextColumnFilter',
      },
      {
        headerName: 'ROLE',
        field: 'role',
        width: 80,
        pinned: 'left',
        filter: 'agTextColumnFilter',
      },
    ];

    const weekGroups: ColGroupDef[] = this.monthGroups.map((group) => ({
      headerName: group.label,
      headerClass: 'month-group-header',
      children: group.weeks.map(
        (week) =>
          ({
            headerName: week.label,
            field: week.date,
            width: 80,
            headerClass: 'week-col-header',
            cellClass: 'week-cell',
            sortable: false,
            filter: false,
            valueFormatter: (params: any) => {
              if (params.value == null) return '';
              return params.value.toFixed(2);
            },
          }) as ColDef,
      ),
    }));

    const actionCol: ColDef = {
      headerName: '',
      field: '_actions',
      width: 90,
      maxWidth: 90,
      sortable: false,
      filter: false,
      pinned: 'right',
      cellRenderer: (params: any) => {
        const parent: CapacityInputsComponent = params.context.componentParent;

        const container = document.createElement('div');
        container.style.display = 'flex';
        container.style.gap = '2px';
        container.style.alignItems = 'center';
        container.style.height = '100%';

        const editBtn = document.createElement('button');
        editBtn.classList.add('grid-action-btn', 'grid-edit-btn');
        editBtn.title = 'Edit';
        editBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 000-1.41l-2.34-2.34a1 1 0 00-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>`;
        editBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          parent.ngZone.run(() => parent.onEditRow(params.data));
        });

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('grid-action-btn', 'grid-delete-btn');
        deleteBtn.title = 'Delete';
        deleteBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="currentColor"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>`;
        deleteBtn.addEventListener('click', (e) => {
          e.stopPropagation();
          parent.ngZone.run(() => parent.onDeleteRow(params.data));
        });

        container.appendChild(editBtn);
        container.appendChild(deleteBtn);
        return container;
      },
    };

    this.columnDefs = [...fixedCols, ...weekGroups, actionCol];
  }

  private loadData(): void {
    forkJoin({
      projects: this.projectService.getAll(),
      allocations: this.allocationService.getAll(),
    }).subscribe({
      next: ({ projects, allocations }) => {
        this.projectMap.clear();
        projects.forEach((p) => {
          if (p.id != null) this.projectMap.set(p.id, p);
        });
        this.rowData = this.buildRowData(allocations);
        setTimeout(() => {
          this.gridApi.setGridOption('columnDefs', this.columnDefs);
          this.gridApi.refreshCells({ force: true });
        });
      },
      error: (err) => console.error('Failed to load data', err),
    });
  }

  private buildRowData(groups: AllocationGroup[]): any[] {
    return groups.map((group) => {
      const proj = this.projectMap.get(group.projectId);
      const row: any = {
        region: group.region || '—',
        projectName: proj?.name || `Project #${group.projectId}`,
        employeeName: group.employeeName || `Employee #${group.employeeId}`,
        role: group.role || '—',
        employeeId: group.employeeId,
        projectId: group.projectId,
      };
      console.log(this.allWeekDates);
      console.log(group.allocations.map((a) => a.weekStartDate));

      for (const alloc of group.allocations) {
        row[alloc.weekStartDate] = alloc.allocationPct;
      }

      return row;
    });
  }
}

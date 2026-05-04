import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { QuarterAllocationRequest } from '../../allocation.model';
import { EmployeeService } from '../../../organization/employees/services/employee.service';
import { ProjectService } from '../../../organization/projects/services/project.service';
import { Employee } from '../../../organization/employees/employee.model';
import { Project } from '../../../organization/projects/project.model';

interface WeekColumn {
  date: string;       // ISO date string (YYYY-MM-DD)
  label: string;      // e.g. "5-JAN"
  month: string;      // e.g. "JAN'26"
}

interface MonthGroup {
  label: string;
  weeks: WeekColumn[];
}

@Component({
  selector: 'app-add-allocation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-allocation.component.html',
  styleUrl: './add-allocation.component.scss'
})
export class AddAllocationComponent implements OnInit, OnChanges {
  @Input() isOpen = false;
  @Input() quarterOffset = 0;
  @Output() saveQuarter = new EventEmitter<QuarterAllocationRequest>();
  @Output() updateQuarter = new EventEmitter<QuarterAllocationRequest>();
  @Output() close = new EventEmitter<void>();

  form!: FormGroup;
  weekValues: { [date: string]: number } = {};

  employees: Employee[] = [];
  projects: Project[] = [];

  selectedEmployee: Employee | null = null;

  monthGroups: MonthGroup[] = [];
  quarterLabel = '';

  isEditMode = false;
  validationError = '';

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadDropdowns();
    this.generateQuarterWeeks();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['quarterOffset'] && !changes['quarterOffset'].firstChange) {
      this.generateQuarterWeeks();
    }
  }

  private loadDropdowns(): void {
    this.employeeService.getAll().subscribe({
      next: (data) => this.employees = data,
      error: (err) => console.error('Failed to load employees', err)
    });
    this.projectService.getAll().subscribe({
      next: (data) => this.projects = data,
      error: (err) => console.error('Failed to load projects', err)
    });
  }

  private buildForm(): void {
    this.form = this.fb.group({
      employeeId: ['', Validators.required],
      projectId: ['', Validators.required]
    });
  }

  onEmployeeChange(): void {
    const empId = Number(this.form.get('employeeId')?.value);
    this.selectedEmployee = this.employees.find(e => e.id === empId) || null;
  }

  private generateQuarterWeeks(): void {
    const now = new Date();
    const baseMonth = now.getMonth() + this.quarterOffset * 3;
    const targetDate = new Date(now.getFullYear(), baseMonth, 1);
    const currentMonth = targetDate.getMonth();
    const currentYear = targetDate.getFullYear();

    const quarterStartMonth = Math.floor(currentMonth / 3) * 3;
    const quarterNumber = Math.floor(quarterStartMonth / 3) + 1;

    const monthNames = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
    const startMonthName = monthNames[quarterStartMonth];
    const endMonthName = monthNames[quarterStartMonth + 2];
    const yearShort = String(currentYear).slice(-2);
    this.quarterLabel = `Q${quarterNumber} ${startMonthName} - ${endMonthName} ${currentYear}`;

    const groups: MonthGroup[] = [];
    this.weekValues = {};

    for (let m = 0; m < 3; m++) {
      const monthIndex = quarterStartMonth + m;
      const monthLabel = `${monthNames[monthIndex]}'${yearShort}`;
      const weeks: WeekColumn[] = [];

      let date = new Date(currentYear, monthIndex, 1);
      const dayOfWeek = date.getDay();
      if (dayOfWeek !== 1) {
        const daysUntilMon = dayOfWeek === 0 ? 1 : (8 - dayOfWeek);
        date.setDate(date.getDate() + daysUntilMon);
      }

      while (date.getMonth() === monthIndex) {
        const iso = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        const dayLabel = `${date.getDate()}-${monthNames[monthIndex]}`;
        weeks.push({ date: iso, label: dayLabel, month: monthLabel });
        this.weekValues[iso] = 0;
        date.setDate(date.getDate() + 7);
      }

      groups.push({ label: monthLabel, weeks });
    }

    this.monthGroups = groups;
  }

  onWeekValueChange(date: string, event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = parseFloat(input.value);

    if (isNaN(value) || value < 0) {
      value = 0;
      input.value = '0';
      this.validationError = 'Value must be between 0.0 and 1.0';
    } else if (value > 1) {
      value = 1;
      input.value = '1';
      this.validationError = 'Value cannot exceed 1.0 — maximum allocation per resource is 100%.';
    } else {
      this.validationError = '';
    }

    this.weekValues[date] = value;
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      return;
    }

    const allocations = Object.entries(this.weekValues)
      .filter(([, pct]) => pct > 0)
      .map(([weekStartDate, allocationPct]) => ({ weekStartDate, allocationPct }));

    if (allocations.length === 0) {
      return;
    }

    const request: QuarterAllocationRequest = {
      employeeId: Number(this.form.get('employeeId')?.value),
      projectId: Number(this.form.get('projectId')?.value),
      allocations
    };

    if (this.isEditMode) {
      this.updateQuarter.emit(request);
    } else {
      this.saveQuarter.emit(request);
    }
  }

  onClose(): void {
    this.close.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.onClose();
    }
  }

  resetForm(): void {
    this.isEditMode = false;
    this.validationError = '';
    this.buildForm();
    this.selectedEmployee = null;
    this.generateQuarterWeeks();
  }

  populateForEdit(employeeId: number, projectId: number, weekData: { [date: string]: number }): void {
    this.isEditMode = true;
    this.generateQuarterWeeks();

    this.form.patchValue({
      employeeId: String(employeeId),
      projectId: String(projectId)
    });

    // Pre-populate week values
    for (const [date, value] of Object.entries(weekData)) {
      if (this.weekValues.hasOwnProperty(date)) {
        this.weekValues[date] = value;
      }
    }

    // Set selected employee for region/role display
    const empId = employeeId;
    this.selectedEmployee = this.employees.find(e => e.id === empId) || null;
  }
}

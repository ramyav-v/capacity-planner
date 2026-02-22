import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Employee, EmployeeDto, EmployeeRole, EmployeeCompany, EmployeeRegion } from '../employee.model';

@Component({
  selector: 'app-add-edit-employee',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-edit-employee.component.html',
  styleUrl: './add-edit-employee.component.scss'
})
export class AddEditEmployeeComponent implements OnInit {
  @Input() employee: Employee | null = null;
  @Input() isOpen = false;
  @Output() save = new EventEmitter<EmployeeDto>();
  @Output() update = new EventEmitter<{ id: number; dto: EmployeeDto }>();
  @Output() close = new EventEmitter<void>();

  form!: FormGroup;

  roles: EmployeeRole[] = ['PM', 'TL', 'Dev', 'QA', 'Support'];
  companies: EmployeeCompany[] = ['Zinier', 'Sankey', 'TopGrep'];
  regions: EmployeeRegion[] = ['US', 'EMEA'];

  get isEditMode(): boolean {
    return !!this.employee;
  }

  get title(): string {
    return this.isEditMode ? 'Edit Employee' : 'Add New Employee';
  }

  get subtitle(): string {
    return this.isEditMode
      ? 'Update the employee information below'
      : 'Fill in the employee information below';
  }

  get submitLabel(): string {
    return this.isEditMode ? 'Update Employee' : 'Add Employee';
  }

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      name: [this.employee?.name || '', Validators.required],
      email: [this.employee?.email || '', [Validators.required, Validators.email]],
      role: [this.employee?.role || '', Validators.required],
      company: [this.employee?.company || '', Validators.required],
      region: [this.employee?.region || '', Validators.required],
      startDate: [this.employee?.startDate || '', Validators.required],
      endDate: [this.employee?.endDate || '']
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const dto: EmployeeDto = {
        ...this.form.value
      };
      if (!dto.endDate) {
        dto.endDate = null;
      }

      if (this.isEditMode && this.employee?.id != null) {
        this.update.emit({ id: this.employee.id, dto });
      } else {
        this.save.emit(dto);
      }
    } else {
      this.form.markAllAsTouched();
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

  resetForm(employee: Employee | null): void {
    this.employee = employee;
    this.buildForm();
  }
}

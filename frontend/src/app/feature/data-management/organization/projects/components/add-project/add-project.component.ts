import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Project, ProjectDto, ProjectRegion, ProjectStatus } from '../../project.model';

@Component({
  selector: 'app-add-project',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-project.component.html',
  styleUrl: './add-project.component.scss'
})
export class AddProjectComponent implements OnInit {
  @Input() project: Project | null = null;
  @Input() isOpen = false;
  @Output() save = new EventEmitter<ProjectDto>();
  @Output() update = new EventEmitter<{ id: number; dto: ProjectDto }>();
  @Output() close = new EventEmitter<void>();

  form!: FormGroup;

  regions: ProjectRegion[] = ['US', 'EMEA'];
  statuses: ProjectStatus[] = ['ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];

  get isEditMode(): boolean {
    return !!this.project;
  }

  get title(): string {
    return this.isEditMode ? 'Edit Project' : 'Add New Project';
  }

  get subtitle(): string {
    return this.isEditMode
      ? 'Update the project details below'
      : 'Fill in the project details below';
  }

  get submitLabel(): string {
    return this.isEditMode ? 'Update Project' : 'Add Project';
  }

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      name: [this.project?.name || '', Validators.required],
      code: [this.project?.code || '', Validators.required],
      region: [this.project?.region || 'US', Validators.required],
      status: [this.project?.status || 'ACTIVE', Validators.required],
      startDate: [this.project?.startDate || '', Validators.required],
      endDate: [this.project?.endDate || '']
    });

    if (this.isEditMode) {
      this.form.get('code')?.disable();
    }
  }

  onSubmit(): void {
    if (this.form.valid) {
      const dto: ProjectDto = {
        ...this.form.getRawValue()
      };
      if (!dto.endDate) {
        dto.endDate = null;
      }

      if (this.isEditMode && this.project?.id != null) {
        this.update.emit({ id: this.project.id, dto });
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

  resetForm(project: Project | null): void {
    this.project = project;
    this.buildForm();
  }
}

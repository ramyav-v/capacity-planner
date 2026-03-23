import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppUser, AppUserDto, UserRole } from '../../../user.model';

@Component({
  selector: 'app-add-edit-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-edit-user.component.html',
  styleUrl: './add-edit-user.component.scss'
})
export class AddEditUserComponent implements OnInit {
  @Input() user: AppUser | null = null;
  @Input() isOpen = false;
  @Output() save = new EventEmitter<AppUserDto>();
  @Output() update = new EventEmitter<{ id: number; dto: AppUserDto }>();
  @Output() close = new EventEmitter<void>();

  form!: FormGroup;

  roles: UserRole[] = ['SUPER_ADMIN', 'ADMIN', 'VIEWER'];

  get isEditMode(): boolean {
    return !!this.user;
  }

  get title(): string {
    return this.isEditMode ? 'Edit User' : 'Add New User';
  }

  get subtitle(): string {
    return this.isEditMode
      ? 'Update the user information below'
      : 'Fill in the user information below';
  }

  get submitLabel(): string {
    return this.isEditMode ? 'Update User' : 'Add User';
  }

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.buildForm();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      username: [this.user?.username || '', Validators.required],
      password: [''],
      email: [this.user?.email || '', [Validators.required, Validators.email]],
      fullName: [this.user?.fullName || '', Validators.required],
      userRole: [this.user?.userRole || '', Validators.required]
    });

    if (this.isEditMode) {
      this.form.get('username')?.disable();
    }
  }

  onSubmit(): void {
    if (this.form.valid) {
      const raw = this.form.getRawValue();
      const dto: AppUserDto = {
        username: raw.username,
        password: raw.password,
        email: raw.email || null,
        fullName: raw.fullName,
        userRole: raw.userRole,
        employeeId: null
      };

      if (this.isEditMode && this.user?.id != null) {
        this.update.emit({ id: this.user.id, dto });
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

  resetForm(user: AppUser | null): void {
    this.user = user;
    this.buildForm();
  }
}

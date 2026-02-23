import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { User } from '../../../core/models/user';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../core/services/user.service';

export interface UserDialogData {
  user?: User; // if present → edit, else → create
}

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  templateUrl: './user-dialog.html',
  styleUrls: ['./user-dialog.scss'],
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    ReactiveFormsModule,
  ],
})
export class UserDialog {
  userForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<UserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: UserDialogData,
    private userService: UserService,
  ) {
    this.userForm = this.fb.group({
      username: [data.user?.username || '', Validators.required],
      password: ['', data.user ? [] : Validators.required],
      role: [data.user?.role || 'USER', Validators.required],
    });
  }

  private saveUser(user: User) {
    if (user.id != null) {
      return this.userService.updateUser(user);
    } else {
      return this.userService.createUser(user);
    }
  }

  save(): void {
    if (!this.userForm.valid) {
      this.userForm.markAllAsTouched(); // show validation errors
      return;
    }

    const user: User = { ...this.data.user, ...this.userForm.value };

    // If editing and password is empty → keep old password
    if (this.data.user && !this.userForm.value.password) {
      user.password = this.data.user.password;
    }

    this.saveUser(user).subscribe({
      next: (savedUser) => this.dialogRef.close(savedUser),
      error: (err) => {
        if (err.status === 409) {
          // Username already exists
          this.userForm.get('username')?.setErrors({ exists: true });
        } else {
          console.error('Error saving user:', err);
        }
      },
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }
}

import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Owner } from 'src/app/dto/owner';
import { OwnerService } from 'src/app/service/owner.service';

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss']
})
export class OwnerCreateComponent {
  owner: Owner = {
    firstName: '',
    lastName: ''
  };

  constructor(
    private service: OwnerService,
    private notification: ToastrService,
    private router: Router,
  ) {

  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {
      if (this.owner.email === '') {
        delete this.owner.email;
      }
      const observable = this.service.create(this.owner);
      observable.subscribe({
        next: () => {
          this.notification.success(`Owner ${`${this.owner.firstName} ${this.owner.lastName}`} successfully created.`);
          this.router.navigate(['/owners']);
        },
        error: error => {
          this.notification.error(`Failed to create owner. ${error.error.errors}`);
        }
      });
    }
  }
}

import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Owner } from 'src/app/dto/owner';
import { OwnerService } from 'src/app/service/owner.service';

@Component({
  selector: 'app-owner',
  templateUrl: './owner.component.html',
  styleUrls: ['./owner.component.scss']
})
export class OwnerComponent implements OnInit {
  owners: Owner[] = [];

  constructor(
    private ownerService: OwnerService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadOwners();
  }

  reloadOwners() {
    this.ownerService.search().subscribe({
      next: data => {
        this.owners = data;
      },
      error: error => {
        console.error('Error fetching owners', error);
        const errorMessage = error.status === 0
          ? 'Our Server is currently not available'
          : error.message.message;
        this.notification.error(errorMessage, 'Could Not Fetch Owners');
      }
    });
  }
}

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { debounceTime } from 'rxjs';
import { PersistedHorse } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';

@Component({
  selector: 'app-horse-familytree',
  templateUrl: './horse-familytree.component.html',
  styleUrls: ['./horse-familytree.component.scss']
})
export class HorseFamilytreeComponent implements OnInit {
  familytree: Map<number, PersistedHorse> = new Map();
  rootHorse: PersistedHorse | undefined;

  familyDepth: number | null = 100;

  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.initQueryParams();
    this.loadFamilyTree();
  }

  initQueryParams() {
    this.activatedRoute.queryParams.subscribe(queryParams => {
      this.familyDepth = queryParams.limit ?? this.familyDepth;
    });
  }

  initRootHorse() {
    this.activatedRoute.paramMap.subscribe(params => {
      const horseId = params.get('id');
      if (horseId) {
        this.rootHorse = this.familytree.get(Number(horseId));
      }
    });
  }

  loadFamilyTree() {
    this.router.navigate(
      [],
      {
        relativeTo: this.activatedRoute,
        queryParams: {
          limit: this.familyDepth
        },
        queryParamsHandling: 'merge', // remove to replace all query params by provided
      });

    this.activatedRoute.paramMap.subscribe(params => {
      const horseId = params.get('id');

      if (horseId) {
        this.service.getFamilyTree({ id: horseId, limit: Number(this.familyDepth) })
          .pipe(debounceTime(300))
          .subscribe({
            next: data => {
              const horseMap = new Map();
              for (const horse of data) {
                horseMap.set(horse.id, horse);
              }
              this.familytree = horseMap;
              this.initRootHorse();
            },
            error: error => {
              console.error('Error fetching family tree of horse ' + horseId, error);
              const errorMessage = error.status === 0
                ? 'Server is not reachable'
                : error.status === 404 ? `Horse does not exist` : error.message.message;
              if (error.status === 409) {
                this.notification.error(errorMessage, `Requested Family Tree does not exist`);
                this.router.navigate(['horses']);
              } else {
                this.notification.error(errorMessage, `Error retrieving Familytree ${error.error.errors}`);
                this.router.navigate(['horses']);
              }
            }
          });
      }
    });
  }

  retrieveParent(horseId: number): PersistedHorse | undefined {
    return this.familytree.get(horseId);
  }
}

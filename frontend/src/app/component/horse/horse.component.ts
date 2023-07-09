import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { HorseService } from 'src/app/service/horse.service';
import { Horse, HorseSearch, PersistedHorse } from '../../dto/horse';
import { Owner } from '../../dto/owner';
import { OwnerService } from 'src/app/service/owner.service';
import { debounceTime, of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  searchParameters: HorseSearch = {};
  horses: Horse[] = [];
  bannerError: string | null = null;

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private notification: ToastrService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.initSearchparamsFromQueryParams();
  }

  initSearchparamsFromQueryParams() {
    this.activatedRoute.queryParams.subscribe(params => {
      const queryParameters = structuredClone(params);
      const name = queryParameters.name;
      const description = queryParameters.description;
      const bornBefore = queryParameters.bornBefore;
      const sex = queryParameters.sex;
      const ownerName = queryParameters.ownerName;
      const excludeHorseById = queryParameters.excludeHorseById;
      const limit = queryParameters.limit;

      if (name) {
        this.searchParameters.name = name;
      }

      if (description) {
        this.searchParameters.description = description;
      }

      if (bornBefore) {
        this.searchParameters.bornBefore = bornBefore;
      }

      if (sex) {
        this.searchParameters.sex = sex;
      }

      if (ownerName) {
        this.searchParameters.ownerName = ownerName;
      }

      if (excludeHorseById) {
        this.searchParameters.excludeHorseById = excludeHorseById;
      }

      if (limit) {
        this.searchParameters.limit = limit;
      }
      this.reloadHorses();
    }
    );
  }

  replaceEmptyStringValuePropertiesWithNull(obj: any): object {
    for (const [key, value] of Object.entries(obj)) {
      if (value === '') {
        obj[key] = null;
      }
    }
    return obj;
  }

  reloadHorses() {
    this.service.search(this.searchParameters)
      .pipe(debounceTime(300))
      .subscribe({
        next: data => {
          this.horses = data;

          //set query parameters
          const queryParams = this.replaceEmptyStringValuePropertiesWithNull(this.searchParameters);
          this.router.navigate(
            [],
            {
              relativeTo: this.activatedRoute,
              queryParams,
              queryParamsHandling: 'merge', // remove to replace all query params by provided
            });
        },
        error: error => {
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Server is not reachable'
            : error.message.message;
          if (error.status === 422) {
            this.notification.error(errorMessage, `Horse search failed: ${error.error.errors}`);
          } else {
            this.notification.error(errorMessage, 'Horse Search failed');
          }
        }
      });
  }

  handleDelete(id: number) {
    this.service.deleteById(id).subscribe({
      next: () => {
        this.reloadHorses();
        this.notification.success(`Successfully deleted horse`);
      },
      error: error => {
        console.error('Error deleting horse', error);
        const errorMessage = error.status === 0
          ? 'Server is not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Error deleting this horse it may not exist');
      }
    });
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.search(input, 5);

  horseSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.search({
      name: input
    });

  formatHorseName(horse: Horse | null | undefined | string): string {
    if (horse === null || horse === undefined) {
      return '';
    } else if (typeof horse === 'string') {
      return `${horse}`;
    }
    else {
      const horseObj = horse as Horse;
      if (horseObj.name) {
        return horseObj.name;
      }
    }
    return '';
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  formatOwnerName(owner: Owner | null | undefined | string): string {
    if (!owner) {
      return '';
    } else if (typeof owner === 'string') {
      return `${owner}`;
    }
    else if (owner.firstName && owner.lastName) {
      return `${owner.firstName} ${owner.lastName}`;
    }
    return '';
  }
}

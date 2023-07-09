import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { CreateUpdateHorseDto, Horse } from 'src/app/dto/horse';
import { Owner } from 'src/app/dto/owner';
import { Sex } from 'src/app/dto/sex';
import { HorseService } from 'src/app/service/horse.service';
import { OwnerService } from 'src/app/service/owner.service';



export enum HorseCreateEditMode {
  create,
  edit,
  view
};

@Component({
  selector: 'app-horse-create-edit-view',
  templateUrl: './horse-create-edit-view.component.html',
  styleUrls: ['./horse-create-edit-view.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: CreateUpdateHorseDto = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
    femaleParent: null,
    maleParent: null,
  };

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse';
      case HorseCreateEditMode.view:
        return 'Details of Horse';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Save';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.search(input, 5);

  fatherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.search({
      sex: Sex.male,
      limit: 5,
      excludeHorseById: this.horse.id,
      bornBefore: this.horse.dateOfBirth ?? undefined
    });

  motherSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.search({
      sex: Sex.female,
      limit: 5,
      excludeHorseById: this.horse.id,
      bornBefore: this.horse.dateOfBirth ?? undefined
    });

  loadCurrentTime() {
    const now = new Date(Date.now());
    const year = now.getFullYear();
    const month = `${now.getMonth() + 1}`.toString().padStart(2, '0');
    const day = now.getDate().toString().padStart(2, '0');
    this.horse.dateOfBirth = `${year}-${month}-${day}`;
  }

  ngOnInit(): void {
    this.loadCurrentTime();
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    this.route.paramMap.subscribe(params => {
      const horseId = params.get('id');

      if (horseId) {
        this.service.getById(Number(horseId)).subscribe({
          next: data => {
            this.horse = {
              ...data,
              femaleParent: null,
              maleParent: null,
            };
            if (data.femaleParentId) {
              this.service.getById(data.femaleParentId).subscribe(
                {
                  next: femaleParent => {
                    this.horse.femaleParent = femaleParent;
                  },
                  error: error => {
                    console.error('Error fetching female parent horse', error);
                    const errorMessage = error.status === 0
                      ? 'Server not reachable'
                      : error.message.message;
                    this.notification.error(errorMessage, 'Could not find this Horse, it may not exist.');
                  }
                }
              );
            }
            if (data.maleParentId) {
              this.service.getById(data.maleParentId).subscribe(
                {
                  next: maleParent => {
                    this.horse.maleParent = maleParent;
                  },
                  error: error => {
                    console.error('Error fetching male parent horse', error);
                    const errorMessage = error.status === 0
                      ? 'Server not reachable'
                      : error.message.message;
                    this.notification.error(errorMessage, 'Could not find this Horse, maybe it does not exist.');
                  }
                }
              );
            }
          },
          error: error => {
            console.error('Error fetching horses', error);
            const errorMessage = error.status === 0
              ? 'Server not reachable'
              : error.message.message;
            this.notification.error(errorMessage, 'Could not find this Horse, maybe it does not exist.');
            this.router.navigate(['horses']);
          }
        }
        );
      }
    });
  }

  public parseHorseFromOberservable(id: number): Horse | void {
    this.service.getById(id).subscribe({
      next: data => data,
      error: error => {
        console.error('Error fetching horse', error);
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Could not find this Horse, maybe it does not exist.');
      }
    });
  }

  public onDelete() {
    const horseId = this.route.snapshot.paramMap.get('id');
    this.service.deleteById(Number(horseId)).subscribe({
      next: () => {
        this.notification.success(`Successfully deleted horse ${this.horse.name}`);
        this.router.navigate(['/horses']);
      },
      error: error => {
        const errorMessage = error.status === 0
          ? 'Server not reachable'
          : error.message.message;
        this.notification.error(errorMessage, 'Error deleting this horse it may not exist');
        this.router.navigate(['/horses']);
      }
    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatHorseName(horse: Horse | null | undefined): string {
    return (horse == null)
      ? ''
      : `${horse.name}`;
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: () => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: error => {
          console.error('Error creating horse', error);
          let modeStatus = '';
          switch (this.mode) {
            case (HorseCreateEditMode.create):
              modeStatus = 'create';
              break;
            case (HorseCreateEditMode.edit):
              modeStatus = 'update';
              break;
          }

          if (error.status === 422) {
            this.notification.error(`Failed to ${modeStatus ?? 'operate'} horse. ${error.error.errors}`);
          } else {
            this.notification.error(`Failed to ${modeStatus ?? 'operate'} horse.`);
          }
        }
      });
    }
  }
}

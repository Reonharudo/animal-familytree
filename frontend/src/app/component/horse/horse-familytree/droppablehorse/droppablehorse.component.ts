import { Component, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { PersistedHorse } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';

@Component({
  selector: 'app-droppablehorse',
  templateUrl: './droppablehorse.component.html',
  styleUrls: ['./droppablehorse.component.scss'],
})
export class DroppablehorseComponent implements OnChanges {

  @Input() horse: PersistedHorse | undefined;
  @Input() familyTree?: Map<number, PersistedHorse>;

  @Input() childrenMarginInRem = 0;

  @Input() showParents = true;

  femaleHorseParent: PersistedHorse | undefined;
  maleHorseParent: PersistedHorse | undefined;

  constructor(
    private service: HorseService,
    private activatedRoute: ActivatedRoute,
    private notification: ToastrService,
  ) { }

  toggleShowParents() {
    this.showParents = !this.showParents;
  }

  handleDelete(id: number) {
    if (this.familyTree && this.horse) {
      const clonedFamilyTree = new Map(this.familyTree);
      clonedFamilyTree.delete(id);
      this.familyTree = clonedFamilyTree;

      if (!this.familyTree?.get(this.horse.id)) {
        this.service.deleteById(id).subscribe({
          next: () => {
            this.horse = undefined;
            this.femaleHorseParent = undefined;
            this.maleHorseParent = undefined;
          },
          error: error => {
            console.error('Error deleting horse', error);
            const errorMessage = error.status === 0
              ? 'Server is not reachable'
              : error.message.message;
            this.notification.error(errorMessage, `Error deleting this horse it may not exist`);
          }
        });
      }
    } else {
      throw new Error('handleDelete can not be invoked yet as family tree does not exist');
    }
  }

  ngOnChanges(): void {
    if (this.horse === undefined) { //means we are at root horse
      this.activatedRoute.paramMap.subscribe(params => {
        const horseId = params.get('id');
        if (Number(horseId)) {
          if (this.familyTree) {
            this.horse = this.familyTree.get(Number(horseId));
          }
        }
      });
    }
    this.initParents();
  }

  initParents() {
    if (this.horse && this.familyTree) {
      const femaleParentId = this.horse.femaleParentId;
      const maleParentId = this.horse.maleParentId;

      if (femaleParentId) {
        this.femaleHorseParent = this.familyTree.get(femaleParentId);
      }

      if (maleParentId) {
        this.maleHorseParent = this.familyTree.get(maleParentId);
      }
    }
  }
}

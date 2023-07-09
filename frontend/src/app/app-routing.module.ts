import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HorseCreateEditComponent, HorseCreateEditMode } from './component/horse/horse-create-edit-view/horse-create-edit-view.component';
import { HorseComponent } from './component/horse/horse.component';
import { OwnerCreateComponent } from './component/owner/owner-create/owner-create.component';
import { OwnerComponent } from './component/owner/owner.component';
import { HorseFamilytreeComponent } from './component/horse/horse-familytree/horse-familytree.component';

const routes: Routes = [
  { path: '', redirectTo: 'horses', pathMatch: 'full' },
  {
    path: 'horses', children: [
      { path: '', component: HorseComponent },
      { path: ':id/familytree', component: HorseFamilytreeComponent },
      { path: 'create', component: HorseCreateEditComponent, data: { mode: HorseCreateEditMode.create } },
      { path: ':id/view', component: HorseCreateEditComponent, data: { mode: HorseCreateEditMode.view } },
      { path: ':id/edit', component: HorseCreateEditComponent, data: { mode: HorseCreateEditMode.edit } },
    ]
  },
  {
    path: 'owners', children: [
      { path: '', component: OwnerComponent },
      { path: 'create', component: OwnerCreateComponent },
    ]
  },
  { path: '**', redirectTo: 'horses' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

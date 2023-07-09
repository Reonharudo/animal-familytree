import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AutocompleteComponent } from './component/autocomplete/autocomplete.component';
import { HeaderComponent } from './component/header/header.component';
import { HorseCreateEditComponent } from './component/horse/horse-create-edit-view/horse-create-edit-view.component';
import { HorseComponent } from './component/horse/horse.component';
import { OwnerCreateComponent } from './component/owner/owner-create/owner-create.component';
import { OwnerComponent } from './component/owner/owner.component';
import { HorseFamilytreeComponent } from './component/horse/horse-familytree/horse-familytree.component';
import { DroppablehorseComponent } from './component/horse/horse-familytree/droppablehorse/droppablehorse.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HorseComponent,
    HorseCreateEditComponent,
    AutocompleteComponent,
    OwnerCreateComponent,
    OwnerComponent,
    HorseFamilytreeComponent,
    DroppablehorseComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    // Needed for Toastr
    BrowserAnimationsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

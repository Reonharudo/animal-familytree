import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CreateUpdateHorseDto, Horse, HorseAncestrySearch, HorseCreateDto, HorseSearch, PersistedHorse } from '../dto/horse';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAll(): Observable<PersistedHorse[]> {
    return this.http.get<PersistedHorse[]>(baseUri);
  }

  /**
   * Search horses stored in the system
   *
   * @param searchParameters parameters to filter requesting horses
   * @returns observable list of found horses with given search parameters
   */
  search(searchParameters: HorseSearch): Observable<PersistedHorse[]> {
    let params = new HttpParams();
    if (searchParameters.name) {
      params = params.set('name', searchParameters.name);
    }
    if (searchParameters.description) {
      params = params.set('description', searchParameters.description);
    }
    if (searchParameters.bornBefore) {
      params = params.set('bornBefore', searchParameters.bornBefore.toString());
    }
    if (searchParameters.bornAfter) {
      params = params.set('bornAfter', searchParameters.bornAfter.toString());
    }
    if (searchParameters.sex) {
      params = params.set('sex', searchParameters.sex.toString().toUpperCase());
    }
    if (searchParameters.excludeHorseById) {
      params = params.set('excludeHorseById', searchParameters.excludeHorseById);
    }
    if (searchParameters.ownerName) {
      params = params.set('ownerName', searchParameters.ownerName);
    }
    if (searchParameters.limit) {
      params = params.set('limit', searchParameters.limit);
    }

    return this.http.get<PersistedHorse[]>(
      baseUri,
      { params }
    );
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: CreateUpdateHorseDto): Observable<PersistedHorse> {
    const clonedHorse: HorseCreateDto = {
      ...horse,
      femaleParentId: horse.femaleParent?.id,
      maleParentId: horse.maleParent?.id,
      ownerId: horse.owner?.id
    };
    return this.http.post<PersistedHorse>(
      baseUri,
      clonedHorse
    );
  }

  /**
   * Deletes horse by id
   *
   * @param id of horse
   */
  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(
      `${baseUri}/${id}`
    );
  }

  /**
   * Get horse by id
   *
   * @param id of horse
   * @returns observable horse
   */
  getById(id: number): Observable<PersistedHorse> {
    return this.http.get<PersistedHorse>(
      `${baseUri}/${id}`
    );
  }

  /**
   * Get family tree of given searchParams
   *
   * @param searchParams search parameters
   * @returns observable list of horses
   */
  getFamilyTree(searchParams: HorseAncestrySearch): Observable<PersistedHorse[]> {
    let params = new HttpParams();

    if (searchParams.limit !== null && searchParams.limit !== undefined) {
      params = params.set('limit', searchParams.limit + 1);
    }
    return this.http.get<PersistedHorse[]>(
      `${baseUri}/${searchParams.id}/ancestry`,
      { params }
    );
  }

  /**
   * Update horse by given dto
   *
   * @param newHorse data of the updated horse
   * @returns observable horse with updated values
   */
  update(newHorse: CreateUpdateHorseDto): Observable<PersistedHorse> {
    const clonedHorse: Horse = {
      ...newHorse,
      femaleParentId: newHorse.femaleParent === null ? undefined : newHorse.femaleParent?.id,
      maleParentId: newHorse.maleParent === null ? undefined : newHorse.maleParent?.id
    };

    return this.http.put<PersistedHorse>(
      `${baseUri}/${clonedHorse.id}`,
      clonedHorse
    );
  }

}

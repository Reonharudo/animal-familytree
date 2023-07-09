import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Owner } from '../dto/owner';

const baseUri = environment.backendUrl + '/owners';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Search Owners
   *
   * @param name which is aggregated from firstname and lastname
   * @param limitTo number of max owners that should be returned
   * @returns observable list of owners
   */
  public search(name?: string, limitTo?: number): Observable<Owner[]> {
    const params = new HttpParams();
    if (name && limitTo) {
      params.set('name', name);
      params.set('maxAmount', limitTo);
    }
    return this.http.get<Owner[]>(baseUri, { params });
  }

  /**
   * Create Owner
   *
   * @param owner the owner to be created
   * @returns observable owner from data store
   */
  public create(owner: Owner): Observable<Owner> {
    return this.http.post<Owner>(baseUri, owner);
  }
}

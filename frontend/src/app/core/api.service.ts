import { Injectable, inject } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SearchResponse } from "../features/search/search.models";

@Injectable({ providedIn: "root" })
export class ApiService {
  private http = inject(HttpClient);
  private readonly API_BASE_URL = "http://localhost:8080";

  search(checkIn: string, checkOut: string, guests: number) {
    const params = new HttpParams()
      .set("checkIn", checkIn)
      .set("checkOut", checkOut)
      .set("guests", guests);
    return this.http.get<SearchResponse>(`${this.API_BASE_URL}/api/search`, {
      params,
    });
  }
}

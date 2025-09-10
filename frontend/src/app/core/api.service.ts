import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";

export interface SearchResult {
  roomId: number;
  type: string;
  capacity: number;
  price: number;
}
export interface SearchResponse {
  checkIn: string;
  checkOut: string;
  guests: number;
  results: SearchResult[];
}

@Injectable({ providedIn: "root" })
export class ApiService {
  constructor(private http: HttpClient) {}
  health() {
    return this.http.get<{ status: string }>("/api/health");
  }
  search(
    checkIn: string,
    checkOut: string,
    guests: number
  ): Observable<SearchResponse> {
    const params = new HttpParams()
      .set("checkIn", checkIn)
      .set("checkOut", checkOut)
      .set("guests", guests.toString());
    return this.http.get<SearchResponse>("/api/search", { params });
  }
}

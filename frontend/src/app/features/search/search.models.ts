export interface SearchResult {
  roomId: number;
  code: string;
  type: string;
  capacity: number;
  price: number;
  amenities: Record<string, unknown>;
}

export interface SearchResponse {
  checkIn: string; // YYYY-MM-DD
  checkOut: string; // YYYY-MM-DD
  guests: number;
  results: SearchResult[];
}

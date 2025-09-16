import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";

// Angular Material
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule } from "@angular/material/table";
import { MatChipsModule } from "@angular/material/chips";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";

import { ApiService } from "../../core/api.service";
import { SearchResult, SearchResponse } from "./search.models";

@Component({
  selector: "app-search",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: "./search.html",
  styleUrl: "./search.scss",
})
export class SearchComponent {
  // Reactive Form
  form = new FormGroup({
    checkIn: new FormControl<Date | null>(null, {
      validators: [Validators.required],
    }),
    checkOut: new FormControl<Date | null>(null, {
      validators: [Validators.required],
    }),
    guests: new FormControl<number>(1, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
  });

  displayedColumns = ["code", "type", "capacity", "price", "amenities"];
  data = signal<SearchResult[]>([]);
  loading = signal(false);
  errorMsg = signal<string | null>(null);

  constructor(private api: ApiService) {}

  submit() {
    this.errorMsg.set(null);

    if (this.form.invalid) {
      this.errorMsg.set("Revisa las fechas y la cantidad de huéspedes.");
      return;
    }

    const checkIn = this.form.value.checkIn!;
    const checkOut = this.form.value.checkOut!;
    const guests = this.form.controls.guests.value!;

    if (checkOut.getTime() <= checkIn.getTime()) {
      this.errorMsg.set(
        "La fecha de salida debe ser posterior a la de entrada."
      );
      return;
    }

    const ci = this.toIsoDate(checkIn);
    const co = this.toIsoDate(checkOut);

    this.loading.set(true);
    this.api.search(ci, co, guests).subscribe({
      next: (resp: SearchResponse) => {
        this.data.set(resp.results);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set("Error consultando disponibilidad.");
        this.loading.set(false);
      },
    });
  }

  private toIsoDate(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
  }

  amenityChips(a: Record<string, unknown>): string[] {
    if (!a) return [];
    const chips: string[] = [];

    // amenidades comunes (booleans)
    (["wifi", "ac", "tv", "minibar", "balcony"] as const).forEach((k) => {
      const v = a[k];
      if (typeof v === "boolean" && v) chips.push(k.toUpperCase());
    });

    // otras claves simples como pares k=v
    Object.entries(a).forEach(([k, v]) => {
      if (["wifi", "ac", "tv", "minibar", "balcony"].includes(k)) return;
      if (["string", "number", "boolean"].includes(typeof v))
        chips.push(`${k}=${v}`);
    });

    return chips;
  }
}

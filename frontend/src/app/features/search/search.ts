import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatTableModule } from "@angular/material/table";
import { ApiService, SearchResponse } from "../../core/api.service";

@Component({
  selector: "app-search",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatCardModule,
    MatTableModule,
  ],
  templateUrl: "./search.html",
  styleUrls: ["./search.scss"],
})
export class SearchComponent {
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

  loading = signal(false);
  error = signal<string | null>(null);
  data = signal<SearchResponse | null>(null);

  displayedColumns = ["roomId", "type", "capacity", "price"];

  constructor(private api: ApiService) {}

  private toIsoDateOnly(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
  }

  submit() {
    this.error.set(null);
    this.data.set(null);

    const { checkIn, checkOut, guests } = this.form.value;
    if (!checkIn || !checkOut || !guests || guests < 1) {
      this.error.set("Completa fechas válidas y cantidad de huéspedes.");
      return;
    }
    if (checkOut <= checkIn) {
      this.error.set(
        "La fecha de salida debe ser posterior a la fecha de entrada."
      );
      return;
    }

    this.loading.set(true);
    const inStr = this.toIsoDateOnly(checkIn);
    const outStr = this.toIsoDateOnly(checkOut);

    this.api.search(inStr, outStr, guests).subscribe({
      next: (resp: SearchResponse) => {
        // <-- tipo explícito
        this.data.set(resp);
        this.loading.set(false);
      },
      error: (_e: unknown) => {
        this.error.set("No se pudo buscar disponibilidad.");
        this.loading.set(false);
      },
    });
  }
}

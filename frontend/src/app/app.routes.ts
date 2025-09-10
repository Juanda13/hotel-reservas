import { Routes } from "@angular/router";
import { SearchComponent } from "./features/search/search"; // <- sin .component

export const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "search" },
  { path: "search", component: SearchComponent },
];

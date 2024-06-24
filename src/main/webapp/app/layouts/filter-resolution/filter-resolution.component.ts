import { NgFor } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Resolucion } from 'app/entities/enumerations/resolucion.model';

@Component({
  selector: 'jhi-filter-resolution',
  standalone: true,
  imports: [RouterModule, FormsModule, NgFor],
  templateUrl: './filter-resolution.component.html',
  styleUrl: './filter-resolution.component.scss',
})
export class FilterResolutionComponent {
  resolution = Object.values(Resolucion);
  selectedResolution!: string;

  @Output() resolutionChange = new EventEmitter<string>();

  onResolutionChange() {
    this.resolutionChange.emit(this.selectedResolution);
    console.log(this.selectedResolution);
  }

  // onResolutionChange(value: string):void {
  //   this.selectedResolution = value;
  //   this.resolutionChange.emit(value);
  //   console.log(this.selectedResolution);
  // }

  //He creado un componente que quiero que sea reutiizable llamado filter-resolution que sirve para filtrar elculpable, inocnte, pendiente de juicio, que son datos que se encuentran en la entidad de expedientes, y esta relacionado con presos, la relacuión, la fk se encuentra en presos. Quiero dos cosas:
  // 1. Que el componente sea reutilizable
  // 2. Que el filtro en expediente me muestre solo los expedientes que tienen la resolucion seleccionada
  // 3. Que el filtro en presos me muestre los presos que tengan la resolución seleccionada
}

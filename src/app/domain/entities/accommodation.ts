// lodging.model.ts
export interface Picture {
  id: number;
  title?: string;
  description?: string;
  isMain?: boolean;
  url: string;
}

export interface ServiceAccommodation {
  id: number;
  title: string;
  description?: string;
  categoryId: number;
  accommodationId: number;
}

export interface Accommodation {
  id: number;
  // Nombre presentado en frontend (proviene de title del backend)
  nombre: string;
  descripcion?: string;
  direccion?: string; // address backend
  ciudad?: string;    // cityName backend
  estado?: string;    // stateName backend
  anfitrion?: string; // hostName backend
  anfitrionImagen?: string; // nueva propiedad para avatar del anfitrión
  departamento?: string; // opcional si lo añades en backend luego
  precio: number;     // dayPrice backend
  capacidad?: string; // capacity backend (lo convertimos a string para la vista)
  imagenUrl: string;  // derivada de pictures
  estrellas: number;  // placeholder hasta que haya rating real
  pictures?: Picture[];
  services?: ServiceAccommodation[];
}

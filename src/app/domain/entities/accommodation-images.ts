// Mapa de imágenes estáticas para alojamientos conocidos
export const ACCOMMODATION_IMAGE_MAP: Record<number, string> = {
  1: '/assets/images/deptos/casa_con_plantas.jpg',
  2: '/assets/images/deptos/cocina_casa.jpg',
  // Agrega aquí más id: imagen si es necesario
};

export function resolveAccommodationImage(id: number | undefined, backendUrl?: string): string {
  if (!id) return backendUrl || '/assets/images/deptos/finca_robles.png';
  const mapped = ACCOMMODATION_IMAGE_MAP[id];
  if (mapped) return mapped;
  return backendUrl || '/assets/images/deptos/finca_robles.png';
}


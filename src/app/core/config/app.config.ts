import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';

import { AppRoutes } from '../config/app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(AppRoutes)
  ]
};

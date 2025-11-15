import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token) {
    const authReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    console.debug('[AuthInterceptor] Token presente, añadiendo Authorization');
    return next(authReq);
  } else {
    console.debug('[AuthInterceptor] No token en localStorage');
  }
  return next(req);
};

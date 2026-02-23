import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserService } from '../services/user.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const userService = inject(UserService);
  const token = userService.getToken();

  // Skip adding token if none exists
  if (!token) {
    return next(req);
  }

  // Skip adding token for login endpoint
  if (req.url.includes('/auth/login')) {
    return next(req);
  }

  // Add Authorization header
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authReq);
};

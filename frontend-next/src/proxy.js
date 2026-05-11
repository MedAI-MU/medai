import { NextResponse } from "next/server";
import { decodeToken } from "./lib/utils/decodeToken";

export async function proxy(request) {
  let accessToken = request.cookies.get("Authentication")?.value;
  let refreshToken = request.cookies.get("Refresh")?.value;
  // Decode refresh token if available, otherwise fallback to access token
  let payload = refreshToken
    ? decodeToken(refreshToken)
    : accessToken
      ? decodeToken(accessToken)
      : null;
  const { pathname } = request.nextUrl;
  // 1) Setup Protected routes
  const ProtectedRoutes = ["/doctor", "/patient"];
  const isProtectedRoute = ProtectedRoutes.some((route) =>
    pathname.startsWith(route),
  );
  const isAuthRoute = pathname.startsWith("/auth");
  // 2a) Cookies returned from /refresh-token includes Tokens
  // Checks if Refresh success
  let refreshedTokensInCookies = null;
  // 2b) Try to refresh tokens
  if (!accessToken && refreshToken) {
    try {
      // Middleware fetch needs an absolute URL; derive it from the request.
      const refreshUrl = new URL("/api/auth/refresh-token", request.url);
      const refreshResponse = await fetch(refreshUrl, {
        method: "POST",
        headers: {
          Cookie: request.headers.get("cookie") || "",
          "Content-Type": "application/json",
        },
      });
      if (!refreshResponse.ok) {
        throw new Error("Refresh token is not valid");
      }
      refreshedTokensInCookies = refreshResponse.headers.get("set-cookie");
    } catch {
      if (isProtectedRoute)
        return NextResponse.redirect(new URL("/auth/login", request.url));
    }
  }
  const isAuthenticated = accessToken || refreshedTokensInCookies;
  // 3) Redirecting section
  // 3a) Redirect to login if not authenticated and on protected route
  if (!isAuthenticated && isProtectedRoute) {
    return NextResponse.redirect(new URL("/auth/login", request.url));
  }
  // 3b) Redirect to dashboard if authenticated and on (auth or landing) route
  if (isAuthenticated && (isAuthRoute || pathname === "/")) {
    const response = NextResponse.redirect(
      new URL(`/${payload?.role}`, request.url),
    );
    if (refreshedTokensInCookies)
      response.headers.set("set-cookie", refreshedTokensInCookies);
    return response;
  }
  // 3c) Redirect to dashboard if authenticated and on wrong role route
  if (
    isAuthenticated &&
    isProtectedRoute &&
    !pathname.includes(payload?.role)
  ) {
    const response = NextResponse.redirect(
      new URL(`/${payload?.role}`, request.url),
    );
    if (refreshedTokensInCookies)
      response.headers.set("set-cookie", refreshedTokensInCookies);
    return response;
  }
  // 4) Normal request + Set cookies if refreshed
  const response = NextResponse.next();
  if (refreshedTokensInCookies)
    response.headers.set("set-cookie", refreshedTokensInCookies);
  return response;
}

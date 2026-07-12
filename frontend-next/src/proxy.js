import { NextResponse } from "next/server";
import { decodeToken } from "./lib/utils/decodeToken";

export const config = {
  matcher: [
    "/",
    "/doctor/:path*",
    "/patient/:path*",
    "/secretary/:path*",
    "/manager/:path*",
    "/auth/:path*",
    "/reset-password",
    "/verify-email",
  ],
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

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
  const ProtectedRoutes = ["/doctor", "/patient", "/secretary", "/manager"];
  const isProtectedRoute = ProtectedRoutes.some((route) =>
    pathname.startsWith(route),
  );
  const isAuthRoute = pathname.startsWith("/auth");

  // 2a) Cookies returned from /refresh-token includes Tokens

  // Checks if Refresh success
  let refreshedTokensInCookies = null;

  // 2b) Try to refresh tokens
  if (!accessToken && refreshToken) {
    console.log("tried to refresh token");
    try {
      const refreshUrl = `${API_BASE_URL}/api/auth/refresh-token`;

      const refreshResponse = await fetch(refreshUrl, {
        method: "POST",
        headers: {
          cookie: request.headers.get("cookie") || "",
        },
      });

      if (!refreshResponse.ok) {
        console.log(refreshResponse);
        const error = new Error("Refresh token is not valid");
        error.status = refreshResponse.status;
        throw error;
      }
      refreshedTokensInCookies = refreshResponse.headers.get("set-cookie");
    } catch (error) {
      console.log("Refresh error:", error);
      if (error.status === 401) {
        let response;
        if (isProtectedRoute) {
          response = NextResponse.redirect(new URL("/auth/login", request.url));
        } else {
          response = NextResponse.next();
        }
        response.cookies.delete("Authentication");
        response.cookies.delete("Refresh");
        return response;
      }
    }
  }

  const isAuthenticated = accessToken || refreshedTokensInCookies;

  // 2c) Pending users redirect (skip patients)
  if (
    isAuthenticated &&
    payload?.status === "pending" &&
    payload?.role !== "patient" &&
    pathname !== "/auth/pending-approval" &&
    pathname !== "/reset-password" &&
    pathname !== "/verify-email"
  ) {
    const response = NextResponse.redirect(
      new URL("/auth/pending-approval", request.url),
    );
    if (refreshedTokensInCookies)
      response.headers.set("set-cookie", refreshedTokensInCookies);
    return response;
  }

  // 2d) Approved / patient on pending page → send to dashboard
  if (
    isAuthenticated &&
    (payload?.status === "approved" || payload?.role === "patient") &&
    pathname === "/auth/pending-approval"
  ) {
    const response = NextResponse.redirect(
      new URL(`/${payload?.role}`, request.url),
    );
    if (refreshedTokensInCookies)
      response.headers.set("set-cookie", refreshedTokensInCookies);
    return response;
  }

  // 3) Redirecting section

  // 3a) Redirect to login if not authenticated and on protected route
  if (!isAuthenticated && isProtectedRoute) {
    return NextResponse.redirect(new URL("/auth/login", request.url));
  }

  // 3b) Redirect to dashboard if authenticated and on (auth or landing) route
  // Exclude pending users on the pending-approval page (handled by 2c)
  if (
    isAuthenticated &&
    (isAuthRoute || pathname === "/") &&
    !(pathname === "/auth/pending-approval")
  ) {
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
  const requestHeaders = new Headers(request.headers);

  if (refreshedTokensInCookies) {
    const authMatch = refreshedTokensInCookies.match(/Authentication=([^;]+)/);
    const refreshMatch = refreshedTokensInCookies.match(/Refresh=([^;]+)/);

    if (authMatch) request.cookies.set("Authentication", authMatch[1]);
    if (refreshMatch) request.cookies.set("Refresh", refreshMatch[1]);

    const newCookieHeader = request.cookies
      .getAll()
      .map((c) => `${c.name}=${c.value}`)
      .join("; ");

    requestHeaders.set("cookie", newCookieHeader);
  }

  const response = NextResponse.next({
    request: {
      headers: requestHeaders,
    },
  });

  if (refreshedTokensInCookies)
    response.headers.set("set-cookie", refreshedTokensInCookies);

  return response;
}

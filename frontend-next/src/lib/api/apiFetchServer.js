/*
  This is for server-components only.
  It sends the cookies manually.
  No refresh token logic "handled by middleware".
*/
import "server-only";

import { cookies, headers } from "next/headers";

const normalizeEndpoint = (endpoint) =>
  endpoint.startsWith("/") ? endpoint : `/${endpoint}`;

const getOrigin = () => {
  const headerStore = headers();
  const host = headerStore.get("x-forwarded-host") || headerStore.get("host");
  const proto = headerStore.get("x-forwarded-proto") || "http";
  return host ? `${proto}://${host}` : "";
};

const buildUrl = (endpoint) => {
  const normalized = normalizeEndpoint(endpoint);
  const origin = getOrigin();
  return origin ? `${origin}${normalized}` : normalized;
};

export async function apiServerFetch({ endpoint, options = {} }) {
  const cookieStore = await cookies();
  const url = buildUrl(endpoint);

  const response = await fetch(url, {
    ...options,
    headers: {
      Cookie: cookieStore.toString(),
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error("Something went wrong during get data");
  }

  return response.json();
}

const apiServer = {
  get: (endpoint, options = {}) =>
    apiServerFetch({ endpoint, options: { ...options, method: "GET" } }),
  post: (endpoint, body, options = {}) =>
    apiServerFetch({
      endpoint,
      options: {
        ...options,
        method: "POST",
        body: body instanceof FormData ? body : JSON.stringify(body),
      },
    }),
  put: (endpoint, body, options = {}) =>
    apiServerFetch({
      endpoint,
      options: {
        ...options,
        method: "PUT",
        body: body instanceof FormData ? body : JSON.stringify(body),
      },
    }),
  delete: (endpoint, options = {}) =>
    apiServerFetch({ endpoint, options: { ...options, method: "DELETE" } }),
};

export { apiServer };

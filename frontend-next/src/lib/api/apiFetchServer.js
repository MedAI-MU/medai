/*
  This is for server-components only.
  It sends the cookies manually.
  No refresh token logic "handled by middleware".
*/
import "server-only";

import { cookies } from "next/headers";

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

export async function apiServerFetch({ endpoint, options = {} }) {
  const cookieStore = await cookies();

  const response = await fetch(`${BASE_URL}/${endpoint}`, {
    ...options,
    headers: {
      Cookie: cookieStore.toString(),
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    const error = new Error("Something went wrong during get data");
    const data = await response.json().catch(() => null);
    console.log(data);
    error.statusCode = response.status;
    throw error;
  }

  return response.json().catch(() => null);
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

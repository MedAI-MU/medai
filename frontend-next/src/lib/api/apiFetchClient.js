import toast from "react-hot-toast";
import { refreshToken } from "../actions/auth";

const normalizeEndpoint = (endpoint) =>
  endpoint.startsWith("/") ? endpoint : `/${endpoint}`;

async function fetchWithOptions(endpoint, options) {
  const isFormData = options?.body instanceof FormData;
  const url = normalizeEndpoint(endpoint);

  return fetch(url, {
    ...options,
    headers: {
      ...(!isFormData && { "Content-Type": "application/json" }),
      ...options.headers,
    },
    credentials: "include",
  });
}

async function apiClientFetch({ endpoint, options = {} }) {
  let response = await fetchWithOptions(endpoint, options);

  // 3) handle unauthorized
  if (response.status === 401) {
    const refreshResponse = await refreshToken();
    if (!refreshResponse.success) {
      const error = new Error("SESSION EXPIRED, PLEASE LOGIN");
      error.status = response.status;
      toast.error("SESSION EXPIRED, PLEASE LOGIN");
      window.location.replace("/auth/login");
      throw error;
    }

    // Second fetch after refresh
    response = await fetchWithOptions(endpoint, options);
  }

  // 4) handle general fails
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    const error = new Error(
      errorData.message || `API Error: ${response.status}`,
    );
    error.status = response.status;
    throw error;
  }

  return response.json();
}

const apiClient = {
  get: (endpoint, options = {}) =>
    apiClientFetch({ endpoint, options: { ...options, method: "GET" } }),
  post: (endpoint, body, options = {}) =>
    apiClientFetch({
      endpoint,
      options: {
        ...options,
        method: "POST",
        body: body instanceof FormData ? body : JSON.stringify(body),
      },
    }),
  patch: (endpoint, body, options = {}) =>
    apiClientFetch({
      endpoint,
      options: {
        ...options,
        method: "PATCH",
        body: body instanceof FormData ? body : JSON.stringify(body),
      },
    }),
  delete: (endpoint, options = {}) =>
    apiClientFetch({ endpoint, options: { ...options, method: "DELETE" } }),
};

export { apiClient };

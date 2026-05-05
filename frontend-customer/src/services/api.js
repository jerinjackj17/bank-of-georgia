import axios from "axios";

// Uses env URL first, then falls back to local Spring Boot API.
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

// Creates one reusable Axios client for all backend API calls.
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Adds JWT token to every request after the customer logs in.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// Extracts the cleanest possible message from backend error responses.
function getErrorMessage(error) {
  const data = error.response?.data;

  if (typeof data === "string") {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error) {
    return data.error;
  }

  if (data?.details) {
    return data.details;
  }

  return error.message || "Request failed";
}

// Converts backend errors into one clean error message for the UI.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = getErrorMessage(error);
    const status = error.response?.status;
    const data = error.response?.data;

    return Promise.reject({
      ...error,
      message,
      status,
      data,
    });
  }
);

// Sends username, email, or phone password login request.
export const login = (loginType, loginId, password) =>
  api.post("/auth/customer/login", {
    loginType,
    loginId,
    password,
  });

// Requests an OTP for phone login.
export const requestOtp = (phoneNumber) =>
  api.post("/auth/customer/login/phone", {
    phoneNumber,
  });

// Verifies the OTP entered by the customer.
export const verifyOtp = (phoneNumber, otp) =>
  api.post("/auth/customer/login/verifyOtp", {
    phoneNumber,
    otp,
  });

// Sends customer registration data to the backend.
export const registerCustomer = (data) =>
  api.post("/customers/register", data);

// Gets all customers from the backend.
export const getAllCustomers = () =>
  api.get("/customers");

// Gets one customer by database ID.
export const getCustomerById = (id) =>
  api.get(`/customers/${id}`);

// Updates customer profile details.
export const updateCustomer = (id, data) =>
  api.put(`/customers/${id}`, data);

// Updates only the customer status field.
export const updateCustomerStatus = (id, status) =>
  api.put(`/customers/${id}/status`, {
    status,
  });

// Exports the configured Axios client for other service files.
export default api;
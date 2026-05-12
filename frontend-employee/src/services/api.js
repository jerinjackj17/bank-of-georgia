import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("employeeAuthToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

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

api.interceptors.response.use(
  (response) => response,
  (error) =>
    Promise.reject({
      ...error,
      message: getErrorMessage(error),
      status: error.response?.status,
      data: error.response?.data,
    })
);

// Employee authentication
export const employeeLogin = (username, password) =>
  api.post("/auth/employee/login/username", {
    username,
    password,
  });

// Customer management
export const getAllCustomers = () => api.get("/customers");

export const getCustomerById = (id) => api.get(`/customers/${id}`);

export const updateCustomer = (id, data) => api.put(`/customers/${id}`, data);

export const updateCustomerStatus = (id, status) =>
  api.put(`/customers/${id}/status`, {
    status,
  });

// Employee management
export const createEmployee = (data) => api.post("/employees", data);

export const getAllEmployees = () => api.get("/employees");

export const getEmployeeById = (id) => api.get(`/employees/${id}`);

export const updateEmployee = (id, data) => api.put(`/employees/${id}`, data);

export const updateEmployeeRole = (id, role) =>
  api.put(`/employees/${id}/role`, {
    role,
  });

export const updateEmployeeStatus = (id, status) =>
  api.put(`/employees/${id}/status`, {
    status,
  });

// Product management
export const getProducts = () => api.get("/products");

export const getProductById = (id) => api.get(`/products/${id}`);

export const createProduct = (data) => api.post("/products", data);

export const updateProduct = (id, data) => api.put(`/products/${id}`, data);

export const updateProductStatus = (id, data) =>
  api.put(`/products/${id}/status`, data);

export default api;
import { createContext, useContext, useEffect, useMemo, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedEmployee = localStorage.getItem("employeeUser");

    if (savedEmployee) {
      setEmployee(JSON.parse(savedEmployee));
    }

    setLoading(false);
  }, []);

  const loginUser = (loginResponse) => {
    const data = loginResponse?.data || loginResponse;

    const token = data?.token || data?.jwt || data?.accessToken;

    if (token) {
      localStorage.setItem("employeeAuthToken", token);
    }

    localStorage.setItem("employeeUser", JSON.stringify(data));
    setEmployee(data);
  };

  const logoutUser = () => {
    localStorage.removeItem("employeeAuthToken");
    localStorage.removeItem("employeeUser");
    setEmployee(null);
  };

  const value = useMemo(
    () => ({
      user: employee,
      employee,
      loading,
      isAuthenticated: Boolean(employee),
      loginUser,
      logoutUser,
    }),
    [employee, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
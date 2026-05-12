// import { createContext, useContext, useState } from "react";

// // create the context
// const AuthContext = createContext(null);

// // provider wraps the whole app
// export function AuthProvider({ children }) {
//   // store the logged-in user object
//   const [user, setUser] = useState(null);

//   // call this after successful login
//   const loginUser = (userData) => setUser(userData);

//   // clear user on logout
//   const logoutUser = () => setUser(null);

//   return (
//     <AuthContext.Provider value={{ user, loginUser, logoutUser }}>
//       {children}
//     </AuthContext.Provider>
//   );
// }

// // custom hook — use this in any component
// export const useAuth = () => useContext(AuthContext);



import { createContext, useContext, useEffect, useMemo, useState } from "react";

// Creates one shared auth context for the full React app.
const AuthContext = createContext(null);

// Wraps the app and makes auth data available everywhere.
export function AuthProvider({ children }) {
  // Stores the currently logged-in customer.
  const [user, setUser] = useState(null);

  // Tracks if saved login data has finished loading.
  const [loading, setLoading] = useState(true);

  // Restores customer data from browser storage after page refresh.
  useEffect(() => {
    const savedUser = localStorage.getItem("authUser");

    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }

    setLoading(false);
  }, []);

  // Saves token and customer data after successful login.
  const loginUser = (loginResponse) => {
    const token =
      loginResponse?.token ||
      loginResponse?.jwt ||
      loginResponse?.accessToken ||
      loginResponse?.data?.token ||
      loginResponse?.data?.jwt ||
      loginResponse?.data?.accessToken;

    const customer =
      loginResponse?.customer ||
      loginResponse?.user ||
      loginResponse?.customerResponse ||
      loginResponse?.data?.customer ||
      loginResponse?.data?.user ||
      loginResponse;

    if (token) {
      localStorage.setItem("authToken", token);
    }

    localStorage.setItem("authUser", JSON.stringify(customer));
    setUser(customer);
  };

  // Clears customer data and token when logging out.
  const logoutUser = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("authUser");
    setUser(null);
  };

  // Keeps context value stable and avoids unnecessary rerenders.
  const value = useMemo(
    () => ({
      user,
      loading,
      isAuthenticated: Boolean(user),
      loginUser,
      logoutUser,
    }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Gives components simple access to auth state and auth actions.
export const useAuth = () => useContext(AuthContext);
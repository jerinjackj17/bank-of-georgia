import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { getAllCustomers } from "../services/api";
import { loginWithPassword, requestOtp } from "../services/authApi";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  // Used to move customer to dashboard or OTP page.
  const navigate = useNavigate();

  // Saves logged-in customer data into global auth state.
  const { loginUser } = useAuth();

  // Controls password login or phone OTP login mode.
  const [mode, setMode] = useState("password");

  // Stores whether customer logs in with username or email.
  const [loginType, setLoginType] = useState("username");

  // Stores username or email entered by the customer.
  const [loginId, setLoginId] = useState("");

  // Stores password entered by the customer.
  const [password, setPassword] = useState("");

  // Stores phone number for OTP login.
  const [phoneNumber, setPhoneNumber] = useState("");

  // Tracks button loading state during backend calls.
  const [loading, setLoading] = useState(false);

  // Stores readable error message for the screen.
  const [error, setError] = useState("");

  // Stores field-specific validation messages.
  const [fieldErrors, setFieldErrors] = useState({});

  // Switches login method and clears old errors.
  function switchMode(newMode) {
    setMode(newMode);
    setError("");
    setFieldErrors({});
  }

  // Clears one field error when the user types again.
  function clearFieldError(field) {
    setFieldErrors((prev) => ({
      ...prev,
      [field]: "",
    }));
    setError("");
  }

  // Converts backend login errors into user-friendly messages.
  function getFriendlyLoginError(message) {
    const text = String(message || "").toLowerCase();

    if (text.includes("password") || text.includes("credential")) {
      return "Invalid password. Please try again.";
    }

    if (text.includes("blocked") || text.includes("inactive")) {
      return "This account is not active. Please contact the bank.";
    }

    if (text.includes("not found") || text.includes("not exist")) {
      return loginType === "email"
        ? "No customer found with this email address."
        : "No customer found with this username.";
    }

    if (text.includes("email")) {
      return "No customer found with this email address.";
    }

    if (text.includes("username")) {
      return "No customer found with this username.";
    }

    return message || "Login failed. Please check your details and try again.";
  }

  // Converts backend OTP errors into user-friendly messages.
  function getFriendlyOtpError(message) {
    const text = String(message || "").toLowerCase();

    if (text.includes("not found") || text.includes("not exist")) {
      return "No customer found with this phone number.";
    }

    if (text.includes("phone")) {
      return "No customer found with this phone number.";
    }

    if (text.includes("otp")) {
      return "Failed to send OTP. Please try again.";
    }

    return message || "Failed to send OTP. Please check your phone number.";
  }

  // Validates username/email password login before backend call.
  function validatePasswordLogin() {
    const errs = {};

    if (!loginId.trim()) {
      errs.loginId = loginType === "email" ? "Email is required." : "Username is required.";
    }

    if (loginType === "email" && loginId.trim() && !loginId.includes("@")) {
      errs.loginId = "Enter a valid email address.";
    }

    if (!password) {
      errs.password = "Password is required.";
    }

    return errs;
  }

  // Validates phone OTP login before backend call.
  function validatePhoneLogin() {
    const errs = {};

    if (!phoneNumber.trim()) {
      errs.phoneNumber = "Phone number is required.";
    }

    return errs;
  }

  // Extracts a customer object from different possible backend response shapes.
  function extractCustomer(loginResponse) {
    return (
      loginResponse?.customer ||
      loginResponse?.user ||
      loginResponse?.customerResponse ||
      loginResponse?.data?.customer ||
      loginResponse?.data?.user ||
      loginResponse
    );
  }

  // Checks if an object already looks like a full customer record.
  function isFullCustomer(customer) {
    return Boolean(
      customer?.id ||
        customer?.customerId ||
        customer?.email ||
        customer?.phone ||
        customer?.phoneNumber
    );
  }

  // Finds the full customer record when login returns only loginId or verified status.
  async function findCustomerAfterLogin(loginResponse) {
    const directCustomer = extractCustomer(loginResponse);

    if (isFullCustomer(directCustomer)) {
      return directCustomer;
    }

    const lookupValue =
      loginResponse?.loginId ||
      loginResponse?.username ||
      loginResponse?.email ||
      loginId;

    const { data } = await getAllCustomers();

    const customers = Array.isArray(data)
      ? data
      : Array.isArray(data?.content)
        ? data.content
        : Array.isArray(data?.data)
          ? data.data
          : [];

    return customers.find((customer) => {
      if (loginType === "email") {
        return customer.email?.toLowerCase() === lookupValue.toLowerCase();
      }

      return customer.username?.toLowerCase() === lookupValue.toLowerCase();
    });
  }

  // Handles username or email password login.
  async function handlePasswordLogin(e) {
    e.preventDefault();

    const validationErrors = validatePasswordLogin();

    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      return;
    }

    setLoading(true);
    setError("");
    setFieldErrors({});

    try {
      const data = await loginWithPassword(
        loginType,
        loginId.trim(),
        password
      );

      const customer = await findCustomerAfterLogin(data);

      if (!customer) {
        setError("Login succeeded, but customer profile could not be loaded.");
        return;
      }

      loginUser(customer);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(getFriendlyLoginError(err.message));
    } finally {
      setLoading(false);
    }
  }

  // Requests OTP and sends customer to OTP verification page.
  async function handlePhoneLogin(e) {
    e.preventDefault();

    const validationErrors = validatePhoneLogin();

    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      return;
    }

    setLoading(true);
    setError("");
    setFieldErrors({});

    try {
      await requestOtp(phoneNumber.trim());
      navigate("/login/otp", { state: { phoneNumber: phoneNumber.trim() } });
    } catch (err) {
      setError(getFriendlyOtpError(err.message));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-10 text-slate-900">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-6xl items-center justify-center">
        <div className="grid w-full overflow-hidden rounded-3xl bg-white shadow-2xl shadow-slate-300/60 lg:grid-cols-[1.1fr_0.9fr]">
          <section className="hidden bg-slate-950 p-12 text-white lg:flex lg:flex-col lg:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.28em] text-blue-300">
                Bank of Georgia
              </p>

              <h1 className="mt-6 max-w-xl text-5xl font-bold leading-tight xl:text-6xl">
                Banking System
              </h1>

              <p className="mt-6 max-w-lg text-base leading-7 text-slate-300">
                Login here to continue.
              </p>
            </div>

            <div className="rounded-3xl border border-white/10 bg-white/5 p-6">
              <p className="text-lg font-bold text-white">
                Welcome
              </p>

              <p className="mt-2 text-sm leading-6 text-slate-300">
                Access your banking dashboard after login.
              </p>
            </div>
          </section>

          <section className="p-8 sm:p-12">
            <div className="mx-auto w-full max-w-md">
              <div className="mb-8">
                <p className="text-sm font-semibold uppercase tracking-[0.2em] text-blue-700">
                  Bank of Georgia
                </p>

                <h2 className="mt-3 text-3xl font-bold text-slate-950">
                  Sign in
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-600">
                  Enter your login details to access the banking system.
                </p>
              </div>

              <div className="mb-6 grid grid-cols-2 rounded-xl bg-slate-100 p-1">
                <button
                  type="button"
                  onClick={() => switchMode("password")}
                  className={`rounded-lg px-4 py-3 text-sm font-semibold transition ${
                    mode === "password"
                      ? "bg-white text-blue-900 shadow-sm"
                      : "text-slate-600 hover:text-slate-900"
                  }`}
                >
                  Password
                </button>

                <button
                  type="button"
                  onClick={() => switchMode("phone")}
                  className={`rounded-lg px-4 py-3 text-sm font-semibold transition ${
                    mode === "phone"
                      ? "bg-white text-blue-900 shadow-sm"
                      : "text-slate-600 hover:text-slate-900"
                  }`}
                >
                  Phone OTP
                </button>
              </div>

              {error && (
                <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                  {error}
                </div>
              )}

              {mode === "password" && (
                <form onSubmit={handlePasswordLogin} className="space-y-5">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-800">
                      Login type
                    </label>

                    <select
                      value={loginType}
                      onChange={(e) => {
                        setLoginType(e.target.value);
                        setLoginId("");
                        setPassword("");
                        setError("");
                        setFieldErrors({});
                      }}
                      className="h-12 w-full rounded-xl border border-slate-300 bg-white px-4 text-sm font-medium text-slate-900 outline-none transition focus:border-blue-700 focus:ring-4 focus:ring-blue-100"
                    >
                      <option value="username">Username</option>
                      <option value="email">Email</option>
                    </select>
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-800">
                      {loginType === "username" ? "Username" : "Email"}
                    </label>

                    <input
                      type={loginType === "email" ? "email" : "text"}
                      value={loginId}
                      onChange={(e) => {
                        setLoginId(e.target.value);
                        clearFieldError("loginId");
                      }}
                      placeholder={loginType === "username" ? "Enter username" : "Enter email"}
                      className={`h-12 w-full rounded-xl border bg-white px-4 text-sm font-medium text-slate-900 placeholder:text-slate-500 outline-none transition focus:border-blue-700 focus:ring-4 focus:ring-blue-100 ${
                        fieldErrors.loginId ? "border-red-400" : "border-slate-300"
                      }`}
                    />

                    {fieldErrors.loginId && (
                      <p className="mt-2 text-sm font-medium text-red-600">
                        {fieldErrors.loginId}
                      </p>
                    )}
                  </div>

                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-800">
                      Password
                    </label>

                    <input
                      type="password"
                      value={password}
                      onChange={(e) => {
                        setPassword(e.target.value);
                        clearFieldError("password");
                      }}
                      placeholder="Enter password"
                      className={`h-12 w-full rounded-xl border bg-white px-4 text-sm font-medium text-slate-900 placeholder:text-slate-500 outline-none transition focus:border-blue-700 focus:ring-4 focus:ring-blue-100 ${
                        fieldErrors.password ? "border-red-400" : "border-slate-300"
                      }`}
                    />

                    {fieldErrors.password && (
                      <p className="mt-2 text-sm font-medium text-red-600">
                        {fieldErrors.password}
                      </p>
                    )}
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="h-12 w-full rounded-xl bg-blue-900 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {loading ? "Signing in..." : "Sign in"}
                  </button>
                </form>
              )}

              {mode === "phone" && (
                <form onSubmit={handlePhoneLogin} className="space-y-5">
                  <div>
                    <label className="mb-2 block text-sm font-semibold text-slate-800">
                      Phone number
                    </label>

                    <input
                      type="tel"
                      value={phoneNumber}
                      onChange={(e) => {
                        setPhoneNumber(e.target.value);
                        clearFieldError("phoneNumber");
                      }}
                      placeholder="Enter phone number"
                      className={`h-12 w-full rounded-xl border bg-white px-4 text-sm font-medium text-slate-900 placeholder:text-slate-500 outline-none transition focus:border-blue-700 focus:ring-4 focus:ring-blue-100 ${
                        fieldErrors.phoneNumber ? "border-red-400" : "border-slate-300"
                      }`}
                    />

                    {fieldErrors.phoneNumber && (
                      <p className="mt-2 text-sm font-medium text-red-600">
                        {fieldErrors.phoneNumber}
                      </p>
                    )}
                  </div>

                  <button
                    type="submit"
                    disabled={loading}
                    className="h-12 w-full rounded-xl bg-blue-900 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {loading ? "Sending OTP..." : "Send OTP"}
                  </button>
                </form>
              )}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { employeeLogin } from "../services/api";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const navigate = useNavigate();
  const { loginUser } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function getFriendlyLoginError(message) {
    const text = String(message || "").toLowerCase();

    if (text.includes("password") || text.includes("credential")) {
      return "Invalid employee username or password.";
    }

    if (text.includes("blocked") || text.includes("inactive")) {
      return "This employee account is not active.";
    }

    if (text.includes("not found") || text.includes("not exist")) {
      return "No employee found with this username.";
    }

    return message || "Employee login failed. Please check your details.";
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!username.trim() || !password) {
      setError("Username and password are required.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await employeeLogin(username.trim(), password);
      loginUser(response.data);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(getFriendlyLoginError(err.message));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-10 text-slate-900">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-5xl items-center justify-center">
        <div className="grid w-full overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-lg lg:grid-cols-[0.9fr_1.1fr]">
          <section className="hidden bg-slate-950 p-10 text-white lg:flex lg:flex-col lg:justify-center">
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-slate-300">
              Bank of Georgia
            </p>

            <h1 className="mt-4 text-4xl font-bold">
              Employee Portal
            </h1>
          </section>

          <section className="p-8 sm:p-10">
            <div className="mx-auto w-full max-w-md">
              <div className="mb-8">
                <p className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
                  Bank of Georgia
                </p>

                <h2 className="mt-3 text-3xl font-bold text-slate-950">
                  Employee Sign In
                </h2>
              </div>

              {error && (
                <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-5">
                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-800">
                    Username
                  </label>

                  <input
                    type="text"
                    value={username}
                    onChange={(event) => {
                      setUsername(event.target.value);
                      setError("");
                    }}
                    placeholder="Enter username"
                    className="h-12 w-full rounded-xl border border-slate-300 bg-white px-4 text-sm font-medium text-slate-900 placeholder:text-slate-500 outline-none focus:border-slate-900 focus:ring-4 focus:ring-slate-200"
                  />
                </div>

                <div>
                  <label className="mb-2 block text-sm font-semibold text-slate-800">
                    Password
                  </label>

                  <input
                    type="password"
                    value={password}
                    onChange={(event) => {
                      setPassword(event.target.value);
                      setError("");
                    }}
                    placeholder="Enter password"
                    className="h-12 w-full rounded-xl border border-slate-300 bg-white px-4 text-sm font-medium text-slate-900 placeholder:text-slate-500 outline-none focus:border-slate-900 focus:ring-4 focus:ring-slate-200"
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="h-12 w-full rounded-xl bg-slate-950 text-sm font-bold text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {loading ? "Signing in..." : "Sign in"}
                </button>
              </form>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
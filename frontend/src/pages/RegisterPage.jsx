import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerCustomer } from "../services/api";
import Card from "../components/ui/Card";
import Button from "../components/ui/Button";
import Input from "../components/ui/Input";
import ConfirmModal from "../components/ui/ConfirmModal";

export default function RegisterPage() {
  // Used to move customer back to login after registration.
  const navigate = useNavigate();

  // Stores registration form fields.
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    username: "",
    phone: "",
    password: "",
    dateOfBirth: "",
  });

  // Stores field-level validation errors.
  const [errors, setErrors] = useState({});

  // Tracks backend registration loading state.
  const [loading, setLoading] = useState(false);

  // Controls confirmation modal before creating customer.
  const [showConfirm, setShowConfirm] = useState(false);

  // Controls success screen after registration.
  const [success, setSuccess] = useState(false);

  // Stores backend error message.
  const [apiError, setApiError] = useState("");

  // Updates one field and clears its old error.
  const handleChange = (field) => (e) => {
    setForm((prev) => ({
      ...prev,
      [field]: e.target.value,
    }));

    setErrors((prev) => ({
      ...prev,
      [field]: "",
    }));

    setApiError("");
  };

  // Validates customer form before confirmation.
  const validate = () => {
    const errs = {};

    if (!form.firstName.trim()) {
      errs.firstName = "First name is required";
    }

    if (!form.lastName.trim()) {
      errs.lastName = "Last name is required";
    }

    if (!form.email.trim() || !form.email.includes("@")) {
      errs.email = "Enter a valid email address";
    }

    if (form.username.trim().length < 3) {
      errs.username = "Username must be at least 3 characters";
    }

    if (!form.phone.trim()) {
      errs.phone = "Phone number is required";
    }

    if (form.password.length < 6) {
      errs.password = "Password must be at least 6 characters";
    }

    if (!form.dateOfBirth) {
      errs.dateOfBirth = "Date of birth is required";
    }

    return errs;
  };

  // Shows confirmation modal after local validation passes.
  const handleSubmitClick = () => {
    const validationErrors = validate();

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setShowConfirm(true);
  };

  // Sends customer registration request to backend.
  const handleConfirm = async () => {
    setLoading(true);
    setApiError("");

    try {
      const payload = {
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        email: form.email.trim(),
        username: form.username.trim(),
        phone: form.phone.trim(),
        password: form.password,
        dateOfBirth: form.dateOfBirth,
      };

      await registerCustomer(payload);

      setShowConfirm(false);
      setSuccess(true);
    } catch (err) {
      setApiError(err.message || "Registration failed. Please try again.");
      setShowConfirm(false);
    } finally {
      setLoading(false);
    }
  };

  // Shows success screen after customer registration.
  if (success) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-100 px-6 py-10 text-slate-900">
        <Card className="w-full max-w-md text-center">
          <div className="mx-auto mb-5 flex h-16 w-16 items-center justify-center rounded-2xl bg-emerald-100">
            <svg
              className="h-9 w-9 text-emerald-700"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2.5}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>

          <h2 className="text-2xl font-bold text-slate-950">
            Account created
          </h2>

          <p className="mt-3 text-sm leading-6 text-slate-600">
            The customer profile has been created successfully.
          </p>

          <Button
            fullWidth
            className="mt-6"
            onClick={() => navigate("/login")}
          >
            Go to Login
          </Button>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-10 text-slate-900">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-6xl items-center justify-center">
        <div className="grid w-full overflow-hidden rounded-3xl bg-white shadow-2xl shadow-slate-300/60 lg:grid-cols-[0.9fr_1.1fr]">
          <section className="hidden bg-slate-950 p-12 text-white lg:flex lg:flex-col lg:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.28em] text-blue-300">
                Customer Registration
              </p>

              <h1 className="mt-6 max-w-xl text-5xl font-bold leading-tight">
                Create a customer profile
              </h1>

              <p className="mt-6 max-w-lg text-base leading-7 text-slate-300">
                Register a customer with identity, contact, and login details.
              </p>
            </div>

            <div className="rounded-3xl border border-white/10 bg-white/5 p-6">
              <p className="text-sm font-semibold text-slate-300">
                Bank of Georgia
              </p>

              <div className="mt-4 grid gap-3 text-sm text-slate-300">
                <p>Customer identity details</p>
                <p>Email and phone records</p>
                <p>Username and password setup</p>
              </div>
            </div>
          </section>

          <section className="p-8 sm:p-12">
            <div className="mx-auto w-full max-w-2xl">
              <div className="mb-8">
                <p className="text-sm font-semibold uppercase tracking-[0.2em] text-blue-700">
                  Bank of Georgia
                </p>

                <h2 className="mt-3 text-3xl font-bold text-slate-950">
                  Register customer
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-600">
                  Enter customer information exactly as it should appear in the banking system.
                </p>
              </div>

              {apiError && (
                <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                  {apiError}
                </div>
              )}

              <div className="space-y-5">
                <div className="grid gap-5 sm:grid-cols-2">
                  <Input
                    label="First name"
                    id="firstName"
                    placeholder="John"
                    value={form.firstName}
                    onChange={handleChange("firstName")}
                    error={errors.firstName}
                    required
                  />

                  <Input
                    label="Last name"
                    id="lastName"
                    placeholder="Smith"
                    value={form.lastName}
                    onChange={handleChange("lastName")}
                    error={errors.lastName}
                    required
                  />
                </div>

                <div className="grid gap-5 sm:grid-cols-2">
                  <Input
                    label="Email address"
                    id="email"
                    type="email"
                    placeholder="john@example.com"
                    value={form.email}
                    onChange={handleChange("email")}
                    error={errors.email}
                    required
                  />

                  <Input
                    label="Phone number"
                    id="phone"
                    type="tel"
                    placeholder="3420967890"
                    value={form.phone}
                    onChange={handleChange("phone")}
                    error={errors.phone}
                    required
                  />
                </div>

                <div className="grid gap-5 sm:grid-cols-2">
                  <Input
                    label="Username"
                    id="username"
                    placeholder="john_smith"
                    value={form.username}
                    onChange={handleChange("username")}
                    error={errors.username}
                    required
                  />

                  <Input
                    label="Date of birth"
                    id="dateOfBirth"
                    type="date"
                    value={form.dateOfBirth}
                    onChange={handleChange("dateOfBirth")}
                    error={errors.dateOfBirth}
                    required
                  />
                </div>

                <Input
                  label="Password"
                  id="password"
                  type="password"
                  placeholder="Minimum 6 characters"
                  value={form.password}
                  onChange={handleChange("password")}
                  error={errors.password}
                  required
                />
              </div>

              <div className="mt-7 flex flex-col gap-3 sm:flex-row">
                <Button
                  fullWidth
                  onClick={handleSubmitClick}
                  loading={loading}
                >
                  Create Account
                </Button>

                <Button
                  fullWidth
                  variant="outline"
                  onClick={() => navigate("/login")}
                  disabled={loading}
                >
                  Back to Login
                </Button>
              </div>

              <p className="mt-5 text-center text-sm font-medium text-slate-500">
                Existing customer?{" "}
                <button
                  type="button"
                  onClick={() => navigate("/login")}
                  className="font-bold text-blue-800 underline underline-offset-4 hover:text-blue-950"
                >
                  Sign in
                </button>
              </p>
            </div>
          </section>
        </div>
      </div>

      <ConfirmModal
        open={showConfirm}
        title="Confirm registration"
        message={`Create customer profile for ${form.firstName} ${form.lastName} using ${form.email}?`}
        confirmLabel="Create account"
        cancelLabel="Review details"
        onConfirm={handleConfirm}
        onCancel={() => setShowConfirm(false)}
        loading={loading}
      />
    </div>
  );
}
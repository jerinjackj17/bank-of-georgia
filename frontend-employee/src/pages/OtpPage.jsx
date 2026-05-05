import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { requestOtp, verifyOtp } from "../services/authApi";
import { useAuth } from "../context/AuthContext";

export default function OtpPage() {
  // Used to move customer after OTP verification.
  const navigate = useNavigate();

  // Reads phone number passed from login page.
  const { state } = useLocation();

  // Saves verified customer data globally after OTP login.
  const { loginUser } = useAuth();

  // Gets phone number from navigation state.
  const phoneNumber = state?.phoneNumber;

  // Stores OTP entered by the customer.
  const [otp, setOtp] = useState("");

  // Tracks verification button loading.
  const [loading, setLoading] = useState(false);

  // Stores readable error message.
  const [error, setError] = useState("");

  // Tracks OTP expiry timer.
  const [secondsLeft, setSecondsLeft] = useState(300);

  // Tracks resend button loading.
  const [resending, setResending] = useState(false);

  // Sends customer back to login if phone number is missing.
  useEffect(() => {
    if (!phoneNumber) {
      navigate("/login", { replace: true });
    }
  }, [phoneNumber, navigate]);

  // Runs the OTP countdown timer.
  useEffect(() => {
    if (secondsLeft <= 0) {
      return;
    }

    const timer = setInterval(() => {
      setSecondsLeft((current) => current - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [secondsLeft]);

  // Formats seconds into mm:ss format.
  function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60).toString().padStart(2, "0");
    const remainingSeconds = (seconds % 60).toString().padStart(2, "0");

    return `${minutes}:${remainingSeconds}`;
  }

  // Verifies OTP, saves customer session, and opens dashboard.
  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const data = await verifyOtp(phoneNumber, otp);

      const isVerified =
        data?.verified === true ||
        data?.verified === "true" ||
        data?.success === true ||
        data?.status === "SUCCESS";

      if (!isVerified) {
        setError("Incorrect OTP. Please try again.");
        return;
      }

      loginUser(data);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(err.message || "Verification failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  // Requests a new OTP and restarts the timer.
  async function handleResend() {
    setResending(true);
    setError("");

    try {
      await requestOtp(phoneNumber);
      setSecondsLeft(300);
      setOtp("");
    } catch (err) {
      setError(err.message || "Failed to resend OTP. Please try again.");
    } finally {
      setResending(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 px-6 py-10 text-slate-900">
      <div className="mx-auto flex min-h-[calc(100vh-5rem)] w-full max-w-md items-center justify-center">
        <div className="w-full rounded-3xl bg-white p-8 shadow-2xl shadow-slate-300/60">
          <div className="mb-8 text-center">
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-blue-700">
              Bank of Georgia
            </p>

            <h1 className="mt-3 text-3xl font-bold text-slate-950">
              Verify OTP
            </h1>

            <p className="mt-3 text-sm leading-6 text-slate-600">
              We sent a 6 digit code to{" "}
              <span className="font-bold text-slate-950">{phoneNumber}</span>
            </p>
          </div>

          {error && (
            <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="mb-2 block text-sm font-semibold text-slate-800">
                OTP code
              </label>

              <input
                type="text"
                inputMode="numeric"
                maxLength={6}
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, ""))}
                required
                placeholder="000000"
                className="h-14 w-full rounded-xl border border-slate-300 bg-white px-4 text-center font-mono text-2xl font-bold tracking-[0.35em] text-slate-950 placeholder:text-slate-500 outline-none transition focus:border-blue-700 focus:ring-4 focus:ring-blue-100"
              />
            </div>

            <div className="text-center text-sm font-medium text-slate-500">
              {secondsLeft > 0 ? (
                <>
                  Code expires in{" "}
                  <span className="font-bold text-slate-800">
                    {formatTime(secondsLeft)}
                  </span>
                </>
              ) : (
                <span className="font-bold text-red-600">Code expired</span>
              )}
            </div>

            <button
              type="submit"
              disabled={loading || otp.length < 6 || secondsLeft <= 0}
              className="h-12 w-full rounded-xl bg-blue-900 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? "Verifying..." : "Verify and continue"}
            </button>
          </form>

          <div className="mt-5 flex items-center justify-between">
            <button
              type="button"
              onClick={() => navigate("/login")}
              className="text-sm font-semibold text-slate-500 transition hover:text-slate-800"
            >
              Back
            </button>

            <button
              type="button"
              onClick={handleResend}
              disabled={secondsLeft > 0 || resending}
              className="text-sm font-bold text-blue-800 transition hover:text-blue-950 disabled:cursor-not-allowed disabled:opacity-40"
            >
              {resending ? "Sending..." : "Resend code"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
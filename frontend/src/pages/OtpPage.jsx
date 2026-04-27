import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { verifyOtp, requestOtp } from '../../services/authApi';

export default function OtpPage() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const phoneNumber = state?.phoneNumber;

  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [secondsLeft, setSecondsLeft] = useState(300);
  const [resending, setResending] = useState(false);

  useEffect(() => {
    if (!phoneNumber) navigate('/');
  }, [phoneNumber, navigate]);

  useEffect(() => {
    if (secondsLeft <= 0) return;
    const timer = setInterval(() => setSecondsLeft(s => s - 1), 1000);
    return () => clearInterval(timer);
  }, [secondsLeft]);

  function formatTime(seconds) {
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await verifyOtp(phoneNumber, otp);
      if (data.verified === 'true') {
        // TODO: navigate to dashboard once it exists
        alert('OTP verified successfully!');
      } else {
        setError('Incorrect OTP. Please try again.');
      }
    } catch {
      setError('Verification failed. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  async function handleResend() {
    setResending(true);
    setError('');
    try {
      await requestOtp(phoneNumber);
      setSecondsLeft(300);
      setOtp('');
    } catch {
      setError('Failed to resend OTP. Please try again.');
    } finally {
      setResending(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="w-full max-w-md px-4">

        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-blue-900">Bank of Georgia</h1>
          <p className="text-gray-500 mt-2">Enter your verification code</p>
        </div>

        <div className="bg-white rounded-2xl shadow-md p-8">

          <p className="text-sm text-gray-600 mb-6 text-center">
            We sent a 6-digit code to{' '}
            <span className="font-medium text-gray-900">{phoneNumber}</span>
          </p>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <input
              type="text"
              inputMode="numeric"
              maxLength={6}
              value={otp}
              onChange={e => setOtp(e.target.value.replace(/\D/g, ''))}
              required
              placeholder="000000"
              className="w-full border border-gray-300 rounded-lg px-3 py-3 text-center text-2xl font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-blue-500"
            />

            <div className="text-center text-sm text-gray-500">
              {secondsLeft > 0 ? (
                <>Code expires in <span className="font-medium text-gray-700">{formatTime(secondsLeft)}</span></>
              ) : (
                <span className="text-red-500">Code expired</span>
              )}
            </div>

            <button
              type="submit"
              disabled={loading || otp.length < 6}
              className="w-full bg-blue-900 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-800 disabled:opacity-50 transition-colors"
            >
              {loading ? 'Verifying...' : 'Verify'}
            </button>
          </form>

          <div className="mt-4 flex items-center justify-between">
            <button
              onClick={() => navigate('/')}
              className="text-sm text-gray-500 hover:text-gray-700"
            >
              ← Back
            </button>
            <button
              onClick={handleResend}
              disabled={secondsLeft > 0 || resending}
              className="text-sm text-blue-700 hover:text-blue-900 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              {resending ? 'Sending...' : 'Resend code'}
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}

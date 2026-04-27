import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginWithPassword, requestOtp } from '../../services/authApi';

export default function LoginPage() {
  const navigate = useNavigate();
  const [mode, setMode] = useState('password');

  const [loginType, setLoginType] = useState('username');
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');

  const [phoneNumber, setPhoneNumber] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function switchMode(newMode) {
    setMode(newMode);
    setError('');
  }

  async function handlePasswordLogin(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await loginWithPassword(loginType, loginId, password);
      if (data.verified === 'true') {
        // TODO: navigate to dashboard once it exists
        alert(`Welcome, ${data.loginId}`);
      } else {
        setError('Login failed. Please check your credentials.');
      }
    } catch {
      setError('Invalid credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  async function handlePhoneLogin(e) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await requestOtp(phoneNumber);
      navigate('/login/otp', { state: { phoneNumber } });
    } catch {
      setError('Failed to send OTP. Please check your phone number.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="w-full max-w-md px-4">

        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-blue-900">Bank of Georgia</h1>
          <p className="text-gray-500 mt-2">Sign in to your account</p>
        </div>

        <div className="bg-white rounded-2xl shadow-md p-8">

          <div className="flex rounded-lg bg-gray-100 p-1 mb-6">
            <button
              onClick={() => switchMode('password')}
              className={`flex-1 py-2 text-sm font-medium rounded-md transition-colors ${
                mode === 'password'
                  ? 'bg-white text-blue-900 shadow-sm'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Password
            </button>
            <button
              onClick={() => switchMode('phone')}
              className={`flex-1 py-2 text-sm font-medium rounded-md transition-colors ${
                mode === 'phone'
                  ? 'bg-white text-blue-900 shadow-sm'
                  : 'text-gray-500 hover:text-gray-700'
              }`}
            >
              Phone OTP
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {mode === 'password' && (
            <form onSubmit={handlePasswordLogin} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Login type</label>
                <select
                  value={loginType}
                  onChange={e => setLoginType(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="username">Username</option>
                  <option value="email">Email</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {loginType === 'username' ? 'Username' : 'Email'}
                </label>
                <input
                  type={loginType === 'email' ? 'email' : 'text'}
                  value={loginId}
                  onChange={e => setLoginId(e.target.value)}
                  required
                  placeholder={loginType === 'username' ? 'Enter your username' : 'Enter your email'}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                <input
                  type="password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                  placeholder="Enter your password"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-900 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-800 disabled:opacity-50 transition-colors"
              >
                {loading ? 'Signing in...' : 'Sign in'}
              </button>
            </form>
          )}

          {mode === 'phone' && (
            <form onSubmit={handlePhoneLogin} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Phone number</label>
                <input
                  type="tel"
                  value={phoneNumber}
                  onChange={e => setPhoneNumber(e.target.value)}
                  required
                  placeholder="+15550000000"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-900 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-800 disabled:opacity-50 transition-colors"
              >
                {loading ? 'Sending OTP...' : 'Send OTP'}
              </button>
            </form>
          )}

        </div>
      </div>
    </div>
  );
}

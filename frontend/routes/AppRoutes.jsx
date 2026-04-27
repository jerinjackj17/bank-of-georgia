import { Routes, Route } from 'react-router-dom';
import LoginPage from '../src/pages/LoginPage';
import OtpPage from '../src/pages/OtpPage';

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/login/otp" element={<OtpPage />} />
    </Routes>
  );
}

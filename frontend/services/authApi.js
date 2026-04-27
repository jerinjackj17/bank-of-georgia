import api from './api';

export async function loginWithPassword(loginType, loginId, password) {
  const { data } = await api.post('/auth/customer/login', { loginType, loginId, password });
  return data;
}

export async function requestOtp(phoneNumber) {
  const { data } = await api.post('/auth/customer/login/phone', { phoneNumber });
  return data;
}

export async function verifyOtp(phoneNumber, otp) {
  const { data } = await api.post('/auth/customer/login/verifyOtp', { phoneNumber, otp });
  return data;
}

import api from "./api";

// Sends password login request using username, email, or phone.
export async function loginWithPassword(loginType, loginId, password) {
  const { data } = await api.post("/auth/customer/login", {
    loginType,
    loginId,
    password,
  });

  return data;
}

// Sends phone number to backend so it can generate and send OTP.
export async function requestOtp(phoneNumber) {
  const { data } = await api.post("/auth/customer/login/phone", {
    phoneNumber,
  });

  return data;
}

// Sends phone number and OTP code to verify customer login.
export async function verifyOtp(phoneNumber, otp) {
  const { data } = await api.post("/auth/customer/login/verifyOtp", {
    phoneNumber,
    otp,
  });

  return data;
}
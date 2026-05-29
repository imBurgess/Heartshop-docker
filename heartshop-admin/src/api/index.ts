import axios, { type AxiosInstance, type AxiosError } from "axios";
import { useAuthStore } from "@/stores/auth";

// 建立 axios 實例
const apiClient: AxiosInstance = axios.create({
  baseURL: "",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 請求攔截器
apiClient.interceptors.request.use(
  (config) => {
    // 如果有 token，加到 header
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 回應攔截器
apiClient.interceptors.response.use(
  (response) => {
    // 統一處理成功回應
    return response.data;
  },
  (error: AxiosError<any>) => {
    // 統一錯誤處理
    const message =
      error.response?.data?.error?.message || error.message || "請求失敗";

    // 401 未授權
    if (error.response?.status === 401) {
      const authStore = useAuthStore();
      authStore.logout();
      window.location.href = "/login";
    }

    console.error("API Error:", message);
    return Promise.reject(new Error(message));
  }
);

export default apiClient;

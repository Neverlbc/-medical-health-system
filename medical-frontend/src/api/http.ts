import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/store/modules/auth';

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
});

http.interceptors.request.use((config) => {
  const store = useAuthStore();
  if (store.token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${store.token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => {
    const data = response.data;
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败');
      return Promise.reject(data);
    }
    if (Object.prototype.hasOwnProperty.call(data, 'data')) {
      return data.data;
    }
    if (Object.prototype.hasOwnProperty.call(data, 'message')) {
      return data.message;
    }
    return data;
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误';
    ElMessage.error(message);
    if (error.response?.status === 401) {
      const store = useAuthStore();
      store.logoutAction();
    }
    return Promise.reject(error);
  }
);

export default http;


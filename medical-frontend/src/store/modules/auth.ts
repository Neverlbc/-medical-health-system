import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { login, logout, type LoginPayload } from '@/api/modules/auth';

const TOKEN_KEY = 'medical-health-token';
const USER_INFO_KEY = 'medical-health-user-info';

export interface UserInfo {
  id: number;
  username: string;
  nickname?: string;
  role: string;
  avatar?: string;
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY));
  const userInfo = ref<UserInfo | null>(null);

  // 初始化时尝试从 localStorage 读取 userInfo
  try {
    const storedUser = localStorage.getItem(USER_INFO_KEY);
    if (storedUser) {
      userInfo.value = JSON.parse(storedUser);
    }
  } catch (e) {
    console.error('Failed to parse user info', e);
  }

  const isAuthenticated = computed(() => Boolean(token.value));

  async function loginAction(payload: LoginPayload) {
    const res = await login(payload);
    token.value = res.token;
    userInfo.value = {
      id: res.userId,
      username: res.username,
      nickname: res.nickname,
      role: res.role
    };
    localStorage.setItem(TOKEN_KEY, token.value);
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo.value));
  }

  async function logoutAction() {
    if (token.value) {
      try {
        await logout();
      } catch (e) {
        // ignore logout error
      }
    }
    token.value = null;
    userInfo.value = null;
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_INFO_KEY);
  }

  function setToken(value: string | null) {
    token.value = value;
    if (value) {
      localStorage.setItem(TOKEN_KEY, value);
    } else {
      localStorage.removeItem(TOKEN_KEY);
    }
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    loginAction,
    logoutAction,
    setToken
  };
});


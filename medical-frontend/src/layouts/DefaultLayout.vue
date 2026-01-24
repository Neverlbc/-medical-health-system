<template>
  <el-container class="layout-wrapper">
    <!-- 侧边导航栏 -->
    <el-aside width="240px" class="side-aside">
      <div class="side-logo">
        <svg viewBox="0 0 100 100" class="logo-svg">
          <rect x="35" y="15" width="30" height="70" rx="4" fill="#ff4d4f" />
          <rect x="15" y="35" width="70" height="30" rx="4" fill="#ff4d4f" />
        </svg>
        <div class="logo-text">
          <h1>智慧医疗</h1>
          <p>Health Management</p>
        </div>
      </div>

      <el-scrollbar class="menu-scroll">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="transparent"
          text-color="rgba(255, 255, 255, 0.7)"
          active-text-color="#fff"
          class="side-menu"
        >
          <template v-if="role === 'PATIENT'">
            <el-menu-item index="/patient">
              <el-icon><Monitor /></el-icon>
              <span>首页概览</span>
            </el-menu-item>
            <el-menu-item index="/records">
              <el-icon><Document /></el-icon>
              <span>健康档案</span>
            </el-menu-item>
            <el-menu-item index="/health-data">
              <el-icon><DataLine /></el-icon>
              <span>数据监测</span>
            </el-menu-item>
            <el-menu-item index="/ai-consultation">
              <el-icon><ChatDotRound /></el-icon>
              <span>AI 智能问诊</span>
            </el-menu-item>
            <el-menu-item index="/appointments">
              <el-icon><Calendar /></el-icon>
              <span>预约挂号</span>
            </el-menu-item>
          </template>
          <template v-else>
            <el-menu-item index="/doctor">
              <el-icon><Monitor /></el-icon>
              <span>统计工作台</span>
            </el-menu-item>
            <el-menu-item index="/patients">
              <el-icon><User /></el-icon>
              <span>患者名单</span>
            </el-menu-item>
            <el-menu-item index="/records">
              <el-icon><Document /></el-icon>
              <span>病历档案</span>
            </el-menu-item>
            <el-menu-item index="/appointments">
              <el-icon><Calendar /></el-icon>
              <span>门诊预约</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="main-header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="custom-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <div class="notice-center">
            <el-badge is-dot class="item">
              <el-icon :size="20"><Bell /></el-icon>
            </el-badge>
          </div>

          <div class="divider-v"></div>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="admin-user">
              <div class="user-meta">
                <span class="u-name">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</span>
                <span class="u-role">{{ roleText }}</span>
              </div>
              <el-avatar :size="36" :icon="UserFilled" class="u-avatar" />
              <el-icon class="caret-icon"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-drop-menu">
                <el-dropdown-item command="profile">
                  <el-icon><Setting /></el-icon>账号设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>安全退出
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容主体 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { 
  Calendar, ChatDotRound, DataLine, Monitor, Document, 
  User, CaretBottom, Bell, Setting, SwitchButton, UserFilled
} from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const activeMenu = computed(() => {
  const path = route.path;
  if (path.startsWith('/records')) return '/records';
  if (path.startsWith('/appointments')) return '/appointments';
  if (path.startsWith('/patients')) return '/patients';
  return path;
});

const pageTitle = computed(() => (route.meta.title as string) || '工作台');
const role = computed(() => authStore.userInfo?.role || 'PATIENT');
const roleText = computed(() => {
  if (role.value === 'DOCTOR') return '医生';
  if (role.value === 'ADMIN') return '管理员';
  return '个人用户';
});

const handleCommand = (cmd: string) => {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录并结束本次会话吗？', '系统提示', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'info',
      roundButton: true
    }).then(async () => {
      await authStore.logoutAction();
      router.replace({ name: 'Login' });
    });
  }
};
</script>

<style scoped lang="scss">
.layout-wrapper {
  height: 100vh;
  background-color: #f7f9fc;
}

// 侧边栏样式
.side-aside {
  background-color: #0d1624;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.side-logo {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 12px;
  background-color: #0b121e;

  .logo-svg {
    width: 32px;
    height: 32px;
  }

  .logo-text {
    h1 {
      margin: 0;
      font-size: 18px;
      font-weight: 700;
      color: #fff;
      letter-spacing: 0.5px;
    }
    p {
      margin: 0;
      font-size: 10px;
      color: rgba(255, 255, 255, 0.4);
      font-weight: 600;
      text-transform: uppercase;
    }
  }
}

.menu-scroll {
  flex: 1;
  border: none;
}

.side-menu {
  border-right: none !important;
  padding: 16px 12px;

  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
    border-radius: 12px;
    margin-bottom: 4px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &.is-active {
      background: linear-gradient(90deg, #2a64ff 0%, #3a80ff 100%) !important;
      color: #fff !important;
      box-shadow: 0 4px 12px rgba(42, 100, 255, 0.3);
      
      &::before {
        content: '';
        position: absolute;
        left: -12px;
        top: 20%;
        bottom: 20%;
        width: 4px;
        background: #fff;
        border-radius: 0 4px 4px 0;
      }
    }

    &:hover:not(.is-active) {
      background: rgba(255, 255, 255, 0.05) !important;
      color: #fff !important;
    }

    .el-icon {
      font-size: 18px;
      margin-right: 12px;
    }

    span {
      font-weight: 600;
      font-size: 14px;
    }
  }
}

// 主容器
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #edf1f7;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: relative;
  z-index: 100;
}

.custom-breadcrumb {
  :deep(.el-breadcrumb__inner) {
    color: #8c8c8c;
    font-weight: 500;
    &.is-link:hover { color: #2a64ff; }
  }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: #1a1a1a;
    font-weight: 700;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;

  .notice-center {
    color: #8c8c8c;
    cursor: pointer;
    transition: 0.3s;
    &:hover { color: #2a64ff; }
  }

  .divider-v {
    width: 1px;
    height: 20px;
    background: #edf1f7;
  }

  .admin-user {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 50px;
    transition: 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    .user-meta {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      
      .u-name {
        font-size: 14px;
        font-weight: 700;
        color: #1a1a1a;
      }
      .u-role {
        font-size: 11px;
        color: #8c8c8c;
        font-weight: 600;
      }
    }

    .u-avatar {
      background: linear-gradient(135deg, #2a64ff, #64dcff);
      font-weight: 700;
      color: #fff;
    }

    .caret-icon {
      color: #bfbfbf;
      font-size: 12px;
    }
  }
}

.main-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  background-color: #f7f9fc;
}

/* 页面切换动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s ease-out;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.user-drop-menu {
  border-radius: 12px;
  padding: 8px;
  border: none;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  
  :deep(.el-dropdown-menu__item) {
    border-radius: 8px;
    margin: 2px 0;
    padding: 10px 16px;
    font-weight: 600;
    
    &:hover {
      background-color: #f5f7fa;
      color: #2a64ff;
    }
    
    .el-icon { margin-right: 8px; }
  }

  .logout-item:hover {
    background-color: #fff1f0 !important;
    color: #ff4d4f !important;
  }
}
</style>

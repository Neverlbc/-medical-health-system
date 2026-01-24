<template>
  <div class="profile">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="profile-card">
          <div class="profile-header">
            <el-avatar :size="100" :icon="UserFilled" class="profile-avatar" />
            <h2 class="profile-name">{{ authStore.userInfo?.nickname || authStore.userInfo?.username }}</h2>
            <el-tag :type="getRoleType(authStore.userInfo?.role)" class="profile-role">
              {{ getRoleName(authStore.userInfo?.role) }}
            </el-tag>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>用户名：{{ authStore.userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <el-icon><Iphone /></el-icon>
              <span>手机号：{{ '138****8888' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：{{ 'user@example.com' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card shadow="hover" class="edit-card">
          <template #header>
            <div class="card-header">
              <span>基本资料</span>
              <el-button type="primary" link>编辑</el-button>
            </div>
          </template>
          <el-form :model="form" label-width="100px" class="profile-form">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="form.bio" type="textarea" rows="4" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="hover" class="mt-4">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
            </div>
          </template>
          <div class="security-item">
            <div class="security-info">
              <div class="security-title">登录密码</div>
              <div class="security-desc">建议定期修改密码以保护账户安全</div>
            </div>
            <el-button link type="primary">修改</el-button>
          </div>
          <el-divider />
          <div class="security-item">
            <div class="security-info">
              <div class="security-title">手机绑定</div>
              <div class="security-desc">已绑定：138****8888</div>
            </div>
            <el-button link type="primary">换绑</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { UserFilled, User, Iphone, Message } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const authStore = useAuthStore();

const form = reactive({
  nickname: authStore.userInfo?.nickname || '',
  gender: 1,
  bio: '这是一段个人简介...'
});

const getRoleName = (role?: string) => {
  switch (role) {
    case 'PATIENT': return '患者';
    case 'DOCTOR': return '医生';
    case 'ADMIN': return '管理员';
    default: return '用户';
  }
};

const getRoleType = (role?: string) => {
  switch (role) {
    case 'PATIENT': return '';
    case 'DOCTOR': return 'success';
    case 'ADMIN': return 'danger';
    default: return 'info';
  }
};

const saveProfile = () => {
  ElMessage.success('个人资料保存成功');
};
</script>

<style scoped lang="scss">
.profile {
  .profile-card {
    text-align: center;
    
    .profile-header {
      padding: 20px 0;
      
      .profile-avatar {
        background: #409eff;
        margin-bottom: 16px;
      }
      
      .profile-name {
        font-size: 20px;
        font-weight: 600;
        margin: 0 0 8px;
        color: #303133;
      }
    }
    
    .profile-info {
      text-align: left;
      padding: 20px 0 0;
      border-top: 1px solid #ebeef5;
      
      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: 12px;
        color: #606266;
        
        .el-icon {
          margin-right: 8px;
          font-size: 16px;
        }
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
  }
  
  .security-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    
    .security-title {
      font-size: 14px;
      color: #303133;
      margin-bottom: 4px;
    }
    
    .security-desc {
      font-size: 12px;
      color: #909399;
    }
  }
}

.mt-4 { margin-top: 16px; }
</style>

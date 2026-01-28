<template>
  <div class="patients">
    <el-card shadow="hover" class="patients__card">
      <template #header>
        <div class="patients__header">
          <div class="header-title">
            <el-icon class="mr-2"><User /></el-icon>
            <span>我的患者管理</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="keyword"
              placeholder="搜索姓名/用户名/手机号"
              :prefix-icon="Search"
              clearable
              @keyup.enter="search"
              class="search-input"
            />
            <el-button type="primary" :icon="Search" class="ml-3" @click="search">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border stripe highlight-current-row v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="nickname" label="姓名/昵称" min-width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="24" :icon="UserFilled" class="mr-2" />
              <span>{{ row.nickname || row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain :icon="Document" @click="toRecords(row.id)">查看档案</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="empty-tip" v-if="list.length === 0 && !loading">
        <el-empty description="暂无患者数据，请尝试搜索" :image-size="100" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { searchPatients, type SimpleUser } from '@/api/modules/user';
import { User, Search, UserFilled, Document } from '@element-plus/icons-vue';

const router = useRouter();
const keyword = ref('');
const list = ref<SimpleUser[]>([]);
const loading = ref(false);

const search = async () => {
  loading.value = true;
  try {
    list.value = await searchPatients(keyword.value);
  } finally {
    loading.value = false;
  }
};

const toRecords = (userId: number) => {
  router.push({ name: 'Records', query: { userId } });
};

search();
</script>

<style scoped lang="scss">
.patients {
  &__card {
    border-radius: 8px;
    min-height: 500px;
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      font-size: 16px;
      font-weight: 600;
      display: flex;
      align-items: center;
    }

    .header-actions {
      display: flex;
      align-items: center;

      .search-input {
        width: 260px;
      }
    }
  }
}

.user-info {
  display: flex;
  align-items: center;
}

.mr-2 { margin-right: 8px; }
.ml-3 { margin-left: 12px; }

// 移动端适配
@media (max-width: 768px) {
  .patients {
    &__card {
      border-radius: 12px;
      min-height: auto;
      
      :deep(.el-card__header) {
        padding: 16px;
      }
      
      :deep(.el-card__body) {
        padding: 12px;
      }
    }
    
    &__header {
      flex-direction: column;
      gap: 12px;
      align-items: flex-start;
      
      .header-title {
        font-size: 14px;
      }
      
      .header-actions {
        width: 100%;
        flex-wrap: wrap;
        gap: 10px;
        
        .search-input {
          flex: 1;
          min-width: 150px;
        }
        
        .el-button {
          margin-left: 0 !important;
        }
      }
    }
  }
  
  :deep(.el-table) {
    min-width: 500px;
  }
  
  .empty-tip {
    padding: 20px 0;
  }
}
</style>


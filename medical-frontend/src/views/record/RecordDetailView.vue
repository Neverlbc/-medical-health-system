<template>
  <div class="record-detail">
    <el-card shadow="hover" class="record-detail__card">
      <template #header>
        <div class="record-detail__header">
          <div class="header-title">
            <el-button :icon="ArrowLeft" circle @click="$router.back()" class="mr-3" />
            <span>档案详情</span>
            <el-tag class="ml-3" type="info">ID: {{ record?.id }}</el-tag>
          </div>
          <div class="header-actions">
            <el-button type="primary" plain :icon="Edit" @click="$router.push({ name: 'Records' })">编辑</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border class="record-descriptions">
        <el-descriptions-item label="用户ID" label-class-name="desc-label">{{ record?.userId }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" label-class-name="desc-label">{{ record?.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" label-class-name="desc-label">
          <el-tag v-if="!record?.allergies || record?.allergies === '无'" type="success">无</el-tag>
          <span v-else class="text-danger">{{ record?.allergies }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="家族病史" label-class-name="desc-label">{{ record?.familyHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2" label-class-name="desc-label">{{ record?.medicalHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="用药史" :span="2" label-class-name="desc-label">{{ record?.medicationHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" label-class-name="desc-label">
          <div class="remark-content">{{ record?.remark || '暂无备注' }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="mt-4 record-detail__card" shadow="hover">
      <template #header>
        <div class="record-detail__header">
          <div class="header-title">
            <el-icon class="mr-2"><Paperclip /></el-icon>
            <span>附件列表</span>
            <el-tag type="info" round class="ml-2">{{ attachments.length }}</el-tag>
          </div>
        </div>
      </template>
      
      <div class="attachments">
        <el-empty v-if="attachments.length === 0" description="暂无附件" :image-size="100" />
        <div v-else class="attachments__grid">
          <div class="att-card" v-for="att in attachments" :key="att.id">
            <div class="att-preview">
              <template v-if="isImage(att)">
                <el-image 
                  :src="att.fileUrl" 
                  :preview-src-list="[att.fileUrl]" 
                  fit="cover" 
                  class="att-image"
                  loading="lazy"
                />
              </template>
              <template v-else-if="isPdf(att)">
                <div class="pdf-icon">
                  <span class="file-type">PDF</span>
                </div>
              </template>
              <template v-else>
                <div class="file-icon">
                  <el-icon><Document /></el-icon>
                </div>
              </template>
            </div>
            
            <div class="att-info">
              <div class="att-name" :title="att.fileName">{{ att.fileName }}</div>
              <div class="att-size">{{ (att.fileSize / 1024).toFixed(1) }} KB</div>
            </div>
            
            <div class="att-actions">
              <el-button-group>
                <el-button size="small" :icon="View" @click="openFile(att)">预览</el-button>
                <el-button size="small" :icon="Download" @click="downloadFile(att)">下载</el-button>
              </el-button-group>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { getRecord, listAttachments, type PatientRecord, type RecordAttachment } from '@/api/modules/record';
import { ArrowLeft, Edit, Paperclip, Document, View, Download } from '@element-plus/icons-vue';

const route = useRoute();
const record = ref<PatientRecord>();
const attachments = ref<RecordAttachment[]>([]);

const isImage = (a: RecordAttachment) => {
  const name = (a.fileName || '').toLowerCase();
  return /\.(png|jpe?g|gif|webp|bmp|svg)$/.test(name) || (a.fileType || '').startsWith('image/');
};
const isPdf = (a: RecordAttachment) => {
  const name = (a.fileName || '').toLowerCase();
  return name.endsWith('.pdf') || (a.fileType || '') === 'application/pdf';
};

const openFile = (att: RecordAttachment) => {
  window.open(att.fileUrl, '_blank');
};

const downloadFile = (att: RecordAttachment) => {
  const link = document.createElement('a');
  link.href = att.fileUrl || '';
  link.download = att.fileName || 'download';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

onMounted(async () => {
  const id = Number(route.params.id);
  record.value = await getRecord(id);
  attachments.value = await listAttachments(id);
});
</script>

<style scoped lang="scss">
.record-detail {
  &__card {
    border-radius: 8px;
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 600;
    }
  }
}

.record-descriptions {
  :deep(.desc-label) {
    width: 120px;
    font-weight: 600;
    background-color: #f5f7fa;
  }
}

.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

.remark-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.attachments__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.att-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  background: #fff;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .att-preview {
    height: 140px;
    background: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    .att-image {
      width: 100%;
      height: 100%;
    }

    .pdf-icon, .file-icon {
      font-size: 48px;
      color: #909399;
      display: flex;
      flex-direction: column;
      align-items: center;
    }

    .file-type {
      font-size: 14px;
      font-weight: 700;
      margin-top: 4px;
      color: #f56c6c;
    }
  }

  .att-info {
    padding: 12px;
    border-top: 1px solid #ebeef5;
    border-bottom: 1px solid #ebeef5;

    .att-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-bottom: 4px;
    }

    .att-size {
      font-size: 12px;
      color: #909399;
    }
  }

  .att-actions {
    padding: 8px;
    display: flex;
    justify-content: center;
    background: #fcfcfc;
    
    .el-button-group {
      width: 100%;
      display: flex;
      
      .el-button {
        flex: 1;
      }
    }
  }
}

.mt-4 { margin-top: 16px; }
.mr-2 { margin-right: 8px; }
.mr-3 { margin-right: 12px; }
.ml-2 { margin-left: 8px; }
.ml-3 { margin-left: 12px; }
</style>


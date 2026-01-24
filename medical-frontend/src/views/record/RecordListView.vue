<template>
  <div class="record">
    <el-card shadow="hover" class="record__card">
      <template #header>
        <div class="record__header">
          <div class="header-title">
            <el-icon class="mr-2"><Document /></el-icon>
            <span>健康档案管理</span>
          </div>
          <div class="header-actions">
            <el-input
              v-model="keyword"
              placeholder="搜索过敏史/既往史/备注"
              :prefix-icon="Search"
              clearable
              @keyup.enter="load"
              class="search-input"
            />
            <el-button type="primary" :icon="Plus" class="ml-3" @click="openEdit()">新建档案</el-button>
          </div>
        </div>
      </template>

      <el-table :data="data.records" border stripe v-loading="loading" highlight-current-row>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="allergies" label="过敏史" min-width="120" show-overflow-tooltip />
        <el-table-column prop="medicalHistory" label="既往病史" min-width="150" show-overflow-tooltip />
        <el-table-column prop="medicationHistory" label="用药史" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" :icon="Edit" @click="$router.push({ name: 'FullRecordDetail', params: { patientId: row.userId } })">详情/编辑</el-button>
            <el-button size="small" :icon="Paperclip" @click="openAttachments(row.id)">附件</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="record__pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
          :current-page="data.current"
          :page-size="data.size"
          :page-sizes="[10, 20, 50]"
          @current-change="(p:number)=>{pageNum=p;load();}"
          @size-change="(s:number)=>{pageSize=s;load();}"
        />
      </div>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑档案' : '新建档案'" width="640px" destroy-on-close>
      <el-form :model="form" label-width="100px" class="record-form">
        <el-form-item v-if="role !== 'PATIENT'" label="选择患者">
          <el-select v-model="form.userId" filterable remote reserve-keyword placeholder="输入姓名/用户名/手机号"
                     :remote-method="onRemoteSearch" :loading="remoteLoading" style="width: 100%">
            <el-option v-for="u in candidates" :key="u.id" :label="u.nickname || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input v-model="form.allergies" type="textarea" :rows="3" placeholder="无过敏史请填'无'" />
        </el-form-item>
        <el-form-item label="家族病史">
          <el-input v-model="form.familyHistory" type="textarea" :rows="3" placeholder="无家族病史请填'无'" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="form.medicalHistory" type="textarea" :rows="3" placeholder="无既往病史请填'无'" />
        </el-form-item>
        <el-form-item label="用药史">
          <el-input v-model="form.medicationHistory" type="textarea" :rows="3" placeholder="近期用药情况" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="其他补充说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="attachVisible" title="档案附件管理" width="720px" destroy-on-close>
      <div class="upload-area mb-4">
        <el-upload
          class="upload-demo"
          drag
          :action="uploadAction"
          :headers="uploadHeaders"
          :on-success="onUploadSuccess"
          :on-error="onUploadError"
          :before-upload="beforeUpload"
          :show-file-list="false"
          accept=".png,.jpg,.jpeg,.gif,.webp,.bmp,.svg,.pdf"
          :data="{ type: 'record' }"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持 jpg/png/pdf 文件，单个不超过 20MB
            </div>
          </template>
        </el-upload>
      </div>
      <el-table :data="attachments" border stripe height="300">
        <el-table-column prop="fileName" label="文件名" show-overflow-tooltip />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">
            {{ (row.fileSize / 1024).toFixed(1) }} KB
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link :href="row.fileUrl" target="_blank">预览</el-button>
            <el-button size="small" type="danger" link @click="removeAttachment(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { listRecords, createRecord, updateRecord, listAttachments, addAttachment, deleteAttachment, type PatientRecord, type PageResult, type RecordAttachment } from '@/api/modules/record';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/store/modules/auth';
import { searchPatients, type SimpleUser } from '@/api/modules/user';
import { Search, Plus, Edit, Paperclip, View, Document, UploadFilled } from '@element-plus/icons-vue';

const auth = useAuthStore();
const route = useRoute();
const role = auth.userInfo?.role || 'PATIENT';

const data = reactive<PageResult<PatientRecord>>({
  records: [],
  total: 0,
  current: 1,
  size: 10
});
const pageNum = ref(1);
const pageSize = ref(10);
const keyword = ref('');
const filterUserId = ref<number | undefined>(undefined);
const visible = ref(false);
const saving = ref(false);
const loading = ref(false);
const form = reactive<PatientRecord>({});

// doctor/admin patient selector
const candidates = ref<SimpleUser[]>([]);
const remoteLoading = ref(false);
const onRemoteSearch = async (kw: string) => {
  remoteLoading.value = true;
  try {
    candidates.value = await searchPatients(kw);
  } finally {
    remoteLoading.value = false;
  }
};

const load = async () => {
  loading.value = true;
  try {
    const res = await listRecords({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, userId: filterUserId.value });
    Object.assign(data, res);
  } finally {
    loading.value = false;
  }
};

const openEdit = (row?: PatientRecord) => {
  Object.assign(form, row || { allergies: '', familyHistory: '', medicalHistory: '', medicationHistory: '', remark: '' });
  if (!row) delete form.id;
  visible.value = true;
};

const save = async () => {
  saving.value = true;
  try {
    if (form.id) {
      await updateRecord(form.id, form);
    } else {
      await createRecord(form);
    }
    ElMessage.success('保存成功');
    visible.value = false;
    await load();
  } finally {
    saving.value = false;
  }
};

onMounted(load);
onMounted(() => {
  const uid = Number(route.query.userId);
  if (!Number.isNaN(uid) && uid > 0) {
    filterUserId.value = uid;
  }
  load();
});

// attachments
const attachVisible = ref(false);
const currentRecordId = ref<number | null>(null);
const attachments = ref<RecordAttachment[]>([]);
const uploadAction = '/api/v1/file/upload';
const uploadHeaders = computed(() => {
  const token = auth.token;
  return token ? { Authorization: `Bearer ${token}` } : {};
});
const MAX_UPLOAD_MB = 20;
const MAX_UPLOAD_BYTES = MAX_UPLOAD_MB * 1024 * 1024;
const openAttachments = async (recordId: number) => {
  currentRecordId.value = recordId;
  attachments.value = await listAttachments(recordId);
  attachVisible.value = true;
};
const onUploadSuccess = async (resp: any) => {
  try {
    if (resp && resp.code === 200 && currentRecordId.value) {
      const d = resp.data;
      await addAttachment(currentRecordId.value, {
        fileName: d.fileName,
        fileUrl: d.fileUrl,
        fileType: d.type,
        fileSize: d.fileSize
      });
      attachments.value = await listAttachments(currentRecordId.value);
      ElMessage.success('上传成功');
    } else {
      ElMessage.error('上传失败');
    }
  } catch {
    ElMessage.error('上传失败');
  }
};
const onUploadError = (err: any) => {
  const msg = err?.message || '';
  if (msg.includes('maximum permitted size') || msg.includes('size')) {
    ElMessage.error(`文件超过大小限制（<= ${MAX_UPLOAD_MB}MB）`);
  } else {
    ElMessage.error(`上传失败${msg ? `：${msg}` : ''}`);
  }
};
const beforeUpload = (file: File) => {
  if (file.size > MAX_UPLOAD_BYTES) {
    ElMessage.error(`文件超过大小限制（<= ${MAX_UPLOAD_MB}MB）`);
    return false;
  }
  return true;
};
const removeAttachment = async (id?: number) => {
  if (!id) return;
  await deleteAttachment(id);
  if (currentRecordId.value) {
    attachments.value = await listAttachments(currentRecordId.value);
  }
};
</script>

<style scoped lang="scss">
.record {
  &__card {
    border-radius: 8px;
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
        width: 240px;
      }
    }
  }

  &__pager {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}

.mr-2 { margin-right: 8px; }
.ml-3 { margin-left: 12px; }
.mb-4 { margin-bottom: 16px; }
</style>

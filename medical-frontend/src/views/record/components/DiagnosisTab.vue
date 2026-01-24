<template>
  <div class="diagnosis-tab">
    <div class="actions mb-4">
      <el-button type="primary" @click="openDialog()">添加诊断</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="diagnosisDate" label="诊断日期" width="120" />
      <el-table-column prop="diagnosisType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.diagnosisType === 'CONFIRMED' ? 'success' : 'warning'">
            {{ row.diagnosisType === 'CONFIRMED' ? '确诊' : '初步诊断' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="diseaseName" label="疾病名称" min-width="150" />
      <el-table-column prop="icd10Code" label="ICD-10" width="100" />
      <el-table-column prop="description" label="诊断描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑诊断' : '添加诊断'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="诊断类型">
          <el-radio-group v-model="form.diagnosisType">
            <el-radio label="PRELIMINARY">初步诊断</el-radio>
            <el-radio label="CONFIRMED">确诊</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="疾病名称">
          <el-input v-model="form.diseaseName" placeholder="请输入疾病名称" />
        </el-form-item>
        <el-form-item label="ICD-10">
          <el-input v-model="form.icd10Code" placeholder="可选" />
        </el-form-item>
        <el-form-item label="诊断日期">
          <el-date-picker v-model="form.diagnosisDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { diagnosisApi, type DiagnosisRecord } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps<{
  patientId: number;
}>();

const list = ref<DiagnosisRecord[]>([]);
const visible = ref(false);
const form = reactive<DiagnosisRecord>({ diagnosisType: 'PRELIMINARY', diseaseName: '', diagnosisDate: '' });

const load = async () => {
  if (!props.patientId) return;
  list.value = await diagnosisApi.list(props.patientId);
};

const openDialog = (row?: DiagnosisRecord) => {
  if (row) {
    Object.assign(form, row);
  } else {
    Object.assign(form, { 
      id: undefined, 
      patientId: props.patientId, 
      diagnosisType: 'PRELIMINARY', 
      diseaseName: '', 
      icd10Code: '', 
      description: '',
      diagnosisDate: dayjs().format('YYYY-MM-DD') 
    });
  }
  visible.value = true;
};

const save = async () => {
  try {
    if (form.id) {
      await diagnosisApi.update(form);
    } else {
      await diagnosisApi.create(form);
    }
    ElMessage.success('保存成功');
    visible.value = false;
    load();
  } catch (e) {
    // error
  }
};

const remove = async (id?: number) => {
  if (!id) return;
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' });
  await diagnosisApi.delete(id);
  ElMessage.success('删除成功');
  load();
};

onMounted(load);
</script>

<style scoped lang="scss">
.mb-4 { margin-bottom: 16px; }
</style>

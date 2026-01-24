<template>
  <div class="treatment-tab">
    <div class="actions mb-4">
      <el-button type="primary" @click="openDialog()">添加治疗方案</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="planName" label="方案名称" min-width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ statusMap[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="起止时间" width="200">
        <template #default="{ row }">
          {{ row.startDate }} ~ {{ row.endDate || '至今' }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="方案描述" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑治疗方案' : '添加治疗方案'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="方案名称">
          <el-input v-model="form.planName" placeholder="如：高血压常规治疗" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="val" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="可选" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细的治疗计划、用药说明等" />
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
import { treatmentPlanApi, type TreatmentPlan } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps<{
  patientId: number;
}>();

const statusMap: any = { ACTIVE: '进行中', COMPLETED: '已完成', TERMINATED: '已终止' };
const getStatusType = (status: string) => {
  if (status === 'ACTIVE') return 'success';
  if (status === 'COMPLETED') return 'info';
  return 'danger';
};

const list = ref<TreatmentPlan[]>([]);
const visible = ref(false);
const form = reactive<TreatmentPlan>({ planName: '', startDate: '', status: 'ACTIVE' });

const load = async () => {
  if (!props.patientId) return;
  list.value = await treatmentPlanApi.list(props.patientId);
};

const openDialog = (row?: TreatmentPlan) => {
  if (row) {
    Object.assign(form, row);
  } else {
    Object.assign(form, { 
      id: undefined, 
      patientId: props.patientId, 
      planName: '', 
      startDate: dayjs().format('YYYY-MM-DD'),
      status: 'ACTIVE',
      description: ''
    });
  }
  visible.value = true;
};

const save = async () => {
  try {
    if (form.id) {
      await treatmentPlanApi.update(form);
    } else {
      await treatmentPlanApi.create(form);
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
  await treatmentPlanApi.delete(id);
  ElMessage.success('删除成功');
  load();
};

onMounted(load);
</script>

<style scoped lang="scss">
.mb-4 { margin-bottom: 16px; }
</style>

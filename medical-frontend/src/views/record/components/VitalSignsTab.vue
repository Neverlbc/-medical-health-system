<template>
  <div class="vital-signs-tab">
    <div class="actions mb-4">
      <el-button type="primary" @click="openDialog()">添加记录</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="measureTime" label="测量时间" width="160" />
      <el-table-column label="血压 (mmHg)" width="120" align="center">
        <template #default="{ row }">
          <span v-if="row.systolic && row.diastolic">
            {{ row.systolic }}/{{ row.diastolic }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="heartRate" label="心率 (bpm)" width="100" align="center" />
      <el-table-column prop="temperature" label="体温 (℃)" width="100" align="center" />
      <el-table-column prop="spo2" label="血氧 (%)" width="100" align="center" />
      <el-table-column label="身高/体重" width="140" align="center">
        <template #default="{ row }">
          <div v-if="row.height">{{ row.height }} cm</div>
          <div v-if="row.weight">{{ row.weight }} kg</div>
        </template>
      </el-table-column>
      <el-table-column prop="bmi" label="BMI" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.bmi" :type="getBmiType(row.bmi)">{{ row.bmi }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑生命体征' : '添加生命体征'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压">
              <el-input-number v-model="form.systolic" :min="0" :max="300" placeholder="mmHg" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压">
              <el-input-number v-model="form.diastolic" :min="0" :max="200" placeholder="mmHg" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="心率">
              <el-input-number v-model="form.heartRate" :min="0" :max="250" placeholder="bpm" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体温">
              <el-input-number v-model="form.temperature" :min="30" :max="45" :precision="1" :step="0.1" placeholder="℃" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血氧">
              <el-input-number v-model="form.spo2" :min="0" :max="100" placeholder="%" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="呼吸频率">
              <el-input-number v-model="form.respiratoryRate" :min="0" :max="100" placeholder="次/分" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身高">
              <el-input-number v-model="form.height" :min="0" :max="300" :precision="1" placeholder="cm" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重">
              <el-input-number v-model="form.weight" :min="0" :max="500" :precision="1" placeholder="kg" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="测量时间">
          <el-date-picker v-model="form.measureTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
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
import { vitalSignsApi, type VitalSigns } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps<{
  patientId: number;
}>();

const list = ref<VitalSigns[]>([]);
const visible = ref(false);
const form = reactive<VitalSigns>({});

const load = async () => {
  if (!props.patientId) return;
  list.value = await vitalSignsApi.list(props.patientId);
};

const openDialog = (row?: VitalSigns) => {
  if (row) {
    Object.assign(form, row);
  } else {
    Object.assign(form, { 
      id: undefined, 
      patientId: props.patientId, 
      measureTime: dayjs().format('YYYY-MM-DD HH:mm:ss') 
    });
    // clear other fields
    delete form.systolic;
    delete form.diastolic;
    delete form.heartRate;
    delete form.temperature;
    delete form.spo2;
    delete form.respiratoryRate;
    delete form.height;
    delete form.weight;
  }
  visible.value = true;
};

const save = async () => {
  try {
    if (form.id) {
      await vitalSignsApi.update(form);
    } else {
      await vitalSignsApi.create(form);
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
  await vitalSignsApi.delete(id);
  ElMessage.success('删除成功');
  load();
};

const getBmiType = (bmi: number) => {
  if (bmi < 18.5) return 'info';
  if (bmi >= 24 && bmi < 28) return 'warning';
  if (bmi >= 28) return 'danger';
  return 'success';
};

onMounted(load);
</script>

<style scoped lang="scss">
.mb-4 { margin-bottom: 16px; }
</style>

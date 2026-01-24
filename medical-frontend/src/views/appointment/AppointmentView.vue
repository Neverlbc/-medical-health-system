<template>
  <div class="appointment">
    <el-card shadow="hover" class="appointment__card">
      <template #header>
        <div class="appointment__header">
          <div class="header-title">
            <el-icon class="mr-2"><Calendar /></el-icon>
            <span>预约挂号管理</span>
          </div>
          <el-button type="primary" :icon="Plus" @click="openDialog">创建预约</el-button>
        </div>
      </template>
      <el-table :data="appointments" border stripe highlight-current-row>
        <el-table-column prop="patientName" label="患者" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="24" class="mr-2">{{ row.patientName ? row.patientName.charAt(0) : 'U' }}</el-avatar>
              <span>{{ row.patientName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="doctor" label="医生" width="140">
          <template #default="{ row }">
            <div class="doctor-info">
              <el-avatar :size="24" :icon="UserFilled" class="mr-2" />
              <span>{{ row.doctorName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="科室">
          <template #default="{ row }">
            <el-tag size="small" effect="plain">{{ row.department }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="140">
          <template #default="{ row }">
            <el-icon class="mr-1"><Timer /></el-icon>
            <span>{{ row.appointmentDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="时段" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/records/full/${row.patientUserId}`)">详情</el-button>
            <el-button type="success" link size="small" @click="handleComplete(row.id)" v-if="row.status === 0 && isDoctor">完成</el-button>
            <el-button type="danger" link size="small" @click="handleCancel(row.id)" v-if="row.status === 0">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建预约" width="520px" destroy-on-close>
      <el-form :model="form" label-width="100px" class="appointment-form">
        <el-form-item label="就诊科室">
          <el-select v-model="form.department" placeholder="请选择科室" style="width: 100%" @change="handleDepartmentChange">
            <el-option
              v-for="dept in uniqueDepartments"
              :key="dept"
              :label="dept"
              :value="dept"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="form.doctorId" placeholder="请选择医生" style="width: 100%">
            <el-option
              v-for="doc in filteredDoctors"
              :key="doc.id"
              :label="doc.realName"
              :value="doc.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊日期">
          <el-date-picker v-model="form.appointmentDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预约时段">
          <el-select v-model="form.appointmentTime" placeholder="请选择时段" style="width: 100%">
            <el-option label="上午" value="上午" />
            <el-option label="下午" value="下午" />
          </el-select>
        </el-form-item>
        <el-form-item label="症状描述">
          <el-input v-model="form.symptoms" type="textarea" rows="3" placeholder="请简要描述您的症状" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue';
import { Calendar, Plus, UserFilled, Timer } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getDoctorList, type DoctorInfo } from '@/api/modules/doctor';
import { appointmentApi, type Appointment } from '@/api/modules/appointment';
import { ElMessageBox } from 'element-plus';
import { useAuthStore } from '@/store/modules/auth';

const authStore = useAuthStore();
const isDoctor = computed(() => authStore.userInfo?.role === 'DOCTOR');

const appointments = ref<Appointment[]>([]);
const doctorList = ref<DoctorInfo[]>([]);
const loading = ref(false);

const loadAppointments = async () => {
  loading.value = true;
  try {
    const res = await appointmentApi.list({ pageNum: 1, pageSize: 20 });
    appointments.value = res.records;
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  loadAppointments();
  try {
    const res = await getDoctorList();
    doctorList.value = res;
  } catch (error) {
    console.error('Failed to fetch doctor list:', error);
  }
});

const uniqueDepartments = computed(() => {
  const depts = new Set(doctorList.value.map(doc => doc.department));
  return Array.from(depts);
});

const filteredDoctors = computed(() => {
  if (!form.department) {
    return doctorList.value;
  }
  return doctorList.value.filter(doc => doc.department === form.department);
});

const handleDepartmentChange = () => {
  form.doctorId = undefined;
};

const dialogVisible = ref(false);
const form = reactive<Partial<Appointment>>({
  doctorId: undefined,
  department: '',
  appointmentDate: '',
  appointmentTime: '',
  symptoms: ''
});

const openDialog = () => {
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!form.doctorId || !form.appointmentDate || !form.appointmentTime) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  try {
    await appointmentApi.create(form as Appointment);
    ElMessage.success('预约创建成功');
    dialogVisible.value = false;
    loadAppointments();
  } catch (e) {
    // error
  }
};

const handleCancel = async (id?: number) => {
  if (!id) return;
  await ElMessageBox.confirm('确定取消该预约吗？', '提示', { type: 'warning' });
  await appointmentApi.cancel(id);
  ElMessage.success('取消成功');
  loadAppointments();
};

const handleComplete = async (id?: number) => {
  if (!id) return;
  await ElMessageBox.confirm('确定该次就诊已完成吗？', '提示', { type: 'success' });
  await appointmentApi.complete(id);
  ElMessage.success('操作成功');
  loadAppointments();
};

const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'primary';
    case 1: return 'success';
    case 2: return 'info';
    default: return '';
  }
};

const getStatusText = (status: number) => {
  const map: any = { 0: '待就诊', 1: '已完成', 2: '已取消' };
  return map[status] || '未知';
};
</script>

<style scoped lang="scss">
.appointment {
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
  }
}

.doctor-info {
  display: flex;
  align-items: center;
}

.mr-2 { margin-right: 8px; }
.mr-1 { margin-right: 4px; }
</style>


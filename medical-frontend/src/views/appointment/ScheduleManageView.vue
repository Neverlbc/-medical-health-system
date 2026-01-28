<template>
  <div class="schedule-manage">
    <!-- 顶部状态统计 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-header">总排班数</div>
          <div class="stat-value">{{ statistics.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-header">今日开诊</div>
          <div class="stat-value">{{ statistics.todayOpen }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orange">
          <div class="stat-header">预约已满</div>
          <div class="stat-value">{{ statistics.full }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card purple">
          <div class="stat-header">排班覆盖医生</div>
          <div class="stat-value">{{ statistics.doctorCount }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 过滤器 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="科室">
          <el-select v-model="queryParams.department" placeholder="所有科室" clearable @change="handleQuery">
            <el-option v-for="dept in departments" :key="dept" :label="dept" :value="dept" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="queryParams.doctorId" placeholder="选择医生" clearable filterable @change="handleQuery">
            <el-option v-for="doc in doctors" :key="doc.id" :label="doc.realName" :value="doc.id">
              <span style="float: left">{{ doc.realName }}</span>
              <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">{{ doc.department }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="所有状态" clearable @change="handleQuery">
            <el-option label="可预约" :value="1" />
            <el-option label="停诊" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增排班</el-button>
      <el-button type="success" :icon="'MagicStick'" @click="handleBatchAdd">批量排班</el-button>
      <el-button type="danger" :icon="'Delete'" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
      <el-button type="warning" :icon="'Refresh'" @click="handleAutoGenerate">一键生成下周排班</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="scheduleList"
      @selection-change="handleSelectionChange"
      style="width: 100%"
      class="custom-table"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="scheduleDate" label="日期" width="120" sortable />
      <el-table-column prop="timePeriod" label="时段" width="100">
        <template #default="{ row }">
          <el-tag :type="row.timePeriod === '上午' ? '' : 'warning'">{{ row.timePeriod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="doctorName" label="医生" width="120" />
      <el-table-column prop="department" label="科室" width="120" />
      <el-table-column label="挂号情况" width="180">
        <template #default="{ row }">
          <div class="slot-progress">
            <el-progress 
              :percentage="Math.min(100, (row.bookedPatients / row.maxPatients) * 100)" 
              :status="row.bookedPatients >= row.maxPatients ? 'exception' : ''"
              :stroke-width="8"
            />
            <span class="slot-text">{{ row.bookedPatients }} / {{ row.maxPatients }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" :disabled="row.bookedPatients > 0" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑排班' : '新增排班'"
      width="500px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="选择医生" filterable :disabled="isEdit">
            <el-option v-for="doc in doctors" :key="doc.id" :label="doc.realName" :value="doc.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="时段" prop="timePeriod">
          <el-radio-group v-model="form.timePeriod" :disabled="isEdit">
            <el-radio label="上午">上午</el-radio>
            <el-radio label="下午">下午</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="最大接诊数" prop="maxPatients">
          <el-input-number v-model="form.maxPatients" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">可预约</el-radio>
            <el-radio :label="0">停诊</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量创建弹窗 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量创建排班"
      width="600px"
    >
      <el-form :model="batchForm" label-width="100px" :rules="batchRules" ref="batchFormRef">
        <el-form-item label="选择医生" prop="doctorId">
          <el-select v-model="batchForm.doctorId" placeholder="选择医生" filterable>
            <el-option v-for="doc in doctors" :key="doc.id" :label="doc.realName" :value="doc.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="选择时段" prop="timePeriods">
          <el-checkbox-group v-model="batchForm.timePeriods">
            <el-checkbox label="上午">上午</el-checkbox>
            <el-checkbox label="下午">下午</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="最大接诊数" prop="maxPatients">
          <el-input-number v-model="batchForm.maxPatients" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <div class="batch-tips">
        <el-icon><InfoFilled /></el-icon>
        <span>系统将为所选医生在日期范围内自动生成对应时段的排班记录。如果某些时段已存在排班，则会跳过。</span>
      </div>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatchForm">开始生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { scheduleApi, type DoctorSchedule, type ScheduleQueryParams } from '@/api/modules/schedule';
import { getDoctorList, type DoctorInfo } from '@/api/modules/doctor';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import { Plus, MagicStick, Delete, Refresh, InfoFilled } from '@element-plus/icons-vue';

// 数据
const loading = ref(false);
const scheduleList = ref<DoctorSchedule[]>([]);
const doctors = ref<DoctorInfo[]>([]);
const departments = computed(() => [...new Set(doctors.value.map(d => d.department))]);
const dateRange = ref<[string, string] | null>(null);
const selectedIds = ref<number[]>([]);

const queryParams = reactive<ScheduleQueryParams>({
  doctorId: undefined,
  department: '',
  startDate: '',
  endDate: '',
  status: undefined
});

const statistics = reactive({
  total: 0,
  todayOpen: 0,
  full: 0,
  doctorCount: 0
});

// 表单相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();
const form = reactive<Partial<DoctorSchedule>>({
  id: undefined,
  doctorId: undefined,
  scheduleDate: '',
  timePeriod: '上午',
  maxPatients: 20,
  status: 1
});

const rules = {
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  timePeriod: [{ required: true, message: '请选择时段', trigger: 'change' }],
  maxPatients: [{ required: true, message: '请输入最大人数', trigger: 'blur' }]
};

// 批量表单
const batchDialogVisible = ref(false);
const batchFormRef = ref();
const batchForm = reactive({
  doctorId: undefined,
  dateRange: [] as string[],
  timePeriods: ['上午', '下午'],
  maxPatients: 20
});

const batchRules = {
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择日期范围', trigger: 'change' }],
  timePeriods: [{ type: 'array', required: true, message: '请至少选择一个时段', trigger: 'change' }]
};

// 方法
const fetchDoctors = async () => {
  try {
    const res = await getDoctorList();
    doctors.value = res;
  } catch (error) {
    console.error('Failed to fetch doctors', error);
  }
};

const fetchSchedules = async () => {
  loading.value = true;
  try {
    const res = await scheduleApi.list(queryParams);
    scheduleList.value = res;
    updateStatistics();
  } catch (error) {
    ElMessage.error('获取排班列表失败');
  } finally {
    loading.value = false;
  }
};

const updateStatistics = () => {
  const today = new Date().toISOString().split('T')[0];
  statistics.total = scheduleList.value.length;
  statistics.todayOpen = scheduleList.value.filter(s => s.scheduleDate === today && s.status === 1).length;
  statistics.full = scheduleList.value.filter(s => s.bookedPatients >= s.maxPatients).length;
  statistics.doctorCount = new Set(scheduleList.value.map(s => s.doctorId)).size;
};

const handleQuery = () => {
  fetchSchedules();
};

const resetQuery = () => {
  Object.assign(queryParams, {
    doctorId: undefined,
    department: '',
    startDate: '',
    endDate: '',
    status: undefined
  });
  dateRange.value = null;
  fetchSchedules();
};

const handleDateRangeChange = (val: [string, string] | null) => {
  if (val) {
    queryParams.startDate = val[0];
    queryParams.endDate = val[1];
  } else {
    queryParams.startDate = '';
    queryParams.endDate = '';
  }
  handleQuery();
};

const handleSelectionChange = (selection: DoctorSchedule[]) => {
  selectedIds.value = selection.map(item => item.id);
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(form, {
    id: undefined,
    doctorId: undefined,
    scheduleDate: '',
    timePeriod: '上午',
    maxPatients: 20,
    status: 1
  });
  dialogVisible.value = true;
};

const handleEdit = (row: DoctorSchedule) => {
  isEdit.value = true;
  Object.assign(form, {
    ...row
  });
  dialogVisible.value = true;
};

const handleDelete = (row: DoctorSchedule) => {
  ElMessageBox.confirm('确定要删除该排班吗？已有预约的排班无法删除。', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await scheduleApi.delete(row.id);
    ElMessage.success('删除成功');
    fetchSchedules();
  });
};

const handleStatusChange = async (row: DoctorSchedule) => {
  try {
    await scheduleApi.toggleStatus(row.id, row.status);
    ElMessage.success(`排班已${row.status === 1 ? '启用' : '停诊'}`);
  } catch (err) {
    row.status = row.status === 1 ? 0 : 1; // 恢复
  }
};

const handleBatchAdd = () => {
  Object.assign(batchForm, {
    doctorId: undefined,
    dateRange: [],
    timePeriods: ['上午', '下午'],
    maxPatients: 20
  });
  batchDialogVisible.value = true;
};

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要批量删除选中的 ${selectedIds.value.length} 条排班吗？已有预约的排班将不会被删除。`, '批量删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    const res = await scheduleApi.batchDelete(selectedIds.value);
    ElMessage.success(`成功删除 ${res} 条记录`);
    fetchSchedules();
  });
};

const handleAutoGenerate = async () => {
  ElMessageBox.confirm('系统将为所有在岗医生自动生成未来 7 天的默认排班（上午/下午各 20 个号），确定执行吗？', '智能生成', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    await scheduleApi.generate(7);
    ElMessage.success('生成任务已提交');
    fetchSchedules();
  });
};

const submitForm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      if (isEdit.value) {
        await scheduleApi.update(form.id!, form);
        ElMessage.success('修改成功');
      } else {
        await scheduleApi.create(form);
        ElMessage.success('添加成功');
      }
      dialogVisible.value = false;
      fetchSchedules();
    }
  });
};

const submitBatchForm = async () => {
  if (!batchFormRef.value) return;
  await batchFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const loadingInstance = ElLoading.service({ text: '正在生成排班...' });
      try {
        await scheduleApi.batchCreate({
          doctorId: batchForm.doctorId!,
          startDate: batchForm.dateRange[0],
          endDate: batchForm.dateRange[1],
          timePeriods: batchForm.timePeriods,
          maxPatients: batchForm.maxPatients
        });
        ElMessage.success('批量生成成功');
        batchDialogVisible.value = false;
        fetchSchedules();
      } catch (err) {
        ElMessage.error('生成排班失败');
      } finally {
        loadingInstance.close();
      }
    }
  });
};

onMounted(() => {
  fetchDoctors();
  fetchSchedules();
});
</script>

<style scoped lang="scss">
.schedule-manage {
  padding: 2px;

  .stat-cards {
    margin-bottom: 20px;
    
    .stat-card {
      border: none;
      border-radius: 12px;
      color: white;
      
      .stat-header {
        font-size: 14px;
        opacity: 0.9;
      }
      
      .stat-value {
        font-size: 32px;
        font-weight: bold;
        margin-top: 10px;
      }
      
      &.blue { background: linear-gradient(135deg, #409EFF 0%, #0072ff 100%); }
      &.green { background: linear-gradient(135deg, #67C23A 0%, #13ce66 100%); }
      &.orange { background: linear-gradient(135deg, #E6A23C 0%, #ffa500 100%); }
      &.purple { background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%); }
    }
  }

  .filter-card {
    margin-bottom: 20px;
    border-radius: 12px;
    background-color: #fcfcfc;
  }

  .action-bar {
    margin-bottom: 16px;
    display: flex;
    gap: 12px;
  }

  .custom-table {
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  }

  .slot-progress {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .slot-text {
      font-size: 12px;
      color: #909399;
    }
  }

  .batch-tips {
    margin-top: 20px;
    padding: 12px;
    background-color: #f0f9eb;
    border-radius: 4px;
    display: flex;
    gap: 8px;
    color: #67c23a;
    font-size: 13px;
    line-height: 1.5;
  }
}

:deep(.el-progress-bar__outer) {
  background-color: #ebeef5;
}

// 移动端适配
@media (max-width: 768px) {
  .schedule-manage {
    padding: 0 8px;
    
    .stat-cards {
      margin-bottom: 16px;
      
      :deep(.el-row) {
        display: grid !important;
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
      }
      
      :deep(.el-col) {
        max-width: 100% !important;
        flex: none !important;
        padding: 0 !important;
      }
      
      .stat-card {
        border-radius: 12px;
        
        :deep(.el-card__body) {
          padding: 14px 12px;
        }
        
        .stat-header { font-size: 11px; }
        .stat-value { font-size: 22px; margin-top: 4px; }
      }
    }
    
    .filter-card {
      border-radius: 12px;
      margin-bottom: 12px;
      
      :deep(.el-card__body) {
        padding: 12px;
      }
      
      :deep(.el-form) {
        display: flex;
        flex-direction: column;
        
        .el-form-item {
          margin-bottom: 12px;
          margin-right: 0;
          width: 100%;
          
          .el-form-item__label {
            width: 60px !important;
            font-size: 13px;
          }
          
          .el-form-item__content {
            flex: 1;
          }
        }
        
        .el-select,
        .el-date-editor {
          width: 100% !important;
        }
        
        // 按钮行
        .el-form-item:last-child {
          .el-form-item__content {
            display: flex;
            gap: 10px;
            
            .el-button {
              flex: 1;
              margin: 0;
            }
          }
        }
      }
    }
    
    .action-bar {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 8px;
      margin-bottom: 12px;
      
      .el-button {
        font-size: 11px;
        padding: 8px 10px;
        margin: 0 !important;
        
        .el-icon {
          margin-right: 4px;
        }
      }
    }
    
    .custom-table {
      border-radius: 12px;
      overflow: hidden;
      
      // 包裹一层使表格可以横向滚动
      :deep(.el-table) {
        display: block;
        overflow-x: auto;
        
        .el-table__header-wrapper,
        .el-table__body-wrapper {
          min-width: 650px;
        }
      }
    }
  }
  
  // 弹窗移动端
  :deep(.el-dialog) {
    width: 95% !important;
    max-width: 95% !important;
    margin: 16px auto !important;
    
    .el-dialog__header {
      padding: 16px;
    }
    
    .el-dialog__body {
      padding: 16px;
    }
    
    .el-dialog__footer {
      padding: 12px 16px;
    }
    
    .el-form-item {
      margin-bottom: 16px;
      
      .el-form-item__label {
        font-size: 13px;
      }
    }
    
    .el-select,
    .el-date-editor,
    .el-input-number {
      width: 100% !important;
    }
  }
  
  .batch-tips {
    font-size: 11px;
    padding: 10px;
    border-radius: 8px;
  }
}
</style>

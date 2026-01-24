<template>
  <div class="health-monitor-view">
    <!-- 上部：统计与趋势 -->
    <el-row :gutter="24" class="top-row">
      <el-col :span="18">
        <div class="trend-panel">
          <header class="panel-header">
            <div class="p-title">
              <el-icon><TrendCharts /></el-icon>
              <span>生理体征趋势分析</span>
            </div>
            <el-radio-group v-model="chartType" size="small" @change="handleChartTypeChange" class="modern-radio">
              <el-radio-button label="BLOOD_PRESSURE">血压</el-radio-button>
              <el-radio-button label="BLOOD_SUGAR">血糖</el-radio-button>
              <el-radio-button label="HEART_RATE">心率</el-radio-button>
              <el-radio-button label="TEMPERATURE">体温</el-radio-button>
              <el-radio-button label="WEIGHT">体重</el-radio-button>
            </el-radio-group>
          </header>
          <div class="chart-wrapper">
            <div class="chart-container" ref="chartRef"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stats-column">
          <div class="mini-stat-card normal">
            <div class="s-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="s-info">
              <div class="s-val">{{ statistics.normalCount || 0 }}</div>
              <div class="s-label">正常指标数</div>
            </div>
          </div>
          <div class="mini-stat-card warning">
            <div class="s-icon"><el-icon><Warning /></el-icon></div>
            <div class="s-info">
              <div class="s-val">{{ statistics.abnormalCount || 0 }}</div>
              <div class="s-label">异常预警数</div>
            </div>
          </div>
          <div class="mini-stat-card info">
            <div class="s-icon"><el-icon><DataLine /></el-icon></div>
            <div class="s-info">
              <div class="s-val">{{ statistics.totalCount || 0 }}</div>
              <div class="s-label">累计监测次数</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 下部：详细记录表格 -->
    <div class="data-table-panel">
      <header class="panel-header">
        <div class="p-title">
          <el-icon><List /></el-icon>
          <span>全量监测历史记录</span>
        </div>
        <div class="actions">
          <el-button type="success" :icon="Files" @click="handleBatchAdd" plain round>批量快录</el-button>
          <el-button type="primary" :icon="Plus" @click="handleAdd" round>新增单次记录</el-button>
        </div>
      </header>

      <div class="table-container">
        <el-table 
          v-loading="loading"
          :data="records" 
          style="width: 100%"
          header-cell-class-name="med-table-header"
          class="med-modern-table"
        >
          <el-table-column prop="dataType" label="监测类型" width="150">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.dataType)" effect="dark" class="type-tag">
                {{ getTypeName(row.dataType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="监测数值" min-width="150">
            <template #default="{ row }">
              <span class="val-text">{{ formatValue(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="measureTime" label="测量时间" width="200">
            <template #default="{ row }">
              <div class="time-cell">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTime(row.measureTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="分析结论" width="160">
            <template #default="{ row }">
              <div class="status-cell" :class="'status-' + row.status">
                <span class="dot"></span>
                {{ getStatusLabel(row) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="说明/表现" min-width="200" show-overflow-tooltip />
          <el-table-column label="指令" width="100" fixed="right" align="center">
            <template #default="{ row }">
              <el-popconfirm title="确定废弃此条临床记录？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button type="danger" link :icon="Delete">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-footer">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- Add Record Dialog -->
    <el-dialog v-model="dialogVisible" title="记录健康数据" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="health-form">
        <el-form-item label="数据类型" prop="dataType">
          <el-select v-model="form.dataType" placeholder="请选择" style="width: 100%" @change="handleDataTypeChange">
            <el-option label="血压" value="BLOOD_PRESSURE" />
            <el-option label="血糖" value="BLOOD_SUGAR" />
            <el-option label="心率" value="HEART_RATE" />
            <el-option label="体温" value="TEMPERATURE" />
            <el-option label="体重" value="WEIGHT" />
          </el-select>
        </el-form-item>
        
        <!-- Dynamic Input Fields based on Type -->
        <template v-if="form.dataType === 'BLOOD_PRESSURE'">
          <el-form-item label="收缩压" prop="systolicPressure">
            <el-input v-model.number="form.systolicPressure" placeholder="高压 (mmHg)">
              <template #append>mmHg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="舒张压" prop="diastolicPressure">
            <el-input v-model.number="form.diastolicPressure" placeholder="低压 (mmHg)">
              <template #append>mmHg</template>
            </el-input>
          </el-form-item>
        </template>
        
        <template v-else-if="form.dataType === 'BLOOD_SUGAR'">
          <el-form-item label="血糖值" prop="bloodSugar">
            <el-input v-model="form.bloodSugar" placeholder="mmol/L">
              <template #append>mmol/L</template>
            </el-input>
          </el-form-item>
        </template>

        <template v-else-if="form.dataType === 'HEART_RATE'">
          <el-form-item label="心率" prop="heartRate">
            <el-input v-model.number="form.heartRate" placeholder="次/分">
              <template #append>bpm</template>
            </el-input>
          </el-form-item>
        </template>

        <template v-else-if="form.dataType === 'TEMPERATURE'">
          <el-form-item label="体温" prop="temperature">
            <el-input v-model="form.temperature" placeholder="℃">
              <template #append>℃</template>
            </el-input>
          </el-form-item>
        </template>

        <template v-else-if="form.dataType === 'WEIGHT'">
          <el-form-item label="体重" prop="weight">
            <el-input v-model="form.weight" placeholder="kg">
              <template #append>kg</template>
            </el-input>
          </el-form-item>
        </template>

        <el-form-item label="测量时间" prop="measureTime">
          <el-date-picker
            v-model="form.measureTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择测量时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="如有特殊情况请备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存记录</el-button>
      </template>
    </el-dialog>

    <!-- Batch Add Dialog -->
    <el-dialog v-model="batchDialogVisible" title="批量录入健康数据" width="700px" destroy-on-close>
      <el-form ref="batchFormRef" :model="batchForm" label-width="100px" class="batch-form">
        <el-form-item label="测量时间" required>
          <el-date-picker
            v-model="batchForm.measureTime"
            type="datetime"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择测量时间"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-divider content-position="left">数据项</el-divider>
        
        <div class="batch-item">
          <div class="batch-item__header">
            <el-checkbox v-model="batchForm.bloodPressure.enabled">血压</el-checkbox>
          </div>
          <div class="batch-item__content" v-if="batchForm.bloodPressure.enabled">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="收缩压" prop="bloodPressure.systolicPressure" 
                  :rules="batchForm.bloodPressure.enabled ? [{ required: true, message: '请输入收缩压', trigger: 'blur' }] : []">
                  <el-input v-model.number="batchForm.bloodPressure.systolicPressure" placeholder="高压">
                    <template #append>mmHg</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="舒张压" prop="bloodPressure.diastolicPressure"
                  :rules="batchForm.bloodPressure.enabled ? [{ required: true, message: '请输入舒张压', trigger: 'blur' }] : []">
                  <el-input v-model.number="batchForm.bloodPressure.diastolicPressure" placeholder="低压">
                    <template #append>mmHg</template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <div class="batch-item">
          <div class="batch-item__header">
            <el-checkbox v-model="batchForm.bloodSugar.enabled">血糖</el-checkbox>
          </div>
          <div class="batch-item__content" v-if="batchForm.bloodSugar.enabled">
            <el-form-item label="血糖值" prop="bloodSugar.value"
              :rules="batchForm.bloodSugar.enabled ? [{ required: true, message: '请输入血糖值', trigger: 'blur' }] : []">
              <el-input v-model="batchForm.bloodSugar.value" placeholder="mmol/L">
                <template #append>mmol/L</template>
              </el-input>
            </el-form-item>
          </div>
        </div>

        <div class="batch-item">
          <div class="batch-item__header">
            <el-checkbox v-model="batchForm.heartRate.enabled">心率</el-checkbox>
          </div>
          <div class="batch-item__content" v-if="batchForm.heartRate.enabled">
            <el-form-item label="心率" prop="heartRate.value"
              :rules="batchForm.heartRate.enabled ? [{ required: true, message: '请输入心率', trigger: 'blur' }] : []">
              <el-input v-model.number="batchForm.heartRate.value" placeholder="次/分">
                <template #append>bpm</template>
              </el-input>
            </el-form-item>
          </div>
        </div>

        <div class="batch-item">
          <div class="batch-item__header">
            <el-checkbox v-model="batchForm.temperature.enabled">体温</el-checkbox>
          </div>
          <div class="batch-item__content" v-if="batchForm.temperature.enabled">
            <el-form-item label="体温" prop="temperature.value"
              :rules="batchForm.temperature.enabled ? [{ required: true, message: '请输入体温', trigger: 'blur' }] : []">
              <el-input v-model="batchForm.temperature.value" placeholder="℃">
                <template #append>℃</template>
              </el-input>
            </el-form-item>
          </div>
        </div>

        <div class="batch-item">
          <div class="batch-item__header">
            <el-checkbox v-model="batchForm.weight.enabled">体重</el-checkbox>
          </div>
          <div class="batch-item__content" v-if="batchForm.weight.enabled">
            <el-form-item label="体重" prop="weight.value"
              :rules="batchForm.weight.enabled ? [{ required: true, message: '请输入体重', trigger: 'blur' }] : []">
              <el-input v-model="batchForm.weight.value" placeholder="kg">
                <template #append>kg</template>
              </el-input>
            </el-form-item>
          </div>
        </div>

        <el-form-item label="备注">
          <el-input v-model="batchForm.remark" type="textarea" rows="2" placeholder="统一备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchSubmitLoading" @click="handleBatchSubmit">批量保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick, onUnmounted } from 'vue';
import { TrendCharts, List, Plus, Clock, Delete, Files, CircleCheck, Warning, DataLine } from '@element-plus/icons-vue';
import { ElMessage, type FormInstance } from 'element-plus';
import * as echarts from 'echarts';
import { 
  getHealthDataPage, 
  createHealthData, 
  deleteHealthData, 
  getHealthDataTrend,
  getHealthDataStatistics,
  type HealthData 
} from '@/api/modules/health';

// State
const loading = ref(false);
const submitLoading = ref(false);
const records = ref<HealthData[]>([]);
const total = ref(0);
const chartType = ref('BLOOD_PRESSURE');
const dialogVisible = ref(false);
const chartRef = ref<HTMLElement>();
let chartInstance: echarts.ECharts | null = null;
const formRef = ref<FormInstance>();
const statistics = ref({
  totalCount: 0,
  normalCount: 0,
  abnormalCount: 0
});

const queryParams = reactive({
  current: 1,
  size: 10,
  dataType: ''
});

const form = reactive<HealthData>({
  dataType: 'BLOOD_PRESSURE',
  measureTime: '',
  remark: ''
});

// Batch State
const batchDialogVisible = ref(false);
const batchSubmitLoading = ref(false);
const batchFormRef = ref<FormInstance>();
const batchForm = reactive({
  measureTime: '',
  remark: '',
  bloodPressure: { enabled: false, systolicPressure: undefined, diastolicPressure: undefined },
  bloodSugar: { enabled: false, value: undefined },
  heartRate: { enabled: false, value: undefined },
  temperature: { enabled: false, value: undefined },
  weight: { enabled: false, value: undefined }
});

// Validation Rules
const rules = {
  dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
  measureTime: [{ required: true, message: '请选择测量时间', trigger: 'change' }],
  systolicPressure: [{ required: true, message: '请输入收缩压', trigger: 'blur' }],
  diastolicPressure: [{ required: true, message: '请输入舒张压', trigger: 'blur' }],
  bloodSugar: [{ required: true, message: '请输入血糖值', trigger: 'blur' }],
  heartRate: [{ required: true, message: '请输入心率', trigger: 'blur' }],
  temperature: [{ required: true, message: '请输入体温', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入体重', trigger: 'blur' }]
};

// Lifecycle
onMounted(() => {
  fetchData();
  fetchStatistics();
  initChart();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose();
  }
  window.removeEventListener('resize', handleResize);
});

// Methods
const fetchData = async () => {
  loading.value = true;
  try {
    const res = await getHealthDataPage(queryParams);
    if (res) {
      records.value = res.records;
      total.value = res.total;
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const fetchStatistics = async () => {
  try {
    const res = await getHealthDataStatistics();
    console.log('统计数据返回:', JSON.stringify(res));
    console.log('totalCount:', res?.totalCount);
    console.log('abnormalCount:', res?.abnormalCount);
    console.log('recentCount:', res?.recentCount);
    
    if (res) {
      statistics.value = {
        totalCount: res.totalCount || 0,
        normalCount: (res.totalCount || 0) - (res.abnormalCount || 0),
        abnormalCount: res.abnormalCount || 0
      };
      console.log('统计数据更新后:', JSON.stringify(statistics.value));
    } else {
      console.warn('统计数据返回为空');
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

const fetchTrendData = async () => {
  if (!chartInstance) return;
  
  chartInstance.showLoading();
  try {
    const res = await getHealthDataTrend({
      dataType: chartType.value,
      days: 30
    });
    
    if (res) {
      updateChart(res);
    }
  } catch (error) {
    console.error(error);
  } finally {
    chartInstance.hideLoading();
  }
};

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    fetchTrendData();
  }
};

const handleResize = () => {
  chartInstance?.resize();
};

const updateChart = (data: HealthData[]) => {
  if (!chartInstance) return;

  const dates = data.map(item => formatTime(item.measureTime, 'MM-DD HH:mm'));
  let series = [];
  let yAxisName = '';

  switch (chartType.value) {
    case 'BLOOD_PRESSURE':
      series = [
        {
          name: '收缩压',
          type: 'line',
          data: data.map(item => item.systolicPressure),
          smooth: true,
          markLine: {
            data: [{ yAxis: 140, name: '高压警戒' }, { yAxis: 90, name: '低压警戒' }]
          }
        },
        {
          name: '舒张压',
          type: 'line',
          data: data.map(item => item.diastolicPressure),
          smooth: true,
          markLine: {
            data: [{ yAxis: 90, name: '高压警戒' }, { yAxis: 60, name: '低压警戒' }]
          }
        }
      ];
      yAxisName = 'mmHg';
      break;
    case 'BLOOD_SUGAR':
      series = [{
        name: '血糖',
        type: 'line',
        data: data.map(item => item.bloodSugar),
        smooth: true,
        areaStyle: { opacity: 0.1 },
        markLine: {
          data: [{ yAxis: 6.1, name: '高血糖' }, { yAxis: 3.9, name: '低血糖' }]
        }
      }];
      yAxisName = 'mmol/L';
      break;
    case 'HEART_RATE':
      series = [{
        name: '心率',
        type: 'line',
        data: data.map(item => item.heartRate),
        smooth: true,
        itemStyle: { color: '#F56C6C' },
        markLine: {
          data: [{ yAxis: 100, name: '过快' }, { yAxis: 60, name: '过慢' }]
        }
      }];
      yAxisName = 'bpm';
      break;
    case 'TEMPERATURE':
      series = [{
        name: '体温',
        type: 'line',
        data: data.map(item => item.temperature),
        smooth: true,
        itemStyle: { color: '#E6A23C' },
        markLine: {
          data: [{ yAxis: 37.3, name: '发热' }]
        }
      }];
      yAxisName = '℃';
      break;
    case 'WEIGHT':
      series = [{
        name: '体重',
        type: 'line',
        data: data.map(item => item.weight),
        smooth: true,
        itemStyle: { color: '#67C23A' }
      }];
      yAxisName = 'kg';
      break;
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: yAxisName
    },
    series: series
  };

  chartInstance.setOption(option, true);
};

const handleChartTypeChange = () => {
  fetchTrendData();
};

const handleAdd = () => {
  resetForm();
  form.measureTime = formatTime(new Date());
  dialogVisible.value = true;
};

const handleDataTypeChange = () => {
  // Clear specific fields when type changes
  form.systolicPressure = undefined;
  form.diastolicPressure = undefined;
  form.bloodSugar = undefined;
  form.heartRate = undefined;
  form.temperature = undefined;
  form.weight = undefined;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true;
      try {
        const res = await createHealthData(form);
        if (res) {
          ElMessage.success('记录保存成功');
          dialogVisible.value = false;
          fetchData();
          fetchStatistics();
          fetchTrendData(); // Refresh chart if needed
        }
      } catch (error) {
        console.error(error);
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

const handleDelete = async (id: number) => {
  try {
    const res = await deleteHealthData(id);
    if (res) {
      ElMessage.success('删除成功');
      fetchData();
      fetchStatistics();
      fetchTrendData();
    }
  } catch (error) {
    console.error(error);
  }
};

const handleSizeChange = (val: number) => {
  queryParams.size = val;
  fetchData();
};

const handleCurrentChange = (val: number) => {
  queryParams.current = val;
  fetchData();
};

const resetForm = () => {
  form.dataType = 'BLOOD_PRESSURE';
  form.systolicPressure = undefined;
  form.diastolicPressure = undefined;
  form.bloodSugar = undefined;
  form.heartRate = undefined;
  form.temperature = undefined;
  form.weight = undefined;
  form.remark = '';
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

const handleBatchAdd = () => {
  resetBatchForm();
  batchForm.measureTime = formatTime(new Date());
  batchDialogVisible.value = true;
};

const resetBatchForm = () => {
  batchForm.remark = '';
  batchForm.bloodPressure = { enabled: false, systolicPressure: undefined, diastolicPressure: undefined };
  batchForm.bloodSugar = { enabled: false, value: undefined };
  batchForm.heartRate = { enabled: false, value: undefined };
  batchForm.temperature = { enabled: false, value: undefined };
  batchForm.weight = { enabled: false, value: undefined };
  if (batchFormRef.value) {
    batchFormRef.value.resetFields();
  }
};

const handleBatchSubmit = async () => {
  if (!batchFormRef.value) return;
  
  await batchFormRef.value.validate(async (valid) => {
    if (valid) {
      // Check if at least one type is enabled
      if (!batchForm.bloodPressure.enabled && !batchForm.bloodSugar.enabled && 
          !batchForm.heartRate.enabled && !batchForm.temperature.enabled && !batchForm.weight.enabled) {
        ElMessage.warning('请至少选择一种数据类型');
        return;
      }

      batchSubmitLoading.value = true;
      const promises = [];

      if (batchForm.bloodPressure.enabled) {
        promises.push(createHealthData({
          dataType: 'BLOOD_PRESSURE',
          measureTime: batchForm.measureTime,
          remark: batchForm.remark,
          systolicPressure: batchForm.bloodPressure.systolicPressure,
          diastolicPressure: batchForm.bloodPressure.diastolicPressure
        }));
      }

      if (batchForm.bloodSugar.enabled) {
        promises.push(createHealthData({
          dataType: 'BLOOD_SUGAR',
          measureTime: batchForm.measureTime,
          remark: batchForm.remark,
          bloodSugar: batchForm.bloodSugar.value
        }));
      }

      if (batchForm.heartRate.enabled) {
        promises.push(createHealthData({
          dataType: 'HEART_RATE',
          measureTime: batchForm.measureTime,
          remark: batchForm.remark,
          heartRate: batchForm.heartRate.value
        }));
      }

      if (batchForm.temperature.enabled) {
        promises.push(createHealthData({
          dataType: 'TEMPERATURE',
          measureTime: batchForm.measureTime,
          remark: batchForm.remark,
          temperature: batchForm.temperature.value
        }));
      }

      if (batchForm.weight.enabled) {
        promises.push(createHealthData({
          dataType: 'WEIGHT',
          measureTime: batchForm.measureTime,
          remark: batchForm.remark,
          weight: batchForm.weight.value
        }));
      }

      try {
        await Promise.all(promises);
        ElMessage.success(`成功录入 ${promises.length} 条数据`);
        batchDialogVisible.value = false;
        fetchData();
        fetchStatistics();
        fetchTrendData();
      } catch (error) {
        console.error(error);
        ElMessage.error('部分数据录入失败，请重试');
      } finally {
        batchSubmitLoading.value = false;
      }
    }
  });
};

// Utils
const formatTime = (time: string | Date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!time) return '';
  const date = new Date(time);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  if (format === 'MM-DD HH:mm') {
    return `${month}-${day} ${hours}:${minutes}`;
  }
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const formatValue = (row: HealthData) => {
  switch (row.dataType) {
    case 'BLOOD_PRESSURE':
      return `${row.systolicPressure}/${row.diastolicPressure} mmHg`;
    case 'BLOOD_SUGAR':
      return `${row.bloodSugar} mmol/L`;
    case 'HEART_RATE':
      return `${row.heartRate} bpm`;
    case 'TEMPERATURE':
      return `${row.temperature} ℃`;
    case 'WEIGHT':
      return `${row.weight} kg`;
    default:
      return '-';
  }
};

const getTypeName = (type: string) => {
  const map: Record<string, string> = {
    'BLOOD_PRESSURE': '血压',
    'BLOOD_SUGAR': '血糖',
    'HEART_RATE': '心率',
    'TEMPERATURE': '体温',
    'WEIGHT': '体重'
  };
  return map[type] || type;
};

const getTypeTag = (type: string) => {
  const map: Record<string, string> = {
    'BLOOD_PRESSURE': 'danger',
    'BLOOD_SUGAR': 'warning',
    'HEART_RATE': 'danger',
    'TEMPERATURE': 'warning',
    'WEIGHT': 'success'
  };
  return map[type] || 'info';
};

const getStatusLabel = (row: HealthData) => {
  const { dataType, status } = row;
  
  if (dataType === 'BLOOD_PRESSURE') {
    switch (status) {
      case 0: return '正常';
      case 1: return '低血压';
      case 2: return '1级高血压';
      case 3: return '2级高血压';
      case 4: return '3级高血压/危险';
      default: return '未知';
    }
  } else if (dataType === 'BLOOD_SUGAR') {
    switch (status) {
      case 0: return '正常';
      case 1: return '低血糖';
      case 2: return '糖尿病前期';
      case 3: return '糖尿病';
      case 4: return '危险';
      default: return '未知';
    }
  } else if (dataType === 'HEART_RATE') {
    switch (status) {
      case 0: return '正常';
      case 1: return '轻度心动过缓';
      case 2: return '轻度心动过速';
      case 3: return '中度异常';
      case 4: return '危险';
      default: return '未知';
    }
  } else if (dataType === 'TEMPERATURE') {
    switch (status) {
      case 0: return '正常';
      case 1: return '低热或体温偏低';
      case 2: return '中度发热';
      case 3: return '高热';
      case 4: return '危险';
      default: return '未知';
    }
  } else if (dataType === 'WEIGHT') {
    switch (status) {
      case 0: return '正常';
      case 1: return '偏瘦';
      case 2: return '超重';
      case 3: return '肥胖';
      case 4: return '危险';
      default: return '正常';
    }
  }
  
  return '未知';
};

const getUnit = (type: string) => {
  const map: Record<string, string> = {
    'BLOOD_PRESSURE': 'mmHg',
    'BLOOD_SUGAR': 'mmol/L',
    'HEART_RATE': 'bpm',
    'TEMPERATURE': '°C',
    'WEIGHT': 'kg'
  };
  return map[type] || '';
};
</script>

<style scoped lang="scss">
.health-monitor-view {
  max-width: 1300px;
  margin: 0 auto;
}

// 通用面板头部
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .p-title {
    display: flex; align-items: center; gap: 10px;
    font-size: 18px; font-weight: 800; color: #1e293b;
    .el-icon { color: #2a64ff; font-size: 22px; }
  }
}

// 趋势面板
.trend-panel {
  background: #fff; border-radius: 24px; padding: 28px; border: 1px solid #edf2f7;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02); height: 480px; display: flex; flex-direction: column;
}
.chart-wrapper { flex: 1; min-height: 0; }
.chart-container { height: 100%; width: 100%; }

.modern-radio {
  :deep(.el-radio-button__inner) {
    background: #f8fafc; border: none; border-radius: 8px !important; margin: 0 4px; border: 1px solid #f1f5f9;
    color: #64748b; font-weight: 600; transition: 0.3s;
  }
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background: #2a64ff; color: #fff; box-shadow: 0 4px 12px rgba(42, 100, 255, 0.2); border-color: #2a64ff;
  }
}

// 统计侧栏
.stats-column { display: flex; flex-direction: column; gap: 20px; height: 100%; }
.mini-stat-card {
  flex: 1; background: #fff; border-radius: 20px; padding: 20px; border: 1px solid #edf2f7;
  display: flex; align-items: center; gap: 16px; transition: 0.3s;
  
  &:hover { transform: translateX(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.03); }

  .s-icon { width: 50px; height: 50px; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 22px; }
  .s-val { font-size: 26px; font-weight: 900; color: #1e293b; line-height: 1.2; }
  .s-label { font-size: 13px; color: #94a3b8; font-weight: 600; }

  &.normal { .s-icon { background: #f0fdf4; color: #16a34a; } }
  &.warning { .s-icon { background: #fff1f2; color: #e11d48; } }
  &.info { .s-icon { background: #eff6ff; color: #2563eb; } }
}

// 数据表格面板
.data-table-panel {
  background: #fff; border-radius: 24px; padding: 28px; border: 1px solid #edf2f7;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02); margin-top: 24px;
}

:deep(.med-table-header) {
  background-color: #f8fafc !important; color: #64748b !important; font-weight: 700 !important; font-size: 14px;
}

.med-modern-table {
  border-radius: 12px; overflow: hidden;
  :deep(.el-table__row) { transition: 0.2s; &:hover { background-color: #f8fafc !important; } }
}

.type-tag { border: none; font-weight: 700; border-radius: 6px; }
.val-text { font-family: 'Inter', sans-serif; font-weight: 700; color: #1e293b; font-size: 15px; }

.time-cell { display: flex; align-items: center; gap: 6px; color: #64748b; font-size: 13px; font-weight: 500; }

.status-cell {
  display: flex; align-items: center; gap: 8px; font-weight: 700; font-size: 13px;
  .dot { width: 6px; height: 6px; border-radius: 50%; }
  
  &.status-0 { color: #16a34a; .dot { background: #16a34a; box-shadow: 0 0 8px #16a34a; } }
  &.status-1 { color: #2563eb; .dot { background: #2563eb; } }
  &.status-2 { color: #ea580c; .dot { background: #ea580c; box-shadow: 0 0 8px #ea580c; } }
  &.status-3, &.status-4 { color: #e11d48; .dot { background: #e11d48; box-shadow: 0 0 8px #e11d48; animation: pulse-red 1.5s infinite; } }
}

@keyframes pulse-red { 0% { opacity: 1; } 50% { opacity: 0.4; } 100% { opacity: 1; } }

.pagination-footer { margin-top: 24px; display: flex; justify-content: flex-end; }

// 表单样式
.health-form :deep(.el-form-item__label) { font-weight: 700; color: #475569; }
.health-form :deep(.el-input__wrapper) { border-radius: 10px; background: #f8fafc; }

.batch-item {
  border: 1px solid #f1f5f9; border-radius: 16px; padding: 20px; background: #f8fafc; margin-bottom: 20px;
  &__header { margin-bottom: 15px; :deep(.el-checkbox__label) { font-weight: 800; color: #1e293b; } }
}

.mr-1 { margin-right: 4px; }
.mr-2 { margin-right: 8px; }
</style>

<template>
  <div class="data-analysis">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>📊 数据概览</span>
          <el-button :icon="Refresh" circle @click="refresh" :loading="loading" />
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-card blue">
            <div class="stat-value">{{ overview.totalPatients }}</div>
            <div class="stat-label">总患者数</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card green">
            <div class="stat-value">{{ overview.todayAppointments }}</div>
            <div class="stat-label">今日预约</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card orange">
            <div class="stat-value">{{ overview.totalDiagnoses }}</div>
            <div class="stat-label">累计诊疗</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📈 近7日预约趋势</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>🍰 疾病分布 Top 10</span>
            </div>
          </template>
          <div ref="diseaseChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { Refresh } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import { getOverview, getDiseaseDistribution, getAppointmentTrend, type OverviewVO } from '@/api/modules/statistics';

const loading = ref(false);
const overview = reactive<OverviewVO>({
  totalPatients: 0,
  todayAppointments: 0,
  totalDiagnoses: 0
});

const trendChartRef = ref<HTMLElement>();
const diseaseChartRef = ref<HTMLElement>();
let trendChart: echarts.ECharts | null = null;
let diseaseChart: echarts.ECharts | null = null;

const initCharts = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
  }
  if (diseaseChartRef.value) {
    diseaseChart = echarts.init(diseaseChartRef.value);
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    // 1. Overview
    const resOverview = await getOverview();
    Object.assign(overview, resOverview);

    // 2. Trend
    const resTrend = await getAppointmentTrend();
    if (trendChart) {
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: resTrend.map(i => i.date) },
        yAxis: { type: 'value' },
        series: [{
          data: resTrend.map(i => i.count),
          type: 'line',
          smooth: true,
          areaStyle: {}
        }]
      });
    }

    // 3. Disease
    const resDisease = await getDiseaseDistribution();
    if (diseaseChart) {
      diseaseChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '疾病分布',
          type: 'pie',
          radius: '50%',
          data: resDisease,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      });
    }

  } finally {
    loading.value = false;
  }
};

const refresh = () => {
  loadData();
};

onMounted(() => {
  initCharts();
  loadData();
  window.addEventListener('resize', () => {
    trendChart?.resize();
    diseaseChart?.resize();
  });
});
</script>

<style scoped lang="scss">
.data-analysis {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
  }

  .stat-card {
    padding: 20px;
    border-radius: 8px;
    color: white;
    text-align: center;
    
    &.blue { background: linear-gradient(135deg, #409eff, #36cfc9); }
    &.green { background: linear-gradient(135deg, #67c23a, #95d475); }
    &.orange { background: linear-gradient(135deg, #e6a23c, #f3d19e); }

    .stat-value {
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 8px;
    }
    .stat-label {
      font-size: 14px;
      opacity: 0.9;
    }
  }

  .chart-card {
    height: 400px;
    .chart-container {
      height: 340px;
    }
  }
  
  .mb-4 { margin-bottom: 20px; }
}

// 移动端适配
@media (max-width: 768px) {
  .data-analysis {
    :deep(.el-row) {
      flex-direction: column;
      
      .el-col {
        max-width: 100%;
        flex: 0 0 100%;
        margin-bottom: 12px;
      }
    }
    
    .stat-card {
      padding: 16px;
      border-radius: 12px;
      
      .stat-value {
        font-size: 26px;
      }
      
      .stat-label {
        font-size: 12px;
      }
    }
    
    .chart-card {
      height: auto;
      min-height: 300px;
      border-radius: 12px;
      
      :deep(.el-card__body) {
        padding: 12px;
      }
      
      .chart-container {
        height: 250px;
      }
    }
  }
}
</style>

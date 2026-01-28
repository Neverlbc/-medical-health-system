<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in summaryCards" :key="card.title">
        <el-card shadow="never" class="dashboard__summary">
          <div class="dashboard__summary-title">{{ card.title }}</div>
          <div class="dashboard__summary-value">{{ card.value }}</div>
          <div class="dashboard__summary-desc">{{ card.desc }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="dashboard__charts">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>近 7 天血压趋势</template>
          <div ref="bpChartRef" class="dashboard__chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>预约统计</template>
          <div ref="appointmentChartRef" class="dashboard__chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';

const summaryCards = [
  { title: '今日问诊', value: 12, desc: 'AI 问诊成功次数' },
  { title: '预约待就诊', value: 6, desc: '本周预约待处理' },
  { title: '健康评分', value: '86', desc: '综合健康指数' }
];

const bpChartRef = ref<HTMLDivElement>();
const appointmentChartRef = ref<HTMLDivElement>();

onMounted(() => {
  if (bpChartRef.value) {
    const chart = echarts.init(bpChartRef.value);
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['收缩压', '舒张压'] },
      xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
      yAxis: { type: 'value', min: 60, max: 160 },
      series: [
        { name: '收缩压', type: 'line', smooth: true, data: [125, 128, 130, 126, 132, 129, 127] },
        { name: '舒张压', type: 'line', smooth: true, data: [80, 82, 84, 79, 85, 83, 81] }
      ]
    });
  }

  if (appointmentChartRef.value) {
    const chart = echarts.init(appointmentChartRef.value);
    chart.setOption({
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          name: '预约状态',
          type: 'pie',
          radius: '60%',
          data: [
            { value: 10, name: '待就诊' },
            { value: 18, name: '已完成' },
            { value: 4, name: '已取消' }
          ]
        }
      ]
    });
  }
});
</script>

<style scoped lang="scss">
.dashboard {
  &__summary {
    margin-bottom: 16px;

    &-title {
      font-size: 14px;
      color: #666;
    }

    &-value {
      font-size: 28px;
      font-weight: 600;
      margin-top: 8px;
    }

    &-desc {
      margin-top: 4px;
      color: #909399;
    }
  }

  &__charts {
    margin-top: 8px;
  }

  &__chart {
    height: 320px;
  }
}

// 移动端适配
@media (max-width: 768px) {
  .dashboard {
    :deep(.el-row) {
      display: flex;
      flex-direction: column;
      
      .el-col {
        max-width: 100%;
        flex: 0 0 100%;
        padding: 0 !important;
        margin-bottom: 12px;
      }
    }
    
    &__summary {
      border-radius: 12px;
      
      :deep(.el-card__body) {
        padding: 16px;
      }
      
      &-title { font-size: 12px; }
      &-value { font-size: 24px; }
      &-desc { font-size: 12px; }
    }
    
    &__charts {
      margin-top: 0;
    }
    
    &__chart {
      height: 250px;
    }
  }
}
</style>


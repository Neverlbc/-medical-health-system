<template>
  <div class="health-analysis">
    <el-card shadow="hover" class="analysis-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon class="mr-2"><FirstAidKit /></el-icon>
            <span>全方位健康评估</span>
          </div>
          <el-button type="primary" :loading="loading" @click="analyze" :icon="DataAnalysis">
            开始评估
          </el-button>
        </div>
      </template>

      <div class="analysis-content">
        <el-alert
          title="AI 医生将分析您的全量健康数据（包括病史、近期检查、生命体征等），为您提供深度评估。"
          type="info"
          show-icon
          :closable="false"
          class="mb-4"
        />

        <div v-if="result" class="result-container">
          <div class="markdown-body" v-html="renderedResult"></div>
        </div>

        <div v-else-if="!loading" class="empty-state">
          <el-empty description="点击上方按钮开始评估" />
        </div>
        
        <div v-else class="loading-state">
          <el-skeleton :rows="10" animated />
        </div>
      </div>
      
      <div class="question-section mt-4" v-if="result">
        <el-input
          v-model="question"
          placeholder="基于以上评估，您还有什么想问的吗？"
          class="input-with-select"
          @keyup.enter="analyze"
        >
          <template #append>
            <el-button :loading="loading" @click="analyze">追问</el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { FirstAidKit, DataAnalysis } from '@element-plus/icons-vue';
import { aiApi } from '@/api/modules/ai';
import { marked } from 'marked';
import { ElMessage } from 'element-plus';

const loading = ref(false);
const result = ref('');
const question = ref('');

const renderedResult = computed(() => {
  return marked(result.value);
});

const analyze = async () => {
  loading.value = true;
  try {
    const res = await aiApi.analyzeHealth({
      question: question.value
    });
    result.value = res;
    question.value = ''; // Clear question after asking
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.health-analysis {
  .card-header {
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
  
  .result-container {
    background-color: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
    min-height: 200px;
  }
  
  .markdown-body {
    line-height: 1.6;
    color: #333;
    
    :deep(h3) {
      color: #409eff;
      margin-top: 20px;
      margin-bottom: 10px;
    }
    
    :deep(ul) {
      padding-left: 20px;
    }
    
    :deep(strong) {
      color: #303133;
    }
  }
}

.mt-4 { margin-top: 16px; }
.mb-4 { margin-bottom: 16px; }
.mr-2 { margin-right: 8px; }
</style>

<template>
  <div class="full-record">
    <!-- 顶部患者信息卡片 -->
    <el-card class="patient-info-card mb-4" shadow="hover">
      <div class="patient-header">
        <div class="avatar-area">
          <el-avatar :size="64" :src="patientInfo?.avatar || ''">{{ patientInfo?.realName?.charAt(0) }}</el-avatar>
        </div>
        <div class="info-area">
          <div class="name-row">
            <span class="name">{{ patientInfo?.realName || '未知患者' }}</span>
            <el-tag size="small" :type="patientInfo?.gender === 1 ? '' : 'danger'" class="ml-2">
              {{ patientInfo?.gender === 1 ? '男' : '女' }}
            </el-tag>
            <span class="age ml-2">{{ calculateAge(patientInfo?.birthday) }}岁</span>
          </div>
          <div class="detail-row mt-2">
            <span class="label">手机号：</span><span class="value mr-4">{{ patientInfo?.phone || '-' }}</span>
            <span class="label">身份证：</span><span class="value mr-4">{{ patientInfo?.idCard || '-' }}</span>
            <span class="label">建档时间：</span><span class="value">{{ formatDate(patientInfo?.createTime) }}</span>
          </div>
        </div>
        <div class="action-area">
          <el-button @click="$router.back()">返回列表</el-button>
        </div>
      </div>
    </el-card>

    <!-- 主要内容 Tabs -->
    <el-card class="record-tabs-card" shadow="hover">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="病史信息" name="history">
          <HistoryTab :patient-id="realPatientId" v-if="activeTab === 'history' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="生命体征" name="vital">
          <VitalSignsTab :patient-id="realPatientId" v-if="activeTab === 'vital' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="体格检查" name="physical">
          <PhysicalExamTab :patient-id="realPatientId" v-if="activeTab === 'physical' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="实验室检查" name="lab">
          <LabTestTab :patient-id="realPatientId" v-if="activeTab === 'lab' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="诊断记录" name="diagnosis">
          <DiagnosisTab :patient-id="realPatientId" v-if="activeTab === 'diagnosis' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="治疗方案" name="treatment">
          <TreatmentTab :patient-id="realPatientId" v-if="activeTab === 'treatment' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="随访记录" name="followup">
          <FollowUpTab :patient-id="realPatientId" v-if="activeTab === 'followup' && realPatientId" />
        </el-tab-pane>
        <el-tab-pane label="🤖 AI 辅助诊断" name="ai-diagnosis">
          <div class="ai-diagnosis-tab" v-if="activeTab === 'ai-diagnosis' && routeUserId">
            <!-- 免责声明 -->
            <div class="ai-disclaimer-box">
              <el-icon><Warning /></el-icon>
              <div class="disclaimer-content">
                <strong>⚠️ 重要提示</strong>
                <p>以下内容由 AI 基于患者健康档案自动生成，<strong>仅供诊疗参考</strong>，不能替代医生的专业判断。最终诊断请以临床检查和专业医师意见为准。</p>
              </div>
            </div>
            
            <!-- 生成按钮 -->
            <div class="ai-action-area" v-if="!aiAnalysisResult">
              <el-button type="primary" size="large" :loading="aiLoading" @click="generateAIDiagnosis">
                <el-icon v-if="!aiLoading"><MagicStick /></el-icon>
                {{ aiLoading ? 'AI 分析中，请稍候...' : '生成 AI 辅助诊断建议' }}
              </el-button>
              <p class="action-hint">点击按钮，AI 将综合分析患者的病史、检验结果和生命体征</p>
            </div>
            
            <!-- AI 分析结果 -->
            <div class="ai-result-area" v-if="aiAnalysisResult">
              <div class="result-header">
                <h3><el-icon><MagicStick /></el-icon> AI 辅助诊断建议</h3>
                <el-button link @click="aiAnalysisResult = ''">重新生成</el-button>
              </div>
              <div class="result-content" v-html="formatAIContent(aiAnalysisResult)"></div>
              <div class="result-footer">
                <el-tag type="warning" size="small">AI 生成内容 · 仅供参考</el-tag>
                <span class="generate-time">生成时间：{{ new Date().toLocaleString() }}</span>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getPatientInfoByUserId, type PatientInfo } from '@/api/modules/user';
import { aiApi } from '@/api/modules/ai';
import { ElMessage } from 'element-plus';
import { Warning, MagicStick } from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import HistoryTab from './components/HistoryTab.vue';
import VitalSignsTab from './components/VitalSignsTab.vue';
import DiagnosisTab from './components/DiagnosisTab.vue';
import PhysicalExamTab from './components/PhysicalExamTab.vue';
import LabTestTab from './components/LabTestTab.vue';
import TreatmentTab from './components/TreatmentTab.vue';
import FollowUpTab from './components/FollowUpTab.vue';

const route = useRoute();
const routeUserId = ref(Number(route.params.patientId) || 0);
const realPatientId = ref(0);
const activeTab = ref('history');
const patientInfo = ref<PatientInfo>({} as PatientInfo);

// AI 辅助诊断
const aiLoading = ref(false);
const aiAnalysisResult = ref('');

const loadPatientInfo = async () => {
  if (routeUserId.value) {
    try {
      const res = await getPatientInfoByUserId(routeUserId.value);
      if (res) {
        patientInfo.value = res;
        realPatientId.value = res.id;
      } else {
        ElMessage.warning('未找到该患者的详细档案信息');
      }
    } catch (e) {
      console.error(e);
    }
  }
};

const generateAIDiagnosis = async () => {
  if (!routeUserId.value) return;
  aiLoading.value = true;
  try {
    const result = await aiApi.analyzeHealth({
      patientId: routeUserId.value,
      question: '请综合分析该患者的健康档案，给出可能的诊断方向、需要关注的风险因素，以及进一步检查建议。'
    });
    aiAnalysisResult.value = result || '暂无分析结果';
  } catch (error) {
    ElMessage.error('AI 分析失败，请稍后重试');
  } finally {
    aiLoading.value = false;
  }
};

const formatAIContent = (content: string) => {
  return content
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>');
};

const calculateAge = (birthday?: string) => {
  if (!birthday) return '-';
  return dayjs().diff(dayjs(birthday), 'year');
};

const formatDate = (date?: string) => {
  if (!date) return '-';
  return dayjs(date).format('YYYY-MM-DD');
};

onMounted(() => {
  loadPatientInfo();
});
</script>

<style scoped lang="scss">
.full-record {
  .patient-info-card {
    .patient-header {
      display: flex;
      align-items: center;
      
      .avatar-area {
        margin-right: 20px;
      }
      
      .info-area {
        flex: 1;
        
        .name-row {
          display: flex;
          align-items: center;
          .name {
            font-size: 20px;
            font-weight: bold;
          }
          .age {
            color: #666;
          }
        }
        
        .detail-row {
          color: #666;
          font-size: 14px;
          .label {
            color: #999;
          }
          .value {
            color: #333;
          }
        }
      }
    }
  }
  
  .record-tabs-card {
    min-height: 500px;
  }
  
  .placeholder {
    padding: 40px;
    text-align: center;
    color: #999;
    font-size: 16px;
  }
}

.ml-2 { margin-left: 8px; }
.mt-2 { margin-top: 8px; }
.mr-4 { margin-right: 16px; }
.mb-4 { margin-bottom: 16px; }

// AI 辅助诊断 Tab 样式
.ai-diagnosis-tab {
  padding: 20px;
}

.ai-disclaimer-box {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 1px solid #f59e0b;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  
  .el-icon {
    font-size: 28px;
    color: #b45309;
    flex-shrink: 0;
  }
  
  .disclaimer-content {
    strong {
      color: #b45309;
      font-size: 15px;
    }
    
    p {
      margin: 8px 0 0;
      color: #78350f;
      font-size: 13px;
      line-height: 1.6;
    }
  }
}

.ai-action-area {
  text-align: center;
  padding: 60px 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px dashed #e2e8f0;
  
  .action-hint {
    margin-top: 16px;
    color: #64748b;
    font-size: 13px;
  }
}

.ai-result-area {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  
  .result-header {
    background: linear-gradient(135deg, #2a64ff 0%, #64dcff 100%);
    color: #fff;
    padding: 16px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
    }
    
    .el-button {
      color: rgba(255,255,255,0.8);
      
      &:hover {
        color: #fff;
      }
    }
  }
  
  .result-content {
    padding: 24px;
    font-size: 14px;
    line-height: 1.8;
    color: #334155;
    
    :deep(h2) {
      font-size: 18px;
      color: #1e40af;
      margin: 20px 0 12px;
      padding-left: 10px;
      border-left: 4px solid #2a64ff;
    }
    
    :deep(h3) {
      font-size: 15px;
      color: #1e293b;
      margin: 16px 0 8px;
    }
    
    :deep(strong) {
      color: #2a64ff;
    }
  }
  
  .result-footer {
    padding: 12px 20px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .generate-time {
      font-size: 12px;
      color: #94a3b8;
    }
  }
}
</style>

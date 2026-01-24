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
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getPatientInfoByUserId, type PatientInfo } from '@/api/modules/user';
import dayjs from 'dayjs';
import HistoryTab from './components/HistoryTab.vue';
import VitalSignsTab from './components/VitalSignsTab.vue';
import DiagnosisTab from './components/DiagnosisTab.vue';
import PhysicalExamTab from './components/PhysicalExamTab.vue';
import LabTestTab from './components/LabTestTab.vue';
import TreatmentTab from './components/TreatmentTab.vue';
import FollowUpTab from './components/FollowUpTab.vue';

const route = useRoute();
const routeUserId = ref(Number(route.params.patientId) || 0); // This is actually userId passed from list
const realPatientId = ref(0); // This is the ID from patient_info table
const activeTab = ref('history');
const patientInfo = ref<PatientInfo>({} as PatientInfo);

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
</style>

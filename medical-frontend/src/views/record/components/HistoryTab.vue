<template>
  <div class="history-tab">
    <!-- 过敏史部分 -->
    <div class="section mb-4">
      <div class="section-header">
        <span class="title">过敏史</span>
        <el-button type="primary" size="small" @click="openAllergyDialog()">添加过敏记录</el-button>
      </div>
      <el-table :data="allergies" border stripe size="small" class="mt-2">
        <el-table-column prop="allergen" label="过敏原" width="150" />
        <el-table-column prop="allergyType" label="类型" width="100">
          <template #default="{ row }">
            {{ allergyTypeMap[row.allergyType] || row.allergyType }}
          </template>
        </el-table-column>
        <el-table-column prop="reaction" label="过敏反应" />
        <el-table-column prop="severity" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getSeverityType(row.severity)">
              {{ severityMap[row.severity] || row.severity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openAllergyDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteAllergy(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 既往病史 -->
    <div class="section mb-4">
      <div class="section-header">
        <span class="title">既往病史</span>
        <el-button type="primary" size="small" @click="openHistoryDialog('PAST')">添加既往史</el-button>
      </div>
      <el-table :data="pastHistories" border stripe size="small" class="mt-2">
        <el-table-column prop="diseaseName" label="疾病名称" width="180" />
        <el-table-column prop="diagnosisDate" label="确诊日期" width="120" />
        <el-table-column prop="description" label="详细描述" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openHistoryDialog('PAST', row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteHistory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 家族病史 -->
    <div class="section mb-4">
      <div class="section-header">
        <span class="title">家族病史</span>
        <el-button type="primary" size="small" @click="openHistoryDialog('FAMILY')">添加家族史</el-button>
      </div>
      <el-table :data="familyHistories" border stripe size="small" class="mt-2">
        <el-table-column prop="diseaseName" label="疾病名称" width="180" />
        <el-table-column prop="relationship" label="亲属关系" width="120" />
        <el-table-column prop="description" label="详细描述" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openHistoryDialog('FAMILY', row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteHistory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 个人史 -->
    <div class="section mb-4">
      <div class="section-header">
        <span class="title">个人史</span>
        <el-button type="primary" size="small" @click="openHistoryDialog('PERSONAL')">添加个人史</el-button>
      </div>
      <el-table :data="personalHistories" border stripe size="small" class="mt-2">
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openHistoryDialog('PERSONAL', row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="deleteHistory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 过敏史弹窗 -->
    <el-dialog v-model="allergyVisible" :title="allergyForm.id ? '编辑过敏记录' : '添加过敏记录'" width="500px">
      <el-form :model="allergyForm" label-width="80px">
        <el-form-item label="过敏原">
          <el-input v-model="allergyForm.allergen" placeholder="如：青霉素、花生" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="allergyForm.allergyType" style="width: 100%">
            <el-option v-for="(label, val) in allergyTypeMap" :key="val" :label="label" :value="val" />
          </el-select>
        </el-form-item>
        <el-form-item label="反应">
          <el-input v-model="allergyForm.reaction" type="textarea" placeholder="过敏症状描述" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="allergyForm.severity" style="width: 100%">
            <el-option v-for="(label, val) in severityMap" :key="val" :label="label" :value="val" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="allergyVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAllergy">保存</el-button>
      </template>
    </el-dialog>

    <!-- 病史弹窗 -->
    <el-dialog v-model="historyVisible" :title="getHistoryTitle()" width="500px">
      <el-form :model="historyForm" label-width="80px">
        <el-form-item label="疾病名称" v-if="historyForm.historyType !== 'PERSONAL'">
          <el-input v-model="historyForm.diseaseName" />
        </el-form-item>
        <el-form-item label="确诊日期" v-if="historyForm.historyType === 'PAST'">
          <el-date-picker v-model="historyForm.diagnosisDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="亲属关系" v-if="historyForm.historyType === 'FAMILY'">
          <el-input v-model="historyForm.relationship" placeholder="如：父亲、母亲" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="historyForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="historyVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHistory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { allergyApi, historyApi, type PatientAllergy, type PatientHistory } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';

const props = defineProps<{
  patientId: number;
}>();

// 字典映射
const allergyTypeMap: any = { DRUG: '药物', FOOD: '食物', ENV: '环境', OTHER: '其他' };
const severityMap: any = { MILD: '轻微', MODERATE: '中度', SEVERE: '严重' };

const getSeverityType = (severity?: string) => {
  if (severity === 'SEVERE') return 'danger';
  if (severity === 'MODERATE') return 'warning';
  return 'info';
};

// 数据列表
const allergies = ref<PatientAllergy[]>([]);
const pastHistories = ref<PatientHistory[]>([]);
const familyHistories = ref<PatientHistory[]>([]);
const personalHistories = ref<PatientHistory[]>([]);

// 加载数据
const loadAll = async () => {
  if (!props.patientId) return;
  
  const [allergyRes, pastRes, familyRes, personalRes] = await Promise.all([
    allergyApi.list(props.patientId),
    historyApi.list(props.patientId, 'PAST'),
    historyApi.list(props.patientId, 'FAMILY'),
    historyApi.list(props.patientId, 'PERSONAL')
  ]);
  
  allergies.value = allergyRes;
  pastHistories.value = pastRes;
  familyHistories.value = familyRes;
  personalHistories.value = personalRes;
};

// --- 过敏史操作 ---
const allergyVisible = ref(false);
const allergyForm = reactive<PatientAllergy>({ allergen: '' });

const openAllergyDialog = (row?: PatientAllergy) => {
  if (row) {
    Object.assign(allergyForm, row);
  } else {
    Object.assign(allergyForm, { id: undefined, patientId: props.patientId, allergen: '', allergyType: 'DRUG', reaction: '', severity: 'MILD' });
  }
  allergyVisible.value = true;
};

const saveAllergy = async () => {
  try {
    if (allergyForm.id) {
      await allergyApi.update(allergyForm);
    } else {
      await allergyApi.create(allergyForm);
    }
    ElMessage.success('保存成功');
    allergyVisible.value = false;
    loadAll();
  } catch (e) {
    // error handled by interceptor
  }
};

const deleteAllergy = async (id?: number) => {
  if (!id) return;
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' });
  await allergyApi.delete(id);
  ElMessage.success('删除成功');
  loadAll();
};

// --- 病史操作 ---
const historyVisible = ref(false);
const historyForm = reactive<PatientHistory>({ historyType: 'PAST' });

const getHistoryTitle = () => {
  const map: any = { PAST: '既往史', FAMILY: '家族史', PERSONAL: '个人史' };
  return (historyForm.id ? '编辑' : '添加') + map[historyForm.historyType];
};

const openHistoryDialog = (type: 'PAST' | 'FAMILY' | 'PERSONAL', row?: PatientHistory) => {
  if (row) {
    Object.assign(historyForm, row);
  } else {
    Object.assign(historyForm, { id: undefined, patientId: props.patientId, historyType: type, diseaseName: '', description: '', diagnosisDate: '', relationship: '' });
  }
  historyVisible.value = true;
};

const saveHistory = async () => {
  try {
    if (historyForm.id) {
      await historyApi.update(historyForm);
    } else {
      await historyApi.create(historyForm);
    }
    ElMessage.success('保存成功');
    historyVisible.value = false;
    loadAll();
  } catch (e) {
    // error
  }
};

const deleteHistory = async (id?: number) => {
  if (!id) return;
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' });
  await historyApi.delete(id);
  ElMessage.success('删除成功');
  loadAll();
};

onMounted(loadAll);
</script>

<style scoped lang="scss">
.section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 8px;
    border-bottom: 1px solid #eee;
    
    .title {
      font-weight: 600;
      font-size: 15px;
      border-left: 4px solid #409eff;
      padding-left: 8px;
    }
  }
}
.mt-2 { margin-top: 8px; }
.mb-4 { margin-bottom: 16px; }
</style>

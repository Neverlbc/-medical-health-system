<template>
  <div class="physical-exam-tab">
    <div class="actions mb-4">
      <el-button type="primary" @click="openDialog()">添加体格检查</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="examDate" label="检查日期" width="120" />
      <el-table-column prop="generalCondition" label="一般情况" min-width="150" show-overflow-tooltip />
      <el-table-column prop="headNeck" label="头颈部" min-width="150" show-overflow-tooltip />
      <el-table-column prop="chestLungs" label="胸部/肺" min-width="150" show-overflow-tooltip />
      <el-table-column prop="heart" label="心脏" min-width="150" show-overflow-tooltip />
      <el-table-column prop="abdomen" label="腹部" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑体格检查' : '添加体格检查'" width="800px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="检查日期">
          <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="一般情况">
              <el-input v-model="form.generalCondition" type="textarea" :rows="2" placeholder="意识、精神、营养等" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="皮肤粘膜">
              <el-input v-model="form.skinMucosa" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="淋巴结">
              <el-input v-model="form.lymphNodes" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="头颈部">
              <el-input v-model="form.headNeck" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="胸部/肺">
              <el-input v-model="form.chestLungs" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="心脏">
              <el-input v-model="form.heart" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="腹部">
              <el-input v-model="form.abdomen" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="脊柱四肢">
              <el-input v-model="form.spineLimbs" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="神经系统">
          <el-input v-model="form.nervousSystem" type="textarea" :rows="2" />
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
import { physicalExamApi, type PhysicalExam } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps<{
  patientId: number;
}>();

const list = ref<PhysicalExam[]>([]);
const visible = ref(false);
const form = reactive<PhysicalExam>({ examDate: '' });

const load = async () => {
  if (!props.patientId) return;
  list.value = await physicalExamApi.list(props.patientId);
};

const openDialog = (row?: PhysicalExam) => {
  if (row) {
    Object.assign(form, row);
  } else {
    Object.assign(form, { 
      id: undefined, 
      patientId: props.patientId, 
      examDate: dayjs().format('YYYY-MM-DD'),
      generalCondition: '',
      skinMucosa: '',
      lymphNodes: '',
      headNeck: '',
      chestLungs: '',
      heart: '',
      abdomen: '',
      spineLimbs: '',
      nervousSystem: ''
    });
  }
  visible.value = true;
};

const save = async () => {
  try {
    if (form.id) {
      await physicalExamApi.update(form);
    } else {
      await physicalExamApi.create(form);
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
  await physicalExamApi.delete(id);
  ElMessage.success('删除成功');
  load();
};

onMounted(load);
</script>

<style scoped lang="scss">
.mb-4 { margin-bottom: 16px; }
</style>

<template>
  <div class="followup-tab">
    <div class="actions mb-4">
      <el-button type="primary" @click="openDialog()">添加随访记录</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="followUpDate" label="随访日期" width="120" />
      <el-table-column prop="method" label="方式" width="120" />
      <el-table-column prop="content" label="随访内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="result" label="随访结果" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="visible" :title="form.id ? '编辑随访记录' : '添加随访记录'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="随访日期">
          <el-date-picker v-model="form.followUpDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="方式">
          <el-select v-model="form.method" placeholder="请选择" style="width: 100%">
            <el-option label="门诊" value="OUTPATIENT" />
            <el-option label="电话" value="PHONE" />
            <el-option label="家访" value="HOME" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="随访内容" />
        </el-form-item>
        <el-form-item label="结果">
          <el-input v-model="form.result" type="textarea" :rows="3" placeholder="随访结果/病情变化" />
        </el-form-item>
        <el-form-item label="下次随访">
          <el-date-picker v-model="form.nextDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="可选" />
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
import { followUpApi, type FollowUp } from '@/api/modules/health-record-full';
import { ElMessage, ElMessageBox } from 'element-plus';
import dayjs from 'dayjs';

const props = defineProps<{
  patientId: number;
}>();

const list = ref<FollowUp[]>([]);
const visible = ref(false);
const form = reactive<FollowUp>({ followUpDate: '' });

const load = async () => {
  if (!props.patientId) return;
  list.value = await followUpApi.list(props.patientId);
};

const openDialog = (row?: FollowUp) => {
  if (row) {
    Object.assign(form, row);
  } else {
    Object.assign(form, {
      id: undefined,
      patientId: props.patientId,
      followUpDate: dayjs().format('YYYY-MM-DD'),
      method: undefined,
      content: '',
      result: '',
      nextDate: ''
    });
  }
  visible.value = true;
};

const save = async () => {
  try {
    if (form.id) {
      await followUpApi.update(form);
    } else {
      await followUpApi.create(form);
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
  await followUpApi.delete(id);
  ElMessage.success('删除成功');
  load();
};

onMounted(load);
</script>

<style scoped lang="scss">
.mb-4 { margin-bottom: 16px; }
</style>

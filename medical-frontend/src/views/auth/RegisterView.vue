<template>
  <div class="med-register-root" @mousemove="handleMouseMove">
    <!-- 背景 Canvas 神经元层 -->
    <canvas ref="particleCanvas" class="bg-canvas"></canvas>

    <!-- 有机生物细胞背景 (Bio-Cells) -->
    <div class="bio-cells">
      <div class="cell cell--1"></div>
      <div class="cell cell--2"></div>
      <div class="cell cell--3"></div>
    </div>

    <!-- 医疗辅助视觉层 -->
    <div class="med-visual-layer">
      <div class="med-grid"></div>
      

      <div class="dna-aside dna-aside--left">
        <div class="dna-double-helix">
          <div v-for="i in 25" :key="i" class="dna-segment" :style="`--off: ${i*0.2}s; --top: ${i*4}%;` ">
            <div class="dot dot--a"></div>
            <div class="line"></div>
            <div class="dot dot--b"></div>
          </div>
        </div>
      </div>

      <div class="dna-aside dna-aside--right">
        <div class="dna-double-helix">
          <div v-for="i in 25" :key="i" class="dna-segment" :style="`--off: ${i*0.2}s; --top: ${i*4}%;` ">
            <div class="dot dot--a"></div>
            <div class="line"></div>
            <div class="dot dot--b"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 注册面板 -->
    <main class="register-panel">
      <div class="card-container" v-show="isMounted">
        <div class="card-glass-effect"></div>
        
        <header class="panel-header">
          <h1 class="title">创建您的健康账户</h1>
          <p class="tagline">JOIN SMART MEDICAL · 开启数字化健康管理</p>
        </header>

        <el-form :model="form" :rules="rules" ref="formRef" class="register-form" label-position="left" label-width="80px" hide-required-asterisk>
          <el-form-item prop="username">
            <template #label><span class="form-label">账号</span></template>
            <el-input v-model="form.username" placeholder="4-20位用户名" class="med-input">
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <template #label><span class="form-label">密码</span></template>
            <el-input v-model="form.password" type="password" placeholder="6-20位安全密码" show-password class="med-input">
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <template #label><span class="form-label">确认密码</span></template>
            <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password class="med-input">
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="phone">
            <template #label><span class="form-label">手机号</span></template>
            <el-input v-model="form.phone" placeholder="接收系统通知用" class="med-input">
              <template #prefix><el-icon><Iphone /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="role">
            <template #label><span class="form-label">您的身份</span></template>
            <el-radio-group v-model="form.role" class="med-roles">
              <el-radio-button label="PATIENT">患者/个人</el-radio-button>
              <el-radio-button label="DOCTOR">医疗专家</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <button type="button" class="btn-submit" :disabled="loading" @click="handleRegister">
            <span v-if="!loading">立 即 申 请 注 册</span>
            <span v-else><el-icon class="is-loading"><Loading /></el-icon> 账户创建中...</span>
          </button>

          <footer class="panel-footer">
            <span>已有系统访问权限？</span>
            <span class="link-action" @click="goLogin">立即登录</span>
          </footer>
        </el-form>
      </div>
    </main>

    <!-- 底部声明 -->
    <div class="page-info-deco">
       Copyright © 2024 智慧医疗健康管理系统 · 您的隐私与健康全权保障
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onUnmounted } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import { register as registerApi } from '@/api/modules/auth';
import { User, Lock, Iphone, Loading } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();

const isMounted = ref(false);
const loading = ref(false);
const formRef = ref<FormInstance>();
const particleCanvas = ref<HTMLCanvasElement | null>(null);

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  email: '',
  role: 'PATIENT' as 'PATIENT' | 'DOCTOR'
});

const rules: FormRules<typeof form> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '长度4-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== form.password) callback(new Error('两次输入不一致'));
        else callback();
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '格式不正确', trigger: 'blur' }
  ]
};

// 交互粒子逻辑 (与登录页同步)
let rafId: number;
const mousePos = { x: -1000, y: -1000 };

const handleMouseMove = (e: MouseEvent) => {
  mousePos.x = e.clientX;
  mousePos.y = e.clientY;
};

const drawNodes = () => {
  const canvas = particleCanvas.value;
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  const updateSize = () => {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  };
  window.addEventListener('resize', updateSize);
  updateSize();

  const nodes: any[] = [];
  for (let i = 0; i < 60; i++) {
    nodes.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      vx: (Math.random() - 0.5) * 0.4,
      vy: (Math.random() - 0.5) * 0.4,
      r: Math.random() * 1.5 + 0.5,
      pulse: Math.random() * 10
    });
  }

  const render = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    nodes.forEach((node, i) => {
      node.x += node.vx; node.y += node.vy; node.pulse += 0.05;
      if (node.x < 0 || node.x > canvas.width) node.vx *= -1;
      if (node.y < 0 || node.y > canvas.height) node.vy *= -1;

      const dx = mousePos.x - node.x, dy = mousePos.y - node.y;
      const d = Math.sqrt(dx*dx + dy*dy);
      if (d < 180) { node.x += dx * 0.01; node.y += dy * 0.01; }

      for (let j = i + 1; j < nodes.length; j++) {
        const target = nodes[j];
        const tdx = node.x - target.x, tdy = node.y - target.y;
        const dist = Math.sqrt(tdx*tdx + tdy*tdy);
        if (dist < 120) {
          ctx.beginPath();
          ctx.lineWidth = 0.5;
          ctx.strokeStyle = `rgba(42, 100, 255, ${0.1 * (1 - dist/120)})`;
          ctx.moveTo(node.x, node.y); ctx.lineTo(target.x, target.y); ctx.stroke();
        }
      }
      ctx.beginPath();
      ctx.fillStyle = `rgba(42, 100, 255, ${0.15 + Math.sin(node.pulse)*0.05})`;
      ctx.arc(node.x, node.y, node.r, 0, Math.PI * 2); ctx.fill();
    });
    rafId = requestAnimationFrame(render);
  };
  render();
};

onMounted(() => {
  isMounted.value = true;
  drawNodes();
});

onUnmounted(() => {
  cancelAnimationFrame(rafId);
});

const handleRegister = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      await registerApi({
        username: form.username,
        password: form.password,
        phone: form.phone,
        email: form.email || undefined,
        role: form.role
      });
      ElMessage.success('注册成功，即将跳转登录页...');
      setTimeout(() => {
        const redirect = (route.query.redirect as string) || '/';
        router.replace({ name: 'Login', query: redirect ? { redirect } : undefined });
      }, 1500);
    } finally {
      loading.value = false;
    }
  });
};

const goLogin = () => {
  const redirect = (route.query.redirect as string) || undefined;
  router.push({ name: 'Login', query: redirect ? { redirect } : undefined });
};
</script>

<style scoped lang="scss">
.med-register-root {
  min-height: 100vh;
  background-color: #fcfdfe;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family: 'PingFang SC', sans-serif;
  color: #333;
}

.bg-canvas { position: absolute; inset: 0; z-index: 1; pointer-events: none; }

.bio-cells {
  position: absolute; inset: 0; z-index: 0; overflow: hidden;
  .cell {
    position: absolute; border-radius: 50%; filter: blur(100px); opacity: 0.12; animation: cell-float 35s ease-in-out infinite alternate;
    &--1 { width: 400px; height: 400px; background: #2a64ff; top: -10%; left: -5%; }
    &--2 { width: 350px; height: 350px; background: #409eff; bottom: 5%; right: -5%; }
    &--3 { width: 200px; height: 200px; background: #64dcff; top: 30%; right: 20%; }
  }
}

@keyframes cell-float {
  from { transform: translate(0, 0); }
  to { transform: translate(60px, 40px); }
}

.med-visual-layer {
  position: absolute; inset: 0; z-index: 2;
  .med-grid {
    position: absolute; inset: 0;
    background-image: 
      linear-gradient(rgba(42, 100, 255, 0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(42, 100, 255, 0.04) 1px, transparent 1px);
    background-size: 60px 60px;
  }
  .dna-aside {
    position: absolute; top: 0; width: 60px; height: 100%; opacity: 0.28;
    &--left { left: 5.5%; }
    &--right { right: 5.5%; }
    .dna-segment {
      position: absolute; width: 100%; height: 10px; top: var(--top); display: flex; align-items: center; justify-content: center;
      .dot {
        width: 8px; height: 8px; border-radius: 50%; background: #2a64ff; box-shadow: 0 0 12px rgba(42, 100, 255, 0.6);
        animation: helix-dot 3s ease-in-out infinite alternate; animation-delay: var(--off);
        &--b { animation-delay: calc(var(--off) - 1.5s); background: #64dcff; }
      }
      .line { flex: 1; height: 1.5px; background: rgba(42, 100, 255, 0.3); margin: 0 -4px; animation: helix-line 3s ease-in-out infinite alternate; animation-delay: var(--off); }
    }
  }
}

@keyframes helix-dot { 0% { transform: translateX(-22px) scale(0.8); opacity: 0.4; } 100% { transform: translateX(22px) scale(1.1); opacity: 1; } }
@keyframes helix-line { 0%, 100% { transform: scaleX(1); opacity: 0.2; } 50% { transform: scaleX(0.1); opacity: 0.7; } }

.register-panel {
  position: relative; z-index: 10; width: 100%; max-width: 500px; padding: 24px;
}

.card-container {
  position: relative; background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(50px) saturate(180%);
  padding: 50px 40px; border-radius: 34px; border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 0 15px 35px -5px rgba(0, 0, 0, 0.05), 0 40px 100px -20px rgba(42, 100, 255, 0.12);
}

.panel-header {
  text-align: center; margin-bottom: 40px;
  .title { font-size: 26px; font-weight: 800; color: #1a1a1a; margin: 0; letter-spacing: -0.5px; }
  .tagline { font-size: 12px; font-weight: 600; color: #8e8e93; margin-top: 8px; letter-spacing: 1px; }
}

.form-label { font-size: 14px; font-weight: 600; color: #4a4a4a; letter-spacing: 0.5px; }

:deep(.med-input) {
  .el-input__wrapper {
    background-color: #f7f9fc !important; border-radius: 14px; padding: 8px 16px;
    box-shadow: none !important; border: 1px solid rgba(42, 100, 255, 0.05) !important;
    &.is-focus { background-color: #fff !important; border-color: #2a64ff !important; box-shadow: 0 0 0 4px rgba(42, 100, 255, 0.08) !important; }
  }
}

.med-roles {
  width: 100%; display: flex;
  :deep(.el-radio-button) { flex: 1; }
  :deep(.el-radio-button__inner) { width: 100%; border-radius: 12px; border: none !important; background: #f7f9fc; color: #8e8e93; transition: 0.3s; }
  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) { background: #2a64ff; color: #fff; box-shadow: 0 4px 12px rgba(42, 100, 255, 0.3); }
}

.btn-submit {
  width: 100%; height: 58px; border: none; border-radius: 16px; background: linear-gradient(135deg, #409eff, #2a64ff);
  color: #fff; font-size: 16px; font-weight: 700; cursor: pointer; margin-top: 10px;
  box-shadow: 0 12px 28px -10px rgba(42, 100, 255, 0.5); transition: 0.4s;
  display: flex; align-items: center; justify-content: center; gap: 10px;
  &:hover { transform: translateY(-2px); box-shadow: 0 20px 35px -10px rgba(42, 100, 255, 0.4); filter: brightness(1.05); }
}

.panel-footer {
  margin-top: 35px; text-align: center; font-size: 14px; color: #a1a1a1;
  .link-action { color: #2a64ff; font-weight: 700; margin-left: 8px; cursor: pointer; &:hover { border-bottom: 2px solid; } }
}

.page-info-deco {
  position: absolute; bottom: 30px; width: 100%; text-align: center; font-size: 10px; color: #c7c7cc; letter-spacing: 1px; font-weight: 700;
}

@media (max-width: 480px) {
  .card-container { padding: 40px 20px; }
  .title { font-size: 22px; }
}
</style>


<template>
  <div class="med-login-root" @mousemove="handleMouseMove">
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
      <!-- 坐标网格 -->
      <div class="med-grid"></div>
      
      <!-- 心电图波形 -->
      <div class="med-ecg-container">
        <svg class="med-ecg-svg" viewBox="0 0 1200 200" preserveAspectRatio="none">
          <path class="med-ecg-line" d="M0,100 L150,100 L170,80 L190,120 L210,100 L350,100 L370,40 L390,160 L410,100 L550,100 L570,85 L590,115 L610,100 L750,100 L770,20 L790,180 L810,100 L950,100 L970,80 L990,120 L1010,100 L1200,100" />
        </svg>
      </div>

      <!-- DNA 双螺旋装饰 (左右对称) -->
      <div class="dna-aside dna-aside--left">
        <div class="dna-double-helix">
          <div v-for="i in 18" :key="i" class="dna-segment" :style="`--off: ${i*0.2}s; --top: ${i*5}%;` ">
            <div class="dot dot--a"></div>
            <div class="line"></div>
            <div class="dot dot--b"></div>
          </div>
        </div>
      </div>

      <div class="dna-aside dna-aside--right">
        <div class="dna-double-helix">
          <div v-for="i in 18" :key="i" class="dna-segment" :style="`--off: ${i*0.2}s; --top: ${i*5}%;` ">
            <div class="dot dot--a"></div>
            <div class="line"></div>
            <div class="dot dot--b"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录面板 -->
    <main class="login-panel">
      <div class="card-container" v-show="isMounted">
        <div class="card-glass-effect"></div>
        
        <header class="panel-header">
          <div class="brand-logo">
            <svg viewBox="0 0 100 100" class="hospital-cross hospital-cross--pulse">
              <rect x="40" y="20" width="20" height="60" rx="4" fill="#ff4d4f" />
              <rect x="20" y="40" width="60" height="20" rx="4" fill="#ff4d4f" />
              <circle cx="50" cy="50" r="45" fill="none" stroke="rgba(255, 77, 79, 0.2)" stroke-width="2" />
            </svg>
          </div>
          <h1 class="title">智慧医疗健康管理系统</h1>
          <p class="tagline">SMART MEDICAL HEALTH · 数字化诊疗保障</p>
        </header>

        <el-form :model="form" :rules="rules" ref="formRef" class="login-form" label-position="left" label-width="70px" hide-required-asterisk>
          <el-form-item prop="username">
            <template #label><span class="form-label">账号</span></template>
            <el-input v-model="form.username" placeholder="请输入手机号/工号" class="med-input">
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <template #label><span class="form-label">密码</span></template>
            <el-input v-model="form.password" type="password" placeholder="请输入登录密码" show-password class="med-input" @keyup.enter="handleLogin">
              <template #prefix><el-icon><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <div class="form-actions">
            <el-checkbox v-model="rememberMe">保持登录状态</el-checkbox>
          </div>

          <button type="button" class="btn-submit" :disabled="loading" @click="handleLogin">
            <span v-if="!loading">授 权 登 录</span>
            <span v-else><el-icon class="is-loading"><Loading /></el-icon> 数据核验中...</span>
          </button>

          <footer class="panel-footer">
            <span>暂无系统访问权限？</span>
            <span class="link-action" @click="goRegister">立即注册申请</span>
          </footer>
        </el-form>
      </div>
    </main>

    <!-- 底部版权声明 -->
    <div class="page-info-deco">
       Copyright © 2024 智慧医疗健康管理系统 · 让医疗更智能，让健康更触手可及
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, onUnmounted } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';
import { User, Lock, Loading } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const isMounted = ref(false);
const loading = ref(false);
const rememberMe = ref(true);
const formRef = ref<FormInstance>();
const particleCanvas = ref<HTMLCanvasElement | null>(null);

const form = reactive({
  username: 'admin',
  password: 'test123'
});

const rules: FormRules<typeof form> = {
  username: [{ required: true, message: '请提供认证号', trigger: 'blur' }],
  password: [{ required: true, message: '请提供安全码', trigger: 'blur' }]
};

// 交互粒子与神经连接逻辑
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
  const nodeCount = 70;

  for (let i = 0; i < nodeCount; i++) {
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
      node.x += node.vx;
      node.y += node.vy;
      node.pulse += 0.05;

      if (node.x < 0 || node.x > canvas.width) node.vx *= -1;
      if (node.y < 0 || node.y > canvas.height) node.vy *= -1;

      // 鼠标吸引
      const dx = mousePos.x - node.x;
      const dy = mousePos.y - node.y;
      const d = Math.sqrt(dx*dx + dy*dy);
      if (d < 200) {
        node.x += dx * 0.015;
        node.y += dy * 0.015;
      }

      // 绘制连线 (神经纤桥)
      for (let j = i + 1; j < nodes.length; j++) {
        const target = nodes[j];
        const ndx = node.x - target.x;
        const ndy = node.y - target.y;
        const dist = Math.sqrt(ndx*ndx + ndy*ndy);
        
        if (dist < 120) {
          ctx.beginPath();
          ctx.lineWidth = 0.5;
          ctx.strokeStyle = `rgba(42, 100, 255, ${0.1 * (1 - dist/120)})`;
          ctx.moveTo(node.x, node.y);
          ctx.lineTo(target.x, target.y);
          ctx.stroke();

          // 随机脉冲光点
          if (Math.sin(node.pulse) > 0.98) {
            const ratio = (Math.sin(node.pulse * 2) + 1) / 2;
            const px = node.x + (target.x - node.x) * ratio;
            const py = node.y + (target.y - node.y) * ratio;
            ctx.beginPath();
            ctx.fillStyle = 'rgba(42, 100, 255, 0.4)';
            ctx.arc(px, py, 1.2, 0, Math.PI * 2);
            ctx.fill();
          }
        }
      }

      // 绘制核心点
      ctx.beginPath();
      ctx.fillStyle = `rgba(42, 100, 255, ${0.15 + Math.sin(node.pulse)*0.05})`;
      ctx.arc(node.x, node.y, node.r, 0, Math.PI * 2);
      ctx.fill();
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

const handleLogin = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      await authStore.loginAction({ ...form });
      const redirect = (route.query.redirect as string) || '';
      if (redirect) {
        router.replace(redirect);
      } else {
        const role = authStore.userInfo?.role;
        if (role === 'PATIENT') {
          router.replace({ name: 'PatientDashboard' });
        } else {
          router.replace({ name: 'DoctorDashboard' });
        }
      }
    } finally {
      loading.value = false;
    }
  });
};

const goRegister = () => {
  const redirect = (route.query.redirect as string) || undefined;
  router.push({ name: 'Register', query: redirect ? { redirect } : undefined });
};
</script>

<style scoped lang="scss">
.med-login-root {
  min-height: 100vh;
  background-color: #fcfdfe;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family: 'PingFang SC', 'Source Han Sans CN', sans-serif;
  color: #333;
}

.bg-canvas {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

// 生物细胞背景
.bio-cells {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;

  .cell {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 0.12;
    animation: cell-float 30s ease-in-out infinite alternate;

    &--1 { width: 400px; height: 400px; background: #2a64ff; top: -10%; left: -5%; }
    &--2 { width: 350px; height: 350px; background: #409eff; bottom: 5%; right: -5%; animation-duration: 40s; }
    &--3 { width: 200px; height: 200px; background: #64dcff; top: 30%; right: 20%; animation-duration: 25s; }
  }
}

@keyframes cell-float {
  from { transform: translate(0, 0) scale(1); }
  to { transform: translate(50px, 30px) scale(1.1); }
}

// 可视化层
.med-visual-layer {
  position: absolute;
  inset: 0;
  z-index: 2;

  .med-grid {
    position: absolute;
    inset: 0;
    background-image: 
      linear-gradient(rgba(42, 100, 255, 0.04) 1px, transparent 1px),
      linear-gradient(90deg, rgba(42, 100, 255, 0.04) 1px, transparent 1px);
    background-size: 60px 60px;
  }

  .med-ecg-container {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 200%;
    height: 350px;
    opacity: 0.15;
    pointer-events: none;
    animation: ecg-container-pulse 3.5s ease-in-out infinite;

    .med-ecg-svg {
      width: 100%;
      height: 100%;
    }

    .med-ecg-line {
      fill: none;
      stroke: #2a64ff;
      stroke-width: 1.2;
      stroke-dasharray: 2000;
      stroke-dashoffset: 2000;
      filter: drop-shadow(0 0 5px rgba(42, 100, 255, 0.5));
      animation: svg-scan 7s linear infinite;
    }
  }

  .dna-aside {
    position: absolute;
    top: 38%;
    transform: translateY(-50%);
    width: 45px;
    height: 65%;
    opacity: 0.28;

    &--left { left: 5.5%; }
    &--right { right: 5.5%; }

    .dna-segment {
      position: absolute;
      width: 100%;
      height: 10px;
      top: var(--top);
      display: flex;
      align-items: center;
      justify-content: center;

      .dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background: #2a64ff;
        box-shadow: 0 0 12px rgba(42, 100, 255, 0.6);
        animation: helix-dot 3s ease-in-out infinite alternate;
        animation-delay: var(--off);

        &--b { animation-delay: calc(var(--off) - 1.5s); background: #64dcff; }
      }

      .line {
        flex: 1;
        height: 1.5px;
        background: rgba(42, 100, 255, 0.3);
        margin: 0 -4px;
        animation: helix-line 3s ease-in-out infinite alternate;
        animation-delay: var(--off);
      }
    }
  }
}

@keyframes svg-scan {
  0% { stroke-dashoffset: 2000; }
  100% { stroke-dashoffset: 0; }
}

@keyframes ecg-container-pulse {
  0%, 100% { opacity: 0.1; transform: scaleY(1); }
  50% { opacity: 0.25; transform: scaleY(1.08); }
}

@keyframes helix-dot {
  0% { transform: translateX(-25px) scale(0.85); opacity: 0.4; }
  100% { transform: translateX(25px) scale(1.15); opacity: 1; box-shadow: 0 0 15px rgba(42, 100, 255, 0.8); }
}

@keyframes helix-line {
  0%, 100% { transform: scaleX(1); opacity: 0.2; }
  50% { transform: scaleX(0.1); opacity: 0.7; }
}

// 登录面板
.login-panel {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 480px;
  padding: 24px;
}

.card-container {
  position: relative;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(50px) saturate(180%);
  padding: 55px 45px;
  border-radius: 34px;
  border: 1px solid rgba(255, 255, 255, 1);
  box-shadow: 
    0 15px 35px -5px rgba(0, 0, 0, 0.05),
    0 40px 100px -20px rgba(42, 100, 255, 0.12);
}

.panel-header {
  text-align: center;
  margin-bottom: 50px;

  .hospital-cross {
    width: 76px;
    height: 76px;
    margin-bottom: 24px;
    
    &--pulse {
      animation: cross-heartbeat 2.5s ease-in-out infinite;
    }
  }

  .title {
    font-size: 28px;
    font-weight: 800;
    color: #1a1a1a;
    margin: 0;
    letter-spacing: -0.5px;
  }

  .tagline {
    font-size: 13px;
    font-weight: 600;
    color: #8e8e93;
    margin-top: 12px;
    letter-spacing: 1.5px;
  }
}

@keyframes cross-heartbeat {
  0%, 100% { transform: scale(1); filter: drop-shadow(0 0 5px rgba(255, 77, 79, 0.2)); }
  50% { transform: scale(1.05); filter: drop-shadow(0 0 20px rgba(255, 77, 79, 0.4)); }
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #4a4a4a;
  letter-spacing: 1px;
  display: inline-block;
  vertical-align: middle;
}

:deep(.el-form-item) {
  margin-bottom: 28px;
  display: flex;
  align-items: center;
}

:deep(.med-input) {
  .el-input__wrapper {
    background-color: #f7f9fc !important;
    border-radius: 15px;
    padding: 10px 18px;
    box-shadow: none !important;
    border: 1px solid rgba(42, 100, 255, 0.05) !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &.is-focus {
      background-color: #fff !important;
      border-color: #2a64ff !important;
      box-shadow: 0 0 0 4px rgba(42, 100, 255, 0.08) !important;
    }
  }
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin: 15px 4px 45px;
  font-size: 14px;
}

.btn-submit {
  width: 100%;
  height: 62px;
  border: none;
  border-radius: 18px;
  background: linear-gradient(135deg, #409eff, #2a64ff);
  color: #fff;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 12px 28px -10px rgba(42, 100, 255, 0.5);
  transition: all 0.4s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 22px 35px -10px rgba(42, 100, 255, 0.4);
    filter: brightness(1.05);
  }

  &:active { transform: translateY(-1px); }
  &:disabled { opacity: 0.8; cursor: not-allowed; }
}

.panel-footer {
  margin-top: 40px;
  text-align: center;
  font-size: 14px;
  color: #a1a1a1;

  .link-action {
    color: #2a64ff;
    font-weight: 700;
    margin-left: 8px;
    cursor: pointer;
    transition: 0.2s;
    &:hover { border-bottom: 2px solid; }
  }
}

.page-info-deco {
  position: absolute;
  bottom: 35px;
  width: 100%;
  text-align: center;
  font-size: 10px;
  color: #c7c7cc;
  letter-spacing: 2px;
  font-weight: 700;
  display: flex;
  justify-content: center;
  gap: 40px;
}

@media (max-width: 480px) {
  .card-container { padding: 40px 25px; }
  .title { font-size: 24px; }
}
</style>







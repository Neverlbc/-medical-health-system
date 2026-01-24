<template>
  <div class="not-found">
    <div class="content">
      <div class="error-code">404</div>
      <div class="error-desc">抱歉，您访问的页面不存在或已被移除</div>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
        <el-button size="large" @click="$router.back()">返回上一页</el-button>
      </div>
    </div>
    <div class="decoration">
      <!-- 简单的装饰背景圆 -->
      <div class="circle c1"></div>
      <div class="circle c2"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.replace('/');
};
</script>

<style scoped lang="scss">
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  position: relative;
  overflow: hidden;

  .content {
    text-align: center;
    z-index: 10;
    padding: 40px;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    
    .error-code {
      font-size: 120px;
      font-weight: 700;
      color: #409eff;
      line-height: 1.2;
      text-shadow: 4px 4px 0px rgba(64, 158, 255, 0.2);
    }

    .error-desc {
      font-size: 18px;
      color: #606266;
      margin: 16px 0 32px;
    }

    .actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }
  }

  .decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
    pointer-events: none;

    .circle {
      position: absolute;
      border-radius: 50%;
      opacity: 0.6;
    }

    .c1 {
      width: 300px;
      height: 300px;
      background: linear-gradient(135deg, #a0cfff, #ecf5ff);
      top: -50px;
      right: -50px;
    }

    .c2 {
      width: 400px;
      height: 400px;
      background: linear-gradient(135deg, #d9ecff, #f0f9eb);
      bottom: -100px;
      left: -100px;
    }
  }
}
</style>


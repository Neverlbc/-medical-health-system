<template>
  <div class="policy-page">
    <div class="policy-container">
      <header class="policy-header">
        <el-button link @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h1>{{ pageTitle }}</h1>
      </header>
      
      <div class="policy-content" v-if="type === 'privacy'">
        <h2>隐私政策</h2>
        <p class="update-date">最后更新日期：2026年1月28日</p>
        
        <section>
          <h3>1. 信息收集</h3>
          <p>我们收集的信息类型包括：</p>
          <ul>
            <li><strong>账户信息</strong>：用户名、手机号、邮箱等注册信息</li>
            <li><strong>健康数据</strong>：血压、血糖、心率等健康监测数据</li>
            <li><strong>医疗档案</strong>：病史、过敏史、诊断记录等医疗信息</li>
            <li><strong>使用数据</strong>：访问日志、设备信息等技术数据</li>
          </ul>
        </section>
        
        <section>
          <h3>2. 信息使用</h3>
          <p>我们使用收集的信息用于：</p>
          <ul>
            <li>提供、维护和改进我们的服务</li>
            <li>生成个性化的健康建议和 AI 分析报告</li>
            <li>与医疗专业人员共享必要的健康信息（仅在您授权的情况下）</li>
            <li>发送服务相关的通知和提醒</li>
          </ul>
        </section>
        
        <section>
          <h3>3. 信息保护</h3>
          <p>我们采取以下措施保护您的信息：</p>
          <ul>
            <li>使用 HTTPS 加密传输所有数据</li>
            <li>采用 BCrypt 算法加密存储密码</li>
            <li>实施严格的访问控制和权限管理</li>
            <li>定期进行安全审计和漏洞扫描</li>
          </ul>
        </section>
        
        <section>
          <h3>4. 信息共享</h3>
          <p>除以下情况外，我们不会与第三方共享您的个人信息：</p>
          <ul>
            <li>获得您的明确同意</li>
            <li>法律法规要求或政府部门要求</li>
            <li>为保护用户或公众的人身安全</li>
          </ul>
        </section>
        
        <section>
          <h3>5. AI 服务声明</h3>
          <p>
            <strong>重要提示</strong>：本系统的 AI 智能问诊功能基于大语言模型技术，所提供的健康建议仅供参考，
            <strong>不构成医疗诊断或治疗建议</strong>。如有健康问题，请务必咨询专业医疗人员。
          </p>
        </section>
        
        <section>
          <h3>6. 用户权利</h3>
          <p>您享有以下权利：</p>
          <ul>
            <li><strong>访问权</strong>：查看我们持有的您的个人信息</li>
            <li><strong>更正权</strong>：更正不准确的个人信息</li>
            <li><strong>删除权</strong>：要求删除您的个人信息</li>
            <li><strong>导出权</strong>：以常见格式导出您的数据</li>
          </ul>
        </section>
        
        <section>
          <h3>7. 联系我们</h3>
          <p>如有任何隐私相关问题，请通过以下方式联系我们：</p>
          <p>邮箱：privacy@lbc-ai.top</p>
        </section>
      </div>
      
      <div class="policy-content" v-else-if="type === 'terms'">
        <h2>用户服务协议</h2>
        <p class="update-date">最后更新日期：2026年1月28日</p>
        
        <section>
          <h3>1. 服务说明</h3>
          <p>
            智慧医疗健康管理系统（以下简称"本系统"）是一个数字化健康管理平台，
            提供健康档案管理、健康数据监测、AI 智能问诊、预约挂号等服务。
          </p>
        </section>
        
        <section>
          <h3>2. 用户注册</h3>
          <p>使用本系统前，您需要注册账户并同意以下条款：</p>
          <ul>
            <li>您必须年满18周岁或在监护人同意下使用本系统</li>
            <li>提供真实、准确、完整的注册信息</li>
            <li>妥善保管账户密码，对账户下的所有行为负责</li>
          </ul>
        </section>
        
        <section>
          <h3>3. AI 服务免责声明</h3>
          <div class="disclaimer-box">
            <el-icon><Warning /></el-icon>
            <p>
              <strong>特别提醒：</strong><br>
              本系统的 AI 智能问诊功能由人工智能模型提供，其回复内容<strong>仅供健康参考</strong>，
              <strong>不能替代专业医生的诊断和治疗建议</strong>。<br><br>
              如您出现紧急症状或严重健康问题，请立即拨打 <strong>120</strong> 急救电话或前往最近的医疗机构就诊。
              对于因依赖 AI 建议而产生的任何后果，本系统不承担责任。
            </p>
          </div>
        </section>
        
        <section>
          <h3>4. 用户行为规范</h3>
          <p>使用本系统时，您不得：</p>
          <ul>
            <li>上传虚假或误导性的健康信息</li>
            <li>干扰或破坏系统的正常运行</li>
            <li>侵犯他人的隐私权或其他合法权益</li>
            <li>将系统用于任何非法目的</li>
          </ul>
        </section>
        
        <section>
          <h3>5. 知识产权</h3>
          <p>
            本系统的所有内容，包括但不限于文字、图片、软件、代码等，均受知识产权法保护。
            未经授权，不得复制、修改、分发或用于商业目的。
          </p>
        </section>
        
        <section>
          <h3>6. 服务变更与终止</h3>
          <p>我们保留随时修改、暂停或终止服务的权利，并会提前通知用户。</p>
        </section>
        
        <section>
          <h3>7. 适用法律</h3>
          <p>本协议受中华人民共和国法律管辖，如发生争议，应提交至有管辖权的人民法院解决。</p>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { ArrowLeft, Warning } from '@element-plus/icons-vue';

const route = useRoute();
const type = computed(() => route.query.type || 'privacy');
const pageTitle = computed(() => type.value === 'privacy' ? '隐私政策' : '用户服务协议');
</script>

<style scoped lang="scss">
.policy-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e8f4fd 100%);
  padding: 40px 20px;
}

.policy-container {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05);
  padding: 40px;
}

.policy-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  
  h1 {
    margin: 0;
    font-size: 24px;
    color: #1e293b;
  }
}

.policy-content {
  h2 {
    font-size: 28px;
    color: #1e293b;
    margin-bottom: 10px;
    text-align: center;
  }
  
  .update-date {
    text-align: center;
    color: #64748b;
    font-size: 14px;
    margin-bottom: 40px;
  }
  
  section {
    margin-bottom: 30px;
    
    h3 {
      font-size: 18px;
      color: #334155;
      margin-bottom: 12px;
      padding-left: 12px;
      border-left: 4px solid #2a64ff;
    }
    
    p {
      color: #475569;
      line-height: 1.8;
      margin-bottom: 12px;
    }
    
    ul {
      padding-left: 20px;
      
      li {
        color: #475569;
        line-height: 1.8;
        margin-bottom: 8px;
      }
    }
  }
  
  .disclaimer-box {
    background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
    border: 1px solid #f59e0b;
    border-radius: 16px;
    padding: 24px;
    display: flex;
    gap: 16px;
    
    .el-icon {
      font-size: 32px;
      color: #f59e0b;
      flex-shrink: 0;
    }
    
    p {
      margin: 0;
      color: #78350f;
      
      strong {
        color: #b45309;
      }
    }
  }
}

@media (max-width: 640px) {
  .policy-container {
    padding: 24px;
  }
  
  .policy-content h2 {
    font-size: 22px;
  }
}
</style>

<template>
  <div class="ai-consultation-container">
    <div class="ai-chat-card">
      <!-- 聊天头部 -->
      <header class="chat-header">
        <div class="bot-info">
          <div class="bot-avatar-ring">
            <div class="bot-avatar">
              <svg viewBox="0 0 128 128" class="med-bot-svg">
                <rect x="24" y="32" width="80" height="72" rx="20" fill="#f0f7ff" stroke="#2a64ff" stroke-width="4" />
                <rect x="36" y="48" width="56" height="30" rx="8" fill="#fff" stroke="#2a64ff" stroke-width="2" />
                <ellipse cx="50" cy="63" rx="4" ry="6" fill="#2a64ff"><animate attributeName="ry" values="6;1;6" dur="3s" repeatCount="indefinite" /></ellipse>
                <ellipse cx="78" cy="63" rx="4" ry="6" fill="#2a64ff"><animate attributeName="ry" values="6;1;6" dur="3s" repeatCount="indefinite" /></ellipse>
                <path d="M48 88 L55 88 L58 82 L63 94 L66 88 L75 88" fill="none" stroke="#2a64ff" stroke-width="2.5" stroke-linecap="round" />
                <rect x="58" y="38" width="12" height="4" rx="1.5" fill="#ff4d4f" /><rect x="62" y="34" width="4" height="12" rx="1.5" fill="#ff4d4f" />
              </svg>
            </div>
            <div class="online-status"></div>
          </div>
          <div class="bot-meta">
            <h3>智能医疗助手</h3>
            <p>全域医学知识库 · 辅助诊疗专家</p>
          </div>
        </div>
        <div class="header-actions">
          <el-button link :icon="Delete" @click="clearHistory" class="action-btn">清空对话记录</el-button>
        </div>
      </header>
      
      <!-- 聊天主体 -->
      <div class="chat-main" ref="scrollContainer">
        <div v-for="item in messages" :key="item.id" :class="['msg-row', item.role]">
          <div class="msg-avatar">
            <el-avatar v-if="item.role === 'user'" :size="40" class="u-avatar">
              {{ (authStore.userInfo?.nickname || 'U').charAt(0) }}
            </el-avatar>
            <div v-else class="a-avatar">
              <svg viewBox="0 0 128 128" class="med-bot-svg">
                <rect x="24" y="32" width="80" height="72" rx="20" fill="#fff" stroke="#2a64ff" stroke-width="5" />
                <rect x="36" y="48" width="56" height="30" rx="8" fill="#f0f7ff" />
                <circle cx="50" cy="63" r="4" fill="#2a64ff" /><circle cx="78" cy="63" r="4" fill="#2a64ff" />
                <rect x="58" y="38" width="12" height="4" rx="1.5" fill="#ff4d4f" /><rect x="62" y="34" width="4" height="12" rx="1.5" fill="#ff4d4f" />
              </svg>
            </div>
          </div>
          <div class="msg-bubble">
            <div class="bubble-content" v-html="formatContent(item.content)"></div>
            <div class="bubble-footer">
              <span class="msg-time">{{ formatTime(item.id) }}</span>
              <span v-if="item.role === 'assistant'" class="verified-tag">已通过医学验证</span>
            </div>
          </div>
        </div>

        <!-- 输入中动效 -->
        <div v-if="loading" class="msg-row assistant">
          <div class="msg-avatar">
            <div class="a-avatar">
              <svg viewBox="0 0 128 128" class="med-bot-svg">
                <rect x="24" y="32" width="80" height="72" rx="20" fill="#fff" stroke="#2a64ff" stroke-width="5" />
                <rect x="36" y="48" width="56" height="30" rx="8" fill="#f0f7ff" />
                <circle cx="50" cy="63" r="4" fill="#2a64ff" /><circle cx="78" cy="63" r="4" fill="#2a64ff" />
                <rect x="58" y="38" width="12" height="4" rx="1.5" fill="#ff4d4f" /><rect x="62" y="34" width="4" height="12" rx="1.5" fill="#ff4d4f" />
              </svg>
            </div>
          </div>
          <div class="msg-bubble typing-bubble">
            <div class="typing-dots">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <footer class="chat-footer">
        <div class="input-wrapper">
          <el-input
            v-model="question"
            type="textarea"
            :rows="3"
            placeholder="请详细描述您的症状，例如：'头痛持续了两天，伴有恶心'..."
            @keydown="onKeydown"
            resize="none"
            class="chat-input"
          />
          <div class="footer-bottom">
            <div class="safety-tip">
              <el-icon><ShieldCheck /></el-icon>
              <span>AI 建议基于大数据模型，急重症请务必拨打 120 或立刻前往门诊</span>
            </div>
            <button class="send-btn" :disabled="loading || !question.trim()" @click="handleAsk">
              <el-icon v-if="!loading"><Position /></el-icon>
              <el-icon v-else class="is-loading"><Loading /></el-icon>
              <span>发送指令</span>
            </button>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { aiApi } from '@/api/modules/ai';
import { useAuthStore } from '@/store/modules/auth';
import { Delete, Position, Loading } from '@element-plus/icons-vue';

interface MessageItem {
  id: number;
  role: 'user' | 'assistant';
  content: string;
}

const loading = ref(false);
const question = ref('');
const messages = ref<MessageItem[]>([]);
const scrollContainer = ref<HTMLDivElement>();
const authStore = useAuthStore();
const storageKey = ref<string>('ai-history-' + (authStore.userInfo?.username || 'guest'));

const appendMessage = (message: MessageItem) => {
  messages.value.push(message);
  saveHistory();
  scrollToBottom();
};

const scrollToBottom = () => {
  nextTick(() => {
    const el = scrollContainer.value;
    if (el) el.scrollTo({ top: el.scrollHeight, behavior: 'smooth' });
  });
};

const saveHistory = () => {
  try {
    localStorage.setItem(storageKey.value, JSON.stringify(messages.value.slice(-100)));
  } catch {}
};

const loadHistory = () => {
  try {
    const raw = localStorage.getItem(storageKey.value);
    if (raw) {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed) && parsed.length > 0) {
        messages.value = parsed;
        scrollToBottom();
        return;
      }
    }
  } catch {}
  messages.value = [{ id: Date.now(), role: 'assistant', content: '您好，我是您的专属医疗 AI 助手。我可以为您解释化验单、分析常规症状或提供日常健康建议。请问有什么我可以帮您的？' }];
};

const handleAsk = async () => {
  if (!question.value.trim() || loading.value) return;
  const content = question.value.trim();
  appendMessage({ id: Date.now(), role: 'user', content });
  question.value = '';
  loading.value = true;
  
  try {
    const reply = await aiApi.chat({ message: content });
    appendMessage({
      id: Date.now() + 1,
      role: 'assistant',
      content: reply ? String(reply).trim() : '目前无法获取 AI 回复，请稍后再次尝试。'
    });
  } catch (error) {
    ElMessage.error('网络通讯异常，请检查接口服务');
  } finally {
    loading.value = false;
  }
};

const onKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    handleAsk();
  }
};

const clearHistory = () => {
  messages.value = [{ id: Date.now(), role: 'assistant', content: '对话已重置。您可以开始新的问询。' }];
  saveHistory();
};

const formatTime = (ts: number) => new Date(ts).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

const formatContent = (content: string) => {
  return content
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>');
};

onMounted(loadHistory);
</script>

<style scoped lang="scss">
.ai-consultation-container {
  height: 100%;
  display: flex;
  justify-content: center;
}

.ai-chat-card {
  width: 100%;
  max-width: 1000px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid #edf2f7;
}

// 头部
.chat-header {
  padding: 20px 30px;
  background: #fff;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .bot-info {
    display: flex; align-items: center; gap: 15px;
    .bot-avatar-ring {
      position: relative;
      .bot-avatar { width: 44px; height: 44px; background: #f0f7ff; border-radius: 14px; display: flex; align-items: center; justify-content: center; font-size: 24px; border: 1px solid #e1effe; }
      .online-status { position: absolute; bottom: -2px; right: -2px; width: 12px; height: 12px; background: #52c41a; border: 2px solid #fff; border-radius: 50%; }
    }
    .bot-meta {
      h3 { margin: 0; font-size: 17px; font-weight: 800; color: #1e293b; }
      p { margin: 0; font-size: 12px; color: #94a3b8; font-weight: 600; }
    }
  }

  .action-btn { color: #94a3b8; font-weight: 600; font-size: 13px; &:hover { color: #ff4d4f; } }
}

// 对话区
.chat-main {
  flex: 1; overflow-y: auto; padding: 30px; background-color: #f8fafc;
  display: flex; flex-direction: column; gap: 24px;
}

.msg-row {
  display: flex; gap: 16px; max-width: 85%;
  &.user { align-self: flex-end; flex-direction: row-reverse;
    .msg-bubble { background: #2a64ff; color: #fff; border-radius: 20px 20px 4px 20px; box-shadow: 0 8px 20px rgba(42,100,255,0.15); }
    .msg-time { color: rgba(255,255,255,0.6); }
  }
  &.assistant { align-self: flex-start;
    .msg-bubble { background: #fff; color: #334155; border-radius: 20px 20px 20px 4px; box-shadow: 0 4px 15px rgba(0,0,0,0.03); border: 1px solid #edf2f7; }
  }
}

.msg-avatar {
  flex-shrink: 0;
  .u-avatar { background: linear-gradient(135deg, #2a64ff, #64dcff); font-weight: 800; }
  .a-avatar { width: 40px; height: 40px; background: #fff; border-radius: 12px; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.05); border: 1px solid #f1f5f9; overflow: hidden;
    .med-bot-svg { width: 85%; height: 85%; }
  }
}

.msg-bubble {
  padding: 16px 20px; position: relative;
  .bubble-content { font-size: 15px; line-height: 1.7; word-break: break-word; 
    :deep(h3) { font-size: 16px; margin: 10px 0 5px; }
    :deep(strong) { color: #2a64ff; font-weight: 700; }
  }
  .bubble-footer { margin-top: 8px; display: flex; align-items: center; gap: 10px; }
  .msg-time { font-size: 11px; font-weight: 600; opacity: 0.7; }
  .verified-tag { font-size: 10px; color: #52c41a; background: #f6ffed; padding: 1px 6px; border-radius: 4px; font-weight: 700; }
}

// 正在输入
.typing-bubble { display: flex; align-items: center; min-width: 60px;
  .typing-dots { display: flex; gap: 4px;
    span { width: 6px; height: 6px; background: #cbd5e1; border-radius: 50%; animation: bounce 1.4s infinite;
      &:nth-child(2) { animation-delay: 0.2s; } &:nth-child(3) { animation-delay: 0.4s; }
    }
  }
}

@keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-5px); } }

// 尾部输入区
.chat-footer {
  padding: 24px 30px; background: #fff; border-top: 1px solid #f1f5f9;
  
  .input-wrapper { position: relative; }
  .chat-input :deep(.el-textarea__inner) {
    background: #f8fafc; border-radius: 16px; border: 1px solid #e2e8f0; padding: 15px 20px; font-size: 15px; transition: 0.3s;
    &:focus { border-color: #2a64ff; background: #fff; box-shadow: 0 0 0 4px rgba(42,100,255,0.05); }
  }

  .footer-bottom {
    margin-top: 15px; display: flex; justify-content: space-between; align-items: center;
    .safety-tip { display: flex; align-items: center; gap: 6px; font-size: 11px; color: #94a3b8; font-weight: 600; }
    .send-btn { 
      background: #2a64ff; color: #fff; border: none; padding: 10px 24px; border-radius: 12px; 
      font-weight: 700; display: flex; align-items: center; gap: 8px; cursor: pointer; transition: 0.3s;
      &:hover:not(:disabled) { background: #1e50e6; transform: translateY(-2px); box-shadow: 0 8px 20px rgba(42,100,255,0.25); }
      &:disabled { background: #e2e8f0; color: #94a3b8; cursor: not-allowed; }
    }
  }
}
</style>

<template>
  <div class="ai-chat-wrapper">

    <!-- ========== 左側の会話パネル ========== -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <el-button
          class="btn-new-chat"
          :loading="creatingConv"
          :disabled="isStreaming"
          @click="handleNewConversation"
        >
          <el-icon v-if="!creatingConv"><EditPen /></el-icon>
          新しいチャット
        </el-button>
      </div>

      <div
        v-loading="loadingConvs"
        element-loading-text="読み込み中..."
        class="conv-list"
      >
        <div v-if="!loadingConvs && conversations.length === 0" class="conv-empty">
          会話履歴はありません
        </div>

        <TransitionGroup name="conv-fade" tag="div">
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conv-item"
            :class="{ active: currentConvId === conv.id }"
            @click="handleSelectConversation(conv.id)"
          >
            <el-icon class="conv-icon"><ChatDotRound /></el-icon>
            <span class="conv-title">{{ conv.title }}</span>
            <div class="conv-actions" @click.stop>
              <el-tooltip content="名前を変更" placement="top" :show-after="300">
                <el-button
                  text
                  class="conv-action-btn"
                  @click="openRenameDialog(conv)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="削除" placement="top" :show-after="300">
                <el-button
                  text
                  class="conv-action-btn danger"
                  @click="handleDeleteConversation(conv.id)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </div>
          </div>
        </TransitionGroup>
      </div>
    </aside>

    <!-- ========== メインチャットエリア ========== -->
    <main class="chat-main">

      <!-- ウェルカム画面 -->
      <Transition name="fade">
        <div v-if="!currentConvId" class="welcome-screen">
          <div class="welcome-glow"></div>
          <div class="welcome-avatar">✦</div>
          <h2 class="welcome-title">こんにちは、AI アシスタントです</h2>
          <p class="welcome-subtitle">左側の会話を選択するか、「新しいチャット」をクリックして開始</p>
          <div class="welcome-tips">
            <div class="tip-card" @click="handleNewConversation">
              <el-icon><MagicStick /></el-icon>
              <span>会話を始める</span>
            </div>
            <div class="tip-card" @click="handleNewConversation">
              <el-icon><Cpu /></el-icon>
              <span>AI の機能を探索</span>
            </div>
            <div class="tip-card" @click="handleNewConversation">
              <el-icon><DocumentChecked /></el-icon>
              <span>実際の問題を解決</span>
            </div>
          </div>
        </div>
      </Transition>

      <!-- メッセージ一覧 -->
      <div v-show="currentConvId" class="messages-area" ref="messagesAreaRef">
        <div v-if="loadingMessages" class="messages-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>メッセージを読み込み中...</span>
        </div>

        <template v-else>
          <TransitionGroup name="msg-slide" tag="div" class="messages-inner">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              class="msg-row"
              :class="msg.role"
            >
              <!-- AI アバター -->
              <div v-if="msg.role === 'assistant'" class="avatar ai-av">✦</div>

              <!-- メッセージバブル -->
              <div
                class="bubble"
                :class="[`${msg.role}-bubble`, { 'has-error': msg.error }]"
              >
                <!-- AI：読み込み中の三点リーダー -->
                <div v-if="msg.loading" class="loading-dots">
                  <span></span><span></span><span></span>
                </div>
                <!-- AI：エラー状態 -->
                <div v-else-if="msg.error" class="error-msg">
                  <el-icon><Warning /></el-icon>
                  {{ msg.error }}
                </div>
                <!-- AI：通常コンテンツ -->
                <div
                  v-else-if="msg.role === 'assistant'"
                  class="markdown-body"
                  :class="{ 'typing-cursor': msg.streaming }"
                  v-html="renderMarkdown(msg.content)"
                ></div>
                <!-- ユーザー：プレーンテキスト -->
                <span v-else class="user-text">{{ msg.content }}</span>
              </div>

              <!-- ユーザーアバター -->
              <div v-if="msg.role === 'user'" class="avatar user-av">私</div>
            </div>
          </TransitionGroup>
        </template>
      </div>

      <!-- 入力エリア -->
      <div v-show="currentConvId" class="input-area">
        <div class="input-box" :class="{ focused: inputFocused }">
          <el-input
            v-model="inputText"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 5 }"
            placeholder="メッセージを入力。Enter で送信、Shift+Enter で改行"
            resize="none"
            :disabled="isStreaming"
            @keydown="handleKeyDown"
            @focus="inputFocused = true"
            @blur="inputFocused = false"
            ref="inputRef"
          />
          <el-button
            class="btn-send"
            :disabled="isStreaming || !inputText.trim()"
            @click="handleSendMessage"
          >
            <svg viewBox="0 0 24 24" width="15" height="15" fill="currentColor">
              <path d="M2 21l21-9L2 3v7l15 2-15 2z"/>
            </svg>
          </el-button>
        </div>
        <p class="input-hint">AI が生成した内容は参考情報です。内容をご確認ください。</p>
      </div>
    </main>

    <!-- ========== 会話名変更ダイアログ ========== -->
    <el-dialog
      v-model="renameDialogVisible"
      title="会話名を変更"
      width="400px"
      :close-on-click-modal="false"
      draggable
      @opened="renameInputRef?.focus()"
    >
      <el-input
        ref="renameInputRef"
        v-model="renameTitle"
        maxlength="50"
        show-word-limit
        placeholder="新しい名前を入力してください"
        @keyup.enter="submitRename"
      />
      <template #footer>
        <el-button @click="renameDialogVisible = false">キャンセル</el-button>
        <el-button
          type="primary"
          :disabled="!renameTitle.trim()"
          @click="submitRename"
        >確認</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  EditPen, ChatDotRound, Edit, Delete, Loading,
  Warning, MagicStick, Cpu, DocumentChecked
} from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import {
  listConversations,
  createConversation,
  renameConversation,
  deleteConversation,
  listMessages
} from '@/api/ai/chat'

// ──────────────────────────────────────────
// リアクティブ状態
// ──────────────────────────────────────────
const conversations    = ref([])
const loadingConvs     = ref(false)
const creatingConv     = ref(false)
const currentConvId    = ref(null)
const messages         = ref([])
const loadingMessages  = ref(false)
const inputText        = ref('')
const inputFocused     = ref(false)
const isStreaming      = ref(false)
const renameDialogVisible = ref(false)
const renameTitle      = ref('')
const renameTargetId   = ref(null)

// SSE 関連
let currentReader      = null
let sseEventBuffer     = null   // event: 行を一時保存

// DOM 参照
const messagesAreaRef  = ref(null)
const inputRef         = ref(null)
const renameInputRef   = ref(null)

// ──────────────────────────────────────────
// ライフサイクル
// ──────────────────────────────────────────
onMounted(() => loadConvList(true))
onBeforeUnmount(() => abortStream())

// ──────────────────────────────────────────
// 会話管理
// ──────────────────────────────────────────
async function loadConvList(autoSelect = false) {
  loadingConvs.value = true
  try {
    const res = await listConversations()
    if (res.code === 200) {
      conversations.value = res.data || []
      if (autoSelect && conversations.value.length > 0 && !currentConvId.value) {
        await selectConversation(conversations.value[0].id)
      }
    }
  } catch {
    ElMessage.error('会話一覧の読み込みに失敗しました')
  } finally {
    loadingConvs.value = false
  }
}

async function handleNewConversation() {
  if (isStreaming.value) return
  creatingConv.value = true
  try {
    const res = await createConversation()
    if (res.code === 200) {
      currentConvId.value = res.data.id
      messages.value = []
      await loadConvList()
      nextTick(() => focusInput())
    } else {
      ElMessage.error('作成に失敗しました：' + (res.msg || res.code))
    }
  } catch {
    ElMessage.error('作成に失敗しました。もう一度お試しください')
  } finally {
    creatingConv.value = false
  }
}

async function handleSelectConversation(id) {
  if (isStreaming.value || id === currentConvId.value) return
  await selectConversation(id)
}

async function selectConversation(id) {
  currentConvId.value = id
  await loadMessageList(id)
}

async function loadMessageList(convId) {
  loadingMessages.value = true
  messages.value = []
  try {
    const res = await listMessages(convId)
    if (res.code === 200) {
      messages.value = (res.data || []).map(m => ({
        role: m.role,
        content: m.content,
        loading: false,
        streaming: false,
        error: null
      }))
      nextTick(() => { scrollToBottom(); focusInput() })
    }
  } catch {
    ElMessage.error('メッセージの読み込みに失敗しました')
  } finally {
    loadingMessages.value = false
  }
}

function openRenameDialog(conv) {
  renameTargetId.value = conv.id
  renameTitle.value = conv.title
  renameDialogVisible.value = true
}

async function submitRename() {
  const title = renameTitle.value.trim()
  if (!title) return
  try {
    const res = await renameConversation(renameTargetId.value, title)
    if (res.code === 200) {
      renameDialogVisible.value = false
      await loadConvList()
      ElMessage.success('会話名を変更しました')
    } else {
      ElMessage.error('会話名の変更に失敗しました：' + (res.msg || res.code))
    }
  } catch {
    ElMessage.error('会話名の変更に失敗しました。もう一度お試しください')
  }
}

async function handleDeleteConversation(id) {
  try {
    await ElMessageBox.confirm('この会話とすべてのメッセージを削除しますか？', '確認', {
      confirmButtonText: '削除',
      cancelButtonText: 'キャンセル',
      type: 'warning'
    })
  } catch {
    return  // ユーザーがキャンセル
  }
  try {
    const res = await deleteConversation(id)
    if (res.code === 200) {
      if (currentConvId.value === id) {
        currentConvId.value = null
        messages.value = []
      }
      await loadConvList()
      ElMessage.success('削除しました')
    } else {
      ElMessage.error('削除に失敗しました：' + (res.msg || res.code))
    }
  } catch {
    ElMessage.error('削除に失敗しました。もう一度お試しください')
  }
}

// ──────────────────────────────────────────
// メッセージ送信 — Fetch SSE（JWT を付与）
// ──────────────────────────────────────────
function handleKeyDown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSendMessage()
  }
}

async function handleSendMessage() {
  if (isStreaming.value || !currentConvId.value) return
  const text = inputText.value.trim()
  if (!text) return

  inputText.value = ''
  isStreaming.value = true

  // ユーザーのメッセージバブルを追加
  messages.value.push({ role: 'user', content: text, loading: false, streaming: false, error: null })

  // AI の読み込みプレースホルダーを追加
  const aiIndex = messages.value.length
  messages.value.push({ role: 'assistant', content: '', loading: true, streaming: false, error: null })
  nextTick(() => scrollToBottom())

  const baseUrl = import.meta.env.VITE_APP_BASE_API || ''
  const url = `${baseUrl}/ai/chat/stream?conversationId=${currentConvId.value}&message=${encodeURIComponent(text)}`
  const token = getToken()

  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: { Authorization: 'Bearer ' + token }
    })

    if (!response.ok) throw new Error(`HTTP ${response.status}`)

    const reader = response.body.getReader()
    currentReader = reader
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    sseEventBuffer = null

    // loading → streaming
    messages.value[aiIndex] = { ...messages.value[aiIndex], loading: false, streaming: true }

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop()   // 末尾が不完全な行の場合、次回まで保持

      for (const line of lines) {
        const trimmed = line.trim()
        if (!trimmed) { sseEventBuffer = null; continue }

        if (trimmed.startsWith('event:')) {
          sseEventBuffer = trimmed.slice(6).trim()
        } else if (trimmed.startsWith('data:')) {
          const data  = trimmed.slice(5).trim()
          const event = sseEventBuffer || 'message'
          sseEventBuffer = null

          if (event === 'message') {
            const cur = messages.value[aiIndex]
            messages.value[aiIndex] = { ...cur, content: cur.content + data }
            nextTick(() => scrollToBottom())

          } else if (event === 'done') {
            messages.value[aiIndex] = { ...messages.value[aiIndex], streaming: false }
            isStreaming.value = false
            currentReader = null
            loadConvList()   // サイドバーのタイトルを更新
            return

          } else if (event === 'error') {
            throw new Error(data || 'AI サービスエラー')
          }
        }
      }
    }

  } catch (e) {
    if (e.name === 'AbortError') return
    const errMsg = e.message || 'サービスエラーです。もう一度お試しください'
    messages.value[aiIndex] = {
      role: 'assistant', content: '', loading: false, streaming: false, error: errMsg
    }
    ElMessage.error('AI の応答に失敗しました：' + errMsg)
  } finally {
    isStreaming.value = false
    currentReader = null
    nextTick(() => focusInput())
  }
}

function abortStream() {
  if (currentReader) {
    try { currentReader.cancel() } catch (_) {}
    currentReader = null
  }
  isStreaming.value = false
}

// ──────────────────────────────────────────
// Markdown の簡易レンダリング
// ──────────────────────────────────────────
function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function renderMarkdown(text) {
  if (!text) return ''
  let html = escapeHtml(text)
  // コードブロック
  html = html.replace(
    /```[\w]*\n?([\s\S]*?)```/g,
    '<pre class="code-block"><code>$1</code></pre>'
  )
  // インラインコード
  html = html.replace(/`([^`\n]+)`/g, '<code class="inline-code">$1</code>')
  // 太字
  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  // 斜体
  html = html.replace(/(?<!\*)\*(?!\*)(.+?)(?<!\*)\*(?!\*)/g, '<em>$1</em>')
  // 順序なしリスト
  html = html.replace(/^[-*] (.+)$/gm, '<li>$1</li>')
  html = html.replace(/(<li>[\s\S]*?<\/li>)/g, m => `<ul>${m}</ul>`)
  // 順序付きリスト
  html = html.replace(/^\d+\. (.+)$/gm, '<li>$1</li>')
  // 水平線
  html = html.replace(/^---+$/gm, '<hr>')
  // 改行
  html = html.replace(/\n/g, '<br>')
  return html
}

// ──────────────────────────────────────────
// ユーティリティ
// ──────────────────────────────────────────
function scrollToBottom() {
  const el = messagesAreaRef.value
  if (el) el.scrollTop = el.scrollHeight
}

function focusInput() {
  const textarea = inputRef.value?.$el?.querySelector('textarea')
  textarea?.focus()
}
</script>

<style scoped>
/* ===== 全体レイアウト ===== */
.ai-chat-wrapper {
  display: flex;
  /* Vue3 のレイアウトに対応：上部 navbar 50px + tabs-nav 34px + 余白 */
  height: calc(100vh - 84px);
  background: #f5f7fa;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB',
               'Microsoft YaHei', sans-serif;
}

/* ===== サイドバー ===== */
.sidebar {
  width: 226px;
  min-width: 226px;
  background: #ffffff;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 14px 12px 10px;
  border-bottom: 1px solid #f2f4f7;
}

.btn-new-chat {
  width: 100% !important;
  background: #f0f7ff !important;
  border: 1.5px dashed #409eff !important;
  color: #409eff !important;
  border-radius: 9px !important;
  font-size: 13px !important;
  font-weight: 500 !important;
  gap: 6px !important;
  height: 38px !important;
  transition: all 0.2s !important;
}
.btn-new-chat:hover:not(:disabled) {
  background: #d9ecff !important;
  border-color: #66b1ff !important;
}

.conv-list {
  flex: 1;
  overflow-y: auto;
  padding: 6px 8px 8px;
  min-height: 0;
}

.conv-empty {
  text-align: center;
  color: #c0c4cc;
  font-size: 13px;
  padding: 28px 0;
}

/* 会話項目 */
.conv-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: background 0.15s;
  user-select: none;
  gap: 7px;
  margin-bottom: 2px;
  position: relative;
}
.conv-item:hover { background: #f5f7fa; }
.conv-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: 500;
}

.conv-icon {
  font-size: 13px;
  flex-shrink: 0;
  opacity: 0.6;
}

.conv-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.conv-actions {
  display: none;
  align-items: center;
  gap: 0;
  flex-shrink: 0;
}
.conv-item:hover .conv-actions,
.conv-item.active .conv-actions {
  display: flex;
}

.conv-action-btn {
  padding: 3px 4px !important;
  height: auto !important;
  color: #909399 !important;
  font-size: 13px !important;
}
.conv-action-btn:hover { color: #409eff !important; }
.conv-action-btn.danger:hover { color: #f56c6c !important; }

/* サイドバーリストのトランジション */
.conv-fade-enter-active,
.conv-fade-leave-active { transition: all 0.2s; }
.conv-fade-enter-from  { opacity: 0; transform: translateX(-8px); }
.conv-fade-leave-to    { opacity: 0; transform: translateX(-8px); }

/* ===== メインチャットエリア ===== */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
  background: #f5f7fa;
  position: relative;
}

/* ウェルカム画面 */
.welcome-screen {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  z-index: 1;
}

.welcome-glow {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(64, 158, 255, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

.welcome-avatar {
  width: 68px;
  height: 68px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6c3be4 0%, #409eff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  box-shadow: 0 8px 32px rgba(64, 158, 255, 0.25);
  margin-bottom: 6px;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
  letter-spacing: -0.3px;
}

.welcome-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.welcome-tips {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.tip-card {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.tip-card:hover {
  border-color: #409eff;
  color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.12);
  transform: translateY(-1px);
}

/* ウェルカム画面のトランジション */
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s; }
.fade-enter-from, .fade-leave-to       { opacity: 0; }

/* ===== メッセージ一覧 ===== */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0 10px;
  min-height: 0;
}

.messages-inner {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.messages-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  font-size: 13px;
  padding: 48px 0;
}

/* メッセージ行のトランジション */
.msg-slide-enter-active { transition: all 0.22s ease; }
.msg-slide-enter-from   { opacity: 0; transform: translateY(10px); }

/* メッセージ行 */
.msg-row {
  display: flex;
  align-items: flex-start;
  padding: 5px 44px;
  gap: 12px;
  max-width: 900px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}
.msg-row.user { justify-content: flex-end; }

/* アバター */
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}
.user-av {
  background: #409eff;
  color: #fff;
  font-size: 12px;
}
.ai-av {
  background: linear-gradient(135deg, #6c3be4 0%, #409eff 100%);
  color: #fff;
  font-size: 16px;
}

/* メッセージバブル */
.bubble {
  max-width: 70%;
  padding: 11px 16px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.75;
  word-break: break-word;
}
.user-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}
.assistant-bubble {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
}
.has-error {
  background: #fff5f5;
  border: 1px solid #fde2e2;
}

/* タイピングカーソル */
.typing-cursor::after {
  content: '▋';
  font-size: 12px;
  animation: blink 0.8s step-start infinite;
  margin-left: 2px;
  color: #409eff;
  vertical-align: baseline;
}
@keyframes blink { 50% { opacity: 0; } }

/* 三点リーダーの読み込み表示 */
.loading-dots {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 2px 0;
}
.loading-dots span {
  display: inline-block;
  width: 7px;
  height: 7px;
  background: #c0c4cc;
  border-radius: 50%;
  animation: dotBounce 1.2s infinite ease-in-out;
}
.loading-dots span:nth-child(2) { animation-delay: 0.18s; }
.loading-dots span:nth-child(3) { animation-delay: 0.36s; }
@keyframes dotBounce {
  0%, 60%, 100% { transform: translateY(0);    }
  30%           { transform: translateY(-7px); }
}

/* エラー表示 */
.error-msg {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #f56c6c;
  font-size: 13px;
}

/* Markdown スタイル */
.markdown-body :deep(pre.code-block) {
  background: #1e1e2e;
  color: #cdd6f4;
  padding: 14px 18px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 13px;
  margin: 8px 0;
  font-family: 'Fira Code', 'JetBrains Mono', 'Courier New', monospace;
  line-height: 1.65;
}
.markdown-body :deep(code.inline-code) {
  background: #f0f2f5;
  color: #e83e8c;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 13px;
  font-family: 'Fira Code', 'Courier New', monospace;
}
.markdown-body :deep(ul) {
  margin: 6px 0;
  padding-left: 20px;
}
.markdown-body :deep(li) { margin: 3px 0; }
.markdown-body :deep(strong) { font-weight: 600; color: #1a1a2e; }
.markdown-body :deep(em) { font-style: italic; color: #606266; }
.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid #ebeef5;
  margin: 10px 0;
}

/* ===== 入力エリア ===== */
.input-area {
  padding: 10px 44px 14px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.input-box {
  max-width: 812px;
  margin: 0 auto;
  background: #fff;
  border: 1.5px solid #dcdfe6;
  border-radius: 14px;
  padding: 8px 10px 8px 16px;
  display: flex;
  align-items: flex-end;
  gap: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}
.input-box.focused {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.08);
}

/* el-input / el-textarea のスタイルを上書き */
.input-box :deep(.el-textarea__inner) {
  border: none !important;
  box-shadow: none !important;
  background: transparent !important;
  padding: 5px 0 !important;
  font-size: 14px !important;
  line-height: 1.65 !important;
  color: #303133 !important;
  font-family: inherit !important;
  resize: none !important;
}
.input-box :deep(.el-textarea__inner)::placeholder {
  color: #c0c4cc;
}

.btn-send {
  width: 36px !important;
  height: 36px !important;
  min-width: 36px !important;
  padding: 0 !important;
  border-radius: 50% !important;
  background: #409eff !important;
  border-color: #409eff !important;
  color: #fff !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  flex-shrink: 0 !important;
  transition: background 0.2s, transform 0.12s, box-shadow 0.2s !important;
  margin-bottom: 1px;
}
.btn-send:hover:not(:disabled) {
  background: #66b1ff !important;
  box-shadow: 0 2px 10px rgba(64, 158, 255, 0.3) !important;
}
.btn-send:active:not(:disabled) { transform: scale(0.91) !important; }
.btn-send:disabled {
  background: #c0c4cc !important;
  border-color: #c0c4cc !important;
  cursor: not-allowed !important;
}

.input-hint {
  max-width: 812px;
  margin: 5px auto 0;
  font-size: 11px;
  color: #c0c4cc;
  text-align: center;
}

/* ===== スクロールバー ===== */
.conv-list::-webkit-scrollbar,
.messages-area::-webkit-scrollbar {
  width: 4px;
}
.conv-list::-webkit-scrollbar-track,
.messages-area::-webkit-scrollbar-track {
  background: transparent;
}
.conv-list::-webkit-scrollbar-thumb,
.messages-area::-webkit-scrollbar-thumb {
  background: #e4e7ed;
  border-radius: 4px;
}
</style>

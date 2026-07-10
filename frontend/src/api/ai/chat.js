import request from '@/utils/request'

// 会話一覧を取得
export function listConversations() {
  return request({
    url: '/ai/chat/conversations',
    method: 'get'
  })
}

// 新しい会話を作成
export function createConversation(model) {
  return request({
    url: '/ai/chat/conversations',
    method: 'post',
    params: { model }
  })
}

// 会話名を変更
export function renameConversation(id, title) {
  return request({
    url: `/ai/chat/conversations/${id}/title`,
    method: 'put',
    params: { title }
  })
}

// 会話を削除
export function deleteConversation(id) {
  return request({
    url: `/ai/chat/conversations/${id}`,
    method: 'delete'
  })
}

// 会話のメッセージ履歴を取得
export function listMessages(conversationId) {
  return request({
    url: `/ai/chat/conversations/${conversationId}/messages`,
    method: 'get'
  })
}

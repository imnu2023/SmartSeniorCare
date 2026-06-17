<template>
  <div class="messages-page">
    <div class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5m0 0l7-7m-7 7l7 7"/>
          </svg>
          <span>返回</span>
        </button>
        <div class="header-text">
          <h1>消息中心</h1>
          <p>与家人和社区保持联系</p>
        </div>
        <button class="mark-all-read" @click="markAllRead" v-if="unreadCount > 0">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
            <circle cx="12" cy="12" r="3"/>
          </svg>
          <span>全部标为已读 ({{ unreadCount }})</span>
        </button>
      </div>
    </div>

    <div class="messages-container">
      <div class="contacts-list">
        <div 
          v-for="contact in contacts" 
          :key="contact.id"
          :class="['contact-item', { active: activeContact?.id === contact.id }]"
          @click="selectContact(contact)"
        >
          <div class="contact-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="contact-info">
            <div class="contact-name">{{ contact.name }}</div>
            <div class="contact-last-message">{{ contact.lastMessage }}</div>
          </div>
          <div class="contact-meta">
            <div class="contact-time">{{ contact.time }}</div>
            <div v-if="contact.unread > 0" class="unread-badge">{{ contact.unread }}</div>
          </div>
        </div>
      </div>

      <div class="chat-area" v-if="activeContact">
        <div class="chat-header">
          <div class="chat-title">
            <div class="chat-avatar">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <div>
              <div class="chat-name">{{ activeContact.name }}</div>
              <div class="chat-relation">{{ activeContact.relation }}</div>
            </div>
          </div>
          <div class="online-status">
            <span class="status-dot online"></span>
            <span>在线</span>
          </div>
        </div>

        <div class="chat-messages" ref="chatMessages">
          <div 
            v-for="message in conversation" 
            :key="message.id"
            :class="['message-item', { sent: message.senderId === currentUserId, received: message.senderId !== currentUserId }]"
          >
            <div class="message-avatar" v-if="message.senderId !== currentUserId">👤</div>
            <div class="message-content">
              <p>{{ message.content }}</p>
              <div class="message-time">{{ message.time }}</div>
            </div>
            <div class="message-avatar" v-if="message.senderId === currentUserId">👤</div>
          </div>
        </div>

        <div class="chat-input">
          <input 
            type="text" 
            v-model="newMessage"
            placeholder="输入消息..."
            @keyup.enter="sendMessage"
            class="message-input"
          />
          <button class="send-btn" @click="sendMessage">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="22" y1="2" x2="11" y2="13"/>
              <polygon points="22 2 15 22 11 13 2 9 22 2"/>
            </svg>
          </button>
        </div>
      </div>

      <div class="empty-chat" v-else>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <p>选择一个联系人开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { messageAPI, relationAPI } from '../api'

const router = useRouter()

const goBack = () => {
  router.push('/dashboard')
}

const contacts = ref([])
const activeContact = ref(null)
const conversation = ref([])
const newMessage = ref('')
const chatMessages = ref(null)

const unreadCount = computed(() => {
  return contacts.value.reduce((sum, c) => sum + (c.unread || 0), 0)
})

const formatTime = (dateStr) => {
  if (!dateStr) return '刚刚'
  
  let date = null
  
  if (typeof dateStr === 'string') {
    date = new Date(dateStr.replace('T', ' '))
  } else if (dateStr instanceof Date) {
    date = dateStr
  }
  
  if (!date || isNaN(date.getTime())) {
    date = new Date()
  }
  
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const loadContacts = async () => {
  const userId = parseInt(localStorage.getItem('userId')) || 3
  const role = parseInt(localStorage.getItem('role')) || 3
  const isElder = role === 3
  
  const adminContact = { id: 1, name: '社区管理员', relation: '', lastMessage: '点击开始聊天', time: '今天', unread: 0 }
  const newContacts = [adminContact]
  
  try {
    let relations = []
    
    if (isElder) {
      const response = await relationAPI.getByElder(userId)
      if (response && response.data) {
        relations = response.data
      }
    } else {
      const response = await relationAPI.getByFamilyMember(userId)
      if (response && response.data) {
        relations = response.data
      }
    }
    
    for (const relation of relations) {
      if (isElder) {
        newContacts.push({
          id: relation.familyMemberId,
          name: relation.familyMemberName || '家属',
          relation: getRelationLabel(relation.relationType),
          lastMessage: '点击开始聊天',
          time: '今天',
          unread: 0
        })
      } else {
        newContacts.push({
          id: relation.elderId,
          name: relation.elderName || '老人',
          relation: getRelationLabel(relation.relationType),
          lastMessage: '点击开始聊天',
          time: '今天',
          unread: 0
        })
      }
    }
  } catch (error) {
    console.error('获取联系人失败:', error)
  }
  
  contacts.value = newContacts
}

const getRelationLabel = (type) => {
  const labels = {
    'child': '子女',
    'parent': '父母',
    'spouse': '配偶',
    'sibling': '兄弟姐妹',
    'other': '其他'
  }
  return labels[type] || ''
}

const currentUserId = parseInt(localStorage.getItem('userId')) || 3

const selectContact = async (contact) => {
  activeContact.value = contact
  contact.unread = 0
  
  try {
    const response = await messageAPI.getConversation(currentUserId, contact.id)
    
    if (response && response.data) {
      const messages = response.data.map(msg => ({
        id: msg.id,
        senderId: msg.senderId,
        content: msg.content,
        time: formatTime(msg.createdAt),
        createdAt: msg.createdAt
      }))
      messages.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
      conversation.value = messages
    }
  } catch (error) {
    console.error('获取消息失败:', error)
    conversation.value = []
  }
  
  nextTick(() => {
    if (chatMessages.value) {
      chatMessages.value.scrollTop = chatMessages.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!newMessage.value.trim() || !activeContact.value) return
  
  const content = newMessage.value.trim()
  const receiverId = activeContact.value.id
  
  try {
    const response = await messageAPI.send(currentUserId, receiverId, content, 'text')
    
    if (response && response.data) {
      conversation.value.push({
        id: response.data.id,
        senderId: currentUserId,
        content: content,
        time: formatTime(response.data.createdAt || new Date()),
        createdAt: response.data.createdAt || new Date().toISOString()
      })
      
      newMessage.value = ''
      
      nextTick(() => {
        if (chatMessages.value) {
          chatMessages.value.scrollTop = chatMessages.value.scrollHeight
        }
      })
      
      ElMessage.success('消息已发送')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

const markAllRead = () => {
  contacts.value.forEach(c => c.unread = 0)
  ElMessage.success('已全部标为已读')
}

let lastMessageHash = ''

const checkNewMessages = () => {
  const stored = localStorage.getItem('elder-family-chat')
  const currentHash = stored ? stored.length.toString() : ''
  if (currentHash !== lastMessageHash) {
    lastMessageHash = currentHash
    if (activeContact.value) {
      selectContact(activeContact.value)
    }
  }
}

const handleStorageChange = (event) => {
  if (event.key === 'elder-family-chat' && activeContact.value) {
    selectContact(activeContact.value)
  }
}

let pollInterval = null

onMounted(() => {
  loadContacts()
  window.addEventListener('storage', handleStorageChange)
  pollInterval = setInterval(checkNewMessages, 1000)
})

onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange)
  if (pollInterval) {
    clearInterval(pollInterval)
  }
})
</script>

<style scoped>
.messages-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #f8fafc;
  border-color: #667eea;
  color: #667eea;
}

.back-btn svg {
  width: 18px;
  height: 18px;
}

.header-text h1 {
  margin: 0;
  font-size: 28px;
  color: #1e293b;
  font-weight: 700;
}

.header-text p {
  margin: 8px 0 0 0;
  color: #64748b;
}

.mark-all-read {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.mark-all-read svg {
  width: 16px;
  height: 16px;
}

.mark-all-read:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.messages-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 180px);
}

.contacts-list {
  width: 320px;
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  overflow-y: auto;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.contact-item:hover {
  background: #f8fafc;
}

.contact-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
}

.contact-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.contact-avatar svg {
  width: 22px;
  height: 22px;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.contact-last-message {
  font-size: 13px;
  color: #64748b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.contact-meta {
  text-align: right;
}

.contact-time {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.unread-badge {
  background: #ef4444;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 12px;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: #fafafa;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.chat-avatar svg {
  width: 20px;
  height: 20px;
}

.chat-name {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.chat-relation {
  font-size: 13px;
  color: #64748b;
}

.online-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #22c55e;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #94a3b8;
}

.status-dot.online {
  background: #22c55e;
}

.chat-messages {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  max-width: 70%;
  display: flex;
  gap: 10px;
}

.message-item.sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-item.received {
  align-self: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.message-content {
  background: #f1f5f9;
  border-radius: 16px;
  padding: 14px 18px;
}

.sent .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 16px 16px 6px 16px;
}

.received .message-content {
  background: #f1f5f9;
  color: #1e293b;
  border-radius: 16px 16px 16px 6px;
}

.message-content p {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
}

.message-time {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 8px;
  text-align: right;
}

.sent .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.chat-input {
  display: flex;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #fafafa;
}

.message-input {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s;
  background: white;
}

.message-input:focus {
  border-color: #667eea;
}

.message-input::placeholder {
  color: #94a3b8;
}

.send-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s;
}

.send-btn:hover {
  transform: scale(1.05);
}

.send-btn svg {
  width: 20px;
  height: 20px;
}

.empty-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  color: #94a3b8;
}

.empty-chat svg {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.empty-chat p {
  font-size: 16px;
}

@media (max-width: 768px) {
  .messages-container {
    flex-direction: column;
    height: auto;
  }
  
  .contacts-list {
    width: 100%;
    max-height: 160px;
  }
  
  .chat-area {
    height: 400px;
  }
}
</style>
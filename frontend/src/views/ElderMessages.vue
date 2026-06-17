<template>
  <div class="elder-messages-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1>消息中心</h1>
          <p>与家人、社区和医生保持联系</p>
        </div>
      </div>
      
      <div class="quick-actions">
        <button v-for="action in quickActions" :key="action.text" class="quick-btn" @click="sendQuickMessage(action.text)">
          {{ action.text }}
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
            <div class="contact-relation">{{ contact.relation }}</div>
          </div>
          <div v-if="contact.unread > 0" class="unread-badge">{{ contact.unread }}</div>
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
            :class="['message-item', { sent: message.senderId === 15, received: message.senderId !== 15 }]"
          >
            <div class="message-content">
              <p>{{ message.content }}</p>
              <div class="message-time">{{ formatTime(message.createdAt) }}</div>
            </div>
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
        <p>选择一位联系人开始聊天</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { messageAPI } from '../api'

const contacts = ref([])
const activeContact = ref(null)
const conversation = ref([])
const newMessage = ref('')
const chatMessages = ref(null)

const quickActions = [
  { text: '我出门了' },
  { text: '我到家了' },
  { text: '今天挺好的' },
  { text: '需要帮忙' }
]

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const loadContacts = () => {
  contacts.value = [
    { id: -1, name: '社区通知', relation: '社区', unread: 0 },
    { id: -2, name: '健康顾问', relation: '医生', unread: 0 },
    { id: 3, name: '张小红', relation: '女儿', unread: 0 }
  ]
}

const getBaseMessages = (contactId) => {
  const messages = {
    '-1': [
      { id: 1, senderId: -1, content: '尊敬的居民，本周六上午9点将在社区活动中心举办健康讲座，欢迎参加！', createdAt: '2024-01-15 08:00:00' },
      { id: 2, senderId: -1, content: '社区通知：明天将进行电梯维护，请大家注意出行安全。', createdAt: '2024-01-14 16:30:00' },
      { id: 3, senderId: 15, content: '收到，谢谢通知', createdAt: '2024-01-14 16:35:00' }
    ],
    '-2': [
      { id: 1, senderId: -2, content: '李叔叔您好，根据您最近的健康数据，您的血压控制得很好，继续保持！', createdAt: '2024-01-14 14:00:00' },
      { id: 2, senderId: 15, content: '谢谢医生关心', createdAt: '2024-01-14 14:05:00' },
      { id: 3, senderId: -2, content: '记得按时服药，有任何不适请及时联系我们。', createdAt: '2024-01-14 14:08:00' }
    ],
    '3': [
      { id: 1, senderId: 3, content: '爸爸，今天天气不错，记得出去走走', createdAt: '2024-01-15 10:30:00' },
      { id: 2, senderId: 15, content: '好的，我下午出去', createdAt: '2024-01-15 10:35:00' },
      { id: 3, senderId: 3, content: '记得带伞，可能会下雨', createdAt: '2024-01-15 10:40:00' }
    ]
  }
  return messages[contactId] || []
}

const selectContact = async (contact) => {
  activeContact.value = contact
  contact.unread = 0
  
  try {
    const response = await messageAPI.getConversation(15, contact.id)
    
    if (response && response.data) {
      const messages = response.data.map(msg => ({
        id: msg.id,
        senderId: msg.senderId,
        content: msg.content,
        createdAt: msg.createdTime || msg.createdAt || new Date().toISOString()
      }))
      messages.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
      conversation.value = messages
    }
  } catch (error) {
    console.error('获取消息失败:', error)
    conversation.value = getBaseMessages(contact.id)
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
    const response = await messageAPI.send(15, receiverId, content, 'text')
    
    if (response && response.data) {
      const msg = {
        id: response.data.id,
        senderId: 15,
        content: content,
        createdAt: response.data.createdTime || new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      conversation.value.push(msg)
      
      newMessage.value = ''
      
      nextTick(() => {
        if (chatMessages.value) {
          chatMessages.value.scrollTop = chatMessages.value.scrollHeight
        }
      })
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

const sendQuickMessage = async (text) => {
  if (!activeContact.value && contacts.value.length > 0) {
    selectContact(contacts.value[2])
    await new Promise(resolve => setTimeout(resolve, 100))
  }
  newMessage.value = text
  sendMessage()
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
.elder-messages-page {
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
  margin-bottom: 16px;
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

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.quick-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.messages-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 200px);
}

.contacts-list {
  width: 280px;
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

.contact-relation {
  font-size: 13px;
  color: #64748b;
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
}

.message-item.sent {
  align-self: flex-end;
}

.message-item.received {
  align-self: flex-start;
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
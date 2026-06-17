<template>
  <div class="activities-page">
    <div class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5m0 0l7-7m-7 7l7 7"/>
          </svg>
          <span>返回</span>
        </button>
        <div class="header-text">
          <h1>社区活动</h1>
          <p>参与丰富多彩的社区活动，丰富晚年生活</p>
        </div>
        <button class="create-btn" @click="showCreateModal = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          <span>创建活动</span>
        </button>
      </div>
    </div>

    <div class="filter-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        <svg :viewBox="tab.iconViewBox" fill="none" stroke="currentColor" stroke-width="2">
          <path :d="tab.iconPath"/>
        </svg>
        <span>{{ tab.label }}</span>
        <span v-if="tab.count > 0" class="tab-count">{{ tab.count }}</span>
      </button>
    </div>

    <div class="activities-grid">
      <div 
        v-for="activity in activities" 
        :key="activity.id"
        class="activity-card"
        @click="showActivityDetail(activity)"
      >
        <div class="activity-cover">
          <div class="cover-image">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path :d="getActivityIcon(activity.type)"/>
            </svg>
          </div>
          <div class="activity-type" :class="getActivityTypeClass(activity.type)">
            {{ getActivityTypeName(activity.type) }}
          </div>
        </div>
        
        <div class="activity-content">
          <h3 class="activity-title">{{ activity.title }}</h3>
          <p class="activity-desc">{{ activity.description }}</p>
          
          <div class="activity-info">
            <div class="info-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17.657 16.657L13.414 20.9a1.998 1.998 0 0 1-2.827 0l-4.244-4.243a8 8 0 1 1 11.314 0z"/>
                <path d="M15 11a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
              </svg>
              <span>{{ activity.location }}</span>
            </div>
            <div class="info-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 8v4l3 3"/>
                <circle cx="12" cy="12" r="10"/>
              </svg>
              <span>{{ formatDateTime(activity.startTime) }}</span>
            </div>
          </div>
          
          <div class="activity-footer">
            <div class="participants">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
              </svg>
              <span>{{ activity.currentParticipants }}/{{ activity.maxParticipants }}</span>
            </div>
            <button 
              class="register-btn"
              :class="{ 'cancel-btn': isParticipating(activity.id) }"
              @click.stop="registerActivity(activity)"
            >
              {{ isParticipating(activity.id) ? '取消报名' : '立即报名' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showDetailModal" @click="showDetailModal = false">
      <div class="detail-modal" @click.stop>
        <div class="modal-header">
          <h2>{{ selectedActivity?.title }}</h2>
          <button class="close-btn" @click="showDetailModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="detail-info">
            <div class="detail-row">
              <span class="detail-label">活动类型</span>
              <span class="detail-value">{{ getActivityTypeName(selectedActivity?.type) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">活动地点</span>
              <span class="detail-value">{{ selectedActivity?.location }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">开始时间</span>
              <span class="detail-value">{{ formatDateTime(selectedActivity?.startTime) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">结束时间</span>
              <span class="detail-value">{{ formatDateTime(selectedActivity?.endTime) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">活动人数</span>
              <span class="detail-value">{{ selectedActivity?.currentParticipants }}/{{ selectedActivity?.maxParticipants }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">组织者</span>
              <span class="detail-value">{{ selectedActivity?.organizer }}</span>
            </div>
          </div>
          
          <div class="detail-desc">
            <h3>活动介绍</h3>
            <p>{{ selectedActivity?.description }}</p>
          </div>
        </div>
        
        <div class="modal-footer">
          <button 
            class="register-btn large"
            :class="{ 'cancel-btn': isParticipating(selectedActivity?.id) }"
            @click="registerActivity(selectedActivity)"
          >
            {{ isParticipating(selectedActivity?.id) ? '取消报名' : '立即报名' }}
          </button>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showCreateModal" @click="showCreateModal = false">
      <div class="create-modal" @click.stop>
        <div class="modal-header">
          <h2>创建活动</h2>
          <button class="close-btn" @click="showCreateModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>活动标题</label>
            <input v-model="newActivity.title" type="text" placeholder="请输入活动标题"/>
          </div>
          <div class="form-group">
            <label>活动类型</label>
            <select v-model="newActivity.type">
              <option value="culture">文化活动</option>
              <option value="sports">体育健身</option>
              <option value="study">学习课程</option>
              <option value="social">社交聚会</option>
              <option value="health">健康养生</option>
            </select>
          </div>
          <div class="form-group">
            <label>活动地点</label>
            <input v-model="newActivity.location" type="text" placeholder="请输入活动地点"/>
          </div>
          <div class="form-group">
            <label>开始时间</label>
            <input v-model="newActivity.startTime" type="datetime-local"/>
          </div>
          <div class="form-group">
            <label>结束时间</label>
            <input v-model="newActivity.endTime" type="datetime-local"/>
          </div>
          <div class="form-group">
            <label>最大人数</label>
            <input v-model="newActivity.maxParticipants" type="number" min="1"/>
          </div>
          <div class="form-group">
            <label>组织者</label>
            <input v-model="newActivity.organizer" type="text" placeholder="请输入组织者姓名"/>
          </div>
          <div class="form-group">
            <label>活动描述</label>
            <textarea v-model="newActivity.description" rows="4" placeholder="请输入活动描述"></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="showCreateModal = false">取消</button>
          <button class="submit-btn" @click="createActivity">创建活动</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { activityAPI } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()

const goBack = () => {
  router.push('/dashboard')
}

const activeTab = ref('all')
const activities = ref([])
const selectedActivity = ref(null)
const showDetailModal = ref(false)
const showCreateModal = ref(false)
const registeredActivities = ref(new Set())

const newActivity = ref({
  title: '',
  description: '',
  type: 'culture',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 20,
  organizer: ''
})

const tabs = computed(() => [
  { label: '全部', value: 'all', iconViewBox: '0 0 24 24', iconPath: 'M4 5a1 1 0 0 1 1-1h14a1 1 0 0 1 1 1v2a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5zM4 13a1 1 0 0 1 1-1h6a1 1 0 0 1 1 1v6a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-6zM16 13a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v6a1 1 0 0 1-1 1h-2a1 1 0 0 1-1-1v-6z', count: 0 },
  { label: '即将开始', value: 'upcoming', iconViewBox: '0 0 24 24', iconPath: 'M12 8v4l3 3', count: 0 },
  { label: '文化活动', value: 'culture', iconViewBox: '0 0 24 24', iconPath: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z', count: 0 },
  { label: '体育健身', value: 'sports', iconViewBox: '0 0 24 24', iconPath: 'M22 12h-4l-3 9L9 3l-3 9H2', count: 0 },
  { label: '我的活动', value: 'my', iconViewBox: '0 0 24 24', iconPath: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2', count: 0 }
])

const getActivityIcon = (type) => {
  const icons = {
    culture: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z',
    sports: 'M22 12h-4l-3 9L9 3l-3 9H2',
    study: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253',
    social: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2',
    health: 'M4.318 6.318a4.5 4.5 0 0 0 0 6.364L12 20.364l7.682-7.682a4.5 4.5 0 0 0-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 0 0-6.364 0z'
  }
  return icons[type] || icons.culture
}

const getActivityTypeClass = (type) => {
  const classes = {
    culture: 'culture',
    sports: 'sports',
    study: 'study',
    social: 'social',
    health: 'health'
  }
  return classes[type] || 'culture'
}

const getActivityTypeName = (type) => {
  const names = {
    culture: '文化活动',
    sports: '体育健身',
    study: '学习课程',
    social: '社交聚会',
    health: '健康养生'
  }
  return names[type] || '其他'
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const isParticipating = (activityId) => {
  return registeredActivities.value.has(activityId)
}

const loadActivities = async () => {
  try {
    let response
    if (activeTab.value === 'my') {
      const userId = localStorage.getItem('userId')
      response = await activityAPI.getByParticipant(userId)
    } else if (activeTab.value === 'upcoming') {
      response = await activityAPI.getUpcoming()
    } else if (activeTab.value === 'all') {
      response = await activityAPI.getActive()
    } else {
      response = await activityAPI.getByType(activeTab.value)
    }
    
    registeredActivities.value.clear()
    
    if (response.code === 200) {
      if (response.data.length > 0) {
        activities.value = response.data
        if (activeTab.value !== 'my') {
          const userId = localStorage.getItem('userId')
          for (const activity of response.data) {
            try {
              const result = await activityAPI.isParticipant(activity.id, userId)
              if (result.code === 200 && result.data) {
                registeredActivities.value.add(activity.id)
              }
            } catch {
            }
          }
        } else {
          for (const activity of response.data) {
            registeredActivities.value.add(activity.id)
          }
        }
      } else {
        activities.value = []
      }
    } else {
      activities.value = []
    }
  } catch {
    activities.value = []
  }
}

const generateMyActivities = () => {
  const myActivities = [
    {
      id: 1,
      title: '书法艺术交流',
      description: '邀请社区书法爱好者一起交流书法技巧，分享作品，共同提高书法水平。',
      type: 'culture',
      location: '社区活动中心三楼',
      startTime: '2024-01-20 09:00:00',
      endTime: '2024-01-20 11:30:00',
      maxParticipants: 20,
      currentParticipants: 15,
      organizer: '李老师',
      status: 'active'
    },
    {
      id: 3,
      title: '智能手机使用培训',
      description: '帮助老年人学习智能手机的基本操作，包括微信使用、视频通话、健康码等。',
      type: 'study',
      location: '社区活动中心二楼',
      startTime: '2024-01-22 14:00:00',
      endTime: '2024-01-22 16:00:00',
      maxParticipants: 30,
      currentParticipants: 28,
      organizer: '社区志愿者',
      status: 'active'
    },
    {
      id: 5,
      title: '健康养生讲座',
      description: '邀请专业医生讲解老年人健康养生知识，解答健康疑问。',
      type: 'health',
      location: '社区活动中心一楼大厅',
      startTime: '2024-01-28 14:30:00',
      endTime: '2024-01-28 16:30:00',
      maxParticipants: 80,
      currentParticipants: 65,
      organizer: '社区卫生服务中心',
      status: 'active'
    }
  ]
  
  myActivities.forEach(activity => {
    registeredActivities.value.add(activity.id)
  })
  
  return myActivities
}

const generateMockActivities = () => {
  return [
    {
      id: 1,
      title: '书法艺术交流',
      description: '邀请社区书法爱好者一起交流书法技巧，分享作品，共同提高书法水平。',
      type: 'culture',
      location: '社区活动中心三楼',
      startTime: '2024-01-20 09:00:00',
      endTime: '2024-01-20 11:30:00',
      maxParticipants: 20,
      currentParticipants: 15,
      organizer: '李老师',
      status: 'active'
    },
    {
      id: 2,
      title: '广场舞健身活动',
      description: '每天早晨的广场舞健身活动，强身健体，愉悦心情。',
      type: 'sports',
      location: '社区广场',
      startTime: '2024-01-18 07:00:00',
      endTime: '2024-01-18 08:30:00',
      maxParticipants: 50,
      currentParticipants: 42,
      organizer: '王阿姨',
      status: 'active'
    },
    {
      id: 3,
      title: '智能手机使用培训',
      description: '帮助老年人学习智能手机的基本操作，包括微信使用、视频通话、健康码等。',
      type: 'study',
      location: '社区活动中心二楼',
      startTime: '2024-01-22 14:00:00',
      endTime: '2024-01-22 16:00:00',
      maxParticipants: 30,
      currentParticipants: 28,
      organizer: '社区志愿者',
      status: 'active'
    },
    {
      id: 4,
      title: '茶艺品鉴会',
      description: '品味茶香，感受茶文化的魅力，交流品茶心得。',
      type: 'social',
      location: '社区茶室',
      startTime: '2024-01-25 10:00:00',
      endTime: '2024-01-25 12:00:00',
      maxParticipants: 15,
      currentParticipants: 10,
      organizer: '张老师',
      status: 'active'
    },
    {
      id: 5,
      title: '健康养生讲座',
      description: '邀请专业医生讲解老年人健康养生知识，解答健康疑问。',
      type: 'health',
      location: '社区活动中心一楼大厅',
      startTime: '2024-01-28 14:30:00',
      endTime: '2024-01-28 16:30:00',
      maxParticipants: 80,
      currentParticipants: 65,
      organizer: '社区卫生服务中心',
      status: 'active'
    },
    {
      id: 6,
      title: '棋牌友谊赛',
      description: '麻将、象棋、围棋等棋牌类比赛，增进邻里感情。',
      type: 'social',
      location: '社区棋牌室',
      startTime: '2024-01-19 09:00:00',
      endTime: '2024-01-19 12:00:00',
      maxParticipants: 40,
      currentParticipants: 35,
      organizer: '棋牌协会',
      status: 'active'
    }
  ]
}

const showActivityDetail = (activity) => {
  selectedActivity.value = activity
  showDetailModal.value = true
}

const registerActivity = async (activity) => {
  if (!activity) return
  
  if (isParticipating(activity.id)) {
    await unregisterActivity(activity)
    return
  }
  
  try {
    const userId = localStorage.getItem('userId')
    const response = await activityAPI.register(activity.id, userId)
    
    if (response.code === 200) {
      registeredActivities.value.add(activity.id)
      activity.currentParticipants++
      ElMessage.success('报名成功')
      showDetailModal.value = false
    } else {
      ElMessage.error('报名失败')
    }
  } catch (error) {
    ElMessage.error('报名失败')
  }
}

const unregisterActivity = async (activity) => {
  try {
    const userId = parseInt(localStorage.getItem('userId'))
    console.log('Unregister params:', { activityId: activity.id, userId })
    
    const response = await activityAPI.unregister(activity.id, userId)
    console.log('Unregister response:', response)
    
    if (response && response.code === 200) {
      registeredActivities.value.delete(activity.id)
      activity.currentParticipants--
      ElMessage.success('取消报名成功')
      showDetailModal.value = false
    } else {
      ElMessage.error(response?.message || '取消报名失败')
      loadActivities()
    }
  } catch (error) {
    console.error('Unregister error:', error)
    ElMessage.error('取消报名失败')
    loadActivities()
  }
}

const createActivity = async () => {
  if (!newActivity.value.title || !newActivity.value.location) {
    ElMessage.error('请填写必填项')
    return
  }
  
  try {
    const response = await activityAPI.create(newActivity.value)
    
    if (response.code === 200) {
      ElMessage.success('创建成功')
      showCreateModal.value = false
      newActivity.value = {
        title: '',
        description: '',
        type: 'culture',
        location: '',
        startTime: '',
        endTime: '',
        maxParticipants: 20,
        organizer: ''
      }
      loadActivities()
    } else {
      ElMessage.error('创建失败')
    }
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

watch(activeTab, () => {
  loadActivities()
})

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.activities-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  margin-bottom: 24px;
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
  font-size: 14px;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.create-btn svg {
  width: 18px;
  height: 18px;
}

.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  white-space: nowrap;
}

.tab-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
  color: white;
}

.tab-btn svg {
  width: 16px;
  height: 16px;
}

.tab-count {
  background: rgba(0, 0, 0, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 20px;
}

.activity-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.activity-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.activity-cover {
  position: relative;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
}

.cover-image {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
}

.cover-image svg {
  width: 64px;
  height: 64px;
}

.activity-type {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.activity-type.culture {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
}

.activity-type.sports {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.activity-type.study {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.activity-type.social {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.activity-type.health {
  background: linear-gradient(135deg, #ec4899 0%, #db2777 100%);
}

.activity-content {
  padding: 20px;
}

.activity-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.activity-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-info {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.info-item svg {
  width: 14px;
  height: 14px;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.participants {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
}

.participants svg {
  width: 16px;
  height: 16px;
}

.register-btn {
  padding: 8px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  color: white;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.register-btn:hover:not(:disabled) {
  transform: scale(1.05);
}

.register-btn.cancel-btn {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.register-btn.cancel-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.4);
}

.register-btn.large {
  padding: 12px 32px;
  font-size: 15px;
  border-radius: 25px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.detail-modal, .create-modal {
  background: white;
  border-radius: 20px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #1e293b;
}

.close-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s;
}

.close-btn:hover {
  background: #e2e8f0;
}

.close-btn svg {
  width: 18px;
  height: 18px;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: 50vh;
}

.detail-info {
  margin-bottom: 24px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.detail-label {
  font-size: 14px;
  color: #64748b;
}

.detail-value {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.detail-desc h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #1e293b;
}

.detail-desc p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #667eea;
}

.form-group textarea {
  resize: none;
}

.cancel-btn {
  padding: 12px 24px;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.cancel-btn:hover {
  background: #e2e8f0;
}

.submit-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .activities-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-tabs {
    gap: 8px;
  }
  
  .tab-btn {
    padding: 8px 14px;
    font-size: 13px;
  }
  
  .activity-info {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
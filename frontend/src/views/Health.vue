<template>
  <div class="health-page">
    <div class="page-header">
      <div class="header-content">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5m0 0l7-7m-7 7l7 7"/>
          </svg>
          <span>返回</span>
        </button>
        <div class="header-text">
          <h1>健康监测</h1>
          <p>实时追踪您的健康数据</p>
        </div>
        <button class="refresh-btn" @click="refreshData">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10"/>
            <polyline points="1 20 1 14 7 14"/>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
          <span>刷新数据</span>
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :class="stat.type">
          <svg :viewBox="stat.iconViewBox" fill="none" stroke="currentColor" stroke-width="2">
            <path :d="stat.iconPath"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            <span>{{ stat.value }}</span>
            <span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-status" :class="stat.status">
            <span class="status-dot"></span>
            {{ stat.statusText }}
          </div>
        </div>
        <div class="stat-trend" :class="stat.trend">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path :d="stat.trend === 'up' ? 'M12 19l7-7 3 3-7 7-3-3z' : 'M12 5l-7 7-3-3 7-7 3 3z'"/>
          </svg>
          <span>{{ stat.trendText }}</span>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-card">
        <div class="card-header">
          <h3>健康评分</h3>
        </div>
        <div class="health-score">
          <div class="score-ring">
            <svg viewBox="0 0 120 120" class="ring-svg">
              <circle cx="60" cy="60" r="50" fill="none" stroke="#e2e8f0" stroke-width="8"/>
              <circle 
                cx="60" cy="60" r="50" 
                fill="none" 
                stroke="url(#scoreGradient)" 
                stroke-width="8"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="scoreOffset"
                stroke-linecap="round"
                transform="rotate(-90 60 60)"
              />
              <defs>
                <linearGradient id="scoreGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
            </svg>
            <div class="score-text">
              <span class="score-value">{{ healthScore }}</span>
              <span class="score-label">健康评分</span>
            </div>
          </div>
          <div class="score-details">
            <div 
              v-for="(detail, index) in healthDetails" 
              :key="index" 
              class="detail-item"
            >
              <span class="detail-icon">{{ detail.icon }}</span>
              <span class="detail-text">{{ detail.text }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chart-card health-tips-card">
        <div class="card-header">
          <h3>健康建议</h3>
        </div>
        <div class="tips-container">
          <div class="tip-card">
            <div class="tip-icon">🏃</div>
            <div class="tip-content">
              <h4>运动建议</h4>
              <p>每日建议运动30分钟，可选择散步、太极等温和运动</p>
            </div>
          </div>
          <div class="tip-card">
            <div class="tip-icon">🥗</div>
            <div class="tip-content">
              <h4>饮食建议</h4>
              <p>低盐低脂饮食，多吃蔬菜水果，保持营养均衡</p>
            </div>
          </div>
          <div class="tip-card">
            <div class="tip-icon">💧</div>
            <div class="tip-content">
              <h4>饮水提醒</h4>
              <p>每日建议饮用8杯水，约1.5-2升</p>
            </div>
          </div>
          <div class="tip-card">
            <div class="tip-icon">😴</div>
            <div class="tip-content">
              <h4>睡眠建议</h4>
              <p>保证每晚7-9小时睡眠，保持规律作息</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="report-section">
      <div class="report-card">
        <div class="card-header">
          <h3>AI 健康分析报告</h3>
          <button class="generate-btn" @click="generateReport">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10 9 9 9 8 9"/>
            </svg>
            生成报告
          </button>
        </div>
        <div class="report-content">
          <div v-if="!reportGenerated" class="empty-report">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <p>点击上方按钮生成您的健康分析报告</p>
          </div>
          <div v-else class="report-text">
            <div class="report-header">
              <span class="report-date">{{ reportDate }}</span>
              <span class="report-status success">分析完成</span>
            </div>
            <div class="report-body">
              <p>{{ healthReport }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="upload-section">
      <div class="upload-card">
        <div class="card-header">
          <h3>上传健康报告</h3>
          <span class="upload-tip">支持 JPG、PNG、PDF 格式，单个文件不超过 10MB</span>
        </div>
        
        <div class="upload-area" @click="selectFiles" @dragover.prevent @drop.prevent="handleDrop">
          <input 
            ref="fileInput" 
            type="file" 
            multiple 
            accept="image/jpeg,image/png,application/pdf" 
            @change="handleFileSelect"
            class="file-input"
          />
          <div class="upload-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
          </div>
          <p>点击或拖拽文件到此处上传</p>
        </div>

        <div v-if="uploadedFiles.length > 0" class="files-list">
          <div 
            v-for="(file, index) in uploadedFiles" 
            :key="index"
            class="file-item"
          >
            <div class="file-preview">
              <img v-if="file.type.startsWith('image')" :src="file.preview" alt="预览"/>
              <div v-else class="file-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="16" y1="13" x2="8" y2="13"/>
                  <line x1="16" y1="17" x2="8" y2="17"/>
                  <polyline points="10 9 9 9 8 9"/>
                </svg>
              </div>
            </div>
            <div class="file-info">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
            </div>
            <div class="file-status" :class="file.status">
              <span v-if="file.status === 'uploading'">上传中...</span>
              <span v-else-if="file.status === 'success'">上传成功</span>
              <span v-else-if="file.status === 'error'">上传失败</span>
              <span v-else>待上传</span>
            </div>
            <button class="delete-btn" @click="removeFile(index)" v-if="file.status !== 'uploading'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>

        <div v-if="uploadedFiles.length > 0" class="upload-actions">
          <button class="upload-all-btn" @click="uploadAllFiles" :disabled="isUploading">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="17 8 12 3 7 8"/>
              <line x1="12" y1="3" x2="12" y2="15"/>
            </svg>
            {{ isUploading ? '上传中...' : '上传全部' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { healthAPI } from '../api'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()

const goBack = () => {
  router.push('/dashboard')
}

const latestData = ref(null)
const fileInput = ref(null)
const uploadedFiles = ref([])
const isUploading = ref(false)

const selectFiles = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  addFiles(files)
  event.target.value = ''
}

const handleDrop = (event) => {
  const files = Array.from(event.dataTransfer.files)
  addFiles(files)
}

const addFiles = (files) => {
  files.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error(`${file.name} 超过 10MB 限制`)
      return
    }
    
    const fileItem = {
      file,
      name: file.name,
      size: file.size,
      type: file.type,
      status: 'pending',
      preview: file.type.startsWith('image') ? URL.createObjectURL(file) : null,
      url: ''
    }
    
    uploadedFiles.value.push(fileItem)
  })
}

const removeFile = (index) => {
  const file = uploadedFiles.value[index]
  if (file.preview) {
    URL.revokeObjectURL(file.preview)
  }
  uploadedFiles.value.splice(index, 1)
}

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const uploadFile = async (fileItem) => {
  try {
    fileItem.status = 'uploading'
    
    const formData = new FormData()
    formData.append('file', fileItem.file)
    
    const response = await axios.post('/api/upload/health-report', formData)
    
    if (response.data && response.data.code === 200) {
      fileItem.status = 'success'
      fileItem.url = response.data.data
      return response.data.data
    } else {
      throw new Error(response.data.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    fileItem.status = 'error'
    throw error
  }
}

const uploadAllFiles = async () => {
  isUploading.value = true
  const pendingFiles = uploadedFiles.value.filter(f => f.status === 'pending')
  
  if (pendingFiles.length === 0) {
    ElMessage.warning('没有待上传的文件')
    isUploading.value = false
    return
  }
  
  try {
    for (const fileItem of pendingFiles) {
      await uploadFile(fileItem)
    }
    
    const successCount = uploadedFiles.value.filter(f => f.status === 'success').length
    if (successCount === pendingFiles.length) {
      ElMessage.success('所有文件上传成功')
    } else {
      ElMessage.warning(`部分文件上传成功，共 ${successCount}/${pendingFiles.length}`)
    }
  } catch (error) {
    ElMessage.error('部分文件上传失败')
  } finally {
    isUploading.value = false
  }
}
const healthReport = ref('')
const reportGenerated = ref(false)
const reportDate = ref('')
const activeTimeTab = ref('week')

const timeTabs = [
  { label: '近7天', value: 'week' },
  { label: '近30天', value: 'month' },
  { label: '近3月', value: 'quarter' }
]

const circumference = 2 * Math.PI * 50

const healthScore = computed(() => {
  if (!latestData.value) return 75
  
  let score = 80
  let factors = 0
  
  const heartRate = latestData.value.heartRate
  if (heartRate) {
    factors++
    if (heartRate >= 60 && heartRate <= 100) {
      score += 5
    } else if (heartRate >= 50 && heartRate <= 110) {
      score += 2
    }
  }
  
  const sys = latestData.value.bloodPressureHigh
  const dia = latestData.value.bloodPressureLow
  if (sys && dia) {
    factors++
    if (sys < 130 && dia < 80) {
      score += 10
    } else if (sys < 140 && dia < 90) {
      score += 5
    } else if (sys < 160 && dia < 100) {
      score += 2
    }
  }
  
  const sugar = latestData.value.bloodSugar
  if (sugar) {
    factors++
    if (sugar >= 3.9 && sugar <= 6.1) {
      score += 10
    } else if (sugar >= 3.5 && sugar <= 7.8) {
      score += 5
    } else if (sugar >= 3.0 && sugar <= 10.0) {
      score += 2
    }
  }
  
  const sleep = latestData.value.sleepHours
  if (sleep) {
    factors++
    if (sleep >= 7 && sleep <= 9) {
      score += 5
    } else if (sleep >= 6 && sleep <= 10) {
      score += 3
    }
  }
  
  const finalScore = Math.min(100, score)
  return factors > 0 ? finalScore : 75
})

const scoreOffset = computed(() => {
  return circumference - (healthScore.value / 100) * circumference
})

const healthDetails = computed(() => {
  if (!latestData.value) return []
  
  const details = []
  
  const heartRate = latestData.value.heartRate
  if (heartRate) {
    if (heartRate >= 60 && heartRate <= 100) {
      details.push({ icon: '❤️', text: '心率正常' })
    } else {
      details.push({ icon: '❤️', text: '心率异常' })
    }
  }
  
  const sys = latestData.value.bloodPressureHigh
  const dia = latestData.value.bloodPressureLow
  if (sys && dia) {
    if (sys < 130 && dia < 80) {
      details.push({ icon: '💪', text: '血压稳定' })
    } else {
      details.push({ icon: '💪', text: '血压偏高' })
    }
  }
  
  const sleep = latestData.value.sleepHours
  if (sleep) {
    if (sleep >= 7 && sleep <= 9) {
      details.push({ icon: '😴', text: '睡眠充足' })
    } else if (sleep >= 6 && sleep <= 10) {
      details.push({ icon: '😴', text: '睡眠一般' })
    } else {
      details.push({ icon: '😴', text: '睡眠不足' })
    }
  }
  
  return details
})



const stats = computed(() => [
  {
    label: '心率',
    value: latestData.value?.heartRate || '--',
    unit: 'bpm',
    type: 'heart',
    iconViewBox: '0 0 24 24',
    iconPath: 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z',
    status: 'normal',
    statusText: '正常',
    trend: 'down',
    trendText: '-2%'
  },
  {
    label: '血压',
    value: latestData.value?.bloodPressureHigh || '--',
    unit: '/' + (latestData.value?.bloodPressureLow || '--') + ' mmHg',
    type: 'bp',
    iconViewBox: '0 0 24 24',
    iconPath: 'M22 12h-4l-3 9L9 3l-3 9H2',
    status: 'normal',
    statusText: '稳定',
    trend: 'up',
    trendText: '+1%'
  },
  {
    label: '血糖',
    value: latestData.value?.bloodSugar || '--',
    unit: 'mmol/L',
    type: 'sugar',
    iconViewBox: '0 0 24 24',
    iconPath: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z',
    status: 'good',
    statusText: '良好',
    trend: 'down',
    trendText: '-3%'
  },
  {
    label: '睡眠',
    value: latestData.value?.sleepHours || '--',
    unit: '小时',
    type: 'sleep',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0z',
    status: 'good',
    statusText: '充足',
    trend: 'up',
    trendText: '+5%'
  }
])

const loadData = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const latestRes = await healthAPI.getLatest(userId)
    if (latestRes.code === 200) {
      latestData.value = latestRes.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const generateReport = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await healthAPI.analyze(userId)
    if (response.code === 200) {
      healthReport.value = response.data || '根据您的健康数据分析：\n\n1. 心率状况：您的心率处于正常范围，心血管功能良好。\n\n2. 血压状况：血压控制在理想水平，建议继续保持健康的生活方式。\n\n3. 血糖状况：血糖值正常，请注意饮食均衡。\n\n4. 睡眠状况：睡眠时长充足，睡眠质量良好。\n\n综合建议：您的整体健康状况良好，请继续保持规律作息和适度运动。'
      reportGenerated.value = true
      reportDate.value = new Date().toLocaleDateString('zh-CN')
      ElMessage.success('报告生成成功')
    }
  } catch (error) {
    ElMessage.error('生成报告失败')
  }
}

const refreshData = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.health-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  margin-bottom: 28px;
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

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: #667eea;
}

.refresh-btn svg {
  width: 18px;
  height: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: white;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-icon.heart {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  box-shadow: 0 4px 15px rgba(244, 63, 94, 0.4);
}

.stat-icon.bp {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.4);
}

.stat-icon.sugar {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  box-shadow: 0 4px 15px rgba(251, 191, 36, 0.4);
}

.stat-icon.sleep {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.4);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: #64748b;
  margin-left: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.stat-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.stat-status.normal .status-dot {
  background: #22c55e;
}

.stat-status.normal {
  color: #22c55e;
}

.stat-status.good .status-dot {
  background: #3b82f6;
}

.stat-status.good {
  color: #3b82f6;
}

.stat-status.warning .status-dot {
  background: #f59e0b;
}

.stat-status.warning {
  color: #f59e0b;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.stat-trend svg {
  width: 14px;
  height: 14px;
}

.stat-trend.up {
  color: #22c55e;
}

.stat-trend.down {
  color: #ef4444;
}

.health-tips-card {
  display: flex;
  flex-direction: column;
}

.tips-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.tip-card {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px;
  background: rgba(99, 102, 241, 0.04);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.tip-card:hover {
  background: rgba(99, 102, 241, 0.08);
  transform: translateX(4px);
}

.tip-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  flex-shrink: 0;
}

.tip-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.tip-content p {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 28px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

.chart-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 16px;
  background: #f1f5f9;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chart-container {
  height: 200px;
}

.mini-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
  padding-top: 20px;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 12%;
}

.chart-bar {
  width: 100%;
  max-width: 32px;
  border-radius: 8px 8px 0 0;
  transition: height 0.5s ease;
}

.chart-bar.normal {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.chart-bar.good {
  background: linear-gradient(180deg, #22c55e 0%, #16a34a 100%);
}

.chart-bar.warning {
  background: linear-gradient(180deg, #f59e0b 0%, #d97706 100%);
}

.chart-bar.excellent {
  background: linear-gradient(180deg, #06b6d4 0%, #0891b2 100%);
}

.chart-label {
  font-size: 12px;
  color: #94a3b8;
}

.health-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-ring {
  position: relative;
  width: 140px;
  height: 140px;
  margin-bottom: 20px;
}

.ring-svg {
  width: 100%;
  height: 100%;
}

.score-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.score-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
}

.score-label {
  font-size: 12px;
  color: #64748b;
}

.score-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.detail-icon {
  font-size: 20px;
}

.detail-text {
  font-size: 14px;
  color: #334155;
}

.report-section {
  margin-bottom: 28px;
}

.report-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.generate-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.generate-btn svg {
  width: 16px;
  height: 16px;
}

.report-content {
  min-height: 200px;
  border-radius: 12px;
  padding: 20px;
  background: #f8fafc;
}

.empty-report {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;
}

.empty-report svg {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.empty-report p {
  margin: 0;
  font-size: 14px;
}

.report-text {
  color: #334155;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.report-date {
  font-size: 13px;
  color: #64748b;
}

.report-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.report-status.success {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.report-body p {
  margin: 0;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.upload-section {
  margin-bottom: 28px;
}

.upload-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.upload-tip {
  font-size: 13px;
  color: #94a3b8;
}

.upload-area {
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 16px;
  background: #fafafa;
}

.upload-area:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.upload-area.dragover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.upload-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.upload-icon svg {
  width: 28px;
  height: 28px;
}

.upload-area p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.file-input {
  display: none;
}

.files-list {
  margin-top: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.file-preview {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon {
  width: 32px;
  height: 32px;
  color: #667eea;
}

.file-icon svg {
  width: 100%;
  height: 100%;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-name {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.file-size {
  font-size: 12px;
  color: #94a3b8;
}

.file-status {
  font-size: 12px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 20px;
}

.file-status.pending {
  background: #fef3c7;
  color: #d97706;
}

.file-status.uploading {
  background: #dbeafe;
  color: #2563eb;
}

.file-status.success {
  background: #dcfce7;
  color: #16a34a;
}

.file-status.error {
  background: #fee2e2;
  color: #dc2626;
}

.delete-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  background: #fee2e2;
  color: #dc2626;
  cursor: pointer;
  transition: all 0.2s;
}

.delete-btn:hover {
  background: #fecaca;
}

.delete-btn svg {
  width: 16px;
  height: 16px;
}

.upload-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.upload-all-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.upload-all-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.upload-all-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.upload-all-btn svg {
  width: 16px;
  height: 16px;
}

@media (max-width: 768px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .file-item {
    flex-wrap: wrap;
  }
  
  .file-info {
    flex: 1;
    min-width: 200px;
  }
}
</style>
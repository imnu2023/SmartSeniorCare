<template>
  <div class="family-pay-page">
    <div class="page-header">
      <div class="header-text">
        <h1>家属代付</h1>
        <p>为您关联的老人支付订单</p>
      </div>
    </div>

    <div v-if="elderList.length > 0" class="elder-selector">
      <div class="selector-label">选择老人</div>
      <div class="elder-cards">
        <div 
          v-for="elder in elderList" 
          :key="elder.id"
          :class="['elder-card', { active: selectedElderId === elder.id }]"
          @click="selectElder(elder.id)"
        >
          <div class="elder-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="elder-info">
            <span class="elder-name">{{ elder.name }}</span>
            <span class="elder-phone">{{ elder.phone }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedElderId && orderList.length > 0" class="order-section">
      <h3>待支付订单</h3>
      <div class="order-list">
        <div 
          v-for="order in pendingOrders" 
          :key="order.id" 
          class="order-card"
        >
          <div class="card-header">
            <div class="order-info">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <span class="status-tag pending">待支付</span>
          </div>
          
          <div class="card-body">
            <div class="service-card">
              <div class="service-icon" :class="getServiceType(order.serviceId)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path :d="getServiceIcon(order.serviceId)"/>
                </svg>
              </div>
              <div class="service-info">
                <h4>{{ getServiceName(order.serviceId) }}</h4>
                <p>{{ getServiceTypeName(order.serviceId) }}</p>
              </div>
            </div>
            
            <div class="order-details">
              <div class="detail-row">
                <span class="detail-icon">🕐</span>
                <span class="detail-text">{{ order.serviceTime }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-icon">📍</span>
                <span class="detail-text">{{ order.address }}</span>
              </div>
              <div class="detail-row total">
                <span class="detail-icon">💰</span>
                <span class="detail-text">¥{{ order.amount }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-footer">
            <button class="pay-btn" @click="openPayModal(order)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="9" cy="9" r="7"/>
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              代付 ¥{{ order.amount }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="pendingOrders.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 7h-9M14 17H5m15-4h-9"/>
          <circle cx="17" cy="7" r="2"/>
          <circle cx="7" cy="17" r="2"/>
          <circle cx="17" cy="17" r="2"/>
        </svg>
        <p>暂无待支付订单</p>
      </div>
    </div>

    <div v-if="!selectedElderId && elderList.length > 0" class="empty-hint">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
      </svg>
      <p>请选择一位老人查看订单</p>
    </div>

    <div v-if="elderList.length === 0" class="empty-state">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M17 20h5v-2a3 3 0 0 0-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 0 1 5.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 0 1 9.288 0M15 7a3 3 0 1 1-6 0 3 3 0 0 1 6 0zm6 3a2 2 0 1 1-4 0 2 2 0 0 1 4 0zM7 10a2 2 0 1 1-4 0 2 2 0 0 1 4 0z"/>
      </svg>
      <p>暂无关联的老人</p>
    </div>

    <div v-if="showPayModal" class="modal-overlay" @click="closePayModal">
      <div class="modal-content pay-modal" @click.stop>
        <div class="modal-header">
          <h3>确认代付</h3>
          <button class="close-btn" @click="closePayModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div v-if="orderToPay" class="modal-body">
          <div class="pay-info">
            <div class="info-section">
              <h4>订单信息</h4>
              <div class="info-item">
                <span class="info-label">订单号</span>
                <span class="info-value">{{ orderToPay.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务项目</span>
                <span class="info-value">{{ getServiceName(orderToPay.serviceId) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务时间</span>
                <span class="info-value">{{ orderToPay.serviceTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务地址</span>
                <span class="info-value">{{ orderToPay.address }}</span>
              </div>
            </div>
            
            <div class="divider"></div>
            
            <div class="pay-section">
              <div class="pay-item total">
                <span class="pay-label">代付金额</span>
                <span class="pay-value">¥{{ orderToPay.amount }}</span>
              </div>
              <div class="wallet-info">
                <span class="wallet-label">您的钱包余额</span>
                <span :class="['wallet-value', { warning: walletBalance < orderToPay.amount }]">¥{{ walletBalance }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closePayModal">取消</button>
          <button 
            class="btn-primary" 
            :disabled="walletBalance < (orderToPay?.amount || 0)" 
            @click="handleFamilyPay"
          >
            {{ walletBalance < (orderToPay?.amount || 0) ? '余额不足' : '确认代付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { relationAPI, orderAPI, serviceAPI, paymentAPI, walletAPI } from '../api'
import { ElMessage } from 'element-plus'

const elderList = ref([])
const serviceList = ref([])
const orderList = ref([])
const selectedElderId = ref(null)
const showPayModal = ref(false)
const orderToPay = ref(null)
const walletBalance = ref(0)

const pendingOrders = computed(() => {
  return orderList.value.filter(o => o.status === 'pending' && o.paymentStatus !== 'paid')
})

const loadElders = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await relationAPI.getByFamilyMember(parseInt(userId))
    if (response.code === 200 && response.data) {
      elderList.value = response.data.map(r => ({
        id: r.elderId,
        name: r.elderName,
        phone: r.elderPhone
      }))
      
      if (elderList.value.length > 0) {
        selectedElderId.value = elderList.value[0].id
        loadOrders()
      }
    }
  } catch (error) {
    ElMessage.error('加载老人列表失败')
  }
}

const loadServices = async () => {
  try {
    const response = await serviceAPI.getAll()
    if (response.code === 200) {
      serviceList.value = response.data
    }
  } catch (error) {
    console.error('加载服务列表失败')
  }
}

const loadOrders = async () => {
  if (!selectedElderId.value) return
  
  try {
    const response = await orderAPI.getByUser(selectedElderId.value)
    if (response.code === 200) {
      orderList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const loadWalletBalance = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await walletAPI.getBalance(parseInt(userId))
    if (response.code === 200) {
      walletBalance.value = response.data.balance
    }
  } catch (error) {
    walletBalance.value = 0
  }
}

const selectElder = (elderId) => {
  selectedElderId.value = elderId
  loadOrders()
}

const getServiceName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceName : '未知服务'
}

const getServiceTypeName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  if (!service) return ''
  const types = {
    housekeeping: '家政服务',
    medical: '医疗护理',
    life: '生活服务',
    culture: '文化娱乐'
  }
  return types[service.serviceType] || service.serviceType
}

const getServiceType = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceType : 'default'
}

const getServiceIcon = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  if (!service) return 'M20 7h-9M14 17H5m15-4h-9'
  
  const icons = {
    housekeeping: 'M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21M16 11.3A6 6 0 1 1 10.7 6',
    medical: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0',
    life: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
    culture: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z'
  }
  return icons[service.serviceType] || icons.life
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const openPayModal = async (order) => {
  orderToPay.value = order
  await loadWalletBalance()
  showPayModal.value = true
}

const closePayModal = () => {
  showPayModal.value = false
  orderToPay.value = null
}

const handleFamilyPay = async () => {
  if (!orderToPay.value) return
  
  try {
    const familyMemberId = parseInt(localStorage.getItem('userId'))
    const response = await paymentAPI.familyPay(orderToPay.value.id, familyMemberId)
    
    if (response.code === 200) {
      ElMessage.success('代付成功')
      closePayModal()
      loadOrders()
      loadWalletBalance()
    }
  } catch (error) {
    ElMessage.error('代付失败')
  }
}

onMounted(() => {
  loadElders()
  loadServices()
  loadWalletBalance()
})
</script>

<style scoped>
.family-pay-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  margin-bottom: 28px;
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

.elder-selector {
  margin-bottom: 28px;
}

.selector-label {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  margin-bottom: 16px;
}

.elder-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.elder-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 14px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 200px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.elder-card:hover {
  border-color: #e2e8f0;
  transform: translateY(-2px);
}

.elder-card.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.elder-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.elder-avatar svg {
  width: 22px;
  height: 22px;
}

.elder-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.elder-name {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.elder-phone {
  font-size: 13px;
  color: #64748b;
}

.order-section {
  max-width: 600px;
}

.order-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #1e293b;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-size: 13px;
  color: #64748b;
}

.order-date {
  font-size: 12px;
  color: #94a3b8;
}

.status-tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.status-tag.pending {
  background: rgba(251, 191, 36, 0.1);
  color: #f59e0b;
}

.card-body {
  margin-bottom: 16px;
}

.service-card {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.service-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.service-icon svg {
  width: 24px;
  height: 24px;
}

.service-icon.housekeeping {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.service-icon.medical {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
}

.service-icon.life {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.service-icon.culture {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.service-icon.default {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.service-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #1e293b;
}

.service-info p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.order-details {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-row.total .detail-text {
  font-size: 18px;
  font-weight: 700;
  color: #ef4444;
}

.detail-icon {
  font-size: 16px;
}

.detail-text {
  font-size: 14px;
  color: #334155;
}

.card-footer {
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
}

.pay-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.pay-btn svg {
  width: 18px;
  height: 18px;
}

.empty-state,
.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #94a3b8;
}

.empty-state svg,
.empty-hint svg {
  width: 56px;
  height: 56px;
  margin-bottom: 16px;
}

.empty-state p,
.empty-hint p {
  margin: 0;
  font-size: 14px;
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
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 480px;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f1f5f9;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.modal-body {
  padding: 24px;
}

.pay-info {
  display: flex;
  flex-direction: column;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.divider {
  height: 1px;
  background: #e2e8f0;
  margin: 16px 0;
}

.pay-section {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
}

.pay-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pay-item.total {
  margin-bottom: 16px;
}

.pay-label {
  font-size: 14px;
  color: #64748b;
}

.pay-value {
  font-size: 24px;
  font-weight: 700;
  color: #ef4444;
}

.wallet-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px dashed #cbd5e1;
}

.wallet-label {
  font-size: 13px;
  color: #64748b;
}

.wallet-value {
  font-size: 16px;
  font-weight: 600;
  color: #22c55e;
}

.wallet-value.warning {
  color: #ef4444;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary,
.btn-primary {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary {
  background: #f1f5f9;
  color: #64748b;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .elder-cards {
    flex-direction: column;
  }
  
  .elder-card {
    width: 100%;
  }
}
</style>

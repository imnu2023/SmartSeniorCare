<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <div class="header-actions">
        <el-select v-model="statusFilter" placeholder="筛选状态">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
      </div>
    </div>
    
    <el-table :data="filteredOrders" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="serviceId" label="服务ID" />
      <el-table-column prop="serviceName" label="服务名称" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="serviceTime" label="服务时间" />
      <el-table-column prop="address" label="服务地址">
        <template #default="scope">
          {{ fixAddress(scope.row.address) }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额">
        <template #default="scope">
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态">
        <template #default="scope">
          <el-tag :type="scope.row.paymentStatus === 'paid' ? 'success' : 'warning'">
            {{ scope.row.paymentStatus === 'paid' ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'pending'" type="primary" size="small" @click="updateStatus(scope.row.id, 'completed')">完成</el-button>
          <el-button v-if="scope.row.status === 'pending'" type="danger" size="small" @click="updateStatus(scope.row.id, 'cancelled')">取消</el-button>
          <el-button type="info" size="small" @click="viewOrder(scope.row)">查看</el-button>
          <el-button type="danger" size="small" @click="deleteOrder(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="订单详情" v-model="showDetailModal">
      <div v-if="selectedOrder" class="order-detail">
        <div class="detail-row">
          <span class="label">订单号</span>
          <span class="value">{{ selectedOrder.orderNo }}</span>
        </div>
        <div class="detail-row">
          <span class="label">用户ID</span>
          <span class="value">{{ selectedOrder.userId }}</span>
        </div>
        <div class="detail-row">
          <span class="label">服务名称</span>
          <span class="value">{{ getServiceName(selectedOrder.serviceId) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">服务时间</span>
          <span class="value">{{ selectedOrder.serviceTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">服务地址</span>
          <span class="value">{{ fixAddress(selectedOrder.address) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">金额</span>
          <span class="value">¥{{ selectedOrder.amount }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态</span>
          <span class="value">{{ getStatusText(selectedOrder.status) }}</span>
        </div>
        <div class="detail-row">
          <span class="label">支付状态</span>
          <span class="value">{{ selectedOrder.paymentStatus === 'paid' ? '已支付' : '未支付' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">备注</span>
          <span class="value">{{ selectedOrder.remark || '无' }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderAPI, serviceAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const orderList = ref([])
const serviceList = ref([])
const statusFilter = ref('')
const showDetailModal = ref(false)
const selectedOrder = ref(null)

const filteredOrders = computed(() => {
  let orders = orderList.value.map(order => ({
    ...order,
    serviceName: getServiceName(order.serviceId)
  }))
  
  if (statusFilter.value) {
    orders = orders.filter(o => o.status === statusFilter.value)
  }
  
  return orders
})

const loadOrders = async () => {
  try {
    const response = await orderAPI.getAll()
    if (response.code === 200) {
      orderList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
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

const getServiceName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceName : '未知服务'
}

const fixAddress = (address) => {
  if (!address) return ''
  if (address.includes('Community') || address.includes('Building')) {
    return '幸福社区1号楼101室'
  }
  if (address === '1') {
    return '幸福社区'
  }
  return address
}

const updateStatus = async (orderId, status) => {
  try {
    const response = await orderAPI.updateStatus(orderId, status)
    if (response.code === 200) {
      ElMessage.success('状态已更新')
      loadOrders()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const viewOrder = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const deleteOrder = async (orderId) => {
  await ElMessageBox.confirm('确定要删除该订单吗？', '确认删除', {
    type: 'warning'
  })
  
  try {
    const response = await orderAPI.delete(orderId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  if (status === 'completed') return 'success'
  if (status === 'pending') return 'warning'
  return 'danger'
}

const getStatusText = (status) => {
  if (status === 'pending') return '待处理'
  if (status === 'completed') return '已完成'
  return '已取消'
}

onMounted(() => {
  loadOrders()
  loadServices()
})
</script>

<style scoped>
.admin-orders {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.order-detail {
  padding: 10px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  font-weight: 500;
  color: #666;
}

.detail-row .value {
  color: #333;
}
</style>
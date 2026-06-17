<template>
  <div class="admin-devices">
    <div class="page-header">
      <h2>设备管理</h2>
      <button class="add-btn" @click="openAddModal">添加设备</button>
    </div>
    
    <el-table :data="deviceList" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="deviceName" label="设备名称">
        <template #default="scope">
          {{ fixDeviceName(scope.row.deviceName) }}
        </template>
      </el-table-column>
      <el-table-column prop="deviceType" label="设备类型">
        <template #default="scope">
          {{ getDeviceTypeName(scope.row.deviceType) }}
        </template>
      </el-table-column>
      <el-table-column prop="deviceId" label="设备ID" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="位置">
        <template #default="scope">
          {{ fixLocation(scope.row.location) }}
        </template>
      </el-table-column>
      <el-table-column prop="lastOnlineTime" label="最后在线" />
      <el-table-column prop="createdAt" label="添加时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <button class="edit-btn" @click="openEditModal(scope.row)">编辑</button>
          <button class="delete-btn" @click="handleDelete(scope.row.id)">删除</button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="showAddModal" class="modal-overlay" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加设备</h3>
          <button class="close-btn" @click="closeAddModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>设备名称</label>
            <input v-model="newDevice.deviceName" type="text" placeholder="请输入设备名称" />
          </div>
          <div class="form-item">
            <label>设备类型</label>
            <select v-model="newDevice.deviceType">
              <option value="light">智能灯光</option>
              <option value="aircon">智能空调</option>
              <option value="curtain">智能窗帘</option>
              <option value="security">安防设备</option>
              <option value="sensor">传感器</option>
            </select>
          </div>
          <div class="form-item">
            <label>设备ID</label>
            <input v-model="newDevice.deviceId" type="text" placeholder="请输入设备ID" />
          </div>
          <div class="form-item">
            <label>位置</label>
            <input v-model="newDevice.location" type="text" placeholder="请输入位置" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeAddModal">取消</button>
          <button class="btn-primary" @click="handleAdd">添加</button>
        </div>
      </div>
    </div>

    <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑设备</h3>
          <button class="close-btn" @click="closeEditModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>设备名称</label>
            <input v-model="editDevice.deviceName" type="text" placeholder="请输入设备名称" />
          </div>
          <div class="form-item">
            <label>设备类型</label>
            <select v-model="editDevice.deviceType">
              <option value="light">智能灯光</option>
              <option value="aircon">智能空调</option>
              <option value="curtain">智能窗帘</option>
              <option value="security">安防设备</option>
              <option value="sensor">传感器</option>
            </select>
          </div>
          <div class="form-item">
            <label>设备ID</label>
            <input v-model="editDevice.deviceId" type="text" placeholder="请输入设备ID" />
          </div>
          <div class="form-item">
            <label>位置</label>
            <input v-model="editDevice.location" type="text" placeholder="请输入位置" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeEditModal">取消</button>
          <button class="btn-primary" @click="handleEdit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { deviceAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const deviceList = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)

const newDevice = ref({
  deviceName: '',
  deviceType: 'light',
  deviceId: '',
  location: ''
})

const editDevice = ref({
  id: '',
  deviceName: '',
  deviceType: 'light',
  deviceId: '',
  location: ''
})

const loadDevices = async () => {
  try {
    const response = await deviceAPI.getAll()
    if (response.code === 200) {
      deviceList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载设备列表失败')
  }
}

const fixDeviceName = (name) => {
  if (!name) return ''
  if (name.includes('?')) {
    const mapping = {
      '???': '智能灯光',
      '????': '智能空调',
      '?????': '智能窗帘'
    }
    return mapping[name] || '未知设备'
  }
  return name
}

const fixLocation = (location) => {
  if (!location) return ''
  if (location.includes('?')) {
    return '未知位置'
  }
  return location
}

const getDeviceTypeName = (type) => {
  const types = {
    light: '智能灯光',
    aircon: '智能空调',
    curtain: '智能窗帘',
    security: '安防设备',
    sensor: '传感器'
  }
  return types[type] || type
}

const getStatusType = (status) => {
  if (status === 'online' || status === 'active') return 'success'
  if (status === 'inactive') return 'warning'
  return 'danger'
}

const getStatusText = (status) => {
  if (status === 'online') return '在线'
  if (status === 'active') return '运行中'
  if (status === 'inactive') return '已关闭'
  return '离线'
}

const openAddModal = () => {
  newDevice.value = {
    deviceName: '',
    deviceType: 'light',
    deviceId: '',
    location: ''
  }
  showAddModal.value = true
}

const closeAddModal = () => {
  showAddModal.value = false
}

const handleAdd = async () => {
  if (!newDevice.value.deviceName || !newDevice.value.deviceId) {
    ElMessage.warning('请填写设备名称和设备ID')
    return
  }
  
  try {
    const response = await deviceAPI.add(newDevice.value)
    if (response.code === 200) {
      ElMessage.success('添加成功')
      closeAddModal()
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const openEditModal = (device) => {
  editDevice.value = {
    id: device.id,
    deviceName: fixDeviceName(device.deviceName),
    deviceType: device.deviceType,
    deviceId: device.deviceId,
    location: fixLocation(device.location)
  }
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const handleEdit = async () => {
  if (!editDevice.value.deviceName || !editDevice.value.deviceId) {
    ElMessage.warning('请填写设备名称和设备ID')
    return
  }
  
  try {
    const response = await deviceAPI.update(editDevice.value.id, editDevice.value)
    if (response.code === 200) {
      ElMessage.success('更新成功')
      closeEditModal()
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    const response = await deviceAPI.delete(id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadDevices()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
.admin-devices {
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

.add-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.add-btn:hover {
  opacity: 0.9;
}

.edit-btn, .delete-btn {
  padding: 4px 10px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.edit-btn {
  background: #409EFF;
  color: white;
}

.delete-btn {
  background: #F56C6C;
  color: white;
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

.modal-content {
  background: white;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #999;
  padding: 4px;
}

.modal-body {
  padding: 16px;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
}

.form-item input,
.form-item select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px;
  border-top: 1px solid #eee;
}

.btn-secondary, .btn-primary {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-secondary {
  background: #eee;
  color: #666;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}
</style>
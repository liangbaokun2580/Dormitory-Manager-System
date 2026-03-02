<template>
  <div class="page">
    <div class="page-header">
      <div class="filters">
        <select v-model="filterDorm" class="filter-select">
          <option value="">全部楼栋</option>
          <option v-for="d in dormitories" :key="d" :value="d">{{ d }}</option>
        </select>
        <select v-model="filterStatus" class="filter-select">
          <option value="">全部状态</option>
          <option value="空">空</option>
          <option value="部分入住">部分入住</option>
          <option value="已满">已满</option>
        </select>
      </div>
      <button class="btn-primary" @click="openCreateForm">新建房间</button>
    </div>
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>楼栋</th>
            <th>楼层</th>
            <th>房间号</th>
            <th>容量</th>
            <th>已住</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in rooms" :key="r.id">
            <td>{{ r.dormitory }}</td>
            <td>{{ r.floor }} 层</td>
            <td>{{ r.number }}</td>
            <td>{{ r.capacity }} 人</td>
            <td>{{ r.occupied }} 人</td>
            <td>
              <span class="badge" :class="statusClass(r.status)">{{ r.status }}</span>
            </td>
            <td>
              <button class="btn-link" @click="openDetail(r)">查看详情</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 房间详情弹窗 -->
    <div v-if="detailVisible" class="modal-overlay" @click.self="detailVisible = false">
      <div class="modal">
        <h3>房间详情</h3>
        <div v-if="detailRoom" class="detail-grid">
          <div class="detail-item">
            <span class="label">楼栋</span>
            <span class="value">{{ detailRoom.dormitory }}</span>
          </div>
          <div class="detail-item">
            <span class="label">楼层</span>
            <span class="value">{{ detailRoom.floor }} 层</span>
          </div>
          <div class="detail-item">
            <span class="label">房间号</span>
            <span class="value">{{ detailRoom.number }}</span>
          </div>
          <div class="detail-item">
            <span class="label">容量</span>
            <span class="value">{{ detailRoom.capacity }} 人</span>
          </div>
          <div class="detail-item">
            <span class="label">已住</span>
            <span class="value">{{ detailRoom.occupied }} 人</span>
          </div>
          <div class="detail-item">
            <span class="label">状态</span>
            <span class="value">
              <span class="badge" :class="statusClass(detailRoom.status)">{{ detailRoom.status }}</span>
            </span>
          </div>
          <div class="detail-item full">
            <span class="label">剩余床位</span>
            <span class="value">{{ (detailRoom.capacity || 0) - (detailRoom.occupied || 0) }} 床</span>
          </div>
        </div>
        
        <!-- 学生列表 -->
        <div v-if="roomStudents.length > 0" class="students-list">
          <h4>学生信息</h4>
          <div class="students-table">
            <div class="student-header">
              <span>姓名</span>
              <span>学号</span>
              <span>系部</span>
              <span>年级</span>
              <span>操作</span>
            </div>
            <div v-for="student in roomStudents" :key="student.id" class="student-row">
              <span>{{ student.name }}</span>
              <span>{{ student.studentId }}</span>
              <span>{{ student.college }}</span>
              <span>{{ student.grade }}</span>
              <button class="btn-link danger" @click="removeStudentFromRoom(student)">移除</button>
            </div>
          </div>
        </div>
        <div v-else-if="detailRoom && detailRoom.occupied > 0" class="no-students">
          <p>暂无学生信息</p>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn-primary" @click="detailVisible = false">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="createVisible" class="modal-overlay" @click.self="createVisible = false">
      <div class="modal">
        <h3>新建房间</h3>
        <form @submit.prevent="submitCreate">
          <div class="form-row">
            <div class="form-group">
              <label>楼栋</label>
              <select v-model="createForm.dormitory" required>
                <option value="">请选择楼栋</option>
                <option v-for="d in dormitories" :key="d" :value="d">{{ d }}</option>
              </select>
            </div>
            <div class="form-group">
              <label>楼层</label>
              <input v-model.number="createForm.floor" type="number" min="1" required />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>房间号</label>
              <input v-model.trim="createForm.number" required placeholder="请输入房间号" />
            </div>
            <div class="form-group">
              <label>容量</label>
              <input v-model.number="createForm.capacity" type="number" min="1" required />
            </div>
          </div>
          <p v-if="createError" class="form-error">{{ createError }}</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="createVisible = false">取消</button>
            <button type="submit" class="btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { rooms as roomsApi, dormitories as dormitoriesApi, students as studentsApi } from '@/api'

const route = useRoute()
const filterDorm = ref('')
const filterStatus = ref('')
const rooms = ref([])
const dormitories = ref([])
const detailVisible = ref(false)
const detailRoom = ref(null)
const roomStudents = ref([])
const refreshInterval = ref(null)
const dormRefreshInterval = ref(null)
const createVisible = ref(false)
const createError = ref('')
const createForm = ref({
  dormitory: '',
  floor: 1,
  number: '',
  capacity: 4,
})

async function openDetail(room) {
  detailRoom.value = room
  const roomKey = getRoomKey(room)
  try {
    const allStudents = await studentsApi.list()
    roomStudents.value = allStudents.filter((s) => s.room === roomKey)
  } catch {
    roomStudents.value = []
  }
  detailVisible.value = true
}

function getRoomKey(room) {
  return `${room.dormitory}-${room.number}`
}

async function refreshDetail() {
  if (!detailRoom.value) return
  await fetchRooms()
  const latestRoom = rooms.value.find((r) => r.id === detailRoom.value.id)
  if (latestRoom) {
    detailRoom.value = latestRoom
    const allStudents = await studentsApi.list()
    roomStudents.value = allStudents.filter((s) => s.room === getRoomKey(latestRoom))
  }
}

async function removeStudentFromRoom(student) {
  if (!confirm(`确认将 ${student.name} 移出当前宿舍吗？`)) return
  try {
    await studentsApi.deallocate(student.id)
    await refreshDetail()
  } catch (e) {
    alert(e.message || '移除失败')
  }
}

async function fetchRooms() {
  try {
    rooms.value = await roomsApi.list(filterDorm.value || undefined, filterStatus.value || undefined)
  } catch {
    rooms.value = []
  }
}

async function fetchDormitories() {
  try {
    const list = await dormitoriesApi.list()
    dormitories.value = list.map((d) => d.name)
  } catch {}
}

watch([filterDorm, filterStatus], fetchRooms)

function statusClass(status) {
  if (status === '已满') return 'full'
  if (status === '部分入住') return 'partial'
  return 'empty'
}

function openCreateForm() {
  createError.value = ''
  createForm.value = {
    dormitory: dormitories.value[0] || '',
    floor: 1,
    number: '',
    capacity: 4,
  }
  createVisible.value = true
}

async function submitCreate() {
  createError.value = ''
  if (!createForm.value.dormitory) {
    createError.value = '请选择楼栋'
    return
  }
  if (!createForm.value.number) {
    createError.value = '请输入房间号'
    return
  }
  try {
    await roomsApi.create({
      dormitory: createForm.value.dormitory,
      floor: createForm.value.floor,
      number: createForm.value.number,
      capacity: createForm.value.capacity,
      occupied: 0,
    })
    createVisible.value = false
    await fetchRooms()
  } catch (e) {
    createError.value = e.message || '新建失败'
  }
}

onMounted(async () => {
  const dorm = route.query.dorm
  const status = route.query.status
  if (dorm) filterDorm.value = dorm
  if (status) filterStatus.value = status
  await fetchDormitories()
  fetchRooms()
  
  // 设置定时刷新，每5秒更新一次数据
  refreshInterval.value = setInterval(() => {
    fetchRooms()
  }, 5000)

  // 楼栋列表也实时刷新，保证筛选和新建房间下拉是最新数据
  dormRefreshInterval.value = setInterval(() => {
    fetchDormitories()
  }, 5000)
})

onUnmounted(() => {
  // 清除定时器
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
  }
  if (dormRefreshInterval.value) {
    clearInterval(dormRefreshInterval.value)
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.filters {
  display: flex;
  gap: 1rem;
}

.filter-select {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  color: var(--color-text);
}

.btn-primary {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.table-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  overflow-x: auto;
}

.data-table {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem 1.25rem;
  text-align: left;
}

.data-table th {
  background: var(--color-surface-elevated);
  color: var(--color-text-muted);
  font-size: 0.8125rem;
  font-weight: 500;
}

.badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8125rem;
}

.badge.full {
  background: rgba(248, 113, 113, 0.2);
  color: var(--color-danger);
}

.badge.partial {
  background: rgba(251, 191, 36, 0.2);
  color: var(--color-warning);
}

.badge.empty {
  background: rgba(52, 211, 153, 0.2);
  color: var(--color-success);
}

.btn-link {
  color: var(--color-primary-light);
  font-size: 0.875rem;
}

.btn-link:hover {
  text-decoration: underline;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.5rem;
  width: 100%;
  max-width: 400px;
}

.modal h3 {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.form-group label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.form-group input,
.form-group select {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.form-error {
  color: var(--color-danger);
  font-size: 0.875rem;
  margin: 0.25rem 0 0;
}

.btn-secondary {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  color: var(--color-text-muted);
  border-radius: var(--radius-sm);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .filters {
    flex-direction: column;
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-item.full {
  grid-column: 1 / -1;
}

.detail-item .label {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

.detail-item .value {
  font-size: 0.9375rem;
}

.students-list {
  margin-top: 1.5rem;
  border-top: 1px solid var(--color-border);
  padding-top: 1.5rem;
}

.students-list h4 {
  margin-bottom: 1rem;
  font-size: 0.9rem;
  font-weight: 500;
}

.students-table {
  width: 100%;
  border-collapse: collapse;
}

.student-header {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr auto;
  gap: 0.5rem;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.8125rem;
  color: var(--color-text-muted);
  font-weight: 500;
}

.student-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr auto;
  gap: 0.5rem;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--color-border);
  font-size: 0.875rem;
}

.student-row:last-child {
  border-bottom: none;
}

.btn-link.danger {
  color: var(--color-danger);
}

.no-students {
  margin-top: 1.5rem;
  text-align: center;
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

.modal-actions {
  margin-top: 1.25rem;
  display: flex;
  justify-content: flex-end;
}
</style>

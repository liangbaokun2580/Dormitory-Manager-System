<template>
  <div class="student-page">
    <header class="header">
      <div>
        <h1>我的宿舍</h1>
        <p>欢迎，{{ student?.name || '同学' }}</p>
      </div>
      <button class="btn-secondary" @click="logout">退出登录</button>
    </header>

    <div class="info-grid">
      <div class="info-card">
        <span class="label">学号</span>
        <span class="value">{{ student?.studentId || '-' }}</span>
      </div>
      <div class="info-card">
        <span class="label">姓名</span>
        <span class="value">{{ student?.name || '-' }}</span>
      </div>
      <div class="info-card">
        <span class="label">系部/专业</span>
        <span class="value">{{ student?.college || '-' }} {{ student?.major || '' }}</span>
      </div>
      <div class="info-card">
        <span class="label">宿舍</span>
        <span class="value">{{ student?.room || '暂未分配' }}</span>
      </div>
    </div>

    <div class="card">
      <h3>我要报修</h3>
      <p v-if="!student?.room" class="tip">当前未分配宿舍，无法提交报修。</p>
      <p v-else class="tip">将自动提交到当前宿舍：{{ student.room }}</p>
      <form v-if="student?.room" @submit.prevent="submitRepair">
        <div class="form-row">
          <div class="form-group">
            <label>问题标题</label>
            <input v-model.trim="form.title" required placeholder="请输入问题" />
          </div>
          <div class="form-group">
            <label>联系方式（可选）</label>
            <input v-model.trim="form.contact" placeholder="请输入联系方式" />
          </div>
        </div>
        <div class="form-group">
          <label>问题描述</label>
          <textarea v-model.trim="form.description" rows="4" placeholder="请描述问题详情"></textarea>
        </div>
        <p v-if="formError" class="error">{{ formError }}</p>
        <button class="btn-primary" type="submit">提交报修</button>
      </form>
    </div>

    <div class="card">
      <div class="card-head">
        <h3>我的报修记录</h3>
        <select v-model="statusFilter" class="filter-select">
          <option value="">全部状态</option>
          <option value="待处理">待处理</option>
          <option value="处理中">处理中</option>
          <option value="已完成">已完成</option>
        </select>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>标题</th>
              <th>宿舍</th>
              <th>状态</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in repairs" :key="item.id">
              <td>{{ item.title }}</td>
              <td>{{ item.dormitory }}-{{ item.roomNumber }}</td>
              <td><span class="badge" :class="statusClass(item.status)">{{ item.status }}</span></td>
              <td>{{ formatTime(item.createdAt) }}</td>
            </tr>
            <tr v-if="repairs.length === 0">
              <td colspan="4" class="empty">暂无报修记录</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { studentPortal } from '@/api'

const router = useRouter()
const student = ref(null)
const repairs = ref([])
const statusFilter = ref('')
const formError = ref('')
const refreshTimer = ref(null)
const form = ref({
  title: '',
  description: '',
  contact: '',
})

function formatTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString()
}

function statusClass(status) {
  if (status === '已完成') return 'done'
  if (status === '处理中') return 'doing'
  return 'todo'
}

async function loadStudent() {
  const studentId = sessionStorage.getItem('studentId')
  if (!studentId) {
    logout()
    return
  }
  student.value = await studentPortal.profile(studentId)
  sessionStorage.setItem('studentProfile', JSON.stringify(student.value))
}

async function loadRepairs() {
  const studentId = sessionStorage.getItem('studentId')
  if (!studentId) return
  repairs.value = await studentPortal.repairs(studentId, statusFilter.value || undefined)
}

async function submitRepair() {
  if (!student.value?.room) return
  if (!form.value.title) {
    formError.value = '请填写报修标题'
    return
  }
  formError.value = ''
  try {
    await studentPortal.submitRepair({
      title: form.value.title,
      description: [form.value.description, form.value.contact ? `联系方式：${form.value.contact}` : '']
        .filter(Boolean)
        .join('\n'),
      studentId: student.value.studentId,
    })
    form.value = { title: '', description: '', contact: '' }
    await loadRepairs()
  } catch (e) {
    formError.value = e.message || '提交失败'
  }
}

function logout() {
  sessionStorage.removeItem('studentToken')
  sessionStorage.removeItem('studentId')
  sessionStorage.removeItem('studentProfile')
  router.push('/student/login')
}

watch(statusFilter, loadRepairs)

onMounted(async () => {
  try {
    await loadStudent()
    await loadRepairs()
    refreshTimer.value = setInterval(loadRepairs, 5000)
  } catch {
    logout()
  }
})

onUnmounted(() => {
  if (refreshTimer.value) clearInterval(refreshTimer.value)
})
</script>

<style scoped>
.student-page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  font-size: 1.5rem;
}

.header p {
  color: var(--color-text-muted);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.75rem;
}

.info-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 0.9rem;
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.8rem;
  color: var(--color-text-muted);
}

.value {
  margin-top: 0.2rem;
  font-weight: 600;
}

.card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1rem;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 0.75rem;
}

.form-group input,
.form-group textarea,
.filter-select {
  padding: 0.52rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.btn-primary {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
}

.btn-secondary {
  padding: 0.45rem 0.9rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.tip {
  color: var(--color-text-muted);
}

.error {
  color: var(--color-danger);
  margin-bottom: 0.6rem;
}

.table-wrap {
  overflow-x: auto;
}

.table {
  width: 100%;
  min-width: 620px;
  border-collapse: collapse;
  margin-top: 0.75rem;
}

.table th,
.table td {
  padding: 0.75rem 0.9rem;
  text-align: left;
}

.table th {
  color: var(--color-text-muted);
  background: var(--color-surface-elevated);
  font-size: 0.85rem;
}

.badge {
  padding: 0.15rem 0.45rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.badge.todo {
  background: rgba(251, 191, 36, 0.2);
  color: var(--color-warning);
}

.badge.doing {
  background: rgba(6, 182, 212, 0.2);
  color: var(--color-accent);
}

.badge.done {
  background: rgba(52, 211, 153, 0.2);
  color: var(--color-success);
}

.empty {
  text-align: center;
  color: var(--color-text-muted);
}

@media (max-width: 900px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

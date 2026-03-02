<template>
  <div class="page">
    <div class="page-header">
      <div class="filters">
        <input v-model.trim="keyword" class="filter-input" placeholder="搜索楼栋/房间/标题/报修人" />
        <select v-model="status" class="filter-select">
          <option value="">全部状态</option>
          <option value="待处理">待处理</option>
          <option value="处理中">处理中</option>
          <option value="已完成">已完成</option>
        </select>
      </div>
      <button class="btn-primary" @click="openForm()">新增工单</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>楼栋</th>
            <th>房间</th>
            <th>标题</th>
            <th>报修人</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in repairs" :key="item.id">
            <td>{{ item.dormitory }}</td>
            <td>{{ item.roomNumber }}</td>
            <td>{{ item.title }}</td>
            <td>{{ item.reporter || '-' }}</td>
            <td><span class="badge" :class="statusClass(item.status)">{{ item.status }}</span></td>
            <td>{{ formatTime(item.createdAt) }}</td>
            <td>
              <button class="btn-link" @click="openForm(item)">编辑</button>
              <button v-if="item.status !== '已完成'" class="btn-link" @click="completeRepair(item.id)">完结</button>
              <button class="btn-link danger" @click="removeRepair(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="formVisible" class="modal-overlay" @click.self="formVisible = false">
      <div class="modal">
        <h3>{{ editingId ? '编辑工单' : '新增工单' }}</h3>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>楼栋</label>
              <input v-model.trim="form.dormitory" required placeholder="请输入楼栋" />
            </div>
            <div class="form-group">
              <label>房间号</label>
              <input v-model.trim="form.roomNumber" required placeholder="请输入房间号" />
            </div>
          </div>
          <div class="form-group">
            <label>报修标题</label>
            <input v-model.trim="form.title" required placeholder="请输入报修标题" />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>报修人</label>
              <input v-model.trim="form.reporter" placeholder="请输入报修人" />
            </div>
          </div>
          <div class="form-group">
            <label>问题描述</label>
            <textarea v-model.trim="form.description" rows="4" placeholder="请描述故障现象"></textarea>
          </div>
          <p v-if="formError" class="form-error">{{ formError }}</p>
          <div class="modal-actions">
            <button type="button" class="btn-secondary" @click="formVisible = false">取消</button>
            <button type="submit" class="btn-primary">{{ editingId ? '保存' : '新增' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { repairs as repairsApi } from '@/api'

const repairs = ref([])
const status = ref('')
const keyword = ref('')
const refreshInterval = ref(null)

const formVisible = ref(false)
const editingId = ref(null)
const formError = ref('')
const form = ref({
  dormitory: '',
  roomNumber: '',
  title: '',
  description: '',
  reporter: '',
  status: '待处理',
})

function formatTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString()
}

function statusClass(value) {
  if (value === '已完成') return 'done'
  if (value === '处理中') return 'doing'
  return 'todo'
}

async function fetchRepairs() {
  try {
    repairs.value = await repairsApi.list(status.value || undefined, keyword.value || undefined)
  } catch {
    repairs.value = []
  }
}

watch([status, keyword], fetchRepairs)

function openForm(row) {
  editingId.value = row ? row.id : null
  formError.value = ''
  if (row) {
    form.value = {
      dormitory: row.dormitory || '',
      roomNumber: row.roomNumber || '',
      title: row.title || '',
      description: row.description || '',
      reporter: row.reporter || '',
      status: row.status || '待处理',
    }
  } else {
    form.value = {
      dormitory: '',
      roomNumber: '',
      title: '',
      description: '',
      reporter: '',
      status: '待处理',
    }
  }
  formVisible.value = true
}

async function submitForm() {
  formError.value = ''
  try {
    if (editingId.value) {
      await repairsApi.update(editingId.value, { ...form.value })
    } else {
      await repairsApi.create({ ...form.value })
    }
    formVisible.value = false
    await fetchRepairs()
  } catch (e) {
    formError.value = e.message || '操作失败'
  }
}

async function completeRepair(id) {
  try {
    await repairsApi.complete(id)
    await fetchRepairs()
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

async function removeRepair(id) {
  if (!confirm('确认删除该工单？')) return
  try {
    await repairsApi.delete(id)
    await fetchRepairs()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

onMounted(() => {
  fetchRepairs()
  refreshInterval.value = setInterval(fetchRepairs, 5000)
})

onUnmounted(() => {
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
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
  gap: 0.75rem;
}

.filter-input,
.filter-select {
  padding: 0.5rem 0.9rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  color: var(--color-text);
}

.filter-input {
  min-width: 280px;
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
  min-width: 880px;
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
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8125rem;
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

.btn-link {
  color: var(--color-primary-light);
  font-size: 0.875rem;
  margin-right: 0.75rem;
}

.btn-link.danger {
  color: var(--color-danger);
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
  max-width: 560px;
}

.modal h3 {
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 1rem;
}

.form-group label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.5rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.form-group textarea {
  resize: vertical;
}

.form-error {
  color: var(--color-danger);
  font-size: 0.875rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1rem;
}

.btn-secondary {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
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

  .filter-input {
    min-width: 0;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

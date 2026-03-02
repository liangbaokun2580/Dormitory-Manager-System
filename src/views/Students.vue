<template>
  <div class="page">
    <div class="page-header">
      <div class="search-bar">
        <input v-model="searchQuery" type="text" placeholder="搜索姓名、学号..." />
      </div>
      <button class="btn-primary" @click="openForm()">新增学生</button>
    </div>
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>性别</th>
            <th>系部</th>
            <th>年级</th>
            <th>宿舍</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in students" :key="s.id">
            <td>{{ s.studentId }}</td>
            <td>{{ s.name }}</td>
            <td>{{ s.gender }}</td>
            <td>{{ s.college }}</td>
            <td>{{ s.grade }}</td>
            <td>
              <span v-if="s.room" class="badge">{{ s.room }}</span>
              <span v-else class="badge empty">未分配</span>
            </td>
            <td>
              <button class="btn-link" @click="openForm(s)">编辑</button>
              <button class="btn-link danger" @click="handleDelete(s.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="formVisible" class="modal-overlay" @click.self="formVisible = false">
      <div class="modal">
        <h3>{{ editingId ? '编辑学生' : '新增学生' }}</h3>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>姓名</label>
              <input v-model="form.name" required placeholder="请输入姓名" />
            </div>
            <div class="form-group">
              <label>学号</label>
              <input v-model="form.studentId" required placeholder="请输入学号" :readonly="!!editingId" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>性别</label>
              <select v-model="form.gender">
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </div>
            <div class="form-group">
              <label>年级</label>
              <input v-model="form.grade" placeholder="请输入年级" />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>系部</label>
              <input v-model="form.college" placeholder="请输入系部" />
            </div>
            <div class="form-group">
              <label>专业</label>
              <input v-model="form.major" placeholder="请输入专业" />
            </div>
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
import { students as studentsApi } from '@/api'

const searchQuery = ref('')
const students = ref([])
const loading = ref(false)
const formVisible = ref(false)
const editingId = ref(null)
const formError = ref('')
const form = ref({
  name: '',
  studentId: '',
  gender: '男',
  college: '',
  major: '',
  grade: '',
})
const refreshInterval = ref(null)

function openForm(row) {
  editingId.value = row ? row.id : null
  formError.value = ''
  if (row) {
    form.value = {
      name: row.name,
      studentId: row.studentId,
      gender: row.gender || '男',
      college: row.college || '',
      major: row.major || '',
      grade: row.grade || '',
    }
  } else {
    form.value = {
      name: '',
      studentId: '',
      gender: '男',
      college: '',
      major: '',
      grade: '',
    }
  }
  formVisible.value = true
}

async function submitForm() {
  formError.value = ''
  try {
    if (editingId.value) {
      await studentsApi.update(editingId.value, { ...form.value })
    } else {
      await studentsApi.create({ ...form.value })
    }
    formVisible.value = false
    fetchStudents(searchQuery.value?.trim() || undefined)
  } catch (e) {
    formError.value = e.message || '操作失败'
  }
}

async function fetchStudents(keyword) {
  loading.value = true
  try {
    students.value = await studentsApi.list(keyword)
  } catch {
    students.value = []
  } finally {
    loading.value = false
  }
}

watch(searchQuery, (q) => {
  fetchStudents(q?.trim() || undefined)
})

async function handleDelete(id) {
  if (!confirm('确定删除该学生？')) return
  try {
    await studentsApi.delete(id)
    await fetchStudents(searchQuery.value?.trim() || undefined)
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

onMounted(() => {
  fetchStudents()
  
  // 设置定时刷新，每5秒更新一次数据
  refreshInterval.value = setInterval(() => {
    fetchStudents(searchQuery.value?.trim() || undefined)
  }, 5000)
})

onUnmounted(() => {
  // 清除定时器
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
  }
})

</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.search-bar input {
  width: 280px;
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  color: var(--color-text);
}

.search-bar input::placeholder {
  color: var(--color-text-muted);
}

.btn-primary {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.btn-primary:hover {
  background: var(--color-primary-light);
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

.data-table tr:hover td {
  background: rgba(124, 58, 237, 0.05);
}

.badge {
  padding: 0.25rem 0.5rem;
  background: rgba(52, 211, 153, 0.2);
  color: var(--color-success);
  border-radius: 4px;
  font-size: 0.8125rem;
}

.badge.empty {
  background: rgba(166, 173, 200, 0.2);
  color: var(--color-text-muted);
}

.btn-link {
  color: var(--color-primary-light);
  font-size: 0.875rem;
  margin-right: 0.75rem;
}

.btn-link:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .search-bar input {
    width: 100%;
  }
}

.btn-link.danger {
  color: var(--color-danger);
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
  max-width: 480px;
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

.form-group input:read-only {
  opacity: 0.8;
}

.form-error {
  color: var(--color-danger);
  font-size: 0.875rem;
  margin-bottom: 1rem;
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

.btn-secondary:hover {
  background: var(--color-border);
}
</style>

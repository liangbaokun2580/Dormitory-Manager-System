<template>
  <div class="page">
    <div class="page-header">
      <h2>楼栋列表</h2>
      <button class="btn-primary" @click="openForm()">新增楼栋</button>
    </div>
    <div class="cards-grid">
      <div
        v-for="d in dormitories"
        :key="d.id"
        class="dorm-card"
      >
        <div class="dorm-header">
          <span class="dorm-name">{{ d.name }}</span>
          <span class="dorm-type" :class="d.type === '男寝' ? 'male' : 'female'">
            {{ d.type }}
          </span>
        </div>
        <div class="dorm-stats">
          <div class="dorm-stat">
            <span class="value">{{ d.floors }}</span>
            <span class="label">楼层</span>
          </div>
          <div class="dorm-stat">
            <span class="value">{{ d.totalRooms }}</span>
            <span class="label">房间数</span>
          </div>
          <div class="dorm-stat">
            <span class="value">{{ d.capacity }}</span>
            <span class="label">床位</span>
          </div>
        </div>
        <div class="dorm-actions">
          <router-link :to="`/rooms?dorm=${encodeURIComponent(d.name)}`" class="btn-link">查看房间</router-link>
          <button class="btn-link" @click="openForm(d)">编辑</button>
          <button class="btn-link danger" @click="handleDelete(d.id)">删除</button>
        </div>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="formVisible" class="modal-overlay" @click.self="formVisible = false">
      <div class="modal">
        <h3>{{ editingId ? '编辑楼栋' : '新增楼栋' }}</h3>
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group">
              <label>楼栋名称</label>
              <input v-model="form.name" required placeholder="请输入楼栋名称" />
            </div>
            <div class="form-group">
              <label>类型</label>
              <select v-model="form.type">
                <option value="男寝">男寝</option>
                <option value="女寝">女寝</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>楼层数</label>
              <input v-model.number="form.floors" type="number" min="1" placeholder="请输入楼层" />
            </div>
            <div class="form-group">
              <label>房间总数</label>
              <input v-model.number="form.totalRooms" type="number" min="1" placeholder="请输入房间总数" />
            </div>
          </div>
          <div class="form-group">
            <label>床位容量</label>
            <input v-model.number="form.capacity" type="number" min="1" placeholder="请输入床位容量" />
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
import { ref, onMounted, onUnmounted } from 'vue'
import { dormitories as dormitoriesApi } from '@/api'

const dormitories = ref([])
const formVisible = ref(false)
const editingId = ref(null)
const formError = ref('')
const form = ref({
  name: '',
  floors: null,
  totalRooms: null,
  capacity: null,
  type: '男寝',
})
const refreshInterval = ref(null)

function openForm(row) {
  editingId.value = row ? row.id : null
  formError.value = ''
  if (row) {
    form.value = {
      name: row.name,
      floors: row.floors,
      totalRooms: row.totalRooms,
      capacity: row.capacity,
      type: row.type || '男寝',
    }
  } else {
    form.value = {
      name: '',
      floors: null,
      totalRooms: null,
      capacity: null,
      type: '男寝',
    }
  }
  formVisible.value = true
}

async function submitForm() {
  formError.value = ''
  if (!form.value.name?.trim()) {
    formError.value = '请填写楼栋名称'
    return
  }
  try {
    if (editingId.value) {
      await dormitoriesApi.update(editingId.value, { ...form.value })
    } else {
      await dormitoriesApi.create({ ...form.value })
    }
    formVisible.value = false
    fetchDormitories()
  } catch (e) {
    formError.value = e.message || '操作失败'
  }
}

async function fetchDormitories() {
  try {
    dormitories.value = await dormitoriesApi.list()
  } catch {}
}

async function handleDelete(id) {
  if (!confirm('确认删除该楼栋？若存在关联房间/学生可能删除失败。')) return
  try {
    await dormitoriesApi.delete(id)
    await fetchDormitories()
  } catch (e) {
    alert(e.message || '删除失败')
  }
}

onMounted(async () => {
  await fetchDormitories()
  
  // 设置定时刷新，每5秒更新一次数据
  refreshInterval.value = setInterval(() => {
    fetchDormitories()
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

.page-header h2 {
  font-size: 1.25rem;
}

.btn-primary {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.dorm-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.5rem;
  transition: var(--transition);
}

.dorm-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow);
}

.dorm-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.25rem;
}

.dorm-name {
  font-size: 1.25rem;
  font-weight: 600;
}

.dorm-type {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
}

.dorm-type.male {
  background: rgba(34, 211, 238, 0.2);
  color: var(--color-accent);
}

.dorm-type.female {
  background: rgba(251, 191, 36, 0.2);
  color: var(--color-warning);
}

.dorm-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.25rem;
}

.dorm-stat {
  display: flex;
  flex-direction: column;
}

.dorm-stat .value {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-primary-light);
}

.dorm-stat .label {
  font-size: 0.75rem;
  color: var(--color-text-muted);
}

.dorm-actions {
  display: flex;
  gap: 1rem;
}

.btn-link {
  color: var(--color-primary-light);
  font-size: 0.875rem;
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
  max-width: 420px;
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
  margin-bottom: 1rem;
}

.form-group:last-of-type {
  margin-bottom: 0;
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
  margin: 1rem 0 0;
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

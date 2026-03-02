<template>
  <div class="page">
    <header class="header">
      <div>
        <h1>维修工单中心</h1>
        <p>欢迎，{{ workerName }}</p>
      </div>
      <button class="btn-secondary" @click="logout">退出登录</button>
    </header>

    <div class="toolbar">
      <input v-model.trim="keyword" class="input" placeholder="搜索楼栋/房间/标题/报修人" />
      <select v-model="status" class="select">
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
            <th>报修位置</th>
            <th>标题</th>
            <th>报修人</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in repairs" :key="item.id">
            <td>{{ item.dormitory }}-{{ item.roomNumber }}</td>
            <td>{{ item.title }}</td>
            <td>{{ item.reporter || '-' }}</td>
            <td><span class="badge" :class="statusClass(item.status)">{{ item.status }}</span></td>
            <td>{{ formatTime(item.createdAt) }}</td>
            <td>
              <button v-if="item.status === '待处理'" class="btn-link" @click="accept(item.id)">接单</button>
              <button v-if="item.status !== '已完成'" class="btn-link" @click="complete(item.id)">完结</button>
            </td>
          </tr>
          <tr v-if="repairs.length === 0">
            <td colspan="6" class="empty">暂无工单</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { workerPortal } from '@/api'

const router = useRouter()
const repairs = ref([])
const keyword = ref('')
const status = ref('')
const timer = ref(null)

const workerName = computed(() => sessionStorage.getItem('workerName') || '维修师傅')

function formatTime(value) {
  if (!value) return '-'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return '-'
  return d.toLocaleString()
}

function statusClass(v) {
  if (v === '已完成') return 'done'
  if (v === '处理中') return 'doing'
  return 'todo'
}

async function fetchRepairs() {
  try {
    repairs.value = await workerPortal.repairs(status.value || undefined, keyword.value || undefined)
  } catch {
    repairs.value = []
  }
}

async function accept(id) {
  try {
    await workerPortal.accept(id)
    await fetchRepairs()
  } catch (e) {
    alert(e.message || '接单失败')
  }
}

async function complete(id) {
  try {
    await workerPortal.complete(id)
    await fetchRepairs()
  } catch (e) {
    alert(e.message || '完结失败')
  }
}

function logout() {
  sessionStorage.removeItem('workerToken')
  sessionStorage.removeItem('workerName')
  router.push('/worker/login')
}

watch([keyword, status], fetchRepairs)

onMounted(() => {
  fetchRepairs()
  timer.value = setInterval(fetchRepairs, 5000)
})

onUnmounted(() => {
  if (timer.value) clearInterval(timer.value)
})
</script>

<style scoped>
.page {
  max-width: 1120px;
  margin: 0 auto;
  padding: 1.2rem;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.header h1 {
  font-size: 1.5rem;
}

.header p {
  color: var(--color-text-muted);
}

.toolbar {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.input,
.select {
  padding: 0.52rem 0.75rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface);
  color: var(--color-text);
}

.input {
  flex: 1;
}

.table-wrap {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  overflow-x: auto;
}

.table {
  width: 100%;
  min-width: 760px;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: 0.9rem 1rem;
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

.btn-link {
  color: var(--color-primary-light);
  margin-right: 0.75rem;
}

.btn-link:hover {
  text-decoration: underline;
}

.btn-secondary {
  padding: 0.45rem 0.9rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.empty {
  text-align: center;
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .toolbar {
    flex-direction: column;
  }
}
</style>

<template>
  <div class="dashboard">
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <div class="stat-icon" :style="{ background: stat.color }">
          <component :is="stat.icon" />
        </div>
        <div class="stat-content">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>
    <div class="dashboard-grid">
      <div class="card">
        <h3>快捷操作</h3>
        <div class="quick-actions">
          <router-link to="/students" class="action-btn">新增学生</router-link>
          <router-link to="/allocation" class="action-btn">分配宿舍</router-link>
          <router-link to="/rooms?status=空" class="action-btn">查看空床</router-link>
        </div>
      </div>
      <div class="card">
        <h3>入住概况</h3>
        <div class="occupancy-chart">
          <div class="chart-bar" :style="{ height: occupancyPercent + '%' }">
            <span class="chart-label">{{ occupancyPercent }}%</span>
          </div>
        </div>
        <p class="chart-desc">当前入住率：{{ data.occupiedRooms }}/{{ data.totalRooms }} 间</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { stats as statsApi, dormitories as dormitoriesApi } from '@/api'
import IconUsers from '@/components/icons/IconUsers.vue'
import IconBuilding from '@/components/icons/IconBuilding.vue'
import IconDoor from '@/components/icons/IconDoor.vue'

const data = ref({
  totalStudents: 0,
  totalRooms: 0,
  occupiedRooms: 0,
  vacancy: 0,
})
const dormCount = ref(3)
const refreshInterval = ref(null)

async function fetchData() {
  try {
    const [s, d] = await Promise.all([statsApi.get(), dormitoriesApi.list()])
    data.value = s
    dormCount.value = d.length
  } catch {}
}

onMounted(() => {
  fetchData()
  
  // 设置定时刷新，每5秒更新一次数据
  refreshInterval.value = setInterval(() => {
    fetchData()
  }, 5000)
})

onUnmounted(() => {
  // 清除定时器
  if (refreshInterval.value) {
    clearInterval(refreshInterval.value)
  }
})

const stats = computed(() => [
  { label: '在校学生', value: data.value.totalStudents, icon: IconUsers, color: 'linear-gradient(135deg, #7c3aed, #a78bfa)' },
  { label: '宿舍楼栋', value: `${dormCount.value} 栋`, icon: IconBuilding, color: 'linear-gradient(135deg, #22d3ee, #67e8f9)' },
  { label: '房间总数', value: data.value.totalRooms, icon: IconDoor, color: 'linear-gradient(135deg, #34d399, #6ee7b7)' },
  { label: '剩余床位', value: data.value.vacancy, icon: IconDoor, color: 'linear-gradient(135deg, #fbbf24, #fcd34d)' },
])

const occupancyPercent = computed(() => {
  const t = data.value.totalRooms
  return t ? Math.round((data.value.occupiedRooms / t) * 100) : 0
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1.25rem;
  transition: var(--transition);
}

.stat-card:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  display: block;
}

.stat-label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

.card {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.5rem;
}

.card h3 {
  margin-bottom: 1rem;
  font-size: 1rem;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.action-btn {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
  transition: var(--transition);
}

.action-btn:hover {
  background: var(--color-primary-light);
  transform: translateY(-1px);
}

.occupancy-chart {
  height: 120px;
  background: var(--color-surface-elevated);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: flex-end;
  padding: 0.5rem;
  margin-bottom: 1rem;
}

.chart-bar {
  width: 100%;
  background: linear-gradient(180deg, var(--color-primary), var(--color-accent));
  border-radius: 4px;
  min-height: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
}

.chart-label {
  font-size: 0.75rem;
  color: white;
  font-weight: 600;
}

.chart-desc {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}
</style>

<template>
  <div class="page">
    <div class="stats-row">
      <div class="stat-box">
        <span class="stat-num">{{ data.totalStudents }}</span>
        <span class="stat-name">学生总数</span>
      </div>
      <div class="stat-box">
        <span class="stat-num">{{ data.totalRooms }}</span>
        <span class="stat-name">房间总数</span>
      </div>
      <div class="stat-box">
        <span class="stat-num">{{ data.occupiedRooms }}</span>
        <span class="stat-name">已入住房间</span>
      </div>
      <div class="stat-box">
        <span class="stat-num">{{ data.vacancy }}</span>
        <span class="stat-name">剩余床位</span>
      </div>
    </div>
    <div class="card">
      <h3>楼栋分布</h3>
      <div class="distribution-list">
        <div
          v-for="d in dormitories"
          :key="d.name"
          class="distribution-item"
        >
          <span class="dorm-name">{{ d.name }}</span>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: d.percent + '%' }"
            ></div>
          </div>
          <span class="dorm-detail">{{ d.occupiedBeds }}/{{ d.totalBeds }} 床</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { stats as statsApi, dormitories as dormitoriesApi, rooms as roomsApi } from '@/api'

const data = ref({
  totalStudents: 0,
  totalRooms: 0,
  occupiedRooms: 0,
  vacancy: 0,
})
const dormList = ref([])
const roomList = ref([])
const refreshInterval = ref(null)

async function fetchData() {
  try {
    const [s, d, r] = await Promise.all([statsApi.get(), dormitoriesApi.list(), roomsApi.list()])
    data.value = s
    dormList.value = d
    roomList.value = r
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

const dormitories = computed(() =>
  dormList.value
    .map((d) => {
      const dormRooms = roomList.value.filter((r) => r.dormitory === d.name)
      const totalBeds = dormRooms.reduce((sum, r) => sum + (r.capacity || 0), 0)
      const occupiedBeds = dormRooms.reduce((sum, r) => sum + (r.occupied || 0), 0)
      const percent = totalBeds > 0 ? Math.round((occupiedBeds / totalBeds) * 100) : 0
      return {
        ...d,
        totalBeds,
        occupiedBeds,
        percent,
      }
    })
    .sort((a, b) => {
      if (b.occupiedBeds !== a.occupiedBeds) return b.occupiedBeds - a.occupiedBeds
      return b.totalBeds - a.totalBeds
    })
)
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-box {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.5rem;
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-primary-light);
}

.stat-name {
  font-size: 0.875rem;
  color: var(--color-text-muted);
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

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.distribution-item {
  display: grid;
  grid-template-columns: 60px 1fr 80px;
  align-items: center;
  gap: 1rem;
}

.dorm-name {
  font-weight: 500;
}

.progress-bar {
  height: 8px;
  background: var(--color-surface-elevated);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
  border-radius: 4px;
  transition: width 0.3s ease;
}

.dorm-detail {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

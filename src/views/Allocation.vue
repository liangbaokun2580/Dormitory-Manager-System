<template>
  <div class="page">
    <div class="allocation-form card">
      <h3>分配宿舍</h3>
      <div class="form-row">
        <div class="form-group">
          <label>选择学生</label>
          <select v-model="selectedStudent" class="form-input">
            <option value="">请选择...</option>
            <option
              v-for="s in unassignedStudents"
              :key="s.id"
              :value="s.id"
            >
              {{ s.name }} ({{ s.studentId }}) - {{ s.college }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>选择房间</label>
          <select v-model="selectedRoom" class="form-input">
            <option value="">请选择...</option>
            <option
              v-for="r in availableRooms"
              :key="r.id"
              :value="r.id"
            >
              {{ r.dormitory }}-{{ r.number }} (剩余 {{ r.capacity - r.occupied }} 床)
            </option>
          </select>
        </div>
      </div>
      <button class="btn-primary" :disabled="!canSubmit" @click="handleAllocate">
        确认分配
      </button>
    </div>
    <div class="card">
      <h3>待分配学生</h3>
      <p v-if="unassignedStudents.length === 0" class="empty-tip">暂无待分配学生</p>
      <div v-else class="student-tags">
        <span
          v-for="s in unassignedStudents"
          :key="s.id"
          class="student-tag"
        >
          {{ s.name }} ({{ s.studentId }})
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { students as studentsApi, rooms as roomsApi } from '@/api'

const selectedStudent = ref('')
const selectedRoom = ref('')
const unassignedStudents = ref([])
const availableRooms = ref([])
const refreshInterval = ref(null)

const canSubmit = computed(
  () => selectedStudent.value && selectedRoom.value
)

const selectedStudentGender = computed(() => {
  const student = unassignedStudents.value.find((s) => s.id === selectedStudent.value)
  return student?.gender
})

async function fetchData() {
  try {
    const students = await studentsApi.unassigned()
    unassignedStudents.value = students
    availableRooms.value = await roomsApi.available(selectedStudentGender.value)
  } catch {}
}

async function handleAllocate() {
  if (!canSubmit.value) return
  try {
    await studentsApi.allocate(selectedStudent.value, selectedRoom.value)
    selectedStudent.value = ''
    selectedRoom.value = ''
    fetchData()
  } catch (e) {
    alert(e.message || '分配失败')
  }
}

watch(selectedStudent, async () => {
  selectedRoom.value = ''
  try {
    availableRooms.value = await roomsApi.available(selectedStudentGender.value)
  } catch {
    availableRooms.value = []
  }
})

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
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.form-input {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.btn-primary {
  padding: 0.5rem 1.25rem;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.empty-tip {
  color: var(--color-text-muted);
  font-size: 0.875rem;
}

.student-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.student-tag {
  padding: 0.35rem 0.75rem;
  background: var(--color-surface-elevated);
  border-radius: 20px;
  font-size: 0.875rem;
}

@media (max-width: 640px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1>学生端登录</h1>
      <p class="sub">请输入学号与姓名</p>

      <form class="login-form" @submit.prevent="handleSubmit">
        <label class="form-item">
          <span>学号</span>
          <input v-model="studentId" type="text" placeholder="请输入学号" />
        </label>
        <label class="form-item">
          <span>姓名</span>
          <input v-model="name" type="text" placeholder="请输入姓名" />
        </label>
        <p v-if="error" class="error-text">{{ error }}</p>
        <button class="btn-primary" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <router-link class="admin-link" to="/login">管理员登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { auth } from '@/api'

const router = useRouter()
const route = useRoute()
const studentId = ref('')
const name = ref('')
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  if (!studentId.value.trim() || !name.value.trim()) {
    error.value = '请填写学号和姓名'
    return
  }
  error.value = ''
  loading.value = true
  try {
    const res = await auth.studentLogin(studentId.value.trim(), name.value.trim())
    sessionStorage.setItem('studentToken', res.token)
    sessionStorage.setItem('studentId', res.student.studentId)
    sessionStorage.setItem('studentProfile', JSON.stringify(res.student))
    const redirect = route.query.redirect || '/student/home'
    router.push(redirect)
  } catch (e) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  padding: 1.8rem;
}

h1 {
  font-size: 1.4rem;
}

.sub {
  margin-top: 0.3rem;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.login-form {
  margin-top: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.form-item span {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.form-item input {
  padding: 0.55rem 0.8rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  color: var(--color-text);
}

.btn-primary {
  margin-top: 0.35rem;
  padding: 0.6rem 1rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #06b6d4, #4f46e5);
  color: white;
  font-weight: 500;
}

.error-text {
  color: var(--color-danger);
  font-size: 0.85rem;
}

.admin-link {
  margin-top: 1rem;
  display: inline-block;
  color: var(--color-primary-light);
  font-size: 0.9rem;
}
</style>

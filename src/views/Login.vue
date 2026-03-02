<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
        </div>
        <div class="title-wrap">
          <h1>宿舍管理系统</h1>
          <p>请输入账号密码登录</p>
        </div>
      </div>

      <form class="login-form" @submit.prevent="handleSubmit">
        <label class="form-item">
          <span>账号</span>
          <input
            v-model="username"
            type="text"
            placeholder="请输入账号"
            autocomplete="username"
          />
        </label>

        <label class="form-item">
          <span>密码</span>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </label>

        <p v-if="error" class="error-text">{{ error }}</p>

        <button class="btn-primary" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="links">
        <router-link class="student-link" to="/student/login">学生端登录</router-link>
        <router-link class="student-link" to="/worker/login">维修师傅登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { auth } from '@/api'

const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  if (!username.value.trim() || !password.value.trim()) {
    error.value = '请填写账号和密码'
    return
  }
  error.value = ''
  loading.value = true
  try {
    const res = await auth.login(username.value.trim(), password.value)
    sessionStorage.setItem('token', res.token)
    sessionStorage.setItem('username', res.username)
    const redirect = route.query.redirect || '/dashboard'
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
  background:
    radial-gradient(circle at top, rgba(79, 70, 229, 0.15), transparent 55%),
    radial-gradient(circle at bottom, rgba(6, 182, 212, 0.12), transparent 55%),
    var(--color-bg);
  padding: 1.5rem;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--color-surface);
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.25);
  box-shadow: var(--shadow);
  padding: 2rem;
  backdrop-filter: blur(18px);
}

.login-header {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-bottom: 1.75rem;
}

.logo-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #7c3aed, #22d3ee);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 26px;
  height: 26px;
  color: white;
}

.title-wrap h1 {
  font-size: 1.35rem;
  font-weight: 600;
}

.title-wrap p {
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  font-size: 0.875rem;
}

.form-item span {
  color: var(--color-text-muted);
}

.form-item input {
  padding: 0.6rem 0.85rem;
  border-radius: 10px;
  border: 1px solid var(--color-border);
  background: var(--color-surface-elevated);
  color: var(--color-text);
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.form-item input::placeholder {
  color: rgba(148, 163, 184, 0.7);
}

.form-item input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 1px rgba(124, 58, 237, 0.6);
  background: var(--color-surface);
}

.error-text {
  font-size: 0.8125rem;
  color: #f97373;
}

.btn-primary {
  margin-top: 0.5rem;
  width: 100%;
  padding: 0.6rem 1rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #7c3aed, #22d3ee);
  color: white;
  font-weight: 500;
  font-size: 0.95rem;
  border: none;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease, opacity 0.12s ease;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 30px rgba(37, 99, 235, 0.45);
}

.btn-primary:active {
  transform: translateY(0);
  box-shadow: none;
  opacity: 0.9;
}

.student-link {
  display: inline-block;
  color: var(--color-primary-light);
  font-size: 0.9rem;
}

.links {
  margin-top: 1rem;
  display: flex;
  gap: 1rem;
}

.hint {
  margin-top: 1.25rem;
  font-size: 0.8rem;
  color: rgba(148, 163, 184, 0.85);
  text-align: center;
}

@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
  }
}
</style>

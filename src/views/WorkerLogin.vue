<template>
  <div class="login-page">
    <div class="login-card">
      <h1>维修师傅登录</h1>
      <p class="sub">请输入维修账号</p>
      <form class="login-form" @submit.prevent="handleSubmit">
        <label class="form-item">
          <span>账号</span>
          <input v-model="username" type="text" placeholder="请输入账号" />
        </label>
        <label class="form-item">
          <span>密码</span>
          <input v-model="password" type="password" placeholder="请输入密码" />
        </label>
        <p v-if="error" class="error-text">{{ error }}</p>
        <button class="btn-primary" type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <router-link class="back-link" to="/login">返回管理员登录</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { auth } from '@/api'

const route = useRoute()
const router = useRouter()
const username = ref('worker')
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleSubmit() {
  if (!username.value.trim() || !password.value.trim()) {
    error.value = '请填写账号和密码'
    return
  }
  error.value = ''
  loading.value = true
  try {
    const res = await auth.workerLogin(username.value.trim(), password.value)
    sessionStorage.setItem('workerToken', res.token)
    sessionStorage.setItem('workerName', res.username)
    const redirect = route.query.redirect || '/worker/home'
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
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  background: var(--color-surface);
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
  margin-top: 1rem;
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
  margin-top: 0.4rem;
  padding: 0.58rem 1rem;
  border-radius: 999px;
  background: linear-gradient(135deg, #0ea5e9, #4f46e5);
  color: #fff;
}

.error-text {
  color: var(--color-danger);
  font-size: 0.85rem;
}

.back-link {
  margin-top: 1rem;
  display: inline-block;
  color: var(--color-primary-light);
  font-size: 0.9rem;
}
</style>

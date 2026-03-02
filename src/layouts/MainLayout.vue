<template>
  <div class="layout" :class="{ 'mobile-open': mobileMenuOpen }">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
        </div>
        <span>宿舍管理</span>
      </div>
      <nav class="nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="active"
          @click="closeMobileMenu"
        >
          <component :is="item.icon" class="nav-icon" />
          <span>{{ item.title }}</span>
        </router-link>
      </nav>
    </aside>
    <div class="sidebar-overlay" @click="closeMobileMenu"></div>
    <main class="main">
      <header class="header">
        <div class="header-left">
          <button class="menu-toggle" @click="toggleMobileMenu" aria-label="打开菜单">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="3" y1="6" x2="21" y2="6" />
              <line x1="3" y1="12" x2="21" y2="12" />
              <line x1="3" y1="18" x2="21" y2="18" />
            </svg>
          </button>
          <h1 class="page-title">{{ currentTitle }}</h1>
        </div>
        <div class="header-actions">
          <div class="user-badge">
            <span class="user-name">{{ displayName }}</span>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </div>
        </div>
      </header>
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  IconDashboard,
  IconUsers,
  IconBuilding,
  IconDoor,
  IconUserPlus,
  IconChart,
  IconTool,
} from '@/components/icons'

const route = useRoute()
const router = useRouter()
const mobileMenuOpen = ref(false)

const navItems = [
  { path: '/dashboard', title: '概览', icon: IconDashboard },
  { path: '/students', title: '学生管理', icon: IconUsers },
  { path: '/dormitories', title: '楼栋管理', icon: IconBuilding },
  { path: '/rooms', title: '房间管理', icon: IconDoor },
  { path: '/allocation', title: '宿舍分配', icon: IconUserPlus },
  { path: '/statistics', title: '数据统计', icon: IconChart },
  { path: '/repairs', title: '维修管理', icon: IconTool },
]

const currentTitle = computed(() => route.meta.title || '概览')

const displayName = computed(() => sessionStorage.getItem('username') || '管理员')

function handleLogout() {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('username')
  router.push('/login')
}

function toggleMobileMenu() {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

function closeMobileMenu() {
  mobileMenuOpen.value = false
}

watch(() => route.fullPath, closeMobileMenu)
</script>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: var(--color-surface);
  border-right: 1px solid var(--color-border);
  padding: 1.5rem 0;
  position: fixed;
  height: 100vh;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0 1.5rem 2rem;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--color-primary), #a78bfa);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 22px;
  height: 22px;
  color: white;
}

.logo span {
  font-size: 1.25rem;
  font-weight: 600;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  color: var(--color-text-muted);
  transition: var(--transition);
  margin: 0 0.5rem;
  border-radius: var(--radius-sm);
}

.nav-item:hover {
  color: var(--color-text);
  background: var(--color-surface-elevated);
}

.nav-item.active {
  color: var(--color-primary-light);
  background: rgba(124, 58, 237, 0.15);
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  padding: 1.25rem 2rem;
  background: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.menu-toggle {
  display: none;
  width: 36px;
  height: 36px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-surface-elevated);
  align-items: center;
  justify-content: center;
}

.menu-toggle svg {
  width: 18px;
  height: 18px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 600;
}

.user-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.25rem 0.6rem 0.25rem 0.75rem;
  background: var(--color-surface-elevated);
  border-radius: 999px;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.user-name {
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.logout-btn {
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  border: none;
  background: transparent;
  color: var(--color-primary);
  font-size: 0.75rem;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;
}

.logout-btn:hover {
  background: rgba(79, 70, 229, 0.08);
  color: var(--color-primary-light);
}

.content {
  flex: 1;
  padding: 2rem;
  overflow: auto;
}

.sidebar-overlay {
  display: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1024px) {
  .sidebar {
    z-index: 40;
    transform: translateX(-100%);
    transition: transform 0.2s ease;
  }

  .layout.mobile-open .sidebar {
    transform: translateX(0);
  }

  .sidebar-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.35);
    z-index: 30;
  }

  .layout.mobile-open .sidebar-overlay {
    display: block;
  }

  .main {
    margin-left: 0;
  }

  .menu-toggle {
    display: inline-flex;
  }

  .header {
    padding: 1rem;
  }

  .content {
    padding: 1rem;
  }
}

@media (max-width: 640px) {
  .page-title {
    font-size: 1.25rem;
  }

  .user-name {
    max-width: 72px;
  }
}
</style>

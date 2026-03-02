import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/student/login',
    name: 'StudentLogin',
    component: () => import('@/views/StudentLogin.vue'),
    meta: { title: '学生登录', public: true },
  },
  {
    path: '/worker/login',
    name: 'WorkerLogin',
    component: () => import('@/views/WorkerLogin.vue'),
    meta: { title: '师傅登录', public: true },
  },
  {
    path: '/student',
    redirect: '/student/home',
  },
  {
    path: '/student/home',
    name: 'StudentHome',
    component: () => import('@/views/StudentHome.vue'),
    meta: { title: '我的宿舍' },
  },
  {
    path: '/worker',
    redirect: '/worker/home',
  },
  {
    path: '/worker/home',
    name: 'WorkerHome',
    component: () => import('@/views/WorkerHome.vue'),
    meta: { title: '师傅工单' },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘' },
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('@/views/Students.vue'),
        meta: { title: '学生管理' },
      },
      {
        path: 'dormitories',
        name: 'Dormitories',
        component: () => import('@/views/Dormitories.vue'),
        meta: { title: '楼栋管理' },
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('@/views/Rooms.vue'),
        meta: { title: '房间管理' },
      },
      {
        path: 'allocation',
        name: 'Allocation',
        component: () => import('@/views/Allocation.vue'),
        meta: { title: '宿舍分配' },
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '数据统计' },
      },
      {
        path: 'repairs',
        name: 'Repairs',
        component: () => import('@/views/Repairs.vue'),
        meta: { title: '维修管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宿舍管理系统` : '宿舍管理系统'

  const isStudentRoute = to.path === '/student' || to.path.startsWith('/student/')
  const isWorkerRoute = to.path === '/worker' || to.path.startsWith('/worker/')
  const isPublic = !!to.meta.public
  const isAdminLoggedIn = !!sessionStorage.getItem('token')
  const isStudentLoggedIn = !!sessionStorage.getItem('studentToken')
  const isWorkerLoggedIn = !!sessionStorage.getItem('workerToken')

  if (isStudentRoute) {
    if (to.path === '/student/login') {
      if (isStudentLoggedIn) next('/student/home')
      else next()
    } else if (!isStudentLoggedIn) {
      next({ path: '/student/login', query: { redirect: to.fullPath } })
    } else {
      next()
    }
    return
  }

  if (isWorkerRoute) {
    if (to.path === '/worker/login') {
      if (isWorkerLoggedIn) next('/worker/home')
      else next()
    } else if (!isWorkerLoggedIn) {
      next({ path: '/worker/login', query: { redirect: to.fullPath } })
    } else {
      next()
    }
    return
  }

  if (!isPublic && !isAdminLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && isAdminLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router

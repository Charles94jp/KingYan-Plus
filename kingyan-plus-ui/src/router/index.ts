import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  { path: '/', component: () => import('@/views/Home/home.vue') },
  { path: '/login', component: () => import('@/views/login.vue') }
]

const router = createRouter({
  // 4. 内部提供了 history 模式的实现。为了简单起见，我们在这里使用 hash 模式。
  // 子路径
  history: createWebHashHistory(import.meta.env.PROD ? import.meta.env.VITE_APP_BASE_URL : '/'),
  routes
})

export default router

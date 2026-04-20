import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useAuthStore } from '@/stores/auth'

// 定义需要认证的路由
const authRequiredRoutes = ['search', 'recommendations', 'price-trend', 'feedback']

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresGuest: true } // 仅允许未登录用户访问
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { requiresGuest: true } // 仅允许未登录用户访问
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('../views/SearchView.vue'),
      meta: { requiresAuth: true } // 需要认证
    },
    {
      path: '/recommendations',
      name: 'recommendations',
      component: () => import('../views/RecommendationsView.vue'),
      meta: { requiresAuth: true } // 需要认证
    },
    {
      path: '/price-trend',
      name: 'price-trend',
      component: () => import('../views/PriceTrendView.vue'),
      meta: { requiresAuth: true } // 需要认证
    },
    {
      path: '/feedback',
      name: 'feedback',
      component: () => import('../views/FeedbackView.vue'),
      meta: { requiresAuth: true } // 需要认证
    },
    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      redirect: '/'
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  console.log('[路由守卫] 从', from.path, '到', to.path, '名称:', to.name)
  const authStore = useAuthStore()

  // 检查是否需要认证
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest)

  // 检查认证状态
  const isAuthenticated = authStore.isAuthenticated
  console.log('[路由守卫] requiresAuth:', requiresAuth, 'requiresGuest:', requiresGuest, 'isAuthenticated:', isAuthenticated)

  if (requiresAuth && !isAuthenticated) {
    console.log('[路由守卫] 需要认证但未登录，跳转到登录页')
    // 需要认证但未登录，跳转到登录页
    next('/login')
  } else if (requiresGuest && isAuthenticated) {
    console.log('[路由守卫] 要求未登录但已登录，跳转到首页')
    // 要求未登录但已登录，跳转到首页
    next('/')
  } else {
    console.log('[路由守卫] 正常导航')
    // 其他情况正常导航
    next()
  }
})

export default router
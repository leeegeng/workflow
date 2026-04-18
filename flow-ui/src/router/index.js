import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: 'system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/dept',
        name: 'Dept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      // 工作流管理
      {
        path: 'workflow/model',
        name: 'Model',
        component: () => import('@/views/workflow/model/index.vue'),
        meta: { title: '流程模型', icon: 'Connection' }
      },
      {
        path: 'workflow/model/design/:id',
        name: 'ModelDesign',
        component: () => import('@/views/workflow/model/design.vue'),
        meta: { title: '流程设计', hidden: true }
      },
      {
        path: 'workflow/definition',
        name: 'Definition',
        component: () => import('@/views/workflow/definition/index.vue'),
        meta: { title: '流程定义', icon: 'Document' }
      },
      {
        path: 'workflow/instance',
        name: 'Instance',
        component: () => import('@/views/workflow/instance/index.vue'),
        meta: { title: '流程实例', icon: 'List' }
      },
      {
        path: 'workflow/todo',
        name: 'Todo',
        component: () => import('@/views/workflow/todo/index.vue'),
        meta: { title: '待办任务', icon: 'BellFilled' }
      },
      {
        path: 'workflow/done',
        name: 'Done',
        component: () => import('@/views/workflow/done/index.vue'),
        meta: { title: '已办任务', icon: 'CircleCheckFilled' }
      },
      {
        path: 'workflow/form',
        name: 'Form',
        component: () => import('@/views/workflow/form/index.vue'),
        meta: { title: '表单设计', icon: 'Document' }
      },
      {
        path: 'workflow/form/design/:id',
        name: 'FormDesign',
        component: () => import('@/views/workflow/form/design.vue'),
        meta: { title: '表单设计器', hidden: true }
      },
      {
        path: 'workflow/monitor',
        name: 'Monitor',
        component: () => import('@/views/workflow/monitor/index.vue'),
        meta: { title: '流程监控', icon: 'Monitor' }
      },
      {
        path: 'workflow/report',
        name: 'Report',
        component: () => import('@/views/workflow/report/index.vue'),
        meta: { title: '报表统计', icon: 'TrendCharts' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', hidden: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 清除可能残留的遮罩层
  const overlays = document.querySelectorAll('.el-overlay')
  overlays.forEach(overlay => overlay.remove())
  document.body.classList.remove('el-popup-parent--hidden')
  document.body.style.overflow = ''
  document.body.style.paddingRight = ''

  if (to.meta.public) {
    next()
  } else if (!userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router

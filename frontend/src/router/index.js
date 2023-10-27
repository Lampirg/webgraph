import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/find'
    },

    {
      path: '/find',
      name: 'find',
      component: () => import('../views/SameResidentsView.vue')
    }
  ]
})

export default router

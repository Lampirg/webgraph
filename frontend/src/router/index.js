import { createRouter, createWebHistory } from 'vue-router';
import SameResidentsView from '../views/SameResidentsView.vue';

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
      component: SameResidentsView,
      props: route => ({ ...route.query, ...route.params })
    },
    {
      path: '/all',
      name: 'all',
      component: () => import('../views/AllResidentsView.vue')
    }
  ]
})

export default router

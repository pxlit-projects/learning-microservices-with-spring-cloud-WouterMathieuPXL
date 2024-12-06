import { createRouter, createWebHistory } from 'vue-router'
import UserView from '../views/UserView.vue'
import CatalogView from "@/views/CatalogView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'catalog',
      component: CatalogView,
    },
    {
      path: '/user',
      name: 'user',
      component: UserView
    },
  ],
})

export default router

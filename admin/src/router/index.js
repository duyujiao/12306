import { createRouter, createWebHistory } from 'vue-router'



const routes = [{
  path: '/',
  component: () => import('../views/main.vue'),
  children: [{
    path: 'welcome',
    component: () => import('../views/main/welcome.vue'),
  }, {
    path: 'about',
    component: () => import('../views/main/about.vue'),
  }, {
    path: 'station',
    component: () => import('../views/main/business/station.vue'),
  }, {
    path: 'train',
    component: () => import('../views/main/business/train.vue'),
  }, {
    path: 'train-station',
    component: () => import('../views/main/business/train-station.vue'),
  }, {
    path: 'train-carriage',
    component: () => import('../views/main/business/train-carriage.vue'),
  }, {
    path: 'train-seat',
    component: () => import('../views/main/business/train-seat.vue'),
  }]
}, {
  path: '',
  redirect: '/welcome'
}];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})


export default router

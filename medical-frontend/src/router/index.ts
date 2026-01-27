import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: 'patient',
        name: 'PatientDashboard',
        component: () => import('@/views/dashboard/PatientDashboardView.vue'),
        meta: { title: '患者首页', roles: ['PATIENT'] }
      },
      {
        path: 'doctor',
        name: 'DoctorDashboard',
        component: () => import('@/views/dashboard/DoctorDashboardView.vue'),
        meta: { title: '医生首页', roles: ['DOCTOR', 'ADMIN'] }
      },
      {
        path: 'health-data',
        name: 'HealthData',
        component: () => import('@/views/health/HealthDataView.vue'),
        meta: { title: '健康数据', roles: ['PATIENT'] }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/appointment/AppointmentView.vue'),
        meta: { title: '预约挂号', roles: ['PATIENT', 'DOCTOR', 'ADMIN'] }
      },
      {
        path: 'schedule-manage',
        name: 'ScheduleManage',
        component: () => import('@/views/appointment/ScheduleManageView.vue'),
        meta: { title: '排班管理', roles: ['ADMIN'] }
      },
      {
        path: 'ai-consultation',
        name: 'AIConsultation',
        component: () => import('@/views/ai/AIConsultationView.vue'),
        meta: { title: 'AI 智能问诊', roles: ['PATIENT'] }
      },
      {
        path: 'ai-analysis',
        name: 'HealthAnalysis',
        component: () => import('@/views/ai/HealthAnalysisView.vue'),
        meta: { title: '全方位健康评估', roles: ['PATIENT', 'DOCTOR'] }
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/record/RecordListView.vue'),
        meta: { title: '健康档案', roles: ['PATIENT', 'DOCTOR', 'ADMIN'] }
      },
      {
        path: 'records/:id',
        name: 'RecordDetail',
        component: () => import('@/views/record/RecordDetailView.vue'),
        meta: { title: '档案详情', roles: ['PATIENT', 'DOCTOR', 'ADMIN'] }
      },
      {
        path: 'records/full/:patientId',
        name: 'FullRecordDetail',
        component: () => import('@/views/record/FullRecordDetail.vue'),
        meta: { title: '综合档案详情', roles: ['PATIENT', 'DOCTOR', 'ADMIN'] }
      },
      {
        path: 'patients',
        name: 'DoctorPatients',
        component: () => import('@/views/doctor/DoctorPatientsView.vue'),
        meta: { title: '我的患者', roles: ['DOCTOR', 'ADMIN'] }
      },
      {
        path: 'statistics',
        name: 'DataAnalysis',
        component: () => import('@/views/statistics/DataAnalysisView.vue'),
        meta: { title: '数据分析', roles: ['DOCTOR', 'ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/ProfileView.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/errors/NotFoundView.vue'),
    meta: { public: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, _from, next) => {
  const store = useAuthStore();
  if (!to.meta.public && !store.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
    return;
  }

  // Role-based access control
  const roles = (to.meta as any)?.roles as string[] | undefined;
  const userRole = store.userInfo?.role;

  if (roles && userRole && !roles.includes(userRole)) {
    // If user has role but not allowed, redirect to their default home
    if (userRole === 'PATIENT') {
      next({ name: 'PatientDashboard' });
    } else {
      next({ name: 'DoctorDashboard' });
    }
    return;
  }

  // Redirect root to role-specific home
  if (to.path === '/') {
    if (userRole === 'PATIENT') {
      next({ name: 'PatientDashboard' });
      return;
    } else if (userRole === 'DOCTOR' || userRole === 'ADMIN') {
      next({ name: 'DoctorDashboard' });
      return;
    }
  }

  next();
});

export default router;

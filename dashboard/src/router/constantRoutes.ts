import type { RouteRecordRaw } from "vue-router";
import HomeView from "../views/Main.vue";

//系统默认的路由
const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "root",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/login/login.vue"),
  },
  {
    path: "/",
    name: "main",
    component: HomeView, // 使用父布局组件
    children: [
      {
        path: "engines/list",
        name: "enginesList",
        component: () => import("@/views/engines/index.vue"),
        meta: { title: "引擎列表" },
      },
      {
        path: "engines/edit",
        name: "enginesEdit",
        component: () => import("@/views/engines/edit.vue"),
        meta: { title: "引擎编辑" },
      },
    ]
  }
];
export default routes;

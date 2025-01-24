export declare interface Menu {
  id: string;
  icon?: string;
  path?: string;
  name?: string;
  componentPath?: string;
  title: string;
  children?: Array<Menu>;
}

export const menuList: Array<Menu> = [

  {
    id: "Home_M0001",
    icon: "HomeFilled",
    path: "/home",
    name: "Home",
    componentPath: "home/index.vue",
    title: "首页",
  },
  {
    id: "flow_engine_manage",
    icon: "Grid",
    title: "引擎管理",
    children: [
      {
        id: "enginesList",
        path: "/engines/list",
        name: "enginesList",
        componentPath: "@/views/engines/index.vue",
        title: "引擎列表",
      }
    ]
  }

];

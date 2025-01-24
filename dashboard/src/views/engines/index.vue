<template>
  <div class="container">
    <el-row :gutter="24">
      <el-col :span="5">
        <el-input
          v-model="searchName"
          placeholder="请输入名称进行搜索"
          suffix-icon="el-icon-search"
          clearable
        />
      </el-col>

      <el-col :span="1.2">
        <el-button type="primary" @click="searchByName">搜索</el-button>
      </el-col>
      <el-col :span="1.2">
        <el-button type="primary" @click="addRowEvent">新增</el-button>
      </el-col>
    </el-row>

    <el-divider content-position="left">引擎列表</el-divider>

    <div>
      <vxe-table
        ref="xTable"
        border
        show-overflow
        :column-config="{ resizable: true }"
        :loading="engineList.loading"
        :data="engineList.tableData"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-column field="id" title="id" width="180"></vxe-column>
        <vxe-column field="name" title="名称" width="200"></vxe-column>
        <vxe-column field="status" title="状态" width="120"></vxe-column>
        <vxe-column field="executionMode" title="执行方式" width="120"></vxe-column>
        <vxe-column field="version" title="版本号" width="120"></vxe-column>
        <vxe-column field="description" title="描述"></vxe-column>
        <vxe-column field="content" title="内容"></vxe-column>
        <vxe-column title="操作" width="160">
          <template #default="{ row }">
            <el-button @click="editRowEvent(row)" style="margin-right: 10px;">编辑</el-button>
            <span v-if="row.status === 'published'">
              <el-button type="danger" @click="changeEngineStatus(row.id, 'unavailable')" style="margin-bottom: 3px;">下架</el-button>
            </span>
            <span v-else-if="row.status === 'unavailable'">
              <el-button type="success" @click="changeEngineStatus(row.id, 'published')" style="margin-bottom: 3px;">上架</el-button>
            </span>
          </template>
        </vxe-column>
      </vxe-table>

      <!-- 分页控件 -->
      <el-pagination
        v-if="engineList.tableData.length"
        :page-size="pageSize"
        :current-page="currentPage"
        :total="engineList.total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        @size-change="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from "vue";
import { engines, engineChangeStatus } from '@/api/module/api';
import { VXETable } from "vxe-table";
import { useRouter } from "vue-router";

type TableData = {
  id: number;
  name?: string;
  description?: string;
  executionMode?: string;
  version?: number;
  content?: string;
  status?: string;
  updateTime?: string;
};

const router = useRouter();
const xTable = ref();
const searchName = ref(""); // 用于存储搜索框的名称

const engineList = reactive<{
  loading: boolean;
  tableData: TableData[];
  total: number; // 用于存储总条目数
}>( {
  loading: false,
  tableData: [],
  total: 0, // 初始为 0
});

// 配置分页的默认条数
const DEFAULT_PAGE_SIZE = 10;

const pageSize = ref(DEFAULT_PAGE_SIZE); // 每页显示的条数
const currentPage = ref(1); // 当前页码

// 加载表格数据的通用函数
const loadTableData = async (engineName = "", pageNo = 1, pageSize = DEFAULT_PAGE_SIZE) => {
  try {
    engineList.loading = true;
    const response = await engines(engineName, pageNo, pageSize);
    const result = response.data;
    engineList.tableData = [];
    engineList.total = 0;
    if (result.code === '0000' && result.data && Array.isArray(result.data.records)) {
      // 从接口响应中获取数据
      engineList.tableData = result.data.records.map((item: any) => ( {
        id: item.id,
        name: item.name,
        description: item.description,
        content: item.content,
        version: item.version,
        status: item.status,
        executionMode: item.executionMode,
        updateTime: item.updateTime
      }));
      engineList.total = result.data.total || 0; // 设置总记录数
    }
  } catch (error) {
    console.error("加载表格数据失败", error);
    engineList.tableData = [];
    engineList.total = 0;
  } finally {
    engineList.loading = false;
  }
};

// 处理页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadTableData(searchName.value, page, pageSize.value); // 带上搜索条件
};

// 处理每页显示条数变化
const handlePageSizeChange = (size: number) => {
  pageSize.value = size;
  loadTableData(searchName.value, currentPage.value, size); // 带上搜索条件
};

// 搜索按钮点击事件
const searchByName = () => {
  currentPage.value = 1; // 搜索时重置为第一页
  loadTableData(searchName.value, currentPage.value, pageSize.value); // 根据名称搜索
};

// 修改列表页面的路由跳转方式
const editRowEvent = (row: TableData) => {
  router.push({ name: "enginesEdit", query: { id: row.id } });
};

// 新增按钮点击事件
const addRowEvent = () => {
  router.push({ name: "enginesEdit" });
};

// 修改引擎状态（上架 / 下架）
const changeEngineStatus = async (id: number, status: string) => {
  try {
    const response = await engineChangeStatus(id, status);
    if (response.data.code === '0000') {
      // 状态更新成功后重新加载数据
      loadTableData(searchName.value, currentPage.value, pageSize.value);
    }
  } catch (error) {
    console.error("状态更新失败", error);
  }
};

onMounted(() => {
  loadTableData();
});
</script>

<style lang="less" scoped>
.page {
  padding: 10px 0;
  display: flex;
  justify-content: right;
}
</style>

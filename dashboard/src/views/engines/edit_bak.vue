<template>
  <div class="container">
    <div class="edit-container">
      <el-form :model="form" label-width="80px" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="5">
            <el-form-item label="名称：">
              <el-input class="short-input" v-model="form.name" placeholder="请输入名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="4" v-if="isEdit">
            <el-form-item label="执行方式:">
              <el-select v-model="form.executionMode" placeholder="请选择">
                <el-option label="同步" value="sync"></el-option>
                <el-option label="异步" value="async"></el-option>
                <el-option label="批量" value="batch"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        
          <el-col :span="14">
            <el-form-item label="描述：">
              <el-input class="long-input" v-model="form.description" placeholder="请输入描述"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div class="flow-container-wrapper">
      <div class="node-list-panel">
        <div class="node-header">通用节点</div>
        <div class="node-container"
            v-for="(node, index) in dynamicNodeList" 
            :key="index" 
            draggable="true"
            @dragstart="handleDragStart($event, node, '1')"
        >
          <div class="node-item" >
            {{ node.label }}
          </div>
        </div>
       
        <div class="node-header">算子库列表</div>
        <el-input v-model="searchName" placeholder="请输入算子名称搜索" class="input-with-select">
          <template #append>
            <el-button :icon="Search" @click="searchOperatorsByName"/>
          </template>
        </el-input>

        <div class="node-container"
          v-for="(node, index) in operatorsList" 
          :key="index" 
          draggable="true" 
          @dragstart="handleDragStart($event, node, '2')"
          >
          <div class="node-item">
            {{node.label}} ({{node.type}})
          </div>
          <div class="node-extra">
            <select >
              <option >
                {{ node.version }}
              </option>
            </select>
          </div>
        </div>
       
        <!-- 分页控件 -->
        <el-pagination
          v-if="totalOperators > 0"
          background
          layout="prev, next"
          :total="totalOperators"
          :page-size="pageSize"
          :current-page="pageNo"
          @current-change="handlePageChange"
        />
      </div>

      <VueFlow
        v-model="elements"
        fit-view-on-init
        class="flow-container"
        :node-types="nodeTypes"
        @node-click="handleNodeClick"
        @drop="handleNodeDrop"
        @dragover="handleDragOver"
      >
        <ToolsControls 
          @save-engine="handleSaveEngine"
          @log-to-object="handleLogToObject"
          @go-back="handleGoBack"
        />
        <Background />
        <Controls />
      </VueFlow>

      <!-- 节点信息面板 -->
      <el-aside width="300px" class="info-panel" v-if="selectedNode">
        <div class="panel-header">
          节点信息
        </div>
        <el-form :model="selectedNode" label-width="80px">
          <el-form-item label="节点id：">
            <span>{{ selectedNode.id }}</span>
          </el-form-item>
          <el-form-item label="节点名称:" v-if="selectedNode.isScript === true">
            <el-input v-model="selectedNode.label"  placeholder="请输入名称"/>
          </el-form-item>
          <el-form-item label="节点名称:" v-else>
            <span>{{ selectedNode.label }}</span>
          </el-form-item>

          <el-form-item label="节点类型:">
            <span>{{ selectedNode.type }}</span>
          </el-form-item>

          <div v-if="selectedNode.type === 'condition'">
            <el-form-item v-if="selectedNode.isScript === true"  label="脚本">
              <el-input  v-model="selectedNode.script" :rows="6" type="textarea" placeholder="请输入脚本"></el-input>
            </el-form-item>
          </div>
          
          <div v-else-if="selectedNode.type ==='start' || selectedNode.type === 'end'"></div>

          <template v-else>
            <el-form-item label="超时时间:">
              <el-input-number v-model="selectedNode.config.timeout" :step="1"/>
            </el-form-item>

            <el-form-item label="忽略异常:">
              <el-radio-group v-model="selectedNode.config.ignoreException">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="是否异步:">
              <el-radio-group v-model="selectedNode.config.async">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>

          </template> 
        </el-form>
        
        <!-- 底部按钮区域 -->
        <el-row class="node-btn-wrapper" style="display: flex; justify-content: flex-end; align-items: flex-end; height: 100%;">
          <el-button type="primary" @click="closePanel">关闭</el-button>
          <el-button type="primary" @click="updateNodeData">确认</el-button>
        </el-row>
      </el-aside>

    </div>
  </div>
</template>



<script lang="ts" setup>
import "@vue-flow/core/dist/style.css";
import "@vue-flow/core/dist/theme-default.css";
import "@/assets/bpmn.css";
import "./edit.css";
import { Search } from '@element-plus/icons-vue'
import { ref, markRaw, onMounted, nextTick } from "vue";
import { Background, Controls } from "@vue-flow/additional-components";
import { VueFlow, useVueFlow, MarkerType } from "@vue-flow/core";
import {engineDetail, operators, engineEdit } from '@/api/module/api';
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import ToolsControls from './tools.vue'
import Condition from "./node/condition.vue";
import Standard from "./node/standard.vue";
import Start from "./node/start.vue";
import End from "./node/end.vue";
const route = useRoute();
const router = useRouter();
const { onConnect, addEdges, project, toObject} = useVueFlow();

const loading = ref(false);

const isEdit = ref(false);
// 算子列表和分页
const operatorsList = ref([]);
const searchName = ref('');
const pageNo = ref(1);
const pageSize = ref(15);
const totalOperators = ref(0);

// 画布数据
const data = [];
const elements = ref(data);

// 选中的节点信息
const selectedNode = ref(null);

// 初始化表单数据
const form = ref({
  id: null,
  name: "",
  description: "",
  executionMode: "sync",
  script: "",
  isScript: false,
  content: ""
});

// 定义节点类型
const nodeTypes = ref({
  condition: markRaw(Condition),
  standard: markRaw(Standard),
  start: markRaw(Start),
  end: markRaw(End)
});

// 动态节点
const dynamicNodeList = ref([
  { label: "条件节点", type: "condition"},
  { label: "开始节点", type: "start"},
  { label: "结束节点", type: "end"},
]);

// 查询属性
const handleLogToObject = () => {
  ElMessage.info(JSON.stringify(toObject()));
};

onMounted(() => {
  // 初始化获取算子列表
  fetchOperators(searchName.value);

  const engineId = route.query.id as string;
  // 编辑
  if (engineId) {
    isEdit.value = true;
    loadEngineData(engineId);
  }
});

// 获取编辑页面时的初始数据
const loadEngineData = async (id: string) => {
  try {
    loading.value = true;
    const response = await engineDetail(id);
    const result = response.data;
    if (result.code === "0000" && result.data) {
      form.value = result.data;
      const content = result.data.content ? JSON.parse(result.data.content) : { nodes: [], edges: [] };
      const { nodes = [], edges = [] } = content;
      elements.value = [...nodes, ...edges];
      if (nodes.length > 0) {
        const maxNodeId = Math.max(...nodes.map((node: { id: string }) => parseInt(node.id, 10)));
        nodeIdCounter = maxNodeId + 1; 
      } else {
        nodeIdCounter = 1;
      }
    }
  } catch (error) {
    console.error("加载引擎数据失败", error);
  } finally {
    loading.value = false;
  }
};

// 保存引擎
const handleSaveEngine = async () => {
  try {
    loading.value = true;
    const data = JSON.stringify(toObject());
    // 验证流程是否合法
    if (!validateGraph(data)) {
      return;
    }
    form.value.content = data;
    let response = await engineEdit(form.value);
    const result = response.data;
    if (result.code === "0000") {
      router.push({ path: "/engines/list", query: { refresh: "true" } });
    } else {
      console.error("保存失败", result.message);
    }
  } catch (error) {
    console.error("保存失败", error);
  } finally {
    loading.value = false;
  }
};

// 返回到列表页面
const handleGoBack = () => {
  router.push("/engines/list");
};

/**
 * 验证流程是否合法
 * @param data 
 */
const validateGraph = (graphData: string) => {
  let data;
  try {
    data = JSON.parse(graphData);
  } catch (error) {
    ElMessage.error("无效的图数据格式");
    return false;
  }

  if (!data || !Array.isArray(data.nodes) || !Array.isArray(data.edges)) {
    ElMessage.error("图数据缺少必要的 nodes 或 edges 属性");
    return false;
  }
  const nodes = data.nodes;
  const edges = data.edges;

  // 创建一个节点ID集合
  const nodeIds = new Set(nodes.map(node => node.id));
  // 用来记录每个节点的出边和入边
  const outgoingEdges = new Map();
  const incomingEdges = new Map(); 

  // 初始化出边和入边
  nodes.forEach(node => {
    outgoingEdges.set(node.id, []);
    incomingEdges.set(node.id, []);
  });

  edges.forEach(edge => {
    outgoingEdges.get(edge.source).push(edge.target);
    incomingEdges.get(edge.target).push(edge.source); 
  });

  // 步骤 1：检查孤立节点（没有出边和入边的节点）
  const isolatedNodes = [];
  nodeIds.forEach(nodeId => {
    if (outgoingEdges.get(nodeId).length === 0 && incomingEdges.get(nodeId).length === 0) {
      isolatedNodes.push(nodeId);
    }
  });

  if (isolatedNodes.length > 0) {
    ElMessage.error(`不能有孤立节点，孤立节点 ID: ${isolatedNodes.join(", ")}`);
    return false;
  }

  // 步骤 2：检查是否有环（深度优先搜索 DFS）
  const visited = new Set();
  // 用来记录递归栈中的节点
  const recursionStack = new Set(); 

  function hasCycle(nodeId) {
    // 如果当前节点已经在递归栈中，说明找到了环
    if (recursionStack.has(nodeId)) {
      return true;
    }
    // 如果当前节点已经访问过，说明没有环
    if (visited.has(nodeId)) {
      return false;
    }

    // 标记当前节点为已访问，并将其加入递归栈
    visited.add(nodeId);
    recursionStack.add(nodeId);

    // 检查当前节点的所有出边
    const targets = outgoingEdges.get(nodeId);
    for (let target of targets) {
      if (hasCycle(target)) {
        return true;
      }
    }

    // 当前节点的递归结束后，移出递归栈
    recursionStack.delete(nodeId);
    return false;
  }

  // 遍历所有节点，检查是否有环
  for (let nodeId of nodeIds) {
    if (hasCycle(nodeId)) {
      ElMessage.error(`存在环形依赖，问题节点: ${nodeId}`);
      return false;
    }
  }

  // 步骤 3：检查终端节点的数量（出度为0的节点）
  const terminalNodes = [];
  outgoingEdges.forEach((targets, nodeId) => {
    if (targets.length === 0) {
      terminalNodes.push(nodeId);
    }
  });

  if (terminalNodes.length !== 1) {
    ElMessage.error("终端节点数量不能超过1个");
    return false;
  }
  return true;
}


/****************************节点操作相关函数********************/
// 关闭面板
const closePanel = () => {
  selectedNode.value = null;
};

// 修改节点信息
const updateNodeData = () => {
  const updatedNode = selectedNode.value;
  const nodeIndex = elements.value.findIndex(node => node.id === updatedNode.id);
  if (nodeIndex !== -1) {
    elements.value[nodeIndex] = { ...updatedNode }; 
    ElMessage.success("节点信息已更新");
  } else {
    ElMessage.error("节点未找到");
  }
  selectedNode.value = null;
};

// 监听节点点击事件
const handleNodeClick = (event: any) => {
  const targetNode = event.node;
  const nodeIndex = elements.value.findIndex(node => node.id === targetNode.id);
  if (nodeIndex !== -1) {
    selectedNode.value = elements.value[nodeIndex];
  }
};



/****************************算子列表相关函数****************/
const searchOperatorsByName = () => {
  pageNo.value = 1;
  fetchOperators(searchName.value);
};

// 获取算子数据
const fetchOperators = async (searchName = "") => {
  try {
    const response = await operators(searchName, pageNo.value, pageSize.value);
    operatorsList.value = response.data.data.records; // 更新算子列表
    totalOperators.value = response.data.data.total; // 更新总数
  } catch (error) {
    ElMessage.error('获取算子列表失败');
  }
};

// 处理分页变化
const handlePageChange = (newPage: number) => {
  pageNo.value = newPage;
  fetchOperators(searchName.value); // 刷新数据
};


/********************** 画布事件处理 *********************/

// 定义一个自增ID变量
let nodeIdCounter = 1;

// 处理拖拽到画布区域的事件
const handleDragOver = (event: DragEvent) => {
  event.preventDefault(); // 防止默认行为，允许放置
};

// 拖拽开始事件，设置拖拽节点类型
const handleDragStart = (event: DragEvent, node: any, opsType: string) => {
  if (opsType === '1') {
    if (node.type === 'condition') {
      node.script = "";
      node.isScript = true;
    }else if (node.type === 'start') {
      node.isScript = false;
      node.config = {
        timeout: 0,
        ignoreException: false,
        async: false
      };
    }else if (node.type === 'end') {
      node.isScript = false;
      node.config = {
        timeout: 0,
        ignoreException: false,
        async: false
      };
    }else{
      ElMessage.info(`不支持的操作`);
    }
  }
  event.dataTransfer.setData("nodeData", JSON.stringify(node)); 
};

// 拖拽放置事件，处理节点加入画布
const handleNodeDrop = (event: DragEvent) => {
  const nodeDataStr = event.dataTransfer.getData("nodeData"); 
  const nodeData = JSON.parse(nodeDataStr);
  
  // 获取鼠标在画布上的位置
  const { x: positionX, y: positionY } = getMousePositionOnCanvas(event, ".flow-container");
  const addNode = {
    id: generateNodeId(nodeData),
    type: nodeData.type,
    position: { x: positionX, y: positionY },
    label: nodeData.label,
    isScript: nodeData.isScript,
    script: nodeData.script,
    config: nodeData.config
  };
  elements.value.push(addNode);
  ElMessage.info(`新增节点: ${addNode.label}`);
};

// 生成节点ID
const generateNodeId = (nodeData) => {
  let nodeId;
  switch (nodeData.type) {
    case 'start':
      nodeId = '0';
      break;
    case 'end':
      nodeId = '99999999';
      break;
    default:
      nodeId = `${nodeIdCounter++}`;
      break;
  }
  return nodeId;
}

// 获取鼠标在画布上的位置
const getMousePositionOnCanvas = (event: DragEvent, containerSelector: string) => {
  // 获取画布容器
  const flowContainer = document.querySelector(containerSelector);
  if (!flowContainer) {
    return { x: event.clientX - 100, y: event.clientY - 100 };
  }
  // 获取画布的边界
  const canvasRect = flowContainer.getBoundingClientRect();
  const mouseX = event.clientX - canvasRect.left;
  const mouseY = event.clientY - canvasRect.top;
  return project({ x: mouseX, y: mouseY });
};

// 连接事件
onConnect((params) => {
  const { source, target, sourceHandle, targetHandle } = params;

  // 检查是否是自连接
  if (source === target) {
    ElMessage.warning('不能连接到自身');
    return;
  }
  // 防止 top-to-top 连接
  if (sourceHandle && targetHandle && sourceHandle.includes("top") && targetHandle.includes("top")) {
    ElMessage.warning('不能连接相同位置类型的节点（top-to-top）');
    return;
  }
  // 防止 bottom-to-bottom 连接
  if (sourceHandle && targetHandle && sourceHandle.includes("bottom") && targetHandle.includes("bottom")) {
    ElMessage.warning('不能连接相同位置类型的节点（bottom-to-bottom）');
    return;
  }
  console.log(params);

  // 只有在连接符合规则时，才添加边
  addEdges([
    {
      ...params,
      markerEnd: MarkerType.ArrowClosed,

    }
  ]);
});


</script>


<template>
  <div ref="homePage" class="page">
    <!-- 待办、已办、办结、消息、预警 -->
    <el-row :gutter="20" class="mgb20">
      <el-col v-for="(item, index) in messageList" :key="index" :span="4">
        <el-card shadow="hover" :body-style="{ padding: '0px' }">
          <div class="grid-message">
            <el-icon class="grid-msg-icon" :style="{ background: item.color }">
              <component :is="item.icon" />
            </el-icon>
            <div class="grid-msg-right">
              <div class="grid-msg-num" :style="{ color: item.color }">
                {{ item.count }}
              </div>
              <div>{{ item.name }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script lang="ts" setup>
import { nextTick, onMounted, ref } from "vue";
import { onBeforeUnmount } from "vue";
import { watch } from "vue";
import { useElementSize } from "@vueuse/core";

const messageList = ref([
  {
    type: "mytask",
    name: "我的申请",
    count: 10,
    color: "#409EFF",
    icon: "Promotion",
    linkUrl: "#",
  },
  {
    type: "todo",
    name: "代办",
    count: 2,
    color: "#409EFF",
    icon: "Flag",
    linkUrl: "#",
  },
  {
    type: "done",
    name: "已办",
    count: 10,
    color: "#67C23A",
    icon: "SuccessFilled",
    linkUrl: "#",
  },
  {
    type: "conclude",
    name: "办结",
    count: 3,
    color: "#67C23A",
    icon: "Checked",
    linkUrl: "#",
  },
  {
    type: "message",
    name: "我的消息",
    count: 10,
    color: "#E6A23C",
    icon: "BellFilled",
    linkUrl: "#",
  },
  {
    type: "warninng",
    name: "预警信息",
    count: 10,
    color: "#F56C6C",
    icon: "WarningFilled",
    linkUrl: "#",
  },
]);

</script>

<style lang="less" scoped>
@message-width: 80px;

.grid-message {
  display: flex;
  align-items: center;
  height: @message-width;
  cursor: pointer;
}

.grid-msg-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-msg-num {
  font-size: 30px;
  font-weight: bold;
}

.grid-msg-icon {
  font-size: 50px;
  width: @message-width;
  height: @message-width;
  text-align: center;
  line-height: @message-width;
  color: #fff;
}


</style>

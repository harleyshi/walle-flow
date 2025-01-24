<template>
  <div class="diamond-node">
    <div class="diamond-label">{{ label }}</div>

    <Handle
      type="target"
      position="top"
      :id="`top-${id}`"
      :style="handleStyleTop"
    />
    <Handle
      type="source"
      position="bottom"
      :id="`bottom-${id}`"
      :style="handleStyleBottom"
    />
  </div>
</template>

<script lang="ts" setup>
  import { Handle } from "@vue-flow/core";
  
  defineProps({
    label: String,
    id: String,
  });
  
  const handleStyleTop = {
    width: "5px",
    height: "5px",
    borderRadius: "50%",
    
    backgroundColor: "black",
    transform: "translate(-265%, -52%) rotate(43deg)",  // 调整到尖端位置
  };

  const handleStyleBottom = {
    width: "5px",
    height: "5px",
    borderRadius: "50%",
    backgroundColor: "black",
    transform: "translate(165%, 50%) rotate(43deg)",  // 调整到尖端位置
};
  
</script>

<style scoped>
.diamond-node {
  width: 30px;
  height: 30px;
  border: 1px solid #ffca28;
  background-color: rgb(255 255 255 / 70%);
  color: #333;
  text-align: center;
  line-height: 30px;
  transform: rotate(45deg);
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(99, 94, 94, 0.1);
 
}

.diamond-label {
  font-size: 8px;
  transform: rotate(-45deg);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
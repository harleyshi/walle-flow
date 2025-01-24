<template>
    <div class="business-process-node">
      <div class="node-label">{{ label }}</div>
      
      <Handle
        type="target"
        position="top"
        :id="`top-${id}`"
        :style="handleStyle"
      />
      <Handle
        type="source"
        position="bottom"
        :id="`bottom-${id}`"
        :style="handleStyle"
      />
    </div>
</template>
  
<script lang="ts" setup>
  import { Handle } from "@vue-flow/core";
  
  defineProps({
    label: String,
    id: String,
  });
  
  const handleStyle = {
    width: "5px",
    height: "5px",
    borderRadius: "50%",
    backgroundColor: "black",
  };
  
</script>
  
<style scoped>
.business-process-node {
  font-size: 10px;
  width: 100px;
  height: 30px;
  border: 1px solid #ffca28;
  background-color: rgb(255 255 255 / 70%);
  color: #333;
  text-align: center;
  line-height: 30px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

}
  
.node-label {
  font-size: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding-left: 2px;
  padding-right: 2px;
}
  
</style>
  
<template>
  <Panel position="top-right">
    <div class="buttons">
      <button title="保存" @click="triggerSaveEngine">
        <Icon name="save" />
      </button>
      <button title="导出" @click="triggerLogToObject">
        <Icon name="download" />
      </button>
      <button title="返回" @click="triggerGoBack">
        <Icon name="return" />
      </button>
    </div>
  </Panel>
</template>

<script setup>
import { Panel } from '@vue-flow/core'
import Icon from '@/components/Icon.vue'
const emit = defineEmits();

const triggerSaveEngine = () => {
  emit('save-engine');
}

const triggerLogToObject = () => {
  emit('log-to-object');
}

const triggerGoBack = () => {
  emit('go-back');
}
</script>

<style scoped>
.vue-flow__panel {
    background-color:#2d3748;
    padding:10px;
    border-radius:8px;
    box-shadow:0 0 10px #00000080;
}

.vue-flow__panel .buttons {
    display:flex;
    gap:8px
}

.vue-flow__panel button {
    border:none;
    cursor:pointer;
    background-color:#4a5568;
    border-radius:8px;
    color:#fff;
    box-shadow:0 0 10px #0000004d;
    width:40px;
    height:40px;
    font-size:16px;
    display:flex;
    align-items:center;
    justify-content:center
}

.vue-flow__panel button:hover {
    background-color:#2563eb;
    transition:background-color .2s
}
</style>
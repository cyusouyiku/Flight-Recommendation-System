<template>
  <div id="app">
    <router-view />
    <AiAssistantDialog />
  </div>
</template>

<script setup lang="ts">
import AiAssistantDialog from '@/components/AiAssistantDialog.vue'
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  overflow-y: auto;
}
</style>
<script setup lang="ts">
// This starter template is using Vue 3 <script setup> SFCs
// Check out https://vuejs.org/api/sfc-script-setup.html#script-setup
import router from '@/router'

function toHello () {
  router.push('/hello')
}
</script>

<template>
  <img alt="Vue logo" src="./assets/logo.png"/>
<!--  <HelloWorld msg="Hello Vue 3 + TypeScript + Vite"/>-->
  <router-view/>
  <br><br><br>
  <el-button type="primary" @click="toHello" round>el-button: show hello</el-button>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>

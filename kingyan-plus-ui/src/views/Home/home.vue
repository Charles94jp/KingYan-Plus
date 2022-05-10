<template>
  <div style="text-align: center;">
    <br> <br>
    <h1>User: {{ user ? user.name : '' }}</h1>
    <h3>whit roles: {{ user ? user.roles : [] }}</h3>
    <h3>and permissions: {{ user ? user.permissions : [] }}</h3>
    <el-button type="warning" :icon="SwitchButton" @click="logout" round>Logout</el-button>
    <br><br>
    <div>
      <el-link type="info" @click="fetchAllRoles()">all roles</el-link>
      <br> <br>
      <el-link type="primary" @click="fetchAdmin()">admin</el-link>
      <br> <br>
      <el-link type="success" @click="fetchTest()">test</el-link>
    </div>
    <br> <br> <br>
    <el-input
      v-model="textarea"
      disabled
      :rows="3"
      :clos="30"
      type="textarea"
      placeholder="请求结果"
      :input-style="{width:'500px'}"
    />
    <br>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { SwitchButton } from '@element-plus/icons-vue'
import service from '@/axios'
import router from '@/router'
import elMessage from '@/util/el-message'

const textarea = ref('')
const text = reactive({ value: '' })
text.value = '请求结果'

interface Permission{
  id: number
  name: string
}

interface Role{
  id: number
  name: string
  permissions: Array<Permission>
}

interface User {
  id: number
  name: string
  roles: Array<Role>
  permissions: Array<Permission>
  nickname: string
  sex: number
  phone: string
  email: string
  createdDate: string
}

// todo: get user from login.vue
const user = ref<User>()

service.get('/user/getUserInfo').then(r => {
  if (r) {
    user.value = r.data
  }
})

// service.get('/auth/getUserInfo').then(r => {
//   user.value = r.data as any
//   userName.value = user.value?.name as string
//   userRole.value = user.value?.role.name as string
// }).catch(e => console.log(e))

function logout () {
  service.get('/auth/logout').then(r => {
    if (r.data.success) {
      elMessage.elMessage('退出成功', 'success')
    }
    router.push('/login')
  })
}

function fetchAllRoles () {
  service.get('/all/helloAllRoles').then(r => {
    textarea.value = r.data.msg
  })
}

function fetchTest () {
  service.get('/test/hello').then(r => {
    textarea.value = r.data.msg
  })
}

function fetchAdmin () {
  service.get('/admin/hello').then(r => {
    textarea.value = r.data.msg
  })
}
</script>

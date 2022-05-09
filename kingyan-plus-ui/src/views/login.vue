<template>
  <div id="login_container">
    <div class="container">
      <div class="form-box" :style="formBoxTransform">
        <!-- 注册 -->
        <div class="register-box" :class="registerBox">
          <h1>register</h1>
          <input type="text" placeholder="用户名">
          <input type="email" placeholder="邮箱">
          <input type="password" placeholder="密码">
          <input type="password" placeholder="确认密码">
          <button>注册</button>
        </div>
        <!-- 登录 -->
        <div class="login-box" :class="loginBox">
          <h1>login</h1>
          <input v-model="loginData.userName" type="text" placeholder="用户名">
          <input v-model="loginData.pwd" type="password" placeholder="密码">
          <button @click="login">登录</button>
        </div>
      </div>
      <div class=" con-box left
          ">
        <h2>欢迎注册<span>清焰</span></h2>
        <p>简洁<span>管理</span>平台</p>
        <img src="@/assets/images/1.png" alt="">
        <p>已有账号</p>
        <button id="login" @click="toLogin">去登录</button>
      </div>
      <div class="con-box right">
        <h2>欢迎来到<span>清焰</span></h2>
        <p>简洁<span>管理</span>平台</p>
        <img src="@/assets/images/2.png" alt="">
        <p>没有账号？</p>
        <button id="register" @click="toRegister">去注册</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import service from '@/axios'
import { reactive, ref } from 'vue'
import router from '@/router'
import elMessage from '@/util/el-message'
// eslint-disable-next-line @typescript-eslint/no-var-requires
const sm2 = require('sm-crypto').sm2

// todo: 图片验证码
interface LoginConfig {
  publicKey: ''
}

const loginConfig = ref<LoginConfig>()

// todo: 有公钥就不再请求公钥
service({ method: 'get', url: '/getLoginConfig' })
  .then(({ data }) => {
    loginConfig.value = data
  })

const loginData = reactive({ userName: '', pwd: '' })
const encryptedLoginData = reactive({ userName: '', pwd: '' })

function login () {
  if (loginConfig.value) {
    // encryptedLoginData.userName = sm2.doEncrypt(loginData.userName, loginConfig.value.publicKey)
    encryptedLoginData.userName = loginData.userName
    encryptedLoginData.pwd = sm2.doEncrypt(loginData.pwd, loginConfig.value.publicKey)
  }
  service({
    method: 'POST',
    url: '/login',
    data: 'username=' + encryptedLoginData.userName + '&password=' + encryptedLoginData.pwd
  })
    .then(({ data }) => {
      if (data.success) {
        router.push('/')
      } else {
        elMessage.elMessage(data.error.message, 'warning')
      }
    })
}

// const loginConfig = service.get('/getLoginConfig')

// 异步，这里不一定获取到了loginConfig
// console.log(loginConfig)

// 滑动样式
const formBoxTransform = reactive({ transform: '' })
const registerBox = reactive({ hidden: true })
const loginBox = reactive({ hidden: false })

function toRegister () {
  formBoxTransform.transform = 'translateX(80%)'
  loginBox.hidden = true
  registerBox.hidden = false
}

function toLogin () {
  formBoxTransform.transform = 'translateX(0%)'
  registerBox.hidden = true
  loginBox.hidden = false
}

</script>

<style scoped>
* {
  /* 初始化 */
  margin: 0;
  padding: 0;
}

#login_container {
  /* 100%窗口高度 */
  height: 100vh;
  /* 弹性布局 水平+垂直居中 */
  display: flex;
  justify-content: center;
  align-items: center;
  /* 渐变背景 */
  background: linear-gradient(200deg, #f3e7e9, #e3eeff);
}

.container {
  background-color: #fff;
  width: 650px;
  height: 415px;
  border-radius: 5px;
  /* 阴影 */
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.1);
  /* 相对定位 */
  position: relative;
}

.form-box {
  /* 绝对定位 */
  position: absolute;
  top: -10%;
  left: 5%;
  background-color: #d3b7d8;
  width: 320px;
  height: 500px;
  border-radius: 5px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
  /* 动画过渡 加速后减速 */
  transition: 0.5s ease-in-out;
}

.register-box, .login-box {
  /* 弹性布局 垂直排列 */
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.hidden {
  display: none;
  transition: 0.5s;
}

h1 {
  text-align: center;
  margin-bottom: 25px;
  /* 大写 */
  text-transform: uppercase;
  color: #fff;
  /* 字间距 */
  letter-spacing: 5px;
}

input {
  background-color: transparent;
  width: 70%;
  color: #fff;
  border: none;
  /* 下边框样式 */
  border-bottom: 1px solid rgba(255, 255, 255, 0.4);
  padding: 10px 0;
  text-indent: 10px;
  margin: 8px 0;
  font-size: 14px;
  letter-spacing: 2px;
}

input::placeholder {
  color: #fff;
}

input:focus {
  color: #a262ad;
  outline: none;
  border-bottom: 1px solid #a262ad80;
  transition: 0.5s;
}

input:focus::placeholder {
  opacity: 0;
}

.form-box button {
  width: 70%;
  margin-top: 35px;
  background-color: #f6f6f6;
  outline: none;
  border-radius: 8px;
  padding: 13px;
  color: #a262ad;
  letter-spacing: 2px;
  border: none;
  cursor: pointer;
}

.form-box button:hover {
  background-color: #a262ad;
  color: #f6f6f6;
  transition: background-color 0.5s ease;
}

.con-box {
  width: 50%;
  /* 弹性布局 垂直排列 居中 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  /* 绝对定位 居中 */
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

.con-box.left {
  left: -2%;
}

.con-box.right {
  right: -2%;
}

.con-box h2 {
  color: #8e9aaf;
  font-size: 25px;
  font-weight: bold;
  letter-spacing: 3px;
  text-align: center;
  margin-bottom: 4px;
}

.con-box p {
  font-size: 12px;
  letter-spacing: 2px;
  color: #8e9aaf;
  text-align: center;
}

.con-box span {
  color: #d3b7d8;
}

.con-box img {
  width: 150px;
  height: 150px;
  opacity: 0.9;
  margin: 40px 0;
}

.con-box button {
  margin-top: 3%;
  background-color: #fff;
  color: #a262ad;
  border: 1px solid #d3b7d8;
  padding: 6px 10px;
  border-radius: 5px;
  letter-spacing: 1px;
  outline: none;
  cursor: pointer;
}

.con-box button:hover {
  background-color: #d3b7d8;
  color: #fff;
}
</style>

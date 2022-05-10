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
          <input v-model="loginData.password" type="password" placeholder="密码">
          <el-row justify="center">
            <el-col :span="9"><input v-model="loginData.captcha" type="text" style="width: 95%" placeholder="验证码"></el-col>
            <el-col :span="9"><img style="width: 95%; transform: translateY(10%)" :src="captchaSrc+captchaSrcFlag" @click="()=>captchaSrcFlag++"></el-col>
          </el-row>
          <button @click="login">登录</button>
        </div>
      </div>
      <div class="con-box left">
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
import { sm2 } from 'sm-crypto'
import { Base64 } from 'js-base64'
import hexToArrayBuffer from 'hex-to-array-buffer'
import elMessage from '@/util/el-message'

// todo: 图片验证码
interface LoginConfig {
  publicKey: string
  timeout: number
}

const loginConfig = ref<LoginConfig>()
const captchaSrc = ref(import.meta.env.VITE_APP_BASE_URL + 'auth/getCaptchaImg?')
const captchaSrcFlag = ref(1)

service({ method: 'get', url: '/auth/getLoginConfig' })
  .then(({ data }) => {
    loginConfig.value = data
  })

// todo: captcha and rememberMe
const loginData = reactive({ captcha: '', userName: '', password: '', rememberMe: false })
const encryptedLoginData = reactive({ captcha: '', userName: '', password: '', rememberMe: false })
const user = ref()

async function login () {
  if (loginConfig.value) {
    // 如果密公钥过期，必须用同步请求先更新公钥
    if (Date.now() > loginConfig.value.timeout) {
      await service.get('/auth/getLoginConfig').then(({ data }) => {
        loginConfig.value = data
      })
    }
    // 因为sm-crypto库的加密结果不符合规范，手动添加上第一个字节 0x04。拷贝数组性能更高，但是ArrayBuffer难写...
    encryptedLoginData.userName = '04' + sm2.doEncrypt(loginData.userName, loginConfig.value.publicKey)
    let byte = hexToArrayBuffer(encryptedLoginData.userName)
    encryptedLoginData.userName = Base64.fromUint8Array(new Uint8Array(byte))
    encryptedLoginData.password = '04' + sm2.doEncrypt(loginData.password, loginConfig.value.publicKey)
    byte = hexToArrayBuffer(encryptedLoginData.password)
    encryptedLoginData.password = Base64.fromUint8Array(new Uint8Array(byte))
    //
    encryptedLoginData.captcha = loginData.captcha
  }
  service({
    method: 'POST',
    url: '/auth/login',
    data: encryptedLoginData
  })
    .then(({ data }) => {
      if (data.success) {
        router.push('/')
        user.value = data.user
        console.log(user.value)
      } else {
        // todo: elMessage style error
        elMessage.elMessage(data.msg, 'warning')
        captchaSrcFlag.value++
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

defineExpose({ user })

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

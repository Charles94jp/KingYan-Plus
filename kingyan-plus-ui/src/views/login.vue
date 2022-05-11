<template>
  <div id="login_container">
    <div class="container">
      <div class="form-box" :style="formBoxTransform">
        <!-- 注册 -->
        <div class="register-box" :class="registerBox">
          <h1>register</h1>
          <el-row justify="center" style="width: 242px; transform: translateX(3.2%)">
            <input @input="checkUserNameLegality" style="width: 92.6%;" type="text" v-model="registerData.userName" placeholder="用户名">
            <el-icon v-if="!registerData.userName||checkUserName"></el-icon>
            <el-tooltip v-if="registerData.userName&&!checkUserName" content="至少3位且只能使用数字字母" placement="top" effect="light">
              <el-icon color="var(--el-color-error-dark-2)" style="transform: translateY(100%)"><circle-close-filled/></el-icon>
            </el-tooltip>
          </el-row>
          <input type="email" v-model="encryptedRegisterData.email" placeholder="邮箱">
          <input type="password" v-model="registerData.password" placeholder="密码">
          <el-row justify="center" style="width: 242px; transform: translateX(3.2%)">
            <input style="width: 92.6%;" @input="checkPassword" type="password" v-model="registerData.confirmPassword" placeholder="确认密码">
            <el-icon v-if="!registerData.confirmPassword"></el-icon>
            <el-icon v-if="registerData.confirmPassword&&confirmPassword" color="var(--el-color-success-dark-2)" style="transform: translateY(100%)"><circle-check-filled/></el-icon>
            <el-tooltip content="两次输入的密码不一致" placement="top" effect="light" v-if="!confirmPassword">
              <el-icon color="var(--el-color-error-dark-2)" style="transform: translateY(100%)"><circle-close-filled/></el-icon>
            </el-tooltip>
          </el-row>
          <el-row justify="center">
            <el-col :span="9"><input v-model="encryptedRegisterData.captcha" type="text" style="width: 95%; transform: translateX(3%)" placeholder="验证码"></el-col>
            <el-col :span="9"><img style="width: 95%; transform: translate(2%,10%)" :src="captchaSrc+captchaSrcFlag" @click="()=>captchaSrcFlag++"></el-col>
          </el-row>
          <button @click="register">注册</button>
        </div>
        <!-- 登录 -->
        <div class="login-box" :class="loginBox">
          <h1>login</h1>
          <input v-model="loginData.userName" type="text" placeholder="用户名">
          <input v-model="loginData.password" type="password" placeholder="密码">
          <el-row justify="center">
            <el-col :span="9"><input v-model="encryptedLoginData.captcha" type="text" style="width: 95%; transform: translateX(3%)" placeholder="验证码"></el-col>
            <el-col :span="9"><img style="width: 95%; transform: translate(2%,10%)" :src="captchaSrc+captchaSrcFlag" @click="()=>captchaSrcFlag++"></el-col>
          </el-row>
          <button @click="login">登录</button>
          <el-checkbox v-model="encryptedLoginData.rememberMe" label="记住我" size="large" />
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
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'

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

const loginData = reactive({ userName: '', password: '' })
const encryptedLoginData = reactive({ captcha: '', userName: '', password: '', rememberMe: false })
const registerData = reactive({ userName: '', password: '', confirmPassword: '' })
const encryptedRegisterData = reactive({ captcha: '', userName: '', password: '', email: '' })
const checkUserName = ref(false)
const confirmPassword = ref(true)

function checkUserNameLegality () {
  checkUserName.value = /^[a-zA-Z0-9]{3,}$/.test(registerData.userName)
}

function checkPassword () {
  if (registerData.password === registerData.confirmPassword) {
    confirmPassword.value = true
  } else {
    confirmPassword.value = false
  }
}

async function realEncrypt (plaintext:string) {
  if (!loginConfig.value) {
    return
  }
  // 如果密公钥过期，必须用同步请求先更新公钥
  console.log(Date.now())
  console.log(loginConfig.value.timeout)
  if (Date.now() > loginConfig.value.timeout) {
    await service.get('/auth/getLoginConfig').then(({ data }) => {
      loginConfig.value = data
    })
  }
  // 因为sm-crypto库的加密结果不符合规范，手动添加上第一个字节 0x04。拷贝数组性能更高，但是ArrayBuffer难写...
  const hexStr = '04' + sm2.doEncrypt(plaintext, loginConfig.value.publicKey)
  const byte = hexToArrayBuffer(hexStr)
  return Base64.fromUint8Array(new Uint8Array(byte))
}

async function login () {
  const _userName = await realEncrypt(loginData.userName)
  if (!_userName) return
  encryptedLoginData.userName = _userName
  const _password = await realEncrypt(loginData.password)
  if (!_password) return
  encryptedLoginData.password = _password

  service({
    method: 'POST',
    url: '/auth/login',
    data: encryptedLoginData
  })
    .then(({ data }) => {
      if (data.success) {
        router.push('/')
      } else {
        // todo: elMessage style error
        elMessage.elMessage(data.msg, 'warning')
        captchaSrcFlag.value++
      }
    })
}

async function register () {
  if (!confirmPassword.value || !checkUserName.value || !encryptedRegisterData.email) {
    elMessage.elMessage('输入的数据有误', 'warning')
  }
  const _userName = await realEncrypt(registerData.userName)
  if (!_userName) return
  encryptedRegisterData.userName = _userName
  const _password = await realEncrypt(registerData.password)
  if (!_password) return
  encryptedRegisterData.password = _password
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
  captchaSrcFlag.value++
}

function toLogin () {
  formBoxTransform.transform = 'translateX(0%)'
  registerBox.hidden = true
  loginBox.hidden = false
  captchaSrcFlag.value++
}

</script>

<style scoped>
* {
  /* 初始化 */
  margin: 0;
  padding: 0;
}

/*--勾选框start*/
.login-box >>> .el-checkbox__label {
  color: #a262ad;
}

.login-box >>> .is-checked .el-checkbox__label {
  color: #de7ab1;
}

.login-box >>> .is-checked .el-checkbox__inner{
  background-color: #de7ab1;
  border-color: #e18dac
}

/*focus时外框的颜色*/
.el-checkbox{
  --el-checkbox-input-border-color-hover: #e18dac
}

.el-checkbox__input.is-checked+.el-checkbox__label {
  color: var(--el-color-danger);
}
/*--勾选框end*/

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

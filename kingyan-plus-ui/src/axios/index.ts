import axios from 'axios'
import elMessage from '@/util/el-message'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API_URL,
  timeout: 3000,
  headers: { 'X-Custom-Header': 'foobar' }
})

// 添加请求拦截器
service.interceptors.request.use(function (config) {
  // 在发送请求之前做些什么
  return config
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error)
})

// 响应拦截器
service.interceptors.response.use(function (response) {
  // 2xx 范围内的状态码都会触发该函数。
  // 对响应数据做点什么
  return response
}, function (error) {
  // 超时
  if (!error.response) {
    return Promise.reject(error)
  }
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  if (error.response.status === 401) {
    elMessage.elMessage('请先登录', 'warning')
    router.push('/login')
  } else if (error.response.status === 403) {
    elMessage.elMessage('权限不足，拒绝访问', 'warning')
  } else {
    return Promise.reject(error)
  }
})

export default service

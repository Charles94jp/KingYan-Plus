import { AxiosError } from 'axios'

export default {
  commonErrorCatch (error: AxiosError) {
    if (error.response) {
      // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
      console.log(error.response.data)
      console.log(error.response.status)
      console.log(error.response.headers)
    } else if (error.request) {
      // 请求已经成功发起，但没有收到响应
      // `error.request` 在浏览器中是 XMLHttpRequest 的实例，
      // 而在node.js中是 http.ClientRequest 的实例
      console.log('服务器未响应 [' + error.config.url + '] : ' + error.message)
    } else {
      // 发送请求时出了点问题
      console.log('Error', error.message)
    }
    // console.log(error.config)
  }
}

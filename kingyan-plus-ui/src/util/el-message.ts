import { ElMessage } from 'element-plus/es'
import 'element-plus/es/components/message/style/css'
import { BuildPropType } from 'element-plus/es/utils'

export default {
  elMessage: function (msg: string, mtype: BuildPropType<StringConstructor, 'info' | 'success' | 'warning' | 'error', unknown> | undefined) {
    ElMessage({
      message: msg,
      type: mtype,
      duration: 1200,
      center: true,
      showClose: true
    })
  }
}

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// npm install @types/node
// @ts-ignore
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Icons from 'unplugin-icons/vite'
import Components from 'unplugin-vue-components/vite'
import IconsResolver from 'unplugin-icons/resolver'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // element-plus
    AutoImport({
      resolvers: [
        // 按需导入ElementPlus，只能template中的标签生效，js中的方法还需要导入
        ElementPlusResolver(),
        // 按需导入ElementPlus icons，未生效
        IconsResolver({
          prefix: 'Icon'
        })
      ]
    }),
    Components({
      resolvers: [
        ElementPlusResolver(),
        IconsResolver({
          enabledCollections: ['ep']
        })
      ]
    }),
    Icons({
      autoInstall: true
    })
  ],
  // @别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})

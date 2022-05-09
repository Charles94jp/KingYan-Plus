import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// npm install @types/node
// @ts-ignore
import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // element-plus
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  // @别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})

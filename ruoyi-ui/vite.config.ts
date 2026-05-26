import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      host: '0.0.0.0',
      port: 80,
      open: true,
      proxy: {
        '/auth': {
          target: env.VITE_APP_BASE_API || 'http://localhost:8080',
          changeOrigin: true
        },
        '/system': {
          target: env.VITE_APP_BASE_API || 'http://localhost:8080',
          changeOrigin: true
        },
        '/code': {
          target: env.VITE_APP_BASE_API || 'http://localhost:8080',
          changeOrigin: true
        },
        '/job': {
          target: env.VITE_APP_BASE_API || 'http://localhost:8080',
          changeOrigin: true
        },
        '/file': {
          target: env.VITE_APP_BASE_API || 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    build: {
      outDir: 'dist',
      assetsDir: 'assets',
      sourcemap: false,
      chunkSizeWarningLimit: 1500
    }
  }
})

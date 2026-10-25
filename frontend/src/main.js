import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'//导入其样式
import { createPinia } from 'pinia'
import locale from 'element-plus/dist/locale/zh-cn.js'
import './assets/main.scss'
import router from '@/router'
import {createPersistedState} from 'pinia-persistedstate-plugin'

import App from './App.vue'

const app = createApp(App)
const pinia= createPinia()
const persist=createPersistedState()
pinia.use(persist)
app.use(pinia)
app.use(router)
app.use(ElementPlus,{locale})
app.mount('#app')

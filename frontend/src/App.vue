<template>
  <div id="app">
    <el-container style="height: 100vh">
      <el-aside width="200px" style="background-color: #304156">
        <div class="logo" style="height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #1f2d3d">
          AutoTest
        </div>
        <el-menu
            :default-active="activeMenu"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF"
            router
        >
          <el-menu-item index="/sql-rules">
            <el-icon><Document /></el-icon>
            <span>SQL规则</span>
          </el-menu-item>
          <el-menu-item index="/param-rules">
            <el-icon><Edit /></el-icon>
            <span>入参规则</span>
          </el-menu-item>
          <el-menu-item index="/response-rules">
            <el-icon><Checked /></el-icon>
            <span>返参规则</span>
          </el-menu-item>
          <el-menu-item index="/api-definitions">
            <el-icon><Connection /></el-icon>
            <span>接口定义</span>
          </el-menu-item>
          <el-menu-item index="/api-tests">
            <el-icon><Monitor /></el-icon>
            <span>接口测试</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="background-color: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; padding: 0 20px">
          <span style="font-size: 18px; font-weight: bold">{{ currentMenuName }}</span>
        </el-header>
        <el-main style="background-color: #f0f2f5; padding: 20px">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)

const menuMap = {
  '/sql-rules': 'SQL规则管理',
  '/param-rules': '入参规则管理',
  '/response-rules': '返参规则管理',
  '/api-definitions': '接口定义管理',
  '/api-tests': '接口测试管理'
}

const currentMenuName = computed(() => menuMap[route.path] || '')
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

.el-menu-item.is-active {
  background-color: #263445 !important;
}
</style>
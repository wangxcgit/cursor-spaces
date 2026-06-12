<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="header-left" @click="$router.push('/')">
        <el-icon :size="24"><Shop /></el-icon>
        <span class="logo-text">商城</span>
      </div>
      <div class="header-center">
        <el-input
          v-model="keyword"
          placeholder="搜索商品"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      <div class="header-right">
        <el-button text @click="$router.push('/cart')">
          <el-icon><ShoppingCart /></el-icon> 购物车
        </el-button>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <el-button text>
              <el-icon><User /></el-icon> {{ userStore.username }}
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/orders')">我的订单</el-dropdown-item>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
        </template>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
    <el-footer class="app-footer">
      © 2024 Mall Shop - Spring Boot + Vue 商城
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

function handleSearch() {
  router.push({ path: '/', query: { keyword: keyword.value } })
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f5f5f5; }
.app-container { min-height: 100vh; }
.app-header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.08); padding: 0 40px;
}
.header-left { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.logo-text { font-size: 20px; font-weight: bold; color: #409eff; }
.header-center { flex: 1; max-width: 500px; margin: 0 40px; }
.search-input { width: 100%; }
.header-right { display: flex; align-items: center; gap: 12px; }
.app-footer { text-align: center; color: #999; background: #fff; }
</style>

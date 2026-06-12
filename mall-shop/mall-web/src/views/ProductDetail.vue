<template>
  <div class="product-detail" v-if="product">
    <el-row :gutter="40">
      <el-col :span="10">
        <img :src="product.mainImage" class="main-image" />
      </el-col>
      <el-col :span="14">
        <h1 class="title">{{ product.name }}</h1>
        <p class="subtitle">{{ product.subtitle }}</p>
        <div class="price-box">
          <span class="price">¥{{ product.price }}</span>
          <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
        </div>
        <div class="meta">
          <span>库存: {{ product.stock }}</span>
          <span>销量: {{ product.sales }}</span>
          <span>分类: {{ product.categoryName }}</span>
        </div>
        <div class="quantity">
          <span>数量:</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock" />
        </div>
        <div class="actions">
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </el-col>
    </el-row>
    <div class="detail-section" v-if="product.detail">
      <h3>商品详情</h3>
      <div v-html="product.detail"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { productApi, cartApi } from '@/api'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const product = ref(null)
const quantity = ref(1)

async function loadProduct() {
  product.value = await productApi.detail(route.params.id)
}

async function addToCart() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await cartApi.add({ productId: product.value.id, quantity: quantity.value })
  ElMessage.success('已加入购物车')
}

function buyNow() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  router.push({ path: '/cart', query: { buy: product.value.id, qty: quantity.value } })
}

onMounted(loadProduct)
</script>

<style scoped>
.product-detail { max-width: 1200px; margin: 0 auto; background: #fff; padding: 30px; border-radius: 8px; }
.main-image { width: 100%; border-radius: 8px; }
.title { font-size: 24px; margin-bottom: 8px; }
.subtitle { color: #999; margin-bottom: 20px; }
.price-box { background: #fff5f5; padding: 16px; border-radius: 8px; margin-bottom: 20px; }
.price { color: #f56c6c; font-size: 32px; font-weight: bold; }
.original-price { color: #999; text-decoration: line-through; margin-left: 12px; }
.meta { display: flex; gap: 24px; color: #666; margin-bottom: 20px; }
.quantity { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; }
.actions { display: flex; gap: 16px; }
.detail-section { margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; }
</style>

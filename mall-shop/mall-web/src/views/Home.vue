<template>
  <div class="home">
    <div class="category-bar">
      <el-button
        v-for="cat in categories"
        :key="cat.id"
        :type="selectedCategory === cat.id ? 'primary' : 'default'"
        text
        @click="selectCategory(cat.id)"
      >
        {{ cat.name }}
      </el-button>
    </div>
    <el-row :gutter="20" class="product-grid">
      <el-col v-for="product in products" :key="product.id" :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="product-card" @click="$router.push(`/product/${product.id}`)">
          <img :src="product.mainImage" class="product-image" />
          <div class="product-info">
            <div class="product-name">{{ product.name }}</div>
            <div class="product-subtitle">{{ product.subtitle }}</div>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-sales">已售 {{ product.sales }} 件</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadProducts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { productApi, categoryApi } from '@/api'

const route = useRoute()
const categories = ref([])
const products = ref([])
const selectedCategory = ref(null)
const keyword = ref('')
const page = ref(1)
const size = ref(12)
const total = ref(0)

async function loadCategories() {
  categories.value = await categoryApi.list()
}

async function loadProducts() {
  const data = await productApi.list({
    categoryId: selectedCategory.value,
    keyword: keyword.value,
    page: page.value,
    size: size.value
  })
  products.value = data.records
  total.value = data.total
}

function selectCategory(id) {
  selectedCategory.value = selectedCategory.value === id ? null : id
  page.value = 1
  loadProducts()
}

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  page.value = 1
  loadProducts()
}, { immediate: true })

onMounted(loadCategories)
</script>

<style scoped>
.home { max-width: 1200px; margin: 0 auto; }
.category-bar { background: #fff; padding: 16px; border-radius: 8px; margin-bottom: 20px; display: flex; flex-wrap: wrap; gap: 8px; }
.product-grid { margin-bottom: 20px; }
.product-card { cursor: pointer; margin-bottom: 20px; }
.product-image { width: 100%; height: 200px; object-fit: cover; }
.product-info { padding: 12px 0; }
.product-name { font-size: 14px; font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-subtitle { font-size: 12px; color: #999; margin: 4px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { margin: 8px 0; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; }
.original-price { color: #999; font-size: 12px; text-decoration: line-through; margin-left: 8px; }
.product-sales { font-size: 12px; color: #999; }
.pagination { display: flex; justify-content: center; padding: 20px; }
</style>

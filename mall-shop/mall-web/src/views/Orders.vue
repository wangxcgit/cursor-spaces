<template>
  <div class="orders-page">
    <h2>我的订单</h2>
    <el-tabs v-model="activeStatus" @tab-change="loadOrders">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="待收货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>
    <div v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span>订单号: {{ order.orderNo }}</span>
        <span>{{ order.createTime }}</span>
        <el-tag :type="statusType(order.status)">{{ order.statusDesc }}</el-tag>
      </div>
      <div v-for="item in order.items" :key="item.productId" class="order-item">
        <img :src="item.productImage" class="item-image" />
        <div class="item-info">
          <div>{{ item.productName }}</div>
          <div class="item-price">¥{{ item.price }} x {{ item.quantity }}</div>
        </div>
        <div class="item-subtotal">¥{{ item.subtotal }}</div>
      </div>
      <div class="order-footer">
        <span>实付: <strong class="pay-amount">¥{{ order.payAmount }}</strong></span>
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" size="small" @click="payOrder(order.id)">去支付</el-button>
          <el-button v-if="order.status === 0" size="small" @click="cancelOrder(order.id)">取消</el-button>
          <el-button v-if="order.status === 2" type="success" size="small" @click="confirmOrder(order.id)">确认收货</el-button>
        </div>
      </div>
    </div>
    <el-empty v-if="!orders.length" description="暂无订单" />
    <div class="pagination" v-if="total > 0">
      <el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev, pager, next" @current-change="loadOrders" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api'

const orders = ref([])
const activeStatus = ref('all')
const page = ref(1)
const total = ref(0)

function statusType(status) {
  const map = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

async function loadOrders() {
  const params = { page: page.value, size: 10 }
  if (activeStatus.value !== 'all') params.status = Number(activeStatus.value)
  const data = await orderApi.list(params)
  orders.value = data.records
  total.value = data.total
}

async function payOrder(id) {
  await orderApi.pay(id)
  ElMessage.success('支付成功')
  loadOrders()
}

async function cancelOrder(id) {
  await ElMessageBox.confirm('确定取消该订单？', '提示')
  await orderApi.cancel(id)
  ElMessage.success('订单已取消')
  loadOrders()
}

async function confirmOrder(id) {
  await orderApi.confirm(id)
  ElMessage.success('已确认收货')
  loadOrders()
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page { max-width: 900px; margin: 0 auto; }
.order-card { background: #fff; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
.order-header { display: flex; justify-content: space-between; align-items: center; padding-bottom: 12px; border-bottom: 1px solid #eee; color: #666; font-size: 13px; }
.order-item { display: flex; align-items: center; padding: 12px 0; gap: 12px; }
.item-image { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.item-info { flex: 1; }
.item-price { color: #999; font-size: 13px; margin-top: 4px; }
.item-subtotal { color: #f56c6c; font-weight: bold; }
.order-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 12px; border-top: 1px solid #eee; }
.pay-amount { color: #f56c6c; font-size: 18px; }
.order-actions { display: flex; gap: 8px; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
</style>

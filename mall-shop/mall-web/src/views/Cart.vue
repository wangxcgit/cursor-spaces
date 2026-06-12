<template>
  <div class="cart-page">
    <h2>我的购物车</h2>
    <el-table :data="cart.items" v-if="cart.items?.length" style="width: 100%">
      <el-table-column label="商品" min-width="300">
        <template #default="{ row }">
          <div class="cart-item">
            <img :src="row.mainImage" class="item-image" />
            <span>{{ row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="120">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="数量" width="160">
        <template #default="{ row }">
          <el-input-number
            v-model="row.quantity"
            :min="1"
            :max="row.stock"
            size="small"
            @change="(val) => updateQuantity(row.productId, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120">
        <template #default="{ row }">
          <span class="subtotal">¥{{ row.subtotal }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" text @click="removeItem(row.productId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-else description="购物车是空的" />
    <div class="cart-footer" v-if="cart.items?.length">
      <div class="total">
        共 <strong>{{ cart.totalQuantity }}</strong> 件商品，合计：
        <span class="total-price">¥{{ cart.totalAmount }}</span>
      </div>
      <el-button type="danger" size="large" @click="showCheckout = true">去结算</el-button>
    </div>

    <el-dialog v-model="showCheckout" title="确认订单" width="500px">
      <el-form label-width="80px">
        <el-form-item label="收货地址">
          <el-select v-model="selectedAddress" placeholder="选择收货地址" style="width: 100%">
            <el-option
              v-for="addr in addresses"
              :key="addr.id"
              :label="`${addr.receiverName} ${addr.receiverPhone} ${addr.province}${addr.city}${addr.district}${addr.detail}`"
              :value="addr.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="remark" type="textarea" placeholder="订单备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi, addressApi, orderApi } from '@/api'

const router = useRouter()
const cart = reactive({ items: [], totalQuantity: 0, totalAmount: 0 })
const addresses = ref([])
const showCheckout = ref(false)
const selectedAddress = ref(null)
const remark = ref('')
const submitting = ref(false)

async function loadCart() {
  const data = await cartApi.get()
  Object.assign(cart, data)
}

async function loadAddresses() {
  addresses.value = await addressApi.list()
  const defaultAddr = addresses.value.find(a => a.isDefault === 1)
  if (defaultAddr) selectedAddress.value = defaultAddr.id
}

async function updateQuantity(productId, quantity) {
  await cartApi.update(productId, quantity)
  loadCart()
}

async function removeItem(productId) {
  await cartApi.remove(productId)
  ElMessage.success('已删除')
  loadCart()
}

async function submitOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const order = await orderApi.create({ addressId: selectedAddress.value, remark: remark.value })
    ElMessage.success('订单创建成功')
    showCheckout.value = false
    router.push('/orders')
    await orderApi.pay(order.id)
    ElMessage.success('支付成功')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCart()
  loadAddresses()
})
</script>

<style scoped>
.cart-page { max-width: 1000px; margin: 0 auto; background: #fff; padding: 24px; border-radius: 8px; }
.cart-item { display: flex; align-items: center; gap: 12px; }
.item-image { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; }
.subtotal { color: #f56c6c; font-weight: bold; }
.cart-footer { display: flex; justify-content: flex-end; align-items: center; gap: 24px; margin-top: 24px; padding-top: 16px; border-top: 1px solid #eee; }
.total-price { color: #f56c6c; font-size: 24px; font-weight: bold; }
</style>

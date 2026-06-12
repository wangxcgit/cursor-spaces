<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>个人信息</template>
          <el-form :model="profile" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="profile.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profile.nickname" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profile.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profile.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="address-header">
              <span>收货地址</span>
              <el-button type="primary" text @click="showAddAddress = true">新增地址</el-button>
            </div>
          </template>
          <div v-for="addr in addresses" :key="addr.id" class="address-item">
            <div>
              <strong>{{ addr.receiverName }}</strong> {{ addr.receiverPhone }}
              <el-tag v-if="addr.isDefault" size="small" type="success">默认</el-tag>
            </div>
            <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
            <div class="address-actions">
              <el-button text size="small" @click="setDefault(addr.id)" v-if="!addr.isDefault">设为默认</el-button>
              <el-button text size="small" type="danger" @click="removeAddress(addr.id)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="!addresses.length" description="暂无地址" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showAddAddress" title="新增地址" width="500px">
      <el-form :model="addressForm" label-width="80px">
        <el-form-item label="收货人"><el-input v-model="addressForm.receiverName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="addressForm.receiverPhone" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="addressForm.province" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="addressForm.city" /></el-form-item>
        <el-form-item label="区县"><el-input v-model="addressForm.district" /></el-form-item>
        <el-form-item label="详细地址"><el-input v-model="addressForm.detail" type="textarea" /></el-form-item>
        <el-form-item label="默认地址"><el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAddress = false">取消</el-button>
        <el-button type="primary" @click="addAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi, addressApi } from '@/api'

const profile = reactive({ username: '', nickname: '', phone: '', email: '' })
const addresses = ref([])
const showAddAddress = ref(false)
const addressForm = reactive({
  receiverName: '', receiverPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0
})

async function loadProfile() {
  const data = await userApi.profile()
  Object.assign(profile, data)
}

async function loadAddresses() {
  addresses.value = await addressApi.list()
}

async function saveProfile() {
  await userApi.updateProfile(profile)
  ElMessage.success('保存成功')
}

async function addAddress() {
  await addressApi.create(addressForm)
  ElMessage.success('地址添加成功')
  showAddAddress.value = false
  loadAddresses()
}

async function setDefault(id) {
  await addressApi.setDefault(id)
  ElMessage.success('已设为默认地址')
  loadAddresses()
}

async function removeAddress(id) {
  await addressApi.remove(id)
  ElMessage.success('地址已删除')
  loadAddresses()
}

onMounted(() => {
  loadProfile()
  loadAddresses()
})
</script>

<style scoped>
.profile-page { max-width: 1000px; margin: 0 auto; }
.address-header { display: flex; justify-content: space-between; align-items: center; }
.address-item { padding: 12px 0; border-bottom: 1px solid #eee; }
.address-detail { color: #666; font-size: 13px; margin: 4px 0; }
.address-actions { display: flex; gap: 8px; }
</style>

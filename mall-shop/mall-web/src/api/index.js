import request from './request'

export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data)
}

export const productApi = {
  list: (params) => request.get('/products', { params }),
  detail: (id) => request.get(`/products/${id}`)
}

export const categoryApi = {
  list: () => request.get('/categories')
}

export const cartApi = {
  get: () => request.get('/cart'),
  add: (data) => request.post('/cart', data),
  update: (productId, quantity) => request.put(`/cart/${productId}`, null, { params: { quantity } }),
  remove: (productId) => request.delete(`/cart/${productId}`),
  clear: () => request.delete('/cart')
}

export const orderApi = {
  create: (data) => request.post('/orders', data),
  list: (params) => request.get('/orders', { params }),
  detail: (id) => request.get(`/orders/${id}`),
  pay: (id) => request.post(`/orders/${id}/pay`),
  cancel: (id) => request.post(`/orders/${id}/cancel`),
  confirm: (id) => request.post(`/orders/${id}/confirm`)
}

export const addressApi = {
  list: () => request.get('/addresses'),
  create: (data) => request.post('/addresses', data),
  update: (id, data) => request.put(`/addresses/${id}`, data),
  remove: (id) => request.delete(`/addresses/${id}`),
  setDefault: (id) => request.put(`/addresses/${id}/default`)
}

export const userApi = {
  profile: () => request.get('/user/profile'),
  updateProfile: (data) => request.put('/user/profile', data)
}

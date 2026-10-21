export class ApiError extends Error {
  constructor(status, body) {
    super(body?.message || body?.status || `Request failed (${status})`)
    this.status = status
    this.body = body
  }
}

async function request(path, options = {}) {
  const response = await fetch(path, { headers: { Accept: 'application/json', ...options.headers }, ...options })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new ApiError(response.status, body)
  return body
}

export const getStock = (productId) => request(`/api/v1/orders/products/${productId}/stock`)
export const createOrder = (productId, idempotencyKey) => request('/api/v1/orders', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ productId, quantity: 1, idempotencyKey }),
})
export const getOrder = (orderId) => request(`/api/v1/orders/${encodeURIComponent(orderId)}`)

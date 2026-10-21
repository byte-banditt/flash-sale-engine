import { useCallback, useEffect, useRef, useState } from 'react'
import { createOrder, getOrder, getStock, ApiError } from './api'
import './styles.css'

const PRODUCT_ID = 101
const TERMINAL = new Set(['CONFIRMED', 'CANCELLED'])

function messageFor(error) {
  if (!(error instanceof ApiError)) return 'Network failure. Retry the same purchase attempt.'
  if (error.status === 400) return error.body?.message || 'The purchase request is invalid.'
  if (error.status === 404 || error.body?.status === 'NOT_FOUND') return 'Sale is not initialized.'
  if (error.body?.status === 'OUT_OF_STOCK') return 'Out of stock.'
  return error.body?.message || 'Purchase failed.'
}

export default function App() {
  const [stock, setStock] = useState(null)
  const [stockMessage, setStockMessage] = useState('Loading stock…')
  const [order, setOrder] = useState(null)
  const [purchaseMessage, setPurchaseMessage] = useState('')
  const attemptKey = useRef(null)

  const refreshStock = useCallback(async () => {
    try {
      const current = await getStock(PRODUCT_ID)
      setStock(current.availableStock)
      setStockMessage('')
    } catch (error) {
      setStock(null)
      setStockMessage(messageFor(error))
    }
  }, [])

  useEffect(() => {
    refreshStock()
    const timer = setInterval(refreshStock, 5000)
    return () => clearInterval(timer)
  }, [refreshStock])

  useEffect(() => {
    if (!order?.orderId || TERMINAL.has(order.status)) return undefined
    const poll = async () => {
      try {
        setOrder(await getOrder(order.orderId))
      } catch (error) {
        setPurchaseMessage(messageFor(error))
      }
    }
    const timer = setInterval(poll, 1500)
    return () => clearInterval(timer)
  }, [order?.orderId, order?.status])

  async function buy() {
    if (!attemptKey.current) attemptKey.current = crypto.randomUUID()
    setPurchaseMessage('Submitting purchase…')
    try {
      const response = await createOrder(PRODUCT_ID, attemptKey.current)
      setOrder(response)
      setPurchaseMessage(response.status === 'PENDING' ? 'Order accepted; waiting for confirmation.' : response.message || 'Order already accepted.')
      refreshStock()
    } catch (error) {
      setPurchaseMessage(messageFor(error))
    }
  }

  return <main>
    <h1>Flash Sale</h1>
    <section aria-labelledby="stock-heading">
      <h2 id="stock-heading">Live stock</h2>
      {stock === null ? <p role="status">{stockMessage}</p> : <p role="status">{stock} available</p>}
      <button onClick={buy} disabled={stock === 0}>{attemptKey.current ? 'Retry purchase' : 'Buy'}</button>
    </section>
    <section aria-labelledby="order-heading">
      <h2 id="order-heading">Order status</h2>
      {order ? <p role="status">{order.orderId}: {order.status}</p> : <p role="status">No order yet.</p>}
      {purchaseMessage && <p role="alert">{purchaseMessage}</p>}
    </section>
  </main>
}

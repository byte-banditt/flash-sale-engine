import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react'
import App from './App'

const stock = (value = 5) => ({ ok: true, json: async () => ({ productId: 101, availableStock: value }) })
const response = (body, ok = true, status = 200) => ({ ok, status, json: async () => body })

beforeEach(() => {
  vi.stubGlobal('crypto', { randomUUID: vi.fn(() => 'attempt-1') })
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(stock()))
})
afterEach(() => { cleanup(); vi.useRealTimers(); vi.unstubAllGlobals() })

async function loaded() {
  render(<App />)
  await waitFor(() => expect(screen.getByText('5 available')).toBeInTheDocument())
}

describe('purchase states', () => {
  it('shows accepted pending order', async () => {
    await loaded()
    fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'PENDING', message: 'accepted' }))
    fireEvent.click(screen.getByRole('button', { name: 'Buy' }))
    await waitFor(() => expect(screen.getByText(/o-1: PENDING/)).toBeInTheDocument())
  })

  it('shows duplicate idempotency result', async () => {
    await loaded()
    fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'CONFIRMED', message: 'already accepted' }))
    fireEvent.click(screen.getByRole('button', { name: 'Buy' }))
    await waitFor(() => expect(screen.getByText(/o-1: CONFIRMED/)).toBeInTheDocument())
  })

  it.each([
    [{ status: 'OUT_OF_STOCK', message: 'Stock not available' }, 'Out of stock.'],
    [{ status: 'NOT_FOUND', message: 'Sale not initialized' }, 'Sale is not initialized.'],
    [{ status: 'INVALID_REQUEST', message: 'quantity must be greater than zero' }, 'quantity must be greater than zero'],
  ])('shows documented API error', async (body, text) => {
    await loaded()
    fetch.mockResolvedValueOnce(response(body, false, body.status === 'INVALID_REQUEST' ? 400 : 409))
    fireEvent.click(screen.getByRole('button', { name: 'Buy' }))
    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent(text))
  })

  it('shows network failure and retries with the same key', async () => {
    await loaded()
    fetch.mockRejectedValueOnce(new Error('offline'))
    fireEvent.click(screen.getByRole('button', { name: 'Buy' }))
    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('Network failure'))
    fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'PENDING' }))
    fireEvent.click(screen.getByRole('button', { name: 'Retry purchase' }))
    await waitFor(() => expect(screen.getByText(/o-1: PENDING/)).toBeInTheDocument())
    const calls = fetch.mock.calls.filter(([url]) => url === '/api/v1/orders')
    expect(JSON.parse(calls[0][1].body).idempotencyKey).toBe(JSON.parse(calls[1][1].body).idempotencyKey)
  })
})

it('polls until terminal status and then stops', async () => {
  await loaded()
  fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'PENDING' }))
  fireEvent.click(screen.getByRole('button', { name: 'Buy' }))
  await waitFor(() => expect(screen.getByText(/o-1: PENDING/)).toBeInTheDocument())
  fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'CONFIRMED' }))
  await new Promise((resolve) => setTimeout(resolve, 1700))
  await waitFor(() => expect(screen.getByText(/o-1: CONFIRMED/)).toBeInTheDocument())
  const callsAfterTerminal = fetch.mock.calls.length
  fetch.mockResolvedValueOnce(response({ orderId: 'o-1', status: 'PENDING' }))
  await new Promise((resolve) => setTimeout(resolve, 3200))
  expect(fetch.mock.calls.length).toBeGreaterThanOrEqual(callsAfterTerminal)
  expect(fetch.mock.calls.filter(([url]) => url === '/api/v1/orders/o-1').length).toBe(1)
})

import { ref, watch, effectScope } from 'vue'
const sessions = new Map()
export function useChatHistory() {
 const owner = sessionStorage.getItem('origin.chatOwner')
 const key = owner ? `origin.chat.v1:${owner}` : null
 if (sessions.has(key)) return sessions.get(key)
 let saved = {}
 try { saved = JSON.parse(key ? localStorage.getItem(key) || '{}' : '{}') } catch {}
 const messages = ref(Array.isArray(saved.messages) ? saved.messages : [])
 for (const m of messages.value) {
  if (m.state === 'sending') m.state = 'failed'
  if (m.state === 'applying') m.state = 'error'
 }
 const state = { messages, draft: ref(saved.draft || ''), busy: ref(false), applying: ref(false), storageError: ref(false) }
 effectScope(true).run(() => watch([messages, state.draft], () => {
  if (!key) return
  try { state.storageError.value = false; localStorage.setItem(key, JSON.stringify({messages: messages.value.map(m => m.file?.kind === 'image' ? {...m, file: undefined} : m), draft: state.draft.value})) }
  catch { state.storageError.value = true }
 }, {deep: true, flush: 'sync'}))
 sessions.set(key, state)
 return state
}

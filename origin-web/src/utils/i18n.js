import { preferences } from './preferences.js'
export const t = (zh, en) => preferences.language === 'en' ? en : zh

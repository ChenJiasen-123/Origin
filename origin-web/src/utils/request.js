import axios from 'axios'

const request = axios.create({
  baseURL: 'http://43.138.166.243:2528',//服务器IP
  timeout: 5000
})

export default request
import axios from 'axios'

const request = axios.create({
  baseURL: 'http://192.168.124.9:8080',//服务器IP
  timeout: 5000
})

export default request
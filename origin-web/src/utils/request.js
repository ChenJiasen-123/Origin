import axios from 'axios'

const request = axios.create({
  baseURL: '/origin',
  timeout: 5000,
  withCredentials: true
})

export default request

import axios from 'axios';
import type { AxiosInstance } from 'axios';

const getBaseUrl = () => {

  if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    return 'http://localhost:8080/api';
  }

  return 'https://librarymanagement-nxl6.onrender.com/api';
};

const api: AxiosInstance = axios.create({
  baseURL: getBaseUrl(),
  headers: { 'Content-Type': 'application/json' }
});

export default api;
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000, // 增加超时到30秒，匹配后端AI服务超时
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 如果后端返回统一格式 { code, message, data }
    const responseData = response.data
    if (responseData && typeof responseData === 'object') {
      const code = responseData.code
      // 成功状态码：0 或 200
      if (code === 0 || code === 200) {
        // 如果包含data字段，返回data；否则返回整个响应
        return responseData.data ?? responseData
      } else {
        return Promise.reject(new Error(responseData.message || '请求失败'))
      }
    }
    return responseData
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          return Promise.reject(new Error('没有权限访问该资源'))
        case 404:
          return Promise.reject(new Error('请求的资源不存在'))
        case 500:
          return Promise.reject(new Error('服务器内部错误'))
        default:
          return Promise.reject(new Error(error.response.data?.message || '请求失败'))
      }
    } else if (error.request) {
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    } else {
      return Promise.reject(error)
    }
  }
)

/**
 * 使用Fetch API进行带认证的请求
 * @param url 请求URL
 * @param options Fetch选项
 * @returns 解析后的响应数据
 */
export async function fetchWithAuth(url: string, options: RequestInit = {}): Promise<any> {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  };

  try {
    const response = await fetch(url, {
      ...options,
      headers
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));

      // 处理特定的HTTP状态码
      switch (response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token');
          window.location.href = '/login';
          throw new Error('未授权，请重新登录');
        case 403:
          throw new Error('没有权限访问该资源');
        case 404:
          throw new Error('请求的资源不存在');
        case 500:
          throw new Error('服务器内部错误');
        default:
          const message = errorData.message || errorData.error || `HTTP error ${response.status}`;
          throw new Error(message || '请求失败');
      }
    }

    const data = await response.json();

    // 处理后端统一响应格式 { code, message, data }
    if (data && typeof data === 'object' && (data.code === 0 || data.code === 200)) {
      return data.data ?? data;
    } else if (data && typeof data === 'object') {
      throw new Error(data.message || '请求失败');
    }

    return data;
  } catch (error) {
    console.error('API请求失败:', error);
    throw error;
  }
}

export default api
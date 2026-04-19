/**
 * 开发环境（VUE_APP_USE_PAGE_HOST_API=true）：API 与当前页面同主机、8000 端口，
 * 这样用 localhost、127.0.0.1 或局域网 IP 打开前端，请求都会打到同一台机器的后端。
 * 生产环境：使用构建时注入的 VUE_APP_SERVER。
 */
export function getApiBaseUrl() {
  const fromEnv = process.env.VUE_APP_SERVER || ''
  if (process.env.VUE_APP_USE_PAGE_HOST_API === 'true') {
    if (typeof window !== 'undefined' && window.location) {
      return `${window.location.protocol}//${window.location.hostname}:8000`
    }
  }
  return fromEnv
}

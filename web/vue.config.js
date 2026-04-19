const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    // 允许局域网内其它机器用本机 IP 访问 dev server
    host: '0.0.0.0',
  },
})

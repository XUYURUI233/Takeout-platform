// 测试环境配置
// 请根据您的实际环境选择合适的baseUrl

// 选项1: 使用本地开发环境（后端服务已启动）
export const baseUrl = 'http://localhost:8072'

// 选项2: 使用cpolar内网穿透地址（不指定端口，需要确保默认映射）
// export const baseUrl = 'http://2ae29966.r10.cpolar.top'

// 选项3: 使用itheima测试环境（如果本地服务无法启动）
// export const baseUrl = 'http://reggie-dev.itheima.net'

// 选项4: 使用本地开发环境（如果后端服务在本地运行）
// export const baseUrl = 'http://localhost:8072'

// 选项5: 使用本地IP地址（如果需要在真机上测试）
// export const baseUrl = 'http://192.168.1.100:8072'

// 选项6: 使用itheima线上环境
// export const baseUrl = 'https://registakeaway.itheima.net'

// 选项7: 使用备用测试环境
// export const baseUrl = 'https://reggie-parent-t.itheima.net'

// 注意：
// 1. 如果使用https协议，请确保域名已配置SSL证书
// 2. 如果使用http协议，在微信小程序中需要在开发工具中勾选"不校验合法域名"
// 3. 后端服务默认运行在8072端口
// 4. 如果使用内网穿透，请确保正确映射到8072端口
// 模拟数据，用于后端服务不可用时的应急方案

// 店铺状态模拟数据
export const mockShopStatus = {
    code: 200,
    msg: "success",
    data: 1  // 1表示营业中，0表示打烊中
}

// 用户登录模拟数据
export const mockUserLogin = {
    code: 200,
    msg: "success",
    data: {
        id: 1,
        openid: "mock_openid_123",
        token: "mock_token_123456"
    }
}

// 菜品分类模拟数据
export const mockCategories = {
    code: 200,
    msg: "success",
    data: [
        {
            id: 1,
            type: 1,
            name: "热销",
            sort: 1,
            status: 1
        },
        {
            id: 2,
            type: 1,
            name: "凉菜",
            sort: 2,
            status: 1
        },
        {
            id: 3,
            type: 1,
            name: "主食",
            sort: 3,
            status: 1
        }
    ]
}

// 菜品列表模拟数据
export const mockDishes = {
    code: 200,
    msg: "success",
    data: {
        records: [
            {
                id: 1,
                name: "宫保鸡丁",
                categoryId: 1,
                price: 2800,
                image: "/static/dish1.png",
                description: "经典川菜，口感麻辣鲜香",
                status: 1
            },
            {
                id: 2,
                name: "麻婆豆腐",
                categoryId: 1,
                price: 1800,
                image: "/static/dish2.png",
                description: "川菜代表，麻辣鲜香",
                status: 1
            }
        ],
        total: 2,
        size: 10,
        current: 1
    }
}

// 购物车模拟数据
export const mockShoppingCart = {
    code: 200,
    msg: "success",
    data: []
}

// 订单模拟数据
export const mockOrders = {
    code: 200,
    msg: "success",
    data: {
        records: [],
        total: 0,
        size: 10,
        current: 1
    }
}

// 地址列表模拟数据
export const mockAddresses = {
    code: 200,
    msg: "success",
    data: [
        {
            id: 1,
            userId: 1,
            consignee: "张三",
            phone: "13800138000",
            sex: 1,
            provinceCode: "110000",
            provinceName: "北京市",
            cityCode: "110100",
            cityName: "北京市",
            districtCode: "110101",
            districtName: "东城区",
            detail: "王府井大街1号",
            label: "公司",
            isDefault: 1
        }
    ]
}

// ======================== 秒杀功能相关模拟数据 ========================

// 秒杀活动模拟数据
export const mockSeckillActivities = {
    code: 1,
    msg: "success",
    data: [
        {
            id: 1,
            name: "限时秒杀",
            description: "限时2小时秒杀，数量有限，先到先得！",
            image: "../../static/bg.png", // 使用相对路径
            startTime: (() => {
                const now = new Date()
                const startTime = new Date(now.getTime() - 1000 * 60 * 30) // 30分钟前开始
                return startTime.toISOString().slice(0, 19).replace('T', ' ')
            })(),
            endTime: (() => {
                const now = new Date()
                const endTime = new Date(now.getTime() + 1000 * 60 * 90) // 90分钟后结束
                return endTime.toISOString().slice(0, 19).replace('T', ' ')
            })(),
            status: 1,
            remainingTime: 5400 // 90分钟 = 5400秒
        }
    ]
}

// 秒杀商品列表模拟数据
export const mockSeckillGoods = {
    code: 1,
    msg: "success",
    data: [
        {
            id: 1,
            activityId: 1,
            goodsType: 1,
            goodsId: 46,
            goodsName: "王老吉",
            goodsImage: "https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png",
            originalPrice: 6.00,
            seckillPrice: 3.00,
            availableStock: 85,
            soldCount: 15,
            limitCount: 2,
            userPurchased: 0,
            canPurchase: true
        },
        {
            id: 2,
            activityId: 1,
            goodsType: 1,
            goodsId: 49,
            goodsName: "米饭",
            goodsImage: "https://sky-itcast.oss-cn-beijing.aliyuncs.com/76752350-2121-44d2-b477-10791c23a8ec.png",
            originalPrice: 2.00,
            seckillPrice: 1.00,
            availableStock: 180,
            soldCount: 20,
            limitCount: 3,
            userPurchased: 0,
            canPurchase: true
        }
    ]
}

// 秒杀商品详情模拟数据
export const mockSeckillGoodsDetail = {
    code: 1,
    msg: "success",
    data: {
        id: 1,
        activityId: 1,
        activityName: "限时秒杀",
        goodsType: 1,
        goodsId: 46,
        goodsName: "王老吉",
        goodsImage: "https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png",
        originalPrice: 6.00,
        seckillPrice: 3.00,
        availableStock: 85,
        soldCount: 15,
        limitCount: 2,
        userPurchased: 0,
        canPurchase: true,
        startTime: (() => {
            const now = new Date()
            const startTime = new Date(now.getTime() - 1000 * 60 * 30)
            return startTime.toISOString().slice(0, 19).replace('T', ' ')
        })(),
        endTime: (() => {
            const now = new Date()
            const endTime = new Date(now.getTime() + 1000 * 60 * 90)
            return endTime.toISOString().slice(0, 19).replace('T', ' ')
        })(),
        remainingTime: 5400,
        description: "还是小时候的味道"
    }
}
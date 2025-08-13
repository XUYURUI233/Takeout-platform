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

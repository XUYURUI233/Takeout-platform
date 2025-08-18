// ģ�����ݣ����ں�˷��񲻿���ʱ��Ӧ������

// ����״̬ģ������
export const mockShopStatus = {
    code: 200,
    msg: "success",
    data: 1  // 1��ʾӪҵ�У�0��ʾ������
}

// �û���¼ģ������
export const mockUserLogin = {
    code: 200,
    msg: "success",
    data: {
        id: 1,
        openid: "mock_openid_123",
        token: "mock_token_123456"
    }
}

// ��Ʒ����ģ������
export const mockCategories = {
    code: 200,
    msg: "success",
    data: [
        {
            id: 1,
            type: 1,
            name: "����",
            sort: 1,
            status: 1
        },
        {
            id: 2,
            type: 1,
            name: "����",
            sort: 2,
            status: 1
        },
        {
            id: 3,
            type: 1,
            name: "��ʳ",
            sort: 3,
            status: 1
        }
    ]
}

// ��Ʒ�б�ģ������
export const mockDishes = {
    code: 200,
    msg: "success",
    data: {
        records: [
            {
                id: 1,
                name: "��������",
                categoryId: 1,
                price: 2800,
                image: "/static/dish1.png",
                description: "���䴨�ˣ��ڸ���������",
                status: 1
            },
            {
                id: 2,
                name: "���Ŷ���",
                categoryId: 1,
                price: 1800,
                image: "/static/dish2.png",
                description: "���˴�����������",
                status: 1
            }
        ],
        total: 2,
        size: 10,
        current: 1
    }
}

// ���ﳵģ������
export const mockShoppingCart = {
    code: 200,
    msg: "success",
    data: []
}

// ����ģ������
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

// ��ַ�б�ģ������
export const mockAddresses = {
    code: 200,
    msg: "success",
    data: [
        {
            id: 1,
            userId: 1,
            consignee: "����",
            phone: "13800138000",
            sex: 1,
            provinceCode: "110000",
            provinceName: "������",
            cityCode: "110100",
            cityName: "������",
            districtCode: "110101",
            districtName: "������",
            detail: "���������1��",
            label: "��˾",
            isDefault: 1
        }
    ]
}

// ======================== ��ɱ�������ģ������ ========================

// ��ɱ�ģ������
export const mockSeckillActivities = {
    code: 1,
    msg: "success",
    data: [
        {
            id: 1,
            name: "��ʱ��ɱ",
            description: "��ʱ2Сʱ��ɱ���������ޣ��ȵ��ȵã�",
            image: "../../static/bg.png", // ʹ�����·��
            startTime: (() => {
                const now = new Date()
                const startTime = new Date(now.getTime() - 1000 * 60 * 30) // 30����ǰ��ʼ
                return startTime.toISOString().slice(0, 19).replace('T', ' ')
            })(),
            endTime: (() => {
                const now = new Date()
                const endTime = new Date(now.getTime() + 1000 * 60 * 90) // 90���Ӻ����
                return endTime.toISOString().slice(0, 19).replace('T', ' ')
            })(),
            status: 1,
            remainingTime: 5400 // 90���� = 5400��
        }
    ]
}

// ��ɱ��Ʒ�б�ģ������
export const mockSeckillGoods = {
    code: 1,
    msg: "success",
    data: [
        {
            id: 1,
            activityId: 1,
            goodsType: 1,
            goodsId: 46,
            goodsName: "���ϼ�",
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
            goodsName: "�׷�",
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

// ��ɱ��Ʒ����ģ������
export const mockSeckillGoodsDetail = {
    code: 1,
    msg: "success",
    data: {
        id: 1,
        activityId: 1,
        activityName: "��ʱ��ɱ",
        goodsType: 1,
        goodsId: 46,
        goodsName: "���ϼ�",
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
        description: "����Сʱ���ζ��"
    }
}
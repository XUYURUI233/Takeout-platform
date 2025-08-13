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

@echo off
echo ========================================
echo ����Sky����API�ӿ�
echo ========================================

echo 1. ���Ե���״̬�ӿ�...
curl -X GET "http://localhost:8072/user/shop/status"
echo.
echo.

echo 2. ���Բ�Ʒ����ӿ�...
curl -X GET "http://localhost:8072/user/category/list"
echo.
echo.

echo 3. ���Բ�Ʒ�б�ӿ�...
curl -X GET "http://localhost:8072/user/dish/list"
echo.
echo.

echo 4. ���Թ��ﳵ�б�ӿ�...
curl -X GET "http://localhost:8072/user/shoppingCart/list"
echo.
echo.

echo 5. ���Ե�ַ�б�ӿ�...
curl -X GET "http://localhost:8072/user/addressBook/list"
echo.
echo.

echo 6. ���Զ�����ʷ�ӿ�...
curl -X GET "http://localhost:8072/user/order/historyOrders?page=1&pageSize=10"
echo.
echo.

echo ========================================
echo API�������
echo ========================================
pause


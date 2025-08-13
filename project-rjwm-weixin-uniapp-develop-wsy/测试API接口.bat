@echo off
echo ========================================
echo 测试Sky外卖API接口
echo ========================================

echo 1. 测试店铺状态接口...
curl -X GET "http://localhost:8072/user/shop/status"
echo.
echo.

echo 2. 测试菜品分类接口...
curl -X GET "http://localhost:8072/user/category/list"
echo.
echo.

echo 3. 测试菜品列表接口...
curl -X GET "http://localhost:8072/user/dish/list"
echo.
echo.

echo 4. 测试购物车列表接口...
curl -X GET "http://localhost:8072/user/shoppingCart/list"
echo.
echo.

echo 5. 测试地址列表接口...
curl -X GET "http://localhost:8072/user/addressBook/list"
echo.
echo.

echo 6. 测试订单历史接口...
curl -X GET "http://localhost:8072/user/order/historyOrders?page=1&pageSize=10"
echo.
echo.

echo ========================================
echo API测试完成
echo ========================================
pause


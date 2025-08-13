@echo off
echo ========================================
echo 检查Sky外卖后端服务状态
echo ========================================

echo 检查端口8072是否被占用...
netstat -ano | findstr :8072
if %errorlevel% equ 0 (
    echo ? 后端服务正在运行 (端口8072)
) else (
    echo ? 后端服务未运行
    echo 请运行 启动后端服务.bat 启动服务
    pause
    exit /b 1
)

echo.
echo 测试API接口...
curl -X GET "http://localhost:8072/user/shop/status"
if %errorlevel% equ 0 (
    echo ? API接口正常
) else (
    echo ? API接口异常
)

echo.
echo 检查数据库连接...
curl -X GET "http://localhost:8072/user/category/list"
if %errorlevel% equ 0 (
    echo ? 数据库连接正常
) else (
    echo ? 数据库连接异常
)

echo.
echo ========================================
echo 检查完成
echo ========================================
pause

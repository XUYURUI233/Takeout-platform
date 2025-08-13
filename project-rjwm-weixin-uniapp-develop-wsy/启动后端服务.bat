@echo off
echo ========================================
echo 启动Sky外卖后端服务
echo ========================================

cd /d "%~dp0..\sky-take-out\sky-server"

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK
    pause
    exit /b 1
)

echo 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven
    pause
    exit /b 1
)

echo 启动后端服务...
echo 服务将在 http://localhost:8072 启动
echo 按 Ctrl+C 停止服务
echo.

mvn spring-boot:run

pause

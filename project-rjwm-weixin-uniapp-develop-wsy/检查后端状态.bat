@echo off
echo ========================================
echo ���Sky������˷���״̬
echo ========================================

echo ���˿�8072�Ƿ�ռ��...
netstat -ano | findstr :8072
if %errorlevel% equ 0 (
    echo ? ��˷����������� (�˿�8072)
) else (
    echo ? ��˷���δ����
    echo ������ ������˷���.bat ��������
    pause
    exit /b 1
)

echo.
echo ����API�ӿ�...
curl -X GET "http://localhost:8072/user/shop/status"
if %errorlevel% equ 0 (
    echo ? API�ӿ�����
) else (
    echo ? API�ӿ��쳣
)

echo.
echo ������ݿ�����...
curl -X GET "http://localhost:8072/user/category/list"
if %errorlevel% equ 0 (
    echo ? ���ݿ���������
) else (
    echo ? ���ݿ������쳣
)

echo.
echo ========================================
echo ������
echo ========================================
pause

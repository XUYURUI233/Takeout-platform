@echo off
echo ========================================
echo ����Sky������˷���
echo ========================================

cd /d "%~dp0..\sky-take-out\sky-server"

echo ���Java����...
java -version
if %errorlevel% neq 0 (
    echo ����: δ�ҵ�Java���������Ȱ�װJDK
    pause
    exit /b 1
)

echo ���Maven����...
mvn -version
if %errorlevel% neq 0 (
    echo ����: δ�ҵ�Maven���������Ȱ�װMaven
    pause
    exit /b 1
)

echo ������˷���...
echo ������ http://localhost:8072 ����
echo �� Ctrl+C ֹͣ����
echo.

mvn spring-boot:run

pause

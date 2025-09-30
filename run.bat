@echo off
REM ===================================================================
REM Quick Run Script for QuanLyDiemTN (Windows)
REM ===================================================================
REM This script provides convenient ways to run the application
REM ===================================================================

setlocal enabledelayedexpansion

REM Colors for output (limited in Windows)
set GREEN=[92m
set BLUE=[94m
set PURPLE=[95m
set CYAN=[96m
set NC=[0m

REM Functions
:print_info
echo %PURPLE%ℹ️  %~1%NC%
goto :eof

:print_step
echo %BLUE%📋 %~1%NC%
goto :eof

:print_success
echo %GREEN%✅ %~1%NC%
goto :eof

REM Check if project is built
:check_build
if not exist "target\classes" (
    call :print_step "Building project..."
    call mvn clean compile
    if %errorlevel% neq 0 (
        echo Build failed!
        pause
        exit /b 1
    )
)
goto :eof

REM Run with Maven
:run_with_maven
call :print_step "Running application with Maven..."
call mvn exec:java -Dexec.mainClass="QuanLyDiemTN" -q
goto :eof

REM Run with JAR
:run_with_jar
call :print_step "Building JAR file..."
call mvn package -DskipTests -q

if exist "target\QuanLyDiemTN-1.0-SNAPSHOT.jar" (
    call :print_step "Running application from JAR..."
    java -jar target\QuanLyDiemTN-1.0-SNAPSHOT.jar
) else (
    echo JAR file not found!
    pause
    exit /b 1
)
goto :eof

REM Main function
:main
echo.
echo %CYAN%################################################################%NC%
echo %CYAN%#                🎓 QuanLyDiemTN Runner                    #%NC%
echo %CYAN%################################################################%NC%
echo.

if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help
if "%1"=="--jar" goto :jar_run
if "%1"=="--build" goto :build_only
if "%1"=="--maven" goto :maven_run

REM Default to maven
:maven_run
call :check_build
call :run_with_maven
goto :end

:jar_run
call :check_build
call :run_with_jar
goto :end

:build_only
call :print_step "Building project..."
call mvn clean package -DskipTests
call :print_success "Build completed!"
goto :end

:show_help
echo Usage: %0 [option]
echo.
echo Options:
echo   --help, -h    Show this help message
echo   --maven       Run with Maven (default)
echo   --jar         Build and run JAR file
echo   --build       Build project only
echo.
goto :end

:end
pause
exit /b 0
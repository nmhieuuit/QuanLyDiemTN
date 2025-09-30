@echo off
REM ===================================================================
REM Database Setup Script for QuanLyDiemTN (Windows)
REM ===================================================================
REM This script sets up the SQL Server database for the project
REM Supports both local SQL Server and Docker installations
REM ===================================================================

setlocal enabledelayedexpansion

REM Configuration
set SQL_PASSWORD=Admin@123
set SQL_USERNAME=SA
set SQL_DATABASE=QuanLyDiemTN
set SQL_PORT=1433
set DOCKER_CONTAINER=sqlserver

REM Colors for output (limited in Windows)
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set BLUE=[94m
set PURPLE=[95m
set CYAN=[96m
set NC=[0m

REM Functions
:print_step
echo %BLUE%📋 %~1%NC%
goto :eof

:print_success
echo %GREEN%✅ %~1%NC%
goto :eof

:print_warning
echo %YELLOW%⚠️  %~1%NC%
goto :eof

:print_error
echo %RED%❌ %~1%NC%
goto :eof

:print_info
echo %PURPLE%ℹ️  %~1%NC%
goto :eof

REM Check if command exists
:command_exists
where %1 >nul 2>&1
if %errorlevel% equ 0 (
    exit /b 0
) else (
    exit /b 1
)

REM Test SQL Server connection
:test_connection
set method=%1

if "%method%"=="docker" (
    docker exec %DOCKER_CONTAINER% /opt/mssql-tools/bin/sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% -Q "SELECT 1" >nul 2>&1
    exit /b %errorlevel%
) else if "%method%"=="local" (
    call :command_exists sqlcmd
    if %errorlevel% neq 0 (
        exit /b 1
    )
    sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% -Q "SELECT 1" >nul 2>&1
    exit /b %errorlevel%
) else (
    exit /b 1
)

REM Setup SQL Server via Docker
:setup_docker_sql
call :print_step "Setting up SQL Server via Docker..."

call :command_exists docker
if %errorlevel% neq 0 (
    call :print_error "Docker not found. Please install Docker first."
    exit /b 1
)

REM Check if container exists
docker ps -a | findstr %DOCKER_CONTAINER% >nul 2>&1
if %errorlevel% equ 0 (
    call :print_info "SQL Server container already exists"
    
    REM Start if not running
    docker ps | findstr %DOCKER_CONTAINER% >nul 2>&1
    if %errorlevel% neq 0 (
        call :print_info "Starting SQL Server container..."
        docker start %DOCKER_CONTAINER%
    )
) else (
    call :print_info "Creating new SQL Server container..."
    docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=%SQL_PASSWORD%" -p %SQL_PORT%:1433 --name %DOCKER_CONTAINER% -d mcr.microsoft.com/mssql/server:2019-latest
)

REM Wait for SQL Server to start
call :print_info "Waiting for SQL Server to start..."
timeout /t 10 >nul

call :test_connection docker
if %errorlevel% equ 0 (
    call :print_success "SQL Server container is running"
    exit /b 0
) else (
    call :print_error "SQL Server container failed to start properly"
    exit /b 1
)

REM Execute SQL script
:execute_sql
set method=%1
set sql_file=%2

if not exist "%sql_file%" (
    call :print_error "SQL file not found: %sql_file%"
    exit /b 1
)

if "%method%"=="docker" (
    docker exec -i %DOCKER_CONTAINER% /opt/mssql-tools/bin/sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% < "%sql_file%"
) else if "%method%"=="local" (
    sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% -i "%sql_file%"
) else (
    call :print_error "Unknown method: %method%"
    exit /b 1
)
goto :eof

REM Create database and schema
:create_database
set method=%1

call :print_step "Creating database and schema..."

if exist src\main\resources\database\schema.sql (
    call :execute_sql "%method%" "src\main\resources\database\schema.sql"
    if %errorlevel% equ 0 (
        call :print_success "Database schema created successfully"
    ) else (
        call :print_error "Failed to create database schema"
        exit /b 1
    )
) else (
    call :print_error "schema.sql file not found in src\main\resources\database\"
    exit /b 1
)
goto :eof

REM Insert sample data
:insert_sample_data
set method=%1

call :print_step "Inserting sample data..."

REM Create sample data SQL
(
echo USE QuanLyDiemTN;
echo GO
echo.
echo -- Insert sample students
echo INSERT INTO DM_HocSinh ^(HoTen, NgaySinh, GioiTinh, DiaChi^) VALUES 
echo ^(N'Nguyễn Văn An', '2005-01-15', 1, N'123 Đường ABC, TP.HCM'^),
echo ^(N'Trần Thị Bình', '2005-03-22', 0, N'456 Đường XYZ, Hà Nội'^),
echo ^(N'Lê Văn Cường', '2005-02-10', 1, N'789 Đường DEF, Đà Nẵng'^),
echo ^(N'Phạm Thị Dung', '2005-04-05', 0, N'321 Đường GHI, Cần Thơ'^),
echo ^(N'Hoàng Văn Em', '2005-05-20', 1, N'654 Đường JKL, Hải Phòng'^);
echo.
echo -- Insert sample classes
echo INSERT INTO DM_Lop ^(TenLop, NamHoc, KhoiID^) VALUES 
echo ^(N'12A1', 2023, 3^),
echo ^(N'12A2', 2023, 3^),
echo ^(N'11A1', 2023, 2^),
echo ^(N'10A1', 2023, 1^);
echo.
echo -- Assign students to classes
echo INSERT INTO Lop_HocSinh ^(HS_ID, LOP_ID, NgayVaoLop, TrangThai^) VALUES 
echo ^(1, 1, '2023-09-01', 1^),
echo ^(2, 1, '2023-09-01', 1^),
echo ^(3, 2, '2023-09-01', 1^),
echo ^(4, 2, '2023-09-01', 1^),
echo ^(5, 3, '2023-09-01', 1^);
echo.
echo -- Insert sample semester grades
echo INSERT INTO HocSinh_DiemHocKy ^(HS_ID, HK1_10, HK2_10, HK1_11, HK2_11, HK1_12, HK2_12^) VALUES 
echo ^(1, 8.5, 8.8, 8.2, 8.6, 8.9, 9.0^),
echo ^(2, 7.8, 8.1, 7.9, 8.3, 8.1, 8.4^),
echo ^(3, 9.0, 9.2, 8.8, 9.1, 9.3, 9.5^),
echo ^(4, 6.5, 7.0, 7.2, 7.5, 7.8, 8.0^),
echo ^(5, 8.0, 8.3, 8.1, 8.4, 8.2, 8.5^);
echo.
echo -- Insert sample exam scores
echo INSERT INTO HocSinh_DiemThi ^(HS_ID, DiemToan, DiemVan, TuChon1MH, TuChon1Diem, TuChon2MH, TuChon2Diem, DiemUT^) VALUES 
echo ^(1, 8.5, 8.0, 5, 8.2, 6, 8.8, 0.5^),
echo ^(2, 7.5, 8.5, 4, 7.8, 7, 8.0, 0.0^),
echo ^(3, 9.0, 8.8, 5, 9.2, 6, 9.0, 1.0^),
echo ^(4, 6.8, 7.2, 8, 7.0, 9, 6.5, 0.0^),
echo ^(5, 8.2, 7.8, 5, 8.0, 11, 7.5, 0.25^);
echo.
echo PRINT 'Sample data inserted successfully';
) > sample_data.sql

call :execute_sql "%method%" "sample_data.sql"
if %errorlevel% equ 0 (
    call :print_success "Sample data inserted successfully"
    del sample_data.sql
) else (
    call :print_warning "Failed to insert sample data (this is optional)"
    del sample_data.sql
)
goto :eof

REM Verify database setup
:verify_setup
set method=%1

call :print_step "Verifying database setup..."

REM Create verification SQL
(
echo USE QuanLyDiemTN;
echo GO
echo.
echo SELECT 'Tables created:' AS Status;
echo SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';
echo.
echo SELECT 'Sample students:' AS Status;
echo SELECT COUNT^(*^) AS StudentCount FROM DM_HocSinh;
echo.
echo SELECT 'Sample classes:' AS Status;  
echo SELECT COUNT^(*^) AS ClassCount FROM DM_Lop;
echo.
echo SELECT 'Database setup verification completed' AS Status;
) > verify.sql

call :execute_sql "%method%" "verify.sql"
if %errorlevel% equ 0 (
    call :print_success "Database verification completed"
    del verify.sql
) else (
    call :print_warning "Database verification failed"
    del verify.sql
)
goto :eof

REM Main setup function
:main
echo.
echo %CYAN%################################################################%NC%
echo %CYAN%#            🗄️  Database Setup for QuanLyDiemTN           #%NC%
echo %CYAN%################################################################%NC%
echo.

REM Detect setup method
set method=

REM Check if Docker is available and preferred
call :command_exists docker
if %errorlevel% equ 0 (
    docker ps >nul 2>&1
    if %errorlevel% equ 0 (
        call :print_info "Docker detected and running"
        set method=docker
    ) else (
        call :print_warning "Docker detected but not running"
    )
)

REM Check for local SQL Server
if "%method%"=="" (
    call :command_exists sqlcmd
    if %errorlevel% equ 0 (
        call :test_connection local
        if %errorlevel% equ 0 (
            call :print_info "Local SQL Server detected"
            set method=local
        )
    )
)

REM If no method detected, try to setup via Docker
if "%method%"=="" (
    call :print_info "No SQL Server detected. Setting up via Docker..."
    call :setup_docker_sql
    if %errorlevel% equ 0 (
        set method=docker
    ) else (
        call :print_error "Failed to setup SQL Server"
        pause
        exit /b 1
    )
)

call :print_info "Using method: %method%"

REM Create database and schema
call :create_database "%method%"
if %errorlevel% neq 0 (
    pause
    exit /b 1
)

REM Ask user if they want sample data
echo.
set /p "choice=Do you want to insert sample data? (y/N): "
if /i "%choice%"=="y" (
    call :insert_sample_data "%method%"
)

REM Verify setup
call :verify_setup "%method%"

echo.
call :print_success "Database setup completed!"
call :print_info "Connection details:"
echo   Server: localhost:%SQL_PORT%
echo   Username: %SQL_USERNAME%
echo   Password: %SQL_PASSWORD%
echo   Database: %SQL_DATABASE%

if "%method%"=="docker" (
    echo.
    call :print_info "Docker commands:"
    echo   Start SQL Server: docker start %DOCKER_CONTAINER%
    echo   Stop SQL Server: docker stop %DOCKER_CONTAINER%
    echo   Connect to SQL Server: docker exec -it %DOCKER_CONTAINER% /opt/mssql-tools/bin/sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD%
)
goto :eof

REM Parse command line arguments
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help
if "%1"=="--docker" goto :docker_setup
if "%1"=="--local" goto :local_setup
if "%1"=="--verify-only" goto :verify_only

call :main
goto :end

:show_help
echo Usage: %0 [options]
echo.
echo Options:
echo   --help, -h     Show this help message
echo   --docker       Force Docker setup
echo   --local        Force local SQL Server setup
echo   --verify-only  Only verify existing setup
echo.
goto :end

:docker_setup
call :setup_docker_sql
call :create_database docker
call :verify_setup docker
goto :end

:local_setup
call :test_connection local
if %errorlevel% equ 0 (
    call :create_database local
    call :verify_setup local
) else (
    call :print_error "Local SQL Server not accessible"
    pause
    exit /b 1
)
goto :end

:verify_only
call :test_connection docker
if %errorlevel% equ 0 (
    call :verify_setup docker
) else (
    call :test_connection local
    if %errorlevel% equ 0 (
        call :verify_setup local
    ) else (
        call :print_error "No accessible SQL Server found"
        pause
        exit /b 1
    )
)
goto :end

:end
pause
exit /b 0
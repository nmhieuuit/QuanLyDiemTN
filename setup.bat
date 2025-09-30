@echo off
REM ===================================================================
REM QuanLyDiemTN Setup Script for Windows
REM ===================================================================
REM This script automates the setup process for the QuanLyDiemTN project
REM Author: Your Name
REM Version: 1.0
REM ===================================================================

setlocal enabledelayedexpansion

REM Configuration
set PROJECT_NAME=QuanLyDiemTN
set REQUIRED_JAVA_VERSION=11
set SQL_PASSWORD=Admin@123
set SQL_USERNAME=SA
set SQL_DATABASE=QuanLyDiemTN
set SQL_PORT=1433

REM Colors for output (limited in Windows)
set GREEN=[92m
set RED=[91m
set YELLOW=[93m
set BLUE=[94m
set PURPLE=[95m
set CYAN=[96m
set NC=[0m

REM Functions
:print_header
echo.
echo %CYAN%################################################################%NC%
echo %CYAN%#               🎓 QuanLyDiemTN Setup Script               #%NC%
echo %CYAN%#                                                              #%NC%
echo %CYAN%#  This script will setup your development environment        #%NC%
echo %CYAN%#  for the QuanLyDiemTN graduation management system          #%NC%
echo %CYAN%################################################################%NC%
echo.
goto :eof

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

REM Check Java version
:check_java
call :print_step "Checking Java installation..."

call :command_exists java
if %errorlevel% neq 0 (
    call :print_warning "Java not found"
    exit /b 1
)

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION_STRING=%%g
)

REM Extract version number (remove quotes and get major version)
set JAVA_VERSION_STRING=!JAVA_VERSION_STRING:"=!
for /f "delims=." %%a in ("!JAVA_VERSION_STRING!") do set JAVA_MAJOR=%%a

if !JAVA_MAJOR! geq %REQUIRED_JAVA_VERSION% (
    call :print_success "Java !JAVA_MAJOR! found"
    exit /b 0
) else (
    call :print_warning "Java version !JAVA_MAJOR! is too old. Required: %REQUIRED_JAVA_VERSION%+"
    exit /b 1
)

REM Install Java
:install_java
call :print_step "Installing Java..."

call :print_info "Please install Java manually:"
call :print_info "1. Go to https://adoptium.net/"
call :print_info "2. Download and install OpenJDK 17"
call :print_info "3. Add JAVA_HOME to environment variables"
call :print_info "4. Add %%JAVA_HOME%%\bin to PATH"
pause
goto :eof

REM Check Maven
:check_maven
call :print_step "Checking Maven installation..."

call :command_exists mvn
if %errorlevel% equ 0 (
    for /f "tokens=3" %%g in ('mvn -version 2^>^&1 ^| findstr /i "Apache Maven"') do (
        call :print_success "Maven %%g found"
    )
    exit /b 0
) else (
    call :print_warning "Maven not found"
    exit /b 1
)

REM Install Maven
:install_maven
call :print_step "Installing Maven..."

call :print_info "Please install Maven manually:"
call :print_info "1. Go to https://maven.apache.org/download.cgi"
call :print_info "2. Download Maven binary zip"
call :print_info "3. Extract to C:\Program Files\Apache\maven"
call :print_info "4. Add MAVEN_HOME environment variable"
call :print_info "5. Add %%MAVEN_HOME%%\bin to PATH"
pause
goto :eof

REM Check Docker
:check_docker
call :print_step "Checking Docker installation..."

call :command_exists docker
if %errorlevel% neq 0 (
    call :print_warning "Docker not found"
    exit /b 1
)

docker ps >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "Docker found and running"
    exit /b 0
) else (
    call :print_warning "Docker found but not running"
    exit /b 1
)

REM Install Docker
:install_docker
call :print_step "Installing Docker..."

call :print_info "Please install Docker Desktop manually:"
call :print_info "1. Go to https://docs.docker.com/desktop/windows/install/"
call :print_info "2. Download and install Docker Desktop"
call :print_info "3. Start Docker Desktop"
pause
goto :eof

REM Setup SQL Server
:setup_sql_server
call :print_step "Setting up SQL Server..."

REM Check if SQL Server container already exists
docker ps -a | findstr sqlserver >nul 2>&1
if %errorlevel% equ 0 (
    call :print_info "SQL Server container already exists"
    docker ps | findstr sqlserver >nul 2>&1
    if %errorlevel% neq 0 (
        call :print_info "Starting existing SQL Server container..."
        docker start sqlserver
    )
) else (
    call :print_info "Creating new SQL Server container..."
    docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=%SQL_PASSWORD%" -p %SQL_PORT%:1433 --name sqlserver -d mcr.microsoft.com/mssql/server:2019-latest
    
    REM Wait for SQL Server to start
    call :print_info "Waiting for SQL Server to start..."
    timeout /t 10 >nul
)

REM Test connection
docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% -Q "SELECT 1" >nul 2>&1
if %errorlevel% equ 0 (
    call :print_success "SQL Server is running and accessible"
) else (
    call :print_error "SQL Server is not responding"
    exit /b 1
)
goto :eof

REM Setup database schema
:setup_database
call :print_step "Setting up database schema..."

if exist src\main\resources\database\schema.sql (
    call :print_info "Creating database and tables..."
    docker exec -i sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U %SQL_USERNAME% -P %SQL_PASSWORD% < src\main\resources\database\schema.sql
    call :print_success "Database schema created successfully"
) else (
    call :print_error "schema.sql file not found in src\main\resources\database\"
    exit /b 1
)
goto :eof

REM Build project
:build_project
call :print_step "Building project..."

REM Clean and compile
call mvn clean compile
if %errorlevel% neq 0 (
    call :print_error "Project compilation failed"
    exit /b 1
)
call :print_success "Project compiled successfully"

REM Run tests if they exist
if exist src\test (
    call :print_info "Running tests..."
    call mvn test
    if %errorlevel% neq 0 (
        call :print_warning "Some tests failed, but continuing..."
    )
)

REM Package
call mvn package -DskipTests
if %errorlevel% equ 0 (
    call :print_success "Project packaged successfully"
) else (
    call :print_error "Project packaging failed"
    exit /b 1
)
goto :eof

REM Test application
:test_application
call :print_step "Testing application..."

REM Test database connection
mvn test-compile exec:java -Dexec.mainClass="QuanLyDiemTN" -Dexec.args="test" -q >nul 2>&1
if %errorlevel% neq 0 (
    call :print_warning "Database connection test failed, but application may still work"
)
goto :eof

REM Main setup function
:main_setup
call :print_header

call :print_info "Starting setup process..."
echo.

REM Check and install Java
call :check_java
if %errorlevel% neq 0 (
    call :install_java
    call :check_java
    if %errorlevel% neq 0 (
        call :print_error "Java installation failed"
        pause
        exit /b 1
    )
)

REM Check and install Maven
call :check_maven
if %errorlevel% neq 0 (
    call :install_maven
    call :check_maven
    if %errorlevel% neq 0 (
        call :print_error "Maven installation failed"
        pause
        exit /b 1
    )
)

REM Check and install Docker
call :check_docker
if %errorlevel% neq 0 (
    call :install_docker
    call :check_docker
    if %errorlevel% neq 0 (
        call :print_error "Docker installation failed"
        pause
        exit /b 1
    )
)

REM Setup SQL Server
call :setup_sql_server
if %errorlevel% neq 0 (
    pause
    exit /b 1
)

REM Setup database
call :setup_database
if %errorlevel% neq 0 (
    pause
    exit /b 1
)

REM Build project
call :build_project
if %errorlevel% neq 0 (
    pause
    exit /b 1
)

REM Test application
call :test_application

call :print_success "Setup completed successfully!"
echo.
call :print_info "You can now run the application with:"
echo   mvn exec:java -Dexec.mainClass="QuanLyDiemTN"
echo   or
echo   java -jar target\QuanLyDiemTN-1.0-SNAPSHOT.jar
echo.
call :print_info "To stop SQL Server: docker stop sqlserver"
call :print_info "To start SQL Server: docker start sqlserver"
echo.
goto :eof

REM Parse command line arguments
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help
if "%1"=="--java-only" goto :java_only
if "%1"=="--maven-only" goto :maven_only
if "%1"=="--docker-only" goto :docker_only
if "%1"=="--db-only" goto :db_only
if "%1"=="--build-only" goto :build_only

call :main_setup
goto :end

:show_help
echo Usage: %0 [options]
echo.
echo Options:
echo   --help, -h     Show this help message
echo   --java-only    Install only Java
echo   --maven-only   Install only Maven
echo   --docker-only  Install only Docker
echo   --db-only      Setup only database
echo   --build-only   Build project only
echo.
goto :end

:java_only
call :print_header
call :check_java
if %errorlevel% neq 0 call :install_java
goto :end

:maven_only
call :print_header
call :check_maven
if %errorlevel% neq 0 call :install_maven
goto :end

:docker_only
call :print_header
call :check_docker
if %errorlevel% neq 0 call :install_docker
goto :end

:db_only
call :print_header
call :setup_sql_server
call :setup_database
goto :end

:build_only
call :print_header
call :build_project
goto :end

:end
pause
exit /b 0
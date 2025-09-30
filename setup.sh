#!/bin/bash

# ===================================================================
# QuanLyDiemTN Setup Script for macOS/Linux
# ===================================================================
# This script automates the setup process for the QuanLyDiemTN project
# Author: Your Name
# Version: 1.0
# ===================================================================

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="QuanLyDiemTN"
REQUIRED_JAVA_VERSION="11"
REQUIRED_MAVEN_VERSION="3.6"
SQL_PASSWORD="Admin@123"
SQL_USERNAME="SA"
SQL_DATABASE="QuanLyDiemTN"
SQL_PORT="1433"

# Functions
print_header() {
    echo -e "${CYAN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║               🎓 QuanLyDiemTN Setup Script               ║"
    echo "║                                                           ║"
    echo "║  This script will setup your development environment     ║"
    echo "║  for the QuanLyDiemTN graduation management system       ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_step() {
    echo -e "${BLUE}📋 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${PURPLE}ℹ️  $1${NC}"
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Get OS type
get_os() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macos"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "linux"
    else
        echo "unknown"
    fi
}

# Check Java version
check_java() {
    print_step "Checking Java installation..."
    
    if command_exists java; then
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge "$REQUIRED_JAVA_VERSION" ]; then
            print_success "Java $JAVA_VERSION found"
            return 0
        else
            print_warning "Java version $JAVA_VERSION is too old. Required: $REQUIRED_JAVA_VERSION+"
            return 1
        fi
    else
        print_warning "Java not found"
        return 1
    fi
}

# Install Java
install_java() {
    print_step "Installing Java..."
    OS=$(get_os)
    
    case $OS in
        "macos")
            if command_exists brew; then
                brew install openjdk@17
                echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
                export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
                print_success "Java 17 installed via Homebrew"
            else
                print_error "Homebrew not found. Please install Homebrew first or install Java manually."
                exit 1
            fi
            ;;
        "linux")
            if command_exists apt; then
                sudo apt update
                sudo apt install -y openjdk-17-jdk
                print_success "Java 17 installed via apt"
            elif command_exists yum; then
                sudo yum install -y java-17-openjdk-devel
                print_success "Java 17 installed via yum"
            elif command_exists dnf; then
                sudo dnf install -y java-17-openjdk-devel
                print_success "Java 17 installed via dnf"
            else
                print_error "Package manager not found. Please install Java manually."
                exit 1
            fi
            ;;
        *)
            print_error "Unsupported OS. Please install Java manually."
            exit 1
            ;;
    esac
}

# Check Maven
check_maven() {
    print_step "Checking Maven installation..."
    
    if command_exists mvn; then
        MAVEN_VERSION=$(mvn -version | head -n1 | cut -d' ' -f3)
        print_success "Maven $MAVEN_VERSION found"
        return 0
    else
        print_warning "Maven not found"
        return 1
    fi
}

# Install Maven
install_maven() {
    print_step "Installing Maven..."
    OS=$(get_os)
    
    case $OS in
        "macos")
            if command_exists brew; then
                brew install maven
                print_success "Maven installed via Homebrew"
            else
                print_error "Homebrew not found. Please install Homebrew first or install Maven manually."
                exit 1
            fi
            ;;
        "linux")
            if command_exists apt; then
                sudo apt update
                sudo apt install -y maven
                print_success "Maven installed via apt"
            elif command_exists yum; then
                sudo yum install -y maven
                print_success "Maven installed via yum"
            elif command_exists dnf; then
                sudo dnf install -y maven
                print_success "Maven installed via dnf"
            else
                print_error "Package manager not found. Please install Maven manually."
                exit 1
            fi
            ;;
        *)
            print_error "Unsupported OS. Please install Maven manually."
            exit 1
            ;;
    esac
}

# Check Docker
check_docker() {
    print_step "Checking Docker installation..."
    
    if command_exists docker; then
        if docker ps >/dev/null 2>&1; then
            print_success "Docker found and running"
            return 0
        else
            print_warning "Docker found but not running"
            return 1
        fi
    else
        print_warning "Docker not found"
        return 1
    fi
}

# Install Docker
install_docker() {
    print_step "Installing Docker..."
    OS=$(get_os)
    
    case $OS in
        "macos")
            print_info "Please install Docker Desktop for Mac from: https://docs.docker.com/desktop/mac/install/"
            read -p "Press Enter after installing Docker Desktop and starting it..."
            ;;
        "linux")
            # Install Docker using the official script
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            sudo usermod -aG docker $USER
            sudo systemctl start docker
            sudo systemctl enable docker
            rm get-docker.sh
            print_success "Docker installed"
            print_info "You may need to log out and back in for Docker permissions to take effect"
            ;;
        *)
            print_error "Unsupported OS. Please install Docker manually."
            exit 1
            ;;
    esac
}

# Setup SQL Server
setup_sql_server() {
    print_step "Setting up SQL Server..."
    
    # Check if SQL Server container already exists
    if docker ps -a | grep -q sqlserver; then
        print_info "SQL Server container already exists"
        if ! docker ps | grep -q sqlserver; then
            print_info "Starting existing SQL Server container..."
            docker start sqlserver
        fi
    else
        print_info "Creating new SQL Server container..."
        docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=$SQL_PASSWORD" \
            -p $SQL_PORT:1433 --name sqlserver \
            -d mcr.microsoft.com/mssql/server:2019-latest
        
        # Wait for SQL Server to start
        print_info "Waiting for SQL Server to start..."
        sleep 10
    fi
    
    # Test connection
    if docker exec sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD -Q "SELECT 1" >/dev/null 2>&1; then
        print_success "SQL Server is running and accessible"
    else
        print_error "SQL Server is not responding"
        exit 1
    fi
}

# Setup database schema
setup_database() {
    print_step "Setting up database schema..."
    
    if [ -f "src/main/resources/database/schema.sql" ]; then
        print_info "Creating database and tables..."
        docker exec -i sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD < src/main/resources/database/schema.sql
        print_success "Database schema created successfully"
    else
        print_error "schema.sql file not found in src/main/resources/database/"
        exit 1
    fi
}

# Build project
build_project() {
    print_step "Building project..."
    
    # Clean and compile
    mvn clean compile
    if [ $? -eq 0 ]; then
        print_success "Project compiled successfully"
    else
        print_error "Project compilation failed"
        exit 1
    fi
    
    # Run tests if they exist
    if [ -d "src/test" ]; then
        print_info "Running tests..."
        mvn test || print_warning "Some tests failed, but continuing..."
    fi
    
    # Package
    mvn package -DskipTests
    if [ $? -eq 0 ]; then
        print_success "Project packaged successfully"
    else
        print_error "Project packaging failed"
        exit 1
    fi
}

# Test application
test_application() {
    print_step "Testing application..."
    
    # Test database connection
    mvn test-compile exec:java -Dexec.mainClass="QuanLyDiemTN" -Dexec.args="test" -q || {
        print_warning "Database connection test failed, but application may still work"
    }
}

# Main setup function
main_setup() {
    print_header
    
    print_info "Starting setup process..."
    echo
    
    # Check and install Java
    if ! check_java; then
        install_java
        if ! check_java; then
            print_error "Java installation failed"
            exit 1
        fi
    fi
    
    # Check and install Maven
    if ! check_maven; then
        install_maven
        if ! check_maven; then
            print_error "Maven installation failed"
            exit 1
        fi
    fi
    
    # Check and install Docker
    if ! check_docker; then
        install_docker
        if ! check_docker; then
            print_error "Docker installation failed"
            exit 1
        fi
    fi
    
    # Setup SQL Server
    setup_sql_server
    
    # Setup database
    setup_database
    
    # Build project
    build_project
    
    # Test application
    test_application
    
    print_success "Setup completed successfully!"
    echo
    print_info "You can now run the application with:"
    echo -e "${CYAN}  mvn exec:java -Dexec.mainClass=\"QuanLyDiemTN\"${NC}"
    echo -e "${CYAN}  or${NC}"
    echo -e "${CYAN}  java -jar target/QuanLyDiemTN-1.0-SNAPSHOT.jar${NC}"
    echo
    print_info "To stop SQL Server: docker stop sqlserver"
    print_info "To start SQL Server: docker start sqlserver"
    echo
}

# Parse command line arguments
case "${1:-}" in
    --help|-h)
        echo "Usage: $0 [options]"
        echo
        echo "Options:"
        echo "  --help, -h     Show this help message"
        echo "  --java-only    Install only Java"
        echo "  --maven-only   Install only Maven"
        echo "  --docker-only  Install only Docker"
        echo "  --db-only      Setup only database"
        echo "  --build-only   Build project only"
        echo
        exit 0
        ;;
    --java-only)
        print_header
        check_java || install_java
        ;;
    --maven-only)
        print_header
        check_maven || install_maven
        ;;
    --docker-only)
        print_header
        check_docker || install_docker
        ;;
    --db-only)
        print_header
        setup_sql_server
        setup_database
        ;;
    --build-only)
        print_header
        build_project
        ;;
    *)
        main_setup
        ;;
esac

exit 0
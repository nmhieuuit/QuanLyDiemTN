#!/bin/bash

# ===================================================================
# Database Setup Script for QuanLyDiemTN
# ===================================================================
# This script sets up the SQL Server database for the project
# Supports both local SQL Server and Docker installations
# ===================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
SQL_PASSWORD="Admin@123"
SQL_USERNAME="SA"
SQL_DATABASE="QuanLyDiemTN"
SQL_PORT="1433"
DOCKER_CONTAINER="sqlserver"

# Functions
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

# Test SQL Server connection
test_connection() {
    local method=$1
    
    case $method in
        "docker")
            docker exec $DOCKER_CONTAINER /opt/mssql-tools/bin/sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD -Q "SELECT 1" >/dev/null 2>&1
            ;;
        "local")
            if command_exists sqlcmd; then
                sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD -Q "SELECT 1" >/dev/null 2>&1
            else
                return 1
            fi
            ;;
        *)
            return 1
            ;;
    esac
}

# Setup SQL Server via Docker
setup_docker_sql() {
    print_step "Setting up SQL Server via Docker..."
    
    if ! command_exists docker; then
        print_error "Docker not found. Please install Docker first."
        exit 1
    fi
    
    # Check if container exists
    if docker ps -a | grep -q $DOCKER_CONTAINER; then
        print_info "SQL Server container already exists"
        
        # Start if not running
        if ! docker ps | grep -q $DOCKER_CONTAINER; then
            print_info "Starting SQL Server container..."
            docker start $DOCKER_CONTAINER
        fi
    else
        print_info "Creating new SQL Server container..."
        docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=$SQL_PASSWORD" \
            -p $SQL_PORT:1433 --name $DOCKER_CONTAINER \
            -d mcr.microsoft.com/mssql/server:2019-latest
    fi
    
    # Wait for SQL Server to start
    print_info "Waiting for SQL Server to start..."
    for i in {1..30}; do
        if test_connection "docker"; then
            break
        fi
        sleep 1
    done
    
    if test_connection "docker"; then
        print_success "SQL Server container is running"
        return 0
    else
        print_error "SQL Server container failed to start properly"
        return 1
    fi
}

# Execute SQL script
execute_sql() {
    local method=$1
    local sql_file=$2
    
    if [ ! -f "$sql_file" ]; then
        print_error "SQL file not found: $sql_file"
        return 1
    fi
    
    case $method in
        "docker")
            docker exec -i $DOCKER_CONTAINER /opt/mssql-tools/bin/sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD < "$sql_file"
            ;;
        "local")
            sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD -i "$sql_file"
            ;;
        *)
            print_error "Unknown method: $method"
            return 1
            ;;
    esac
}

# Create database and schema
create_database() {
    local method=$1
    
    print_step "Creating database and schema..."
    
    if [ -f "src/main/resources/database/schema.sql" ]; then
        execute_sql "$method" "src/main/resources/database/schema.sql"
        if [ $? -eq 0 ]; then
            print_success "Database schema created successfully"
        else
            print_error "Failed to create database schema"
            return 1
        fi
    else
        print_error "schema.sql file not found in src/main/resources/database/"
        return 1
    fi
}

# Insert sample data
insert_sample_data() {
    local method=$1
    
    print_step "Inserting sample data..."
    
    # Create sample data SQL
    cat > sample_data.sql << 'EOF'
USE QuanLyDiemTN;
GO

-- Insert sample students
INSERT INTO DM_HocSinh (HoTen, NgaySinh, GioiTinh, DiaChi) VALUES 
(N'Nguyễn Văn An', '2005-01-15', 1, N'123 Đường ABC, TP.HCM'),
(N'Trần Thị Bình', '2005-03-22', 0, N'456 Đường XYZ, Hà Nội'),
(N'Lê Văn Cường', '2005-02-10', 1, N'789 Đường DEF, Đà Nẵng'),
(N'Phạm Thị Dung', '2005-04-05', 0, N'321 Đường GHI, Cần Thơ'),
(N'Hoàng Văn Em', '2005-05-20', 1, N'654 Đường JKL, Hải Phòng');

-- Insert sample classes
INSERT INTO DM_Lop (TenLop, NamHoc, KhoiID) VALUES 
(N'12A1', 2023, 3),
(N'12A2', 2023, 3),
(N'11A1', 2023, 2),
(N'10A1', 2023, 1);

-- Assign students to classes
INSERT INTO Lop_HocSinh (HS_ID, LOP_ID, NgayVaoLop, TrangThai) VALUES 
(1, 1, '2023-09-01', 1),
(2, 1, '2023-09-01', 1),
(3, 2, '2023-09-01', 1),
(4, 2, '2023-09-01', 1),
(5, 3, '2023-09-01', 1);

-- Insert sample semester grades
INSERT INTO HocSinh_DiemHocKy (HS_ID, HK1_10, HK2_10, HK1_11, HK2_11, HK1_12, HK2_12) VALUES 
(1, 8.5, 8.8, 8.2, 8.6, 8.9, 9.0),
(2, 7.8, 8.1, 7.9, 8.3, 8.1, 8.4),
(3, 9.0, 9.2, 8.8, 9.1, 9.3, 9.5),
(4, 6.5, 7.0, 7.2, 7.5, 7.8, 8.0),
(5, 8.0, 8.3, 8.1, 8.4, 8.2, 8.5);

-- Insert sample exam scores
INSERT INTO HocSinh_DiemThi (HS_ID, DiemToan, DiemVan, TuChon1MH, TuChon1Diem, TuChon2MH, TuChon2Diem, DiemUT) VALUES 
(1, 8.5, 8.0, 5, 8.2, 6, 8.8, 0.5),
(2, 7.5, 8.5, 4, 7.8, 7, 8.0, 0.0),
(3, 9.0, 8.8, 5, 9.2, 6, 9.0, 1.0),
(4, 6.8, 7.2, 8, 7.0, 9, 6.5, 0.0),
(5, 8.2, 7.8, 5, 8.0, 11, 7.5, 0.25);

PRINT 'Sample data inserted successfully';
EOF

    execute_sql "$method" "sample_data.sql"
    if [ $? -eq 0 ]; then
        print_success "Sample data inserted successfully"
        rm -f sample_data.sql
    else
        print_warning "Failed to insert sample data (this is optional)"
        rm -f sample_data.sql
    fi
}

# Verify database setup
verify_setup() {
    local method=$1
    
    print_step "Verifying database setup..."
    
    # Create verification SQL
    cat > verify.sql << 'EOF'
USE QuanLyDiemTN;
GO

SELECT 'Tables created:' AS Status;
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE';

SELECT 'Sample students:' AS Status;
SELECT COUNT(*) AS StudentCount FROM DM_HocSinh;

SELECT 'Sample classes:' AS Status;  
SELECT COUNT(*) AS ClassCount FROM DM_Lop;

SELECT 'Database setup verification completed' AS Status;
EOF

    execute_sql "$method" "verify.sql"
    if [ $? -eq 0 ]; then
        print_success "Database verification completed"
        rm -f verify.sql
    else
        print_warning "Database verification failed"
        rm -f verify.sql
    fi
}

# Main setup function
main() {
    echo -e "${CYAN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║            🗄️  Database Setup for QuanLyDiemTN           ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    # Detect setup method
    local method=""
    
    # Check if Docker is available and preferred
    if command_exists docker; then
        if docker ps >/dev/null 2>&1; then
            print_info "Docker detected and running"
            method="docker"
        else
            print_warning "Docker detected but not running"
        fi
    fi
    
    # Check for local SQL Server
    if [ -z "$method" ] && command_exists sqlcmd; then
        if test_connection "local"; then
            print_info "Local SQL Server detected"
            method="local"
        fi
    fi
    
    # If no method detected, try to setup via Docker
    if [ -z "$method" ]; then
        print_info "No SQL Server detected. Setting up via Docker..."
        if setup_docker_sql; then
            method="docker"
        else
            print_error "Failed to setup SQL Server"
            exit 1
        fi
    fi
    
    print_info "Using method: $method"
    
    # Create database and schema
    create_database "$method"
    
    # Ask user if they want sample data
    echo
    read -p "Do you want to insert sample data? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        insert_sample_data "$method"
    fi
    
    # Verify setup
    verify_setup "$method"
    
    echo
    print_success "Database setup completed!"
    print_info "Connection details:"
    echo "  Server: localhost:$SQL_PORT"
    echo "  Username: $SQL_USERNAME"
    echo "  Password: $SQL_PASSWORD"
    echo "  Database: $SQL_DATABASE"
    
    if [ "$method" = "docker" ]; then
        echo
        print_info "Docker commands:"
        echo "  Start SQL Server: docker start $DOCKER_CONTAINER"
        echo "  Stop SQL Server: docker stop $DOCKER_CONTAINER"
        echo "  Connect to SQL Server: docker exec -it $DOCKER_CONTAINER /opt/mssql-tools/bin/sqlcmd -S localhost -U $SQL_USERNAME -P $SQL_PASSWORD"
    fi
}

# Parse command line arguments
case "${1:-}" in
    --help|-h)
        echo "Usage: $0 [options]"
        echo
        echo "Options:"
        echo "  --help, -h     Show this help message"
        echo "  --docker       Force Docker setup"
        echo "  --local        Force local SQL Server setup"
        echo "  --verify-only  Only verify existing setup"
        echo
        exit 0
        ;;
    --docker)
        setup_docker_sql
        create_database "docker"
        verify_setup "docker"
        ;;
    --local)
        if test_connection "local"; then
            create_database "local"
            verify_setup "local"
        else
            print_error "Local SQL Server not accessible"
            exit 1
        fi
        ;;
    --verify-only)
        if test_connection "docker"; then
            verify_setup "docker"
        elif test_connection "local"; then
            verify_setup "local"
        else
            print_error "No accessible SQL Server found"
            exit 1
        fi
        ;;
    *)
        main
        ;;
esac

exit 0
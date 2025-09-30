#!/bin/bash

# ===================================================================
# Quick Run Script for QuanLyDiemTN
# ===================================================================
# This script provides convenient ways to run the application
# ===================================================================

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${PURPLE}ℹ️  $1${NC}"
}

print_step() {
    echo -e "${BLUE}📋 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Check if project is built
check_build() {
    if [ ! -d "target/classes" ]; then
        print_step "Building project..."
        mvn clean compile
        if [ $? -ne 0 ]; then
            echo "Build failed!"
            exit 1
        fi
    fi
}

# Run with Maven
run_with_maven() {
    print_step "Running application with Maven..."
    mvn exec:java -Dexec.mainClass="QuanLyDiemTN" -q
}

# Run with JAR
run_with_jar() {
    print_step "Building JAR file..."
    mvn package -DskipTests -q
    
    if [ -f "target/QuanLyDiemTN-1.0-SNAPSHOT.jar" ]; then
        print_step "Running application from JAR..."
        java -jar target/QuanLyDiemTN-1.0-SNAPSHOT.jar
    else
        echo "JAR file not found!"
        exit 1
    fi
}

# Main function
main() {
    echo -e "${CYAN}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║                🎓 QuanLyDiemTN Runner                    ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
    
    case "${1:-}" in
        --help|-h)
            echo "Usage: $0 [option]"
            echo
            echo "Options:"
            echo "  --help, -h    Show this help message"
            echo "  --maven       Run with Maven (default)"
            echo "  --jar         Build and run JAR file"
            echo "  --build       Build project only"
            echo
            exit 0
            ;;
        --jar)
            check_build
            run_with_jar
            ;;
        --build)
            print_step "Building project..."
            mvn clean package -DskipTests
            print_success "Build completed!"
            ;;
        --maven|*)
            check_build
            run_with_maven
            ;;
    esac
}

main "$@"
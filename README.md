# 🎓 Ứng dụng Quản lý Điểm Tốt nghiệp

Ứng dụng quản lý điểm tốt nghiệp THPT được phát triển bằng Java Swing và SQL Server, hỗ trợ đầy đủ các chức năng quản lý học sinh, điểm số và xuất báo cáo Excel.

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

## 📋 Mục lục

- [Tính năng chính](#-tính-năng-chính)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt nhanh](#-cài-đặt-nhanh)
- [Cài đặt chi tiết](#-cài-đặt-chi-tiết)
  - [macOS](#macos)
  - [Windows](#windows)
  - [Linux](#linux)
- [Cấu hình Database](#-cấu-hình-database)
- [Chạy ứng dụng](#-chạy-ứng-dụng)
- [Cách sử dụng](#-cách-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Troubleshooting](#-troubleshooting)

## ✨ Tính năng chính

- 👥 **Quản lý học sinh**: Thêm, sửa, xóa, tìm kiếm học sinh
- 🏫 **Quản lý lớp học**: Tạo lớp và phân công học sinh
- 📊 **Nhập Kết quả học tập**: Điểm các học kỳ lớp 10, 11, 12
- 📝 **Nhập điểm thi**: Điểm thi tốt nghiệp với môn bắt buộc và tự chọn
- 🧮 **Tính toán tự động**: Điểm xét tốt nghiệp theo quy định
- 📈 **Thống kê báo cáo**: Thống kê theo lớp, năm học
- 📄 **Xuất Excel**: Xuất dữ liệu ra file Excel cho tất cả các chức năng

## 💻 Yêu cầu hệ thống

### Tối thiểu
- **Java**: JDK 11 hoặc cao hơn
- **Maven**: 3.6 hoặc cao hơn  
- **Database**: SQL Server 2019 hoặc cao hơn (hoặc SQL Server Express)
- **RAM**: 4GB
- **Ổ cứng**: 500MB trống

### Khuyến nghị
- **Java**: JDK 17 LTS
- **Maven**: 3.8+
- **RAM**: 8GB
- **Ổ cứng**: 1GB trống

## 🚀 Cài đặt nhanh

```bash
# Clone dự án
git clone https://github.com/your-username/QuanLyDiemTN.git
cd QuanLyDiemTN

# Chạy script setup (macOS/Linux)
chmod +x setup.sh
./setup.sh

# Hoặc trên Windows
setup.bat
```

## 🛠 Cài đặt chi tiết

### macOS

#### 1. Cài đặt Java
```bash
# Cài đặt Java 17 qua Homebrew
brew install openjdk@17

# Thêm vào PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Kiểm tra
java -version
```

#### 2. Cài đặt Maven
```bash
# Cài đặt Maven qua Homebrew
brew install maven

# Kiểm tra
mvn -version
```

#### 3. Cài đặt SQL Server
```bash
# Cài đặt SQL Server qua Docker
docker pull mcr.microsoft.com/mssql/server:2019-latest

# Chạy SQL Server container
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Admin@123" \
   -p 1433:1433 --name sqlserver \
   -d mcr.microsoft.com/mssql/server:2019-latest
```

### Windows

#### 1. Cài đặt Java
1. Tải JDK 17 từ [Oracle](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/)
2. Chạy installer và làm theo hướng dẫn
3. Thêm JAVA_HOME vào Environment Variables:
   ```
   JAVA_HOME = C:\Program Files\Java\jdk-17
   PATH = %JAVA_HOME%\bin;%PATH%
   ```
4. Kiểm tra: `java -version`

#### 2. Cài đặt Maven
1. Tải Maven từ [Apache Maven](https://maven.apache.org/download.cgi)
2. Giải nén vào thư mục (ví dụ: `C:\Program Files\Apache\maven`)
3. Thêm vào Environment Variables:
   ```
   MAVEN_HOME = C:\Program Files\Apache\maven
   PATH = %MAVEN_HOME%\bin;%PATH%
   ```
4. Kiểm tra: `mvn -version`

#### 3. Cài đặt SQL Server
1. Tải [SQL Server Express](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
2. Chạy installer và chọn "Basic" installation
3. Hoặc cài đặt SQL Server Management Studio (SSMS) để quản lý

### Linux (Ubuntu/Debian)

#### 1. Cài đặt Java
```bash
# Cập nhật package list
sudo apt update

# Cài đặt OpenJDK 17
sudo apt install openjdk-17-jdk

# Kiểm tra
java -version
```

#### 2. Cài đặt Maven
```bash
# Cài đặt Maven
sudo apt install maven

# Kiểm tra
mvn -version
```

#### 3. Cài đặt SQL Server
```bash
# Thêm Microsoft repository
curl -fsSL https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
sudo add-apt-repository "$(curl -fsSL https://packages.microsoft.com/config/ubuntu/20.04/mssql-server-2019.list)"

# Cài đặt SQL Server
sudo apt update
sudo apt install mssql-server

# Cấu hình SQL Server
sudo /opt/mssql/bin/mssql-conf setup

# Hoặc sử dụng Docker
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=Admin@123" \
   -p 1433:1433 --name sqlserver \
   -d mcr.microsoft.com/mssql/server:2019-latest
```

## 🗄 Cấu hình Database

### 1. Tạo Database
```bash
# Sử dụng sqlcmd (Windows/Linux)
sqlcmd -S localhost -U SA -P Admin@123 -i src/main/resources/database/schema.sql

# Hoặc sử dụng Docker
docker exec -it sqlserver /opt/mssql-tools/bin/sqlcmd \
   -S localhost -U SA -P Admin@123 -i /src/main/resources/database/schema.sql
```

### 2. Cấu hình kết nối
Kiểm tra file `src/main/java/config/DatabaseConnection.java`:
```java
private static final String SERVER = "localhost";
private static final String PORT = "1433";
private static final String DATABASE = "QuanLyDiemTN";
private static final String USERNAME = "SA";
private static final String PASSWORD = "Admin@123";
```

### 3. Kiểm tra kết nối
```bash
# Test connection
mvn test-compile exec:java -Dexec.mainClass="QuanLyDiemTN" -Dexec.args="test"
```

## ▶️ Chạy ứng dụng

### Cách 1: Chạy trực tiếp với Maven
```bash
# Build và chạy
mvn clean compile exec:java -Dexec.mainClass="QuanLyDiemTN"
```

### Cách 2: Tạo JAR file
```bash
# Tạo JAR
mvn clean package

# Chạy JAR
java -jar target/QuanLyDiemTN-1.0-SNAPSHOT.jar
```

### Cách 3: Chạy từ IDE
1. Import project vào IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Chạy class `QuanLyDiemTN.java`

## 📖 Cách sử dụng

### 1. 👥 Quản lý Học sinh
- **Thêm học sinh**: Nhập họ tên, ngày sinh, giới tính, địa chỉ
- **Tìm kiếm**: Tìm theo tên hoặc mã học sinh
- **Chỉnh sửa**: Sửa thông tin học sinh đã có
- **Xuất Excel**: Xuất danh sách học sinh ra file Excel

### 2. 🏫 Quản lý Lớp
- **Tạo lớp**: Tạo lớp mới với tên và năm học
- **Xếp lớp**: Thêm/bỏ học sinh vào/khỏi lớp
- **Xem danh sách**: Hiển thị học sinh trong từng lớp
- **Xuất Excel**: Xuất danh sách lớp ra file Excel

### 3. 📊 Nhập Kết quả học tập
- **Chọn học sinh**: Từ danh sách hoặc tìm kiếm
- **Nhập điểm**: HK1-10, HK2-10, HK1-11, HK2-11, HK1-12, HK2-12
- **Tự động tính**: Hệ thống tự động tính điểm trung bình
- **Xuất Excel**: Xuất bảng Kết quả học tập ra file Excel

### 4. 📝 Nhập Điểm thi tốt nghiệp
- **Môn bắt buộc**: Toán, Văn
- **Môn tự chọn**: Chọn 2 trong các môn có sẵn
- **Điểm ưu tiên**: Từ 0-2 điểm
- **Tự động tính**: Điểm xét tốt nghiệp và kết quả
- **Xuất Excel**: Xuất kết quả thi ra file Excel

### 5. 📈 Thống kê
- **Theo lớp**: Tỷ lệ đậu/rớt của từng lớp
- **Theo năm**: Kết quả các năm học
- **Tổng quát**: Thống kê toàn trường
- **Biểu đồ**: Hiển thị biểu đồ trực quan
- **Xuất Excel**: Xuất báo cáo thống kê ra file Excel

## 🧮 Công thức tính điểm

### Điểm trung bình các năm
```
TB_Lop10 = (HK1_10 + HK2_10 × 2) ÷ 3
TB_Lop11 = (HK1_11 + HK2_11 × 2) ÷ 3  
TB_Lop12 = (HK1_12 + HK2_12 × 2) ÷ 3
TB_CacNam = (TB_Lop10 × 1 + TB_Lop11 × 2 + TB_Lop12 × 3) ÷ 6
```

### Điểm xét tốt nghiệp
```
Tong_Diem_Thi = (Toan + Van + TuChon1 + TuChon2) ÷ 4
Diem_Xet_TN = ((Tong_Diem_Thi + TB_CacNam) ÷ 2) + Diem_UuTien
```

### Điều kiện tốt nghiệp
- ✅ Toán ≥ 5.0
- ✅ Văn ≥ 5.0  
- ✅ Điểm xét tốt nghiệp ≥ 5.0

## 📁 Cấu trúc dự án

```
QuanLyDiemTN/
├── 📄 README.md
├── 📄 pom.xml
├──  setup.sh (macOS/Linux)
├── 📜 setup.bat (Windows)
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/
│   │   │   ├── 📄 QuanLyDiemTN.java
│   │   │   ├── 📂 config/
│   │   │   │   └── 📄 DatabaseConnection.java
│   │   │   ├── 📂 dao/
│   │   │   │   ├── 📄 HocSinhDAO.java
│   │   │   │   ├── 📄 LopDAO.java
│   │   │   │   ├── 📄 DiemDAO.java
│   │   │   │   ├── 📄 DiemTotNghiepDAO.java
│   │   │   │   ├── 📄 KhoiDAO.java
│   │   │   │   └── 📄 ThongKeDAO.java
│   │   │   ├── 📂 model/
│   │   │   │   ├── 📄 HocSinh.java
│   │   │   │   ├── 📄 Lop.java
│   │   │   │   ├── 📄 Khoi.java
│   │   │   │   ├── 📄 DiemHocKy.java
│   │   │   │   ├── 📄 DiemThi.java
│   │   │   │   ├── 📄 DiemTotNghiep.java
│   │   │   │   └── 📄 MonHoc.java
│   │   │   ├── 📂 gui/
│   │   │   │   ├── 📄 MainFrame.java
│   │   │   │   ├── 📄 HocSinhPanel.java
│   │   │   │   ├── 📄 LopPanel.java
│   │   │   │   ├── 📄 DiemHocKyPanel.java
│   │   │   │   ├── 📄 DiemThiPanel.java
│   │   │   │   └── 📄 ThongKePanel.java
│   │   │   └── 📂 util/
│   │   │       ├── 📄 ExcelExporter.java
│   │   │       └── 📄 SchemaExecutor.java
│   │   └── 📂 resources/
│   │       └── 📂 database/
│   │           └── 📄 schema.sql
└── 📂 target/ (generated)
```

## 🐛 Troubleshooting

### Lỗi kết nối Database
```bash
# Kiểm tra SQL Server đang chạy
# Windows
net start MSSQLSERVER

# macOS/Linux (Docker)
docker start sqlserver
docker ps
```

### Lỗi Java
```bash
# Kiểm tra Java version
java -version
javac -version

# Kiểm tra JAVA_HOME
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME% # Windows
```

### Lỗi Maven
```bash
# Kiểm tra Maven
mvn -version

# Clear cache
mvn clean
rm -rf ~/.m2/repository # macOS/Linux
rmdir /s %USERPROFILE%\.m2\repository # Windows
```

### Lỗi Permission (macOS/Linux)
```bash
# Cấp quyền execute cho script
chmod +x setup.sh

# Chạy với sudo nếu cần
sudo ./setup.sh
```

### Lỗi Firewall
- **Windows**: Cho phép Java và SQL Server qua Windows Firewall
- **macOS**: System Preferences > Security & Privacy > Firewall
- **Linux**: Cấu hình iptables hoặc ufw

## 📞 Hỗ trợ

- 🐛 **Báo lỗi**: [Tạo Issue](https://github.com/your-username/QuanLyDiemTN/issues)
- 💬 **Thảo luận**: [Discussions](https://github.com/your-username/QuanLyDiemTN/discussions)
- 📧 **Email**: your-email@example.com

## 📄 License

Dự án này được phân phối dưới [MIT License](LICENSE).

---

<div align="center">
  <p>Phát triển với ❤️ bởi <a href="https://github.com/your-username">Your Name</a></p>
  <p>⭐ Hãy star repo này nếu thấy hữu ích!</p>
</div>
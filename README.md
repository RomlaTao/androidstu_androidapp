## 🔄 Quy Trình Làm Việc với GitHub

### Bước 1: Clone Repository (chỉ thực hiện 1 lần trên máy của bạn)
- git clone https://github.com/<ten-nguoi-dung-hoac-to-chuc>/<ten-repo>.git
- cd <ten-repo>
### Bước 2: Luôn pull code mới từ github trước khi làm việc:
- git pull origin main
### Bước 3: Tạo nhánh mới trước khi làm việc
- git checkout -b ten-nhanh-moi
- git status (kiểm tra trạng thái file)
### Bước 4: Thêm file vào Stagging
- git add . (thêm tất cả)
- git add ten-file (hoặc thêm 1 file cụ thể)
### Bước 5: Commit code
- git commit -m "noi dung thay doi"
### Bước 6: Push lên nhánh bạn đã tạo
- git push origin ten-nhanh-moi
### Bước 7: Tạo Pull ReQuest (PR)
- Truy cập repo trên GitHub
- Vào tab Pull requests
- Nhấn nút New pull request
- Chọn nhánh bạn vừa push so với nhánh chính (main)
- Nhấn Create pull request
- Đợi thành viên khác review
- Cuối cùng, tiến hành Merge
### Bước 8: Xóa branch đã tạo sau khi merge
- git branch -d ten-nhanh-moi

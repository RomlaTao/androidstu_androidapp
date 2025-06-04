# Heath Android App - Authentication Module

## Kiến trúc MVVM cho Authentication

Ứng dụng sử dụng kiến trúc **MVVM (Model-View-ViewModel)** với Java JDK 21 để quản lý chức năng đăng nhập, đăng ký và thiết lập thông tin người dùng.

## Cấu trúc thư mục

```
app/src/main/java/com/example/heath_android/
├── data/
│   ├── model/
│   │   ├── LoginRequest.java          # Model cho request đăng nhập
│   │   ├── LoginResponse.java         # Model cho response đăng nhập
│   │   ├── SignupRequest.java         # Model cho request đăng ký (sử dụng fullName)
│   │   ├── SignupResponse.java        # Model cho response đăng ký
│   │   ├── UserInfoRequest.java       # Model cho request setup user info
│   │   ├── UserInfoResponse.java      # Model cho response setup user info
│   │   └── User.java                  # Model người dùng
│   ├── network/
│   │   └── ApiService.java            # Interface định nghĩa API calls
│   ├── repository/
│   │   ├── LoginRepository.java       # Repository xử lý logic đăng nhập
│   │   ├── SignupRepository.java      # Repository xử lý logic đăng ký
│   │   └── UserInfoRepository.java    # Repository xử lý logic setup user info
│   └── DatabaseInformation.java       # Local storage với SharedPreferences
└── ui/
    ├── auth/
    │   ├── AuthViewModel.java         # ViewModel quản lý state authentication
    │   ├── LoginActivity.java         # Activity đăng nhập
    │   └── SignupActivity.java        # Activity đăng ký
    └── onboarding/
        ├── UserInfoViewModel.java     # ViewModel cho setup user info
        └── UserInfoSetupActivity.java # Activity setup thông tin lần đầu
```

## Workflow Authentication

### Đăng nhập (Login)
1. **User** nhập email và password trong `LoginActivity`
2. **LoginActivity** gọi `AuthViewModel.login(email, password)`
3. **AuthViewModel** gọi `LoginRepository.login(email, password)`
4. **LoginRepository** tạo `LoginRequest` và gọi API qua `ApiService`
5. Kết quả trả về được observe qua `LiveData` và hiển thị UI
6. Nếu thành công, chuyển đến `UserInfoSetupActivity` (lần đầu) hoặc `HomeActivity`

### Đăng ký (Signup)
1. **User** nhập thông tin trong `SignupActivity`
2. **SignupActivity** gọi `AuthViewModel.signupUser(SignupRequest)`
3. **AuthViewModel** gọi `SignupRepository.signup(SignupRequest)`
4. **SignupRepository** gọi API qua `ApiService`
5. Kết quả trả về được observe qua `LiveData`
6. Nếu thành công, chuyển về màn hình đăng nhập

### Setup User Info (Lần đầu đăng nhập)
1. **User** nhập thông tin cá nhân trong `UserInfoSetupActivity`
2. **UserInfoSetupActivity** gọi `UserInfoViewModel.setupUserInfo(token, UserInfoRequest)`
3. **UserInfoViewModel** gọi `UserInfoRepository.setupUserInfo(token, UserInfoRequest)`
4. **UserInfoRepository** gọi API với Bearer token qua `ApiService`
5. Kết quả trả về được observe qua `LiveData`
6. Nếu thành công, chuyển đến `HomeActivity`

## JSON Format

### Signup Request Format
```json
{
    "fullName": "Tên đầy đủ của người dùng",
    "email": "user@example.com",
    "password": "userpassword"
}
```

### Login Request Format
```json
{
    "email": "user@example.com", 
    "password": "userpassword"
}
```

### User Info Setup Request Format
```json
{
    "birthDate": "2004-11-22",
    "gender": "MALE",
    "height": "173",
    "weight": "58",
    "initialActivityLevel": "MODERATELY_ACTIVE"
}
```

### User Info Setup Response Format
```json
{
    "id": 103,
    "fullName": "nguyenthinhphat",
    "email": "nguyenthinhphat@gmail.com",
    "password": null,
    "gender": "MALE",
    "birthDate": "2004-11-22T00:00:00.000+00:00",
    "weight": 58.0,
    "height": 173.0,
    "initialActivityLevel": "MODERATELY_ACTIVE",
    "activityLevelSetAt": "2025-05-30T14:59:09.162+00:00",
    "createdAt": "2025-05-30T13:52:12.728+00:00",
    "updatedAt": "2025-05-30T14:29:40.883+00:00"
}
```

## Activity Level Options

- **SEDENTARY** (1.2): "Ít vận động (ít hoặc không tập thể dục)"
- **LIGHTLY_ACTIVE** (1.375): "Vận động nhẹ (tập thể dục nhẹ 1-3 ngày/tuần)"
- **MODERATELY_ACTIVE** (1.55): "Vận động vừa phải (tập thể dục vừa 3-5 ngày/tuần)"
- **VERY_ACTIVE** (1.725): "Vận động nhiều (tập thể dục nặng 6-7 ngày/tuần)"
- **EXTRA_ACTIVE** (1.9): "Vận động rất nhiều (tập thể dục rất nặng & công việc thể chất)"

## Components chính

### AuthViewModel
- Quản lý state cho cả đăng nhập và đăng ký
- Expose LiveData cho UI observe
- Methods:
  - `login(email, password)`
  - `signupUser(SignupRequest)`
  - `getResponseLiveData()`, `getLoginErrorLiveData()`
  - `getSignupSuccessLiveData()`, `getSignupErrorLiveData()`

### UserInfoViewModel
- Quản lý state cho setup thông tin user
- Methods:
  - `setupUserInfo(token, UserInfoRequest)`
  - `getUserInfoSuccessLiveData()`, `getUserInfoErrorLiveData()`

### Repositories
- **LoginRepository**: Xử lý API calls cho đăng nhập
- **SignupRepository**: Xử lý API calls cho đăng ký
- **UserInfoRepository**: Xử lý API calls cho setup user info
- Sử dụng Retrofit để gọi API

### DatabaseInformation
- Lưu trữ local với SharedPreferences
- Methods:
  - `saveUserInfo(token, email, name)`
  - `getToken()`, `getEmail()`, `getName()`
  - `isLoggedIn()`, `clearUserInfo()`

## Dependencies cần thiết

```gradle
// ViewModel và LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'

// Retrofit cho API calls
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Gson cho JSON parsing
implementation 'com.google.code.gson:gson:2.10.1'
```

## Cấu hình API

Trong các Repository classes, BASE_URL được cấu hình:

```java
// LoginRepository & SignupRepository
private static final String BASE_URL = "http://10.0.2.2:8080/auth/";

// UserInfoRepository  
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

**Lưu ý**: `10.0.2.2` là địa chỉ localhost cho Android Emulator

## API Endpoints

- **POST** `/auth/login` - Đăng nhập
- **POST** `/auth/signup` - Đăng ký
- **PUT** `/users/setup` - Setup thông tin user (cần Bearer token)

## Testing

Unit tests được tạo trong `app/src/test/java/com/example/heath_android/AuthViewModelTest.java`

Chạy tests:
```bash
./gradlew test
```

## Features UserInfoSetupActivity

1. **DatePicker**: Chọn ngày sinh với format YYYY-MM-DD
2. **Gender Dropdown**: Chọn Nam/Nữ (MALE/FEMALE)
3. **Weight Input**: Nhập cân nặng (kg) với số thập phân
4. **Height Input**: Nhập chiều cao (cm) với số thập phân
5. **Activity Level Dropdown**: Chọn mức độ hoạt động với 5 options
6. **Validation**: Kiểm tra đầy đủ thông tin trước khi submit
7. **Bearer Token**: Tự động lấy token từ local storage và gửi API

## Lưu ý

1. **Security**: Cần implement proper validation và encryption cho password
2. **Error Handling**: Đã có basic error handling, có thể mở rộng thêm
3. **Network**: Cần thêm network security config cho production
4. **Token Management**: Implement refresh token mechanism nếu cần
5. **UI Validation**: Thêm validation cho email format, password strength
6. **JSON Format**: Sử dụng `fullName` thay vì `name` trong signup requests
7. **First Time Setup**: UserInfoSetupActivity chỉ hiển thị lần đầu đăng nhập

## Navigation Flow

```
LoginActivity -> UserInfoSetupActivity -> HomeActivity (sau khi complete)
``` 
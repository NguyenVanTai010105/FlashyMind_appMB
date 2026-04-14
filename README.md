# 🧠 FlashyMind - Ứng dụng Học Tập Thông Minh

FlashyMind là một ứng dụng di động hỗ trợ học tập (Flashcard) được phát triển hoàn toàn bằng **Kotlin** và **Jetpack Compose**. Ứng dụng được thiết kế nhằm mang lại trải nghiệm học tập mượt mà, thông minh và cá nhân hóa cho người dùng, kết hợp các công nghệ hiện đại nhất của Android.

---

## ✨ Tính năng nổi bật

* **Giao diện hiện đại, mượt mà:** Sử dụng 100% Jetpack Compose kết hợp với Material Design 3.
* **Hỗ trợ AI Dịch thuật Offline:** Tích hợp Google ML Kit On-Device Translation giúp người dùng dịch từ vựng/tài liệu ngay trên thiết bị mà không cần kết nối mạng.
* **Nhắc nhở học tập thông minh:** Sử dụng WorkManager để lên lịch gửi thông báo nhắc nhở học tập chạy ngầm một cách tối ưu.
* **Lưu trữ ngoại tuyến & Đồng bộ:** Quản lý cơ sở dữ liệu flashcard cục bộ với Room Database và sẵn sàng đồng bộ API qua Retrofit.
* **Cá nhân hóa trải nghiệm:** Ghi nhớ tùy chọn người dùng (ví dụ: Dark/Light Mode) thông qua DataStore Preferences.
* **Tải ảnh tối ưu:** Hiển thị hình ảnh minh họa flashcard nhanh chóng với thư viện Coil.

---

## 🛠 Tech Stack & Kiến trúc

Dự án được xây dựng dựa trên các tiêu chuẩn mới nhất của Google (Modern Android Development - MAD):

**Ngôn ngữ & Nền tảng:**
* [Kotlin](https://kotlinlang.org/) (JVM Target 11)
* Min SDK: 24 | Target SDK: 35

**Giao diện (UI):**
* [Jetpack Compose](https://developer.android.com/jetpack/compose)
* Material 3 & Material Icons Extended
* [Coil Compose](https://coil-kt.github.io/coil/compose/) - Xử lý và tải hình ảnh

**Kiến trúc & Quản lý trạng thái (Architecture Components):**
* **MVVM Architecture** (Model - View - ViewModel)
* [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & Flow - Xử lý đa luồng và bất đồng bộ
* [Dagger Hilt](https://dagger.dev/hilt/) - Dependency Injection (Bao gồm cả Hilt Work)

**Lưu trữ & Mạng (Data & Network):**
* [Room Database](https://developer.android.com/training/data-storage/room) - ORM cho SQLite cục bộ
* [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) - Lưu trữ Key-Value an toàn
* [Retrofit](https://square.github.io/retrofit/) & Gson - Gọi API Backend và parse JSON

**AI & Background Tasks:**
* [Google ML Kit (Translate)](https://developers.google.com/ml-kit) - Dịch thuật trực tiếp trên thiết bị
* [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - Quản lý tác vụ nền và thông báo

**Testing:**
* JUnit4, Espresso, Mockito, Coroutines Test
* Compose UI Test

---

## 🚀 Cài đặt & Chạy dự án

Để chạy ứng dụng trên máy của bạn, hãy làm theo các bước sau:

1. **Clone repository:**
   ```bash
   git clone [https://github.com/your-username/FlashyMind.git](https://github.com/your-username/FlashyMind.git)
   Mở dự án:
2. Mở Android Studio (khuyến nghị phiên bản mới nhất hỗ trợ Compose 1.5+). Chọn File > Open và trỏ tới thư mục dự án vừa clone.

3. Đồng bộ Gradle:
Chờ Android Studio tự động tải xuống các thư viện và đồng bộ file build.gradle.kts.

4. Chạy ứng dụng:
Kết nối thiết bị Android (hoặc sử dụng Emulator) và nhấn nút Run (Shift + F10).
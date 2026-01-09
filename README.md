# 🎒 Petualangan Belajar

**Petualangan Belajar** adalah aplikasi **desktop edukasi interaktif (Java Swing)** yang ditujukan untuk **anak PAUD–TK**.  
Aplikasi ini menggabungkan **pembelajaran dasar** (Angka, Huruf, Warna, Bentuk) dengan **game interaktif, progres pemain, dan sistem cerita**.

---

## ✨ Fitur Utama
- 🎮 Game edukasi interaktif berbasis level
- 👶 Multi user (profil anak)
- 📚 Modul pembelajaran:
  - Angka
  - Huruf
  - Warna
  - Bentuk
- 🧠 Beragam tipe soal:
  - Choice
  - Click
  - Typing
  - Sequence
  - Comparison
- 🏆 Leaderboard (total skor)
- 📖 Story system (Prolog, Start, Success, Epilog)
- 💾 Penyimpanan lokal menggunakan **SQLite**

---

## 🛠️ Teknologi
- **Bahasa**: Java
- **UI**: Java Swing
- **Database**: SQLite
- **Build**: Desktop Application (EXE / MSI ready)
- **Diagram**: Mermaid (ERD)

---

## 📂 Struktur Database
Database disimpan secara lokal (file-based) menggunakan SQLite.  
Struktur dan relasi database digambarkan pada ERD berikut.

---

## 🗂️ Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USERS {
        int id PK
        string name
        string avatar
        int level
        int total_xp
        int bgm_volume
        int sfx_volume
        int is_active
    }

    MODULES {
        int id PK
        string name
        string description
    }

    QUESTIONS {
        int id PK
        int module_id FK
        int level
        string question_type
        string question_text
        string question_image
        string question_audio
        string option_a
        string option_b
        string option_c
        string correct_answer
    }

    USER_PROGRESS {
        int id PK
        int user_id FK
        int module_id FK
        int highest_level_unlocked
    }

    GAME_RESULTS {
        int id PK
        int user_id FK
        int module_id FK
        int level
        int score
        string created_at
    }

    STORY_PROGRESS {
        int id PK
        int user_id FK
        int module_id
        int level
        string story_type
        string seen_at
    }

    USERS ||--o{ USER_PROGRESS : has
    USERS ||--o{ GAME_RESULTS : records
    USERS ||--o{ STORY_PROGRESS : views

    MODULES ||--o{ QUESTIONS : contains
    MODULES ||--o{ USER_PROGRESS : tracks
    MODULES ||--o{ GAME_RESULTS : evaluates


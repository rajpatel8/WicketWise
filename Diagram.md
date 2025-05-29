# 🏏 Cricket Coaching App - Next Phase Sprint Plan
## 📅 2-Week Development Cycle

---

## 🎯 Phase 2 Overview: Profile Management System

```mermaid
flowchart TD
    A[Phase 2: Profile Management] --> B[Week 1: CRUD Setup]
    A --> C[Week 2: Annotation Foundation]
    
    B --> D[Coach Profile System]
    B --> E[Student Profile System]
    
    C --> F[Video Upload Module]
    C --> G[Basic Annotation UI]
    
    style A fill:#2E7D32,color:#fff
    style B fill:#4CAF50,color:#fff
    style C fill:#4CAF50,color:#fff
```

---

## 📋 Sprint Breakdown

### 🏃‍♂️ **Week 1: CRUD Foundation (Days 1-7)**

```mermaid
gantt
    title Week 1 - Profile Management CRUD
    dateFormat  YYYY-MM-DD
    section Coach Profile
    Create Coach Screen    :active, coach1, 2025-06-01, 2025-06-03
    Edit Coach Profile     :coach2, 2025-06-03, 2025-06-05
    Coach Dashboard        :coach3, 2025-06-04, 2025-06-06
    
    section Student Profile
    Create Student Screen  :student1, 2025-06-02, 2025-06-04
    Student List View      :student2, 2025-06-04, 2025-06-06
    Student Details        :student3, 2025-06-05, 2025-06-07
    
    section Integration
    Profile Navigation     :nav, 2025-06-06, 2025-06-07
    Testing & Polish       :test, 2025-06-07, 2025-06-07
```

### 🎬 **Week 2: Annotation Foundation (Days 8-14)**

```mermaid
gantt
    title Week 2 - Annotation Setup
    dateFormat  YYYY-MM-DD
    section Video Module
    Video Upload UI        :video1, 2025-06-08, 2025-06-10
    Video Storage System   :video2, 2025-06-09, 2025-06-11
    Video Player Setup     :video3, 2025-06-10, 2025-06-12
    
    section Annotation Base
    Drawing Canvas         :draw1, 2025-06-11, 2025-06-13
    Basic Drawing Tools    :draw2, 2025-06-12, 2025-06-14
    Annotation Storage     :draw3, 2025-06-13, 2025-06-14
    
    section Final Integration
    End-to-End Testing     :final, 2025-06-14, 2025-06-14
```

---

## 🏗️ Technical Architecture

```mermaid
graph TB
    subgraph "Week 1: CRUD Layer"
        A[Coach Profile Activity]
        B[Student Profile Activity]
        C[Profile Database Helper]
        D[SQLite Local Storage]
    end
    
    subgraph "Week 2: Video & Annotation"
        E[Video Upload Activity]
        F[Custom Video Player]
        G[Drawing Canvas View]
        H[Annotation Storage]
    end
    
    A --> C
    B --> C
    C --> D
    
    E --> F
    F --> G
    G --> H
    
    style A fill:#e3f2fd
    style B fill:#e3f2fd
    style E fill:#fff3e0
    style F fill:#fff3e0
    style G fill:#fff3e0
```

---

## 📱 User Flow Design

```mermaid
flowchart LR
    A[Login] --> B{User Type}
    B -->|Coach| C[Coach Dashboard]
    B -->|Setup| D[Profile Creation]
    
    C --> E[Student List]
    E --> F[Select Student]
    F --> G[Upload Video]
    G --> H[Start Annotation]
    
    D --> I[Create Coach Profile]
    D --> J[Add Students]
    J --> E
    
    style A fill:#2E7D32,color:#fff
    style H fill:#FF6B6B,color:#fff
    style C fill:#4ECDC4,color:#fff
```

---

## ✅ Week 1 Deliverables

### 👨‍🏫 **Coach Profile System**
- [ ] **Create Coach Profile Screen**
  - Name, Email, Phone, Experience
  - Profile Picture Upload
  - Coaching Credentials
- [ ] **Edit Coach Profile**
  - Update personal information
  - Change profile picture
  - Save/Cancel functionality
- [ ] **Coach Dashboard**
  - Welcome message with coach name
  - Quick stats overview
  - Navigation to students

### 👨‍🎓 **Student Profile System**
- [ ] **Add Student Screen**
  - Student name, age, position
  - Contact details
  - Skill level assessment
- [ ] **Student List View**
  - Grid/List toggle
  - Search functionality
  - Quick student info cards
- [ ] **Student Details**
  - Full student profile
  - Performance history placeholder
  - Edit student information

---

## 🎬 Week 2 Deliverables

### 📹 **Video Management**
- [ ] **Video Upload Interface**
  - Select from gallery
  - Record new video (basic)
  - Video thumbnail preview
- [ ] **Video Storage System**
  - Local file management
  - Video metadata storage
  - File size optimization
- [ ] **Basic Video Player**
  - Play/Pause controls
  - Seek bar
  - Full-screen mode

### ✏️ **Annotation Foundation**
- [ ] **Drawing Canvas Overlay**
  - Touch drawing functionality
  - Basic pen tool
  - Clear/Undo options
- [ ] **Annotation Storage**
  - Save drawing coordinates
  - Link annotations to video timeline
  - Basic annotation retrieval

---

## 📊 Success Metrics

```mermaid
pie title Week 1-2 Development Focus
    "Profile CRUD" : 40
    "Video Upload" : 25
    "Basic Player" : 20
    "Drawing Tools" : 15
```

### 🎯 **Definition of Done**
- ✅ Coach can create and edit profile
- ✅ Coach can add/manage students
- ✅ Coach can upload video for student
- ✅ Coach can draw basic annotations on video
- ✅ All data persists locally
- ✅ Smooth navigation between all screens

---

## 🛠️ Technical Implementation

### 📱 **New Activities to Create**
1. `CoachProfileActivity.java`
2. `StudentProfileActivity.java` 
3. `StudentListActivity.java`
4. `VideoUploadActivity.java`
5. `VideoPlayerActivity.java`

### 🗄️ **Database Schema**
```sql
-- Coach Table
CREATE TABLE coaches (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE,
    phone TEXT,
    experience_years INTEGER
);

-- Student Table  
CREATE TABLE students (
    id INTEGER PRIMARY KEY,
    coach_id INTEGER,
    name TEXT NOT NULL,
    age INTEGER,
    position TEXT,
    skill_level TEXT,
    FOREIGN KEY(coach_id) REFERENCES coaches(id)
);

-- Video Table
CREATE TABLE videos (
    id INTEGER PRIMARY KEY,
    student_id INTEGER,
    file_path TEXT NOT NULL,
    created_date TEXT,
    FOREIGN KEY(student_id) REFERENCES students(id)
);
```

---

## 🚀 Sprint Goals

### 🏆 **Primary Objectives**
1. **Complete CRUD functionality** for Coach & Student profiles
2. **Establish video workflow** from upload to basic playback
3. **Implement foundation** for annotation system
4. **Maintain clean UI/UX** consistent with current design

### 🎯 **Success Criteria**
- Coach can manage student roster effectively
- Video upload and playback works smoothly  
- Basic drawing on video is functional
- App remains crash-free and performant

---

## 📋 Daily Standup Structure

### 🌅 **Daily Questions**
1. What did I complete yesterday?
2. What will I work on today?
3. Any blockers or challenges?

### 🎯 **Weekly Review Points**
- **Week 1:** CRUD functionality complete?
- **Week 2:** Annotation foundation ready?

---

```
🏏 Ready to build the next phase! Let's create those profiles and start annotating! ⚡
```

---

*📝 2-Week Sprint Plan - June 1-14, 2025*

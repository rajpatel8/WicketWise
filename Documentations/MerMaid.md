# Mermaid Diagrams for Become Better App Presentation

## 1. System Architecture Diagram

```mermaid
graph TB
    A[Mobile App<br/>Android] --> B[Presentation Layer<br/>Activities & Fragments]
    B --> C[Business Logic Layer<br/>Services & Managers]
    C --> D[Data Access Layer<br/>DatabaseHelper]
    D --> E[Local Storage<br/>SQLite Database]
    
    A --> F[Video Processing<br/>MediaRecorder API]
    A --> G[Real-time Chat<br/>WebSocket]
    A --> H[Annotation System<br/>Custom Canvas]
    
    E --> I[Cloud Sync<br/>Backup & Restore]
    
    style A fill:#e1f5fe
    style E fill:#f3e5f5
    style I fill:#e8f5e8
```

## 2. User Workflow Diagram

```mermaid
flowchart TD
    A[Student Login] --> B[Record/Upload Video]
    B --> C[Add Description & Send to Coach]
    C --> D[Coach Receives Video]
    D --> E[Coach Analyzes Video]
    E --> F[Add Annotations & Voice Feedback]
    F --> G[Send Feedback to Student]
    G --> H[Student Views Annotated Video]
    H --> I[Real-time Chat Discussion]
    I --> J[Student Practices & Improves]
    J --> B
    
    style A fill:#ffecb3
    style D fill:#c8e6c9
    style H fill:#ffecb3
    style I fill:#e1f5fe
```

## 3. App Navigation Flow

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> StudentDashboard : Student Login
    Login --> CoachDashboard : Coach Login
    
    StudentDashboard --> VideoUpload
    StudentDashboard --> ViewFeedback
    StudentDashboard --> Chat
    
    CoachDashboard --> VideoReview
    CoachDashboard --> StudentManagement
    CoachDashboard --> Chat
    
    VideoUpload --> VideoPlayer
    VideoReview --> AnnotationTools
    AnnotationTools --> VoiceFeedback
    VoiceFeedback --> SendFeedback
    
    ViewFeedback --> AnnotatedVideoPlayer
    
    Chat --> [*] : Logout
    SendFeedback --> CoachDashboard
    AnnotatedVideoPlayer --> StudentDashboard
```

## 4. Database Schema Diagram

```mermaid
erDiagram
    COACHES {
        int id PK
        string name
        string email
        string specialization
        int experience_years
        string certification
    }
    
    STUDENTS {
        int id PK
        string name
        string email
        int age
        string skill_level
    }
    
    VIDEOS {
        int id PK
        string title
        string description
        string file_path
        int student_id FK
        datetime created_at
    }
    
    ANNOTATIONS {
        int id PK
        int video_id FK
        int coach_id FK
        string annotation_data
        int timestamp
    }
    
    VOICE_FEEDBACK {
        int id PK
        int video_id FK
        int coach_id FK
        string audio_path
        int timestamp
    }
    
    CHAT_MESSAGES {
        int id PK
        int sender_id FK
        int receiver_id FK
        string message
        datetime sent_at
    }
    
    COACHES ||--o{ ANNOTATIONS : creates
    COACHES ||--o{ VOICE_FEEDBACK : records
    STUDENTS ||--o{ VIDEOS : uploads
    VIDEOS ||--o{ ANNOTATIONS : has
    VIDEOS ||--o{ VOICE_FEEDBACK : receives
    COACHES ||--o{ CHAT_MESSAGES : sends
    STUDENTS ||--o{ CHAT_MESSAGES : sends
```

## 5. Development Timeline

```mermaid
timeline
    title Become Better App Development Timeline
    
    October 2024 : v1.0 Beta
                 : Authentication System
                 : Basic Video Upload
                 : Profile Management
    
    November 2024 : v1.0 Stable
                  : Video Annotation Tools
                  : Voice Feedback System
                  : Coach Dashboard
                  : Student Video Player
    
    December 2024 : v1.1 Stable
                  : Real-time Chat System
                  : Enhanced Video Player
                  : Push Notifications
                  : Performance Optimizations
```

## 6. Technology Stack Diagram

```mermaid
graph LR
    A[Become Better App] --> B[Frontend]
    A --> C[Backend]
    A --> D[Database]
    A --> E[Services]
    
    B --> B1[Native Android]
    B --> B2[Java]
    B --> B3[Material Design]
    B --> B4[Custom UI Components]
    
    C --> C1[MVC Architecture]
    C --> C2[Business Logic]
    C --> C3[API Services]
    C --> C4[File Management]
    
    D --> D1[SQLite]
    D --> D2[Room Persistence]
    D --> D3[Cloud Sync]
    D --> D4[Data Models]
    
    E --> E1[Video Processing]
    E --> E2[Real-time Chat]
    E --> E3[Annotation Engine]
    E --> E4[Voice Recording]
    
    style A fill:#ff9800
    style B fill:#2196f3
    style C fill:#4caf50
    style D fill:#9c27b0
    style E fill:#f44336
```

## 7. Feature Evolution Diagram

```mermaid
mindmap
  root((Become Better<br/>Cricket Academy))
    (Core Features v1.0)
      Authentication
      Video Upload
      Basic Annotations
      Coach Dashboard
      Student Profiles
    (Enhanced Features v1.1)
      Real-time Chat
      Annotated Playback
      Push Notifications
      Performance Optimizations
      Message History
    (Future Features)
      Video Calls
      AI Analysis
      Team Management
      Advanced Analytics
      Multi-language
```

## 8. User Roles and Permissions

```mermaid
graph TD
    A[Become Better App] --> B[Student Role]
    A --> C[Coach Role]
    
    B --> B1[Upload Videos]
    B --> B2[View Feedback]
    B --> B3[Chat with Coaches]
    B --> B4[Track Progress]
    B --> B5[Profile Management]
    
    C --> C1[Review Videos]
    C --> C2[Create Annotations]
    C --> C3[Record Voice Feedback]
    C --> C4[Manage Students]
    C --> C5[Chat with Students]
    C --> C6[Analytics Dashboard]
    
    style B fill:#e3f2fd
    style C fill:#e8f5e8
    style A fill:#fff3e0
```

## 9. Video Processing Pipeline

```mermaid
flowchart LR
    A[Video Recording/<br/>Upload] --> B[File Validation]
    B --> C[Compression &<br/>Optimization]
    C --> D[Metadata<br/>Extraction]
    D --> E[Local Storage]
    E --> F[Coach Review<br/>Interface]
    F --> G[Annotation<br/>Processing]
    G --> H[Voice Feedback<br/>Integration]
    H --> I[Student Playback<br/>with Annotations]
    
    style A fill:#ffcdd2
    style E fill:#c8e6c9
    style I fill:#e1f5fe
```

## 10. Communication Flow

```mermaid
sequenceDiagram
    participant S as Student
    participant A as App
    participant D as Database
    participant C as Coach
    
    S->>A: Upload Video
    A->>D: Store Video & Metadata
    A->>C: Notify Coach
    C->>A: Open Video for Review
    A->>D: Load Video Data
    C->>A: Add Annotations
    C->>A: Record Voice Feedback
    A->>D: Save Feedback
    A->>S: Notify Student
    S->>A: View Annotated Video
    A->>D: Load Annotations
    S->>C: Send Chat Message
    C->>S: Reply via Chat
```

---

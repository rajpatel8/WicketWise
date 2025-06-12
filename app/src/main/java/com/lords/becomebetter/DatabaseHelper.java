package com.lords.becomebetter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CricketCoaching.db";
    private static final int DATABASE_VERSION = 2; // Updated version for new tables

    // Table names
    private static final String TABLE_COACHES = "coaches";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_COACH_CODES = "coach_codes";
    private static final String TABLE_VIDEOS = "videos";
    private static final String TABLE_ANNOTATIONS = "annotations";

    // Common columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Coach specific columns
    private static final String COLUMN_EXPERIENCE_YEARS = "experience_years";
    private static final String COLUMN_SPECIALIZATION = "specialization";
    private static final String COLUMN_CERTIFICATION = "certification";

    // Student specific columns
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_SKILL_LEVEL = "skill_level";
    private static final String COLUMN_COACH_ID = "coach_id";

    // Coach codes columns
    private static final String COLUMN_CODE = "code";
    private static final String COLUMN_IS_USED = "is_used";

    // Video table columns
    private static final String COLUMN_VIDEO_ID = "video_id";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_VIDEO_PATH = "video_path";
    private static final String COLUMN_VIDEO_TITLE = "video_title";
    private static final String COLUMN_VIDEO_DESCRIPTION = "video_description";
    private static final String COLUMN_VIDEO_DURATION = "video_duration";
    private static final String COLUMN_UPLOAD_DATE = "upload_date";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_THUMBNAIL_PATH = "thumbnail_path";

    // Annotation table columns
    private static final String COLUMN_ANNOTATION_ID = "annotation_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_ANNOTATION_TYPE = "annotation_type";
    private static final String COLUMN_ANNOTATION_DATA = "annotation_data";
    private static final String COLUMN_X_POSITION = "x_position";
    private static final String COLUMN_Y_POSITION = "y_position";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create coaches table
        String CREATE_COACHES_TABLE = "CREATE TABLE " + TABLE_COACHES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_EXPERIENCE_YEARS + " INTEGER,"
                + COLUMN_SPECIALIZATION + " TEXT,"
                + COLUMN_CERTIFICATION + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        // Create students table
        String CREATE_STUDENTS_TABLE = "CREATE TABLE " + TABLE_STUDENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT NOT NULL,"
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_AGE + " INTEGER,"
                + COLUMN_SKILL_LEVEL + " TEXT,"
                + COLUMN_COACH_ID + " INTEGER,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Create coach codes table
        String CREATE_COACH_CODES_TABLE = "CREATE TABLE " + TABLE_COACH_CODES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CODE + " TEXT UNIQUE NOT NULL,"
                + COLUMN_IS_USED + " INTEGER DEFAULT 0,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";

        // Create videos table
        String CREATE_VIDEOS_TABLE = "CREATE TABLE " + TABLE_VIDEOS + "("
                + COLUMN_VIDEO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STUDENT_ID + " INTEGER NOT NULL,"
                + COLUMN_COACH_ID + " INTEGER,"
                + COLUMN_VIDEO_PATH + " TEXT NOT NULL,"
                + COLUMN_VIDEO_TITLE + " TEXT,"
                + COLUMN_VIDEO_DESCRIPTION + " TEXT,"
                + COLUMN_VIDEO_DURATION + " INTEGER,"
                + COLUMN_UPLOAD_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_STATUS + " TEXT DEFAULT 'pending',"
                + COLUMN_THUMBNAIL_PATH + " TEXT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Create annotations table
        String CREATE_ANNOTATIONS_TABLE = "CREATE TABLE " + TABLE_ANNOTATIONS + "("
                + COLUMN_ANNOTATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VIDEO_ID + " INTEGER NOT NULL,"
                + COLUMN_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL,"
                + COLUMN_ANNOTATION_TYPE + " TEXT NOT NULL,"
                + COLUMN_ANNOTATION_DATA + " TEXT NOT NULL,"
                + COLUMN_X_POSITION + " FLOAT,"
                + COLUMN_Y_POSITION + " FLOAT,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + COLUMN_VIDEO_ID + ") REFERENCES " + TABLE_VIDEOS + "(" + COLUMN_VIDEO_ID + "),"
                + "FOREIGN KEY(" + COLUMN_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Execute table creation
        db.execSQL(CREATE_COACHES_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_COACH_CODES_TABLE);
        db.execSQL(CREATE_VIDEOS_TABLE);
        db.execSQL(CREATE_ANNOTATIONS_TABLE);

        // Insert some default coach codes
        insertDefaultCoachCodes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables in correct order (annotations first, then videos, etc.)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOTATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);

        // Recreate all tables
        onCreate(db);
    }

    private void insertDefaultCoachCodes(SQLiteDatabase db) {
        // Insert some default 6-digit codes for coaches
        String[] defaultCodes = {"123456", "789012", "345678", "901234", "567890"};

        for (String code : defaultCodes) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, code);
            values.put(COLUMN_IS_USED, 0);
            db.insert(TABLE_COACH_CODES, null, values);
        }
    }

    // Validate coach code
    public boolean validateCoachCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_CODES + " WHERE "
                + COLUMN_CODE + " = ? AND " + COLUMN_IS_USED + " = 0";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Mark coach code as used
    public void markCoachCodeAsUsed(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_USED, 1);
        db.update(TABLE_COACH_CODES, values, COLUMN_CODE + " = ?", new String[]{code});
    }

    // Add new coach
    public long addCoach(Coach coach, String coachCode) {
        if (!validateCoachCode(coachCode)) {
            return -1; // Invalid coach code
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, coach.getName());
        values.put(COLUMN_EMAIL, coach.getEmail());
        values.put(COLUMN_PASSWORD, coach.getPassword());
        values.put(COLUMN_PHONE, coach.getPhone());
        values.put(COLUMN_EXPERIENCE_YEARS, coach.getExperienceYears());
        values.put(COLUMN_SPECIALIZATION, coach.getSpecialization());
        values.put(COLUMN_CERTIFICATION, coach.getCertification());

        long id = db.insert(TABLE_COACHES, null, values);

        if (id != -1) {
            markCoachCodeAsUsed(coachCode);
        }

        return id;
    }

    // Add new student
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, student.getName());
        values.put(COLUMN_EMAIL, student.getEmail());
        values.put(COLUMN_PASSWORD, student.getPassword());
        values.put(COLUMN_PHONE, student.getPhone());
        values.put(COLUMN_AGE, student.getAge());
        values.put(COLUMN_SKILL_LEVEL, student.getSkillLevel());
        values.put(COLUMN_COACH_ID, student.getCoachId());

        return db.insert(TABLE_STUDENTS, null, values);
    }

    // Check if email exists
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check in coaches table
        String coachQuery = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor coachCursor = db.rawQuery(coachQuery, new String[]{email});
        boolean existsInCoaches = coachCursor.getCount() > 0;
        coachCursor.close();

        if (existsInCoaches) {
            return true;
        }

        // Check in students table
        String studentQuery = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor studentCursor = db.rawQuery(studentQuery, new String[]{email});
        boolean existsInStudents = studentCursor.getCount() > 0;
        studentCursor.close();

        return existsInStudents;
    }

    // Authenticate user (returns user type: "coach", "student", or null)
    public String authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check coaches table
        String coachQuery = "SELECT * FROM " + TABLE_COACHES + " WHERE "
                + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor coachCursor = db.rawQuery(coachQuery, new String[]{email, password});
        if (coachCursor.getCount() > 0) {
            coachCursor.close();
            return "coach";
        }
        coachCursor.close();

        // Check students table
        String studentQuery = "SELECT * FROM " + TABLE_STUDENTS + " WHERE "
                + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor studentCursor = db.rawQuery(studentQuery, new String[]{email, password});
        if (studentCursor.getCount() > 0) {
            studentCursor.close();
            return "student";
        }
        studentCursor.close();

        return null; // Authentication failed
    }

    // Get coach by email
    public Coach getCoachByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        Coach coach = null;
        if (cursor.moveToFirst()) {
            coach = new Coach();
            coach.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            coach.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            coach.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            coach.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            coach.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            coach.setExperienceYears(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPERIENCE_YEARS)));
            coach.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIALIZATION)));
            coach.setCertification(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CERTIFICATION)));
            coach.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }
        cursor.close();
        return coach;
    }

    // Get student by email
    public Student getStudentByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        Student student = null;
        if (cursor.moveToFirst()) {
            student = new Student();
            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
            student.setSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL)));
            student.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
            student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }
        cursor.close();
        return student;
    }

    // Update coach profile
    public boolean updateCoach(Coach coach) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, coach.getName());
        values.put(COLUMN_PHONE, coach.getPhone());
        values.put(COLUMN_EXPERIENCE_YEARS, coach.getExperienceYears());
        values.put(COLUMN_SPECIALIZATION, coach.getSpecialization());
        values.put(COLUMN_CERTIFICATION, coach.getCertification());

        int result = db.update(TABLE_COACHES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(coach.getId())});
        return result > 0;
    }

    // Update student profile
    public boolean updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, student.getName());
        values.put(COLUMN_PHONE, student.getPhone());
        values.put(COLUMN_AGE, student.getAge());
        values.put(COLUMN_SKILL_LEVEL, student.getSkillLevel());
        values.put(COLUMN_COACH_ID, student.getCoachId());

        int result = db.update(TABLE_STUDENTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        return result > 0;
    }

    // Get all coaches (for student to choose from)
    public List<Coach> getAllCoaches() {
        List<Coach> coaches = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACHES + " ORDER BY " + COLUMN_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Coach coach = new Coach();
                coach.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                coach.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                coach.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                coach.setExperienceYears(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPERIENCE_YEARS)));
                coach.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIALIZATION)));
                coaches.add(coach);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return coaches;
    }

    // Get coach name by ID
    public String getCoachNameById(int coachId) {
        if (coachId == 0) return "No coach assigned";

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_COACHES + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        String coachName = "Unknown coach";
        if (cursor.moveToFirst()) {
            coachName = cursor.getString(0);
        }
        cursor.close();
        return coachName;
    }

    // Get students assigned to a specific coach
    public List<Student> getStudentsByCoachId(int coachId) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " = ? ORDER BY " + COLUMN_NAME;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                student.setSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL)));
                student.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    // Get count of students assigned to a coach
    public int getStudentCountByCoachId(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get all students without assigned coaches
    public List<Student> getUnassignedStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " = 0 OR " + COLUMN_COACH_ID + " IS NULL ORDER BY " + COLUMN_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
                student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
                student.setSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL)));
                student.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    // Remove coach assignment from student
    public boolean removeCoachFromStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COACH_ID, 0);

        int result = db.update(TABLE_STUDENTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentId)});
        return result > 0;
    }

    // Get assignment statistics for dashboard
    public AssignmentStats getAssignmentStats() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Total coaches
        String coachQuery = "SELECT COUNT(*) FROM " + TABLE_COACHES;
        Cursor coachCursor = db.rawQuery(coachQuery, null);
        int totalCoaches = 0;
        if (coachCursor.moveToFirst()) {
            totalCoaches = coachCursor.getInt(0);
        }
        coachCursor.close();

        // Total students
        String studentQuery = "SELECT COUNT(*) FROM " + TABLE_STUDENTS;
        Cursor studentCursor = db.rawQuery(studentQuery, null);
        int totalStudents = 0;
        if (studentCursor.moveToFirst()) {
            totalStudents = studentCursor.getInt(0);
        }
        studentCursor.close();

        // Assigned students
        String assignedQuery = "SELECT COUNT(*) FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " > 0";
        Cursor assignedCursor = db.rawQuery(assignedQuery, null);
        int assignedStudents = 0;
        if (assignedCursor.moveToFirst()) {
            assignedStudents = assignedCursor.getInt(0);
        }
        assignedCursor.close();

        return new AssignmentStats(totalCoaches, totalStudents, assignedStudents);
    }

    // Inner class for assignment statistics
    public static class AssignmentStats {
        public final int totalCoaches;
        public final int totalStudents;
        public final int assignedStudents;
        public final int unassignedStudents;

        public AssignmentStats(int totalCoaches, int totalStudents, int assignedStudents) {
            this.totalCoaches = totalCoaches;
            this.totalStudents = totalStudents;
            this.assignedStudents = assignedStudents;
            this.unassignedStudents = totalStudents - assignedStudents;
        }
    }

    // VIDEO METHODS

    // Add video to database
    public long addVideo(Video video) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, video.getStudentId());
        values.put(COLUMN_COACH_ID, video.getCoachId());
        values.put(COLUMN_VIDEO_PATH, video.getVideoPath());
        values.put(COLUMN_VIDEO_TITLE, video.getVideoTitle());
        values.put(COLUMN_VIDEO_DESCRIPTION, video.getVideoDescription());
        values.put(COLUMN_VIDEO_DURATION, video.getVideoDuration());
        values.put(COLUMN_STATUS, video.getStatus());
        values.put(COLUMN_THUMBNAIL_PATH, video.getThumbnailPath());

        return db.insert(TABLE_VIDEOS, null, values);
    }

    // Get videos for a specific student
    public List<Video> getVideosByStudentId(int studentId) {
        List<Video> videos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_STUDENT_ID + " = ? ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                Video video = new Video();
                video.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                video.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                video.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_PATH)));
                video.setVideoTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TITLE)));
                video.setVideoDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DESCRIPTION)));
                video.setVideoDuration(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DURATION)));
                video.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_DATE)));
                video.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                video.setThumbnailPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL_PATH)));
                video.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                videos.add(video);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videos;
    }

    // Get videos for a specific coach
    public List<Video> getVideosByCoachId(int coachId) {
        List<Video> videos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_COACH_ID + " = ? ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                Video video = new Video();
                video.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                video.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                video.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_PATH)));
                video.setVideoTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TITLE)));
                video.setVideoDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DESCRIPTION)));
                video.setVideoDuration(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DURATION)));
                video.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_DATE)));
                video.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                video.setThumbnailPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL_PATH)));
                video.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                videos.add(video);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videos;
    }

    // Get video by ID
    public Video getVideoById(int videoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_VIDEO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId)});

        Video video = null;
        if (cursor.moveToFirst()) {
            video = new Video();
            video.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
            video.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            video.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
            video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_PATH)));
            video.setVideoTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TITLE)));
            video.setVideoDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DESCRIPTION)));
            video.setVideoDuration(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DURATION)));
            video.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_DATE)));
            video.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            video.setThumbnailPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL_PATH)));
            video.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }
        cursor.close();
        return video;
    }

    // Update video status
    public boolean updateVideoStatus(int videoId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        int result = db.update(TABLE_VIDEOS, values, COLUMN_VIDEO_ID + " = ?",
                new String[]{String.valueOf(videoId)});
        return result > 0;
    }

    // Get pending videos for coach
    public List<Video> getPendingVideosByCoachId(int coachId) {
        List<Video> videos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_COACH_ID + " = ? AND " + COLUMN_STATUS + " = 'pending' ORDER BY " + COLUMN_UPLOAD_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                Video video = new Video();
                video.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                video.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
                video.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_PATH)));
                video.setVideoTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_TITLE)));
                video.setVideoDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DESCRIPTION)));
                video.setVideoDuration(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_DURATION)));
                video.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPLOAD_DATE)));
                video.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                video.setThumbnailPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL_PATH)));
                video.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                videos.add(video);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videos;
    }

    // Get student name by ID
    public String getStudentNameById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        String studentName = "Unknown Student";
        if (cursor.moveToFirst()) {
            studentName = cursor.getString(0);
        }
        cursor.close();
        return studentName;
    }

}
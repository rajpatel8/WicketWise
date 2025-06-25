package com.lords.becomebetter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaMetadataRetriever;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CricketCoaching.db";
    private static final int DATABASE_VERSION = 5; // Updated version for new tables

    // Table names
    private static final String TABLE_COACHES = "coaches";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_COACH_CODES = "coach_codes";
    private static final String TABLE_VIDEOS = "videos";
    private static final String TABLE_ANNOTATIONS = "annotations";

    private static final String TABLE_COACH_REQUESTS = "coach_requests";

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

    private static final String TABLE_VIDEO_SUBMISSIONS = "video_submissions";
    private static final String TABLE_COACH_SELECTIONS = "coach_selections";
    private static final String TABLE_VIDEO_FEEDBACKS = "video_feedbacks";
    private static final String TABLE_VOICE_RECORDINGS = "voice_recordings";

    // Video Submissions columns
    private static final String COLUMN_SUBMISSION_ID = "submission_id";
    private static final String COLUMN_SUBMISSION_TITLE = "title";
    private static final String COLUMN_SUBMISSION_DESCRIPTION = "description";
    private static final String COLUMN_SUBMISSION_VIDEO_PATH = "video_path";
    private static final String COLUMN_SUBMISSION_STUDENT_ID = "student_id";
    private static final String COLUMN_SUBMISSION_DATE = "submission_date";
    private static final String COLUMN_SUBMISSION_STATUS = "status"; // 'pending', 'reviewed'

    // Coach Selections columns
    private static final String COLUMN_SELECTION_ID = "selection_id";
    private static final String COLUMN_SELECTED_COACH_ID = "selected_coach_id";
    private static final String COLUMN_SELECTION_STATUS = "selection_status"; // 'pending', 'completed'

    // Video Feedbacks columns
    private static final String COLUMN_FEEDBACK_ID = "feedback_id";
    private static final String COLUMN_FEEDBACK_COACH_ID = "feedback_coach_id";
    private static final String COLUMN_FEEDBACK_TEXT = "feedback_text";
    private static final String COLUMN_FEEDBACK_DATE = "feedback_date";
    private static final String COLUMN_FEEDBACK_ANNOTATION_DATA = "annotation_data";
    private static final String COLUMN_FEEDBACK_VOICE_RECORDING_PATH = "voice_recording_path";
    private static final String COLUMN_FEEDBACK_RATING = "rating";
    private static final String COLUMN_FEEDBACK_STATUS = "feedback_status";

    // Voice Recordings columns
    private static final String COLUMN_RECORDING_ID = "recording_id";
    private static final String COLUMN_RECORDING_PATH = "recording_path";
    private static final String COLUMN_RECORDING_DURATION = "duration";
    private static final String COLUMN_RECORDING_TIMESTAMP = "timestamp";
    private static final String COLUMN_RECORDING_TITLE = "recording_title";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Replace the onCreate method in DatabaseHelper.java

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

        // Create coach requests table
        String CREATE_COACH_REQUESTS_TABLE = "CREATE TABLE " + TABLE_COACH_REQUESTS + "("
                + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REQUEST_STUDENT_ID + " INTEGER NOT NULL,"
                + COLUMN_REQUEST_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_REQUEST_STATUS + " TEXT DEFAULT 'pending',"
                + COLUMN_REQUEST_MESSAGE + " TEXT,"
                + COLUMN_RESPONSE_MESSAGE + " TEXT,"
                + COLUMN_REQUEST_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_RESPONSE_DATE + " DATETIME,"
                + "FOREIGN KEY(" + COLUMN_REQUEST_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_REQUEST_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
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

        // === NEW VIDEO SUBMISSION SYSTEM TABLES ===

        // Video Submissions table
        String CREATE_VIDEO_SUBMISSIONS = "CREATE TABLE " + TABLE_VIDEO_SUBMISSIONS + "("
                + COLUMN_SUBMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_TITLE + " TEXT NOT NULL,"
                + COLUMN_SUBMISSION_DESCRIPTION + " TEXT,"
                + COLUMN_SUBMISSION_VIDEO_PATH + " TEXT NOT NULL,"
                + COLUMN_SUBMISSION_STUDENT_ID + " INTEGER NOT NULL,"
                + COLUMN_SUBMISSION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SUBMISSION_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + ")"
                + ")";

        // Coach Selections table
        String CREATE_COACH_SELECTIONS = "CREATE TABLE " + TABLE_COACH_SELECTIONS + "("
                + COLUMN_SELECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTED_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTION_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_ID + ") REFERENCES " + TABLE_VIDEO_SUBMISSIONS + "(" + COLUMN_SUBMISSION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_SELECTED_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Video Feedbacks table
        String CREATE_VIDEO_FEEDBACKS = "CREATE TABLE " + TABLE_VIDEO_FEEDBACKS + "("
                + COLUMN_FEEDBACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_ID + " INTEGER NOT NULL,"
                + COLUMN_FEEDBACK_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_FEEDBACK_TEXT + " TEXT,"
                + COLUMN_FEEDBACK_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_FEEDBACK_ANNOTATION_DATA + " TEXT,"
                + COLUMN_FEEDBACK_VOICE_RECORDING_PATH + " TEXT,"
                + COLUMN_FEEDBACK_RATING + " INTEGER DEFAULT 0,"
                + COLUMN_FEEDBACK_STATUS + " TEXT DEFAULT 'draft',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_ID + ") REFERENCES " + TABLE_VIDEO_SUBMISSIONS + "(" + COLUMN_SUBMISSION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_FEEDBACK_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Voice Recordings table
        String CREATE_VOICE_RECORDINGS = "CREATE TABLE " + TABLE_VOICE_RECORDINGS + "("
                + COLUMN_RECORDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FEEDBACK_ID + " INTEGER NOT NULL,"
                + COLUMN_RECORDING_PATH + " TEXT NOT NULL,"
                + COLUMN_RECORDING_DURATION + " INTEGER,"
                + COLUMN_RECORDING_TIMESTAMP + " INTEGER,"
                + COLUMN_RECORDING_TITLE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_FEEDBACK_ID + ") REFERENCES " + TABLE_VIDEO_FEEDBACKS + "(" + COLUMN_FEEDBACK_ID + ")"
                + ")";

        // Execute all CREATE TABLE statements
        db.execSQL(CREATE_COACHES_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_COACH_CODES_TABLE);
        db.execSQL(CREATE_COACH_REQUESTS_TABLE);
        db.execSQL(CREATE_VIDEOS_TABLE);
        db.execSQL(CREATE_ANNOTATIONS_TABLE);

        // NEW TABLES - These were missing!
        db.execSQL(CREATE_VIDEO_SUBMISSIONS);
        db.execSQL(CREATE_COACH_SELECTIONS);
        db.execSQL(CREATE_VIDEO_FEEDBACKS);
        db.execSQL(CREATE_VOICE_RECORDINGS);

        Log.d("DatabaseHelper", "All tables created successfully!");
    }

// Also, make sure you have these missing methods defined:

    public List<VideoFeedback> getVideoFeedbacks(int submissionId) {
        List<VideoFeedback> feedbacks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Check if table exists first
        if (!tableExists(db, TABLE_VIDEO_FEEDBACKS)) {
            Log.w("DatabaseHelper", "video_feedbacks table does not exist, returning empty list");
            return feedbacks;
        }

        String query = "SELECT vf.*, c.name as coach_name, c.email as coach_email " +
                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
                "INNER JOIN " + TABLE_COACHES + " c ON vf." + COLUMN_FEEDBACK_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE vf." + COLUMN_SUBMISSION_ID + " = ? " +
                "ORDER BY vf." + COLUMN_FEEDBACK_DATE + " DESC";

        try {
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId)});

            if (cursor.moveToFirst()) {
                do {
                    VideoFeedback feedback = new VideoFeedback();
                    feedback.setFeedbackId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ID)));
                    feedback.setSubmissionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_ID)));
                    feedback.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_COACH_ID)));
                    feedback.setFeedbackText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_TEXT)));
                    feedback.setFeedbackDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_DATE)));
                    feedback.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ANNOTATION_DATA)));
                    feedback.setVoiceRecordingPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_VOICE_RECORDING_PATH)));
                    feedback.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_RATING)));
                    feedback.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_STATUS)));

                    // Set coach info
                    feedback.setCoachName(cursor.getString(cursor.getColumnIndexOrThrow("coach_name")));
                    feedback.setCoachEmail(cursor.getString(cursor.getColumnIndexOrThrow("coach_email")));

                    feedbacks.add(feedback);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting video feedbacks", e);
        }

        return feedbacks;
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop all tables in correct order
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOTATIONS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_REQUESTS); // Add this line
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_CODES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);
//
//        // Recreate all tables
//        onCreate(db);
//    }

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

    // Add these methods to your existing DatabaseHelper.java class

// ANNOTATION METHODS

// Add annotation to database
public long addAnnotation(Annotation annotation) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(COLUMN_VIDEO_ID, annotation.getVideoId());
    values.put(COLUMN_COACH_ID, annotation.getCoachId());
    values.put(COLUMN_TIMESTAMP, annotation.getTimestamp());
    values.put(COLUMN_ANNOTATION_TYPE, annotation.getAnnotationType());
    values.put(COLUMN_ANNOTATION_DATA, annotation.getAnnotationData());
    values.put(COLUMN_X_POSITION, annotation.getXPosition());
    values.put(COLUMN_Y_POSITION, annotation.getYPosition());

    return db.insert(TABLE_ANNOTATIONS, null, values);
}

    // Get annotations for a specific video
    public List<Annotation> getAnnotationsByVideoId(int videoId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ANNOTATIONS + " WHERE " + COLUMN_VIDEO_ID + " = ? ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId)});

        if (cursor.moveToFirst()) {
            do {
                Annotation annotation = new Annotation();
                annotation.setAnnotationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_ID)));
                annotation.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                annotation.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                annotation.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                annotation.setAnnotationType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TYPE)));
                annotation.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_DATA)));
                annotation.setXPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_X_POSITION)));
                annotation.setYPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_Y_POSITION)));
                annotation.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                annotations.add(annotation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

    // Delete all annotations for a video
    public boolean deleteAnnotationsByVideoId(int videoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ANNOTATIONS, COLUMN_VIDEO_ID + " = ?",
                new String[]{String.valueOf(videoId)});
        return result > 0;
    }

    // Get annotations by coach for a specific video
    public List<Annotation> getAnnotationsByVideoAndCoach(int videoId, int coachId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ANNOTATIONS + " WHERE " + COLUMN_VIDEO_ID + " = ? AND " +
                COLUMN_COACH_ID + " = ? ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId), String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                Annotation annotation = new Annotation();
                annotation.setAnnotationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_ID)));
                annotation.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                annotation.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                annotation.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                annotation.setAnnotationType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TYPE)));
                annotation.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_DATA)));
                annotation.setXPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_X_POSITION)));
                annotation.setYPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_Y_POSITION)));
                annotation.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                annotations.add(annotation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

    // Helper class for videos with annotation count
//    public static class VideoWithAnnotations {
//        private Video video;
//        private int annotationCount;
//
//        public VideoWithAnnotations(Video video, int annotationCount) {
//            this.video = video;
//            this.annotationCount = annotationCount;
//        }
//
//        public Video getVideo() { return video; }
//        public int getAnnotationCount() { return annotationCount; }
//    }

    // Get all annotations by a coach

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

        int result = db.update(TABLE_COACHES, values,
                COLUMN_ID + " = ?",
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

        int result = db.update(TABLE_STUDENTS, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});

        return result > 0;
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

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " ORDER BY " + COLUMN_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = cursorToStudent(cursor);
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
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

    public boolean updateAnnotation(Annotation annotation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIMESTAMP, annotation.getTimestamp());
        values.put(COLUMN_ANNOTATION_TYPE, annotation.getAnnotationType());
        values.put(COLUMN_ANNOTATION_DATA, annotation.getAnnotationData());
        values.put(COLUMN_X_POSITION, annotation.getXPosition());
        values.put(COLUMN_Y_POSITION, annotation.getYPosition());

        int result = db.update(TABLE_ANNOTATIONS, values, COLUMN_ANNOTATION_ID + " = ?",
                new String[]{String.valueOf(annotation.getAnnotationId())});
        return result > 0;
    }

    // Delete single annotation
    public boolean deleteAnnotation(int annotationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ANNOTATIONS, COLUMN_ANNOTATION_ID + " = ?",
                new String[]{String.valueOf(annotationId)});
        return result > 0;
    }

    // Get annotation count for a video
    public int getAnnotationCountForVideo(int videoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_ANNOTATIONS + " WHERE " + COLUMN_VIDEO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get all annotations by a coach
    public List<Annotation> getAnnotationsByCoachId(int coachId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ANNOTATIONS + " WHERE " + COLUMN_COACH_ID + " = ? ORDER BY " +
                COLUMN_CREATED_AT + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                Annotation annotation = new Annotation();
                annotation.setAnnotationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_ID)));
                annotation.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID)));
                annotation.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
                annotation.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                annotation.setAnnotationType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TYPE)));
                annotation.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_DATA)));
                annotation.setXPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_X_POSITION)));
                annotation.setYPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_Y_POSITION)));
                annotation.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
                annotations.add(annotation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

// ENHANCED VIDEO METHODS

    // Get videos with annotation count
    public List<VideoWithAnnotations> getVideosWithAnnotationsByCoachId(int coachId) {
        List<VideoWithAnnotations> videosWithAnnotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT v.*, COUNT(a." + COLUMN_ANNOTATION_ID + ") as annotation_count " +
                "FROM " + TABLE_VIDEOS + " v " +
                "LEFT JOIN " + TABLE_ANNOTATIONS + " a ON v." + COLUMN_VIDEO_ID + " = a." + COLUMN_VIDEO_ID + " " +
                "WHERE v." + COLUMN_COACH_ID + " = ? " +
                "GROUP BY v." + COLUMN_VIDEO_ID + " " +
                "ORDER BY v." + COLUMN_UPLOAD_DATE + " DESC";

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

                int annotationCount = cursor.getInt(cursor.getColumnIndexOrThrow("annotation_count"));

                VideoWithAnnotations videoWithAnnotations = new VideoWithAnnotations(video, annotationCount);
                videosWithAnnotations.add(videoWithAnnotations);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videosWithAnnotations;
    }

    // Helper class for videos with annotation count
    public static class VideoWithAnnotations {
        private Video video;
        private int annotationCount;

        public VideoWithAnnotations(Video video, int annotationCount) {
            this.video = video;
            this.annotationCount = annotationCount;
        }

        public Video getVideo() { return video; }
        public int getAnnotationCount() { return annotationCount; }
    }

    // Add these methods to your existing DatabaseHelper.java class

// ============= COACH REQUEST SYSTEM METHODS =============

    // Table name for coach requests

    // Coach request table columns
    private static final String COLUMN_REQUEST_ID = "request_id";
    private static final String COLUMN_REQUEST_STUDENT_ID = "student_id";
    private static final String COLUMN_REQUEST_COACH_ID = "coach_id";
    private static final String COLUMN_REQUEST_STATUS = "status";
    private static final String COLUMN_REQUEST_MESSAGE = "message";
    private static final String COLUMN_RESPONSE_MESSAGE = "response_message";
    private static final String COLUMN_REQUEST_DATE = "request_date";
    private static final String COLUMN_RESPONSE_DATE = "response_date";

    // Add this to your onCreate method after other table creations:
    public void createCoachRequestsTable(SQLiteDatabase db) {
        String CREATE_COACH_REQUESTS_TABLE = "CREATE TABLE " + TABLE_COACH_REQUESTS + "("
                + COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REQUEST_STUDENT_ID + " INTEGER NOT NULL,"
                + COLUMN_REQUEST_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_REQUEST_STATUS + " TEXT DEFAULT 'pending',"
                + COLUMN_REQUEST_MESSAGE + " TEXT,"
                + COLUMN_RESPONSE_MESSAGE + " TEXT,"
                + COLUMN_REQUEST_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_RESPONSE_DATE + " DATETIME,"
                + "FOREIGN KEY(" + COLUMN_REQUEST_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_REQUEST_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        db.execSQL(CREATE_COACH_REQUESTS_TABLE);
    }

// Add this to your onUpgrade method:
// db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_REQUESTS);

// ============= COACH REQUEST CRUD OPERATIONS =============

    // Send coach request
//    public long sendCoachRequest(int studentId, int coachId, String message) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // Check if request already exists and is pending
//        if (hasExistingPendingRequest(studentId, coachId)) {
//            return -2; // Indicates duplicate request
//        }
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_REQUEST_STUDENT_ID, studentId);
//        values.put(COLUMN_REQUEST_COACH_ID, coachId);
//        values.put(COLUMN_REQUEST_MESSAGE, message);
//        values.put(COLUMN_REQUEST_STATUS, CoachRequest.STATUS_PENDING);
//
//        return db.insert(TABLE_COACH_REQUESTS, null, values);
//    }

    // Check if pending request exists
//    public boolean hasExistingPendingRequest(int studentId, int coachId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_COACH_REQUESTS + " WHERE "
//                + COLUMN_REQUEST_STUDENT_ID + " = ? AND "
//                + COLUMN_REQUEST_COACH_ID + " = ? AND "
//                + COLUMN_REQUEST_STATUS + " = ?";
//
//        Cursor cursor = db.rawQuery(query, new String[]{
//                String.valueOf(studentId),
//                String.valueOf(coachId),
//                CoachRequest.STATUS_PENDING
//        });
//
//        boolean exists = cursor.getCount() > 0;
//        cursor.close();
//        return exists;
//    }

    // Respond to coach request
    public boolean respondToCoachRequest(int requestId, String status, String responseMessage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STATUS, status);
        values.put(COLUMN_RESPONSE_MESSAGE, responseMessage);
        values.put(COLUMN_RESPONSE_DATE, getCurrentTimestamp());

        int result = db.update(TABLE_COACH_REQUESTS, values,
                COLUMN_REQUEST_ID + " = ?",
                new String[]{String.valueOf(requestId)});

        // If request is accepted, update student's coach_id
        if (result > 0 && CoachRequest.STATUS_ACCEPTED.equals(status)) {
            CoachRequest request = getCoachRequestById(requestId);
            if (request != null) {
                updateStudentCoach(request.getStudentId(), request.getCoachId());
            }
        }

        return result > 0;
    }

    // Update student's coach assignment
    public boolean updateStudentCoach(int studentId, int coachId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COACH_ID, coachId);

        int result = db.update(TABLE_STUDENTS, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentId)});

        return result > 0;
    }

    // Get coach request by ID
    public CoachRequest getCoachRequestById(int requestId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cr.*, " +
                "s." + COLUMN_NAME + " as student_name, " +
                "s." + COLUMN_EMAIL + " as student_email, " +
                "c." + COLUMN_NAME + " as coach_name, " +
                "c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_COACH_REQUESTS + " cr " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON cr." + COLUMN_REQUEST_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_COACHES + " c ON cr." + COLUMN_REQUEST_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE cr." + COLUMN_REQUEST_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(requestId)});

        CoachRequest request = null;
        if (cursor.moveToFirst()) {
            request = cursorToCoachRequest(cursor);
        }
        cursor.close();
        return request;
    }

    // Get pending requests for coach
    public List<CoachRequest> getPendingRequestsForCoach(int coachId) {
        return getCoachRequestsByStatus(coachId, CoachRequest.STATUS_PENDING);
    }

    // Get all requests for coach
    public List<CoachRequest> getAllRequestsForCoach(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cr.*, " +
                "s." + COLUMN_NAME + " as student_name, " +
                "s." + COLUMN_EMAIL + " as student_email, " +
                "c." + COLUMN_NAME + " as coach_name, " +
                "c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_COACH_REQUESTS + " cr " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON cr." + COLUMN_REQUEST_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_COACHES + " c ON cr." + COLUMN_REQUEST_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE cr." + COLUMN_REQUEST_COACH_ID + " = ? " +
                "ORDER BY cr." + COLUMN_REQUEST_DATE + " DESC";

        return executeCoachRequestQuery(query, new String[]{String.valueOf(coachId)});
    }

    // Get requests for student
    public List<CoachRequest> getRequestsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cr.*, " +
                "s." + COLUMN_NAME + " as student_name, " +
                "s." + COLUMN_EMAIL + " as student_email, " +
                "c." + COLUMN_NAME + " as coach_name, " +
                "c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_COACH_REQUESTS + " cr " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON cr." + COLUMN_REQUEST_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_COACHES + " c ON cr." + COLUMN_REQUEST_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE cr." + COLUMN_REQUEST_STUDENT_ID + " = ? " +
                "ORDER BY cr." + COLUMN_REQUEST_DATE + " DESC";

        return executeCoachRequestQuery(query, new String[]{String.valueOf(studentId)});
    }

    // Get coach requests by status
    public List<CoachRequest> getCoachRequestsByStatus(int coachId, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cr.*, " +
                "s." + COLUMN_NAME + " as student_name, " +
                "s." + COLUMN_EMAIL + " as student_email, " +
                "c." + COLUMN_NAME + " as coach_name, " +
                "c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_COACH_REQUESTS + " cr " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON cr." + COLUMN_REQUEST_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_COACHES + " c ON cr." + COLUMN_REQUEST_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE cr." + COLUMN_REQUEST_COACH_ID + " = ? AND cr." + COLUMN_REQUEST_STATUS + " = ? " +
                "ORDER BY cr." + COLUMN_REQUEST_DATE + " DESC";

        return executeCoachRequestQuery(query, new String[]{String.valueOf(coachId), status});
    }

    // Get students assigned to coach
    public List<Student> getStudentsForCoach(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " = ? " +
                "ORDER BY " + COLUMN_NAME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});
        List<Student> students = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Student student = cursorToStudent(cursor);
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    // Get coach for student
    public Coach getCoachForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.* FROM " + TABLE_COACHES + " c " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON c." + COLUMN_ID + " = s." + COLUMN_COACH_ID + " " +
                "WHERE s." + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        Coach coach = null;
        if (cursor.moveToFirst()) {
            coach = cursorToCoach(cursor);
        }
        cursor.close();
        return coach;
    }

    // Helper method to execute coach request queries
    private List<CoachRequest> executeCoachRequestQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        List<CoachRequest> requests = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                CoachRequest request = cursorToCoachRequest(cursor);
                requests.add(request);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requests;
    }

    // Convert cursor to CoachRequest object
    private CoachRequest cursorToCoachRequest(Cursor cursor) {
        CoachRequest request = new CoachRequest();

        request.setRequestId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));
        request.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_STUDENT_ID)));
        request.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_COACH_ID)));
        request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_STATUS)));
        request.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_MESSAGE)));
        request.setResponseMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSE_MESSAGE)));
        request.setRequestDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE)));
        request.setResponseDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSE_DATE)));

        // Set student and coach names if available
        try {
            request.setStudentName(cursor.getString(cursor.getColumnIndexOrThrow("student_name")));
            request.setStudentEmail(cursor.getString(cursor.getColumnIndexOrThrow("student_email")));
            request.setCoachName(cursor.getString(cursor.getColumnIndexOrThrow("coach_name")));
            request.setCoachEmail(cursor.getString(cursor.getColumnIndexOrThrow("coach_email")));
        } catch (Exception e) {
            // Columns might not exist in some queries
        }

        return request;
    }

    // Convert cursor to Student object
    private Student cursorToStudent(Cursor cursor) {
        Student student = new Student();

        student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
        student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
        student.setSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL)));
        student.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
        student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

        return student;
    }

    // Convert cursor to Coach object
    private Coach cursorToCoach(Cursor cursor) {
        Coach coach = new Coach();

        coach.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        coach.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
        coach.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        coach.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        coach.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
        coach.setExperienceYears(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPERIENCE_YEARS)));
        coach.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIALIZATION)));
        coach.setCertification(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CERTIFICATION)));
        coach.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

        return coach;
    }

    // Get current timestamp
//    private String getCurrentTimestamp() {
//        return String.valueOf(System.currentTimeMillis());
//    }

    // Get pending request count for coach
    public int getPendingRequestCount(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_COACH_REQUESTS + " WHERE " +
                COLUMN_REQUEST_COACH_ID + " = ? AND " + COLUMN_REQUEST_STATUS + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId), CoachRequest.STATUS_PENDING});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // Get student count for coach
    public int getStudentCount(int coachId) {
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

    // Add this method to your DatabaseHelper.java class

    // Get student by ID
//    public Student getStudentById(int studentId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";
//
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
//
//        Student student = null;
//        if (cursor.moveToFirst()) {
//            student = new Student();
//            student.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
//            student.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
//            student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
//            student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
//            student.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
//            student.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)));
//            student.setSkillLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SKILL_LEVEL)));
//            student.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COACH_ID)));
//            student.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
//        }
//        cursor.close();
//        return student;
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables in correct order
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICE_RECORDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_FEEDBACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_SELECTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_SUBMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOTATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);

        // Recreate all tables
        onCreate(db);
    }
// 5. COACH REQUEST METHODS

    // Send coach request
    public long sendCoachRequest(int studentId, int coachId, String message) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if request already exists and is pending
        if (hasExistingPendingRequest(studentId, coachId)) {
            return -2; // Indicates duplicate request
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STUDENT_ID, studentId);
        values.put(COLUMN_REQUEST_COACH_ID, coachId);
        values.put(COLUMN_REQUEST_MESSAGE, message);
        values.put(COLUMN_REQUEST_STATUS, "pending");

        return db.insert(TABLE_COACH_REQUESTS, null, values);
    }

    // Check if pending request exists
    public boolean hasExistingPendingRequest(int studentId, int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_REQUESTS + " WHERE "
                + COLUMN_REQUEST_STUDENT_ID + " = ? AND "
                + COLUMN_REQUEST_COACH_ID + " = ? AND "
                + COLUMN_REQUEST_STATUS + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(studentId),
                String.valueOf(coachId),
                "pending"
        });

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get current timestamp
    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    // Get student by ID method

    private void createVideoFeedbackTables(SQLiteDatabase db) {
        // Video Submissions table
        String CREATE_VIDEO_SUBMISSIONS = "CREATE TABLE " + TABLE_VIDEO_SUBMISSIONS + "("
                + COLUMN_SUBMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_TITLE + " TEXT NOT NULL,"
                + COLUMN_SUBMISSION_DESCRIPTION + " TEXT,"
                + COLUMN_SUBMISSION_VIDEO_PATH + " TEXT NOT NULL,"
                + COLUMN_SUBMISSION_STUDENT_ID + " INTEGER NOT NULL,"
                + COLUMN_SUBMISSION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_SUBMISSION_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_ID + ")"
                + ")";

        // Coach Selections table
        String CREATE_COACH_SELECTIONS = "CREATE TABLE " + TABLE_COACH_SELECTIONS + "("
                + COLUMN_SELECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTED_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTION_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_ID + ") REFERENCES " + TABLE_VIDEO_SUBMISSIONS + "(" + COLUMN_SUBMISSION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_SELECTED_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Video Feedbacks table
        String CREATE_VIDEO_FEEDBACKS = "CREATE TABLE " + TABLE_VIDEO_FEEDBACKS + "("
                + COLUMN_FEEDBACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_ID + " INTEGER NOT NULL,"
                + COLUMN_FEEDBACK_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_FEEDBACK_TEXT + " TEXT,"
                + COLUMN_FEEDBACK_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_FEEDBACK_ANNOTATION_DATA + " TEXT,"
                + COLUMN_FEEDBACK_VOICE_RECORDING_PATH + " TEXT,"
                + COLUMN_FEEDBACK_RATING + " INTEGER DEFAULT 0,"
                + COLUMN_FEEDBACK_STATUS + " TEXT DEFAULT 'draft',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_ID + ") REFERENCES " + TABLE_VIDEO_SUBMISSIONS + "(" + COLUMN_SUBMISSION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_FEEDBACK_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";

        // Voice Recordings table
        String CREATE_VOICE_RECORDINGS = "CREATE TABLE " + TABLE_VOICE_RECORDINGS + "("
                + COLUMN_RECORDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FEEDBACK_ID + " INTEGER NOT NULL,"
                + COLUMN_RECORDING_PATH + " TEXT NOT NULL,"
                + COLUMN_RECORDING_DURATION + " INTEGER,"
                + COLUMN_RECORDING_TIMESTAMP + " INTEGER,"
                + COLUMN_RECORDING_TITLE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_FEEDBACK_ID + ") REFERENCES " + TABLE_VIDEO_FEEDBACKS + "(" + COLUMN_FEEDBACK_ID + ")"
                + ")";

        db.execSQL(CREATE_VIDEO_SUBMISSIONS);
        db.execSQL(CREATE_COACH_SELECTIONS);
        db.execSQL(CREATE_VIDEO_FEEDBACKS);
        db.execSQL(CREATE_VOICE_RECORDINGS);
    }

// ============= VIDEO SUBMISSION METHODS =============

    public long createVideoSubmission(VideoSubmission submission) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBMISSION_TITLE, submission.getTitle());
        values.put(COLUMN_SUBMISSION_DESCRIPTION, submission.getDescription());
        values.put(COLUMN_SUBMISSION_VIDEO_PATH, submission.getVideoPath());
        values.put(COLUMN_SUBMISSION_STUDENT_ID, submission.getStudentId());

        long submissionId = db.insert(TABLE_VIDEO_SUBMISSIONS, null, values);

        if (submissionId > 0) {
            // Insert coach selections
            for (Integer coachId : submission.getSelectedCoachIds()) {
                ContentValues selectionValues = new ContentValues();
                selectionValues.put(COLUMN_SUBMISSION_ID, submissionId);
                selectionValues.put(COLUMN_SELECTED_COACH_ID, coachId);
                db.insert(TABLE_COACH_SELECTIONS, null, selectionValues);
            }
        }

        return submissionId;
    }

    public VideoSubmission getVideoSubmissionById(int submissionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT vs.*, s." + COLUMN_NAME + " as student_name " +
                "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON vs." + COLUMN_SUBMISSION_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "WHERE vs." + COLUMN_SUBMISSION_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId)});
        VideoSubmission submission = null;

        if (cursor.moveToFirst()) {
            submission = cursorToVideoSubmission(cursor);

            // Load selected coaches
            List<Integer> selectedCoachIds = getSelectedCoachIds(submissionId);
            submission.setSelectedCoachIds(selectedCoachIds);

            // Load feedbacks
            List<VideoFeedback> feedbacks = getVideoFeedbacks(submissionId);
            submission.setFeedbacks(feedbacks);
        }

        cursor.close();
        return submission;
    }

    public List<VideoSubmission> getVideoSubmissionsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT vs.*, s." + COLUMN_NAME + " as student_name " +
                "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON vs." + COLUMN_SUBMISSION_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "WHERE vs." + COLUMN_SUBMISSION_STUDENT_ID + " = ? " +
                "ORDER BY vs." + COLUMN_SUBMISSION_DATE + " DESC";

        return executeVideoSubmissionQuery(query, new String[]{String.valueOf(studentId)});
    }

    public List<VideoSubmission> getVideoSubmissionsForCoach(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT vs.*, s." + COLUMN_NAME + " as student_name " +
                "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON vs." + COLUMN_SUBMISSION_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_COACH_SELECTIONS + " cs ON vs." + COLUMN_SUBMISSION_ID + " = cs." + COLUMN_SUBMISSION_ID + " " +
                "WHERE cs." + COLUMN_SELECTED_COACH_ID + " = ? " +
                "ORDER BY vs." + COLUMN_SUBMISSION_DATE + " DESC";

        return executeVideoSubmissionQuery(query, new String[]{String.valueOf(coachId)});
    }

    private List<VideoSubmission> executeVideoSubmissionQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        List<VideoSubmission> submissions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                VideoSubmission submission = cursorToVideoSubmission(cursor);

                // Load selected coaches and feedbacks for each submission
                List<Integer> selectedCoachIds = getSelectedCoachIds(submission.getSubmissionId());
                submission.setSelectedCoachIds(selectedCoachIds);

                List<VideoFeedback> feedbacks = getVideoFeedbacks(submission.getSubmissionId());
                submission.setFeedbacks(feedbacks);

                submissions.add(submission);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return submissions;
    }

    private VideoSubmission cursorToVideoSubmission(Cursor cursor) {
        VideoSubmission submission = new VideoSubmission();

        submission.setSubmissionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_ID)));
        submission.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_TITLE)));
        submission.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_DESCRIPTION)));
        submission.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_VIDEO_PATH)));
        submission.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_STUDENT_ID)));
        submission.setSubmissionDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_DATE)));
        submission.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_STATUS)));

        int studentNameIndex = cursor.getColumnIndex("student_name");
        if (studentNameIndex >= 0) {
            submission.setStudentName(cursor.getString(studentNameIndex));
        }

        return submission;
    }

    private List<Integer> getSelectedCoachIds(int submissionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_SELECTED_COACH_ID + " FROM " + TABLE_COACH_SELECTIONS +
                " WHERE " + COLUMN_SUBMISSION_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId)});
        List<Integer> coachIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                coachIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return coachIds;
    }

// ============= VIDEO FEEDBACK METHODS =============

    public boolean saveVideoFeedback(VideoFeedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBMISSION_ID, feedback.getSubmissionId());
        values.put(COLUMN_FEEDBACK_COACH_ID, feedback.getCoachId());
        values.put(COLUMN_FEEDBACK_TEXT, feedback.getFeedbackText());
        values.put(COLUMN_FEEDBACK_ANNOTATION_DATA, feedback.getAnnotationData());
        values.put(COLUMN_FEEDBACK_VOICE_RECORDING_PATH, feedback.getVoiceRecordingPath());
        values.put(COLUMN_FEEDBACK_RATING, feedback.getRating());
        values.put(COLUMN_FEEDBACK_STATUS, feedback.getStatus());

        long feedbackId;
        if (feedback.getFeedbackId() > 0) {
            // Update existing feedback
            int result = db.update(TABLE_VIDEO_FEEDBACKS, values,
                    COLUMN_FEEDBACK_ID + " = ?",
                    new String[]{String.valueOf(feedback.getFeedbackId())});
            feedbackId = feedback.getFeedbackId();
        } else {
            // Insert new feedback
            feedbackId = db.insert(TABLE_VIDEO_FEEDBACKS, null, values);
            feedback.setFeedbackId((int) feedbackId);
        }

        if (feedbackId > 0) {
            // Save voice recordings
            saveVoiceRecordings(feedback.getFeedbackId(), feedback.getVoiceRecordings());
            return true;
        }

        return false;
    }

    public VideoFeedback getVideoFeedback(int submissionId, int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT vf.*, c." + COLUMN_NAME + " as coach_name, c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
                "INNER JOIN " + TABLE_COACHES + " c ON vf." + COLUMN_FEEDBACK_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE vf." + COLUMN_SUBMISSION_ID + " = ? AND vf." + COLUMN_FEEDBACK_COACH_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId), String.valueOf(coachId)});
        VideoFeedback feedback = null;

        if (cursor.moveToFirst()) {
            feedback = cursorToVideoFeedback(cursor);

            // Load voice recordings
            List<VoiceRecording> voiceRecordings = getVoiceRecordings(feedback.getFeedbackId());
            feedback.setVoiceRecordings(voiceRecordings);
        }

        cursor.close();
        return feedback;
    }

//    public List<VideoFeedback> getVideoFeedbacks(int submissionId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT vf.*, c." + COLUMN_NAME + " as coach_name, c." + COLUMN_EMAIL + " as coach_email " +
//                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
//                "INNER JOIN " + TABLE_COACHES + " c ON vf." + COLUMN_FEEDBACK_COACH_ID + " = c." + COLUMN_ID + " " +
//                "WHERE vf." + COLUMN_SUBMISSION_ID + " = ? " +
//                "ORDER BY vf." + COLUMN_FEEDBACK_DATE + " DESC";
//
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId)});
//        List<VideoFeedback> feedbacks = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            do {
//                VideoFeedback feedback = cursorToVideoFeedback(cursor);
//
//                // Load voice recordings for each feedback
//                List<VoiceRecording> voiceRecordings = getVoiceRecordings(feedback.getFeedbackId());
//                feedback.setVoiceRecordings(voiceRecordings);
//
//                feedbacks.add(feedback);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return feedbacks;
//    }

    private VideoFeedback cursorToVideoFeedback(Cursor cursor) {
        VideoFeedback feedback = new VideoFeedback();

        feedback.setFeedbackId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ID)));
        feedback.setSubmissionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_ID)));
        feedback.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_COACH_ID)));
        feedback.setFeedbackText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_TEXT)));
        feedback.setFeedbackDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_DATE)));
        feedback.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ANNOTATION_DATA)));
        feedback.setVoiceRecordingPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_VOICE_RECORDING_PATH)));
        feedback.setRating(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_RATING)));
        feedback.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_STATUS)));

        // Handle optional coach name and email columns
        try {
            int coachNameIndex = cursor.getColumnIndex("coach_name");
            if (coachNameIndex >= 0) {
                feedback.setCoachName(cursor.getString(coachNameIndex));
            }

            int coachEmailIndex = cursor.getColumnIndex("coach_email");
            if (coachEmailIndex >= 0) {
                feedback.setCoachEmail(cursor.getString(coachEmailIndex));
            }
        } catch (Exception e) {
            // Column might not exist in all queries, that's okay
        }

        return feedback;
    }

// ============= VOICE RECORDING METHODS =============

    private void saveVoiceRecordings(int feedbackId, List<VoiceRecording> recordings) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, delete existing recordings for this feedback
        db.delete(TABLE_VOICE_RECORDINGS, COLUMN_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedbackId)});

        // Insert new recordings
        for (VoiceRecording recording : recordings) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FEEDBACK_ID, feedbackId);
            values.put(COLUMN_RECORDING_PATH, recording.getRecordingPath());
            values.put(COLUMN_RECORDING_DURATION, recording.getDuration());
            values.put(COLUMN_RECORDING_TIMESTAMP, recording.getVideoTimestamp());
            values.put(COLUMN_RECORDING_TITLE, recording.getTitle());

            long recordingId = db.insert(TABLE_VOICE_RECORDINGS, null, values);
            recording.setRecordingId((int) recordingId);
        }
    }

    public List<VoiceRecording> getVoiceRecordings(int feedbackId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_VOICE_RECORDINGS +
                " WHERE " + COLUMN_FEEDBACK_ID + " = ? " +
                " ORDER BY " + COLUMN_RECORDING_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(feedbackId)});
        List<VoiceRecording> recordings = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                VoiceRecording recording = cursorToVoiceRecording(cursor);
                recordings.add(recording);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return recordings;
    }

    private VoiceRecording cursorToVoiceRecording(Cursor cursor) {
        VoiceRecording recording = new VoiceRecording();

        recording.setRecordingId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORDING_ID)));
        recording.setFeedbackId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_ID)));
        recording.setRecordingPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECORDING_PATH)));
        recording.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORDING_DURATION)));
        recording.setVideoTimestamp(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECORDING_TIMESTAMP)));
        recording.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECORDING_TITLE)));

        return recording;
    }

// ============= HELPER METHODS FOR COACH-STUDENT RELATIONSHIPS =============

//    public List<Coach> getCoachesForStudent(int studentId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT DISTINCT c.* FROM " + TABLE_COACHES + " c " +
//                "INNER JOIN " + TABLE_COACH_REQUESTS + " cr ON c." + COLUMN_ID + " = cr." + COLUMN_REQUEST_COACH_ID + " " +
//                "WHERE cr." + COLUMN_REQUEST_STUDENT_ID + " = ? AND cr." + COLUMN_REQUEST_STATUS + " = ?";
//
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), CoachRequest.STATUS_ACCEPTED});
//        List<Coach> coaches = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            do {
//                Coach coach = cursorToCoach(cursor);
//                coaches.add(coach);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return coaches;
//    }

    public int getVideoSubmissionCount(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_VIDEO_SUBMISSIONS +
                " WHERE " + COLUMN_SUBMISSION_STUDENT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public int getVideoFeedbackCount(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_VIDEO_FEEDBACKS +
                " WHERE " + COLUMN_FEEDBACK_COACH_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public int getPendingVideoFeedbackCount(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(DISTINCT vs." + COLUMN_SUBMISSION_ID + ") " +
                "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
                "INNER JOIN " + TABLE_COACH_SELECTIONS + " cs ON vs." + COLUMN_SUBMISSION_ID + " = cs." + COLUMN_SUBMISSION_ID + " " +
                "LEFT JOIN " + TABLE_VIDEO_FEEDBACKS + " vf ON vs." + COLUMN_SUBMISSION_ID + " = vf." + COLUMN_SUBMISSION_ID +
                " AND vf." + COLUMN_FEEDBACK_COACH_ID + " = ? " +
                "WHERE cs." + COLUMN_SELECTED_COACH_ID + " = ? AND vf." + COLUMN_FEEDBACK_ID + " IS NULL";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId), String.valueOf(coachId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    // ADD THIS METHOD to your DatabaseHelper.java class

    /**
     * Get all coaches that have accepted this student (coaches available for video feedback)
     * This method looks for coaches who have accepted requests from the student
     */

    // ADD THIS HELPER METHOD if it doesn't exist
//    public Student getStudentById(int studentId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";
//
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
//
//        Student student = null;
//        if (cursor.moveToFirst()) {
//            student = cursorToStudent(cursor);
//        }
//        cursor.close();
//        return student;
//    }

    public Student getStudentById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

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


    // ADD THIS HELPER METHOD if it doesn't exist
    public Coach getCoachById(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        Coach coach = null;
        if (cursor.moveToFirst()) {
            coach = cursorToCoach(cursor);
        }
        cursor.close();
        return coach;
    }

// ADD required import at the top of DatabaseHelper.java


    // ADD this method to your DatabaseHelper.java class
// This includes debugging to see what's happening


    /**
     * Get all coaches available for this student to upload videos to
     * First tries to find coaches through accepted requests, then falls back to direct assignment
     */
    public List<Coach> getCoachesForStudent(int studentId) {
        Log.d("DatabaseHelper", "=== DEBUG getCoachesForStudent ===");
        Log.d("DatabaseHelper", "Looking for coaches for student ID: " + studentId);

        SQLiteDatabase db = this.getReadableDatabase();
        List<Coach> coaches = new ArrayList<>();

        // Method 1: Check for accepted coach requests
        String query1 = "SELECT DISTINCT c.* FROM " + TABLE_COACHES + " c " +
                "INNER JOIN " + TABLE_COACH_REQUESTS + " cr ON c." + COLUMN_ID + " = cr." + COLUMN_REQUEST_COACH_ID + " " +
                "WHERE cr." + COLUMN_REQUEST_STUDENT_ID + " = ? AND cr." + COLUMN_REQUEST_STATUS + " = ? " +
                "ORDER BY c." + COLUMN_NAME + " ASC";

        Log.d("DatabaseHelper", "Query 1 (accepted requests): " + query1);
        Cursor cursor1 = db.rawQuery(query1, new String[]{String.valueOf(studentId), CoachRequest.STATUS_ACCEPTED});

        Log.d("DatabaseHelper", "Accepted requests cursor count: " + cursor1.getCount());

        if (cursor1.moveToFirst()) {
            do {
                Coach coach = cursorToCoach(cursor1);
                coaches.add(coach);
                Log.d("DatabaseHelper", "Found coach through accepted request: " + coach.getName() + " (ID: " + coach.getId() + ")");
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        // Method 2: Check for direct coach assignment (fallback)
        if (coaches.isEmpty()) {
            Log.d("DatabaseHelper", "No coaches found through requests, checking direct assignment...");

            String query2 = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";
            Cursor cursor2 = db.rawQuery(query2, new String[]{String.valueOf(studentId)});

            if (cursor2.moveToFirst()) {
                int coachId = cursor2.getInt(cursor2.getColumnIndexOrThrow(COLUMN_COACH_ID));
                Log.d("DatabaseHelper", "Student's direct coach ID: " + coachId);

                if (coachId > 0) {
                    String query3 = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_ID + " = ?";
                    Cursor cursor3 = db.rawQuery(query3, new String[]{String.valueOf(coachId)});

                    if (cursor3.moveToFirst()) {
                        Coach coach = cursorToCoach(cursor3);
                        coaches.add(coach);
                        Log.d("DatabaseHelper", "Found coach through direct assignment: " + coach.getName() + " (ID: " + coach.getId() + ")");
                    }
                    cursor3.close();
                }
            }
            cursor2.close();
        }

        // Method 3: FOR TESTING ONLY - Return all coaches if none found
        if (coaches.isEmpty()) {
            Log.d("DatabaseHelper", "No coaches found, returning all coaches for testing...");

            String query4 = "SELECT * FROM " + TABLE_COACHES + " ORDER BY " + COLUMN_NAME + " ASC";
            Cursor cursor4 = db.rawQuery(query4, null);

            Log.d("DatabaseHelper", "Total coaches in database: " + cursor4.getCount());

            if (cursor4.moveToFirst()) {
                do {
                    Coach coach = cursorToCoach(cursor4);
                    coaches.add(coach);
                    Log.d("DatabaseHelper", "Added coach for testing: " + coach.getName() + " (ID: " + coach.getId() + ")");
                } while (cursor4.moveToNext());
            }
            cursor4.close();
        }

        Log.d("DatabaseHelper", "Final result: " + coaches.size() + " coaches found for student " + studentId);
        Log.d("DatabaseHelper", "=== END DEBUG ===");

        return coaches;
    }

    // STEP 1: Add these methods to your DatabaseHelper.java file

    // Simple method to get all coaches (for testing)
//    public List<Coach> getCoachesForStudent(int studentId) {
//        android.util.Log.d("COACH_DEBUG", "🚀 getCoachesForStudent called!");
//
//        List<Coach> coaches = new ArrayList<>();
//
//        // Create test coach 1
//        Coach coach1 = new Coach();
//        coach1.setId(1);
//        coach1.setName("John Smith");
//        coach1.setEmail("john@cricket.com");
//        coach1.setPhone("555-0001");
//        coach1.setExperienceYears(10);
//        coach1.setSpecialization("Batting Coach");
//        coach1.setCertification("Level 3");
//        coaches.add(coach1);
//
//        // Create test coach 2
//        Coach coach2 = new Coach();
//        coach2.setId(2);
//        coach2.setName("Sarah Wilson");
//        coach2.setEmail("sarah@cricket.com");
//        coach2.setPhone("555-0002");
//        coach2.setExperienceYears(8);
//        coach2.setSpecialization("Bowling Coach");
//        coach2.setCertification("Level 2");
//        coaches.add(coach2);
//
//        // Create test coach 3
//        Coach coach3 = new Coach();
//        coach3.setId(3);
//        coach3.setName("Mike Johnson");
//        coach3.setEmail("mike@cricket.com");
//        coach3.setPhone("555-0003");
//        coach3.setExperienceYears(12);
//        coach3.setSpecialization("All-Round Coach");
//        coach3.setCertification("Level 4");
//        coaches.add(coach3);
//
//        android.util.Log.d("COACH_DEBUG", "✅ Returning " + coaches.size() + " test coaches");
//
//        return coaches;
//    }

    // Get all coaches from database
    public List<Coach> getAllCoaches() {
        android.util.Log.d("DatabaseHelper", "🏏 Getting all coaches from database...");

        SQLiteDatabase db = this.getReadableDatabase();
        List<Coach> coaches = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_COACHES + " ORDER BY " + COLUMN_NAME + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        android.util.Log.d("DatabaseHelper", "📈 Cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Coach coach = cursorToCoach(cursor);
                coaches.add(coach);
                android.util.Log.d("DatabaseHelper", "✅ Added coach: " + coach.getName() + " (ID: " + coach.getId() + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        android.util.Log.d("DatabaseHelper", "🎯 Returning " + coaches.size() + " coaches");
        return coaches;
    }

    // Create test coaches if database is empty
    public void ensureTestCoaches() {
        android.util.Log.d("DatabaseHelper", "🧪 Checking if test coaches need to be created...");

        List<Coach> existingCoaches = getAllCoaches();

        if (existingCoaches.isEmpty()) {
            android.util.Log.d("DatabaseHelper", "📝 No coaches found, creating test data...");
            createTestCoaches();
        } else {
            android.util.Log.d("DatabaseHelper", "✅ Found " + existingCoaches.size() + " existing coaches");
        }
    }

    // Create some test coaches
    private void createTestCoaches() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Coach 1
        ContentValues coach1 = new ContentValues();
        coach1.put(COLUMN_NAME, "Coach Smith");
        coach1.put(COLUMN_EMAIL, "smith@cricket.com");
        coach1.put(COLUMN_PASSWORD, "password123");
        coach1.put(COLUMN_PHONE, "555-0001");
        coach1.put(COLUMN_EXPERIENCE_YEARS, 10);
        coach1.put(COLUMN_SPECIALIZATION, "Batting");
        coach1.put(COLUMN_CERTIFICATION, "Level 3");
        coach1.put(COLUMN_CREATED_AT, getCurrentTimestamp());

        long id1 = db.insert(TABLE_COACHES, null, coach1);
        android.util.Log.d("DatabaseHelper", "✅ Created Coach Smith (ID: " + id1 + ")");

        // Coach 2
        ContentValues coach2 = new ContentValues();
        coach2.put(COLUMN_NAME, "Coach Johnson");
        coach2.put(COLUMN_EMAIL, "johnson@cricket.com");
        coach2.put(COLUMN_PASSWORD, "password123");
        coach2.put(COLUMN_PHONE, "555-0002");
        coach2.put(COLUMN_EXPERIENCE_YEARS, 8);
        coach2.put(COLUMN_SPECIALIZATION, "Bowling");
        coach2.put(COLUMN_CERTIFICATION, "Level 2");
        coach2.put(COLUMN_CREATED_AT, getCurrentTimestamp());

        long id2 = db.insert(TABLE_COACHES, null, coach2);
        android.util.Log.d("DatabaseHelper", "✅ Created Coach Johnson (ID: " + id2 + ")");

        // Coach 3
        ContentValues coach3 = new ContentValues();
        coach3.put(COLUMN_NAME, "Coach Wilson");
        coach3.put(COLUMN_EMAIL, "wilson@cricket.com");
        coach3.put(COLUMN_PASSWORD, "password123");
        coach3.put(COLUMN_PHONE, "555-0003");
        coach3.put(COLUMN_EXPERIENCE_YEARS, 12);
        coach3.put(COLUMN_SPECIALIZATION, "All-Round");
        coach3.put(COLUMN_CERTIFICATION, "Level 4");
        coach3.put(COLUMN_CREATED_AT, getCurrentTimestamp());

        long id3 = db.insert(TABLE_COACHES, null, coach3);
        android.util.Log.d("DatabaseHelper", "✅ Created Coach Wilson (ID: " + id3 + ")");
    }

    // Helper method for timestamp
//    private String getCurrentTimestamp() {
//        return String.valueOf(System.currentTimeMillis());
//    }

// STEP 2: Add this to EnhancedVideoUploadActivity.java onCreate method
// Replace your loadAvailableCoaches() method call with this:

/*
private void loadAvailableCoaches() {
    android.util.Log.d("EnhancedVideoUpload", "🎯 Starting loadAvailableCoaches...");

    // Ensure we have test coaches
    databaseHelper.ensureTestCoaches();

    // Get coaches for this student
    availableCoaches = databaseHelper.getCoachesForStudent(studentId);

    android.util.Log.d("EnhancedVideoUpload", "📊 Available coaches: " + availableCoaches.size());

    if (availableCoaches.isEmpty()) {
        android.util.Log.d("EnhancedVideoUpload", "❌ No coaches found - showing no coaches message");
        noCoachesText.setVisibility(View.VISIBLE);
        selectCoachesBtn.setEnabled(false);
        selectCoachesBtn.setText("No Coaches Available");
    } else {
        android.util.Log.d("EnhancedVideoUpload", "✅ Coaches found - enabling selection");
        noCoachesText.setVisibility(View.GONE);
        selectCoachesBtn.setEnabled(true);
        selectCoachesBtn.setText("Select Coaches (" + availableCoaches.size() + " available)");

        // Log each coach
        for (Coach coach : availableCoaches) {
            android.util.Log.d("EnhancedVideoUpload", "🏏 Coach: " + coach.getName() + " - " + coach.getSpecialization());
        }
    }
}
*/
public long getVideoDurationFromPath(String videoPath) {
    try {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release();
        return duration != null ? Long.parseLong(duration) : 0;
    } catch (Exception e) {
        Log.e("DatabaseHelper", "Error getting video duration", e);
        return 0;
    }
}
public List<Video> getVideoSubmissionsAsVideos(int coachId) {
    List<Video> videos = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();

    // Get video submissions for this coach through coach selections
    String query = "SELECT DISTINCT vs.*, s." + COLUMN_NAME + " as student_name " +
            "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
            "INNER JOIN " + TABLE_COACH_SELECTIONS + " cs ON vs." + COLUMN_SUBMISSION_ID + " = cs." + COLUMN_SUBMISSION_ID + " " +
            "INNER JOIN " + TABLE_STUDENTS + " s ON vs." + COLUMN_SUBMISSION_STUDENT_ID + " = s." + COLUMN_ID + " " +
            "WHERE cs." + COLUMN_SELECTED_COACH_ID + " = ? " +
            "ORDER BY vs." + COLUMN_SUBMISSION_DATE + " DESC";

    Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

    android.util.Log.d("DatabaseHelper", "🔍 Query for coach " + coachId + " submissions:");
    android.util.Log.d("DatabaseHelper", "📈 Found " + cursor.getCount() + " submissions");

    if (cursor.moveToFirst()) {
        do {
            // Convert VideoSubmission to Video object for compatibility
            Video video = new Video();

            // Map submission fields to video fields
            video.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_ID)));
            video.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_STUDENT_ID)));
            video.setCoachId(coachId); // Set current coach
            video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_VIDEO_PATH)));
            video.setVideoTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_TITLE)));
            video.setVideoDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_DESCRIPTION)));
            video.setUploadDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_DATE)));
            video.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_STATUS)));
            video.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUBMISSION_DATE)));

            // Set default values for missing fields
            video.setVideoDuration(0); // Will be calculated when needed
            video.setThumbnailPath(""); // Can be generated later

            videos.add(video);

            android.util.Log.d("DatabaseHelper", "✅ Added video: " + video.getVideoTitle() + " from student " + cursor.getString(cursor.getColumnIndexOrThrow("student_name")));
        } while (cursor.moveToNext());
    }
    cursor.close();

    android.util.Log.d("DatabaseHelper", "🎯 Returning " + videos.size() + " videos for coach");
    return videos;
}
}
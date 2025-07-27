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
    private static final int DATABASE_VERSION = 9;

    // Table names
    private static final String TABLE_COACHES = "coaches";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_COACH_CODES = "coach_codes";
    private static final String TABLE_VIDEOS = "videos";
    private static final String TABLE_ANNOTATIONS = "annotations";
    private static final String TABLE_COACH_REQUESTS = "coach_requests";
    private static final String TABLE_VIDEO_SUBMISSIONS = "video_submissions";
    private static final String TABLE_COACH_SELECTIONS = "coach_selections";
    private static final String TABLE_VIDEO_FEEDBACKS = "video_feedbacks";
    private static final String TABLE_VOICE_RECORDINGS = "voice_recordings";

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
    private static final String COLUMN_ANNOTATION_VIDEO_ID = "video_id";
    private static final String COLUMN_ANNOTATION_COACH_ID = "coach_id";
    private static final String COLUMN_ANNOTATION_TIMESTAMP = "timestamp";
    private static final String COLUMN_ANNOTATION_TYPE = "annotation_type";
    private static final String COLUMN_ANNOTATION_DATA = "annotation_data";
    private static final String COLUMN_ANNOTATION_X_POSITION = "x_position";
    private static final String COLUMN_ANNOTATION_Y_POSITION = "y_position";
    private static final String COLUMN_ANNOTATION_CREATED_AT = "created_at";
    private static final String COLUMN_ANNOTATION_FEEDBACK_ID = "feedback_id";

    // Video Submissions columns
    private static final String COLUMN_SUBMISSION_ID = "submission_id";
    private static final String COLUMN_SUBMISSION_TITLE = "title";
    private static final String COLUMN_SUBMISSION_DESCRIPTION = "description";
    private static final String COLUMN_SUBMISSION_VIDEO_PATH = "video_path";
    private static final String COLUMN_SUBMISSION_STUDENT_ID = "student_id";
    private static final String COLUMN_SUBMISSION_DATE = "submission_date";
    private static final String COLUMN_SUBMISSION_STATUS = "status";

    // Coach Selections columns
    private static final String COLUMN_SELECTION_ID = "selection_id";
    private static final String COLUMN_SELECTED_COACH_ID = "selected_coach_id";
    private static final String COLUMN_SELECTION_STATUS = "selection_status";

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

    // Coach request columns
    private static final String COLUMN_REQUEST_ID = "request_id";
    private static final String COLUMN_REQUEST_STUDENT_ID = "student_id";
    private static final String COLUMN_REQUEST_COACH_ID = "coach_id";
    private static final String COLUMN_REQUEST_STATUS = "status";
    private static final String COLUMN_REQUEST_MESSAGE = "message";
    private static final String COLUMN_RESPONSE_MESSAGE = "response_message";
    private static final String COLUMN_REQUEST_DATE = "request_date";
    private static final String COLUMN_RESPONSE_DATE = "response_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        createCoachesTable(db);
        createStudentsTable(db);
        createCoachCodesTable(db);
        createCoachRequestsTable(db);
        createVideosTable(db);
        createAnnotationsTable(db);
        createVideoSubmissionsTable(db);
        createCoachSelectionsTable(db);
        createVideoFeedbacksTable(db);
        createVoiceRecordingsTable(db);
        createMessagesTable(db);

        insertDefaultCoachCodes(db);
        Log.d("DatabaseHelper", "All tables created successfully!");
    }

    private void createCoachesTable(SQLiteDatabase db) {
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
        db.execSQL(CREATE_COACHES_TABLE);
    }

    private void createStudentsTable(SQLiteDatabase db) {
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
        db.execSQL(CREATE_STUDENTS_TABLE);
    }

    private void createCoachCodesTable(SQLiteDatabase db) {
        String CREATE_COACH_CODES_TABLE = "CREATE TABLE " + TABLE_COACH_CODES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CODE + " TEXT UNIQUE NOT NULL,"
                + COLUMN_IS_USED + " INTEGER DEFAULT 0,"
                + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_COACH_CODES_TABLE);
    }

    private void createCoachRequestsTable(SQLiteDatabase db) {
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

    private void createVideosTable(SQLiteDatabase db) {
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
        db.execSQL(CREATE_VIDEOS_TABLE);
    }

    private void createAnnotationsTable(SQLiteDatabase db) {
        String CREATE_ANNOTATIONS_TABLE = "CREATE TABLE " + TABLE_ANNOTATIONS + "("
                + COLUMN_ANNOTATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ANNOTATION_VIDEO_ID + " INTEGER,"
                + COLUMN_ANNOTATION_COACH_ID + " INTEGER,"
                + COLUMN_ANNOTATION_TYPE + " TEXT,"
                + COLUMN_ANNOTATION_TIMESTAMP + " INTEGER,"
                + COLUMN_ANNOTATION_DATA + " TEXT,"
                + COLUMN_ANNOTATION_X_POSITION + " REAL,"
                + COLUMN_ANNOTATION_Y_POSITION + " REAL,"
                + COLUMN_ANNOTATION_CREATED_AT + " TEXT,"
                + COLUMN_ANNOTATION_FEEDBACK_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ANNOTATION_VIDEO_ID + ") REFERENCES " + TABLE_VIDEOS + "(" + COLUMN_VIDEO_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ANNOTATION_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_ANNOTATION_FEEDBACK_ID + ") REFERENCES " + TABLE_VIDEO_FEEDBACKS + "(" + COLUMN_FEEDBACK_ID + ")"
                + ")";
        db.execSQL(CREATE_ANNOTATIONS_TABLE);
    }

    private void createVideoSubmissionsTable(SQLiteDatabase db) {
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
        db.execSQL(CREATE_VIDEO_SUBMISSIONS);
    }

    private void createCoachSelectionsTable(SQLiteDatabase db) {
        String CREATE_COACH_SELECTIONS = "CREATE TABLE " + TABLE_COACH_SELECTIONS + "("
                + COLUMN_SELECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SUBMISSION_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTED_COACH_ID + " INTEGER NOT NULL,"
                + COLUMN_SELECTION_STATUS + " TEXT DEFAULT 'pending',"
                + "FOREIGN KEY(" + COLUMN_SUBMISSION_ID + ") REFERENCES " + TABLE_VIDEO_SUBMISSIONS + "(" + COLUMN_SUBMISSION_ID + "),"
                + "FOREIGN KEY(" + COLUMN_SELECTED_COACH_ID + ") REFERENCES " + TABLE_COACHES + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_COACH_SELECTIONS);
    }

    private void createVideoFeedbacksTable(SQLiteDatabase db) {
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
        db.execSQL(CREATE_VIDEO_FEEDBACKS);
    }

    private void createVoiceRecordingsTable(SQLiteDatabase db) {
        String CREATE_VOICE_RECORDINGS = "CREATE TABLE " + TABLE_VOICE_RECORDINGS + "("
                + COLUMN_RECORDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FEEDBACK_ID + " INTEGER NOT NULL,"
                + COLUMN_RECORDING_PATH + " TEXT NOT NULL,"
                + COLUMN_RECORDING_DURATION + " INTEGER,"
                + COLUMN_RECORDING_TIMESTAMP + " INTEGER,"
                + COLUMN_RECORDING_TITLE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_FEEDBACK_ID + ") REFERENCES " + TABLE_VIDEO_FEEDBACKS + "(" + COLUMN_FEEDBACK_ID + ")"
                + ")";
        db.execSQL(CREATE_VOICE_RECORDINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_CODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ANNOTATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_SUBMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_SELECTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEO_FEEDBACKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOICE_RECORDINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    // ============= CORE AUTHENTICATION METHODS =============

    public boolean validateCoachCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_CODES + " WHERE "
                + COLUMN_CODE + " = ? AND " + COLUMN_IS_USED + " = 0";
        Cursor cursor = db.rawQuery(query, new String[]{code});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }



    public void markCoachCodeAsUsed(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_USED, 1);
        db.update(TABLE_COACH_CODES, values, COLUMN_CODE + " = ?", new String[]{code});
    }

    public long addCoach(Coach coach, String coachCode) {
        if (!validateCoachCode(coachCode)) {
            return -1;
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

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String coachQuery = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor coachCursor = db.rawQuery(coachQuery, new String[]{email});
        boolean existsInCoaches = coachCursor.getCount() > 0;
        coachCursor.close();

        if (existsInCoaches) return true;

        String studentQuery = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor studentCursor = db.rawQuery(studentQuery, new String[]{email});
        boolean existsInStudents = studentCursor.getCount() > 0;
        studentCursor.close();

        return existsInStudents;
    }

    public String authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String coachQuery = "SELECT * FROM " + TABLE_COACHES + " WHERE "
                + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor coachCursor = db.rawQuery(coachQuery, new String[]{email, password});
        if (coachCursor.getCount() > 0) {
            coachCursor.close();
            return "coach";
        }
        coachCursor.close();

        String studentQuery = "SELECT * FROM " + TABLE_STUDENTS + " WHERE "
                + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor studentCursor = db.rawQuery(studentQuery, new String[]{email, password});
        if (studentCursor.getCount() > 0) {
            studentCursor.close();
            return "student";
        }
        studentCursor.close();

        return null;
    }

    // ============= GET USER METHODS =============

    public Coach getCoachByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACHES + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        Coach coach = null;
        if (cursor.moveToFirst()) {
            coach = cursorToCoach(cursor);
        }
        cursor.close();
        return coach;
    }

    public Student getStudentByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
        cursor.close();
        return student;
    }

    public Student getStudentById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
        cursor.close();
        return student;
    }

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

    // ============= UPDATE METHODS =============

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

    // ============= NAME HELPER METHODS =============

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

    // ============= COACH/STUDENT RELATIONSHIP METHODS =============

    public List<Coach> getAllCoaches() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Coach> coaches = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_COACHES + " ORDER BY " + COLUMN_NAME + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                coaches.add(cursorToCoach(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return coaches;
    }

    public List<Student> getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " ORDER BY " + COLUMN_NAME + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                students.add(cursorToStudent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    public List<Student> getStudentsByCoachId(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + COLUMN_COACH_ID + " = ? ORDER BY " + COLUMN_NAME;
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                students.add(cursorToStudent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    public List<Student> getStudentsForCoach(int coachId) {
        return getStudentsByCoachId(coachId);
    }

    public List<Coach> getCoachesForStudent(int studentId) {
        // For now, return all coaches
        ensureTestCoaches();
        return getAllCoaches();
    }

    // ============= COACH REQUEST METHODS =============

    public boolean hasExistingPendingRequest(int studentId, int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COACH_REQUESTS + " WHERE "
                + COLUMN_REQUEST_STUDENT_ID + " = ? AND "
                + COLUMN_REQUEST_COACH_ID + " = ? AND "
                + COLUMN_REQUEST_STATUS + " = 'pending'";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(studentId), String.valueOf(coachId)
        });

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public long sendCoachRequest(int studentId, int coachId, String message) {
        if (hasExistingPendingRequest(studentId, coachId)) {
            return -2;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STUDENT_ID, studentId);
        values.put(COLUMN_REQUEST_COACH_ID, coachId);
        values.put(COLUMN_REQUEST_MESSAGE, message);
        values.put(COLUMN_REQUEST_STATUS, "pending");

        return db.insert(TABLE_COACH_REQUESTS, null, values);
    }

    public boolean respondToCoachRequest(int requestId, String status, String responseMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUEST_STATUS, status);
        values.put(COLUMN_RESPONSE_MESSAGE, responseMessage);
        values.put(COLUMN_RESPONSE_DATE, getCurrentTimestamp());

        int result = db.update(TABLE_COACH_REQUESTS, values,
                COLUMN_REQUEST_ID + " = ?", new String[]{String.valueOf(requestId)});

        if (result > 0 && "accepted".equals(status)) {
            CoachRequest request = getCoachRequestById(requestId);
            if (request != null) {
                updateStudentCoach(request.getStudentId(), request.getCoachId());
            }
        }
        return result > 0;
    }

    public boolean updateStudentCoach(int studentId, int coachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COACH_ID, coachId);
        int result = db.update(TABLE_STUDENTS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentId)});
        return result > 0;
    }

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

    public List<CoachRequest> getPendingRequestsForCoach(int coachId) {
        return getCoachRequestsByStatus(coachId, "pending");
    }

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

    public int getPendingRequestCount(int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_COACH_REQUESTS + " WHERE " +
                COLUMN_REQUEST_COACH_ID + " = ? AND " + COLUMN_REQUEST_STATUS + " = 'pending'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(coachId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ============= VIDEO METHODS =============

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

    public List<Video> getVideosByStudentId(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Video> videos = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_VIDEOS + " WHERE " + COLUMN_STUDENT_ID + " = ? ORDER BY " + COLUMN_UPLOAD_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            do {
                videos.add(cursorToVideo(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videos;
    }

    // ============= VIDEO SUBMISSION METHODS =============

    public long createVideoSubmission(VideoSubmission submission) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SUBMISSION_TITLE, submission.getTitle());
            values.put(COLUMN_SUBMISSION_DESCRIPTION, submission.getDescription());
            values.put(COLUMN_SUBMISSION_VIDEO_PATH, submission.getVideoPath());
            values.put(COLUMN_SUBMISSION_STUDENT_ID, submission.getStudentId());

            long submissionId = db.insert(TABLE_VIDEO_SUBMISSIONS, null, values);

            if (submissionId > 0 && submission.getSelectedCoachIds() != null) {
                for (Integer coachId : submission.getSelectedCoachIds()) {
                    ContentValues selectionValues = new ContentValues();
                    selectionValues.put(COLUMN_SUBMISSION_ID, submissionId);
                    selectionValues.put(COLUMN_SELECTED_COACH_ID, coachId);
                    db.insert(TABLE_COACH_SELECTIONS, null, selectionValues);
                }
            }

            db.setTransactionSuccessful();
            return submissionId;

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating video submission", e);
            return -1;
        } finally {
            db.endTransaction();
        }
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
            submission.setSelectedCoachIds(getSelectedCoachIds(submissionId));
            submission.setFeedbacks(getVideoFeedbacks(submissionId));
        }

        cursor.close();
        return submission;
    }

    public List<VideoSubmission> getSubmissionsWithFeedbackForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT vs.*, s." + COLUMN_NAME + " as student_name " +
                "FROM " + TABLE_VIDEO_SUBMISSIONS + " vs " +
                "INNER JOIN " + TABLE_STUDENTS + " s ON vs." + COLUMN_SUBMISSION_STUDENT_ID + " = s." + COLUMN_ID + " " +
                "INNER JOIN " + TABLE_VIDEO_FEEDBACKS + " vf ON vs." + COLUMN_SUBMISSION_ID + " = vf." + COLUMN_SUBMISSION_ID + " " +
                "WHERE vs." + COLUMN_SUBMISSION_STUDENT_ID + " = ? " +
                "ORDER BY vs." + COLUMN_SUBMISSION_DATE + " DESC";

        return executeVideoSubmissionQuery(query, new String[]{String.valueOf(studentId)});
    }

    public void updateVideoSubmissionStatus(int submissionId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBMISSION_STATUS, status);
        db.update(TABLE_VIDEO_SUBMISSIONS, values,
                COLUMN_SUBMISSION_ID + " = ?", new String[]{String.valueOf(submissionId)});
    }

    // ============= VIDEO FEEDBACK METHODS =============

    public boolean hasFeedbackForSubmission(int submissionId, int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_VIDEO_FEEDBACKS)) return false;

        String query = "SELECT COUNT(*) FROM " + TABLE_VIDEO_FEEDBACKS +
                " WHERE " + COLUMN_SUBMISSION_ID + " = ? AND " + COLUMN_FEEDBACK_COACH_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId), String.valueOf(coachId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

    public VideoFeedback getVideoFeedback(int submissionId, int coachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_VIDEO_FEEDBACKS)) return null;

        String query = "SELECT vf.*, c." + COLUMN_NAME + " as coach_name, c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
                "INNER JOIN " + TABLE_COACHES + " c ON vf." + COLUMN_FEEDBACK_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE vf." + COLUMN_SUBMISSION_ID + " = ? AND vf." + COLUMN_FEEDBACK_COACH_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId), String.valueOf(coachId)});
        VideoFeedback feedback = null;

        if (cursor.moveToFirst()) {
            feedback = cursorToVideoFeedback(cursor);
            feedback.setVoiceRecordings(getVoiceRecordings(feedback.getFeedbackId()));
        }

        cursor.close();
        return feedback;
    }

    public List<VideoFeedback> getVideoFeedbacks(int submissionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<VideoFeedback> feedbacks = new ArrayList<>();
        if (!tableExists(db, TABLE_VIDEO_FEEDBACKS)) return feedbacks;

        String query = "SELECT vf.*, c." + COLUMN_NAME + " as coach_name, c." + COLUMN_EMAIL + " as coach_email " +
                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
                "INNER JOIN " + TABLE_COACHES + " c ON vf." + COLUMN_FEEDBACK_COACH_ID + " = c." + COLUMN_ID + " " +
                "WHERE vf." + COLUMN_SUBMISSION_ID + " = ? " +
                "ORDER BY vf." + COLUMN_FEEDBACK_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(submissionId)});

        if (cursor.moveToFirst()) {
            do {
                VideoFeedback feedback = cursorToVideoFeedback(cursor);
                feedback.setVoiceRecordings(getVoiceRecordings(feedback.getFeedbackId()));
                feedbacks.add(feedback);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return feedbacks;
    }

    public boolean saveVideoFeedback(VideoFeedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SUBMISSION_ID, feedback.getSubmissionId());
            values.put(COLUMN_FEEDBACK_COACH_ID, feedback.getCoachId());
            values.put(COLUMN_FEEDBACK_TEXT, feedback.getFeedbackText());
            values.put(COLUMN_FEEDBACK_DATE, getCurrentTimestamp());
            values.put(COLUMN_FEEDBACK_ANNOTATION_DATA, feedback.getAnnotationData());
            values.put(COLUMN_FEEDBACK_VOICE_RECORDING_PATH, feedback.getVoiceRecordingPath());
            values.put(COLUMN_FEEDBACK_RATING, feedback.getRating());
            values.put(COLUMN_FEEDBACK_STATUS, feedback.getStatus());

            long feedbackId;
            if (feedback.getFeedbackId() > 0) {
                int rowsUpdated = db.update(TABLE_VIDEO_FEEDBACKS, values,
                        COLUMN_FEEDBACK_ID + " = ?",
                        new String[]{String.valueOf(feedback.getFeedbackId())});
                feedbackId = feedback.getFeedbackId();
            } else {
                feedbackId = db.insert(TABLE_VIDEO_FEEDBACKS, null, values);
                feedback.setFeedbackId((int)feedbackId);
            }

            if (feedbackId > 0) {
                linkAnnotationsToFeedback((int)feedbackId, feedback.getSubmissionId(), feedback.getCoachId());
                saveVoiceRecordings((int)feedbackId, feedback.getVoiceRecordings());
                updateVideoSubmissionStatus(feedback.getSubmissionId(), "reviewed");
                db.setTransactionSuccessful();
                return true;
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error saving video feedback", e);
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public long addVideoFeedback(VideoFeedback feedback) {
        return saveVideoFeedback(feedback) ? 1 : -1;
    }

    // ============= ANNOTATION METHODS =============

    public long addAnnotation(Annotation annotation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANNOTATION_VIDEO_ID, annotation.getVideoId());
        values.put(COLUMN_ANNOTATION_COACH_ID, annotation.getCoachId());
        values.put(COLUMN_ANNOTATION_TIMESTAMP, annotation.getTimestamp());
        values.put(COLUMN_ANNOTATION_TYPE, annotation.getAnnotationType());
        values.put(COLUMN_ANNOTATION_DATA, annotation.getAnnotationData());
        values.put(COLUMN_ANNOTATION_X_POSITION, annotation.getXPosition());
        values.put(COLUMN_ANNOTATION_Y_POSITION, annotation.getYPosition());
        return db.insert(TABLE_ANNOTATIONS, null, values);
    }

    public List<Annotation> getAnnotationsByVideoId(int videoId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ANNOTATIONS + " WHERE " + COLUMN_ANNOTATION_VIDEO_ID + " = ? ORDER BY " + COLUMN_ANNOTATION_TIMESTAMP + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId)});

        if (cursor.moveToFirst()) {
            do {
                annotations.add(cursorToAnnotation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

    public List<Annotation> getAnnotationsByVideoAndCoach(int videoId, int coachId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_ANNOTATIONS +
                " WHERE " + COLUMN_ANNOTATION_VIDEO_ID + " = ? " +
                " AND " + COLUMN_ANNOTATION_COACH_ID + " = ? " +
                " ORDER BY " + COLUMN_ANNOTATION_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(videoId), String.valueOf(coachId)});

        if (cursor.moveToFirst()) {
            do {
                annotations.add(cursorToAnnotation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

    public boolean deleteAnnotationsByVideoAndCoach(int videoId, int coachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ANNOTATIONS,
                COLUMN_ANNOTATION_VIDEO_ID + " = ? AND " + COLUMN_ANNOTATION_COACH_ID + " = ?",
                new String[]{String.valueOf(videoId), String.valueOf(coachId)});
        return result > 0;
    }

    public List<Annotation> getAnnotationsForFeedback(int feedbackId) {
        List<Annotation> annotations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ANNOTATIONS +
                " WHERE " + COLUMN_ANNOTATION_FEEDBACK_ID + " = ? " +
                " ORDER BY " + COLUMN_ANNOTATION_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(feedbackId)});

        if (cursor.moveToFirst()) {
            do {
                annotations.add(cursorToAnnotation(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return annotations;
    }

    // ============= VOICE RECORDING METHODS =============

    public List<VoiceRecording> getVoiceRecordings(int feedbackId) {
        List<VoiceRecording> recordings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_VOICE_RECORDINGS)) return recordings;

        String query = "SELECT * FROM " + TABLE_VOICE_RECORDINGS +
                " WHERE " + COLUMN_FEEDBACK_ID + " = ? " +
                "ORDER BY " + COLUMN_RECORDING_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(feedbackId)});

        if (cursor.moveToFirst()) {
            do {
                recordings.add(cursorToVoiceRecording(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return recordings;
    }

    // ============= STATISTICS METHODS =============

    public int getFeedbackCountForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_VIDEO_FEEDBACKS)) return 0;

        String query = "SELECT COUNT(DISTINCT vf." + COLUMN_FEEDBACK_ID + ") " +
                "FROM " + TABLE_VIDEO_FEEDBACKS + " vf " +
                "INNER JOIN " + TABLE_VIDEO_SUBMISSIONS + " vs ON vf." + COLUMN_SUBMISSION_ID + " = vs." + COLUMN_SUBMISSION_ID + " " +
                "WHERE vs." + COLUMN_SUBMISSION_STUDENT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getPendingSubmissionsCount(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_VIDEO_SUBMISSIONS)) return 0;

        String query = "SELECT COUNT(*) FROM " + TABLE_VIDEO_SUBMISSIONS +
                " WHERE " + COLUMN_SUBMISSION_STUDENT_ID + " = ? AND " + COLUMN_SUBMISSION_STATUS + " = 'pending'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ============= HELPER METHODS =============

    public void insertDefaultCoachCodes(SQLiteDatabase db) {
        String[] defaultCodes = {"123456", "789012", "345678", "901234", "567890"};
        for (String code : defaultCodes) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CODE, code);
            values.put(COLUMN_IS_USED, 0);
            db.insert(TABLE_COACH_CODES, null, values);
        }
    }

    public void ensureTestCoaches() {
        List<Coach> existingCoaches = getAllCoaches();
        if (existingCoaches.isEmpty()) {
            createTestCoaches();
        }
    }

    private void createTestCoaches() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues coach1 = new ContentValues();
        coach1.put(COLUMN_NAME, "Coach Smith");
        coach1.put(COLUMN_EMAIL, "smith@cricket.com");
        coach1.put(COLUMN_PASSWORD, "password123");
        coach1.put(COLUMN_PHONE, "555-0001");
        coach1.put(COLUMN_EXPERIENCE_YEARS, 10);
        coach1.put(COLUMN_SPECIALIZATION, "Batting");
        coach1.put(COLUMN_CERTIFICATION, "Level 3");
        db.insert(TABLE_COACHES, null, coach1);

        ContentValues coach2 = new ContentValues();
        coach2.put(COLUMN_NAME, "Coach Johnson");
        coach2.put(COLUMN_EMAIL, "johnson@cricket.com");
        coach2.put(COLUMN_PASSWORD, "password123");
        coach2.put(COLUMN_PHONE, "555-0002");
        coach2.put(COLUMN_EXPERIENCE_YEARS, 8);
        coach2.put(COLUMN_SPECIALIZATION, "Bowling");
        coach2.put(COLUMN_CERTIFICATION, "Level 2");
        db.insert(TABLE_COACHES, null, coach2);

        ContentValues coach3 = new ContentValues();
        coach3.put(COLUMN_NAME, "Coach Wilson");
        coach3.put(COLUMN_EMAIL, "wilson@cricket.com");
        coach3.put(COLUMN_PASSWORD, "password123");
        coach3.put(COLUMN_PHONE, "555-0003");
        coach3.put(COLUMN_EXPERIENCE_YEARS, 12);
        coach3.put(COLUMN_SPECIALIZATION, "All-Round");
        coach3.put(COLUMN_CERTIFICATION, "Level 4");
        db.insert(TABLE_COACHES, null, coach3);
    }

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

    private String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void linkAnnotationsToFeedback(int feedbackId, int submissionId, int coachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String updateSql = "UPDATE " + TABLE_ANNOTATIONS +
                    " SET " + COLUMN_ANNOTATION_FEEDBACK_ID + " = ? " +
                    " WHERE " + COLUMN_ANNOTATION_VIDEO_ID + " = ? " +
                    " AND " + COLUMN_ANNOTATION_COACH_ID + " = ? " +
                    " AND (" + COLUMN_ANNOTATION_FEEDBACK_ID + " IS NULL OR " + COLUMN_ANNOTATION_FEEDBACK_ID + " = 0)";

            db.execSQL(updateSql, new Object[]{feedbackId, submissionId, coachId});
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error linking annotations to feedback", e);
        }
    }

    private void saveVoiceRecordings(int feedbackId, List<VoiceRecording> recordings) {
        if (recordings == null || recordings.isEmpty()) return;

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VOICE_RECORDINGS, COLUMN_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedbackId)});

        for (VoiceRecording recording : recordings) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_FEEDBACK_ID, feedbackId);
            values.put(COLUMN_RECORDING_PATH, recording.getRecordingPath());
            values.put(COLUMN_RECORDING_DURATION, recording.getDuration());
            values.put(COLUMN_RECORDING_TIMESTAMP, recording.getVideoTimestamp());
            values.put(COLUMN_RECORDING_TITLE, recording.getTitle());
            db.insert(TABLE_VOICE_RECORDINGS, null, values);
        }
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

    private List<VideoSubmission> executeVideoSubmissionQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        List<VideoSubmission> submissions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                VideoSubmission submission = cursorToVideoSubmission(cursor);
                submission.setSelectedCoachIds(getSelectedCoachIds(submission.getSubmissionId()));
                submission.setFeedbacks(getVideoFeedbacks(submission.getSubmissionId()));
                submissions.add(submission);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return submissions;
    }

    private List<CoachRequest> executeCoachRequestQuery(String query, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);
        List<CoachRequest> requests = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                requests.add(cursorToCoachRequest(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requests;
    }

    // ============= CURSOR TO OBJECT METHODS =============

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

    private Video cursorToVideo(Cursor cursor) {
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
        return video;
    }

    private Annotation cursorToAnnotation(Cursor cursor) {
        Annotation annotation = new Annotation();
        annotation.setAnnotationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_ID)));
        annotation.setVideoId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_VIDEO_ID)));
        annotation.setCoachId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_COACH_ID)));
        annotation.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TIMESTAMP)));
        annotation.setAnnotationType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_TYPE)));
        annotation.setAnnotationData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_DATA)));
        annotation.setXPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_X_POSITION)));
        annotation.setYPosition(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_Y_POSITION)));
        annotation.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOTATION_CREATED_AT)));

        try {
            int feedbackIdIndex = cursor.getColumnIndex(COLUMN_ANNOTATION_FEEDBACK_ID);
            if (feedbackIdIndex >= 0) {
                annotation.setFeedbackId(cursor.getInt(feedbackIdIndex));
            }
        } catch (Exception e) {
            // Column might not exist
        }
        return annotation;
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
            // Columns might not exist
        }
        return feedback;
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

    // Add these constants to your DatabaseHelper.java class

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_MESSAGE_ID = "message_id";
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_RECEIVER_ID = "receiver_id";
    private static final String COLUMN_SENDER_TYPE = "sender_type"; // "student" or "coach"
    private static final String COLUMN_MESSAGE_TEXT = "message_text";
    private static final String COLUMN_MESSAGE_TIMESTAMP = "message_timestamp";
    private static final String COLUMN_IS_READ = "is_read";

    // Add this to your onCreate method in DatabaseHelper.java
    private void createMessagesTable(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SENDER_ID + " INTEGER NOT NULL,"
                + COLUMN_RECEIVER_ID + " INTEGER NOT NULL,"
                + COLUMN_SENDER_TYPE + " TEXT NOT NULL,"
                + COLUMN_MESSAGE_TEXT + " TEXT NOT NULL,"
                + COLUMN_MESSAGE_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_IS_READ + " INTEGER DEFAULT 0"
                + ")";

        db.execSQL(CREATE_MESSAGES_TABLE);
    }

// Add these CRUD methods to DatabaseHelper.java

    // Send a message
    public long sendMessage(int senderId, int receiverId, String senderType, String messageText) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_RECEIVER_ID, receiverId);
        values.put(COLUMN_SENDER_TYPE, senderType);
        values.put(COLUMN_MESSAGE_TEXT, messageText);

        return db.insert(TABLE_MESSAGES, null, values);
    }

    // Get all messages between student and coach
    public List<Message> getMessagesBetweenUsers(int studentId, int coachId) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR " +
                "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) " +
                "ORDER BY " + COLUMN_MESSAGE_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(studentId), String.valueOf(coachId),
                String.valueOf(coachId), String.valueOf(studentId)
        });

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setMessageId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
                message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID)));
                message.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID)));
                message.setSenderType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER_TYPE)));
                message.setMessageText(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_TIMESTAMP)));
                message.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);

                messages.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return messages;
    }

    // Mark messages as read
    public void markMessagesAsRead(int senderId, int receiverId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);

        db.update(TABLE_MESSAGES, values,
                COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ? AND " + COLUMN_IS_READ + " = 0",
                new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
    }

    // Get unread message count
    public int getUnreadMessageCount(int receiverId, int senderId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_RECEIVER_ID + " = ? AND " + COLUMN_SENDER_ID + " = ? AND " + COLUMN_IS_READ + " = 0";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(receiverId), String.valueOf(senderId)
        });

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

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
}
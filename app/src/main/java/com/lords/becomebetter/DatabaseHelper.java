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
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_COACHES = "coaches";
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_COACH_CODES = "coach_codes";

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

        db.execSQL(CREATE_COACHES_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_COACH_CODES_TABLE);

        // Insert some default coach codes
        insertDefaultCoachCodes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACHES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COACH_CODES);
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
}
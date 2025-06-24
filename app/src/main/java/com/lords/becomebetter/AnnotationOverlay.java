package com.lords.becomebetter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AnnotationOverlay extends View {

    private static final String TAG = "AnnotationOverlay";

    private Paint drawPaint;
    private Path currentPath;
    private List<AnnotationDrawing> annotations;
    private boolean isDrawingEnabled = true;
    private VideoPlayerActivity parentActivity; // Reference to get current video time
    private long currentVideoPosition = 0;

    // Inner class to hold annotation drawing data
    public static class AnnotationDrawing {
        Path path;
        Paint paint;
        long timestamp;
        String pathData; // Serialized path data
        List<Point> points; // Store path points for serialization

        public AnnotationDrawing(Path path, Paint paint, long timestamp, String pathData) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
            this.timestamp = timestamp;
            this.pathData = pathData;
            this.points = new ArrayList<>();
        }

        public AnnotationDrawing(Path path, Paint paint, long timestamp, List<Point> points) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
            this.timestamp = timestamp;
            this.points = new ArrayList<>(points);
            this.pathData = pointsToString(points);
        }

        private String pointsToString(List<Point> points) {
            try {
                JSONArray jsonArray = new JSONArray();
                for (Point point : points) {
                    JSONObject pointObj = new JSONObject();
                    pointObj.put("x", point.x);
                    pointObj.put("y", point.y);
                    pointObj.put("action", point.action);
                    jsonArray.put(pointObj);
                }
                return jsonArray.toString();
            } catch (JSONException e) {
                Log.e(TAG, "Error converting points to string", e);
                return "";
            }
        }
    }

    // Point class to store drawing coordinates
    public static class Point {
        float x, y;
        int action; // MotionEvent action (DOWN, MOVE, UP)

        public Point(float x, float y, int action) {
            this.x = x;
            this.y = y;
            this.action = action;
        }
    }

    private List<Point> currentDrawingPoints;

    public AnnotationOverlay(Context context) {
        super(context);
        init();
    }

    public AnnotationOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnnotationOverlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        currentPath = new Path();
        annotations = new ArrayList<>();
        currentDrawingPoints = new ArrayList<>();

        // Enable drawing on this view
        setWillNotDraw(false);

        Log.d(TAG, "AnnotationOverlay initialized");
    }

    // Set parent activity reference to get current video time
    public void setParentActivity(VideoPlayerActivity activity) {
        this.parentActivity = activity;
        Log.d(TAG, "Parent activity set");
    }

    // Update current video position for time-based annotation display
    public void updateVideoPosition(long position) {
        this.currentVideoPosition = position;
        invalidate(); // Trigger redraw to show/hide annotations based on time
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw annotations that should be visible at current time
        for (AnnotationDrawing annotation : annotations) {
            // Show annotation if current time is at or after the annotation time
            // and within a reasonable display window (5 seconds)
            long timeDiff = currentVideoPosition - annotation.timestamp;
            if (timeDiff >= 0 && timeDiff <= 5000) { // Show for 5 seconds
                canvas.drawPath(annotation.path, annotation.paint);
            }
        }

        // Draw current path being drawn
        if (!currentPath.isEmpty()) {
            canvas.drawPath(currentPath, drawPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawingEnabled) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Start a new drawing path
                currentPath.reset();
                currentPath.moveTo(x, y);
                currentDrawingPoints.clear();
                currentDrawingPoints.add(new Point(x, y, MotionEvent.ACTION_DOWN));

                Log.d(TAG, "Drawing started at (" + x + ", " + y + ")");
                return true;

            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                currentDrawingPoints.add(new Point(x, y, MotionEvent.ACTION_MOVE));
                invalidate(); // Trigger redraw
                return true;

            case MotionEvent.ACTION_UP:
                // Finish the current drawing and save it
                currentDrawingPoints.add(new Point(x, y, MotionEvent.ACTION_UP));
                saveCurrentDrawing();
                currentPath.reset();
                currentDrawingPoints.clear();

                Log.d(TAG, "Drawing completed");
                return true;

            default:
                return false;
        }
    }

    private void saveCurrentDrawing() {
        if (currentDrawingPoints.size() > 1) { // Need at least 2 points for a drawing
            // Get the actual video timestamp when the drawing was made
            long drawingTimestamp = parentActivity != null ? parentActivity.getCurrentVideoPosition() : currentVideoPosition;

            Paint savedPaint = new Paint(drawPaint);
            Path savedPath = new Path(currentPath);

            AnnotationDrawing drawing = new AnnotationDrawing(savedPath, savedPaint, drawingTimestamp, currentDrawingPoints);
            annotations.add(drawing);

            Log.d(TAG, "Drawing saved with timestamp: " + drawingTimestamp + "ms, points: " + currentDrawingPoints.size());
            invalidate();
        }
    }

    public void addAnnotation(Annotation annotation) {
        // Convert annotation data back to path
        Path path = stringToPath(annotation.getAnnotationData());
        Paint paint = new Paint(drawPaint);

        // Customize paint based on annotation properties if needed
        paint.setColor(Color.BLUE); // Different color for loaded annotations

        AnnotationDrawing drawing = new AnnotationDrawing(path, paint, annotation.getTimestamp(), annotation.getAnnotationData());
        annotations.add(drawing);

        Log.d(TAG, "Annotation loaded with timestamp: " + annotation.getTimestamp() + "ms");
        invalidate();
    }

    public void clearAnnotations() {
        annotations.clear();
        currentPath.reset();
        currentDrawingPoints.clear();
        Log.d(TAG, "All annotations cleared");
        invalidate();
    }

    public void setDrawingColor(int color) {
        drawPaint.setColor(color);
    }

    public void setDrawingWidth(float width) {
        drawPaint.setStrokeWidth(width);
    }

    public void setDrawingEnabled(boolean enabled) {
        this.isDrawingEnabled = enabled;
        Log.d(TAG, "Drawing enabled: " + enabled);
    }

    // Convert string back to path
    private Path stringToPath(String pathData) {
        Path path = new Path();

        if (pathData == null || pathData.isEmpty()) {
            // Create a simple placeholder path
            path.moveTo(100, 100);
            path.lineTo(200, 200);
            return path;
        }

        try {
            JSONArray jsonArray = new JSONArray(pathData);
            boolean firstPoint = true;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pointObj = jsonArray.getJSONObject(i);
                float x = (float) pointObj.getDouble("x");
                float y = (float) pointObj.getDouble("y");
                int action = pointObj.getInt("action");

                if (action == MotionEvent.ACTION_DOWN || firstPoint) {
                    path.moveTo(x, y);
                    firstPoint = false;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    path.lineTo(x, y);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing path data: " + pathData, e);
            // Create a simple placeholder path
            path.moveTo(100, 100);
            path.lineTo(200, 200);
        }

        return path;
    }

    // Get all annotations for saving
    public List<AnnotationDrawing> getAllAnnotations() {
        return new ArrayList<>(annotations);
    }

    // Get current path as string for saving
    public String getCurrentPathAsString() {
        return pointsToString(currentDrawingPoints);
    }

    private String pointsToString(List<Point> points) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Point point : points) {
                JSONObject pointObj = new JSONObject();
                pointObj.put("x", point.x);
                pointObj.put("y", point.y);
                pointObj.put("action", point.action);
                jsonArray.put(pointObj);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error converting points to string", e);
            return "drawing_path_" + System.currentTimeMillis();
        }
    }
}
package com.lords.becomebetter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * AnnotationOverlay - Complete rewrite for Cricket Coaching App
 * Handles all drawing operations on video with timeline synchronization
 */
public class AnnotationOverlay extends View {

    private static final String TAG = "AnnotationOverlay";

    // Drawing state
    private Paint currentPaint;
    private Path currentPath;
    private boolean isDrawing = false;
    private boolean isDrawingEnabled = true;

    // Annotation storage
    private List<DrawnAnnotation> currentSessionAnnotations;
    private List<DrawnAnnotation> allVideoAnnotations;
    private Stack<List<DrawnAnnotation>> undoStack;
    private Stack<List<DrawnAnnotation>> redoStack;

    // Video synchronization
    private VideoPlayerActivity parentActivity;
    private long currentVideoTimestamp = 0;
    private long annotationDisplayDuration = 5000; // 5 seconds

    // Drawing tools
    private DrawingTool activeTool = DrawingTool.PEN;
    private int activeColor = Color.GREEN;
    private float activeStrokeWidth = 8f;

    // Touch handling
    private PointF lastTouchPoint;
    private List<PointF> currentStroke;

    /**
     * Enum for different drawing tools
     */
    public enum DrawingTool {
        PEN(8f, Paint.Style.STROKE),
        HIGHLIGHTER(16f, Paint.Style.STROKE),
        ARROW(6f, Paint.Style.STROKE),
        CIRCLE(4f, Paint.Style.STROKE),
        RECTANGLE(4f, Paint.Style.STROKE),
        TEXT(14f, Paint.Style.FILL);

        public final float defaultWidth;
        public final Paint.Style style;

        DrawingTool(float width, Paint.Style style) {
            this.defaultWidth = width;
            this.style = style;
        }
    }

    /**
     * Class to represent a single annotation drawing
     */
    public static class DrawnAnnotation {
        public Path path;
        public Paint paint;
        public long timestamp;
        public long duration;
        public DrawingTool tool;
        public String serializedPath;
        public List<PointF> points;
        public String text; // For text annotations
        public PointF textPosition; // For text annotations

        public DrawnAnnotation(DrawingTool tool, Paint paint, long timestamp) {
            this.tool = tool;
            this.paint = new Paint(paint);
            this.timestamp = timestamp;
            this.duration = 5000; // Default 5 seconds
            this.path = new Path();
            this.points = new ArrayList<>();
        }

        public DrawnAnnotation(String text, PointF position, Paint paint, long timestamp) {
            this.tool = DrawingTool.TEXT;
            this.text = text;
            this.textPosition = new PointF(position.x, position.y);
            this.paint = new Paint(paint);
            this.timestamp = timestamp;
            this.duration = 5000;
            this.points = new ArrayList<>();
        }
    }

    // Constructors
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

    /**
     * Initialize the annotation overlay
     */
    private void init() {
        Log.d(TAG, "🎨 Initializing AnnotationOverlay from scratch");

        // Initialize paint
        currentPaint = new Paint();
        setupPaint();

        // Initialize collections
        currentSessionAnnotations = new ArrayList<>();
        allVideoAnnotations = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        // Initialize drawing state
        currentPath = new Path();
        lastTouchPoint = new PointF();
        currentStroke = new ArrayList<>();

        // Enable drawing
        setWillNotDraw(false);

        Log.d(TAG, "✅ AnnotationOverlay initialized successfully");
    }

    /**
     * Setup paint properties based on active tool
     */
    private void setupPaint() {
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(activeColor);
        currentPaint.setStrokeWidth(activeStrokeWidth);
        currentPaint.setStyle(activeTool.style);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);

        // Special handling for highlighter
        if (activeTool == DrawingTool.HIGHLIGHTER) {
            currentPaint.setAlpha(128); // Semi-transparent
        }
    }

    /**
     * Set the parent video player activity
     */
    public void setParentActivity(VideoPlayerActivity activity) {
        this.parentActivity = activity;
        Log.d(TAG, "🎬 Parent activity connected");
    }

    /**
     * Update current video position for timeline synchronization
     */
    public void updateVideoTimestamp(long timestamp) {
        this.currentVideoTimestamp = timestamp;
        invalidate(); // Redraw to show/hide annotations based on time
    }

    /**
     * Enable or disable drawing
     */
    public void setDrawingEnabled(boolean enabled) {
        this.isDrawingEnabled = enabled;
        Log.d(TAG, "✏️ Drawing " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Set active drawing tool
     */
    public void setActiveTool(DrawingTool tool) {
        this.activeTool = tool;
        this.activeStrokeWidth = tool.defaultWidth;
        setupPaint();
        Log.d(TAG, "🔧 Active tool: " + tool.name());
    }

    /**
     * Set active drawing color
     */
    public void setActiveColor(int color) {
        this.activeColor = color;
        setupPaint();
        Log.d(TAG, "🎨 Active color changed");
    }

    /**
     * Set stroke width
     */
    public void setStrokeWidth(float width) {
        this.activeStrokeWidth = width;
        setupPaint();
        Log.d(TAG, "📏 Stroke width: " + width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw all video annotations that should be visible at current time
        for (DrawnAnnotation annotation : allVideoAnnotations) {
            if (shouldDisplayAnnotation(annotation)) {
                drawAnnotation(canvas, annotation);
            }
        }

        // Draw current session annotations
        for (DrawnAnnotation annotation : currentSessionAnnotations) {
            drawAnnotation(canvas, annotation);
        }

        // Draw current path being drawn
        if (isDrawing && !currentPath.isEmpty()) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    /**
     * Check if annotation should be displayed at current video time
     */
    private boolean shouldDisplayAnnotation(DrawnAnnotation annotation) {
        long timeDiff = currentVideoTimestamp - annotation.timestamp;
        return timeDiff >= 0 && timeDiff <= annotation.duration;
    }

    /**
     * Draw a single annotation
     */
    private void drawAnnotation(Canvas canvas, DrawnAnnotation annotation) {
        if (annotation.tool == DrawingTool.TEXT && annotation.text != null) {
            // Draw text annotation
            canvas.drawText(annotation.text,
                    annotation.textPosition.x,
                    annotation.textPosition.y,
                    annotation.paint);
        } else if (annotation.path != null) {
            // Draw path annotation
            canvas.drawPath(annotation.path, annotation.paint);
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
                return handleTouchDown(x, y);

            case MotionEvent.ACTION_MOVE:
                return handleTouchMove(x, y);

            case MotionEvent.ACTION_UP:
                return handleTouchUp(x, y);

            default:
                return false;
        }
    }

    /**
     * Handle touch down event
     */
    private boolean handleTouchDown(float x, float y) {
        Log.d(TAG, "👆 Touch down at (" + x + ", " + y + ")");

        // Save state for undo
        saveStateForUndo();

        // Start new drawing
        isDrawing = true;
        currentPath.reset();
        currentStroke.clear();

        // CRITICAL FIX: Set lastTouchPoint for ALL tools to prevent drawing from (0,0)
        lastTouchPoint.set(x, y);

        // Handle different tools
        switch (activeTool) {
            case PEN:
            case HIGHLIGHTER:
                // Move to starting point (no line drawn yet)
                currentPath.moveTo(x, y);
                currentStroke.add(new PointF(x, y));
                break;

            case ARROW:
                // Store start point for arrow
                currentStroke.add(new PointF(x, y));
                break;

            case CIRCLE:
            case RECTANGLE:
                // Store center/start point for shapes
                currentStroke.add(new PointF(x, y));
                break;
        }

        // Pause video when starting to draw
        if (parentActivity != null) {
            parentActivity.pauseVideo();
        }

        invalidate();
        return true;
    }

    /**
     * Handle touch move event
     */
    private boolean handleTouchMove(float x, float y) {
        if (!isDrawing) return false;

        switch (activeTool) {
            case PEN:
            case HIGHLIGHTER:
                // SAFE APPROACH: Use lineTo for predictable drawing
                // This ensures we draw from the last known position to current position
                currentPath.lineTo(x, y);
                currentStroke.add(new PointF(x, y));
                lastTouchPoint.set(x, y); // Update for next move
                break;

            case ARROW:
                // Redraw arrow from start to current point
                currentPath.reset();
                drawArrow(currentPath, currentStroke.get(0).x, currentStroke.get(0).y, x, y);
                break;

            case CIRCLE:
                // Draw circle with radius from center to current point
                currentPath.reset();
                float radius = (float) Math.sqrt(Math.pow(x - currentStroke.get(0).x, 2) +
                        Math.pow(y - currentStroke.get(0).y, 2));
                currentPath.addCircle(currentStroke.get(0).x, currentStroke.get(0).y, radius, Path.Direction.CW);
                break;

            case RECTANGLE:
                // Draw rectangle from start point to current point
                currentPath.reset();
                currentPath.addRect(Math.min(currentStroke.get(0).x, x), Math.min(currentStroke.get(0).y, y),
                        Math.max(currentStroke.get(0).x, x), Math.max(currentStroke.get(0).y, y),
                        Path.Direction.CW);
                break;
        }

        invalidate();
        return true;
    }

    /**
     * Handle touch up event
     */
    private boolean handleTouchUp(float x, float y) {
        if (!isDrawing) return false;

        Log.d(TAG, "👆 Touch up at (" + x + ", " + y + ")");

        // Complete the current drawing
        isDrawing = false;

        // Create annotation from current drawing
        DrawnAnnotation newAnnotation = new DrawnAnnotation(activeTool, currentPaint, getCurrentTimestamp());
        newAnnotation.path = new Path(currentPath);
        newAnnotation.points = new ArrayList<>(currentStroke);
        newAnnotation.serializedPath = serializePath(currentStroke);

        // Add to current session
        currentSessionAnnotations.add(newAnnotation);

        // Clear current drawing
        currentPath.reset();
        currentStroke.clear();

        Log.d(TAG, "✅ Annotation created with " + newAnnotation.points.size() + " points");

        invalidate();
        return true;
    }

    /**
     * Draw arrow from start to end point
     */
    private void drawArrow(Path path, float startX, float startY, float endX, float endY) {
        // Draw line
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);

        // Calculate arrow head
        double angle = Math.atan2(endY - startY, endX - startX);
        float arrowLength = 30f;
        float arrowAngle = (float) Math.PI / 6; // 30 degrees

        // Arrow head points
        float x1 = (float) (endX - arrowLength * Math.cos(angle - arrowAngle));
        float y1 = (float) (endY - arrowLength * Math.sin(angle - arrowAngle));
        float x2 = (float) (endX - arrowLength * Math.cos(angle + arrowAngle));
        float y2 = (float) (endY - arrowLength * Math.sin(angle + arrowAngle));

        // Draw arrow head
        path.moveTo(endX, endY);
        path.lineTo(x1, y1);
        path.moveTo(endX, endY);
        path.lineTo(x2, y2);
    }

    /**
     * Get current video timestamp for annotation
     */
    private long getCurrentTimestamp() {
        if (parentActivity != null) {
            return parentActivity.getCurrentVideoPosition();
        }
        return currentVideoTimestamp;
    }

    /**
     * Save current state for undo functionality
     */
    private void saveStateForUndo() {
        // Create deep copy of current annotations
        List<DrawnAnnotation> stateCopy = new ArrayList<>();
        for (DrawnAnnotation annotation : currentSessionAnnotations) {
            stateCopy.add(annotation);
        }
        undoStack.push(stateCopy);

        // Clear redo stack
        redoStack.clear();

        // Limit undo stack size
        if (undoStack.size() > 20) {
            undoStack.remove(0);
        }
    }

    /**
     * Undo last annotation
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            // Save current state to redo stack
            List<DrawnAnnotation> currentState = new ArrayList<>(currentSessionAnnotations);
            redoStack.push(currentState);

            // Restore previous state
            currentSessionAnnotations = undoStack.pop();

            invalidate();
            Log.d(TAG, "↶ Undo performed");
        }
    }

    /**
     * Redo last undone annotation
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            // Save current state to undo stack
            List<DrawnAnnotation> currentState = new ArrayList<>(currentSessionAnnotations);
            undoStack.push(currentState);

            // Restore redo state
            currentSessionAnnotations = redoStack.pop();

            invalidate();
            Log.d(TAG, "↷ Redo performed");
        }
    }

    /**
     * Clear all current session annotations
     */
    public void clearCurrentSession() {
        saveStateForUndo();
        currentSessionAnnotations.clear();
        invalidate();
        Log.d(TAG, "🗑️ Current session cleared");
    }

    /**
     * Get all current session annotations
     */
    public List<DrawnAnnotation> getCurrentSessionAnnotations() {
        return new ArrayList<>(currentSessionAnnotations);
    }

    /**
     * Load existing annotations for video
     */
    public void loadVideoAnnotations(List<Annotation> dbAnnotations) {
        allVideoAnnotations.clear();

        for (Annotation dbAnnotation : dbAnnotations) {
            DrawnAnnotation drawn = deserializeAnnotation(dbAnnotation);
            if (drawn != null) {
                allVideoAnnotations.add(drawn);
            }
        }

        invalidate();
        Log.d(TAG, "📥 Loaded " + allVideoAnnotations.size() + " annotations from database");
    }

    /**
     * Convert database annotation to drawable annotation
     */
    private DrawnAnnotation deserializeAnnotation(Annotation dbAnnotation) {
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(Color.GREEN); // Default color
            paint.setStrokeWidth(8f); // Default width

            DrawnAnnotation drawn = new DrawnAnnotation(DrawingTool.PEN, paint, dbAnnotation.getTimestamp());

            // Deserialize path data
            List<PointF> points = deserializePath(dbAnnotation.getAnnotationData());
            drawn.points = points;
            drawn.path = createPathFromPoints(points);

            return drawn;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error deserializing annotation: " + e.getMessage());
            return null;
        }
    }

    /**
     * Serialize path points to string
     */
    private String serializePath(List<PointF> points) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (PointF point : points) {
                JSONObject pointObj = new JSONObject();
                pointObj.put("x", point.x);
                pointObj.put("y", point.y);
                jsonArray.put(pointObj);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            Log.e(TAG, "❌ Error serializing path: " + e.getMessage());
            return "";
        }
    }

    /**
     * Deserialize path points from string
     */
    private List<PointF> deserializePath(String pathData) {
        List<PointF> points = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(pathData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pointObj = jsonArray.getJSONObject(i);
                float x = (float) pointObj.getDouble("x");
                float y = (float) pointObj.getDouble("y");
                points.add(new PointF(x, y));
            }
        } catch (JSONException e) {
            Log.e(TAG, "❌ Error deserializing path: " + e.getMessage());
        }
        return points;
    }

    /**
     * Create Path from points
     */
    private Path createPathFromPoints(List<PointF> points) {
        Path path = new Path();
        if (points.isEmpty()) return path;

        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        return path;
    }

    /**
     * Convert current session annotations to database format
     */
    public List<Annotation> convertToDbAnnotations(int videoId, int coachId) {
        List<Annotation> dbAnnotations = new ArrayList<>();

        for (DrawnAnnotation drawn : currentSessionAnnotations) {
            Annotation dbAnnotation = new Annotation(
                    videoId,
                    coachId,
                    drawn.timestamp,
                    Annotation.TYPE_DRAWING,
                    drawn.serializedPath,
                    0, 0
            );
            dbAnnotations.add(dbAnnotation);
        }

        return dbAnnotations;
    }

    // Getter methods for UI
    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
    public DrawingTool getActiveTool() { return activeTool; }
    public int getActiveColor() { return activeColor; }
    public float getStrokeWidth() { return activeStrokeWidth; }
    public int getCurrentSessionAnnotationCount() { return currentSessionAnnotations.size(); }
}
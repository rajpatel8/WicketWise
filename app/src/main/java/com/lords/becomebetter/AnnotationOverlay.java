package com.lords.becomebetter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class AnnotationOverlay extends View {

    private Paint drawPaint;
    private Path currentPath;
    private List<AnnotationDrawing> annotations;
    private boolean isDrawingEnabled = true;

    // Inner class to hold annotation drawing data
    static class AnnotationDrawing {
        Path path;
        Paint paint;
        long timestamp;

        AnnotationDrawing(Path path, Paint paint, long timestamp) {
            this.path = new Path(path);
            this.paint = new Paint(paint);
            this.timestamp = timestamp;
        }
    }

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

        // Enable drawing on this view
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw all saved annotations
        for (AnnotationDrawing annotation : annotations) {
            canvas.drawPath(annotation.path, annotation.paint);
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
                currentPath.moveTo(x, y);
                return true;

            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                invalidate(); // Trigger redraw
                return true;

            case MotionEvent.ACTION_UP:
                // Save the current drawing as an annotation
                saveCurrentDrawing();
                currentPath.reset();
                return true;

            default:
                return false;
        }
    }

    private void saveCurrentDrawing() {
        if (!currentPath.isEmpty()) {
            Paint savedPaint = new Paint(drawPaint);
            AnnotationDrawing drawing = new AnnotationDrawing(currentPath, savedPaint, System.currentTimeMillis());
            annotations.add(drawing);
            invalidate();
        }
    }

    public void setCurrentPath(Path path) {
        this.currentPath = path;
        invalidate();
    }

    public void addAnnotation(Annotation annotation) {
        // Convert annotation data back to path
        // For now, create a simple path - you can enhance this based on your path format
        Path path = stringToPath(annotation.getAnnotationData());

        Paint paint = new Paint(drawPaint);
        // You can customize paint based on annotation properties

        AnnotationDrawing drawing = new AnnotationDrawing(path, paint, annotation.getTimestamp());
        annotations.add(drawing);
        invalidate();
    }

    public void clearAnnotations() {
        annotations.clear();
        currentPath.reset();
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
    }

    // Convert string back to path (simplified implementation)
    private Path stringToPath(String pathData) {
        Path path = new Path();
        // For now, create a simple placeholder path
        // You would implement proper path parsing here
        path.moveTo(100, 100);
        path.lineTo(200, 200);
        return path;
    }

    // Show/hide annotations based on video timestamp
    public void showAnnotationsForTime(long currentTime) {
        // You can implement time-based annotation visibility here
        // For now, show all annotations
        invalidate();
    }

    // Get all annotations for saving
    public List<AnnotationDrawing> getAllAnnotations() {
        return new ArrayList<>(annotations);
    }

    // Get current path as string for saving
    public String getCurrentPathAsString() {
        // Simple implementation - convert path to string
        // You can implement a more sophisticated format
        return "drawing_path_" + System.currentTimeMillis();
    }
}
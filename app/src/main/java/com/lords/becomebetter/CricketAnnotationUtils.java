package com.lords.becomebetter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for cricket-specific annotation features
 * Provides specialized tools and templates for cricket coaching
 */
public class CricketAnnotationUtils {

    private static final String TAG = "CricketAnnotationUtils";

    // Cricket field positions (normalized coordinates 0-1)
    public static final List<FieldPosition> CRICKET_FIELD_POSITIONS = Arrays.asList(
            // Batting positions
            new FieldPosition("Batsman 1", 0.5f, 0.45f, FieldPosition.Type.BATSMAN),
            new FieldPosition("Batsman 2", 0.5f, 0.55f, FieldPosition.Type.BATSMAN),

            // Wicket keeper
            new FieldPosition("Wicket Keeper", 0.5f, 0.15f, FieldPosition.Type.WICKET_KEEPER),

            // Close fielders
            new FieldPosition("Slip 1", 0.6f, 0.2f, FieldPosition.Type.FIELDER),
            new FieldPosition("Slip 2", 0.65f, 0.25f, FieldPosition.Type.FIELDER),
            new FieldPosition("Gully", 0.7f, 0.3f, FieldPosition.Type.FIELDER),
            new FieldPosition("Point", 0.75f, 0.5f, FieldPosition.Type.FIELDER),
            new FieldPosition("Cover", 0.7f, 0.65f, FieldPosition.Type.FIELDER),
            new FieldPosition("Mid Off", 0.5f, 0.8f, FieldPosition.Type.FIELDER),
            new FieldPosition("Mid On", 0.4f, 0.8f, FieldPosition.Type.FIELDER),
            new FieldPosition("Square Leg", 0.25f, 0.5f, FieldPosition.Type.FIELDER),

            // Boundary fielders
            new FieldPosition("Third Man", 0.8f, 0.15f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Deep Point", 0.9f, 0.5f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Deep Cover", 0.85f, 0.7f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Long Off", 0.5f, 0.95f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Long On", 0.35f, 0.95f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Deep Mid Wicket", 0.15f, 0.7f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Deep Square Leg", 0.1f, 0.5f, FieldPosition.Type.BOUNDARY_FIELDER),
            new FieldPosition("Fine Leg", 0.2f, 0.15f, FieldPosition.Type.BOUNDARY_FIELDER)
    );

    // Cricket coaching annotations
    public static final List<CoachingAnnotation> COACHING_TEMPLATES = Arrays.asList(
            new CoachingAnnotation("Stance", "🏏 Check batting stance", Color.GREEN),
            new CoachingAnnotation("Footwork", "👟 Improve foot movement", Color.BLUE),
            new CoachingAnnotation("Head Position", "👁️ Keep eye on ball", Color.YELLOW),
            new CoachingAnnotation("Follow Through", "🔄 Complete the shot", Color.RED),
            new CoachingAnnotation("Balance", "⚖️ Maintain balance", Color.MAGENTA),
            new CoachingAnnotation("Timing", "⏰ Watch the timing", Color.CYAN),
            new CoachingAnnotation("Line & Length", "🎯 Bowl on target", Color.GREEN),
            new CoachingAnnotation("Release Point", "📍 Consistent release", Color.YELLOW)
    );

    /**
     * Represents a cricket field position
     */
    public static class FieldPosition {
        public enum Type {
            BATSMAN, BOWLER, WICKET_KEEPER, FIELDER, BOUNDARY_FIELDER
        }

        public String name;
        public float x; // Normalized coordinates (0-1)
        public float y;
        public Type type;

        public FieldPosition(String name, float x, float y, Type type) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.type = type;
        }

        public PointF getScreenPosition(int screenWidth, int screenHeight) {
            return new PointF(x * screenWidth, y * screenHeight);
        }

        public int getColor() {
            switch (type) {
                case BATSMAN: return Color.GREEN;
                case BOWLER: return Color.RED;
                case WICKET_KEEPER: return Color.BLUE;
                case FIELDER: return Color.YELLOW;
                case BOUNDARY_FIELDER: return Color.CYAN;
                default: return Color.WHITE;
            }
        }
    }

    /**
     * Represents a coaching annotation template
     */
    public static class CoachingAnnotation {
        public String title;
        public String description;
        public int color;

        public CoachingAnnotation(String title, String description, int color) {
            this.title = title;
            this.description = description;
            this.color = color;
        }
    }

    /**
     * Draw cricket field layout overlay
     */
    public static void drawCricketField(Canvas canvas, int width, int height, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(100);

        // Draw boundary circle
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = Math.min(width, height) * 0.4f;
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw pitch rectangle
        float pitchWidth = width * 0.1f;
        float pitchHeight = height * 0.3f;
        float pitchLeft = centerX - pitchWidth / 2;
        float pitchTop = centerY - pitchHeight / 2;
        canvas.drawRect(pitchLeft, pitchTop, pitchLeft + pitchWidth, pitchTop + pitchHeight, paint);

        // Draw stumps
        paint.setStrokeWidth(4f);
        // Batting end stumps
        canvas.drawLine(centerX - 10, centerY + pitchHeight / 2 - 20,
                centerX + 10, centerY + pitchHeight / 2 - 20, paint);
        // Bowling end stumps
        canvas.drawLine(centerX - 10, centerY - pitchHeight / 2 + 20,
                centerX + 10, centerY - pitchHeight / 2 + 20, paint);

        // Draw 30-yard circle
        paint.setStrokeWidth(1f);
        canvas.drawCircle(centerX, centerY, radius * 0.7f, paint);
    }

    /**
     * Draw field positions on the field
     */
    public static void drawFieldPositions(Canvas canvas, int width, int height, Paint paint) {
        paint.setTextSize(24f);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setStyle(Paint.Style.FILL);

        for (FieldPosition position : CRICKET_FIELD_POSITIONS) {
            PointF screenPos = position.getScreenPosition(width, height);

            // Draw position circle
            paint.setColor(position.getColor());
            canvas.drawCircle(screenPos.x, screenPos.y, 20f, paint);

            // Draw position number/initial
            paint.setColor(Color.BLACK);
            String initial = position.name.substring(0, 1);
            canvas.drawText(initial, screenPos.x - 8, screenPos.y + 8, paint);
        }
    }

    /**
     * Create arrow annotation for ball trajectory
     */
    public static Path createBallTrajectoryPath(PointF start, PointF end, boolean curved) {
        Path path = new Path();

        if (curved) {
            // Create curved path for ball trajectory
            path.moveTo(start.x, start.y);

            // Calculate control point for curve
            float controlX = (start.x + end.x) / 2;
            float controlY = Math.min(start.y, end.y) - 50; // Arc upward

            path.quadTo(controlX, controlY, end.x, end.y);
        } else {
            // Straight line
            path.moveTo(start.x, start.y);
            path.lineTo(end.x, end.y);
        }

        return path;
    }

    /**
     * Create batting shot angle lines
     */
    public static List<Path> createBattingShotAngles(PointF batsmanPos, int screenWidth, int screenHeight) {
        List<Path> angles = new ArrayList<>();

        // Common batting shot angles (in degrees from vertical)
        int[] shotAngles = {-45, -20, 0, 20, 45, 90, 135, 180, -135, -90};
        String[] shotNames = {"Point", "Cover", "Straight", "Mid On", "Mid Wicket",
                "Square Leg", "Fine Leg", "Long Stop", "Third Man", "Point"};

        for (int angle : shotAngles) {
            Path path = new Path();
            path.moveTo(batsmanPos.x, batsmanPos.y);

            // Calculate end point
            double radians = Math.toRadians(angle);
            float endX = batsmanPos.x + (float) (Math.sin(radians) * 150);
            float endY = batsmanPos.y - (float) (Math.cos(radians) * 150);

            path.lineTo(endX, endY);
            angles.add(path);
        }

        return angles;
    }

    /**
     * Create bowling line and length grid
     */
    public static List<Path> createBowlingGrid(PointF stumpsPos, int screenWidth, int screenHeight) {
        List<Path> gridLines = new ArrayList<>();

        // Line indicators (relative to stumps)
        float[] lineOffsets = {-60, -30, 0, 30, 60}; // Left to right
        String[] lineNames = {"Wide", "Outside Off", "Off Stump", "Middle", "Leg"};

        // Length indicators (relative to stumps)
        float[] lengthOffsets = {-200, -150, -100, -50}; // Good length to yorker
        String[] lengthNames = {"Good Length", "Short of Length", "Full", "Yorker"};

        // Draw line indicators (vertical lines)
        for (float offset : lineOffsets) {
            Path line = new Path();
            line.moveTo(stumpsPos.x + offset, stumpsPos.y - 250);
            line.lineTo(stumpsPos.x + offset, stumpsPos.y + 50);
            gridLines.add(line);
        }

        // Draw length indicators (horizontal lines)
        for (float offset : lengthOffsets) {
            Path line = new Path();
            line.moveTo(stumpsPos.x - 80, stumpsPos.y + offset);
            line.lineTo(stumpsPos.x + 80, stumpsPos.y + offset);
            gridLines.add(line);
        }

        return gridLines;
    }

    /**
     * Create player movement path with timestamps
     */
    public static class PlayerMovement {
        public List<PointF> positions;
        public List<Long> timestamps;
        public String playerName;
        public int color;

        public PlayerMovement(String playerName, int color) {
            this.playerName = playerName;
            this.color = color;
            this.positions = new ArrayList<>();
            this.timestamps = new ArrayList<>();
        }

        public void addPosition(PointF position, long timestamp) {
            positions.add(new PointF(position.x, position.y));
            timestamps.add(timestamp);
        }

        public Path getMovementPath() {
            if (positions.size() < 2) return new Path();

            Path path = new Path();
            path.moveTo(positions.get(0).x, positions.get(0).y);

            for (int i = 1; i < positions.size(); i++) {
                path.lineTo(positions.get(i).x, positions.get(i).y);
            }

            return path;
        }
    }

    /**
     * Create coaching note bubble
     */
    public static void drawCoachingNote(Canvas canvas, PointF position, String note, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(200);

        // Calculate text bounds
        float textWidth = paint.measureText(note);
        float textHeight = paint.getTextSize();

        // Draw bubble background
        float bubbleWidth = textWidth + 40;
        float bubbleHeight = textHeight + 20;
        float bubbleLeft = position.x - bubbleWidth / 2;
        float bubbleTop = position.y - bubbleHeight - 20;

        canvas.drawRoundRect(bubbleLeft, bubbleTop,
                bubbleLeft + bubbleWidth, bubbleTop + bubbleHeight,
                10, 10, paint);

        // Draw pointer
        Path pointer = new Path();
        pointer.moveTo(position.x, position.y);
        pointer.lineTo(position.x - 10, bubbleTop + bubbleHeight);
        pointer.lineTo(position.x + 10, bubbleTop + bubbleHeight);
        pointer.close();
        canvas.drawPath(pointer, paint);

        // Draw text
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(note, bubbleLeft + 20, bubbleTop + textHeight + 5, paint);
    }

    /**
     * Generate cricket-specific colors
     */
    public static class CricketColors {
        public static final int FIELD_GREEN = Color.parseColor("#2E7D32");
        public static final int BALL_RED = Color.parseColor("#C62828");
        public static final int STUMPS_BROWN = Color.parseColor("#5D4037");
        public static final int BOUNDARY_WHITE = Color.WHITE;
        public static final int PITCH_BROWN = Color.parseColor("#8D6E63");
        public static final int COACHING_HIGHLIGHT = Color.parseColor("#FFD54F");

        public static int[] getBattingColors() {
            return new int[]{FIELD_GREEN, Color.BLUE, Color.RED, Color.YELLOW};
        }

        public static int[] getBowlingColors() {
            return new int[]{BALL_RED, Color.CYAN, Color.MAGENTA, COACHING_HIGHLIGHT};
        }

        public static int[] getFieldingColors() {
            return new int[]{Color.YELLOW, Color.CYAN, Color.GREEN, Color.WHITE};
        }
    }

    /**
     * Validate cricket field coordinates
     */
    public static boolean isValidFieldPosition(PointF position, int fieldWidth, int fieldHeight) {
        // Check if position is within field boundaries
        float centerX = fieldWidth / 2f;
        float centerY = fieldHeight / 2f;
        float maxRadius = Math.min(fieldWidth, fieldHeight) * 0.45f;

        float distance = (float) Math.sqrt(
                Math.pow(position.x - centerX, 2) + Math.pow(position.y - centerY, 2)
        );

        return distance <= maxRadius;
    }

    /**
     * Get position name based on coordinates
     */
    public static String getPositionName(PointF position, int screenWidth, int screenHeight) {
        float normalizedX = position.x / screenWidth;
        float normalizedY = position.y / screenHeight;

        // Find closest field position
        FieldPosition closest = null;
        float minDistance = Float.MAX_VALUE;

        for (FieldPosition fp : CRICKET_FIELD_POSITIONS) {
            float distance = (float) Math.sqrt(
                    Math.pow(normalizedX - fp.x, 2) + Math.pow(normalizedY - fp.y, 2)
            );

            if (distance < minDistance) {
                minDistance = distance;
                closest = fp;
            }
        }

        if (closest != null && minDistance < 0.1f) { // Within 10% of screen
            return closest.name;
        }

        return "Custom Position";
    }

    /**
     * Create batting technique analysis overlay
     */
    public static List<Path> createBattingAnalysisOverlay(PointF batsmanPos) {
        List<Path> overlays = new ArrayList<>();

        // Stance width indicator
        Path stanceWidth = new Path();
        stanceWidth.moveTo(batsmanPos.x - 30, batsmanPos.y + 40);
        stanceWidth.lineTo(batsmanPos.x + 30, batsmanPos.y + 40);
        overlays.add(stanceWidth);

        // Head position circle
        Path headPosition = new Path();
        headPosition.addCircle(batsmanPos.x, batsmanPos.y - 60, 15, Path.Direction.CW);
        overlays.add(headPosition);

        // Bat swing arc
        Path batSwing = new Path();
        batSwing.addArc(batsmanPos.x - 50, batsmanPos.y - 30,
                batsmanPos.x + 50, batsmanPos.y + 70, -45, 90);
        overlays.add(batSwing);

        return overlays;
    }

    /**
     * Create bowling action analysis overlay
     */
    public static List<Path> createBowlingAnalysisOverlay(PointF bowlerPos) {
        List<Path> overlays = new ArrayList<>();

        // Run-up line
        Path runUp = new Path();
        runUp.moveTo(bowlerPos.x, bowlerPos.y - 200);
        runUp.lineTo(bowlerPos.x, bowlerPos.y);
        overlays.add(runUp);

        // Release point circle
        Path releasePoint = new Path();
        releasePoint.addCircle(bowlerPos.x, bowlerPos.y - 80, 10, Path.Direction.CW);
        overlays.add(releasePoint);

        // Follow through arc
        Path followThrough = new Path();
        followThrough.addArc(bowlerPos.x - 40, bowlerPos.y - 20,
                bowlerPos.x + 40, bowlerPos.y + 100, 0, 180);
        overlays.add(followThrough);

        return overlays;
    }
}
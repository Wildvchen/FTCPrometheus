package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

// Limelight native FTC imports
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name="DECODE: Rotatable Hood Auto-Tracker", group="Outtake")
public class LimelightTest extends LinearOpMode {

    // ---------------------------------------------------------
    // HARDWARE DECLARATIONS
    // ---------------------------------------------------------
    private Servo hoodServo;
    private Limelight3A limelight;

    // ---------------------------------------------------------
    // CONSTANTS & TUNING VARIABLES
    // ---------------------------------------------------------
    // 5-Turn Servo maps 0.0 to 1.0. 0.5 is perfectly centered (2.5 turns).
    private final double SERVO_CENTER = 0.5;

    // Safety limits to prevent wire tangling
    private final double MAX_SAFE_LIMIT = 0.85;
    private final double MIN_SAFE_LIMIT = 0.15;

    // Proportional control constant (How aggressively it tracks)
    // You will need to tune this! Lower it if the hood jitters.
    private final double Kp = 0.005;

    // Speed at which the servo unwinds during recovery
    private final double UNWIND_SPEED = 0.03;

    // ---------------------------------------------------------
    // STATE MACHINE SETUP
    // ---------------------------------------------------------
    public enum HoodState {
        TRACKING,
        UNTANGLING
    }
    private HoodState currentState = HoodState.TRACKING;

    @Override
    public void runOpMode() {
        initializeHardware();

        telemetry.addLine("Status: Initialized. Waiting for start.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double currentServoPos = hoodServo.getPosition();
            LLResult visionResult = limelight.getLatestResult();

            // Run the appropriate logic based on our current state
            switch (currentState) {
                case TRACKING:
                    executeTrackingMode(visionResult, currentServoPos);
                    break;
                case UNTANGLING:
                    executeUntangleMode(currentServoPos);
                    break;
            }

            // Update driver station with critical info
            updateTelemetry(currentServoPos);
        }

        // Stop the Limelight when the OpMode stops to save processing power
        limelight.stop();
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    /**
     * Maps hardware from the config and sets initial states.
     */
    private void initializeHardware() {
        // 1. Initialize Servo
        hoodServo = hardwareMap.get(Servo.class, "hoodServo");
        hoodServo.setDirection(Servo.Direction.FORWARD); // Change to REVERSE if tracking goes the wrong way
        hoodServo.setPosition(SERVO_CENTER); // Center the hood on Init

        // 2. Initialize Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0); // Ensure we are on the AprilTag pipeline
        limelight.start();
    }

    /**
     * Handles centering the AprilTag using a Proportional control loop.
     */
    private void executeTrackingMode(LLResult result, double currentPos) {
        // If we don't see a tag, just stay still
        if (result == null || !result.isValid()) {
            return;
        }

        // Calculate the new position based on the horizontal offset (tx)
        double tx = result.getTx();
        double targetPos = currentPos + (tx * Kp);

        // Clip the target position so we don't send illegal values to the servo
        targetPos = Range.clip(targetPos, 0.0, 1.0);

        // Check if our new position violates our physical safety limits
        if (targetPos >= MAX_SAFE_LIMIT || targetPos <= MIN_SAFE_LIMIT) {
            currentState = HoodState.UNTANGLING;
        } else {
            // Safe to move!
            hoodServo.setPosition(targetPos);
        }
    }

    /**
     * Slowly rotates the servo back to the center position.
     */
    private void executeUntangleMode(double currentPos) {
        // If we are significantly off-center, keep moving toward center
        if (currentPos > SERVO_CENTER + 0.02) {
            hoodServo.setPosition(currentPos - UNWIND_SPEED);
        } else if (currentPos < SERVO_CENTER - 0.02) {
            hoodServo.setPosition(currentPos + UNWIND_SPEED);
        } else {
            // We have reached the center safely! Resume tracking.
            currentState = HoodState.TRACKING;
        }
    }

    /**
     * Centralized telemetry reporting.
     */
    private void updateTelemetry(double currentPos) {
        telemetry.addData("Current State", currentState);
        telemetry.addData("Servo Position", "%.3f", currentPos);
        telemetry.addData("Limelight FPS", "%.1f", limelight.getConnectionInfo());
        telemetry.update();
    }
}
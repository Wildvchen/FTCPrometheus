package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Kicker Servo Test", group = "Linear Opmode")
public class kickerTest extends LinearOpMode {

    // Define the servos
    private Servo kickerServo1;
    private Servo kickerServo2;

    // Define a variable to hold the servo position
    private double servoPosition = 0.5; // Initial position

    // Define the increment for changing the servo position
    static final double POSITION_INCREMENT = 0.01;

    @Override
    public void runOpMode() {
        // --- Initialization ---
        telemetry.addData("Status", "Initializing");

        // Map the servos from the hardware configuration
        // Make sure your configuration file has servos named "kicker_1" and "kicker_2"
        kickerServo1 = hardwareMap.get(Servo.class, "kicker_1");
        kickerServo2 = hardwareMap.get(Servo.class, "kicker_2");

        // To make the servos move in opposite directions, we set one servo's
        // position to be the inverse of the other.
        // For example, if servo 1 is at position P, servo 2 will be at (1.0 - P).

        telemetry.addData("Status", "Initialized");
        telemetry.addData(">", "Press Start to begin.");
        telemetry.addData(">", "Use D-pad Up/Down on gamepad 1 to control servos.");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // --- Main Loop ---
        while (opModeIsActive()) {

            // --- Servo Control (Gamepad 1 D-pad) ---
            // Increase position
            if (gamepad1.dpad_up) {
                servoPosition += POSITION_INCREMENT;
            }
            // Decrease position
            else if (gamepad1.dpad_down) {
                servoPosition -= POSITION_INCREMENT;
            }

            // Clamp the position value to be between 0.0 and 1.0
            servoPosition = Range.clip(servoPosition, 0.0, 1.0);

            // Set the position of the servos
            // One servo will go from 0 to 1, the other from 1 to 0.
            kickerServo1.setPosition(servoPosition);
            kickerServo2.setPosition(1.0 - servoPosition);


            // --- Telemetry ---
            // Display the current servo position
            telemetry.addData("Servo Position", servoPosition);
            telemetry.addData("Kicker 1 Position", kickerServo1.getPosition());
            telemetry.addData("Kicker 2 Position", kickerServo2.getPosition());
            telemetry.update();

            // Small delay to prevent button press spam
            sleep(20);
        }
    }
}

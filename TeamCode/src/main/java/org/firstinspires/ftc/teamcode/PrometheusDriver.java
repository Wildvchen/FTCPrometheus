package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Prometheus Driver Mecanum", group = "Linear Opmode")
public class PrometheusDriver extends LinearOpMode {

    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private DcMotor intakeMotor, transferMotor;
    private Servo kickerServo1, kickerServo2;

    private boolean intakeTransferOn = false;
    private boolean lastAState = false;
    private boolean lastBState = false;

    // Kicker positions - adjust these values as needed
    private static final double KICKER_REST = 0.0;
    private static final double KICKER_UP = 0.5;

    @Override
    public void runOpMode() {
        // Initialize hardware
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive   = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive  = hardwareMap.get(DcMotor.class, "right_back_drive");

        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer_motor");
        
        kickerServo1 = hardwareMap.get(Servo.class, "kicker_servo1");
        kickerServo2 = hardwareMap.get(Servo.class, "kicker_servo2");

        // Set directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        transferMotor.setDirection(DcMotor.Direction.FORWARD);
        
        // Reverse one servo if they are mounted symmetrically
        kickerServo1.setDirection(Servo.Direction.FORWARD);
        kickerServo2.setDirection(Servo.Direction.REVERSE);

        kickerServo1.setPosition(KICKER_REST);
        kickerServo2.setPosition(KICKER_REST);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // --- Mecanum Drive ---
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            leftFrontDrive.setPower(frontLeftPower);
            leftBackDrive.setPower(backLeftPower);
            rightFrontDrive.setPower(frontRightPower);
            rightBackDrive.setPower(backRightPower);

            // --- Intake & Transfer Control (Toggle - Button A) ---
            if (gamepad1.a && !lastAState) {
                intakeTransferOn = !intakeTransferOn;
            }
            lastAState = gamepad1.a;

            // --- Kicker Sequence (Button B) ---
            if (gamepad1.b && !lastBState) {
                // Kick 3 times
                for (int i = 0; i < 3; i++) {
                    kickerServo1.setPosition(KICKER_UP);
                    kickerServo2.setPosition(KICKER_UP);
                    sleep(250); // Adjust timing for speed
                    kickerServo1.setPosition(KICKER_REST);
                    kickerServo2.setPosition(KICKER_REST);
                    sleep(250);
                }
            }
            lastBState = gamepad1.b;

            // Apply power to Intake and Transfer Motors
            double power = intakeTransferOn ? 1.0 : 0.0;
            intakeMotor.setPower(power);
            transferMotor.setPower(power);

            telemetry.addData("Status", "Running");
            telemetry.addData("Intake/Transfer", intakeTransferOn ? "ON" : "OFF");
            telemetry.update();
        }
    }
}

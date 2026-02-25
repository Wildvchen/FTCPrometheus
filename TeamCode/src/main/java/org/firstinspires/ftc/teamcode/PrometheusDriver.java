package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Prometheus Driver Mecanum", group = "Linear Opmode")
public class PrometheusDriver extends LinearOpMode {

    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private DcMotor intakeMotor1, intakeMotor2;

    private boolean intakeOn = false;
    private boolean lastAState = false;

    @Override
    public void runOpMode() {
        // Initialize hardware
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive   = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive  = hardwareMap.get(DcMotor.class, "right_back_drive");

        intakeMotor1 = hardwareMap.get(DcMotor.class, "intake_motor1");
        intakeMotor2 = hardwareMap.get(DcMotor.class, "intake_motor2");

        // Set directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        
        intakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // --- Mecanum Drive ---
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
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

            // --- Intake Control (Toggle) ---
            if (gamepad1.a && !lastAState) {
                intakeOn = !intakeOn;
            }
            lastAState = gamepad1.a;

            if (intakeOn) {
                intakeMotor1.setPower(1.0);
                intakeMotor2.setPower(1.0);
            } else {
                intakeMotor1.setPower(0.0);
                intakeMotor2.setPower(0.0);
            }

            telemetry.addData("Status", "Running");
            telemetry.addData("Intake", intakeOn ? "ON (Toggle)" : "OFF (Toggle)");
            telemetry.update();
        }
    }
}

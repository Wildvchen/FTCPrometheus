package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Prometheus Driver Mecanum", group = "Linear Opmode")
public class PrometheusDriver extends LinearOpMode {

    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private DcMotor intakeMotor1, intakeMotor2;
    private DcMotor outtakeMotor1, outtakeMotor2;
    private DcMotor transferMotor;

    private boolean intakeOn = false;
    private boolean outtakeOn = false;
    private boolean transferOn = false;

    private boolean lastAState = false;
    private boolean lastBState = false;
    private boolean lastXState = false;

    @Override
    public void runOpMode() {
        // Initialize hardware
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive   = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive  = hardwareMap.get(DcMotor.class, "right_back_drive");

        intakeMotor1 = hardwareMap.get(DcMotor.class, "intake_motor1");
        intakeMotor2 = hardwareMap.get(DcMotor.class, "intake_motor2");
        outtakeMotor1 = hardwareMap.get(DcMotor.class, "outtake_motor1");
        outtakeMotor2 = hardwareMap.get(DcMotor.class, "outtake_motor2");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer_motor");

        // Set directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        
        intakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        outtakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor2.setDirection(DcMotor.Direction.FORWARD);
        
        transferMotor.setDirection(DcMotor.Direction.FORWARD);

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

            // --- Intake Control (Toggle - Button A) ---
            if (gamepad1.a && !lastAState) {
                intakeOn = !intakeOn;
            }
            lastAState = gamepad1.a;

            // --- Outtake/Flywheel Control (Toggle - Button B) ---
            if (gamepad1.b && !lastBState) {
                outtakeOn = !outtakeOn;
            }
            lastBState = gamepad1.b;

            // --- Transfer Wheels Control (Toggle - Button X) ---
            if (gamepad1.x && !lastXState) {
                transferOn = !transferOn;
            }
            lastXState = gamepad1.x;

            // Apply power to Intake Motors
            intakeMotor1.setPower(intakeOn ? 1.0 : 0.0);
            intakeMotor2.setPower(intakeOn ? 1.0 : 0.0);

            // Apply power to Outtake (Flywheel) Motors
            outtakeMotor1.setPower(outtakeOn ? 1.0 : 0.0);
            outtakeMotor2.setPower(outtakeOn ? 1.0 : 0.0);
            
            // Apply power to Transfer Motor
            transferMotor.setPower(transferOn ? 1.0 : 0.0);

            telemetry.addData("Status", "Running");
            telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
            telemetry.addData("Transfer", transferOn ? "ON" : "OFF");
            telemetry.addData("Outtake (Flywheel)", outtakeOn ? "ON" : "OFF");
            telemetry.update();
        }
    }
}

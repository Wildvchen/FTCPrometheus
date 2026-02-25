package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutonSupersCloseRed3Ball", group = "Autonomous")
public class AutoCloseRedSimple extends LinearOpMode {
    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private DcMotor intakeMotor1, intakeMotor2;
    private DcMotor outtakeMotor1, outtakeMotor2;
    private DcMotor transferMotor;

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
        // 1. Turn on Outtake motors
        setOuttakePower(1.0);

        // 2. Start moving backward
        leftFrontDrive.setPower(-0.5);
        rightFrontDrive.setPower(-0.5);
        leftBackDrive.setPower(-0.5);
        rightBackDrive.setPower(-0.5);

        // 3. Move backward for 0.5 seconds
        sleep(500);

        // 4. Turn on Intake and Transfer motors while still moving
        setIntakePower(1.0);
        setTransferPower(1.0);
    }
    public void moveForward(double power, long time) {
        leftFrontDrive.setPower(power);
        rightFrontDrive.setPower(power);
        leftBackDrive.setPower(power);
        rightBackDrive.setPower(power);
        sleep(time);
        stopRobot();
    }

    public void moveBackward(double power, long time) {
        leftFrontDrive.setPower(-power);
        rightFrontDrive.setPower(-power);
        leftBackDrive.setPower(-power);
        rightBackDrive.setPower(-power);
        sleep(time);
        stopRobot();
    }

    public void setIntakePower(double power) {
        intakeMotor1.setPower(power);
        intakeMotor2.setPower(power);
    }

    public void setOuttakePower(double power) {
        outtakeMotor1.setPower(power);
        outtakeMotor2.setPower(power);
    }

    public void setTransferPower(double power) {
        transferMotor.setPower(power);
    }

    public void stopAllMechanisms() {
        setIntakePower(0);
        setOuttakePower(0);
        setTransferPower(0);
    }

    public void turnLeft(double power, long time) {
        leftFrontDrive.setPower(-power);
        rightFrontDrive.setPower(power);
        leftBackDrive.setPower(-power);
        rightBackDrive.setPower(power);
        sleep(time);
        stopRobot();
    }

    public void turnRight(double power, long time) {
        leftFrontDrive.setPower(power);
        rightFrontDrive.setPower(-power);
        leftBackDrive.setPower(power);
        rightBackDrive.setPower(-power);
        sleep(time);
        stopRobot();
    }

    public void strafeLeft(double power, long time) {
        leftFrontDrive.setPower(-power);
        rightFrontDrive.setPower(power);
        leftBackDrive.setPower(power);
        rightBackDrive.setPower(-power);
        sleep(time);
        stopRobot();
    }

    public void strafeRight(double power, long time) {
        leftFrontDrive.setPower(power);
        rightFrontDrive.setPower(-power);
        leftBackDrive.setPower(-power);
        rightBackDrive.setPower(power);
        sleep(time);
        stopRobot();
    }

    public void stopRobot() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }
}

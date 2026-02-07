package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Auto: Shoot 1 Ball", group = "Autonomous")
public class Auto extends LinearOpMode {

    // --- Constants from PrometheusDriver ---
    static final double TICKS_PER_REV = 8192;
    static final int TICKS_FOR_60_DEGREES = (int)(TICKS_PER_REV / 6.0);
    
    static final double KICKER_RETRACED = 0.0;
    static final double KICKER_EXTENDED = 0.22;

    static final double SPINUP_TIME = 2.0;
    static final double KICK_TIME = 0.3;
    static final double RESET_TIME = 1.0; 

    // Hardware
    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private DcMotor spindexer, outtakeMotor1, outtakeMotor2;
    private Servo kickerServo1, kickerServo2;

    @Override
    public void runOpMode() {
        // --- Initialization ---
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive   = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive  = hardwareMap.get(DcMotor.class, "right_back_drive");

        spindexer = hardwareMap.get(DcMotor.class, "spindexer");
        spindexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        outtakeMotor1 = hardwareMap.get(DcMotor.class, "outtake_motor1");
        outtakeMotor2 = hardwareMap.get(DcMotor.class, "outtake_motor2");

        kickerServo1 = hardwareMap.get(Servo.class, "kicker_1");
        kickerServo2 = hardwareMap.get(Servo.class, "kicker_2");

        // Directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        outtakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        kickerServo1.setDirection(Servo.Direction.REVERSE);
        kickerServo2.setDirection(Servo.Direction.FORWARD);

        // Reset Spindexer
        spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spindexer.setTargetPosition(0);
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.5);

        // Kicker to start position
        kickerServo1.setPosition(KICKER_RETRACED);
        kickerServo2.setPosition(KICKER_RETRACED);

        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        // --- STEP 1: Modifiable Movement ---
        // Change these values to adjust where the robot goes
        driveForward(0.5, 1000); // Power 0.5 for 1000ms
        strafeRight(0.5, 500);    // Power 0.5 for 500ms
        
        sleep(500); // Stabilize

        // --- STEP 2: Shoot 1 Ball ---
        telemetry.addData("Status", "Spinning up flywheels");
        telemetry.update();
        outtakeMotor1.setPower(1.0);
        outtakeMotor2.setPower(1.0);
        sleep((long)(SPINUP_TIME * 1000));

        telemetry.addData("Status", "Indexing ball");
        telemetry.update();
        spindexer.setTargetPosition(TICKS_FOR_60_DEGREES);
        while (opModeIsActive() && spindexer.isBusy()) {
            // Wait for spindexer to reach 60 degrees
        }
        sleep(200);

        telemetry.addData("Status", "Kicking");
        telemetry.update();
        kickerServo1.setPosition(KICKER_EXTENDED);
        kickerServo2.setPosition(KICKER_EXTENDED);
        sleep((long)(KICK_TIME * 1000));

        telemetry.addData("Status", "Resetting Kicker");
        telemetry.update();
        kickerServo1.setPosition(KICKER_RETRACED);
        kickerServo2.setPosition(KICKER_RETRACED);
        
        // Final wait to ensure ball is gone and kicker is safe for TeleOp
        sleep((long)(RESET_TIME * 1000));

        outtakeMotor1.setPower(0.0);
        outtakeMotor2.setPower(0.0);

        // --- STEP 3: Optional Parking ---
        // driveForward(0.4, 500);

        telemetry.addData("Status", "Done");
        telemetry.update();
    }

    // --- Helper Methods for Modifiable Movement ---

    public void driveForward(double power, long time) {
        setDrivePower(power, power, power, power);
        sleep(time);
        stopDrive();
    }

    public void driveBackward(double power, long time) {
        setDrivePower(-power, -power, -power, -power);
        sleep(time);
        stopDrive();
    }

    public void strafeLeft(double power, long time) {
        setDrivePower(-power, power, power, -power);
        sleep(time);
        stopDrive();
    }

    public void strafeRight(double power, long time) {
        setDrivePower(power, -power, -power, power);
        sleep(time);
        stopDrive();
    }

    public void turnLeft(double power, long time) {
        setDrivePower(-power, -power, power, power);
        sleep(time);
        stopDrive();
    }

    public void turnRight(double power, long time) {
        setDrivePower(power, power, -power, -power);
        sleep(time);
        stopDrive();
    }

    private void setDrivePower(double lf, double lb, double rf, double rb) {
        leftFrontDrive.setPower(lf);
        leftBackDrive.setPower(lb);
        rightFrontDrive.setPower(rf);
        rightBackDrive.setPower(rb);
    }

    private void stopDrive() {
        setDrivePower(0, 0, 0, 0);
    }
}

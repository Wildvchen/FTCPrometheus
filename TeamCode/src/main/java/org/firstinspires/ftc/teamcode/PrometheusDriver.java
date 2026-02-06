package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Mecanum Drive + Spindexer + Kicker + Dual Outtake + Intake", group = "Linear Opmode")
public class PrometheusDriver extends LinearOpMode {

    // REV Through Bore Encoder constants
    static final double TICKS_PER_REV = 8192;
    static final int TICKS_FOR_120_DEGREES = (int)(TICKS_PER_REV / 3.0);

    // Kicker constants
    static final double KICKER_RETRACED = 0.0;
    static final double KICKER_EXTENDED = 0.5;

    // Timing constants for the kick sequence
    static final double SPINUP_TIME = 1;
    static final double KICK_TIME = 0.2;
    static final double RETRACT_TIME = 0.6; // Time for kicker to retract and spindexer to rotate

    private enum KickState {
        IDLE,
        SPINUP,
        KICK,
        RETRACT
    }

    @Override
    public void runOpMode() {
        // Initialize drive motors
        DcMotor leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        DcMotor leftBackDrive   = hardwareMap.get(DcMotor.class, "left_back_drive");
        DcMotor rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        DcMotor rightBackDrive  = hardwareMap.get(DcMotor.class, "right_back_drive");

        // Initialize spindexer motor
        DcMotor spindexer = hardwareMap.get(DcMotor.class, "spindexer");

        // Initialize outtake motors (Flywheels)
        DcMotor outtakeMotor1 = hardwareMap.get(DcMotor.class, "outtake_motor1");
        DcMotor outtakeMotor2 = hardwareMap.get(DcMotor.class, "outtake_motor2");

        // Initialize outtake turning motor
        DcMotor outtakeTurnMotor = hardwareMap.get(DcMotor.class, "outtake_turn");

        // Initialize intake motor
        DcMotor intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");

        // Initialize kicker servos
        Servo kickerServo1 = hardwareMap.get(Servo.class, "kicker_1");
        Servo kickerServo2 = hardwareMap.get(Servo.class, "kicker_2");

        // Set directions
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        // Outtake motors: often one needs to be reversed if they are facing each other
        outtakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        // Configure spindexer to use encoder
        spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spindexer.setTargetPosition(0);
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.5);

        // Initialize kicker position
        kickerServo1.setPosition(KICKER_RETRACED);
        kickerServo2.setPosition(KICKER_RETRACED);

        // Track state for buttons and sequences
        boolean lastYState = false;
        boolean lastAState = false;
        int spindexerTarget = 0;
        int ballsKicked = 0;

        KickState currentKickState = KickState.IDLE;
        ElapsedTime kickTimer = new ElapsedTime();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // --- Drive Logic (Mecanum - Gamepad 1) ---
            double y  = -gamepad1.left_stick_y;
            double x  =  gamepad1.left_stick_x;
            double rx =  gamepad1.right_stick_x;

            double frontLeftPower  = y + x + rx;
            double backLeftPower   = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower  = y + x - rx;

            double max = Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower  /= max;
                backLeftPower   /= max;
                frontRightPower /= max;
                backRightPower  /= max;
            }

            leftFrontDrive.setPower(frontLeftPower);
            leftBackDrive.setPower(backLeftPower);
            rightFrontDrive.setPower(frontRightPower);
            rightBackDrive.setPower(backRightPower);

            // --- Intake Logic (Gamepad 1) ---
            // Bumpers toggle intake on (forward/reverse), B button stops it.
            if (gamepad1.right_bumper) {
                intakeMotor.setPower(1.0);
            } else if (gamepad1.left_bumper) {
                intakeMotor.setPower(-1.0);
            } else if (gamepad1.b) {
                intakeMotor.setPower(0.0);
            }

            // --- Outtake Turning Logic (Gamepad 2) ---
            // Controlling the horizontal turning of the outtake assembly
//            double turnPower = gamepad2.left_stick_x;
//            outtakeTurnMotor.setPower(turnPower);

            // --- Spindexer Logic (Manual Y press - Gamepad 1) ---
            if (gamepad1.y) {
                if (!lastYState) {
                    spindexerTarget += TICKS_FOR_120_DEGREES;
                    spindexer.setTargetPosition(spindexerTarget);
                }
                lastYState = true;
            } else {
                lastYState = false;
            }

            // --- Automated Kick Sequence (A button for 3 balls - Gamepad 1) ---
            switch (currentKickState) {
                case IDLE:
                    outtakeMotor1.setPower(0.0);
                    outtakeMotor2.setPower(0.0);
                    if (gamepad1.a && !lastAState) {
                        ballsKicked = 0;
                        currentKickState = KickState.SPINUP;
                        kickTimer.reset();
                    }
                    break;

                case SPINUP:
                    outtakeMotor1.setPower(1.0);
                    outtakeMotor2.setPower(1.0);
                    if (kickTimer.seconds() >= SPINUP_TIME) {
                        currentKickState = KickState.KICK;
                        kickTimer.reset();
                        kickerServo1.setPosition(KICKER_EXTENDED);
                        kickerServo2.setPosition(KICKER_EXTENDED);
                    }
                    break;

                case KICK:
                    outtakeMotor1.setPower(1.0);
                    outtakeMotor2.setPower(1.0);
                    if (kickTimer.seconds() >= KICK_TIME) {
                        ballsKicked++;
                        kickerServo1.setPosition(KICKER_RETRACED);
                        kickerServo2.setPosition(KICKER_RETRACED);

                        if (ballsKicked < 3) {
                            spindexerTarget += TICKS_FOR_120_DEGREES;
                            spindexer.setTargetPosition(spindexerTarget);

                            currentKickState = KickState.RETRACT;
                            kickTimer.reset();
                        } else {
                            currentKickState = KickState.IDLE;
                        }
                    }
                    break;

                case RETRACT:
                    outtakeMotor1.setPower(1.0);
                    outtakeMotor2.setPower(1.0);
                    if (kickTimer.seconds() >= RETRACT_TIME) {
                        currentKickState = KickState.KICK;
                        kickTimer.reset();
                        kickerServo1.setPosition(KICKER_EXTENDED);
                        kickerServo2.setPosition(KICKER_EXTENDED);
                    }
                    break;
            }
            lastAState = gamepad1.a;

            // --- Telemetry ---
            telemetry.addData("Status", "Running");
            telemetry.addData("Kick State", currentKickState);
            //telemetry.addData("Outtake Turn Power", turnPower);
            telemetry.addData("Spindexer Target", spindexerTarget);
            telemetry.update();
        }
    }
}

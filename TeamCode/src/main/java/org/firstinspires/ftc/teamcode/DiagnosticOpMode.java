package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Diagnostic OpMode", group = "Diagnostics")
public class DiagnosticOpMode extends LinearOpMode {

    private DcMotor intakeMotor, transferMotor;
    private Servo kickerServo1, kickerServo2;

    private double kickerPos = 0.0;
    private boolean intakeOn = false;
    private boolean transferOn = false;
    
    private boolean lastA = false;
    private boolean lastB = false;

    @Override
    public void runOpMode() {
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer_motor");
        kickerServo1 = hardwareMap.get(Servo.class, "kicker_servo1");
        kickerServo2 = hardwareMap.get(Servo.class, "kicker_servo2");

        kickerServo1.setDirection(Servo.Direction.FORWARD);
        kickerServo2.setDirection(Servo.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Test Intake (Button A Toggle)
            if (gamepad1.a && !lastA) intakeOn = !intakeOn;
            lastA = gamepad1.a;
            intakeMotor.setPower(intakeOn ? 1.0 : 0.0);

            // Test Transfer (Button B Toggle)
            if (gamepad1.b && !lastB) transferOn = !transferOn;
            lastB = gamepad1.b;
            transferMotor.setPower(transferOn ? 1.0 : 0.0);

            // Test Servos (X = REST, Y = UP, Dpad = Fine tune)
            if (gamepad1.x) kickerPos = 0.0;
            if (gamepad1.y) kickerPos = 0.5;
            
            if (gamepad1.dpad_up) kickerPos += 0.001;
            if (gamepad1.dpad_down) kickerPos -= 0.001;
            
            // Clamp position
            kickerPos = Math.max(0.0, Math.min(1.0, kickerPos));
            
            kickerServo1.setPosition(kickerPos);
            kickerServo2.setPosition(kickerPos);

            telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
            telemetry.addData("Transfer", transferOn ? "ON" : "OFF");
            telemetry.addData("Kicker Target Pos", kickerPos);
            telemetry.addData("Kicker Servo 1 Pos", kickerServo1.getPosition());
            telemetry.addData("Kicker Servo 2 Pos", kickerServo2.getPosition());
            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class OutTakeTest extends LinearOpMode{
    @Override
    public void runOpMode() {
        DcMotor outtakeMotor1 = hardwareMap.get(DcMotor.class, "outtake_motor1");
        DcMotor outtakeMotor2 = hardwareMap.get(DcMotor.class, "outtake_motor2");

        waitForStart();
        while(opModeIsActive() ){
            outtakeMotor1.setPower(0.1);
            outtakeMotor2.setPower(0.1);
        }
    }
}

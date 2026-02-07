package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Spindexer Encoder Test", group = "Test")
public class EncoderTest extends LinearOpMode {

    private DcMotor spindexer;
    private boolean lastAState = false;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initializing...");
        telemetry.update();

        // Initialize the spindexer motor
        // The encoder is assumed to be plugged into the same port as this motor
        spindexer = hardwareMap.get(DcMotor.class, "spindexer");

        // We need to set a run mode to use the encoder, but we won't be setting a target.
        // STOP_AND_RESET_ENCODER is a good initial state.
        spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // Use RUN_WITHOUT_ENCODER so we can control the motor manually if needed,
        // but still read the encoder value.
        spindexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        telemetry.addData("Status", "Initialized");
        telemetry.addData(">", "Press Start to begin.");
        telemetry.addData(">", "Manually rotate the spindexer to see tick counts.");
        telemetry.addData(">", "Press 'A' on Gamepad 1 to reset the encoder to 0.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Get the current position of the encoder
            int currentPosition = spindexer.getCurrentPosition();

            // Reset the encoder if the 'A' button is pressed
            if (gamepad1.a && !lastAState) {
                spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                // We must set a run mode again after resetting
                spindexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
            lastAState = gamepad1.a;

            // --- Telemetry ---
            telemetry.addData("Spindexer Encoder Ticks", currentPosition);
            telemetry.addData("Full Rotations", "%.2f", (double)currentPosition / 8192.0);
            telemetry.addData(">", "Press 'A' to reset.");
            telemetry.update();
        }
    }
}

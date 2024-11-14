/*
Copyright 2024 FIRST Tech Challenge Team 21881

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.lang.Math;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this OpMode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp

public class driveTrain extends LinearOpMode {
    private Blinker control_Hub;
    private Blinker expansion_Hub_2;
    private HardwareDevice webcam_1;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor hang;
    private Servo left_hand;
    
    
    private IMU imu;
    
    
    
    
    
    public void driveInDirection(double direction, double power, double right_stick) {
        double x = power*Math.cos(direction);
        double y = power*Math.sin(direction);
        
        frontLeft.setPower(-(y-x-right_stick)); 
        frontRight.setPower(-(y+x+right_stick));
        backLeft.setPower(-(y+x-right_stick));
        backRight.setPower((y-x+right_stick));
    }


    @Override
    public void runOpMode() {
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        expansion_Hub_2 = hardwareMap.get(Blinker.class, "Expansion Hub 2");
        webcam_1 = hardwareMap.get(HardwareDevice.class, "Webcam 1");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        hang = hardwareMap.get(DcMotor.class, "hang");
        left_hand = hardwareMap.get(Servo.class, "left_hand");
        
        imu = hardwareMap.get(IMU.class, "imu");
        
        
        IMU.Parameters params;

        params = new IMU.Parameters(
             new RevHubOrientationOnRobot(
                  RevHubOrientationOnRobot.LogoFacingDirection.UP,
                  RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
             )
        );
        
        imu.initialize(params);
        
        YawPitchRollAngles robotOrientation;
        double Yaw;
        double relativeDirection;
        
        

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        
        double x;
        double y;
        double direction;
        double power;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            robotOrientation = imu.getRobotYawPitchRollAngles();
            x = (double)this.gamepad1.left_stick_x;
            y = (double)this.gamepad1.left_stick_y;
            
            // set direction to the joystick direction minus the robot direction (impliment later)
            direction = Math.atan2(y, x);
            Yaw = robotOrientation.getYaw(AngleUnit.RADIANS);
            
            //Yaw = -Yaw; // These next few lines are all for translating the yaw into the same format as the joystick
            
            /*
            Yaw -= Math.PI/2;
            if (Yaw < -Math.PI) {
                Yaw += 2*Math.PI;
            
            } else if (Yaw > Math.PI) {
                Yaw -= 2*Math.PI; 
            }
            */
            relativeDirection = direction-Yaw;
            if (relativeDirection < -Math.PI) {
                relativeDirection += 2*Math.PI;
            } else if (relativeDirection > Math.PI) {
                relativeDirection -= 2*Math.PI;
            }
            
            
    
    
            if(this.gamepad1.x) {
                power = Math.sqrt(x*x+y*y) * 1.5; // this is if we need to go fast
            } else {
                power = Math.sqrt(x*x+y*y) * 0.5; // this is without the button, so it moves a little slower for precision
            }
            
            if(this.gamepad1.start && this.gamepad1.dpad_up) {
                imu.resetYaw();
            }
            
            telemetry.addData("Joystick Direction", String.valueOf(direction));
            telemetry.addData("Yaw", String.valueOf(Yaw));
            telemetry.addData("Relative Direction", String.valueOf(relativeDirection));
            telemetry.addData("Power", String.valueOf(power));
            
            
            
            
            driveInDirection(relativeDirection, power, (double)this.gamepad1.right_stick_x);
            
            telemetry.addData("Status", "Running");
            telemetry.update();

        }
    }

    
}
// This is the code that had the IMU, but the IMU keeps killing itself after a while.

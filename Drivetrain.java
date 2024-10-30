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
// THIS CODE DOES NOT WORK
// THIS CODE DOES NOT WORK
// THIS CODE DOES NOT WORK
// THIS CODE DOES NOT WORK
// THIS CODE DOES NOT WORK

package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

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
    private BNO055IMU imu;
    private Orientation lastAngles = new Orientation();
    private double globalAngle, power = .30, correction;
    
    
    
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
        
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES; //Potentially change radians to degrees
        //parameters.accelUnit = BNO055IMU.AngleUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = true;
        
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        
        imu.initialize(parameters);
        
        telemetry.addData("Mode", "calibrating...");
        
        while(!isStopRequested() && !imu.isGyroCalibrated())
        {
            sleep(50);
            idle();
        }
        
        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calibration status", imu.getCalibrationStatus().toString());
        telemetry.update();
        
    


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        
        

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double x = (double)this.gamepad1.left_stick_x;
            double y = (double)this.gamepad1.left_stick_y;
            
            // set direction to the joystick direction minus the robot direction (impliment later)
            double direction = Math.atan2(y, x);
            double power;
            
            if(this.gamepad1.x) {
                power = Math.sqrt(x*x+y*y) * 1.5; // this is if we need to go fast
            } else {
                power = Math.sqrt(x*x+y*y) * 0.5; // this is without the button, so it moves a little slower for precision
            }
            
            
            telemetry.addData("Direction", String.valueOf(direction));
            telemetry.addData("Power", String.valueOf(power));
            //telemetry.addData("IMU", String.valueOf(imu.getRobotYawPitchRollAngles()));
            telemetry.addData("IMU 2 Electric Boogaloo", String.valueOf(imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)));
            
            
            driveInDirection(direction, power, (double)this.gamepad1.right_stick_x);
            
            telemetry.addData("Status", "Running");
            telemetry.update();

        }
    }
    private double getAngle(){
    
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;
        
        if (deltaAngle < -180){
            deltaAngle += 360;
        } else if (deltaAngle > 180){
            deltaAngle -= 360;
        }    
        globalAngle += deltaAngle;
        
        lastAngles = angles;
        
        return globalAngle;
        
}
    
}





// THIS CODE DOES NOT WORK

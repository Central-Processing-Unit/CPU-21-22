package org.firstinspires.ftc.teamcode.autonomous.hardware;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class Hardware {
    public DcMotor leftFrontMotor, rightFrontMotor, leftBackMotor, rightBackMotor;
    public BNO055IMU gyro;
    public WebcamName camera;

    public Hardware (HardwareMap hardware)
    {
        initializeGyro(hardware);
        initializeMotors(hardware);
        //initializeWebcam(hardware);
    }

    private void initializeMotors(HardwareMap hardware)
    {
        leftFrontMotor = hardware.dcMotor.get("leftFrontMotor");
        leftFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftBackMotor = hardware.dcMotor.get("leftBackMotor");
        leftBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightFrontMotor = hardware.dcMotor.get("rightFrontMotor");
        rightFrontMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        rightBackMotor = hardware.dcMotor.get("rightBackMotor");
        rightBackMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBackMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setMotorValues(double posinput, double neginput)
    {
        leftFrontMotor.setPower(posinput);
        rightFrontMotor.setPower(-neginput);
        leftBackMotor.setPower(neginput);
        rightBackMotor.setPower(-posinput);
    }

    private void initializeGyro(HardwareMap hardware)
    {
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        gyro = hardware.get(BNO055IMU.class, "imu");
        gyro.initialize(params);
    }

    private void initializeWebcam(HardwareMap hardware)
    {
        camera = hardware.get(WebcamName.class, "PrimaryWebcam");
    }
}

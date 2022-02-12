package org.firstinspires.ftc.teamcode.autonomous.actions;

import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.autonomous.AutonCore;
import org.firstinspires.ftc.teamcode.autonomous.Constants;
import org.firstinspires.ftc.teamcode.autonomous.actions.util.ObjectDetector;
import org.firstinspires.ftc.teamcode.autonomous.control.PID;
import org.firstinspires.ftc.teamcode.autonomous.hardware.Hardware;
import org.firstinspires.ftc.teamcode.autonomous.localization.Localization;
import org.firstinspires.ftc.teamcode.autonomous.localization.Position;
import org.firstinspires.ftc.teamcode.autonomous.nav.path.LinearPath;
import org.firstinspires.ftc.teamcode.autonomous.vision.Vuforia;
import org.firstinspires.ftc.teamcode.autonomous.nav.Navigation;

public class PlaceCubeAction extends Action {
    private final Navigation navigation;

    private double armPos;
    private double prevArmPos;

    public PlaceCubeAction(int index, Navigation navigation) {
        super(index);
        this.navigation = navigation;
    }

    @Override
    public void execute(Hardware hardware, Localization localization, Vuforia vuforia, ObjectDetector detector) {
        ObjectDetector.BarcodeLocation location = detector.calculateState();

        PID slidePID = new PID(new PIDCoefficients(0.008, 0.00005, 0));

        double slideLevel = 5;

        switch (location)
        {
            case LEFT:
                if (Constants.IS_BLUE_TEAM)
                    slideLevel = 500;
                else
                    slideLevel = 0;
                break;
            case CENTER:
                slideLevel = 200;
                break;
            case RIGHT:
                if (Constants.IS_BLUE_TEAM)
                    slideLevel = 0;
                else
                    slideLevel = 500;
                break;
            default:
                break;
        }
        if (Constants.IS_BLUE_TEAM)
        {
            Position pos;

            if (!Constants.IS_LEFT_OPMODE)
            {
                pos = new Position(700, 1830, Constants.CURRENT_INITIAL_THETA);

            } else
            {
                pos = new Position(700, 1234, Constants.CURRENT_INITIAL_THETA);
            }

            moveToLevel(slideLevel, hardware, slidePID);
        } else
        {
            Position pos;

            if (!Constants.IS_LEFT_OPMODE)
            {
                pos = new Position(700, 1234, Constants.CURRENT_INITIAL_THETA);
            } else
            {
                pos = new Position(700, 1830, Constants.CURRENT_INITIAL_THETA);
            }

            moveToLevel(slideLevel, hardware, slidePID);

        }
    }

    private void moveToLevel(double ticks, Hardware hardware, PID pid)
    {
        armPos = hardware.armMotor.getCurrentPosition();

        ElapsedTime time = new ElapsedTime();

        while (armPos < ticks - 20)
        {
            if (armPos > 200) {
                hardware.boxServo.setPosition(0.50);
            }

            double error = ticks - armPos;

            hardware.armMotor.setPower(0.2 * pid.getOutput(error, 0));

            armPos = hardware.armMotor.getCurrentPosition();

            AutonCore.telem.addData("TARGET POS", ticks);
            AutonCore.telem.addData("CURRENT POS", armPos);
            AutonCore.telem.update();
        }

        time.reset();

        hardware.boxServo.setPosition(1);

        while (time.milliseconds() < 2000) {

        }


        armPos = hardware.armMotor.getCurrentPosition();

        while (armPos > 10)
        {
            if (armPos < 300)
                hardware.boxServo.setPosition(0.7);

            double error = 10 - armPos;

            hardware.armMotor.setPower(0.2 * pid.getOutput(error, 0));

            armPos = hardware.armMotor.getCurrentPosition();
        }
    }
}

package org.firstinspires.ftc.teamcode.autonomous.waypoint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.autonomous.AutonCore;
import org.firstinspires.ftc.teamcode.autonomous.control.PID;
import org.firstinspires.ftc.teamcode.autonomous.hardware.Hardware;
import org.firstinspires.ftc.teamcode.autonomous.localization.Localization;
import org.firstinspires.ftc.teamcode.autonomous.localization.Position;
import org.firstinspires.ftc.teamcode.autonomous.localization.Velocity;

import java.util.ArrayList;

public class Navigation extends AutonCore {
    private Hardware _hardware;
    private Localization _localization;
    private ElapsedTime runtime;
    private PID controller;
    private Position position = new Position();
    private Velocity velocity = new Velocity();
    private Telemetry telem;

    /*
    Holds waypoints that we can drive to. This allows for the robot to split a move up into
    multiple linear movements. This allows the robot to avoid obstacles and for movement to be planned.

    In the future, this feature may be used to add a GUI for motion planning.
     */
    private ArrayList<Waypoint> waypoints;
    private int index = 0;

    public Navigation(Hardware hardware, Localization localization, ElapsedTime runtime, Telemetry telemetry)
    {
        telem = telemetry;
        this.runtime = runtime;
        _hardware = hardware;
        _localization = localization;

        PIDCoefficients coefficients = new PIDCoefficients(0.001, 0, 0);

        controller = new PID(coefficients);

        waypoints = new ArrayList<>();
    }

    public void addWayPointToQueue(Waypoint waypoint)
    {
        waypoints.add(index, waypoint);
        index++;
    }

    public void executeTask()
    {
        for (Waypoint waypoint : waypoints)
        {
            driveToStart(waypoint);
            driveToTarget(waypoint);
        }

        waypoints.clear();
        index = 0;
    }

    private void driveToStart(Waypoint waypoint)
    {
        //Drive to starting location of waypoint. Robot will take the shortest possible path.
        while((Math.abs(waypoint.startingPos.x - position.x) > 5) ||
                (Math.abs(waypoint.startingPos.y - position.y) > 5))
        {
            position = _localization.getRobotPosition();
            _localization.increment(position);
            velocity = _localization.getRobotVelocity(runtime);

            double orientation, negOutput, posOutput;

            if (waypoint.targetPos.x - position.x > 0)
                orientation = Math.atan(controller.getSlope(position, waypoint.startingPos)) - Math.PI / 4;
            else
                orientation = Math.atan(controller.getSlope(position, waypoint.startingPos)) + Math.PI - Math.PI / 4;

            negOutput = controller.getMagnitude(waypoint.startingPos, position, velocity) * Math.sin(orientation);
            if (orientation == 0)
                posOutput = negOutput;
            else
                posOutput = controller.getMagnitude(waypoint.startingPos, position, velocity) * Math.cos(orientation);

            _hardware.setMotorValues(posOutput, negOutput);
        }
    }

    private void driveToTarget(Waypoint waypoint)
    {
        //Assume that starting position has been reached. Drive to target specified by waypoint.
        while(((Math.abs(waypoint.targetPos.x - position.x) > 5) || (Math.abs(waypoint.targetPos.y - position.y) > 5)) && !isStopRequested())
        {
            position = _localization.getRobotPosition();
            _localization.increment(position);
            velocity = _localization.getRobotVelocity(runtime);

            double orientation, magnitude, negOutput, posOutput;

            if (waypoint.targetPos.x - position.x > 0)
                orientation = Math.atan(controller.getSlope(waypoint.targetPos, position)) - Math.PI / 4;
            else
                orientation = Math.atan(controller.getSlope(waypoint.targetPos, position)) + Math.PI - Math.PI / 4;

            magnitude = controller.getMagnitude(waypoint.targetPos, position, velocity);

            negOutput = magnitude * Math.sin(orientation);
            if (orientation == 0)
                posOutput = negOutput;
            else
                posOutput = magnitude * Math.cos(orientation);

            telem.addData("X", position.x);
            telem.addData("Y", position.y);
            telem.addData("T", position.t);
            telem.addData("Magnitude", magnitude);
            telem.addData("Orientation", orientation);
            telem.addData("Velocity", Math.sqrt(Math.pow(velocity.dx, 2) + Math.pow(velocity.dy, 2)));
            telem.update();

            _hardware.setMotorValues(posOutput, negOutput);
        }
    }
}
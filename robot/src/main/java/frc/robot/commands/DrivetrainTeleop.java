/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;

/**
 * Manually control the drivetrain using control inputs
 */
public class DrivetrainTeleop extends Command {
    
    // Keep an instance of the drivetrain around
    private Drivetrain mDrivetrain;

    public DrivetrainTeleop() {
        mDrivetrain = Drivetrain.getInstance();

        // Calling requires() on a subsystem indicates to the scheduler that
        // this command requires the use a particular subsystem, and that any
        // currently running commands that use said subsystem will need to be
        // interrupted()
        requires(mDrivetrain);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        double moveSpeed = Robot.m_oi.getDriveSpeed();
        double turnRate = Robot.m_oi.getTurnSpeed();
        boolean quickturn = Robot.m_oi.getQuickTurn();

        // Perform curvature drive using gamepad input
        mDrivetrain.curvatureDrive(moveSpeed, turnRate, quickturn);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        // This should never finish
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Claw;
import frc.robot.subsystems.Claw.ControlMode;
import frc.robot.subsystems.Claw.SpinMode;
import frc.robot.subsystems.Claw.TargetMode;

public class ClawDefaultCommand extends Command {
    private Claw mClaw;

    public ClawDefaultCommand() {
        mClaw = Claw.getInstance();

        requires(mClaw);
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println("initializing claw state machine");
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        if (mClaw.getTargetMode() == TargetMode.CARGO) {
            // If the claw is in automatic mode (i.e. no input), determine the speeds
            if (mClaw.getControlMode() == ControlMode.AUTO) {
                // If there is cargo loaded, HOLD it
                if (mClaw.cargoLeftPresent() && mClaw.cargoRightPresent()) {
                    mClaw.setSpinMode(SpinMode.HOLD);
                }
                else {
                    mClaw.setSpinMode(SpinMode.STOP);
                }
            }
            // Otherwise we just use the ensureMode functionality to correctly spin
        }
        else if (mClaw.getTargetMode() == TargetMode.HATCH) {
            // If the claw is in automatic mode (i.e. no input), determine the speeds
            if (mClaw.getControlMode() == ControlMode.AUTO) {
                // If there is cargo loaded, HOLD it
                if (mClaw.hatchLeftPresent() && mClaw.hatchRightPresent()) {
                    mClaw.setSpinMode(SpinMode.HOLD);
                }
                else {
                    mClaw.setSpinMode(SpinMode.STOP);
                }
            }
            // Otherwise we just use the ensureMode functionality to correctly spin
        }

        // Make the appropriate adjustments
        mClaw.ensureMode();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
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

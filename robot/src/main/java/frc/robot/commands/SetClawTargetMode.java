/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Claw;

public class SetClawTargetMode extends Command {

  // Keep an instance of the drivetrain around
  private Claw mClaw;
  private Claw.TargetMode mMode;

  public SetClawTargetMode(Claw.TargetMode mode) {
    mClaw = Claw.getInstance();
    mMode = mode;
    requires(mClaw);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Starting SetClawTargetMode");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    this.mClaw.setTargetMode(this.mMode);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return true;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    System.out.println("Ending SetClawTargetMode");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    System.out.println("IEnding SetClawTargetMode");
  }
}

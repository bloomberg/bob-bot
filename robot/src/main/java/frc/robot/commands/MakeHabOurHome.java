/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

public class MakeHabOurHome extends Command {
  // Keep an instance of the drivetrain around
  private Climber climber = Climber.getInstance();
  private Drivetrain driveTrain = Drivetrain.getInstance();

  private final double DRIVE_WHEELS_SPEED = .6f;

  public MakeHabOurHome(){
    requires(climber);
    requires(driveTrain);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Starting MakeHabOurHome");

    climber.retractArms();
    climber.retractLegs();

    // TODO: is necessary to drive forward??
    driveTrain.arcadeDrive(DRIVE_WHEELS_SPEED, 0f);
  }

  // Called repeatedly when this Command is scheduled to run
  // This is blank because the comment is called continuously by
  // the button that is connected to it
  @Override
  protected void execute() {
    
  }

  // Make this return true when this Command no longer needs to run execute()
  // This only ends when killed by the button its connected to
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    driveTrain.arcadeDrive(0f, 0f);
    System.out.println("Ending MakeHabOurHome");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    System.out.println("MakeHabOurHome Interrupted");
    end();
  }
}

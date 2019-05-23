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

/* 
 * Command to drive onto either hab levels 2 or 3
 * 
 * Start: Expected to be run when the robot even with hab levels
 * 2 or 3. That is, ClimbToHab{2,3} have completed execution.
 * 
 * When Run: 
 *  The arm wheels will spin
 *  The drivetrain wheels will move forward
 * 
 * When Complete:
 *  The robot drive wheels will be as far forward as the extended arm
 *  and extended legs will allow it to be
 * 
 */
public class DriveOntoHab extends Command {
  // Keep an instance of the drivetrain around
  private Drivetrain driveTrain = Drivetrain.getInstance();
  private Climber climber = Climber.getInstance();

  private final double DRIVE_WHEELS_SPEED = .6f;

  public DriveOntoHab(){
    requires(driveTrain);
    requires(climber);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Starting DriveOntoHab");

    climber.spinWheels(true);
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
    climber.spinWheels(false);
    driveTrain.arcadeDrive(0f, 0f);
    System.out.println("Ending DriveOntoHab");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    System.out.println("DriveOntoHab Interrupted");
    end();
  }
}

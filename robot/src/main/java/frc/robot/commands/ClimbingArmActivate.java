/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Climber;

/* 
 * Command to extend (lower) both climbing arms and spin the wheels of both climbing arms.
 * We are now using these climbing arms for more than just climbing; we are attaching an
 * intake to help the claw get the cargo. So this command can be used for the climb and
 * also to intake cargo. As for climbing, once driver sees the front wheel is on hab it's just
 * a normal drive onto the platform.
 * 
 * When Run: 
 *  The two arms will be extended
 *  Both set of arm wheels will spin
 * 
 * Another button will retract (raise) the climbing arms and stop the wheels.
 */
public class ClimbingArmActivate extends Command {
  private Climber climber = Climber.getInstance();
  private boolean spinAndExtend;

  public ClimbingArmActivate(boolean spinAndExtend) {
    requires(climber);
    this.spinAndExtend = spinAndExtend;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Starting ClimbingArmActivate");

    if (spinAndExtend) {
      climber.spinWheels(true);
      climber.extendArms();
    } else {
      climber.spinWheels(false);
      climber.retractArms();
    }
  }

  // Called repeatedly when this Command is scheduled to run
  // This is blank because the comment is called continuously by
  // the button that is connected to it
  @Override
  protected void execute() {

  }

  // Make this return true when this Command no longer needs to run execute()
  // This only ends when killed by the button its connected to (??)
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    climber.spinWheels(false);
    System.out.println("Ending ClimbingArmActivate");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    System.out.println("ClimbingArmActivate Interrupted");
    end();
  }
}

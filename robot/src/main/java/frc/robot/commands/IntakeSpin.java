/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Intake;

/* 
 * Command to start or stop spinning the wheels on the end of the intake arms.
 */
public class IntakeSpin extends Command {
    private Intake intake = Intake.getInstance();
    private boolean spin;

    public IntakeSpin(boolean spin) {
        requires(intake);
        this.spin = spin;
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println("Starting IntakeSpin");

        if (spin) {
            intake.spinWheels(true);
        } else {
            intake.spinWheels(false);
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
        intake.stop();
        System.out.println("Ending IntakeSpin");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        System.out.println("IntakeSpin Interrupted");
        end();
    }
}
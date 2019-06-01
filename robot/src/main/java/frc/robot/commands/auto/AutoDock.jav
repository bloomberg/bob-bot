/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import java.util.logging.Logger;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;
import frc.robot.util.DriveHelper;
import frc.robot.util.DriveHelper.SpeedControl;
import frc.robot.util.drivers.Limelight;
import frc.robot.util.drivers.Limelight.LedMode;
import frc.robot.util.drivers.Limelight.Pipeline;
import frc.robot.util.drivers.Limelight.Values;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

public class AutoDock extends Command {
    private static final Logger sLogger = Logger.getLogger("AutoDock");
    
    /**
     * Theory of operation
     * 
     * The FAR_DETECT pipeline will have a target crosshair centered
     * on the target when the robot is right up against the hatch area
     * 
     * Note that due to how the camera is mounted on the robot, that there
     * is a region where the cargo intake arm will occlude the target. However
     * if we keep driving forward on last heading, we should be in a good
     * place to re-acquire the target.
     * 
     * 
     * This diagram shows the various points at which the target appears
     * as the robot moves towards it
     *  -------------------------------
     * |                               |
     * |                               |
     * |                               |
     * |                               |
     * |    X <-- kPreEclipseThresh    |
     * |                               |                          
     * |                               |
     * |                               |
     * |    X <-- kPostEclipseThresh   |
     * |                               |
     * |    X <-- Target Y value       |
     *  -------------------------------
     * 
     * kPreEclipseThresh represents the point JUST BEFORE the target disappears
     * from view (this includes points in which the targeting is unstable)
     * 
     * kPostEclipseThresh represents the point JUST AFTER the target reappears
     * into view
     * 
     * Each of these points represent `ty` values away from the target Y value
     */

    private static final double kPreEclipseThresh = 13.0;
    private static final double kPostEclipseThresh = 3.0;
    
    private static double kSteering;
    private static double kDistance;
    private static double kMinTurningCommand;

    // Hardware
    private Drivetrain mDrivetrain;
    private Limelight mLimelight;
    
    // PID Controller when in eclipse mode
    private PIDController mTurnController;
    private double mTurnToAngleRate = 0;

    private Phase mCurrentPhase;

    private DriveHelper mDriveHelper;

    private boolean mShouldTerminate = false;

    private static enum Phase {
        PRE_ECLIPSE,
        IN_ECLIPSE,
        POST_ECLIPSE,
        DONE
    }

    public AutoDock() {
        kSteering = mProfile.getLimelightKSteering();
        kDistance = mProfile.getLimelightKDistance();
        kMinTurningCommand = mProfile.getLimelightMinTurningCommand();

        mDrivetrain = Drivetrain.getInstance();
        mLimelight = Limelight.getInstance();

        mShouldTerminate = false;
        mDriveHelper = new DriveHelper();
        mNavX = mDrivetrain.getNavX();

        mTurnController = new PIDController(Constants.kTurnToAngleP,
                                            Constants.kTurnToAngleI,
                                            Constants.kTurnToAngleD,
                                            Constants.kTurnToAngleF,
                                            mNavX.getAHRS(),
                                            new PIDOutput(){
                                                @Override
                                                public void pidWrite(double output) {
                                                    mTurnToAngleRate = output;
                                                }
                                            });
        mTurnController.setInputRange(-180.0f, 180.0f);
        mTurnController.setOutputRange(-1.0, 1.0);
        mTurnController.setAbsoluteTolerance(Constants.kTurnToAngleToleranceDeg);
        mTurnController.setContinuous(true);
        mCurrentPhase = Phase.PRE_ECLIPSE;
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        mLimelight.setLeds(LedMode.PIPELINE);
        mLimelight.setActivePipeline(Pipeline.FAR_DETECT);
        // Pick up Limelight values
        Values initialValues = mLimelight.getRawValues();
        if (!initialValues.targetInSight) {
            // Bail out if we don't have a target
            mShouldTerminate = true;
            sLogger.info("No valid target. Terminating tracking");
        }
        else {
            if (initialValues.verticalOffset > kPreEclipseThresh) {
                mShouldTerminate = false;
                mCurrentPhase = Phase.PRE_ECLIPSE;
                sLogger.info("Starting Phase: PRE_ECLIPSE");
            }
            else if (initialValues.verticalOffset < kPostEclipseThresh) {
                mShouldTerminate = false;
                mCurrentPhase = Phase.POST_ECLIPSE;
                sLogger.info("Starting Phase: POST_ECLIPSE");
            }
            else {
                // we should bail out
                mShouldTerminate = true;
                sLogger.info("Target lies in eclipse zone. Cannot guarantee lock. Terminating tracking");
            }
        }
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        Values currValues = mLimelight.getRawValues();
        SpeedControl driveSpeeds;

        switch (mCurrentPhase) {
            case PRE_ECLIPSE:
                mShouldTerminate = false;
                driveSpeeds = calculateSpeeds(currValues);
                mDrivetrain.setOpenLoopSpeed(driveSpeeds.left, driveSpeeds.right);

                if (currValues.targetInSight && currValues.verticalOffset < kPreEclipseThresh) {
                    sLogger.info("Crossing Eclipse threshold. Transitioning to ECLIPSE");
                    sLogger.info("Reseting NavX and Enabling PID controller");
                    mNavX.reset();
                    mTurnController.enable();

                    mCurrentPhase = Phase.IN_ECLIPSE;
                }
                break;
            case IN_ECLIPSE:
                mShouldTerminate = false;
                double driveSpeed = 0.2; // we're driving blind here anyway...
                double turnSpeed = mTurnToAngleRate;
                driveSpeeds = mDriveHelper.arcadeDrive(driveSpeed, turnSpeed, false, false);
                mDrivetrain.setOpenLoopSpeed(driveSpeeds.left, driveSpeeds.right);

                if (currValues.targetInSight && currValues.verticalOffset < kPostEclipseThresh) {
                    sLogger.info("Crossing out of eclipse threshold. Transitioning to POST_ECLIPSE");
                    mTurnController.disable();

                    mCurrentPhase = Phase.POST_ECLIPSE;
                }
                break;
            case POST_ECLIPSE:
                mShouldTerminate = false;
                driveSpeeds = calculateSpeeds(currValues);
                mDrivetrain.setOpenLoopSpeed(driveSpeeds.left, driveSpeeds.right);

                if (currValues.targetInSight && currValues.verticalOffset < 1.0) {
                    sLogger.info("Close to target. Transitioning to DONE");
                    mCurrentPhase = Phase.DONE;
                }
                break;
            case DONE:
                mDrivetrain.setOpenLoopSpeed(0, 0);
        }
    }

    private SpeedControl calculateSpeeds(Values vals) {
        double headingError = vals.horizontalOffset;
        double distanceError = vals.verticalOffset;
        double steeringAdjust = 0.0;

        if (vals.horizontalOffset > 1.0) {
            steeringAdjust = kSteering * headingError - kMinTurningCommand;
        }
        else if (vals.horizontalOffset < 1.0) {
            steeringAdjust = kSteering * headingError + kMinTurningCommand;
        }

        double distanceAdjust = -(kDistance * distanceError) * 0.5;
        return mDriveHelper.autoDrive(distanceAdjust, steeringAdjust);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (mShouldTerminate) {
            sLogger.info("TERMINATING");
        }
        return mShouldTerminate;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        mDrivetrain.setOpenLoopSpeed(0, 0);
        mTurnController.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        sLogger.info("INTEERUPT");
        end();
    }
}

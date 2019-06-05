/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Constants;
import frc.robot.commands.DrivetrainTeleop;

/**
 * The drivetrain is compromised of the chassis, including the
 * different wheels and motors that actually allow the robot 
 * to drive, including any Encoders that let it track its position.
 */
public class Drivetrain extends Subsystem {

    // Generally, return a singleton instance of the subsystem
    private static Drivetrain sInstance;

    // Always return the same instance
    public static Drivetrain getInstance() {
        if (sInstance == null) {
            sInstance = new Drivetrain();
        }

        return sInstance;
    }
    
    // Motor controller declarations
    private CANSparkMax mLeftLeader, mLeftFollower, mRightLeader, mRightFollower;

    // Helper class for drivetrain control
    private DifferentialDrive mDiffDrive;

    /**
     * Constructor to instantiate all of our hardware-wrapping APIs
     */
    private Drivetrain() {
        // Instantiate the motor controllers using the defined CAN IDs
        mLeftLeader = new CANSparkMax(Constants.Drivetrain.kLeftLeaderId, MotorType.kBrushless);
        mLeftFollower = new CANSparkMax(Constants.Drivetrain.kLeftFollowerId, MotorType.kBrushless);
        mRightLeader = new CANSparkMax(Constants.Drivetrain.kRightLeaderId, MotorType.kBrushless);
        mRightFollower = new CANSparkMax(Constants.Drivetrain.kRightFollowerId, MotorType.kBrushless);

        setRampRate(.3);

        // We need one motor controller per motor, and we have two motors powering each side
        // of the drivetrain. We *could* send each speed controller per side the same command,
        // or make use of the CAN network, and slave one controller to another. This allows us
        // to manipulate a single controller, and any followers will automatically synchronize
        // their outputs to match
        mLeftFollower.follow(mLeftLeader);
        mRightFollower.follow(mRightLeader);

        // All CANSparkMax-s implement the SpeedController interface, and can thus be used in 
        // the DifferentialDrive helper.
        // The DifferentialDrive helper simplifies the logic required to drive the robot by
        // abstracting direct motor control away from the user, and instead providing an API
        // that allow users to pass in values from -1..1 in order to adjust speed and turning rate.
        mDiffDrive = new DifferentialDrive(mLeftLeader, mRightLeader);
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for the subsystem. This is the command that will run
        // whenever nothing else using the subsystem is running.
        setDefaultCommand(new DrivetrainTeleop());
    }

    public void setRampRate(double ramp) {
        this.mLeftLeader.setClosedLoopRampRate(ramp);
        this.mRightLeader.setClosedLoopRampRate(ramp);
    }

    /**
     * Reset the built in encoders on the SPARK Max-s
     * 
     * Resetting the encoders is useful when you're about to perform an autonomous driving
     * routine, as errors could have built up between now and the last time you reset them.
     */
    public void resetEncoders() {
        // To reset the encoders, we just set their current positions to 0
        mLeftLeader.getEncoder().setPosition(0.0);
        mRightLeader.getEncoder().setPosition(0.0);
    }

    /**
     * Get the distance traveled by the left side of the drivetrain since initialization or last reset
     * 
     * NOTE: This requires that an appropriate scale factor be set using CANEncoder.setPositionConversionFactor()
     * @return distance traveled in inches
     */
    public double getLeftDistance() {
        return mLeftLeader.getEncoder().getPosition();
    }

    /**
     * Get the distance traveled by the right side of the drivetrain since initialization or last reset
     * 
     * NOTE: This requires that an appropriate scale factor be set using CANEncoder.setPositionConversionFactor()
     * @return distance traveled in inches
     */
    public double getRightDistance() {
        return mRightLeader.getEncoder().getPosition();
    }

    /**
     * Get the current velocity of the left side of the drivetrain since initialization or last reset
     * 
     * NOTE: This requires that an appropriate scale factor be set using CANEncoder.setVelocityConversionFactor()
     * @return speed in inches per second
     */
    public double getLeftVelocity() {
        return mLeftLeader.getEncoder().getVelocity();
    }

    /**
     * Get the current velocity of the right side of the drivetrain since initialization or last reset
     * 
     * NOTE: This requires that an appropriate scale factor be set using CANEncoder.setVelocityConversionFactor()
     * @return speed in inches per second
     */
    public double getRightVelocity() {
        return mRightLeader.getEncoder().getVelocity();
    }


    /**
     * === Methods for controlling motor outputs ===
     * The following methods provide an interface for controlling the output of the motors
     * All of them make direct use of the DifferentialDrive helper in order to convert
     * user/program input into motor output voltages.
     */

    /**
     * Arcade drive method for differential drive platform
     * 
     * See documentation for DifferentialDrive.arcadeDrive
     * @param xSpeed Robot speed along the X-axis [-1.0..1.0]. Forward is positive
     * @param zRotation Robot rotation rate around the Z-axis [-1.0..1.0]. Clockwise is positive
     * @param squareInputs If set, decreases input sensitivity at low speeds
     */
    public void arcadeDrive(double xSpeed, double zRotation, boolean squareInputs) {
        mDiffDrive.arcadeDrive(xSpeed, zRotation, squareInputs);
    }

    /**
     * Arcade drive method for differential drive platform.
     * 
     * Calculated values will be squared to decrease sensitivity at low speeds
     * @param xSpeed Robot speed along the X-axis [-1.0..1.0]. Forward is positive
     * @param zRotation Robot rotation rate around the Z-axis [-1.0..1.0]. Clockwise is positive
     */
    public void arcadeDrive(double xSpeed, double zRotation) {
        this.arcadeDrive(xSpeed, zRotation, true);
    }

    /**
     * Curvature drive method for differential drive platform
     * 
     * The rotation argument controls the *curvature* of the robot's path rather than its rate
     * of heading change. This makes the robot more controllable at high speeds. 
     * 
     * Quick Turn overrides constant-curvature turning for turn-in-place maneuvers
     * @param xSpeed Robot speed along x-axis [-1.0..1.0]. Forward is positive.
     * @param zRotation Robot curvature/rotation rate around Z axis [-1.0..1.0]. Clockwise is positive.
     * @param isQuickTurn If set, overrides constant curvature turning (turns zRotation into rotation rate instead of curvature radius)
     */
    public void curvatureDrive(double xSpeed, double zRotation, boolean isQuickTurn) {

        if (isQuickTurn) {
            setRampRate(.1);
        } else {
            setRampRate(.3);
        }

        mDiffDrive.curvatureDrive(xSpeed, zRotation, isQuickTurn);
    }

    /**
     * Independently control each side of the drive train
     * @param leftSpeed Left side speed along X-axis [-1.0..1.0]. Forward is positive
     * @param rightSpeed Right side speed along X-axis [-1.0..1.0]. Forward is positive
     * @param squareInputs If set, decreases sensitivity at low speeds
     */
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
        mDiffDrive.tankDrive(leftSpeed, rightSpeed, squareInputs);
    }

    /**
     * Independently control each side of the drivetrain
     * 
     * The calculated values will be squared to decrease sensitivity at low speed
     * @param leftSpeed Left side speed along X-axis [-1.0..1.0]. Forward is positive
     * @param rightSpeed Right side speed along X-axis [-1.0..1.0]. Forward is positive
     */
    public void tankDrive(double leftSpeed, double rightSpeed) {
        this.tankDrive(leftSpeed, rightSpeed, true);
    }
}

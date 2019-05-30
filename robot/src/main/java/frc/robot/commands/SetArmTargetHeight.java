/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.subsystems.Arm;

public class SetArmTargetHeight extends InstantCommand {
  private Arm mArm;
  private Constants.TargetHeight mTargetHeight;

  public SetArmTargetHeight(Constants.TargetHeight target) {
      super();
      mArm = Arm.getInstance();
      mTargetHeight = target;
      requires(mArm);
  }

  // Called once when the command executes
  @Override
  protected void initialize() {
      mArm.setPresetHeight(mTargetHeight);
  }

}

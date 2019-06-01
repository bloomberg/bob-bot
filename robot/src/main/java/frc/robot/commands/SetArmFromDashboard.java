/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.subsystems.Arm;

/**
 * Add your docs here.
 */
public class SetArmFromDashboard extends InstantCommand {
  
  private Arm arm = Arm.getInstance();
  private boolean fromChooser;

  public SetArmFromDashboard(boolean c) {
    super();
    fromChooser = c;
    requires(arm);
  }

  // Called once when the command executes
  @Override
  protected void initialize() {
    Constants.TargetHeight value = fromChooser ? Robot.m_oi.getSelectedDashboardHeight() : arm.getTargetHeight();
    arm.setPresetHeight(value);
  }
}

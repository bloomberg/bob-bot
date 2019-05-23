/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/* 
 * Command to get us onto Hab level 2. It executes ClimbToHab2 followed by
 * DriveOntoHab.
 * 
 * When Complete:
 *  Both ClimbToHab2 and DriveOntoHab have completed
 * 
 */
public class AutoOntoHab2 extends CommandGroup {

  public AutoOntoHab2() {
    addSequential(new ClimbToHab2());
    addSequential(new DriveOntoHab());
  }

}

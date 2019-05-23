/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

/* 
 * Command to climb to hab level 3. Makes use of ClimbToHab for implementation
 * 
 * When Complete:
 *  The base of the robot wheels will be even with the floor of hab level 3
 * 
 */
public class ClimbToHab3 extends ClimbToHab {

  private static final double HAB_LEG_HEIGHT = 20.0f;
    //TODO: set appropriate height value

  public ClimbToHab3(){
    super(HAB_LEG_HEIGHT);
  }
}

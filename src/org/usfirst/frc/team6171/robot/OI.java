package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
	public static final int LEFTX = 0;
	public static final int LEFTY = 1;
	//public static final int RTRIGGER = 3;
	//public static final int LTRIGGER = 2;
	public static final int RIGHTX = 4;
	public static final int RIGHTY = 5;
	
	Joystick joy;
	JoystickButton X, A, B, Y, LB, RB, mode;
	
	public OI(){
		joy = new Joystick(0);
		//buttons
		//X = new JoystickButton(joy, 1);
		A = new JoystickButton(joy, 1);
		B = new JoystickButton(joy, 3);
		Y = new JoystickButton(joy, 4);
		LB = new JoystickButton(joy, 5);
		RB = new JoystickButton(joy, 6);
		mode = new JoystickButton(joy, 0); //need to calibrate to correct button. 
		
				
		
	}
}

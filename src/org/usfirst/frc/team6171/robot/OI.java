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
	JoystickButton X, A, B, Y, LB, RB, back, start, leftJoy, rightJoy;
	
	public OI(){
		joy = new Joystick(0);
		//buttons
		A = new JoystickButton(joy, 1);
		B = new JoystickButton(joy, 2);
		X = new JoystickButton(joy, 3);
		Y = new JoystickButton(joy, 4);
		LB = new JoystickButton(joy, 5);
		RB = new JoystickButton(joy, 6);
		back = new JoystickButton(joy, 7);
		start = new JoystickButton(joy, 8);
		leftJoy = new JoystickButton(joy, 9);
		rightJoy = new JoystickButton(joy, 10);
		
		
		//mode = new JoystickButton(joy, 0); //need to calibrate to correct button. 
	}
	public double getLeftY()
	{
		return joy.getRawAxis(LEFTY);
	}
	public double getLeftX()
	{
		return joy.getRawAxis(LEFTX);
	}
	public double getRightY()
	{
		return joy.getRawAxis(RIGHTY);
	}
	public double getRightX()
	{
		return joy.getRawAxis(RIGHTX);
	}
}

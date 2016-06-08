package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
//	 XBOX Buttons
	public static final int LEFTX = 0;
	public static final int LEFTY = 1;
	public static final int RTRIGGER = 3;
	public static final int LTRIGGER = 2;
	public static final int RIGHTX = 4;
	public static final int RIGHTY = 5;
	JoystickButton X, A, B, Y, LB, RB, back, start, leftJoy, rightJoy;
	//Af is stop flywheels
	//Bf is flywheels spin out
	//Yf is piston in and out
	//Xf is flywheels spin in
	JoystickButton Xf,Af,Bf,Yf,LBf,RBf,backf,startf,leftJoyf,rightJoyf;
//	Flight Stick Buttons
	public static final int YAXIS = 1;
	public static final int SLIDER = 3;
	JoystickButton trigger, thumb, leftSmall, leftBig, rightSmall, rightBig, b7, b8, b9, b10, b11, b12;	
	
	Joystick joy, flight,xboxShooter;

	public OI(){
//		XBOX
		joy = new Joystick(1);
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
//		Shooter Xbox
		xboxShooter = new Joystick(2);
		Af = new JoystickButton(xboxShooter,1);
		Bf = new JoystickButton(xboxShooter,2);
		Xf = new JoystickButton(xboxShooter,3);
		Yf = new JoystickButton(xboxShooter,4);
		LBf = new JoystickButton(xboxShooter, 5);
		
		
		
		
		
//		FlightStick
		/*
		flight = new Joystick(0);
		trigger = new JoystickButton(flight, 1);
		thumb = new JoystickButton(flight, 2);
		leftBig = new JoystickButton(flight, 3);
		leftSmall = new JoystickButton(flight, 5);
		rightSmall = new JoystickButton(flight, 6);
		rightBig = new JoystickButton(flight, 4);
		b7 = new JoystickButton(flight, 7);
		b8 = new JoystickButton(flight, 8);
		b9 = new JoystickButton(flight, 9);
		b10 = new JoystickButton(flight, 10);
		b11 = new JoystickButton(flight, 11);
		b12 = new JoystickButton(flight, 12);
		*/

		
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
	public double getRightTrigger()
	{
		return joy.getRawAxis(RTRIGGER);
	}
	public double getLeftTrigger()
	{
		return joy.getRawAxis(LTRIGGER);
	}
	public double getShooterY()
	{
		return xboxShooter.getRawAxis(LEFTY);
	}
	public double getShooterPOV(){
		return xboxShooter.getPOV();
	}

}

package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Solenoid;

public class FlyWheel {
    public static final int MAX_RPM = 4666;
    public static final int DESIRED_RPM = 1000;
	private  VictorSP leftVic, rightVic, winch;
	private Solenoid piston; 
	
	public FlyWheel()
	{
		//leftVic = new VictorSP(4);
		//rightVic = new VictorSP(5);
		//piston  = new Soleniod();
		//winch = new VictorSP();
	}
	public void spin()
	{
		double motorSpeed = MAX_RPM * (DESIRED_RPM/MAX_RPM);
    	leftVic.set(motorSpeed);
    	rightVic.set(motorSpeed);
	}
}

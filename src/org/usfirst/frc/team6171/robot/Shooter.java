package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

public class Shooter {
	public CANTalon talonLeft, talonRight;
	public static final int MAX_RPM = 4000;
	public static final int DESIRED_RPM = 1000;
	
	public Shooter(){
		talonLeft = new CANTalon(1);
		talonLeft.setInverted(true);
		talonRight = new CANTalon(2);
	}
	
	public void spinUp(){
		talonLeft.set(DESIRED_RPM);
		talonRight.set(DESIRED_RPM);
	}
	
	public void intakeSpin(){
		talonLeft.setInverted(false);
		talonRight.set(DESIRED_RPM);
		talonLeft.set(DESIRED_RPM);
		talonRight.setInverted(true);	
	}
	
	

}

package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public Servo leftServo, rightServo;
	public VictorSP vicOne;
	public static final int MAX_RPM;
	public static final int DESIRED_RPM;
	public static final double initialPosition = 0;
	public static final double finalPosition = 90;
	
	public Intake(){
		leftServo = new Servo(1);
		rightServo = new Servo(2);
		vicOne = new VictorSP(3);
	}
	
	public void ballIn(){
		leftServo.set(-finalPosition);
		rightServo.set(finalPosition);
		vicOne.set(DESIRED_RPM);
	}
	
	public void ballOut(){
		leftServo.set(-initialPosition);
		rightServo.set(initialPosition);
		vicOne.set(DESIRED_RPM);
		vicOne.setInverted(true);
	}

}

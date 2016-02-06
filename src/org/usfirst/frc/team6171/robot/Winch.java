package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

public class Winch {
 public VictorSP winch;
 public  Encoder encOne;
 public static final int MAX_RPM = 3000;
 public static final int DESIRED_RPM = 1000;
 

	 public Winch(){
		 winch = new VictorSP(2);
		 encOne = new Encoder(0, 1, true, EncodingType.k4X);
		 //Karthik is a pretty cool guy  but he asked me to code this thing that I dont know how to do		 		 
	 }
	 
	 public void controlWinch() {
		 double motorSpeed = DESIRED_RPM;
		 winch.set(motorSpeed);
	 }
	 
	 public boolean getAngle(double targetAngle){
		 encOne.getDistance();
	 }
	 
 }
 


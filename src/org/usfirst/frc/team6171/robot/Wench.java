package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.VictorSP;

public class Wench {
 private VictorSP winch;
 private static final int MAX_RPM = 3000;
 private static final int DESIRED_RPM = 1000;
 

	 public Wench(){
		 winch = new VictorSP(2);
		 //Karthik is a pretty cool guy  but he asked me to code this thing that I dont know how to do		 
		 
	 }
	 
	 public void raise() {
		 double motorSpeed = DESIRED_RPM;
		 
	 }
 }
 


package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.VictorSP;

public class Intake {
	public VictorSP roller;
	public DoubleSolenoid left, right;
	
	public Intake(){
		roller = new VictorSP(5);
		left = new DoubleSolenoid(2, 3);
		right = new DoubleSolenoid(4, 5);
	}
	
	public void intakeOut(){
		left.set(Value.kForward);
		right.set(Value.kForward);
	}
	
	public void intakeIn(){
		left.set(Value.kReverse);
		right.set(Value.kReverse);
	}
	
	public void spin(){
		roller.set(.5);
	}
	
	public void stop(){
		roller.set(0);
	}
}


package org.usfirst.frc.team6171.robot;

import java.util.ArrayList;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
	VictorSP leftFront, leftRear, rightFront, rightRear, test;
	RobotDrive drive;
	
	Timer time;
	OI oi;
	
	boolean tankDrive; // used to see if mode button is clicked
	
	//these store the data from teleop for use in autonomous replay
	ArrayList<Double> repL, repR;
	
	//these variables control the flow of the replay
	int replayCounter;
	int isReplay;
	
    public void robotInit() {
    	leftFront = new VictorSP(RobotMap.KleftFront);
    	leftRear = new VictorSP(RobotMap.KleftRear);
    	rightFront = new VictorSP(RobotMap.KrightFront);
    	rightRear = new VictorSP(RobotMap.KrightRear);
    	test = new VictorSP(9);
    	
    	drive = new RobotDrive(rightFront, rightRear, leftFront, leftRear);
    	drive.setInvertedMotor(MotorType.kRearLeft, true);
    	drive.setInvertedMotor(MotorType.kFrontLeft, true);
    	//drive.setInvertedMotor
    	oi = new OI();
    	
    	time = new Timer();
    	
    	repL = new ArrayList<Double>();
    	repR = new ArrayList<Double>();
    	replayCounter = 0;
    }
    
    public void autonomousInit(){
    	//isReplay = JOptionPane.showOptionDialog(null, "Would you like to replay?", "Replay Chooser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    	time.reset();
    	time.start();
    	replayCounter = 0;
    }
    
    /**
     * This function is called periodically during autonomous
    */ 
    public void autonomousPeriodic(){
    	if(isReplay == 1){
    		drive.arcadeDrive(repL.get(replayCounter), repR.get(replayCounter));
    		replayCounter++;
    	}
    	double motorSpeed = FlyWheel.MAX_RPM * (FlyWheel.DESIRED_RPM/FlyWheel.MAX_RPM);
    	
    }
    

    public void teleopInit(){
    	repR.clear();
    	repL.clear();
    	tankDrive = false;
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	SmartDashboard.putDouble("Joystick", oi.joy.getRawAxis(1));
    	SmartDashboard.putData("Motor", leftFront);
    	SmartDashboard.putData("Motor", leftRear);
    	SmartDashboard.putData("Motor", rightFront);
    	SmartDashboard.putData("Motor", rightRear);
    	Double l, r;

    	/*boolean boost = false;
    	boolean boosted = false;
    	if(boost && !oi.A.get())
    	{
    		boost = true;
    		boosted = !boosted;
    		if(boosted)
    		{
    			drive.setMaxOutput(.75);
    		}
    		else
    			drive.setMaxOutput(.5);
    	}
    	if(oi.A.get())
    		boost = true;*/
    	
		if(oi.LB.get() && oi.RB.get())
			drive.setMaxOutput(1);
		else
			drive.setMaxOutput(.3);
    	
    	//Following code uses mode button to change from driving modes
		//pauses thread for half a second to give user time to release button
    	if(oi.A.get()){
    		tankDrive = !tankDrive;
    		Timer.delay(.5);
    	}
    	
		if(tankDrive)
    	{
    		l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		r = oi.joy.getRawAxis(OI.RIGHTY); // value added
    		   	
    		drive.tankDrive(l, r);
    		repL.add(l);
    		repR.add(r);
    	}
    	else
    	{
    		l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		r = oi.joy.getRawAxis(OI.RIGHTX); // value added 
    		   	
    		drive.arcadeDrive(l, r);
    		repL.add(l);
    		repR.add(r);
    	}
		if(oi.Y.get())
			test.set(1);
		else
			test.set(0);
		
		System.out.println(oi.joy.getPOV());
		SmartDashboard.putNumber("POV Value", oi.joy.getPOV());
  }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.addActuator("LeftFront", 0, leftFront);
    	LiveWindow.addActuator("LeftRear", 0, leftRear);
    	LiveWindow.addActuator("RightFront", 0, rightFront);
    	LiveWindow.addActuator("RightRear", 0, rightRear);
    }
    
}

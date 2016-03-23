
package org.usfirst.frc.team6171.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
	
	Timer time;
	OI oi;
	Drivetrain driveTrain;
	Shooter shooter;
	Winch winch;
	SendableChooser chooser;
	public static AHRS ahrs;
	CameraServer server;
	NetworkTable network;
	
	final String lowShoot = "Low Shoot";
	final String obstacle = "Obstacle";
	final String lowBar = "Low Bar";
	
	boolean tankDrive; // used to see if mode button is clicked
	boolean driveModeHelper;
	boolean isShooting, isStopping, isIntaking, push, pushed;
	
	double sensitivity, yVal, calculatedAngle, xVal, calculatedAngle2;
	
	int step;
	
	String autoSelected;
	//these store the data from teleop for use in autonomous replay
	//ArrayList<Double> repL, repR;
	
	//these variables control the flow of the replay
	//int replayCounter;
	//int isReplay;
	
    public void robotInit() {
    	
    	time = new Timer();
    	driveTrain = new Drivetrain();
    	shooter = new Shooter();
    	winch = new Winch();
    	oi = new OI();
    	
    	chooser = new SendableChooser();
    	chooser.addDefault("Low Bar and Shoot", lowShoot);
    	chooser.addObject("Obstacle", obstacle);
    	chooser.addObject("Low Bar Only", lowBar);
    	SmartDashboard.putData("Autonomous Chooser", chooser);
    	
    	try {
	          ahrs = new AHRS(SPI.Port.kMXP); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	      }
    	/*
    	try{
    	server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
    	}
    	catch(Exception e){}
        */
        network = NetworkTable.getTable("SmartDashboard");
    	//repL = new ArrayList<Double>();
    	//repR = new ArrayList<Double>();
    	//replayCounter = 0;
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
    }
    
    public void autonomousInit(){
//    	time.reset();
//    	time.start();
    	
    	driveTrain.resetEncoders();
    	ahrs.reset();
    	String autoSelected = (String) chooser.getSelected();
    	//driveTrain.setDistanceSetpoint(150);
    	//driveTrain.pidEnable();
    	//driveTrain.setAngleSetpoint(180);
    	step = 1;
    	switch(autoSelected){
    	case lowShoot:
    
    		break;
    	case obstacle:
    		
    		break;
    	case lowBar:
    		driveTrain.setDistanceSetpoint(-165);
    		driveTrain.pidEnable();
    		break;
    	}
    }
    
    /**
     * This function is called periodically during autonomous
    */ 
    public void autonomousPeriodic(){
    	/*
    	driveTrain.driveDistance();
//    	winch.changeAngle(-15);
    	try{
    		@SuppressWarnings("deprecation")
			double[] points = network.getNumberArray("BFR_COORDINATES");
    		yVal = (points[1]+points[3]+points[5]+points[7])/4;
    		calculatedAngle = 0.0002518742119781*yVal*yVal + -.0087280159262*yVal + 36.073278686034;
    		calculatedAngle2 = -.0000044453428178035*yVal*yVal*yVal + .00307170493685*yVal*yVal + -.5815769037743*yVal + 73.269152919889;
    	}
    	catch(Exception e){};
    	SmartDashboard.putNumber("Y Value", yVal);
    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	SmartDashboard.putNumber("Calculated Angle 2", calculatedAngle2);
    	*/
    	String autoSelected = (String) chooser.getSelected();
    	switch(autoSelected){
    	case lowShoot:
    
    		break;
    	case obstacle:
    		
    		break;
    	case lowBar:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()<-165)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(35);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		if(step==2){
    			System.out.println("Step 2");
    			driveTrain.turnToAngle();
    			System.out.println(ahrs.getYaw());
    			System.out.println(driveTrain.pid.isEnabled());
    			if(driveTrain.getTurnDone())
    			{
    				step++;
    				driveTrain.resetEncoders();
    				ahrs.reset();
    				driveTrain.setDistanceSetpoint(-30);
    				driveTrain.pidEnable();
    				
    			}
    		}
    		if(step==3)
    		{
    			System.out.println("Step 3");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()<-30)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(-155);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		if(step==4){
    			System.out.println(driveTrain.pid.isEnabled());
    			System.out.println(ahrs.getYaw());
    			System.out.println("Step 45");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone())
    			{
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setDistanceSetpoint(15);
    				driveTrain.pidEnable();
    			}
    		}
    		if(step==5)
    		{
    			System.out.println("Step 5");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()>15)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(0);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		if(step==6)
    		{
    			System.out.println("Step 6");
    			try{
    	    		@SuppressWarnings("deprecation")
    				double[] points = network.getNumberArray("BFR_COORDINATES");
    	    		yVal = (points[1]+points[3]+points[5]+points[7])/4;
    	    		calculatedAngle = 0.0002518742119781*yVal*yVal + -.0087280159262*yVal + 36.073278686034;
    	    		calculatedAngle2 = -.0000044453428178035*yVal*yVal*yVal + .00307170493685*yVal*yVal + -.5815769037743*yVal + 73.269152919889;
    	    	}
    	    	catch(Exception e){};
    	    	SmartDashboard.putNumber("Y Value", yVal);
    	    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	    	SmartDashboard.putNumber("Calculated Angle 2", calculatedAngle2);
    			winch.setAngle(calculatedAngle2);
    			winch.controlWinch(-ahrs.getRoll());
    		}
    		}
    		break;
    	}
    	/*
    	if(!driveTrain.getTurnDone()){
    		driveTrain.turnToAngle();
    	}
    	*/
    }
    

    public void teleopInit(){
    	//repR.clear();
    	//repL.clear();
    	winch.setAngle(0);
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		sensitivity = 1;
		yVal = 0;
		xVal = 0;
		driveTrain.pidDisable();
    }
    
    /**
     * This function is called periodically during operator control
     */
    @SuppressWarnings("deprecation")
	public void teleopPeriodic() {
    	//SmartDashboard.putNumber("Joystick", oi.joy.getRawAxis(1));
    	//SmartDashboard.putData("Motor", leftFront);
    	//SmartDashboard.putData("Motor", leftRear);
    	//SmartDashboard.putData("Motor", rightFront);
    	//SmartDashboard.putData("Motor", rightRear);
    	//Double l, r;
    	
    	//for(int i=0;i<points.length;i++)
    	//{
    		//SmartDashboard.putNumber(""+i, points[i]);
    	//}
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		yVal = (points[1]+points[3]+points[5]+points[7])/4;
    		calculatedAngle = 0.0002518742119781*yVal*yVal + -.0087280159262*yVal + 36.073278686034  - 1;
    		calculatedAngle2 = -.0000044453428178035*yVal*yVal*yVal + .00307170493685*yVal*yVal + -.5815769037743*yVal + 73.269152919889;
    	}
    	catch(Exception e){};
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		xVal = (points[0]+points[2]+points[4]+points[6])/4;	
    	}
    	catch(Exception e){};
    	SmartDashboard.putNumber("Y Value", yVal);
    	SmartDashboard.putNumber("X Value", xVal);
    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	SmartDashboard.putNumber("Calculated Angle 2", calculatedAngle2);
    	
    	if(oi.A.get())
		{
    		int setTarget = 131; 
			double tempOutput = 0;
			double output = 0;
			if(Math.abs(setTarget - xVal) > 15){
				tempOutput = -(setTarget - xVal)*.09;
			}
			else if(Math.abs(setTarget - xVal) > 1){
				tempOutput = -(setTarget - xVal)*.1;
			}
			
			output = Math.max(-.75, Math.min(.75, tempOutput));
			System.out.println(output);
			
    		/*
    		int setTarget = 150; 
			double tempOutput = 0;
			double output = 0;
			if(Math.abs(setTarget - xVal) > 15){
				tempOutput = -(setTarget - xVal)*.09;
			}
			else if(Math.abs(setTarget - xVal) > 1){
				tempOutput = -(setTarget - xVal)*.1;
			}
			output = Math.max(-.75, Math.min(.75, tempOutput));
			System.out.println(output);
			*/
    		/*
    		double output = 0;
    		if(149 - xVal>10)
    			output = -.7;
    		else if(149 - xVal>1)
    			output = -.4;
    		if(149 - xVal<-10)
    			output = .7;
    		else if(149 - xVal<-1)
    			output = .4;
    		*/
			//THE FUCK?
			//driveTrain.drive.arcadeDrive(0,0);
			driveTrain.drive.arcadeDrive(0, output);
		}
    	
    	
		if(oi.LB.get() && oi.RB.get())
		{
			//driveTrain.drive.setMaxOutput(Math.abs(oi.getSliderValue()-1));
			sensitivity = .8;
		}
		else if(oi.getRightTrigger()>.5 && oi.getLeftTrigger()>.5)
		{
			driveTrain.drive.setMaxOutput(1.0);
		}
		else
		{
			driveTrain.drive.setMaxOutput(.7);
			sensitivity = 1;
		}
			
    	
    	//Following code uses mode button to change from driving modes
		//pauses thread for half a second to give user time to release button
    	//if(oi.A.get()){
    	//	tankDrive = !tankDrive;
    	//	Timer.delay(.5);
    	//}
		
    	if(driveModeHelper && !oi.back.get())
    	{
    		driveModeHelper = false;
    		tankDrive = !tankDrive;
    	}
    	if(oi.back.get())
    		driveModeHelper = true;

		if(!oi.A.get() && tankDrive)
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTY); // value added
    		   	
    		driveTrain.drive.tankDrive(oi.getLeftY()*sensitivity, oi.getRightY()*sensitivity);
    		
			//driveTrain.drive.tankDrive(0,0);
    		//repL.add(l);
    		//repR.add(r);
    	}
    	else if(!oi.A.get() && !tankDrive)
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTX); // value added 
    		   	
    		driveTrain.drive.arcadeDrive(oi.getLeftY()*sensitivity, oi.getRightX()*sensitivity);
    		
    		//repL.add(l);
    		//repR.add(r);
    	}

		if(oi.leftSmall.get())
    	{
    		isShooting = false;
    		isIntaking = true;
    	}
    	if(oi.rightSmall.get())
    	{
    		isShooting = true;
    		isIntaking = false;
    	}
    	if(oi.leftBig.get())
    	{
    		isShooting = false;
    		isIntaking = false;
    	}
   
    	if(isShooting)
    	{
    		shooter.spinUp();
    	}
    	else if(isIntaking)
    	{
    		shooter.intakeSpin();
    	}
    	else
    	{
    		shooter.stop();
    	}
    	
    	if(push && !oi.trigger.get())
    	{
    		push = false;
    		pushed = !pushed;
    		if(pushed)
    		{
    			shooter.shoot();
    		}
    		else
    		{
    			shooter.retract();
    		}
    	}
    	if(oi.trigger.get())
    		push = true;
    	
    	
    	if(oi.joy.getPOV()==0)
    	{
    		winch.changeAngle(.5);
    	}
    	if(oi.joy.getPOV()==180)
    	{
    		winch.changeAngle(-.5);
    	}
    	
    	//this mapping sucks major ass!!!!! <---  ***** !!!!!
    	if(oi.rightBig.get())
    		winch.setAngle(calculatedAngle-2);
    	if(oi.b9.get())
    		winch.setAngle(25);
    	if(oi.b7.get())
    		winch.setAngle(42);
    	if(oi.b8.get())
    		winch.setAngle(53);
    	if(oi.b11.get())
    		winch.setAngle(0);
    	if(oi.b10.get())
    		winch.setAngle(-15);
    	
    	if(oi.thumb.get())
        	winch.controlWinch(-ahrs.getRoll());
       	else
       		winch.controlWinch(oi.flight.getRawAxis(1), -ahrs.getRoll());
    	
    	SmartDashboard.putNumber("Roll", ahrs.getRoll());
    	SmartDashboard.putNumber("Yaw", ahrs.getYaw());
    	SmartDashboard.putNumber("Pitch", ahrs.getPitch());
    	SmartDashboard.putNumber("Slider Value", oi.getSliderValue());
    	shooter.log();
    	driveTrain.log();
  }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	//LiveWindow.addActuator("LeftFront", 0, leftFront);
    	//LiveWindow.addActuator("LeftRear", 0, leftRear);
    	//LiveWindow.addActuator("RightFront", 0, rightFront);
    	//LiveWindow.addActuator("RightRear", 0, rightRear);
    }
    
}


package org.usfirst.frc.team6171.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
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
	Intake intake;
	SendableChooser positionChooser;
	SendableChooser obstacleChooser;
	public static AHRS ahrs;
	public static AHRS driveGyro;
	CameraServer server;
	NetworkTable network;
	MyPIDController xPID;
	
	final String lowBar = "Low Bar";
	final String two = "Two";
	final String three = "Three";
	final String four = "Four";
	final String five = "Five";
	
	final String moat = "Moat";
	final String rockWall = "Rock Wall";
	final String roughTerrain = "Rough Terrain";
	final String ramparts = "Ramparts";
	
	boolean tankDrive; // used to see if mode button is clicked
	boolean driveModeHelper;
	boolean isShooting, isStopping, isIntaking, push, pushed, aPush, aPushed, xPush, xPushed;
	
	double sensitivity, yVal, calculatedAngle, xVal, calculatedAngle2;
	
	int step;
	int autoDistance;
	
	final int BIG_OBSTACLE_DISTANCE = 150;
	final int ROUGH_TERRAIN_DISTANCE = 125;
	
	String positionSelected;
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
    	intake = new Intake();
    	oi = new OI();
    	
    	positionChooser = new SendableChooser();
    	positionChooser.addDefault("Low Bar", lowBar);
    	positionChooser.addObject("Second Position", two);
    	positionChooser.addObject("Third Position", three);
    	positionChooser.addObject("Fourth Position", four);
    	positionChooser.addObject("Fifth Position", five);
    	SmartDashboard.putData("Autonomous Chooser", positionChooser);
    	
    	obstacleChooser = new SendableChooser();
    	obstacleChooser.addDefault("Moat", moat);
    	obstacleChooser.addObject("Ramparts", ramparts);
    	obstacleChooser.addObject("Rock Wall", rockWall);
    	obstacleChooser.addObject("Rough Terrain", roughTerrain);
    	SmartDashboard.putData("Auto Chooser 2", obstacleChooser);
    	
    	try {
	          ahrs = new AHRS(SPI.Port.kMXP); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	      }
    	/*
    	try {
	          driveGyro = new AHRS(SerialPort.Port.kUSB); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	          System.out.println("no");
	      }
	      */
    	try{
    	server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
    	}
    	catch(Exception e){}
        
        network = NetworkTable.getTable("SmartDashboard");
    	//repL = new ArrayList<Double>();
    	//repR = new ArrayList<Double>();
    	//replayCounter = 0;
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		
		xVal = 0;
		yVal = 0;
		autoDistance = 0;
		//driveGyro.reset();
    }
    
    public void autonomousInit(){
    	
    	time.reset();
    	time.start();
    	driveTrain.resetEncoders();
    	ahrs.reset();
    	String positionSelected = (String) positionChooser.getSelected();
    	String obstacleSelected = (String) obstacleChooser.getSelected();
    	//driveTrain.setDistanceSetpoint(150);
    	//driveTrain.pidEnable();
    	//driveTrain.setAngleSetpoint(180);
    	step = 1;
    	switch(positionSelected){
    	case lowBar:
    		driveTrain.setOutputRange(-.6, .6);
    		driveTrain.setDistanceSetpoint(-173);
    		driveTrain.pidEnable();
    		break;
    	case two:
    	case three:
    		/*
    		winch.setAngle(15);
    		driveTrain.setDistanceSetpoint(-150);
    		driveTrain.setOutputRange(-.9, .9);
    		driveTrain.pidEnable();
    		*/
    	case four:
    	case five:
    		switch(obstacleSelected){
    		case moat:
    		case rockWall:
    			winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(BIG_OBSTACLE_DISTANCE);
    			autoDistance = BIG_OBSTACLE_DISTANCE;
    			driveTrain.setOutputRange(-.9,.9);
    			driveTrain.pidEnable();
    			break;
    		case roughTerrain:
    			winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(ROUGH_TERRAIN_DISTANCE);
    			autoDistance = ROUGH_TERRAIN_DISTANCE;
    			driveTrain.setOutputRange(-.8,.8);
    			driveTrain.pidEnable();
    			break;
    		case ramparts:
    			winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(-BIG_OBSTACLE_DISTANCE);
    			autoDistance = -BIG_OBSTACLE_DISTANCE;
    			driveTrain.setOutputRange(-.9,.9);
    			driveTrain.pidEnable();
    			break;
    		}
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
    	
    	String positionSelected = (String) positionChooser.getSelected();
    	String obstacleSelected = (String) obstacleChooser.getSelected();
    	switch(positionSelected){
    	case lowBar:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()<-173)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(15);
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
    				driveTrain.setDistanceSetpoint(-10);
    				driveTrain.pidEnable();
    				
    			}
    		}
    		
    		if(step==3)
    		{
    			System.out.println("Step 3");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()<-10)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(-138);
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
    				driveTrain.setDistanceSetpoint(52);
    				driveTrain.pidEnable();
    			}
    		}
    		
    		if(step==5)
    		{
    			System.out.println("Step 5");
    			driveTrain.driveDistanceForwards();
    			if(driveTrain.leftEnc.getDistance()>52)
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		if(step==6)
    		{
    			System.out.print("Step 6");
    			winch.setAngle(40);
    			winch.controlWinch(-ahrs.getRoll());
    			if(-ahrs.getRoll()>30)
    			{
    				step++;
    			}
    		}
    		if(step==7)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			winch.setAngle(-15);
    			winch.controlWinch(-ahrs.getRoll());
    		}
    		/*
    		if(step==6)
    		{
    			winch.setAngle(56);
    			winch.changeAngle(-ahrs.getRoll());
    			if(Math.abs(-ahrs.getRoll()-56)<1)
    				step++;
    		}
    		if(step==7)
        	{
        		shooter.spinUp();
        		if(time.get()>13.5)
        			shooter.shoot();
        	}
        	*/
    		/*
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
    			int setTarget = 123; 
    			try{
    	    		double[] points = network.getNumberArray("BFR_COORDINATES");
    	    		xVal = (points[0]+points[2]+points[4]+points[6])/4;	
    	    		if((time.get()/100 %1)%8<6)
        	    	{
    	    	    	
    	    			double tempOutput = 0;
    	    			double output = 0;
    	    			if(Math.abs(setTarget - xVal) > 15){
    	    				tempOutput = -(setTarget - xVal)*.09;
    	    			}
    	    			else if(Math.abs(setTarget - xVal) > 1){
    	    				tempOutput = -(setTarget - xVal)*.1;
    	    			}
    	    			
    	    			output = Math.max(-.75, Math.min(.75, tempOutput));
    	    			driveTrain.drive.arcadeDrive(0, output);
        	    	}
        	    	else
        	    	{
        	    		driveTrain.drive.arcadeDrive(0, 0);
        	    	}
    	    	}
    	    	catch(Exception e){
    	    		
    	    	};
    	    	
    	    	
    	    	if(Math.abs(xVal-setTarget)<2)
    	    	{
    	    		step++;
    	    	}
    		}
    		
    		break;
    	}
    	
    	if(step==7)
    	{
    		shooter.spinUp();
    		if(time.get()>13.5)
    			shooter.shoot();
    	}
    	/*
    	if(!driveTrain.getTurnDone()){``
    		driveTrain.turnToAngle();
    	}
    	*/
    		break;
    	case two:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			winch.controlWinch(-ahrs.getRoll());
    			if(driveTrain.leftEnc.getDistance()<-autoDistance)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(35);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		break;
    	case three:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			winch.controlWinch(-ahrs.getRoll());
    			if(driveTrain.leftEnc.getDistance()<-autoDistance)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(35);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		break;
    	case four:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			winch.controlWinch(-ahrs.getRoll());
    			if(driveTrain.leftEnc.getDistance()<-autoDistance)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(35);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		break;
    	case five:
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			winch.controlWinch(-ahrs.getRoll());
    			if(driveTrain.leftEnc.getDistance()<-autoDistance)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(35);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		break;
    }
    }
    

    public void teleopInit(){
    	//repR.clear();
    	//repL.clear();
    	shooter.stop();
    	driveTrain.pidDisable();
    	winch.setAngle(0);
    	winch.enable();
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		sensitivity = 1;
		yVal = 0;
		xVal = 0;
		driveTrain.pidDisable();
		//driveGyro.reset();
		
		xPID = new MyPIDController(.13, .2, 0);
		xPID.setSetPoint(0);
		xPID.setTolerance(.5);
		xPID.setOutputRange(-.9, .9);
		xPID.setDivide(1.1);
		aPush = false;
		aPushed = false;
		xPush = false;
		xPushed = false;
		
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
    		calculatedAngle = 0.0002518742119781*yVal*yVal + -.0087280159262*yVal + 36.073278686034;
    		//calculatedAngle2 = -.0000044453428178035*yVal*yVal*yVal + .00307170493685*yVal*yVal + -.5815769037743*yVal + 73.269152919889;
    	}
    	catch(Exception e){
    		//System.out.println("No camera data");
    	}
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		xVal = (points[0]+points[2]+points[4]+points[6])/4;	
    	}
    	catch(Exception e){};
    	SmartDashboard.putNumber("Y Value", yVal);
    	SmartDashboard.putNumber("X Value", xVal);
    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	//SmartDashboard.putNumber("Calculated Angle 2", calculatedAngle2);
    	
    	if(aPush && !oi.A.get())
    	{
    		aPush = false;
    		aPushed = !aPushed;
    		if(aPushed)
    		{
    			ahrs.reset();
    			if(xVal<140)
    			{
    				xPID.setSetPoint((xVal-0-120)/5);
    			}
    			else
    			{
    				xPID.setSetPoint((xVal+0-120)/5);
    			}
    				
    		}
    			
    	}
    	if(oi.A.get())
    		aPush = true;
    	
    	if(aPushed)
		{
    		/*
    		int setTarget = 118; 
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
    		try{
    			//double[] temp = network.getNumberArray("BFR_COORDINATES");
    			//double d = temp[1];
    			if(!xPID.isEnabled())
	    			xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
    		}
    		catch(Exception e){
    			driveTrain.drive.arcadeDrive(0, 0);
    			System.out.println("not seen");
    		}
	    		
		}
    	else
    	{
    		SmartDashboard.putNumber("xVal PID", 0);
    		xPID.disable();
    		
    	}
    	SmartDashboard.putNumber("Integral", xPID.getIntegral());
    	SmartDashboard.putBoolean("xVal PID Enabled", xPID.isEnabled());
    	
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
			driveTrain.drive.setMaxOutput(.9);
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
    	
    	if(xPush && !oi.X.get())
    	{
    		xPush = false;
    		xPushed = !xPushed;
    		if(xPushed)
    		{
    			ahrs.reset();
				driveTrain.setAngleSetpoint(-160);
				driveTrain.setTurnDone(false);
    		}
    		else
    		{
    			driveTrain.setTurnDone(true);
    		}
    	}
    	if(xPushed)
    	{
    		driveTrain.turnToAngle();
    		if(driveTrain.getTurnDone())
    		{
    			xPushed = false;
    		}
    	}
    	if(oi.X.get())
    		xPush = true;
    	
		if(!aPushed && !xPushed && tankDrive)
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTY); // value added
    		   	
    		driveTrain.drive.tankDrive(oi.getLeftY()*sensitivity, oi.getRightY()*sensitivity);
    		
			//driveTrain.drive.tankDrive(0,0);
    		//repL.add(l);
    		//repR.add(r);
    	}
    	else if(!aPushed && !xPushed && !tankDrive)
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTX); // value added 
    		   	
    		driveTrain.drive.arcadeDrive(Math.min(.9, oi.getLeftY()*sensitivity), Math.min(.9, oi.getRightX()*sensitivity));
    		
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
    		winch.enable();
    		xPID.enable();
    	}
   
    	if(isShooting)
    	{
    		shooter.spinUp();
    		winch.setWinchTolerance(1);
    		winch.disable();
    		xPID.disable();
    	}
    	else if(isIntaking)
    	{
    		shooter.intakeSpin();
    	}
    	else
    	{
    		shooter.stop();
    		//winch.enable();
    		winch.setWinchTolerance(.7);
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
    		intake.intakeOut();
    		intake.spinIn();
    	}
    	if(oi.joy.getPOV()==180)
    	{
    		intake.intakeIn();
    	}
    	if(oi.joy.getPOV()==90)
    	{
    		intake.spinIn();
    	}
    	if(oi.joy.getPOV()==270)
    	{
    		intake.spinOut();
    	}
    	if(oi.Y.get())
    	{
    		intake.stop();
    	}
    	//this mapping sucks major ass!!!!! <---  ***** !!!!!
    	if(oi.rightBig.get())
    		winch.setAngle(calculatedAngle-2);
    	if(oi.b9.get()){
    		winch.setAngle(25);
    		SmartDashboard.putNumber("Set Angle", 25);
    	}
    		
    	if(oi.b7.get()){
    		winch.setAngle(41);
    		SmartDashboard.putNumber("Set Angle", 37);
    	}
    		
    	if(oi.b8.get()){
    		winch.setAngle(53);
    		SmartDashboard.putNumber("Set Angle", 53);
    	}
    		
    	if(oi.b11.get()){
    		winch.setAngle(0);
    		SmartDashboard.putNumber("Set Angle", 0);
    	}
    		
    	if(oi.b10.get()){
    		winch.setAngle(-15);
    		SmartDashboard.putNumber("Set Angle", -10);
    	}
    	if(oi.b12.get()){
    		winch.setAngle(-ahrs.getRoll());
    		System.out.println(-ahrs.getRoll());
    		SmartDashboard.putNumber("Set Angle", ahrs.getRoll());
    	}
    		
    	
    	//if(oi.thumb.get())
        winch.controlWinch(-ahrs.getRoll());
       	//else
       		//winch.controlWinch(oi.flight.getRawAxis(1), -ahrs.getRoll());
        if(oi.thumb.get())
        	winch.changeAngle(oi.flight.getRawAxis(1)*.6);

        
        
        
    	SmartDashboard.putNumber("Roll", ahrs.getRoll());
    	SmartDashboard.putNumber("Yaw", ahrs.getYaw());
    	SmartDashboard.putNumber("Pitch", ahrs.getPitch());
    	SmartDashboard.putNumber("Slider Value", oi.getSliderValue());
    	SmartDashboard.putBoolean("Piston", shooter.out);
    	SmartDashboard.putNumber("Seconds Remaining", 150 - time.get());
    	/*
    	SmartDashboard.putNumber("Drive Roll", driveGyro.getRoll());
    	SmartDashboard.putNumber("Drive Angle", driveGyro.getAngle());
    	SmartDashboard.putNumber("Drive Yaw", driveGyro.getYaw());
    	*/
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

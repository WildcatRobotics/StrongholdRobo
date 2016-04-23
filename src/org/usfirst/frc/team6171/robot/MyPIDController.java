package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MyPIDController
{
	private double kP, kI, kD;
	private Timer time;
	private double integral, previousMeasure, previousTime, tolerance, setPoint, minOutput, maxOutput, divide;
	private boolean enabled, onTarget;
	public MyPIDController(double P, double I, double D)
	{
		kP = P;
		kI = I;
		kD = D;
		time = new Timer();
		
		integral = 0;
		previousMeasure = 0;
		previousTime = 0;
		tolerance = 0;
		setPoint = 0;
		minOutput = -1;
		maxOutput = 1;
		
		enabled = false;
		onTarget = false;
	}
	
	public void enable()
	{
		time.start();
		integral = 0;
		previousMeasure = 0;
		previousTime = 0;
		enabled = true;
		onTarget = false;
	}
	public void disable()
	{
		enabled = false;
	}
	public void setPID(double P, double I, double D)
	{
		kP = P;
		kI = I;
		kD = D;
	}
	public void setTolerance(double tolerancee)
	{
		tolerance = tolerancee;
	}
	public void setSetPoint(double setPointt)
	{
		setPoint = setPointt;
	}
	public void setDivide(double div)
	{
		divide = div;
	}
	public void changeSetPoint(double change)
	{
		setPoint += change;
	}
	public double getIntegral()
	{
		return integral;
	}
	public boolean onTarget()
	{
		return onTarget;
	}
	public void setOutputRange(double min, double max)
	{
		minOutput = min;
		maxOutput = max;
	}
	public double getOutput(double currentMeasure)
	{
		if(enabled)
		{
			double dt = time.get()-previousTime;
			if(Math.abs(currentMeasure - setPoint)>tolerance)
				integral += (getError(currentMeasure)+getError(previousMeasure))/2*dt;
			else
			{
				integral /= divide;
				//integral = 0;
			}
				
			//SmartDashboard.putNumber("Integral", integral);
			double derivative = (getError(currentMeasure)-getError(previousMeasure))/dt;
			
			double output = kP*getError(currentMeasure) + kI*integral + kD*derivative;
			
			previousTime += dt;
			previousMeasure = currentMeasure;
			
			return Math.max(minOutput, Math.min(maxOutput, output));
		}
		return 0;
		
	}
	private double getError(double num)
	{
		return setPoint - num;
	}
	public boolean isEnabled()
	{
		return enabled;
	}
}

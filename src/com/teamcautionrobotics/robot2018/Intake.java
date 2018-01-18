package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.VictorSP;

public class Intake {

	private VictorSP intake;

	public Intake(int motorPort) {

		intake = new VictorSP(motorPort);

	}

	public void out(double power) {
		
		intake.set(power);
	
	}

	public void out() {
	
		intake.set(1.0);
	
	}

	public void in(double power) {
		
		intake.set(-power);
	
	}

	public void in() {
	
		intake.set(-1.0);
	
	}

}

package com.teamcautionrobotics.robot2018;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

public class Tower {
    
    enum TowerPosition {
        GROUND,
        SWITCH,
        LOW_SCALE,
        HIGH_SCALE
    }

    private VictorSP towerMotor;
    private Encoder towerEncoder;
    
    private double desiredPosition;

    public Tower(int motorPort, int encoderChannelA, int encoderChannelB) {
        towerMotor = new VictorSP(motorPort);
        towerEncoder = new Encoder(encoderChannelA, encoderChannelB);
        towerEncoder.setDistancePerPulse((4 * Math.PI) / 1024);
        towerEncoder.reset();
    }

    /**
     * @param power positive is ascending, negative is descending, range of [-1, 1]
     */
    public void move(double power) {
        towerMotor.set(power);
    }

    public void ascend() {
        this.move(1.0);
    }

    public void descend() {
        this.move(-1.0);
    }
    
    public void stop() {
        this.move(0);
    }
    
    public boolean setPosition(TowerPosition towerPosition) {
        boolean finished = false;
        switch (towerPosition) {
            case GROUND:
                desiredPosition = 0;
                break;
            case SWITCH:
                desiredPosition = 19;
                break;
            case LOW_SCALE:
                desiredPosition = 48;
                break;
            case HIGH_SCALE:
                desiredPosition = 76;
                break;
            default: System.err.println("towerPosition is not set to a normal value, continuing");
                break;
        }
        
        if (towerEncoder.getDistance() > desiredPosition) {
            this.descend();
            finished = false;
        } else if (towerEncoder.getDistance() < desiredPosition) {
            this.ascend();
            finished = false;
        } else if (towerEncoder.getDistance() == desiredPosition) {
            this.stop();
            finished = true;
        }
        
        return finished;
    }
    
    public void resetEncoder() {
        towerEncoder.reset();
    }

}

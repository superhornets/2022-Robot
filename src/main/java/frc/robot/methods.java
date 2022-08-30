package frc.robot;

public class methods {
    static final int SHOOTER_ACCEPTABLE_ERROR = 5;
    static final double HIGH_ESTIMATE_SHOOTER = .7;
    static final double MID_ESTIMATE_SHOOTER = .4;
    static final double LOW_ESTIMATE_SHOOTER = .2;
    static final int HIGH_ROTATION_SHOOTER = 120;
    static final int LOW_ROTATION_SHOOTER = 40;
    static final double SHOOTER_CHANGE_LIMIT = .05;
    static final int SHOOTER_CHANGE_FACTOR = 40;
    static final int INITAL_SHOOTER_CHANGE_FACTOR = 50;


    static double lastShooterChange = 0;


    static double calculateShooterSpeed(Double currentRotations, int targetRotations, double motorSpeed){
        if (Math.abs(targetRotations - currentRotations) < SHOOTER_ACCEPTABLE_ERROR){
            return motorSpeed;
        }
        else if(targetRotations == 0){
            lastShooterChange = 0;
            return 0;
        }
        else{
            if (lastShooterChange == 0){
                if (targetRotations > HIGH_ROTATION_SHOOTER){
                    lastShooterChange = HIGH_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                    return HIGH_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                }
                else if(targetRotations < LOW_ROTATION_SHOOTER){
                    lastShooterChange = LOW_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                    return LOW_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                }
                else{
                    lastShooterChange = MID_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                    return MID_ESTIMATE_SHOOTER/SHOOTER_CHANGE_FACTOR;
                }
            }
            else{
                if(targetRotations > currentRotations){
                    if (lastShooterChange < SHOOTER_CHANGE_LIMIT){
                        return Math.abs(lastShooterChange);
                    }
                    else{
                        lastShooterChange=Math.abs(lastShooterChange)/SHOOTER_CHANGE_FACTOR;
                        return lastShooterChange;
                    }
                }
                else{
                    if (lastShooterChange < SHOOTER_CHANGE_LIMIT){
                        return -(Math.abs(lastShooterChange));
                    }
                     lastShooterChange=(-(Math.abs(lastShooterChange)/SHOOTER_CHANGE_FACTOR));
                     return lastShooterChange;
                }
            }
        }
    }
    
    double leftEncoderValue = 0;
    double rightEncoderValue = 0;
    static void driveStraight(){



    }
    static void driveToDistance(double distance){

    }

}

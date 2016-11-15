package org.usfirst.frc.team2635.robot;

import edu.wpi.first.wpilibj.Timer;

public class ShooterModes {
	private static final double SHOOTER_OFF_TIME = 0.1;
	private static final double SHOOT_TIME = 100.0E-3;
	boolean set;
	
	int num;
	
	ShooterEnabled shooterEnabled;
	double motorSpeed;
	int prevMode;
	public double getMotorSpeed() {
		return motorSpeed;
	}
	Timer shooterTimer;
	enum singleShooterEnum {INIT,SHOOTING,STOP,RESET}
	singleShooterEnum singleShooterState = singleShooterEnum.INIT ;
	enum burstShooterEnum {INIT,SHOOTING,STOP,RESET}
	burstShooterEnum burstShooterState = burstShooterEnum.INIT;
	enum autoShooterEnum {INIT,SHOOTING,STOP,RESET}
	autoShooterEnum autoShooterState = autoShooterEnum.INIT;
	enum modesEnum {SINGLE,BURST,AUTO}
	modesEnum modesState = modesEnum.SINGLE;
	boolean nerfSwitch;
	
	public ShooterModes(ShooterEnabled shEnabled) {
		super();
		prevMode=0;
		shooterEnabled=shEnabled;
		shooterTimer = new Timer();
	}
	
	public void setSwitch(boolean nerfS) {
		nerfSwitch=nerfS;
		if(nerfSwitch) {
			System.out.println("TRUE");
		}
	}
	public void modeChange (boolean singleButton,boolean burstButton,boolean autoButton){
		if(singleButton && !burstButton && !autoButton){
			modesState = modesEnum.SINGLE;
		} 
		else if(burstButton && !singleButton && !autoButton){
			modesState = modesEnum.BURST;
		}
		else if(autoButton && !singleButton && !burstButton) {
			modesState = modesEnum.AUTO;
			System.out.println("Switched mode to auto");
		}
	}
	
	public void update (boolean button){
		switch(modesState) {
		case SINGLE:
			this.singleUpdate(button);
			break;
		case BURST:
			this.burstUpdate(button);
			break;
		case AUTO:
			this.autoUpdate(button);
			break;
		default:
			break;
		}
	}
	
	public void singleUpdate (boolean button) {
		/*if(button && !nerfSwitch) {
			motorSpeed = 1.0;
		} else if(!button) {
			motorSpeed = 0.0;
		} else if (nerfSwitch) {
			motorSpeed = 0.0;
		}*/
		
		switch (singleShooterState){
		case INIT:
			prevMode=0;
			if (button) {
				motorSpeed = 1.0;
				set=false;
				singleShooterState = singleShooterEnum.SHOOTING;
				
			}
			break;
		case SHOOTING:
			if(prevMode!=0){singleShooterState = singleShooterEnum.INIT;};
			if (nerfSwitch==false&&set==true){
				motorSpeed = 0.0;
				singleShooterState = singleShooterEnum.RESET;
				System.out.println("Stopped");
				
		}
			else if(nerfSwitch==true) {
				set=true;
				}
			
		
			
		
			
			break;
		case STOP:
			if (!nerfSwitch){
				singleShooterState = singleShooterEnum.RESET;
			}
			break;
		case RESET:
			motorSpeed=0.0;
			
			if (!button){
				
				set=false;
				shooterEnabled.isEnabled=false;
				singleShooterState = singleShooterEnum.INIT;
			}
		
		default:
			break;
		}
	}
		public void burstUpdate (boolean button) {
			/*if(button && !nerfSwitch) {
				motorSpeed = 1.0;
			} else if(!button) {
				motorSpeed = 0.0;
			} else if (nerfSwitch) {
				motorSpeed = 0.0;
			}*/
			
			switch (burstShooterState){
			case INIT:
				prevMode=1;
				if (button&&num<3) {
					motorSpeed = 1.0;
					set=false;
					burstShooterState = burstShooterEnum.SHOOTING;
					
				}
				else if (num>2){
					num=0;
					motorSpeed=0.0;
				}
				else if(!button){
					motorSpeed=0.0;
					burstShooterState=burstShooterEnum.INIT;
					num=0;
				}
				break;
			case SHOOTING:
				if(prevMode!=1){burstShooterState = burstShooterEnum.INIT;};
				if (nerfSwitch==false&&set==true){
					motorSpeed = 0.0;
					burstShooterState = burstShooterEnum.RESET;
					System.out.println("Stopped");
					
			}
				else if(nerfSwitch==true) {
					set=true;
					}
			
				else if(!button){
					motorSpeed=0.0;
					burstShooterState=burstShooterEnum.INIT;
				}
			
				
				break;
			case STOP:
				if (!nerfSwitch){
					burstShooterState = burstShooterEnum.RESET;
				}
				break;
			case RESET:
				motorSpeed=0.0;
				if(prevMode!=1){burstShooterState = burstShooterEnum.INIT;};
				if (button&&num<2){
					
					set=false;
					shooterEnabled.isEnabled=false;
					num++;
					burstShooterState = burstShooterEnum.INIT;
				}
				else if(num>3){
					num=0;
					motorSpeed=0.0;
				}
				else if(!button){
					motorSpeed=0.0;
					burstShooterState=burstShooterEnum.INIT;
					num=0;
				}
			
			default:
				break;
			}
		
		}
			public void autoUpdate (boolean button) {
		switch (autoShooterState){
		case INIT:
			prevMode=2;
			if (button) {
				motorSpeed = 1.0;
				set=false;
				autoShooterState = autoShooterEnum.SHOOTING;
				
			}
			
			break;
		case SHOOTING:
			if(prevMode!=2){autoShooterState = autoShooterEnum.INIT;};
			if (nerfSwitch==false&&set==true){
				motorSpeed = 0.0;
				autoShooterState = autoShooterEnum.RESET;
				System.out.println("Stopped");
				
		}
			else if(nerfSwitch==true) {
				set=true;
				}
			else if (!button){
				autoShooterState=autoShooterEnum.INIT;
				set=false;
				motorSpeed=0.0;
			}
		
			
		
			
			break;
		case STOP:
			if (!nerfSwitch){
				autoShooterState = autoShooterEnum.RESET;
			}
			break;
		case RESET:
			motorSpeed=0.0;
			if(prevMode!=2){autoShooterState = autoShooterEnum.INIT;};
			
				autoShooterState = autoShooterEnum.INIT;
				set=false;
				shooterEnabled.isEnabled=false;
			
				
			
		
		
			break;
		}
		}
	}
		



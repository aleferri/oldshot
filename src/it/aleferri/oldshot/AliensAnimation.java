/*
 * Copyright 2024 Alessio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.aleferri.oldshot;

/**
 *
 * @author Alessio
 */
public class AliensAnimation {
    
    public final static short PHASE_LINE = 0;
    public final static short PHASE_LEFT = 1;
    public final static short PHASE_DOWN = 2;
    public final static short PHASE_RIGHT = 3;
    
    private final int interval;
    private short dx;
    private short dy;
    
    public short phase;
    public short gOffsetX;
    public short gOffsetY;
    
    
    private int countdown;

    public AliensAnimation(short phase, short gOffsetX, short gOffsetY, int interval) {
        this.phase = phase;
        this.gOffsetX = gOffsetX;
        this.gOffsetY = gOffsetY;
        this.dx = 0;
        this.dy = 0;
        this.interval = interval;
        this.countdown = interval;
    }
    
    public void start() {
        this.initPhase();
    }
    
    public void initPhase() {
        if (this.phase == 1) {            
            this.gOffsetY = 0;
            this.dx = -1;
            this.dy = 0;
        } else if (this.phase == 2) {            
            this.dx = 0;
            this.dy = +2;
        } else if (this.phase == 3) {
            this.dx = +1;
            this.dy = 0;
        } else {            
            this.gOffsetX = 0;
            this.gOffsetY = 0;
            this.dx = 0;
            this.dy = 0;
        }
    }
    
    public void nextPhase() {
        if (this.phase == 0) {
            this.phase = 1;
            
            this.gOffsetY = 0;
            this.dx = -1;
            this.dy = 0;
        } else if (this.phase == 1) {
            this.phase = 2;
            
            this.dx = 0;
            this.dy = +2;
        } else if (this.phase == 2) {
            this.phase = 3;
            
            this.dx = +1;
            this.dy = 0;
        } else {
            this.phase = 0;
            
            this.gOffsetX = 0;
            this.gOffsetY = 0;
            this.dx = 0;
            this.dy = 0;
        }
    }
    
    public boolean tick() {
        this.countdown--;
        
        if (this.countdown == 0) {
            this.phase++;
            if (this.phase == 4) {
                this.phase = 0;
            }
            
            this.initPhase();
            this.countdown = this.interval;
            
            return true;
        } else {
            this.gOffsetX += this.dx;
            this.gOffsetY += this.dy;
        }
        
        return false;
    }
    
}

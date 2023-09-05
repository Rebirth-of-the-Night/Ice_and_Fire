package com.github.alexthe666.iceandfire.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class IAFMath {

    private static final double coeff_1 = Math.PI / 4;
    private static final double coeff_2 = coeff_1 * 3;

    public static double atan2_accurate(double y, double x) {
        double r;
        if (y < 0) {
            y = -y;
            if (x > 0) {
                r = (x - y) / (x + y);
                return -(0.1963 * r * r * r - 0.9817 * r + coeff_1);
            } else {
                r = (x + y) / (y - x);
                return -(0.1963 * r * r * r - 0.9817 * r + coeff_2);
            }
        } else {
            if (y == 0) {
                y = 1.0E-25;
            }
            if (x > 0) {
                r = (x - y) / (x + y);
                return 0.1963 * r * r * r - 0.9817 * r + coeff_1;
            } else {
                r = (x + y) / (y - x);
                return 0.1963 * r * r * r - 0.9817 * r + coeff_2;
            }
        }
    }
    
	/**
	 * Copies the coordinates of an Int vector and centers them.
	 * Code copied from 1.16 Forge, I own no rights to this code.
	 */
	public static Vec3d copyCentered(Vec3i toCopy) {      
		return new Vec3d((double)toCopy.getX() + 0.5D, (double)toCopy.getY() + 0.5D, (double)toCopy.getZ() + 0.5D);
	}
	  
	/**
	 * Copies the coordinates of an int vector and centers them horizontally and applies a vertical offset.
	 * Code copied from 1.16 Forge, I own no rights to this code.
	 */
	public static Vec3d copyCenteredWithVerticalOffset(Vec3i toCopy, double verticalOffset) {
		return new Vec3d((double)toCopy.getX() + 0.5D, (double)toCopy.getY() + verticalOffset, (double)toCopy.getZ() + 0.5D);
	}
}

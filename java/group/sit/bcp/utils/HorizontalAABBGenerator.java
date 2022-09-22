package group.sit.bcp.utils;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HorizontalAABBGenerator {

	public static VoxelShape[][] generate(double[][] parameters){
		if(parameters[0].length != 6)
			throw new IllegalArgumentException("The length of the first dimension of parameter array is required to be equal to 6. ");

		VoxelShape result[][] = new VoxelShape[4][parameters.length];
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < parameters.length; j++) {
				result[i][j] = Block.box(
						parameters[j][0], 
						parameters[j][1], 
						parameters[j][2], 
						parameters[j][3], 
						parameters[j][4], 
						parameters[j][5]);

				double 	oldX1 = parameters[j][0], 
						oldZ1 = parameters[j][2], 
						oldX2 = parameters[j][3], 
						oldZ2 = parameters[j][5];
				parameters[j][0] = -oldZ2 + 16D;
				parameters[j][2] = oldX1;
				parameters[j][3] = -oldZ1 + 16D;
				parameters[j][5] = oldX2;
			}
		}
		return result;
	}

}

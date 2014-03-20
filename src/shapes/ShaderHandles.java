package shapes;

import java.util.ArrayList;

public class ShaderHandles {
	public int programHandle;
	public int positionHandle,normalHandle,mTextureCoordinateHandle,modelHandle,viewHandle,projectionHandle,mTextureUniformHandle,isColored,colorHandle,lightHandle,viewPointHandle;
	public ArrayList<Integer> mTextureDataHandle = new ArrayList<Integer>();
	
	
	public ShaderHandles()
	{
	}
}

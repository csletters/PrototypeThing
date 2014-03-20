package shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class Square {

	float vertices[] = {
			-4.0f,4.0f,0.0f,
			-4.0f,-4.0f,0.0f,
			4.0f,-4.0f,0.0f,
			4.0f,4.0f,0.0f
			};
	
	float normals[] = {
			-0.33f,0.33f,0.33f,
			-0.33f,-0.33f,0.33f,
			0.33f,-0.33f,0.33f,
			0.33f,0.33f,0.33f
	};
	

	float uvCords[] = {
		0.0f,1.0f,
		0.0f,0.0f,
		1.0f,0.0f,
		1.0f,1.0f
	};
	
	public float color[] = new float[4];
	
	short vertexOrder[] = {0,1,2,0,2,3};
	
	FloatBuffer vertexBuffer,normalBuffer,texBuffer;
	ShortBuffer drawlistBuffer;
	
	public Square()
	{
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * vertices.length);
		buffer.order(ByteOrder.nativeOrder());

		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// buffer for cube normals
		ByteBuffer nBuffer = ByteBuffer.allocateDirect(4 * normals.length);
		nBuffer.order(ByteOrder.nativeOrder());

		normalBuffer = nBuffer.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);
				
		//buffer for texture cords
		ByteBuffer tBuffer = ByteBuffer.allocateDirect(4 * uvCords.length);
		tBuffer.order(ByteOrder.nativeOrder());

		texBuffer = tBuffer.asFloatBuffer();
		texBuffer.put(uvCords);
		texBuffer.position(0);
		
		// buffer for vertex order
		ByteBuffer bufferOrder = ByteBuffer.allocateDirect(2 * vertexOrder.length);
		bufferOrder.order(ByteOrder.nativeOrder());

		drawlistBuffer = bufferOrder.asShortBuffer();
		drawlistBuffer.put(vertexOrder);
		drawlistBuffer.position(0);
		
	}
	
	//texture draw
	public void draw(int mTextureDataHandle, float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle,int normalHandle,int isColored,int mTextureCoordinateHandle)
	{

		//position
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	3 * 4, vertexBuffer);

		//normals
		GLES20.glEnableVertexAttribArray(normalHandle);
		GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);
		
		//texture
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,2 * 4, texBuffer);
	
		
		//send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		GLES20.glUniform1i(isColored, 0);
		
		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexOrder.length,GLES20.GL_UNSIGNED_SHORT, drawlistBuffer);
        
        GLES20.glDisableVertexAttribArray(positionHandle);
		//GLES20.glDisableVertexAttribArray(colorHandle);
		GLES20.glDisableVertexAttribArray(normalHandle);

		GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
	}
	
	//no texture draw
	public void draw(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle, int positionHandle, int normalHandle, int isColored, int colorHandle)
	{
		//position
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	3 * 4, vertexBuffer);

		//normals
		GLES20.glEnableVertexAttribArray(normalHandle);
		GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalBuffer);
		

		//send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		GLES20.glUniform1i(isColored, 1);
		GLES20.glUniform4f(colorHandle, color[0], color[1], color[2], color[3]);
				
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexOrder.length,GLES20.GL_UNSIGNED_SHORT, drawlistBuffer);
		        
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(normalHandle);
	}
	
	
	public float[] getVertices()
	{
		return vertices;
	}
	
	public float[] getNormals()
	{
		return normals;
	}


}

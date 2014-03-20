package textpackage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class Character {

	float vertices[] = {
			-0.5f,0.5f,0.0f,
			-0.5f,-0.5f,0.0f,
			0.5f,-0.5f,0.0f,
			0.5f,0.5f,0.0f
			};
	
	
	short vertexOrder[] = {0,1,2,0,2,3};
	
	FloatBuffer vertexBuffer,normalBuffer,texBuffer;
	ShortBuffer drawlistBuffer;
	
	public Character()
	{
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * vertices.length);
		buffer.order(ByteOrder.nativeOrder());

		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// buffer for vertex order
		ByteBuffer bufferOrder = ByteBuffer.allocateDirect(2 * vertexOrder.length);
		bufferOrder.order(ByteOrder.nativeOrder());

		drawlistBuffer = bufferOrder.asShortBuffer();
		drawlistBuffer.put(vertexOrder);
		drawlistBuffer.position(0);

	}
	
	public void setUVCords(float[] cords)
	{
		//buffer for texture cords
		ByteBuffer tBuffer = ByteBuffer.allocateDirect(4 * cords.length);
		tBuffer.order(ByteOrder.nativeOrder());

		texBuffer = tBuffer.asFloatBuffer();
		texBuffer.put(cords);
		texBuffer.position(0);
	}
	
	
	public void draw(int mTextureDataHandle,float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle,int positionHandle,int mTextureCoordinateHandle)
	{
		//position
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	3 * 4, vertexBuffer);
		
		//texture
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,2 * 4, texBuffer);
		
		//send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		
		// Bind the texture.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexOrder.length,GLES20.GL_UNSIGNED_SHORT, drawlistBuffer);
		
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
	}
}

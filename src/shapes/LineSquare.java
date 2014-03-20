package shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class LineSquare {
	
	
	float vertices[] = {
			0.0f,1.0f,0.0f,
			0.0f,0.0f,0.0f,
			1.0f,0.0f,0.0f,
			1.0f,1.0f,0.0f
			};
	
	short vertexOrder[] = {0,1,2,3,0};
	
	FloatBuffer vertexBuffer;
	ShortBuffer drawlistBuffer;
	
	public LineSquare()
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
	
	public void draw(float[] projection, float[] view, float[] model,int projectionHandle,int viewHandle,int modelHandle, int positionHandle)
	{
		//position
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,	3 * 4, vertexBuffer);
		
		//send uniforms
		GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
		GLES20.glLineWidth(2.0f);
		GLES20.glDrawElements(GLES20.GL_LINE_STRIP, vertexOrder.length,GLES20.GL_UNSIGNED_SHORT, drawlistBuffer);
		
		GLES20.glDisableVertexAttribArray(positionHandle);
	}

}

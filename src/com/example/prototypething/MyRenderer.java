package com.example.prototypething;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONArray;
import org.json.JSONException;

import shapes.LineSquare;
import shapes.ShaderHandles;
import shapes.Square;
import textpackage.TextLibrary;


import GPS.GPSTracker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class MyRenderer implements Renderer {

	private final Context mActivityContext;
	float[] projection = new float[16];
	float[] view = new float[16];
	float[] model = new float[16];
	
	int widthView,heightView;
	int currentProgram;
	int selectedSquare = 0;
	int isMoving = -1;
	float numRotations = 90.0f;
	float animationCounter = numRotations-1.0f, count = animationCounter;
	float distanceFromCenter =5.0f;
	float angle = 1.0f;
	float xView=0.0f, yView=0.0f, zView= 20.0f;
	
	Square  squareObject;
	LineSquare test;
	Bitmap googlemap;
	boolean imageLoaded = false,displayLocations = false;
	ArrayList<Square> squareList = new ArrayList<Square>();
	ArrayList<ShaderHandles> shaderPrograms = new ArrayList<ShaderHandles>();
	TextLibrary alphabet = new TextLibrary();
	JsonRetriever words;
	JSONArray values;
	
	public MyRenderer(final Context activityContext)
	{
		mActivityContext = activityContext;
		GPSTracker location = new GPSTracker(mActivityContext);
		String url = "http://maps.google.com/maps/api/staticmap?center=" + location.getLatitude() + "," + location.getLongitude()+"&zoom=14&size=300x300&sensor=true";
		Log.w("location", url);
		new DownloadImagesTask().execute(url);
		words = new JsonRetriever();
		//words.retrieveData("http://hmkcode.appspot.com/rest/controller/get.json");
		words.retrieveTwitterData("twitterapi");
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		//draw all square type objects
		GLES20.glUseProgram(shaderPrograms.get(0).programHandle);
		drawBackground();
		drawMap();
		drawUserSquares();
		
		

		//draw all line square type objects
		GLES20.glUseProgram(shaderPrograms.get(1).programHandle);
			
		if(selectedSquare == 0)
			Matrix.translateM(model, 0, 1, 0, 0);
		if(selectedSquare == 1)
			Matrix.translateM(model, 0, -1, 0, 0);
		if(selectedSquare == 2)
			Matrix.translateM(model, 0, 0, 0, 1);
		if(selectedSquare == 3)
			Matrix.translateM(model, 0, 0, 0, -1);
		Matrix.translateM(model, 0, 0.0f, -9.8f, 0.0f);
		Matrix.rotateM(model, 0, -90, 1, 0, 0);
		test.draw(projection,
			view,
			model,
			shaderPrograms.get(1).projectionHandle,
			shaderPrograms.get(1).viewHandle,
			shaderPrograms.get(1).modelHandle,
			shaderPrograms.get(1).positionHandle);
		Matrix.setIdentityM(model, 0);
		
		
		//draw all character type objects
		drawCharacters();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float)width/height;
		Matrix.perspectiveM(projection, 0, 90, ratio, 1, 1000);
		Matrix.setLookAtM(view, 0, xView,yView, zView, 0, 0, 0, 0, 1, 0);
		Matrix.setIdentityM(model, 0);
		widthView = width;
		heightView = height;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		 GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		 GLES20.glClearDepthf(1.0f);  
		 GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		 GLES20.glDepthFunc( GLES20.GL_LESS);
		 GLES20.glDepthMask( true );
		 GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		 	 
		 squareObject = new Square();
		 //add and link shader program for squares
		 ShaderHandles shader = new ShaderHandles();
		 shader.programHandle = createShader(R.raw.vertex,R.raw.fragment);
		 while(!imageLoaded)
		 {

		 }
		 shader.mTextureDataHandle.add(TextureHelper.loadTexture(googlemap));
		 shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext,R.drawable.background));
		 initSquareShaderHandles(shader.programHandle,shader);
		 shaderPrograms.add(shader);
		 
		 //add and link shader program for line squares
		 shader = new ShaderHandles();
		 shader.programHandle = createShader(R.raw.vertexline,R.raw.fragmentline);
		 initSquareLineShaderHandles(shader.programHandle,shader);
		 shaderPrograms.add(shader);
		 
		 //load characters
		 initCharacters();
		 
		 Square ySquare = new Square();
		 float [] color1 = {1.0f,0.7f,0.0f,1.0f};
		 ySquare.color = color1;
		 squareList.add(ySquare);

		 Square rSquare = new Square();
		 float [] color2 = {0.0f,0.7f,0.8f,1.0f};
		 rSquare.color = color2;
		 squareList.add(rSquare);
		 
		 Square bSquare = new Square();
		 float [] color3 = {0.4f,0.7f,0.8f,1.0f};
		 bSquare.color = color3;
		 squareList.add(bSquare);
		 
		 Square gSquare = new Square();
		 float [] color4 = {0.0f,0.7f,0.2f,1.0f};
		 gSquare.color = color4;
		 squareList.add(gSquare);
		 
		 test = new LineSquare();
	}
	
	public int createShader(int vertex, int fragment)
	{
		String vertexShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, vertex);
		String fragmentShaderCode = RawResourceReader
				.readTextFileFromRawResource(mActivityContext, fragment);

		int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		 int mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
		
		 return mProgram;
	}
	
	public void initSquareShaderHandles(int mProgram, ShaderHandles shader)
	{
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		shader.normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTexCord");
							
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(mProgram, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(mProgram, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(mProgram, "projection");
		shader.mTextureUniformHandle =  GLES20.glGetUniformLocation(mProgram, "uTexture");
		shader.isColored =  GLES20.glGetUniformLocation(mProgram, "uisColored");
		shader.colorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
		shader.lightHandle =  GLES20.glGetUniformLocation(mProgram, "uLight");
		shader.viewPointHandle = GLES20.glGetUniformLocation(mProgram, "uViewpoint");
		GLES20.glUniform3f(shader.lightHandle, 0, 4,10 );
		GLES20.glUniform3f(shader.viewPointHandle, xView, yView,zView);
		
        
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		GLES20.glUniform1i(shader.mTextureUniformHandle, 0);
	}
	
	public void initSquareLineShaderHandles(int mProgram, ShaderHandles shader)
	{
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(mProgram, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(mProgram, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(mProgram, "projection");
	}
	
	public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... urls) {
		    return download_Image(urls[0]);
		}
		private Bitmap download_Image(String url) {
		    //---------------------------------------------------
		    Bitmap bm = null;
		    try {
		        URL aURL = new URL(url);
		        URLConnection conn = aURL.openConnection();
		        conn.connect();
		        InputStream is = conn.getInputStream();
		        BufferedInputStream bis = new BufferedInputStream(is);
		        bm = BitmapFactory.decodeStream(bis);
		        bis.close();
		        is.close();
		        googlemap = bm;
		        imageLoaded = true;
		    } catch (IOException e) {
		        Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
		    } 
		    return bm;
		    //---------------------------------------------------
		}
	}

	public void updateFocus(int direction) {
		// TODO Auto-generated method stub
		selectedSquare += direction;
		if(direction > 0)
		{
			isMoving = 0;
			animationCounter = count;
		}
		else
		{
			isMoving = 1;
			animationCounter = count;
		}
		if(selectedSquare < 0)
		{
			isMoving = -1;
			selectedSquare = 0;
		}
		if(selectedSquare > squareList.size()-1 )
		{
			isMoving = -1;
			selectedSquare =  squareList.size()-1;
		}
	}
	
	public void slideAnimation(int x)
	{
		//moving left
		if(isMoving == 0)
		{
			if(x == selectedSquare-1)
			{
			
			}
			else
			{
				if(x < selectedSquare-1)
					Matrix.translateM(model, 0, (-distanceFromCenter+(x-(selectedSquare-1))), 0, 0);
				else
					Matrix.translateM(model, 0, distanceFromCenter+(x-(selectedSquare-1)), 0, 0);
			}

			if(x == selectedSquare-1)
			{
				Matrix.translateM(model, 0, -(((distanceFromCenter+1)/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, 90-(angle*animationCounter), 0, 1, 0);
			}
			else if(x == selectedSquare)
			{
				Matrix.translateM(model, 0, -(((distanceFromCenter+1)/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, (angle*animationCounter), 0, 1, 0);
			}
			else
			{
				Matrix.translateM(model, 0, -((1.0f/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, 90, 0, 1, 0);
			}
			animationCounter--;	

		}
		else// moving right
		{
			if(x == selectedSquare+1)
			{
			
			}
			else
			{
				if(x < selectedSquare+1)
					Matrix.translateM(model, 0, (-distanceFromCenter+(x-(selectedSquare+1))), 0, 0);
				else
					Matrix.translateM(model, 0, distanceFromCenter+(x-(selectedSquare+1)), 0, 0);
			}

			if(x == selectedSquare+1)
			{
				Matrix.translateM(model, 0, (((distanceFromCenter+1)/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, 90-(angle*animationCounter), 0, 1, 0);
			}
			else if(x == selectedSquare)
			{
				Matrix.translateM(model, 0, (((distanceFromCenter+1)/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, (angle*animationCounter), 0, 1, 0);
			}
			else
			{
				Matrix.translateM(model, 0, ((1.0f/numRotations)*(numRotations-animationCounter)), 0, 0);
				Matrix.rotateM(model, 0, 90, 0, 1, 0);
			}
			animationCounter--;	
		}
		//reset values
		if(animationCounter == -1)
		{
			animationCounter = count;
			isMoving = -1;
		}
	}
	
	public void initCharacters()
	{
		
		ShaderHandles shader = new ShaderHandles();
		shader.programHandle = createShader(R.raw.vertexchar,R.raw.fragmentchar);
		shader.mTextureDataHandle.add(TextureHelper.loadTexture(mActivityContext, R.drawable.text));
		shader.mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader.programHandle, "aTexCord");
		
		//attributes
		shader.positionHandle = GLES20.glGetAttribLocation(shader.programHandle, "aPosition");
		
		//uniforms
		shader.modelHandle =  GLES20.glGetUniformLocation(shader.programHandle, "model");
		shader.viewHandle =  GLES20.glGetUniformLocation(shader.programHandle, "view");
		shader.projectionHandle =  GLES20.glGetUniformLocation(shader.programHandle, "projection");
		shader.mTextureUniformHandle =  GLES20.glGetUniformLocation(shader.programHandle, "uTexture");
		

        
		// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
		GLES20.glUniform1i(shader.mTextureUniformHandle, 0);
		shaderPrograms.add(shader);
	}
	
	public void drawBackground()
	{
		Matrix.translateM(model, 0, 0, 0, -55);
		Matrix.rotateM(model, 0, 90, 0, 0, 1);
		Matrix.scaleM(model, 0, 32f, 20f, 1.0f);
		
		
		squareObject.draw(shaderPrograms.get(0).mTextureDataHandle.get(1),
				projection,
				view,
				model,
				shaderPrograms.get(0).projectionHandle,
				shaderPrograms.get(0).viewHandle,
				shaderPrograms.get(0).modelHandle,
				shaderPrograms.get(0).positionHandle,
				shaderPrograms.get(0).normalHandle,
				shaderPrograms.get(0).isColored,
				shaderPrograms.get(0).mTextureCoordinateHandle);
		Matrix.setIdentityM(model, 0);
	}
	
	public void drawMap()
	{
		if(shaderPrograms.get(0).mTextureDataHandle.get(0) != null )
		{
			Matrix.translateM(model, 0, 0, -10, 0);
			Matrix.rotateM(model, 0, -90, 1, 0, 0);
			Matrix.scaleM(model, 0, 1.5f, 1.5f, 1.5f);
			
			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			
			squareObject.draw(shaderPrograms.get(0).mTextureDataHandle.get(0),
					projection,
					view,
					model,
					shaderPrograms.get(0).projectionHandle,
					shaderPrograms.get(0).viewHandle,
					shaderPrograms.get(0).modelHandle,
					shaderPrograms.get(0).positionHandle,
					shaderPrograms.get(0).normalHandle,
					shaderPrograms.get(0).isColored,
					shaderPrograms.get(0).mTextureCoordinateHandle);
			Matrix.setIdentityM(model, 0);
		}
	}
	
	public void drawUserSquares()
	{
		for(int x=0; x < squareList.size();x++)
		{
			//if there is input from user
			if(isMoving != -1)
			{
				//do animation
				slideAnimation(x);
			}
			else
			{//if no input from user draw like this
				if(x == selectedSquare)
				{
				
				}
				else
				{
					if(x < selectedSquare)
						Matrix.translateM(model, 0, -distanceFromCenter+(x-selectedSquare), 0, 0);
					else
						Matrix.translateM(model, 0, distanceFromCenter+(x-selectedSquare), 0, 0);
					Matrix.rotateM(model, 0, 90, 0, 1, 0);
				}
			}

			squareList.get(x).draw(projection,
					view,
					model,
					shaderPrograms.get(0).projectionHandle,
					shaderPrograms.get(0).viewHandle,
					shaderPrograms.get(0).modelHandle,
					shaderPrograms.get(0).positionHandle,
					shaderPrograms.get(0).normalHandle,
					shaderPrograms.get(0).isColored,
					shaderPrograms.get(0).colorHandle);
			Matrix.setIdentityM(model, 0);
		}
	}
	
	public void drawCharacters()
	{
		if(words.isFinished)
		{
			Matrix.translateM(model, 0, -4, 3, 2);
			GLES20.glUseProgram(shaderPrograms.get(2).programHandle);
			GLES20.glEnable(GLES20.GL_BLEND);
			try {
			
				if(selectedSquare == 0)
				{
					String sentence = words.articles.getJSONObject(0).getString("text");
					for(int x =0; x <10;x++)
					{
						Matrix.translateM(model, 0, 0.8f, 0.0f, 0.0f);
						alphabet.getCharacter(sentence.charAt(x)).draw(shaderPrograms.get(2).mTextureDataHandle.get(0),
							projection,
							view,
							model,
							shaderPrograms.get(2).projectionHandle,
							shaderPrograms.get(2).viewHandle,
							shaderPrograms.get(2).modelHandle,
							shaderPrograms.get(2).positionHandle,
							shaderPrograms.get(2).mTextureCoordinateHandle);
					}
				}
				if(selectedSquare == 1)
				{
					String sentence = words.articles.getJSONObject(0).getString("retweet_count");
					for(int x =0; x <sentence.length();x++)
					{
						Matrix.translateM(model, 0, 0.8f, 0.0f, 0.0f);
						alphabet.getCharacter(sentence.charAt(x)).draw(shaderPrograms.get(2).mTextureDataHandle.get(0),
							projection,
							view,
							model,
							shaderPrograms.get(2).projectionHandle,
							shaderPrograms.get(2).viewHandle,
							shaderPrograms.get(2).modelHandle,
							shaderPrograms.get(2).positionHandle,
							shaderPrograms.get(2).mTextureCoordinateHandle);
					}
				}
				if(selectedSquare == 2)
				{
					String sentence = words.articles.getJSONObject(0).getString("id");
					for(int x =0; x <sentence.length();x++)
					{
						Matrix.translateM(model, 0, 0.8f, 0.0f, 0.0f);
						alphabet.getCharacter(sentence.charAt(x)).draw(shaderPrograms.get(2).mTextureDataHandle.get(0),
							projection,
							view,
							model,
							shaderPrograms.get(2).projectionHandle,
							shaderPrograms.get(2).viewHandle,
							shaderPrograms.get(2).modelHandle,
							shaderPrograms.get(2).positionHandle,
							shaderPrograms.get(2).mTextureCoordinateHandle);
					}
				}
				if(selectedSquare == 3)
				{
					String sentence = words.articles.getJSONObject(1).getString("created_at");
					for(int x =0; x <sentence.length() ;x++)
					{
						Matrix.translateM(model, 0, 0.8f, 0.0f, 0.0f);
						alphabet.getCharacter(sentence.charAt(x)).draw(shaderPrograms.get(2).mTextureDataHandle.get(0),
							projection,
							view,
							model,
							shaderPrograms.get(2).projectionHandle,
							shaderPrograms.get(2).viewHandle,
							shaderPrograms.get(2).modelHandle,
							shaderPrograms.get(2).positionHandle,
							shaderPrograms.get(2).mTextureCoordinateHandle);
					}
				}
	
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GLES20.glDisable(GLES20.GL_BLEND);
			Matrix.setIdentityM(model, 0);
		}
	}


	
}

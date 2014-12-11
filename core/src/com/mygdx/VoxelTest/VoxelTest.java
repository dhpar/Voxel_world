package com.mygdx.VoxelTest;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;


public class VoxelTest extends GdxTest {
	SpriteBatch spriteBatch;
	BitmapFont font;
	ModelBatch modelBatch;
	PerspectiveCamera camera;
	Environment lights;
	FirstPersonCameraController controller;
	VoxelWorld voxelWorld;
	

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		modelBatch = new ModelBatch();

		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		
		//Camera...
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		controller = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(controller);
		
		//Light...
		
        
		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));
		
		
		//Texture...
		Texture texture = new Texture(Gdx.files.internal("data/tiles.png"));
		TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);
		
		MathUtils.random.setSeed(0);
		
		//World creation (3d obj)
		
		//VoxelWorld(tiles, chunks x, chunks y, chunks z)
		voxelWorld = new VoxelWorld(tiles[1], 20, 4, 20);		
		PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 12);
		

		float camX = voxelWorld.voxelsX / 2f;
		float camZ = voxelWorld.voxelsZ / 2f;
		float camY = voxelWorld.getHighest(camX, camZ) + 1.5f;
		camera.position.set(camX, camY, camZ);
	}
	
	@Override
	public void render () {
		//background color
		Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1f);
		
		//Clears the buffer 
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); 
		
		//render camera
		modelBatch.begin(camera);
		
		//render world and lights
		modelBatch.render(voxelWorld, lights);
		modelBatch.end();
		
		//update camera
		controller.update();
		
		//render stats...
		spriteBatch.begin();
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks(rendered/total num): " + voxelWorld.renderedChunks
				+ "/" + voxelWorld.numChunks, 0, 20);
		spriteBatch.end();
		
		
	}
	
	@Override
	public void resize (int width, int height) {
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
}
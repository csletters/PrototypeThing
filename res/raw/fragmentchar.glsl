precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTexCord;
void main() {

	vec2 flipped_texcoord = vec2(vTexCord.x, 1.0 - vTexCord.y);
	gl_FragColor = texture2D(uTexture, flipped_texcoord);
}
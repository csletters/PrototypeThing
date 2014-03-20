precision mediump float;
uniform sampler2D uTexture;
uniform int uisColored;
uniform vec3 uLight;
uniform vec4 uColor;
uniform vec3 uViewpoint;
varying vec2 vTexCord;
varying vec3 vNormal;
varying vec3 vPosition;

void main() {
	
	vec3 lightVector = normalize(uLight - vPosition);
	vec3 viewVector = normalize(uViewpoint - vPosition);
	vec3 lightReflect = normalize(reflect(lightVector,normalize(vNormal)));
	float diffuse = 0.7*dot(normalize(vNormal), lightVector);
	float specFactor = dot(viewVector,lightReflect);
	float specular = 0.0*pow(specFactor,2.0);
	
	if(uisColored == 1)
	{
		gl_FragColor =uColor*(diffuse+specular+0.6);
	}
	else
	{
		vec2 flipped_texcoord = vec2(vTexCord.x, 1.0 - vTexCord.y);
		gl_FragColor = texture2D(uTexture, flipped_texcoord)+0.05;
	}
}
uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
attribute vec2 aTexCord;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec2 vTexCord;
varying vec3 vNormal;
varying vec3 vPosition;

void main() {
vPosition = vec3(view*model*aPosition);
vNormal = vec3(transpose(inverse(view*model))*vec4(aNormal,1.0));
vTexCord = aTexCord;
gl_Position = projection*view*model*aPosition;
}